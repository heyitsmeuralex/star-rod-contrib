package data.map.struct.special;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

public class TreeModelList
  extends BaseStruct.MapStruct
{
  public static final TreeModelList instance = new TreeModelList();
  

  private TreeModelList() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    listLength = fileBuffer.getInt();
    for (int i = 0; i < listLength; i++) {
      fileBuffer.getInt();
    }
  }
  
  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    decoder.printWord(pw, fileBuffer.getInt());
    

    for (int i = 4; i < length; i += 4) {
      decoder.printModelID(pw, fileBuffer.getInt());
    }
    pw.println();
  }
}
