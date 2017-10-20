package data.map.struct.special;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.decoder.Pointer;
import editor.map.marker.Marker;
import editor.map.marker.Marker.MarkerType;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

public class TweesterPath
  extends BaseStruct.MapStruct
{
  public static final TweesterPath instance = new TweesterPath();
  






  private TweesterPath() {}
  






  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    while (fileBuffer.getInt() != -2147483647) {}
  }
  


  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    int count = 0;
    
    int x;
    while ((x = fileBuffer.getInt()) != -2147483647)
    {
      int y = fileBuffer.getInt();
      int z = fileBuffer.getInt();
      count++;
      
      String markerName = ptr.getPointerName().substring(1) + "_" + count;
      
      Marker m = new Marker(markerName, Marker.MarkerType.Position, x, y, z, 0.0D);
      m.setDescription(ptr.getPointerName());
      decoder.addMarker(m);
      
      pw.println("{Vec3d:" + markerName + "}");
    }
    
    pw.printf("%08X\r\n", new Object[] { Integer.valueOf(x) });
  }
}
