package data.battle.extra;

import data.battle.BattleStructTypes;
import data.battle.decoder.BattleDecoder;
import data.shared.SharedStructTypes;
import data.shared.decoder.Pointer;
import data.shared.decoder.Pointer.Origin;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import main.Directories;





public class ItemDecoder
  extends BattleDecoder
{
  public ItemDecoder(ByteBuffer fileBuffer, String scriptName, int start, int end, int ptrMain)
    throws IOException
  {
    sourceName = scriptName;
    
    int endAddress = end - start + -2144727040;
    setAddressRange(-2144727040, endAddress, -2144710656);
    setOffsetRange(start, end);
    
    findLocalPointers(fileBuffer);
    enqueueAsRoot(ptrMain, BattleStructTypes.UseScriptT, Pointer.Origin.DECODED).forceName("Script_UseItem");
    
    super.decode(fileBuffer);
    
    File rawFile = new File(Directories.DUMP_ITEM_RAW + sourceName + ".bin");
    File scriptFile = new File(Directories.DUMP_ITEM_SRC + sourceName + ".bscr");
    File indexFile = new File(Directories.DUMP_ITEM_SRC + sourceName + ".bidx");
    
    printScriptFile(scriptFile, fileBuffer);
    printIndexFile(indexFile);
    writeRawFile(rawFile, fileBuffer);
  }
  

  public ItemDecoder(ByteBuffer fileBuffer, String scriptName, int start, int end, int main, int... scriptHints)
    throws IOException
  {
    sourceName = scriptName;
    
    int endAddress = end - start + -2144727040;
    setAddressRange(-2144727040, endAddress, -2144710656);
    setOffsetRange(start, end);
    
    findLocalPointers(fileBuffer);
    enqueueAsRoot(main, BattleStructTypes.UseScriptT, Pointer.Origin.DECODED).forceName("Script_UseItem");
    
    for (int i : scriptHints) {
      enqueueAsRoot(i, SharedStructTypes.ScriptT, Pointer.Origin.HINT);
    }
    super.decode(fileBuffer);
    
    File rawFile = new File(Directories.DUMP_ITEM_RAW + sourceName + ".bin");
    File scriptFile = new File(Directories.DUMP_ITEM_SRC + sourceName + ".bscr");
    File indexFile = new File(Directories.DUMP_ITEM_SRC + sourceName + ".bidx");
    
    printScriptFile(scriptFile, fileBuffer);
    printIndexFile(indexFile);
    writeRawFile(rawFile, fileBuffer);
  }
}
