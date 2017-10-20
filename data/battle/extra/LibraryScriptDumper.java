package data.battle.extra;

import data.battle.decoder.BattleDecoder;
import data.shared.struct.script.Script;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import main.DevContext;

public class LibraryScriptDumper
  extends BattleDecoder
{
  public static void main(String[] args) throws IOException
  {
    DevContext.initialize();
    ByteBuffer fileBuffer = DevContext.getPristineRomBuffer();
    LibraryScriptDumper dumper = new LibraryScriptDumper();
    

    dumper.printScript(fileBuffer, 7917352);
    DevContext.exit();
  }
  


  public LibraryScriptDumper()
    throws IOException
  {}
  

  public void printScript(ByteBuffer romBuffer, int offset)
    throws IOException
  {
    PrintWriter pw = new PrintWriter(System.out);
    
    romBuffer.position(offset);
    Script.scan(this, romBuffer);
    int length = romBuffer.position() - offset;
    
    romBuffer.position(offset);
    Script.print(this, 0, length, romBuffer, pw);
    pw.close();
  }
}
