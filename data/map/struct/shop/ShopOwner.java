package data.map.struct.shop;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.SharedStructTypes;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;



public class ShopOwner
  extends BaseStruct.MapStruct
{
  public static final ShopOwner instance = new ShopOwner();
  

  private ShopOwner() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    fileBuffer.getInt();
    fileBuffer.getInt();
    fileBuffer.getInt();
    int ptrScript = fileBuffer.getInt();
    fileBuffer.getInt();
    fileBuffer.getInt();
    int ptrStrings = fileBuffer.getInt();
    
    decoder.enqueueAsChild(ptr, ptrScript, SharedStructTypes.ScriptT);
    decoder.enqueueAsChild(ptr, ptrStrings, SharedStructTypes.IntTableT);
  }
  

  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    decoder.printHex(ptr, fileBuffer, pw, 8);
  }
}
