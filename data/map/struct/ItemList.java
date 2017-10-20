package data.map.struct;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.DataConstants;
import data.shared.DataConstants.ConstEnum;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

public class ItemList extends BaseStruct.MapStruct
{
  public static final ItemList instance = new ItemList();
  

  private ItemList() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    listLength = 0;
    while ((listLength < 10) && (fileBuffer.getInt() != 0)) {
      listLength += 1;
    }
  }
  
  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    for (int i = 0; i < listLength; i++)
    {
      int itemID = fileBuffer.getInt();
      pw.println(DataConstants.ItemType.getConstantString(itemID));
    }
    
    if (listLength < 10) {
      pw.println("00000000");
    }
  }
}
