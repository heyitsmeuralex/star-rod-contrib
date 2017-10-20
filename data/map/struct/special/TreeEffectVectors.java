package data.map.struct.special;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

public class TreeEffectVectors
  extends BaseStruct.MapStruct
{
  public static final TreeEffectVectors instance = new TreeEffectVectors();
  

  private TreeEffectVectors() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    listLength = fileBuffer.getInt();
    for (int i = 0; i < listLength; i++)
    {
      fileBuffer.getInt();
      fileBuffer.getInt();
      fileBuffer.getInt();
    }
  }
  

  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    decoder.printWord(pw, fileBuffer.getInt());
    pw.println();
    
    decoder.printHex(fileBuffer, pw, 3, 3 * listLength);
  }
}
