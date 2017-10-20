package data.battle.struct;

import data.battle.BattleStructTypes;
import data.battle.decoder.BattleDecoder;
import data.shared.BaseStruct.BattleStruct;
import data.shared.SharedStructTypes;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;


public class StageTable
  extends BaseStruct.BattleStruct
{
  public static final StageTable instance = new StageTable();
  

  private StageTable() {}
  

  public void scan(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    int v;
    while ((v = fileBuffer.getInt()) != 0)
    {
      decoder.enqueueAsChild(ptr, v, SharedStructTypes.StringT);
      decoder.enqueueAsChild(ptr, fileBuffer.getInt(), BattleStructTypes.StageT);
    }
    fileBuffer.getInt();
  }
  

  public void print(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    decoder.printHex(ptr, fileBuffer, pw, 2);
  }
}
