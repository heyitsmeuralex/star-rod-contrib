package data.map.struct.npc;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

public class AISettings
  extends BaseStruct.MapStruct
{
  public static final AISettings instance = new AISettings();
  


  private AISettings() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer) {}
  

  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    if ((length == 48) || (length == 36))
    {
      if (length == 48)
      {
        pw.printf("%7s %% move speed\r\n", new Object[] { Float.valueOf(fileBuffer.getFloat()) });
        pw.printf("    %d` %% move time\r\n", new Object[] { Integer.valueOf(fileBuffer.getInt()) });
        pw.printf("    %d` %% wait time\r\n", new Object[] { Integer.valueOf(fileBuffer.getInt()) });
      }
      
      pw.printf("%7s %% alert radius\r\n", new Object[] { Float.valueOf(fileBuffer.getFloat()) });
      pw.printf("%7s\r\n", new Object[] { Float.valueOf(fileBuffer.getFloat()) });
      pw.printf("    %d`\r\n", new Object[] { Integer.valueOf(fileBuffer.getInt()) });
      
      pw.printf("%7s %% chase speed\r\n", new Object[] { Float.valueOf(fileBuffer.getFloat()) });
      pw.printf("    %d`\r\n", new Object[] { Integer.valueOf(fileBuffer.getInt()) });
      pw.printf("    %d`\r\n", new Object[] { Integer.valueOf(fileBuffer.getInt()) });
      
      pw.printf("%7s %% chase radius\r\n", new Object[] { Float.valueOf(fileBuffer.getFloat()) });
      pw.printf("%7s\r\n", new Object[] { Float.valueOf(fileBuffer.getFloat()) });
      pw.printf("    %d`\r\n", new Object[] { Integer.valueOf(fileBuffer.getInt()) });
    }
    else
    {
      decoder.printHex(ptr, fileBuffer, pw, 8);
    }
  }
}
