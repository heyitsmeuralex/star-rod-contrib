package data.shared.struct.other;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct;
import data.shared.decoder.DataDecoder;
import data.shared.decoder.Pointer;
import data.shared.encoder.DataEncoder;
import editor.map.marker.Marker;
import editor.map.marker.Marker.MarkerType;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import org.lwjgl.util.vector.Vector3f;


public class VectorList
  extends BaseStruct<DataDecoder, DataEncoder>
{
  public static final VectorList instance = new VectorList();
  

  private VectorList() {}
  

  public void scan(DataDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    if ((decoder instanceof MapDecoder))
    {
      String markerName = String.format("Path_%08X", new Object[] { Integer.valueOf(decoder.toAddress(fileBuffer.position())) });
      ArrayList<Vector3f> vectors = new ArrayList(listLength);
      for (int i = 0; i < listLength; i++)
      {
        float x = fileBuffer.getFloat();
        float y = fileBuffer.getFloat();
        float z = fileBuffer.getFloat();
        vectors.add(new Vector3f(x, y, z));
      }
      
      Vector3f last = (Vector3f)vectors.get(listLength - 1);
      Marker pathMarker = new Marker(markerName, Marker.MarkerType.Path, x, y, z, 0.0D);
      pathMarker.addPath(vectors);
      ((MapDecoder)decoder).addMarker(pathMarker);
    }
    else
    {
      for (int i = 0; i < listLength; i++)
      {
        fileBuffer.getFloat();
        fileBuffer.getFloat();
        fileBuffer.getFloat();
      }
    }
  }
  

  public void print(DataDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    String markerName = String.format("Path_%08X", new Object[] { Integer.valueOf(decoder.toAddress(fileBuffer.position())) });
    pw.println("% {Path3f:" + markerName + "}");
    
    for (int i = 0; i < listLength; i++)
    {
      float x = fileBuffer.getFloat();
      float y = fileBuffer.getFloat();
      float z = fileBuffer.getFloat();
      pw.printf("%f %f %f\r\n", new Object[] { Float.valueOf(x), Float.valueOf(y), Float.valueOf(z) });
    }
  }
}
