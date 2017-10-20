package data.map.struct.special;

import data.map.MapStructTypes;
import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.SharedStructTypes;
import data.shared.decoder.Pointer;
import java.io.PrintWriter;
import java.nio.ByteBuffer;




public class SearchBushEvent
  extends BaseStruct.MapStruct
{
  public static final SearchBushEvent instance = new SearchBushEvent();
  

  private SearchBushEvent() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    decoder.tryEnqueueAsChild(ptr, fileBuffer.getInt(), MapStructTypes.TreeModelListT, "Bush");
    decoder.tryEnqueueAsChild(ptr, fileBuffer.getInt(), MapStructTypes.TreeDropListT);
    decoder.tryEnqueueAsChild(ptr, fileBuffer.getInt(), MapStructTypes.TreeEffectVectorsT);
    decoder.tryEnqueueAsChild(ptr, fileBuffer.getInt(), SharedStructTypes.ScriptT);
  }
  

  public void print(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    decoder.printHex(ptr, fileBuffer, pw, 1);
  }
}
