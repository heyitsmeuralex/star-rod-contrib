package data.map.struct.special;

import data.map.decoder.MapDecoder;
import data.shared.BaseStruct.MapStruct;
import data.shared.decoder.Pointer;
import java.nio.ByteBuffer;

public class NpcList
  extends BaseStruct.MapStruct
{
  public static final NpcList instance = new NpcList();
  

  private NpcList() {}
  

  public void scan(MapDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    while (fileBuffer.getInt() != -1) {}
  }
}
