package data.battle.extra;

import data.battle.BattleStructTypes;
import data.battle.decoder.BattleDecoder;
import data.shared.decoder.Pointer;
import data.shared.decoder.Pointer.Origin;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import main.Directories;





public class MoveDecoder
  extends BattleDecoder
{
  public static final class MoveSectionData
  {
    public final String name;
    public final int start;
    public final int end;
    public final List<Integer> mainAddressList;
    public final List<String> mainNameList;
    
    public MoveSectionData(String name, int start, int end)
    {
      this.name = name;
      this.start = start;
      this.end = end;
      mainAddressList = new LinkedList();
      mainNameList = new LinkedList();
    }
  }
  

  public MoveDecoder(ByteBuffer fileBuffer, MoveSectionData scriptData)
    throws IOException
  {
    startOffset = start;
    endOffset = end;
    sourceName = name;
    
    int startAddress = -2144727040;
    int endAddress = endOffset - startOffset + -2144727040;
    setAddressRange(startAddress, endAddress, -2144709824);
    setOffsetRange(start, end);
    
    findLocalPointers(fileBuffer);
    for (int i = 0; i < mainAddressList.size(); i++)
    {
      int addr = ((Integer)mainAddressList.get(i)).intValue();
      String name = (String)mainNameList.get(i);
      enqueueAsRoot(addr, BattleStructTypes.UseScriptT, Pointer.Origin.DECODED).forceName(name);
    }
    
    super.decode(fileBuffer);
    
    File rawFile = new File(Directories.DUMP_MOVE_RAW + sourceName + ".bin");
    File scriptFile = new File(Directories.DUMP_MOVE_SRC + sourceName + ".bscr");
    File indexFile = new File(Directories.DUMP_MOVE_SRC + sourceName + ".bidx");
    
    printScriptFile(scriptFile, fileBuffer);
    printIndexFile(indexFile);
    writeRawFile(rawFile, fileBuffer);
  }
}
