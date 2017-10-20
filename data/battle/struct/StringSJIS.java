package data.battle.struct;

import data.battle.decoder.BattleDecoder;
import data.shared.BaseStruct.BattleStruct;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import util.japanese.JapaneseHelper;

public class StringSJIS
  extends BaseStruct.BattleStruct
{
  public static final StringSJIS instance = new StringSJIS();
  

  private StringSJIS() {}
  

  public void scan(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    for (;;)
    {
      byte bh = fileBuffer.get();
      
      boolean doubleByteCharacter = ((bh & 0xF0) == Byte.MIN_VALUE) || ((bh & 0xF0) == -112) || ((bh & 0xF0) == -32) || ((bh & 0xF0) == -16);
      



      if (bh == 0) {
        break;
      }
      if (doubleByteCharacter) {
        fileBuffer.get();
      }
    }
    fileBuffer.position(fileBuffer.position() + 3 & 0xFFFFFFFC);
  }
  

  public void print(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    int pos = fileBuffer.position();
    ByteBuffer bb = ByteBuffer.allocateDirect(length);
    byte b;
    while ((b = fileBuffer.get()) != 0)
      bb.put(b);
    bb.flip();
    
    String romaji = JapaneseHelper.convertSJIStoRomaji(bb);
    pw.println("% " + romaji);
    
    fileBuffer.position(pos);
    decoder.printHex(ptr, fileBuffer, pw);
  }
}
