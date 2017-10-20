package data.map.struct.shop;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.DataConstants;
import data.shared.DataConstants.ConstEnum;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

public class BadgeShopInventory extends BaseStruct.MapStruct
{
  public static final BadgeShopInventory instance = new BadgeShopInventory();
  


  private BadgeShopInventory() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer) {}
  

  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    int pos = 0;
    
    while (pos < length)
    {
      int id = fileBuffer.getInt();
      int cost = fileBuffer.getInt();
      int stringID = fileBuffer.getInt();
      pos += 12;
      String itemName = DataConstants.ItemType.getConstantString(id);
      pw.printf("%-20s %3d`    %08X ", new Object[] { itemName, Integer.valueOf(cost), Integer.valueOf(stringID) });
      pw.println(decoder.getStringComment(stringID));
    }
  }
}
