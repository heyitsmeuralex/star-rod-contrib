package data.battle.extra;

import data.battle.BattleStructTypes;
import data.battle.decoder.BattleDecoder;
import data.shared.decoder.Pointer;
import data.shared.decoder.Pointer.Origin;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import main.Directories;






public class PartnerDecoder
  extends BattleDecoder
{
  public PartnerDecoder(ByteBuffer fileBuffer, String scriptName, int start, int end, int ptrActor)
    throws IOException
  {
    sourceName = scriptName;
    
    int startAddress = -2145157120;
    int endAddress = end - start + -2145157120;
    setAddressRange(startAddress, endAddress, -2145132544);
    setOffsetRange(start, end);
    
    findLocalPointers(fileBuffer);
    Pointer ptr = enqueueAsRoot(ptrActor, BattleStructTypes.ActorT, Pointer.Origin.DECODED);
    ptr.forceName(scriptName.substring(3));
    
    super.decode(fileBuffer);
    
    File rawFile = new File(Directories.DUMP_ALLY_RAW + sourceName + ".bin");
    File scriptFile = new File(Directories.DUMP_ALLY_SRC + sourceName + ".bscr");
    File indexFile = new File(Directories.DUMP_ALLY_SRC + sourceName + ".bidx");
    
    printScriptFile(scriptFile, fileBuffer);
    printIndexFile(indexFile);
    writeRawFile(rawFile, fileBuffer);
  }
}
