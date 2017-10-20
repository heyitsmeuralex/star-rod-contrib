package data.map.struct.npc;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.decoder.Pointer;
import java.nio.ByteBuffer;

public class AnimationList
  extends BaseStruct.MapStruct
{
  public static final AnimationList instance = new AnimationList();
  

  private AnimationList() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    while (fileBuffer.getInt() != -1) {}
  }
}
