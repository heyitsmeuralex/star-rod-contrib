package data.map.struct;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

public class LavaResetList
  extends BaseStruct.MapStruct
{
  public static final LavaResetList instance = new LavaResetList();
  


  private LavaResetList() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    listLength = 0;
    int colliderID;
    do {
      colliderID = fileBuffer.getInt();
      fileBuffer.getFloat();
      fileBuffer.getFloat();
      fileBuffer.getFloat();
      listLength += 1;
    }
    while (colliderID != -1);
  }
  

  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    for (int i = 0; i < listLength; i++)
    {
      int colliderID = fileBuffer.getInt();
      int x = fileBuffer.getInt();
      int y = fileBuffer.getInt();
      int z = fileBuffer.getInt();
      decoder.printColliderID(pw, colliderID);
      decoder.printWord(pw, x);
      decoder.printWord(pw, y);
      decoder.printWord(pw, z);
      pw.println("% " + Float.intBitsToFloat(x) + " " + Float.intBitsToFloat(y) + " " + Float.intBitsToFloat(z));
    }
    
    if (listLength < 10) {
      pw.println("00000000");
    }
  }
}
