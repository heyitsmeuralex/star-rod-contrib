package util.japanese;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import main.DevContext;
import util.SimpleProgressBarDialog;

public class JapaneseTextScanner
{
  public JapaneseTextScanner() {}
  
  public static void main(String[] args) throws IOException
  {
    DevContext.initialize();
    
    RandomAccessFile raf = DevContext.getPristineRomReader();
    Writer fout = new java.io.BufferedWriter(new OutputStreamWriter(new FileOutputStream("./SJIS.txt"), "UTF-16"));
    SimpleProgressBarDialog dialog = new SimpleProgressBarDialog("Japanese Text Scanner", "Scanning ROM...");
    
    boolean reading = false;
    ByteBuffer bb = ByteBuffer.allocate(32);
    
    while (raf.getFilePointer() < raf.length())
    {
      byte bh = raf.readByte();
      byte bl = 0;
      
      boolean matchHiragana = bh == -126;
      boolean matchKatakana = bh == -125;
      boolean matchSpace = bh == 32;
      boolean terminator = bh == 0;
      
      if ((matchHiragana) || (matchKatakana)) {
        bl = raf.readByte();
      }
      boolean sjisCharacter = (matchHiragana) || (matchKatakana) || ((reading) && (matchSpace));
      
      if (!reading) {
        reading = sjisCharacter;
      }
      if (reading)
      {

        if ((bb.position() > 30) || ((!sjisCharacter) && (!terminator)))
        {
          reading = false;
          bb.clear();



        }
        else if (!terminator)
        {
          if (matchSpace) {
            bb.put(bh);
          } else {
            bb.put(bh).put(bl);

          }
          


        }
        else if (bb.position() < 4)
        {
          reading = false;
          bb.clear();
        }
        else
        {
          String offsetString = String.format("%08X : ", new Object[] { Integer.valueOf((int)raf.getFilePointer() - (bb.position() + 1)) });
          print(offsetString, bb, fout);
          
          reading = false;
          bb.clear();
        }
      } else {
        dialog.setProgress((int)(100.0F * ((float)raf.getFilePointer() / (float)raf.length())));
      }
    }
    dialog.dismiss();
    fout.close();
    raf.close();
    DevContext.exit();
  }
  
  public static void print(String s, ByteBuffer inBB, Writer fout) throws IOException
  {
    inBB.put((byte)10).flip();
    
    Charset sjis = Charset.forName("Shift-JIS");
    Charset utf8 = Charset.forName("UTF-16");
    
    CharBuffer decodedCB = sjis.decode(inBB);
    ByteBuffer outBB = utf8.encode(decodedCB);
    byte[] outArray = new byte[outBB.limit()];
    outBB.get(outArray);
    
    fout.write(s + new String(outArray, utf8));
  }
}
