package data.battle.extra;

import data.battle.BattleStructTypes;
import data.battle.decoder.BattleDecoder;
import data.shared.decoder.Pointer;
import data.shared.decoder.Pointer.Origin;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import main.Directories;





public class StarPowerDecoder
  extends BattleDecoder
{
  public StarPowerDecoder(ByteBuffer fileBuffer, String scriptName, int start, int end, int ptrMain)
    throws IOException
  {
    sourceName = scriptName;
    
    int startAddress = -2144727040;
    int endAddress = end - start + -2144727040;
    setAddressRange(startAddress, endAddress, -2144710656);
    setOffsetRange(start, end);
    
    findLocalPointers(fileBuffer);
    enqueueAsRoot(ptrMain, BattleStructTypes.UseScriptT, Pointer.Origin.DECODED).forceName("Script_UsePower");
    
    super.decode(fileBuffer);
    
    File rawFile = new File(Directories.DUMP_STARS_RAW + sourceName + ".bin");
    File scriptFile = new File(Directories.DUMP_STARS_SRC + sourceName + ".bscr");
    File indexFile = new File(Directories.DUMP_STARS_SRC + sourceName + ".bidx");
    
    printScriptFile(scriptFile, fileBuffer);
    printIndexFile(indexFile);
    writeRawFile(rawFile, fileBuffer);
  }
}
