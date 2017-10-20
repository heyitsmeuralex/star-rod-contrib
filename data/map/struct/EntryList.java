package data.map.struct;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.decoder.Pointer;
import editor.map.marker.Marker;
import editor.map.marker.Marker.MarkerType;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

public class EntryList
  extends BaseStruct.MapStruct
{
  public static final EntryList instance = new EntryList();
  

  private EntryList() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    for (int i = 0; i < listLength; i++)
    {
      fileBuffer.getInt();
      fileBuffer.getInt();
      fileBuffer.getInt();
      fileBuffer.getInt();
    }
  }
  


  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    int n = 0;
    for (int i = 0; i < length; i += 16)
    {
      float x = fileBuffer.getFloat();
      float y = fileBuffer.getFloat();
      float z = fileBuffer.getFloat();
      float a = fileBuffer.getFloat();
      
      String markerName = String.format("Entry%X", new Object[] { Integer.valueOf(n) });
      decoder.addMarker(new Marker(markerName, Marker.MarkerType.Entry, x, y, z, a));
      pw.printf("{Vec4f:%s} %% %6.1f %6.1f %6.1f %6.1f\r\n", new Object[] { markerName, Float.valueOf(x), Float.valueOf(y), Float.valueOf(z), Float.valueOf(a) });
      n++;
    }
  }
}
