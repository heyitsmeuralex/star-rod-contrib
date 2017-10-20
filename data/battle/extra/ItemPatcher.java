package data.battle.extra;

import data.battle.BattleStructTypes;
import data.battle.encoder.BattleEncoder;
import data.shared.Location;
import data.shared.struct.Struct;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import main.Directories;
import main.InputFileException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import patcher.Patcher;
import patcher.Region;
import util.IOUtils;
import util.Logger;




















public class ItemPatcher
{
  private final Patcher patcher;
  private final RandomAccessFile raf;
  private List<ItemConfig> configs;
  private boolean foundPatches = false;
  
  public ItemPatcher(Patcher patcher)
  {
    this.patcher = patcher;
    raf = patcher.getRandomAccessFile();
  }
  
  public void patchItemData() throws IOException
  {
    FileUtils.forceMkdir(Directories.MOD_ITEM_TEMP.toFile());
    FileUtils.cleanDirectory(Directories.MOD_ITEM_TEMP.toFile());
    
    Collection<File> patchFiles = IOUtils.getFilesWithExtension(Directories.MOD_ITEM_PATCH, "bpat", true);
    
    for (File f : patchFiles)
    {
      Logger.log("Executing patch: " + f.getName());
      String name = FilenameUtils.removeExtension(f.getName());
      ItemEncoder encoder = new ItemEncoder(patcher);
      encoder.encode(name);
      foundPatches = true;
    }
  }
  
  public void generateConfigs() throws IOException
  {
    if (!foundPatches) {
      return;
    }
    configs = new LinkedList();
    File table = new File(Directories.MOD_ITEM + "Items.txt");
    List<String> lines = IOUtils.readTextFile(table, false);
    
    if (lines.size() != 32) {
      throw new InputFileException(table, "Incorrect number of lines in item table.");
    }
    for (int i = 0; i < lines.size(); i++)
    {
      ItemConfig config = new ItemConfig(null);
      configs.add(config);
      
      String[] tokens = ((String)lines.get(i)).split("\\s+");
      
      int id = (int)Long.parseLong(tokens[0], 16);
      String scriptName = tokens[1];
      
      if (id < 0)
      {
        if (!tokens[1].equals("Item_Food")) {
          throw new InputFileException(table, "An item ID of FF must correspond to Item_Food.");
        }
      }
      itemID = id;
      
      File patchFile = new File(Directories.MOD_ITEM_PATCH + scriptName + ".bpat");
      
      File indexFile;
      if (patchFile.exists())
      {
        File indexFile = new File(Directories.MOD_ITEM_TEMP + scriptName + ".bidx");
        source = new File(Directories.MOD_ITEM_TEMP + scriptName + ".bin");
      }
      else
      {
        indexFile = new File(Directories.DUMP_ITEM_SRC + scriptName + ".bidx");
        source = new File(Directories.DUMP_ITEM_RAW + scriptName + ".bin");
      }
      
      HashMap<String, Struct> structMap = new HashMap();
      BattleEncoder tempEncoder = new BattleEncoder(patcher);
      tempEncoder.loadIndexFile(structMap, indexFile);
      
      boolean foundMain = false;
      for (Struct str : structMap.values())
      {
        if ((str.isTypeOf(BattleStructTypes.UseScriptT)) && (name.equals("$Script_UseItem")))
        {
          if (foundMain) {
            throw new InputFileException(indexFile, "Found duplicate UseItem script for " + scriptName);
          }
          mainAddress = originalLocation.address();
          foundMain = true;
        }
      }
      
      if (!foundMain) {
        throw new InputFileException(indexFile, "Could not find UseItem script for " + scriptName);
      }
    }
  }
  
  public void writeItemTable() throws IOException {
    if (!foundPatches) {
      return;
    }
    raf.seek(1844320L);
    
    for (ItemConfig cfg : configs) {
      raf.writeInt(itemID);
    }
    raf.seek(1844452L);
    
    for (ItemConfig cfg : configs)
    {
      entryOffset = ((int)raf.getFilePointer());
      raf.writeInt(0);
      raf.writeInt(0);
      raf.writeInt(-2144727040);
      raf.writeInt(mainAddress);
    }
    
    Logger.log("Wrote item script table.");
  }
  
  public void writeItemData() throws IOException
  {
    if (!foundPatches) {
      return;
    }
    int currentPos = 7428176;
    int currentEnd = 7554464;
    
    HashMap<File, Region> sourceMap = new LinkedHashMap();
    
    for (ItemConfig cfg : configs)
    {
      if (!sourceMap.containsKey(source))
      {
        byte[] data = FileUtils.readFileToByteArray(source);
        
        if (currentPos + data.length > currentEnd)
        {
          patcher.addEmptyRegion(new Region(currentPos, currentEnd));
          currentPos = Patcher.nextAlignedOffset(raf);
          currentEnd = Integer.MAX_VALUE;
        }
        
        int start = currentPos;
        int end = currentPos + data.length;
        sourceMap.put(source, new Region(start, end));
        
        raf.seek(currentPos);
        raf.write(data);
        Logger.log(String.format("Wrote %s to %X", new Object[] { source.getName(), Integer.valueOf(currentPos) }));
        
        currentPos += data.length;
      }
      
      Region r = (Region)sourceMap.get(source);
      

      raf.seek(entryOffset);
      raf.writeInt(start);
      raf.writeInt(end);
    }
    
    if (currentEnd < Integer.MAX_VALUE) {
      patcher.addEmptyRegion(new Region(currentPos, currentEnd));
    }
  }
  
  private static final class ItemConfig
  {
    public File source;
    public int itemID;
    public int mainAddress;
    public int entryOffset;
    
    private ItemConfig() {}
  }
}
