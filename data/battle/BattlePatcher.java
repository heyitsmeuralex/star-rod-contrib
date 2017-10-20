package data.battle;

import data.battle.encoder.BattleEncoder;
import data.shared.Location;
import data.shared.struct.Struct;
import data.yay0.Yay0Helper;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import main.Directories;
import main.InputFileException;
import org.apache.commons.io.FileUtils;
import patcher.Patcher;
import patcher.Region;
import util.IOUtils;
import util.Logger;











public final class BattlePatcher
{
  private final Patcher patcher;
  private final RandomAccessFile raf;
  
  public static class BattleConfig
  {
    public File source;
    public int tableEntryOffset;
    public String name;
    public int startAddress = 0;
    public int ptrFormationTable = 0;
    public int ptrMapTable = 0;
    public int ptrDmaTable = 0;
    
    public final boolean empty;
    
    public BattleConfig(boolean empty)
    {
      this.empty = empty;
    }
  }
  


  private List<BattleConfig> configs = null;
  
  public BattlePatcher(Patcher patcher)
  {
    this.patcher = patcher;
    raf = patcher.getRandomAccessFile();
  }
  
  public void readConfigs() throws IOException
  {
    configs = new LinkedList();
    
    File in = new File(Directories.MOD_BATTLE + "BattleSections.txt");
    for (String line : IOUtils.readTextFile(in, false))
    {
      if (line.equals("empty"))
      {
        configs.add(new BattleConfig(true));
      }
      else
      {
        BattleConfig config = new BattleConfig(false);
        
        String[] tokens = line.split(":");
        if (tokens.length != 2) {
          throw new InputFileException(in, "Invalid line in BattleSections.txt:\r\n" + line);
        }
        try {
          startAddress = ((int)Long.parseLong(tokens[0].trim(), 16));
        } catch (NumberFormatException e) {
          throw new InputFileException(in, "Invalid address in Sprites.txt:\r\n" + line);
        }
        
        name = tokens[1].trim();
        
        configs.add(config);
      }
    }
  }
  
  public void patchBattleData() throws IOException {
    for (BattleConfig cfg : configs)
    {
      BattleEncoder encoder = new BattleEncoder(patcher);
      encoder.encode(cfg);
    }
  }
  
  public void updateConfigs() throws IOException
  {
    for (Iterator localIterator1 = configs.iterator(); localIterator1.hasNext();) { cfg = (BattleConfig)localIterator1.next();
      
      if (!empty)
      {

        File patch = new File(Directories.MOD_BATTLE_PATCH + name + ".bpat");
        

        if (patch.exists())
        {
          File index = new File(Directories.MOD_BATTLE_TEMP + name + ".bidx");
          source = new File(Directories.MOD_BATTLE_TEMP + name + ".bin");
        }
        else
        {
          index = new File(Directories.DUMP_BATTLE_SRC + name + ".bidx");
          source = new File(Directories.DUMP_BATTLE_RAW + name + ".bin");
        }
        
        HashMap<String, Struct> structMap = new HashMap();
        new BattleEncoder(patcher).loadIndexFile(structMap, index);
        
        foundFormationTable = false;
        foundMapTable = false;
        foundDmaTable = false;
        
        for (Struct str : structMap.values())
        {
          if (str.isTypeOf(BattleStructTypes.FormationTableT))
          {
            ptrFormationTable = originalLocation.address();
            if (foundFormationTable)
              throw new InputFileException(index, "Found more than one " + BattleStructTypes.FormationTableT + " in " + name);
            foundFormationTable = true;
          }
          
          if (str.isTypeOf(BattleStructTypes.StageTableT))
          {
            ptrMapTable = originalLocation.address();
            if (foundMapTable)
              throw new InputFileException(index, "Found more than one " + BattleStructTypes.StageTableT + " in " + name);
            foundMapTable = true;
          }
          
          if (str.isTypeOf(BattleStructTypes.DmaArgTableT))
          {
            ptrDmaTable = originalLocation.address();
            if (foundDmaTable)
              throw new InputFileException(index, "Found more than one " + BattleStructTypes.DmaArgTableT + " in " + name);
            foundDmaTable = true;
          } } } }
    BattleConfig cfg;
    File index;
    boolean foundFormationTable;
    boolean foundMapTable;
    boolean foundDmaTable; }
  
  public void writeBattleTable() throws IOException { int size = configs.size() * 32;
    int tableOffset;
    int tableOffset;
    if (size > 1536) {
      tableOffset = Patcher.nextAlignedOffset(raf);
    } else {
      tableOffset = 462384;
    }
    raf.seek(tableOffset);
    
    for (BattleConfig cfg : configs)
    {
      tableEntryOffset = ((int)raf.getFilePointer());
      raf.writeInt(0);
      raf.writeInt(0);
      raf.writeInt(0);
      raf.writeInt(startAddress);
      raf.writeInt(ptrFormationTable);
      raf.writeInt(ptrMapTable);
      raf.writeInt(0);
      raf.writeInt(ptrDmaTable);
    }
    
    Logger.log("Wrote battle section table to 0x" + String.format("%X", new Object[] { Integer.valueOf(tableOffset) }));
    
    fixPointersToBattleTable(tableOffset);
  }
  
  private void fixPointersToBattleTable(int offset) throws IOException
  {
    if (offset < 41943040) {
      return;
    }
    int addr = Patcher.toAddress(offset);
    int ins1 = 0x3C030000 | addr >>> 16;
    int ins2 = 0x24630000 | addr & 0xFFFF;
    


    raf.seek(319464L);
    raf.writeInt(ins1);
    raf.writeInt(ins2);
    


    raf.seek(1672920L);
    raf.writeInt(ins1);
    raf.writeInt(ins2);
    


    raf.seek(1580844L);
    raf.writeInt(0x3C020000 | addr + 28 >>> 16);
    raf.writeInt(4460577);
    raf.writeInt(0x8C420000 | addr + 28 & 0xFFFF);
  }
  

  public void writeBattleData(boolean compressBattleData)
    throws IOException
  {
    int currentPos = 4393376;
    int currentEnd = 5518704;
    
    for (BattleConfig cfg : configs)
    {
      if (!empty)
      {

        byte[] data = FileUtils.readFileToByteArray(source);
        if (compressBattleData) {
          data = Yay0Helper.encode(data);
        }
        if (currentPos + data.length > currentEnd)
        {
          if (currentEnd == 5518704)
          {
            patcher.addEmptyRegion(new Region(currentPos, currentEnd));
            currentPos = 5600224;
            currentEnd = 7277360;
          }
          else
          {
            patcher.addEmptyRegion(new Region(currentPos, currentEnd));
            currentPos = Patcher.nextAlignedOffset(raf);
            currentEnd = Integer.MAX_VALUE;
          }
        }
        
        raf.seek(currentPos);
        raf.write(data);
        Logger.log(String.format("Wrote %s to %X", new Object[] { source.getName(), Integer.valueOf(currentPos) }));
        

        raf.seek(tableEntryOffset + 4);
        raf.writeInt(currentPos);
        raf.writeInt(currentPos + data.length);
        
        currentPos += data.length;
      }
    }
    if (currentEnd < Integer.MAX_VALUE) {
      patcher.addEmptyRegion(new Region(currentPos, currentEnd));
    }
    if (currentEnd == 5518704) {
      patcher.addEmptyRegion(new Region(5600224, 7277360));
    }
  }
}
