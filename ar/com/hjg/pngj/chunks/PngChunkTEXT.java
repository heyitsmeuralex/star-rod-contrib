package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngjException;



public class PngChunkTEXT
  extends PngChunkTextVar
{
  public static final String ID = "tEXt";
  
  public PngChunkTEXT(ImageInfo info)
  {
    super("tEXt", info);
  }
  
  public PngChunkTEXT(ImageInfo info, String key, String val) {
    super("tEXt", info);
    setKeyVal(key, val);
  }
  
  public ChunkRaw createRawChunk()
  {
    if ((key == null) || (key.trim().length() == 0))
      throw new PngjException("Text chunk key must be non empty");
    byte[] b = ChunkHelper.toBytes(key + "\000" + val);
    ChunkRaw chunk = createEmptyChunk(b.length, false);
    data = b;
    return chunk;
  }
  

  public void parseFromRaw(ChunkRaw c)
  {
    for (int i = 0; i < data.length; i++)
      if (data[i] == 0)
        break;
    key = ChunkHelper.toString(data, 0, i);
    i++;
    val = (i < data.length ? ChunkHelper.toString(data, i, data.length - i) : "");
  }
}
