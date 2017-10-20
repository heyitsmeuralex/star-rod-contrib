package data.shared;

import data.battle.decoder.BattleDecoder;
import data.battle.encoder.BattleEncoder;
import data.map.decoder.MapDecoder;
import data.map.encoder.MapEncoder;
import data.shared.decoder.DataDecoder;
import data.shared.decoder.IPrint;
import data.shared.decoder.IScan;
import data.shared.decoder.Pointer;
import data.shared.encoder.DataEncoder;
import data.shared.encoder.IParseOffset;
import data.shared.encoder.IPatchLength;
import data.shared.encoder.IReplaceExpression;
import data.shared.encoder.IReplaceSpecial;
import data.shared.encoder.InvalidExpressionException;
import data.shared.encoder.Patch;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.List;











public class BaseStruct<D extends DataDecoder, E extends DataEncoder>
  implements IPrint<D>, IScan<D>, IParseOffset<E>, IReplaceExpression<E>, IReplaceSpecial<E>, IPatchLength<E>
{
  public static final BaseStruct<DataDecoder, DataEncoder> instance = new BaseStruct();
  



  public BaseStruct() {}
  


  public void scan(D decoder, Pointer ptr, ByteBuffer fileBuffer) {}
  


  public void print(D decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    decoder.printHex(ptr, fileBuffer, pw);
  }
  

  public Patch parseStructOffset(E encoder, String offsetName)
  {
    return null;
  }
  

  public void replaceExpression(E encoder, String[] args, List<String> newTokenList)
    throws InvalidExpressionException
  {}
  

  public void replaceStructConstants(E encoder, Patch patch) {}
  

  public int getPatchLength(E encoder, Patch patch)
  {
    int length = 0;
    
    for (String[] line : lines)
    {
      for (String s : line)
      {
        char prefix = s.charAt(0);
        if ((prefix == '$') || (prefix == '*')) {
          length += 4;
        } else {
          length += DataUtils.getSize(s);
        }
      }
    }
    return length;
  }
  
  public static abstract class BattleStruct
    extends BaseStruct<BattleDecoder, BattleEncoder>
  {
    public BattleStruct() {}
  }
  
  public static abstract class MapStruct
    extends BaseStruct<MapDecoder, MapEncoder>
  {
    public MapStruct() {}
  }
}
