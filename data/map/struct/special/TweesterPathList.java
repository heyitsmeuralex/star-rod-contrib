package data.map.struct.special;

import data.map.MapStructTypes;
import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.decoder.Pointer;
import java.nio.ByteBuffer;


public class TweesterPathList
  extends BaseStruct.MapStruct
{
  public static final TweesterPathList instance = new TweesterPathList();
  

  private TweesterPathList() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    int currentPointer;
    while ((currentPointer = fileBuffer.getInt()) != -1)
    {
      decoder.enqueueAsChild(ptr, currentPointer, MapStructTypes.TweesterPathT);
    }
  }
}
