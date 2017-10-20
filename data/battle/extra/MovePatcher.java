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


















public class MovePatcher
{
  private final Patcher patcher;
  private final RandomAccessFile raf;
  private List<MoveConfig> configs;
  
  public MovePatcher(Patcher patcher)
  {
    this.patcher = patcher;
    raf = patcher.getRandomAccessFile();
  }
  
  public void patchMoveData() throws IOException
  {
    FileUtils.forceMkdir(Directories.MOD_MOVE_TEMP.toFile());
    FileUtils.cleanDirectory(Directories.MOD_MOVE_TEMP.toFile());
    
    Collection<File> patchFiles = IOUtils.getFilesWithExtension(Directories.MOD_MOVE_PATCH, "bpat", true);
    
    for (File f : patchFiles)
    {
      Logger.log("Executing patch: " + f.getName());
      String name = FilenameUtils.removeExtension(f.getName());
      MoveEncoder encoder = new MoveEncoder(patcher);
      encoder.encode(name);
    }
  }
  
  public void generateConfigs() throws IOException
  {
    configs = new LinkedList();
    File tableFile = new File(Directories.MOD_MOVE + "Moves.txt");
    List<String> lines = IOUtils.readTextFile(tableFile, false);
    
    if (lines.size() != 49) {
      throw new InputFileException(tableFile, "Incorrect number of lines in move table.");
    }
    for (int i = 0; i < 49; i++)
    {
      String[] tokens = ((String)lines.get(i)).split("\\s+");
      
      int lineIndex = Integer.parseInt(tokens[0], 16);
      if (lineIndex != i) {
        throw new InputFileException(tableFile, "Index %d out of order.", new Object[] { Integer.valueOf(i) });
      }
      if (tokens[1].equals("null"))
      {
        configs.add(null);
      }
      else
      {
        MoveConfig config = new MoveConfig(null);
        String scriptName = tokens[1];
        String mainName = tokens[2];
        
        File patchFile = new File(Directories.MOD_MOVE_PATCH + scriptName + ".bpat");
        
        File indexFile;
        if (patchFile.exists())
        {
          File indexFile = new File(Directories.MOD_MOVE_TEMP + scriptName + ".bidx");
          source = new File(Directories.MOD_MOVE_TEMP + scriptName + ".bin");
        }
        else
        {
          indexFile = new File(Directories.DUMP_MOVE_SRC + scriptName + ".bidx");
          source = new File(Directories.DUMP_MOVE_RAW + scriptName + ".bin");
        }
        
        HashMap<String, Struct> structMap = new HashMap();
        BattleEncoder tempEncoder = new BattleEncoder(patcher);
        tempEncoder.loadIndexFile(structMap, indexFile);
        
        boolean foundMain = false;
        for (Struct str : structMap.values())
        {
          if ((str.isTypeOf(BattleStructTypes.UseScriptT)) && (name.equals("$" + mainName)))
          {
            if (foundMain) {
              throw new InputFileException(indexFile, "Duplicate " + mainName + " scripts for " + scriptName);
            }
            mainAddress = originalLocation.address();
            foundMain = true;
          }
        }
        
        if (!foundMain) {
          throw new InputFileException(indexFile, "Could not find " + mainName + " script for " + scriptName);
        }
        configs.add(config);
      }
    }
  }
  
  public void writeMoveTable() throws IOException {
    int size = configs.size() * 16;
    int tableOffset;
    int tableOffset;
    if (size > 784) {
      tableOffset = Patcher.nextAlignedOffset(raf);
    } else {
      tableOffset = 1845088;
    }
    raf.seek(tableOffset);
    
    for (MoveConfig cfg : configs)
    {

      if (cfg == null)
      {
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
        raf.writeInt(-2144727040);
        raf.writeInt(mainAddress);
      }
    }
    
    Logger.log("Wrote move script table to 0x" + String.format("%X", new Object[] { Integer.valueOf(tableOffset) }));
    
    fixPointersToMoveTable(tableOffset);
  }
  
  private void fixPointersToMoveTable(int offset) throws IOException
  {
    if (offset < 41943040) {
      return;
    }
    int addr = Patcher.toAddress(offset);
    int ins1 = 0x3C020000 | addr >>> 16;
    int ins2 = 0x34420000 | addr & 0xFFFF;
    

    raf.seek(1665588L);
    raf.writeInt(ins1);
    raf.writeInt(ins2);
  }
  



  public void writeMoveData()
    throws IOException
  {
    int currentPos = 7554464;
    int currentEnd = 7904864;
    
    HashMap<File, Region> sourceMap = new LinkedHashMap();
    
    for (MoveConfig cfg : configs)
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
  
  private static final class MoveConfig
  {
    public File source;
    public int entryOffset;
    public int mainAddress;
    
    private MoveConfig() {}
  }
}
