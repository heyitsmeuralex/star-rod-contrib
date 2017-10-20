package data.battle.struct;

import data.battle.decoder.BattleDecoder;
import data.shared.BaseStruct.BattleStruct;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

public class DmaArgTable
  extends BaseStruct.BattleStruct
{
  public static final DmaArgTable instance = new DmaArgTable();
  

  private DmaArgTable() {}
  

  public void scan(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    fileBuffer.position(fileBuffer.position() + 444);
  }
  

  public void print(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    decoder.printHex(ptr, fileBuffer, pw, 3);
  }
}
