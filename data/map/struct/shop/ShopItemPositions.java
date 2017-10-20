package data.map.struct.shop;

import data.map.decoder.MapDecoder;
import data.map.encoder.MapEncoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.decoder.Pointer;
import data.shared.encoder.InvalidExpressionException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.List;

public class ShopItemPositions
  extends BaseStruct.MapStruct
{
  public static final ShopItemPositions instance = new ShopItemPositions();
  


  private ShopItemPositions() {}
  


  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer) {}
  


  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    int n = 0;
    for (int i = 0; i < length; i += 4)
    {
      short modelID = fileBuffer.getShort();
      short colliderID = fileBuffer.getShort();
      
      String modelName = decoder.getModelName(modelID);
      String colliderName = decoder.getColliderName(colliderID);
      
      pw.printf("{ShopItemPos:%s:%s} ", new Object[] { modelName, colliderName });
      n++; if (n % 4 == 0)
        pw.println();
    }
    if (n % 4 != 0) {
      pw.println();
    }
  }
  
  public void replaceExpression(MapEncoder encoder, String[] tokens, List<String> newTokens) throws InvalidExpressionException
  {
    if (tokens[0].equals("ShopItemPos"))
    {
      if (tokens.length != 3) {
        throw new InvalidExpressionException("Invalid shop item expression");
      }
      int modelID = encoder.getModelID(tokens[1]);
      int colliderID = encoder.getModelID(tokens[2]);
      
      if (modelID < 0) {
        throw new InvalidExpressionException("No such model: " + tokens[1]);
      }
      if (colliderID < 0) {
        throw new InvalidExpressionException("No such collider: " + tokens[2]);
      }
      newTokens.add(String.format("%04X%04X", new Object[] { Integer.valueOf(modelID), Integer.valueOf(colliderID) }));
    }
  }
}
