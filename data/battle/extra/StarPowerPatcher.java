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



















public class StarPowerPatcher
{
  private final Patcher patcher;
  private final RandomAccessFile raf;
  private List<StarPowerConfig> configs;
  private boolean foundPatches = false;
  
  public StarPowerPatcher(Patcher patcher)
  {
    this.patcher = patcher;
    raf = patcher.getRandomAccessFile();
  }
  
  public void patchStarPowerData() throws IOException
  {
    FileUtils.forceMkdir(Directories.MOD_STARS_TEMP.toFile());
    FileUtils.cleanDirectory(Directories.MOD_STARS_TEMP.toFile());
    
    Collection<File> patchFiles = IOUtils.getFilesWithExtension(Directories.MOD_STARS_PATCH, "bpat", true);
    
    for (File f : patchFiles)
    {
      Logger.log("Executing patch: " + f.getName());
      String name = FilenameUtils.removeExtension(f.getName());
      StarPowerEncoder encoder = new StarPowerEncoder(patcher);
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
    
    for (int i = 0; i < 12; i++)
    {
      StarPowerConfig config = new StarPowerConfig(null);
      configs.add(config);
      
      String scriptName = String.format("%02X %s", new Object[] { Integer.valueOf(i), data.battle.BattleConstants.STAR_POWER_NAME[i] });
      
      File patchFile = new File(Directories.MOD_STARS_PATCH + scriptName + ".bpat");
      
      File indexFile;
      if (patchFile.exists())
      {
        File indexFile = new File(Directories.MOD_STARS_TEMP + scriptName + ".bidx");
        source = new File(Directories.MOD_STARS_TEMP + scriptName + ".bin");
      }
      else
      {
        indexFile = new File(Directories.DUMP_STARS_SRC + scriptName + ".bidx");
        source = new File(Directories.DUMP_STARS_RAW + scriptName + ".bin");
      }
      
      HashMap<String, Struct> structMap = new HashMap();
      BattleEncoder tempEncoder = new BattleEncoder(patcher);
      tempEncoder.loadIndexFile(structMap, indexFile);
      
      boolean foundMain = false;
      for (Struct str : structMap.values())
      {
        if ((str.isTypeOf(BattleStructTypes.UseScriptT)) && (name.equals("$Script_UsePower")))
        {
          if (foundMain) {
            throw new InputFileException(indexFile, "Found duplicate UsePower script for " + scriptName);
          }
          mainAddress = originalLocation.address();
          foundMain = true;
        }
      }
      
      if (!foundMain) {
        throw new InputFileException(indexFile, "Could not find UsePower script for " + scriptName);
      }
    }
  }
  
  public void writeStarPowerTable() throws IOException {
    if (!foundPatches) {
      return;
    }
    raf.seek(1880240L);
    
    for (StarPowerConfig cfg : configs)
    {
      entryOffset = ((int)raf.getFilePointer());
      raf.writeInt(0);
      raf.writeInt(0);
      raf.writeInt(-2144727040);
      raf.writeInt(mainAddress);
    }
    
    Logger.log("Wrote star powers script table.");
  }
  
  public void writeStarPowerData() throws IOException
  {
    if (!foundPatches) {
      return;
    }
    int currentPos = 7904864;
    int currentEnd = 7991104;
    
    for (StarPowerConfig cfg : configs)
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
      
      raf.seek(currentPos);
      raf.write(data);
      Logger.log(String.format("Wrote %s to %X", new Object[] { source.getName(), Integer.valueOf(currentPos) }));
      
      currentPos += data.length;
      

      raf.seek(entryOffset);
      raf.writeInt(start);
      raf.writeInt(end);
    }
    
    if (currentEnd < Integer.MAX_VALUE) {
      patcher.addEmptyRegion(new Region(currentPos, currentEnd));
    }
  }
  
  private static final class StarPowerConfig
  {
    public File source;
    public int mainAddress;
    public int entryOffset;
    
    private StarPowerConfig() {}
  }
}
