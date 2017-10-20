package data.globals;

import data.shared.DataConstants;
import data.strings.StringData;
import data.strings.StringDatabase;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.util.HashMap;
import main.DevContext;
import main.Directories;
import util.Logger;
import util.Priority;

public class TableDumper
{
  private static final HashMap<Integer, Integer> abilityMap;
  
  public TableDumper() {}
  
  public static void dumpTables() throws IOException
  {
    Logger.log("Dumping item table.", Priority.MILESTONE);
    dumpItemTable();
    Logger.log("Dumping move table.", Priority.MILESTONE);
    dumpAbilityTable();
  }
  
  private static void dumpItemTable() throws IOException
  {
    PrintWriter pw = util.IOUtils.getBufferedPrintWriter(Directories.DUMP_GLOBALS + "ItemTable.csv");
    pw.print("Index,Name,IconID,Priority,Target,Sell Value,Menu Desc,World Desc,Type,MoveID,HP,MP,Name");
    
    RandomAccessFile raf = DevContext.getPristineRomReader();
    raf.seek(404736L);
    
    for (int i = 1; i <= 364; i++)
    {
      pw.printf("\n\"%X\",", new Object[] { Integer.valueOf(i) });
      int v = raf.readInt();
      pw.printf("\"%02X-%02X\",", new Object[] { Short.valueOf((short)(v >> 16)), Short.valueOf((short)v) });
      pw.printf("\"%X\",", new Object[] { Short.valueOf(raf.readShort()) });
      pw.printf("\"%X\",", new Object[] { Short.valueOf(raf.readShort()) });
      pw.printf("\"%X\",", new Object[] { Integer.valueOf(raf.readInt()) });
      pw.printf("\"%X\",", new Object[] { Short.valueOf(raf.readShort()) });
      short u = raf.readShort();
      assert (u == 0);
      pw.printf("\"%02X-%02X\",", new Object[] { Short.valueOf(raf.readShort()), Short.valueOf(raf.readShort()) });
      pw.printf("\"%02X-%02X\",", new Object[] { Short.valueOf(raf.readShort()), Short.valueOf(raf.readShort()) });
      pw.printf("\"%04X\",", new Object[] { Short.valueOf(raf.readShort()) });
      pw.printf("\"%02X\",", new Object[] { Byte.valueOf(raf.readByte()) });
      pw.printf("\"%X\",", new Object[] { Byte.valueOf(raf.readByte()) });
      pw.printf("\"%X\",", new Object[] { Byte.valueOf(raf.readByte()) });
      if (DataConstants.ItemType.has(i)) {
        pw.printf("\"%s\"", new Object[] { DataConstants.ItemType.getName(i) });
      } else
        pw.printf("\"%s\"", new Object[] { StringDatabase.getString(v).toString() });
      byte w = raf.readByte();
      assert (w == 0);
      w = raf.readByte();
      assert (w == 0);
      w = raf.readByte();
      assert (w == 0);
    }
    raf.close();
    
    pw.close();
  }
  
  private static void dumpAbilityTable() throws IOException
  {
    PrintWriter pw = util.IOUtils.getBufferedPrintWriter(Directories.DUMP_GLOBALS + "MoveTable.csv");
    pw.print("Index,Name,Flags,Menu Desc,World Desc,Slot,FP,BP,Type,Name,Ability");
    
    RandomAccessFile raf = DevContext.getPristineRomReader();
    raf.seek(435296L);
    
    for (int i = 0; i < 185; i++)
    {
      String name = "";
      pw.printf("\n\"%X\",", new Object[] { Integer.valueOf(i) });
      int v = raf.readInt();
      if ((v & 0x80000000) != 0)
      {
        pw.printf("\"%08X\",", new Object[] { Integer.valueOf(v) });
        name = getRoughTranslation(v);
      }
      else
      {
        pw.printf("\"%02X-%02X\",", new Object[] { Short.valueOf((short)(v >> 16)), Short.valueOf((short)v) });
        name = StringDatabase.getString(v).toString();
      }
      pw.printf("\"%X\",", new Object[] { Integer.valueOf(raf.readInt()) });
      pw.printf("\"%02X-%02X\",", new Object[] { Short.valueOf(raf.readShort()), Short.valueOf(raf.readShort()) });
      pw.printf("\"%02X-%02X\",", new Object[] { Short.valueOf(raf.readShort()), Short.valueOf(raf.readShort()) });
      pw.printf("\"%X\",", new Object[] { Byte.valueOf(raf.readByte()) });
      pw.printf("\"%X\",", new Object[] { Byte.valueOf(raf.readByte()) });
      pw.printf("\"%X\",", new Object[] { Byte.valueOf(raf.readByte()) });
      pw.printf("\"%X\",", new Object[] { Byte.valueOf(raf.readByte()) });
      
      if (DataConstants.hasMoveName(i)) {
        pw.printf("\"%s\"", new Object[] { DataConstants.getMoveName(i) });
      } else {
        pw.printf("\"%s\"", new Object[] { name });
      }
      if (abilityMap.containsKey(Integer.valueOf(i)))
        pw.printf(",\"%s\"", new Object[] { abilityMap.get(Integer.valueOf(i)) });
    }
    raf.close();
    
    pw.close();
  }
  
  protected static String getRoughTranslation(int addr)
  {
    String s = "";
    
    switch (addr) {
    case -2146858584: 
      s = "~Megaphone"; break;
    case -2146858572:  s = "~Preach"; break;
    case -2146858560:  s = "~Mumble"; break;
    case -2146858544:  s = "~Stupify"; break;
    case -2146858532:  s = "~Final Bomb-omb"; break;
    case -2146858512:  s = "~Final Old Man Goomba"; break;
    case -2146858492:  s = "~Usually";
    }
    
    return s;
  }
  
  static { abilityMap = new HashMap();
    


    abilityMap.put(Integer.valueOf(76), Integer.valueOf(0));
    abilityMap.put(Integer.valueOf(1), Integer.valueOf(1));
    abilityMap.put(Integer.valueOf(64), Integer.valueOf(2));
    abilityMap.put(Integer.valueOf(77), Integer.valueOf(3));
    abilityMap.put(Integer.valueOf(82), Integer.valueOf(4));
    abilityMap.put(Integer.valueOf(53), Integer.valueOf(5));
    abilityMap.put(Integer.valueOf(83), Integer.valueOf(6));
    abilityMap.put(Integer.valueOf(65), Integer.valueOf(7));
    abilityMap.put(Integer.valueOf(66), Integer.valueOf(8));
    abilityMap.put(Integer.valueOf(90), Integer.valueOf(9));
    abilityMap.put(Integer.valueOf(60), Integer.valueOf(10));
    abilityMap.put(Integer.valueOf(78), Integer.valueOf(11));
    abilityMap.put(Integer.valueOf(91), Integer.valueOf(12));
    abilityMap.put(Integer.valueOf(61), Integer.valueOf(13));
    abilityMap.put(Integer.valueOf(67), Integer.valueOf(14));
    abilityMap.put(Integer.valueOf(84), Integer.valueOf(15));
    abilityMap.put(Integer.valueOf(92), Integer.valueOf(16));
    abilityMap.put(Integer.valueOf(93), Integer.valueOf(17));
    abilityMap.put(Integer.valueOf(94), Integer.valueOf(18));
    abilityMap.put(Integer.valueOf(68), Integer.valueOf(19));
    abilityMap.put(Integer.valueOf(95), Integer.valueOf(20));
    abilityMap.put(Integer.valueOf(96), Integer.valueOf(21));
    abilityMap.put(Integer.valueOf(79), Integer.valueOf(22));
    abilityMap.put(Integer.valueOf(97), Integer.valueOf(23));
    abilityMap.put(Integer.valueOf(98), Integer.valueOf(24));
    abilityMap.put(Integer.valueOf(99), Integer.valueOf(25));
    abilityMap.put(Integer.valueOf(56), Integer.valueOf(26));
    abilityMap.put(Integer.valueOf(69), Integer.valueOf(27));
    abilityMap.put(Integer.valueOf(59), Integer.valueOf(28));
    abilityMap.put(Integer.valueOf(110), Integer.valueOf(29));
    abilityMap.put(Integer.valueOf(100), Integer.valueOf(30));
    abilityMap.put(Integer.valueOf(101), Integer.valueOf(31));
    abilityMap.put(Integer.valueOf(70), Integer.valueOf(32));
    abilityMap.put(Integer.valueOf(71), Integer.valueOf(33));
    abilityMap.put(Integer.valueOf(62), Integer.valueOf(34));
    abilityMap.put(Integer.valueOf(72), Integer.valueOf(35));
    abilityMap.put(Integer.valueOf(102), Integer.valueOf(36));
    abilityMap.put(Integer.valueOf(73), Integer.valueOf(37));
    abilityMap.put(Integer.valueOf(103), Integer.valueOf(38));
    abilityMap.put(Integer.valueOf(109), Integer.valueOf(39));
    abilityMap.put(Integer.valueOf(106), Integer.valueOf(40));
    abilityMap.put(Integer.valueOf(108), Integer.valueOf(41));
    abilityMap.put(Integer.valueOf(80), Integer.valueOf(42));
    abilityMap.put(Integer.valueOf(104), Integer.valueOf(43));
    abilityMap.put(Integer.valueOf(105), Integer.valueOf(44));
    abilityMap.put(Integer.valueOf(107), Integer.valueOf(45));
    abilityMap.put(Integer.valueOf(111), Integer.valueOf(46));
    abilityMap.put(Integer.valueOf(112), Integer.valueOf(47));
    abilityMap.put(Integer.valueOf(113), Integer.valueOf(48));
    abilityMap.put(Integer.valueOf(114), Integer.valueOf(49));
    abilityMap.put(Integer.valueOf(115), Integer.valueOf(50));
    abilityMap.put(Integer.valueOf(51), Integer.valueOf(51));
    abilityMap.put(Integer.valueOf(116), Integer.valueOf(52));
    abilityMap.put(Integer.valueOf(117), Integer.valueOf(53));
    abilityMap.put(Integer.valueOf(118), Integer.valueOf(54));
    abilityMap.put(Integer.valueOf(74), Integer.valueOf(55));
  }
}
