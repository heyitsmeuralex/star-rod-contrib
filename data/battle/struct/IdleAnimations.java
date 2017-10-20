package data.battle.struct;

import data.battle.decoder.BattleDecoder;
import data.shared.BaseStruct.BattleStruct;
import data.shared.DataConstants;
import data.shared.DataConstants.ConstEnum;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import util.Logger;

public class IdleAnimations extends BaseStruct.BattleStruct
{
  public static final IdleAnimations instance = new IdleAnimations();
  

  private IdleAnimations() {}
  

  public void scan(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    while (fileBuffer.getInt() != 0) {
      fileBuffer.getInt();
    }
  }
  
  public void print(BattleDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    int key = 0;
    while ((key = fileBuffer.getInt()) != 0)
    {
      if (DataConstants.StatusType.has(key))
      {
        String keyName = DataConstants.StatusType.getConstantString(key);
        if (keyName.length() > 17) {
          pw.printf("%-23s ", new Object[] { keyName });
        } else {
          pw.printf("%-17s ", new Object[] { keyName });
        }
      }
      else {
        Logger.logWarning("WARNING: Unknown status type found! " + key);
        pw.printf("%08X ", new Object[] { Integer.valueOf(key) });
      }
      pw.printf("%08X\n", new Object[] { Integer.valueOf(fileBuffer.getInt()) });
    }
    pw.println(DataConstants.StatusType.getConstantString(0));
  }
}
