package data.sprites;

import data.yay0.Yay0Helper;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import main.DevContext;
import main.Directories;
import org.apache.commons.io.FileUtils;
import shared.Globals;
import util.IOUtils;
import util.Logger;
import util.Priority;




public class SpriteDumper
{
  public SpriteDumper() {}
  
  public static void dumpSprites()
    throws IOException
  {
    RandomAccessFile raf = DevContext.getPristineRomReader();
    
    raf.seek(27158456L);
    int[] offsets = new int['ê'];
    
    for (int i = 0; i < offsets.length; i++)
    {
      offsets[i] = raf.readInt();
    }
    



    for (int i = 0; i < offsets.length - 1; i++)
    {
      String spriteName = String.format("%02X", new Object[] { Integer.valueOf(i + 1) });
      Logger.log("Dumping sprite " + spriteName + ".", Priority.MILESTONE);
      
      int start = offsets[i] + 27158456;
      int end = offsets[(i + 1)] + 27158456;
      
      byte[] dumpedBytes = new byte[end - start];
      raf.seek(start);
      raf.read(dumpedBytes);
      byte[] writeBytes = Yay0Helper.decode(dumpedBytes);
      
      File out = new File(Directories.DUMP_SPRITE_RAW + spriteName);
      FileUtils.writeByteArrayToFile(out, writeBytes);
    }
    
    raf.close();
    
    PrintWriter pw = IOUtils.getBufferedPrintWriter(Directories.DUMP_SPRITE + "Sprites.txt");
    pw.println("% Sprite ID : Source Directory");
    for (int i = 1; i <= 233; i++)
      pw.printf("%02X : %02X" + Globals.NL, new Object[] { Integer.valueOf(i), Integer.valueOf(i) });
    pw.close();
  }
}
