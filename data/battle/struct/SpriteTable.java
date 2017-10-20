package data.battle.struct;

import data.battle.BattleStructTypes;
import data.battle.decoder.BattleDecoder;
import data.shared.BaseStruct.BattleStruct;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;




public class SpriteTable
  extends BaseStruct.BattleStruct
{
  public static final SpriteTable instance = new SpriteTable();
  

  private SpriteTable() {}
  

  public void scan(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    int start = fileBuffer.position();
    for (int i = 0; i < listLength; i++)
    {
      fileBuffer.position(start + i * 36);
      
      int flags1 = fileBuffer.getInt();
      fileBuffer.get();
      
      int a = fileBuffer.get();
      int b = fileBuffer.get();
      int c = fileBuffer.get();
      
      int targetOffsetX = fileBuffer.get();
      int targetOffsetY = fileBuffer.get();
      int opacity = fileBuffer.getShort();
      assert (opacity < 256);
      
      int ptrIdleAnimations = fileBuffer.getInt();
      decoder.tryEnqueueAsChild(ptr, ptrIdleAnimations, BattleStructTypes.IdleAnimationsT);
      
      int ptrDefenseTable = fileBuffer.getInt();
      decoder.tryEnqueueAsChild(ptr, ptrDefenseTable, BattleStructTypes.DefenseTableT);
      
      int flags2 = fileBuffer.getInt();
      int flags3 = fileBuffer.getInt();
      
      fileBuffer.getInt();
      fileBuffer.getInt();
    }
  }
  

  public void print(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    decoder.printHex(ptr, fileBuffer, pw, 9);
  }
}
