package patcher;

import asm.MIPS;
import data.battle.ActorTypesEditor;
import data.battle.BattlePatcher;
import data.battle.extra.AuxBattlePatcher;
import data.globals.TablePatcher;
import data.globals.WorldMapEditor;
import data.map.MapPatcher;
import data.shared.DataConstants;
import data.sprites.SpritePatcher;
import data.strings.StringPatcher;
import data.texture.IconPatcher;
import data.texture.ImagePatcher;
import data.yay0.Yay0Helper;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import main.DevContext;
import main.Directories;
import main.Mod;
import main.config.Config;
import main.config.Options;
import org.apache.commons.io.FileUtils;
import util.Logger;
import util.Priority;




public class Patcher
  implements SymbolDatabase
{
  public static final int ROM_BASE = 41943040;
  public static final int RAM_BASE = -2143289344;
  private List<Region> emptyRegions = new ArrayList();
  
  private RandomAccessFile raf = null;
  
  private HashMap<String, Integer> globalPointerMap;
  
  private HashMap<String, Integer> stringIDMap;
  private boolean doneAddingGlobalPointers = false;
  
  public Patcher() throws IOException
  {
    try
    {
      DataConstants.loadModGlobals();
      patchROM();
      DataConstants.clearModGlobals();

    }
    catch (Throwable t)
    {

      if (raf != null)
        raf.close();
      DataConstants.clearModGlobals();
      throw t;
    }
  }
  
  private void patchROM() throws IOException
  {
    Logger.log(new Date().toString(), Priority.IMPORTANT);
    Logger.log("Preparing patching process.", Priority.MILESTONE);
    

    Config cfg = currentModconfig;
    boolean optCompressBattleData = cfg.getBoolean(Options.CompressBattleData);
    


    raf = DevContext.currentMod.getTargetRomWriter();
    
    FileUtils.forceMkdir(Directories.MOD_MAP_TEMP.toFile());
    FileUtils.cleanDirectory(Directories.MOD_MAP_TEMP.toFile());
    
    FileUtils.forceMkdir(Directories.MOD_BATTLE_TEMP.toFile());
    FileUtils.cleanDirectory(Directories.MOD_BATTLE_TEMP.toFile());
    
    Logger.log("Building string data...", Priority.MILESTONE);
    StringPatcher stringPatcher = new StringPatcher(this);
    stringPatcher.readAllStrings();
    stringIDMap = stringPatcher.getStringIDMap();
    


    MapPatcher mapPatcher = new MapPatcher(this);
    BattlePatcher battlePatcher = new BattlePatcher(this);
    AuxBattlePatcher auxPatcher = new AuxBattlePatcher(this);
    SpritePatcher spritePatcher = new SpritePatcher(this);
    ImagePatcher imgPatcher = new ImagePatcher(this);
    
    Logger.log("Reading map config files...", Priority.MILESTONE);
    mapPatcher.readConfigs();
    Logger.log("Writing map config table...", Priority.MILESTONE);
    int mapConfigTableBase = mapPatcher.writeConfigTable();
    
    Logger.log("Reading battle config files...", Priority.MILESTONE);
    battlePatcher.readConfigs();
    
    if (cfg.getBoolean(Options.BuildSpriteSheets))
    {
      Logger.log("Reading sprite list...", Priority.MILESTONE);
      spritePatcher.readSpriteList();
    }
    


    globalPointerMap = new LinkedHashMap();
    globalPointerMap.put("$Global_MapConfigTable", Integer.valueOf(toAddress(mapConfigTableBase)));
    


    ActorTypesEditor.patch(this, raf);
    
    doneAddingGlobalPointers = true;
    
    Logger.log("Reading direct ROM patches...", Priority.MILESTONE);
    DirectRomPatcher drm = new DirectRomPatcher(globalPointerMap, this);
    drm.readROMPatches();
    drm.writeGlobalStructs(raf);
    
    Logger.log("Finished reading global pointers:");
    for (Map.Entry<String, Integer> e : globalPointerMap.entrySet()) {
      Logger.logf("%08X = %s", new Object[] { e.getValue(), e.getKey() });
    }
    if (cfg.getBoolean(Options.DisableDemoReel))
    {
      raf.seek(75332L);
      raf.writeInt(268435474);
    }
    
    if (cfg.getBoolean(Options.SkipIntroLogos))
    {
      raf.seek(62104L);
      raf.writeInt((int)Long.parseLong(MIPS.assemble("ADDIU   A0, R0, 0700"), 16));
      raf.skipBytes(4);
      raf.writeInt((int)Long.parseLong(MIPS.assemble("SH      A0, AC (V0)"), 16));
    }
    


    Logger.log("Building battle data...", Priority.MILESTONE);
    battlePatcher.patchBattleData();
    auxPatcher.patchData();
    Logger.log("Building map data...", Priority.MILESTONE);
    mapPatcher.patchMapData();
    

    battlePatcher.updateConfigs();
    auxPatcher.generateConfigs();
    


    Logger.log("Patching tables...", Priority.MILESTONE);
    TablePatcher.patchItemTable(raf);
    TablePatcher.patchAbilityTable(raf);
    
    Logger.log("Patching world map...");
    WorldMapEditor.patch(raf);
    
    Logger.log("Patching icons...", Priority.MILESTONE);
    IconPatcher.patchMenuIcons(raf);
    


    Logger.log("Writing direct ROM patches...", Priority.MILESTONE);
    
    drm.writePatches(raf);
    



    if (optCompressBattleData) {
      FunctionPatcher.enableCompressedBattleSections(raf, nextAlignedOffset());
    }
    
    if (cfg.getBoolean(Options.EnableDebugCode)) {
      DebugPatcher.showDebugInformation(raf, nextAlignedOffset());
    }
    FunctionPatcher.showVersionInfo(raf, nextAlignedOffset());
    
    Logger.log("Writing strings...", Priority.MILESTONE);
    stringPatcher.writeStrings();
    
    Logger.log("Writing config tables...", Priority.MILESTONE);
    battlePatcher.writeBattleTable();
    auxPatcher.writeTables();
    
    if (cfg.getBoolean(Options.BuildSpriteSheets)) {
      spritePatcher.writeTables();
    }
    
    raf.seek(raf.length());
    while (raf.getFilePointer() != nextAlignedOffset()) {
      raf.writeInt(0);
    }
    FunctionPatcher.hookBoot(raf, (int)raf.getFilePointer() - 41943040);
    



    clearRegion(439376, 453312);
    clearRegion(474528, 474640);
    clearRegion(474668, 478880);
    
    imgPatcher.patchMiscImages();
    
    if (cfg.getBoolean(Options.BuildTextures))
    {
      Logger.log("Building texture archives...", Priority.MILESTONE);
      imgPatcher.buildTextureArchives();
    }
    
    if (cfg.getBoolean(Options.BuildBackgrounds))
    {
      Logger.log("Building backgrounds...", Priority.MILESTONE);
      imgPatcher.buildBackgrounds();
    }
    
    Logger.log("Writing map assets...", Priority.MILESTONE);
    mapPatcher.writeAssetTable();
    
    mapPatcher.writeMapData();
    mapPatcher.updateConfigTable();
    
    Logger.log("Writing battle data...", Priority.MILESTONE);
    battlePatcher.writeBattleData(optCompressBattleData);
    auxPatcher.writeData();
    
    if (cfg.getBoolean(Options.BuildSpriteSheets))
    {
      Logger.log("Patching sprite sheets...", Priority.MILESTONE);
      spritePatcher.patchSpriteSheets();
    }
    
















    Logger.log("Calculating new CRC values...", Priority.MILESTONE);
    recalculateCRCs();
    
    for (Region r : emptyRegions)
    {
      Logger.logf("Empty region from %08X to %08X (%X bytes)", new Object[] { Integer.valueOf(start), Integer.valueOf(end), Integer.valueOf(r.length()) });
      raf.seek(start);
      raf.write(new byte[r.length()]);
    }
    
    raf.close();
    
    Logger.log("Done editing " + DevContext.currentMod.getTargetRom(), Priority.MILESTONE);
    Logger.log(new Date().toString(), Priority.IMPORTANT);
  }
  
  public void addGlobalPointer(String name, int addr)
  {
    if (doneAddingGlobalPointers) {
      throw new IllegalStateException("Cannot add global pointers after direct ROM patches are added.");
    }
    globalPointerMap.put(name, Integer.valueOf(addr));
  }
  

  public boolean isFunctionDeclared(String name)
  {
    return globalPointerMap.containsKey(name);
  }
  

  public int getFunctionAddress(String name)
  {
    return ((Integer)globalPointerMap.get(name)).intValue();
  }
  

  public boolean stringNameExists(String name)
  {
    return stringIDMap.containsKey(name);
  }
  

  public int getStringFromName(String name)
  {
    return ((Integer)stringIDMap.get(name)).intValue();
  }
  

  private void recalculateCRCs()
    throws IOException
  {
    long t6;
    
    long t5;
    
    long t4;
    
    long t3;
    long t2;
    long t1 = t2 = t3 = t4 = t5 = t6 = -1551341735L;
    


    for (int i = 4096; i < 1052672; i += 4)
    {
      raf.seek(i);
      long d = raf.readInt() & 0xFFFFFFFF;
      if ((t6 + d & 0xFFFFFFFF) < (t6 & 0xFFFFFFFF)) t4 += 1L;
      t6 += d;
      t3 ^= d;
      
      long r = (d << (int)(d & 0x1F) | d >> (int)(32L - (d & 0x1F))) & 0xFFFFFFFF;
      
      t5 += r;
      if ((t2 & 0xFFFFFFFF) > (d & 0xFFFFFFFF)) t2 ^= r; else {
        t2 ^= t6 ^ d;
      }
      t1 += (t5 ^ d);
    }
    
    int crc1 = (int)((t6 ^ t4) + t3);
    int crc2 = (int)((t5 ^ t2) + t1);
    
    raf.seek(16L);
    raf.writeInt(crc1);
    raf.writeInt(crc2);
    Logger.log(String.format("Wrote new CRCs to ROM (%08X %08X)", new Object[] { Integer.valueOf(crc1), Integer.valueOf(crc2) }), Priority.IMPORTANT);
  }
  
  public static void packageMod(File rom) throws IOException
  {
    LinkedList<Integer> diffStarts = new LinkedList();
    LinkedList<Integer> diffLengths = new LinkedList();
    
    byte[] pristine = DevContext.getPristineRomBytes();
    byte[] patched = FileUtils.readFileToByteArray(rom);
    
    if (patched.length < pristine.length) {
      throw new RuntimeException("Patched ROM should not be smaller than pristine ROM!");
    }
    Logger.log("Starting mod packaging: " + new Date().toString(), Priority.IMPORTANT);
    
    boolean mismatching = false;
    int mismatchStart = -1;
    int lastEnd = Integer.MIN_VALUE;
    
    Logger.log("Finding differences between pristine ROM and patched ROM...", Priority.MILESTONE);
    
    for (int pos = 0; pos < pristine.length; pos++)
    {
      if (pristine[pos] != patched[pos])
      {
        if (!mismatching)
        {
          if (pos < lastEnd + 8)
          {
            diffLengths.removeLast();
            mismatchStart = ((Integer)diffStarts.removeLast()).intValue();
          }
          else
          {
            mismatchStart = pos;
          }
          
          mismatching = true;
        }
        

      }
      else if (mismatching)
      {
        diffStarts.add(Integer.valueOf(mismatchStart));
        diffLengths.add(Integer.valueOf(pos - mismatchStart));
        lastEnd = pos;
        mismatching = false;
      }
    }
    

    if (patched.length > pristine.length)
    {
      if (mismatching)
      {
        diffStarts.add(Integer.valueOf(mismatchStart));
        diffLengths.add(Integer.valueOf(patched.length - mismatchStart));
      }
      else
      {
        diffStarts.add(Integer.valueOf(pristine.length));
        diffLengths.add(Integer.valueOf(patched.length - pristine.length));
      }
    }
    
    Logger.log("Found " + diffStarts.size() + " different byte sequences.", Priority.MILESTONE);
    
    int totalSize = 8 + 8 * diffStarts.size();
    for (Iterator localIterator = diffLengths.iterator(); localIterator.hasNext();) { int len = ((Integer)localIterator.next()).intValue();
      totalSize += len;
    }
    Logger.log("Copying differences to diff file...", Priority.MILESTONE);
    
    ByteBuffer buf = ByteBuffer.allocate(totalSize);
    buf.putInt(1347244882);
    buf.putInt(diffStarts.size());
    
    for (int i = 0; i < diffStarts.size(); i++)
    {
      buf.putInt(((Integer)diffStarts.get(i)).intValue());
      buf.putInt(((Integer)diffLengths.get(i)).intValue());
      buf.put(patched, ((Integer)diffStarts.get(i)).intValue(), ((Integer)diffLengths.get(i)).intValue());
    }
    
    byte[] diffBytes = buf.array();
    
    if (currentModconfig.getBoolean(Options.CompressModPackage))
    {
      Logger.log("Compressing diff file...", Priority.MILESTONE);
      diffBytes = Yay0Helper.encode(diffBytes, true);
      Logger.logf("Compressed %08X -> %08X (%04.2f%%)", new Object[] {
        Integer.valueOf(totalSize), 
        Integer.valueOf(diffBytes.length), 
        Float.valueOf(100.0F * diffBytes.length / totalSize) });
    }
    
    String modName = currentModconfig.getString(Options.ModVersionString);
    if (modName.isEmpty()) {
      modName = ModVersionStringdefaultValue;
    }
    File out = new File(Directories.MOD_OUT + modName + ".mod");
    FileUtils.writeByteArrayToFile(out, diffBytes);
    Logger.log("Wrote MOD file to " + out, Priority.IMPORTANT);
    Logger.log("Mod package complete. " + new Date().toString(), Priority.IMPORTANT);
  }
  
  public void clearRegion(int start, int end) throws IOException
  {
    raf.seek(start);
    raf.write(new byte[end - start]);
    emptyRegions.add(new Region(start, end));
  }
  
  public void addEmptyRegion(Region r)
  {
    emptyRegions.add(r);
  }
  
  private int nextAlignedOffset() throws IOException
  {
    return nextAlignedOffset(raf);
  }
  
  public static int nextAlignedOffset(RandomAccessFile raf) throws IOException
  {
    return (int)raf.length() + 15 & 0xFFFFFFF0;
  }
  
  public RandomAccessFile getRandomAccessFile()
  {
    return raf;
  }
  
  public static int toAddress(int offset)
  {
    return -2143289344 + (offset - 41943040);
  }
  
  public static int toAddress(long filePointer)
  {
    return toAddress((int)filePointer);
  }
}
