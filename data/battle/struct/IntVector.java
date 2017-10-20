package data.battle.struct;

import data.battle.decoder.BattleDecoder;
import data.shared.BaseStruct.BattleStruct;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;

public class IntVector
  extends BaseStruct.BattleStruct
{
  public static final IntVector instance = new IntVector();
  

  private IntVector() {}
  

  public void scan(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    fileBuffer.getInt();
    fileBuffer.getInt();
    fileBuffer.getInt();
  }
  

  public void print(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    pw.print(fileBuffer.getInt() + "` ");
    pw.print(fileBuffer.getInt() + "` ");
    pw.println(fileBuffer.getInt() + "`");
  }
}
