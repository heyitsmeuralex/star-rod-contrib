package data.map.struct.special;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.DataConstants;
import data.shared.DataConstants.ConstEnum;
import data.shared.decoder.Pointer;
import editor.map.marker.Marker;
import editor.map.marker.Marker.MarkerType;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

public class TreeDropList extends BaseStruct.MapStruct
{
  public static final TreeDropList instance = new TreeDropList();
  

  private TreeDropList() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    listLength = fileBuffer.getInt();
    for (int i = 0; i < listLength; i++)
    {
      fileBuffer.getInt();
      fileBuffer.getInt();
      fileBuffer.getInt();
      fileBuffer.getInt();
      fileBuffer.getInt();
      fileBuffer.getInt();
      fileBuffer.getInt();
    }
  }
  

  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    decoder.printWord(pw, fileBuffer.getInt());
    pw.println();
    
    for (int i = 0; i < listLength; i++)
    {
      int itemID = fileBuffer.getInt();
      int x = fileBuffer.getInt();
      int y = fileBuffer.getInt();
      int z = fileBuffer.getInt();
      int type = fileBuffer.getInt();
      int flag = fileBuffer.getInt();
      int unknown = fileBuffer.getInt();
      
      assert (DataConstants.ItemType.has(itemID));
      String itemName = DataConstants.ItemType.getConstantString(itemID);
      
      String markerName = String.format("Tree%X_Drop%d", new Object[] { Integer.valueOf(address), Integer.valueOf(i) });
      Marker m = new Marker(markerName, Marker.MarkerType.Position, x, y, z, 0.0D);
      m.setDescription(itemName);
      decoder.addMarker(m);
      
      pw.print(itemName + " ");
      pw.print("{Vec3d:" + markerName + "} ");
      decoder.printWord(pw, type);
      decoder.printScriptWord(pw, flag);
      decoder.printScriptWord(pw, unknown);
      pw.println();
    }
  }
}
