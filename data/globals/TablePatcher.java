package data.globals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import main.Directories;
import util.Logger;
import util.Priority;

public class TablePatcher
{
  public TablePatcher() {}
  
  public static void patchItemTable(RandomAccessFile raf) throws IOException
  {
    BufferedReader in = new BufferedReader(new java.io.FileReader(Directories.MOD_GLOBALS + "ItemTable.csv"));
    String line = in.readLine();
    
    ByteBuffer bb = ByteBuffer.allocate(11648);
    int i = 0;
    
    while ((line = in.readLine()) != null)
    {
      line = line.replaceAll("[\" \t]+", "");
      String[] tokens = line.split(",");
      
      bb.putInt(interpretID(tokens[1]));
      
      bb.putShort((short)Integer.parseInt(tokens[2], 16));
      bb.putShort((short)Integer.parseInt(tokens[3], 16));
      
      bb.putInt((int)Long.parseLong(tokens[4], 16));
      
      bb.putShort((short)Integer.parseInt(tokens[5], 16));
      bb.putShort((short)0);
      
      bb.putInt(interpretID(tokens[6]));
      
      bb.putInt(interpretID(tokens[7]));
      
      bb.putShort((short)Integer.parseInt(tokens[8], 16));
      bb.put((byte)Short.parseShort(tokens[9], 16));
      bb.put((byte)Short.parseShort(tokens[10], 16));
      
      bb.put((byte)Short.parseShort(tokens[11], 16));
      bb.put((byte)0);
      bb.put((byte)0);
      bb.put((byte)0);
      
      i++;
    }
    
    in.close();
    
    if (i != 364)
    {
      Logger.log("Failed to patch item table! Incorrect number of entries: " + i, Priority.ERROR);
      return;
    }
    
    raf.seek(404736L);
    raf.write(bb.array());
  }
  
  public static void patchAbilityTable(RandomAccessFile raf) throws IOException
  {
    BufferedReader in = new BufferedReader(new java.io.FileReader(Directories.MOD_GLOBALS + "MoveTable.csv"));
    String line = in.readLine();
    
    ByteBuffer bb = ByteBuffer.allocate(3700);
    int i = 0;
    
    while ((line = in.readLine()) != null)
    {
      line = line.replaceAll("[\" \t]+", "");
      String[] tokens = line.split(",");
      
      if (tokens[1].contains("-")) {
        bb.putInt(interpretID(tokens[1]));
      } else {
        bb.putInt((int)Long.parseLong(tokens[1], 16));
      }
      bb.putInt((int)Long.parseLong(tokens[2], 16));
      bb.putInt(interpretID(tokens[3]));
      bb.putInt(interpretID(tokens[4]));
      bb.put((byte)Short.parseShort(tokens[5], 16));
      bb.put((byte)Short.parseShort(tokens[6], 16));
      bb.put((byte)Short.parseShort(tokens[7], 16));
      bb.put((byte)Short.parseShort(tokens[8], 16));
      i++;
    }
    
    in.close();
    
    if (i != 185)
    {
      Logger.log("Failed to patch ability table! Incorrect number of entries: " + i, Priority.ERROR);
      return;
    }
    
    raf.seek(435296L);
    raf.write(bb.array());
  }
  

  private static int interpretID(String s)
  {
    String[] part = s.split("-");
    if (part.length == 0)
    {
      System.out.println("## " + TableDumper.getRoughTranslation((int)Long.parseLong(part[0], 16)));
      return (int)Long.parseLong(part[0], 16);
    }
    
    int high = Integer.parseInt(part[0], 16);
    int low = Integer.parseInt(part[1], 16);
    return high << 16 | low;
  }
}
