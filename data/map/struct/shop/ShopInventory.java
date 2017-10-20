package data.map.struct.shop;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.DataConstants;
import data.shared.DataConstants.ConstEnum;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

public class ShopInventory extends BaseStruct.MapStruct
{
  public static final ShopInventory instance = new ShopInventory();
  

  private ShopInventory() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    while (fileBuffer.getInt() != 0)
    {
      fileBuffer.getInt();
      fileBuffer.getInt();
    }
    
    fileBuffer.getInt();
    fileBuffer.getInt();
  }
  

  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    int itemID;
    
    while ((itemID = fileBuffer.getInt()) != 0)
    {
      int cost = fileBuffer.getInt();
      int desc = fileBuffer.getInt();
      
      pw.printf("%-20s ", new Object[] { DataConstants.ItemType.getConstantString(itemID) });
      decoder.printWord(pw, cost);
      decoder.printWord(pw, desc);
      pw.println(decoder.getStringComment(desc));
    }
    
    pw.printf("%08X %08X %08X\r\n", new Object[] { Integer.valueOf(itemID), Integer.valueOf(fileBuffer.getInt()), Integer.valueOf(fileBuffer.getInt()) });
  }
}
