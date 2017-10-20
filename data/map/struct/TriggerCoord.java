package data.map.struct;

import data.map.decoder.MapDecoder;
import data.map.encoder.MapEncoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.decoder.Pointer;
import data.shared.encoder.InvalidExpressionException;
import editor.map.marker.Marker;
import editor.map.marker.Marker.MarkerType;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.List;

public class TriggerCoord
  extends BaseStruct.MapStruct
{
  public static final TriggerCoord instance = new TriggerCoord();
  

  private TriggerCoord() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    fileBuffer.getFloat();
    fileBuffer.getFloat();
    fileBuffer.getFloat();
    fileBuffer.getFloat();
  }
  

  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    String markerName = String.format("Bomb_%X", new Object[] { Integer.valueOf(address) });
    float x = fileBuffer.getFloat();
    float y = fileBuffer.getFloat();
    float z = fileBuffer.getFloat();
    float r = fileBuffer.getFloat();
    
    Marker m = new Marker(markerName, Marker.MarkerType.Trigger, x, y, z, 0.0D);
    bombRadius = (r / 2.0F);
    m.setDescription("Trigger");
    decoder.addMarker(m);
    pw.printf("{BombPos:%s} %% %f %f %f %f\r\n", new Object[] { markerName, Float.valueOf(x), Float.valueOf(y), Float.valueOf(z), Float.valueOf(r) });
  }
  
  public void replaceExpression(MapEncoder encoder, String[] tokens, List<String> newTokens)
    throws InvalidExpressionException
  {
    if (tokens[0].equals("BombPos"))
    {
      Marker m = encoder.getMarker(tokens[1]);
      if (m == null)
        throw new InvalidExpressionException("No such marker: " + tokens[1]);
      m.putVector(newTokens, true);
      newTokens.add("" + 2.0F * bombRadius);
    }
  }
}
