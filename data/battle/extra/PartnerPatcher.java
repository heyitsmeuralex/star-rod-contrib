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


















public class PartnerPatcher
{
  private final Patcher patcher;
  private final RandomAccessFile raf;
  private List<PartnerConfig> configs;
  private boolean foundPatches = false;
  
  public PartnerPatcher(Patcher patcher)
  {
    this.patcher = patcher;
    raf = patcher.getRandomAccessFile();
  }
  
  public void patchPartnerData() throws IOException
  {
    FileUtils.forceMkdir(Directories.MOD_ALLY_TEMP.toFile());
    FileUtils.cleanDirectory(Directories.MOD_ALLY_TEMP.toFile());
    
    Collection<File> patchFiles = IOUtils.getFilesWithExtension(Directories.MOD_ALLY_PATCH, "bpat", true);
    
    for (File f : patchFiles)
    {
      Logger.log("Executing patch: " + f.getName());
      String name = FilenameUtils.removeExtension(f.getName());
      PartnerEncoder encoder = new PartnerEncoder(patcher);
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
    
    for (int i = 0; i < 11; i++)
    {
      if (i == 9)
      {
        configs.add(null);
      }
      else
      {
        PartnerConfig config = new PartnerConfig(null);
        
        String source = String.format("%02X %s", new Object[] { Integer.valueOf(i), data.battle.BattleConstants.PARTNER_SCRIPT_NAMES[i] });
        File patchFile = new File(Directories.MOD_ALLY_PATCH + source + ".bpat");
        
        File indexFile;
        if (patchFile.exists())
        {
          File indexFile = new File(Directories.MOD_ALLY_TEMP + source + ".bidx");
          source = new File(Directories.MOD_ALLY_TEMP + source + ".bin");
        }
        else
        {
          indexFile = new File(Directories.DUMP_ALLY_SRC + source + ".bidx");
          source = new File(Directories.DUMP_ALLY_RAW + source + ".bin");
        }
        
        HashMap<String, Struct> structMap = new HashMap();
        BattleEncoder tempEncoder = new BattleEncoder(patcher);
        tempEncoder.loadIndexFile(structMap, indexFile);
        
        boolean foundActor = false;
        for (Struct str : structMap.values())
        {
          if (str.isTypeOf(BattleStructTypes.ActorT))
          {
            if (foundActor) {
              throw new InputFileException(indexFile, "Found multiple Actors in " + source);
            }
            actorAddress = originalLocation.address();
            foundActor = true;
          }
        }
        
        if (!foundActor) {
          throw new InputFileException(indexFile, "Could not find Actor in " + source);
        }
        configs.add(config);
      }
    }
  }
  
  public void writePartnerTable() throws IOException {
    if (!foundPatches) {
      return;
    }
    raf.seek(1779716L);
    
    for (PartnerConfig cfg : configs)
    {
      if (cfg == null)
      {
        raf.writeInt(0);
        raf.writeInt(0);
        raf.writeInt(0);
        raf.writeInt(0);
        raf.writeInt(0);
      }
      else
      {
        entryOffset = ((int)raf.getFilePointer());
        raf.writeInt(0);
        raf.writeInt(0);
        raf.writeInt(-2145157120);
        raf.writeInt(actorAddress);
        raf.skipBytes(4);
      }
    }
    
    Logger.log("Wrote partner script table.");
  }
  
  public void writePartnerData() throws IOException
  {
    if (!foundPatches) {
      return;
    }
    int currentPos = 7277360;
    int currentEnd = 7428176;
    
    HashMap<File, Region> sourceMap = new LinkedHashMap();
    
    for (PartnerConfig cfg : configs)
    {
      if (cfg != null)
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
    }
    if (currentEnd < Integer.MAX_VALUE) {
      patcher.addEmptyRegion(new Region(currentPos, currentEnd));
    }
  }
  
  private class PartnerConfig
  {
    public File source;
    public int entryOffset;
    public int actorAddress;
    
    private PartnerConfig() {}
  }
}
