package data.requests;

import data.battle.BattleStructTypes;
import data.shared.DataConstants;
import data.shared.decoder.DataDecoder;
import data.shared.decoder.Pointer;
import data.shared.struct.script.Script;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import main.DevContext;

public class IsolatedBattleScriptDumper
  extends DataDecoder
{
  private final PrintWriter pw;
  
  public static void main(String[] args)
    throws IOException
  {
    DevContext.initialize();
    ByteBuffer romBuffer = DevContext.getPristineRomBuffer();
    PrintWriter pw = new PrintWriter(System.out);
    
    IsolatedBattleScriptDumper dumper = new IsolatedBattleScriptDumper(romBuffer, pw);
    pw.println();
    
    dumper.printScript(romBuffer, 1784756, "Script_Player_HandleEvent");
    
    pw.close();
    DevContext.exit();
  }
  


  public IsolatedBattleScriptDumper(ByteBuffer romBuffer, PrintWriter pw)
  {
    super(BattleStructTypes.battleTypes, BattleStructTypes.ActorT, DataConstants.battleFunctionLib, DataConstants.battleScriptLib);
    this.pw = pw;
  }
  
  public void printScript(ByteBuffer romBuffer, int offset) throws IOException
  {
    printScript(romBuffer, offset, "");
  }
  
  public void printScript(ByteBuffer romBuffer, int offset, String name) throws IOException
  {
    pw.printf("Script: %s (%08X)\r\n", new Object[] { name, Integer.valueOf(offset) });
    pw.println();
    
    romBuffer.position(offset);
    Script.scan(this, romBuffer);
    int length = romBuffer.position() - offset;
    
    romBuffer.position(offset);
    Script.print(this, 0, length, romBuffer, pw);
    pw.println();
  }
  

  public String getSourceName()
  {
    return DevContext.getPristineRomName();
  }
  

  public String printFunctionArgs(PrintWriter pw, int[] arg, int lineAddress)
  {
    for (int i = 1; i < arg.length; i++) {
      printScriptWord(pw, arg[i]);
    }
    return "";
  }
  






























  public void scanScript(Pointer ptr, ByteBuffer fileBuffer)
  {
    throw new RuntimeException();
  }
}
