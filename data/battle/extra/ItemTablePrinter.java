package data.battle.extra;

import data.shared.DataConstants.ConstEnum;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import main.DevContext;

public class ItemTablePrinter
{
  public static void main(String[] args) throws IOException
  {
    DevContext.initialize();
    new ItemTablePrinter();
    DevContext.exit();
  }
  
  public ItemTablePrinter() throws IOException
  {
    int N = 32;
    RandomAccessFile raf = DevContext.getPristineRomReader();
    
    HashMap<Integer, String> dataMap = new HashMap();
    dataMap.put(Integer.valueOf(7549136), "Item_Food");
    dataMap.put(Integer.valueOf(7428176), "Item_Shroom");
    dataMap.put(Integer.valueOf(7541504), "Item_StrangeCake");
    
    raf.seek(1844320L);
    
    int[] ids = new int[N];
    for (int i = 0; i < N; i++)
      ids[i] = raf.readInt();
    ids[0] = 255;
    
    raf.seek(1844452L);
    
    int[] start = new int[N];
    int[] end = new int[N];
    int[] main = new int[N];
    for (int i = 0; i < N; i++)
    {
      start[i] = raf.readInt();
      end[i] = raf.readInt();
      raf.skipBytes(4);
      main[i] = raf.readInt();
    }
    
    raf.close();
    
    for (int i = 0; i < N; i++)
    {
      String name = i == 0 ? null : data.shared.DataConstants.ItemType.getName(ids[i]);
      if (dataMap.containsKey(Integer.valueOf(start[i]))) {
        System.out.printf("%02X %08X %08X %08X %-15s %s\r\n", new Object[] { Integer.valueOf(ids[i]), Integer.valueOf(start[i]), Integer.valueOf(end[i]), Integer.valueOf(main[i]), name, dataMap.get(Integer.valueOf(start[i])) });
      } else {
        System.out.printf("%02X %08X %08X %08X %-15s Item_%s\r\n", new Object[] { Integer.valueOf(ids[i]), Integer.valueOf(start[i]), Integer.valueOf(end[i]), Integer.valueOf(main[i]), name, name });
      }
    }
  }
}
