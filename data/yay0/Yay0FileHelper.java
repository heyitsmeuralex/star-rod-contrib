package data.yay0;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;











public class Yay0FileHelper
{
  public Yay0FileHelper() {}
  
  public static void decode(File source, File dest)
    throws IOException
  {
    if ((source == null) || (dest == null)) {
      throw new IllegalArgumentException("File arguments may not be null!");
    }
    if (!dest.isDirectory()) {
      throw new IllegalArgumentException("Destination must be a directory!");
    }
    RandomAccessFile raf = new RandomAccessFile(source, "r");
    
    int magicNumber = raf.readInt();
    assert (magicNumber == 1499560240);
    int decompressedSize = raf.readInt();
    int linkOffset = raf.readInt();
    int sourceOffset = raf.readInt();
    
    byte currentCommand = 0;
    int commandOffset = 16;
    int remainingBits = 0;
    
    byte[] decoded = new byte[decompressedSize];
    int decodedBytes = 0;
    

    do
    {
      if (remainingBits == 0)
      {
        raf.seek(commandOffset);
        currentCommand = raf.readByte();
        commandOffset++;
        remainingBits = 8;
      }
      

      if ((currentCommand & 0x80) != 0)
      {
        raf.seek(sourceOffset);
        decoded[decodedBytes] = raf.readByte();
        sourceOffset++;
        decodedBytes++;

      }
      else
      {
        raf.seek(linkOffset);
        short link = raf.readShort();
        linkOffset += 2;
        int dist = link & 0xFFF;
        int copySrc = decodedBytes - (dist + 1);
        int length = link >> 12 & 0xF;
        

        if (length == 0)
        {
          raf.seek(sourceOffset);
          length = (raf.readByte() & 0xFF) + 16;
          sourceOffset++;
        }
        
        length += 2;
        

        for (int i = 0; i < length; i++)
        {
          decoded[decodedBytes] = decoded[(copySrc + i)];
          decodedBytes++;
        }
      }
      
      currentCommand = (byte)(currentCommand << 1);
      remainingBits--;
    } while (decodedBytes < decompressedSize);
    
    raf.close();
    
    FileOutputStream out = new FileOutputStream(new File("./test/hos_bt02_hit.bin"));
    out.write(decoded);
    out.close();
  }
}
