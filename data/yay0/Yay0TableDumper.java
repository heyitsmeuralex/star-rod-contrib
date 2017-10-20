package data.yay0;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import main.DevContext;
import main.Directories;
import org.apache.commons.io.FileUtils;
import util.IOUtils;


public class Yay0TableDumper
{
  public Yay0TableDumper() {}
  
  public static void main(String[] args)
    throws IOException
  {}
  
  public static void dumpYay0()
    throws IOException
  {
    RandomAccessFile raf = DevContext.getPristineRomReader();
    
    raf.seek(31719456L);
    for (int i = 0; i < 1033; i++)
    {
      raf.seek(31719456 + i * 28);
      String name = IOUtils.readString(raf, 16);
      int offset = raf.readInt() + 31719456;
      int compressedLength = raf.readInt();
      int decompressedLength = raf.readInt();
      
      System.out.println(String.format("%4d %-16s %8X %8X %8X", new Object[] { Integer.valueOf(i), name, Integer.valueOf(offset), Integer.valueOf(compressedLength), Integer.valueOf(decompressedLength) }));
      



      raf.seek(offset);
      byte[] writeBytes; byte[] writeBytes; if (raf.readInt() == 1499560240)
      {
        int yay0length = raf.readInt();
        assert (yay0length == decompressedLength);
        
        byte[] dumpedBytes = new byte[compressedLength];
        raf.seek(offset);
        raf.read(dumpedBytes);
        writeBytes = Yay0Helper.decode(dumpedBytes);
      }
      else
      {
        byte[] dumpedBytes = new byte[decompressedLength];
        
        raf.seek(offset);
        raf.read(dumpedBytes);
        writeBytes = dumpedBytes;
      }
      
      File out = new File(Directories.DUMP_YAY0_ENCODED + name);
      FileUtils.writeByteArrayToFile(out, writeBytes);
    }
    


    raf.close();
  }
}
