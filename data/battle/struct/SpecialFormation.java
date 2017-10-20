package data.battle.struct;

import data.battle.BattleStructTypes;
import data.battle.decoder.BattleDecoder;
import data.shared.BaseStruct.BattleStruct;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;



public class SpecialFormation
  extends BaseStruct.BattleStruct
{
  public static final SpecialFormation instance = new SpecialFormation();
  

  private SpecialFormation() {}
  

  public void scan(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    for (int i = 0; i < listLength; i++)
    {
      int ptrEnemy = fileBuffer.getInt();
      decoder.tryEnqueueAsChild(ptr, ptrEnemy, BattleStructTypes.ActorT);
      
      int ptrPosition = fileBuffer.getInt();
      decoder.tryEnqueueAsChild(ptr, ptrPosition, BattleStructTypes.Vector3dT);
      
      fileBuffer.position(fileBuffer.position() + 20);
    }
  }
  

  public void print(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    decoder.printHex(ptr, fileBuffer, pw, 7);
  }
}
