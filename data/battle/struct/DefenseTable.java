package data.battle.struct;

import data.battle.decoder.BattleDecoder;
import data.shared.BaseStruct.BattleStruct;
import data.shared.DataConstants;
import data.shared.DataConstants.ConstEnum;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import util.Logger;

public class DefenseTable extends BaseStruct.BattleStruct
{
  public static final DefenseTable instance = new DefenseTable();
  

  private DefenseTable() {}
  

  public void scan(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    while (fileBuffer.getInt() != 0) {
      fileBuffer.getInt();
    }
  }
  
  public void print(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    int defenseType = 0;
    while ((defenseType = fileBuffer.getInt()) != 0)
    {
      if (DataConstants.ElementType.has(defenseType))
      {
        pw.printf("%-16s", new Object[] { DataConstants.ElementType.getConstantString(defenseType) });
      }
      else
      {
        Logger.logWarning("WARNING: Unknown defense type found! " + defenseType);
        pw.printf("%08X ", new Object[] { Integer.valueOf(defenseType) });
      }
      pw.printf("%08X\n", new Object[] { Integer.valueOf(fileBuffer.getInt()) });
    }
    pw.println(DataConstants.ElementType.getConstantString(0));
  }
}
