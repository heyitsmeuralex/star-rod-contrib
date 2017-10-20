package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngjException;





public class PngChunkSTER
  extends PngChunkSingle
{
  public static final String ID = "sTER";
  private byte mode;
  
  public PngChunkSTER(ImageInfo info)
  {
    super("sTER", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.BEFORE_IDAT;
  }
  
  public ChunkRaw createRawChunk()
  {
    ChunkRaw c = createEmptyChunk(1, true);
    data[0] = mode;
    return c;
  }
  
  public void parseFromRaw(ChunkRaw chunk)
  {
    if (len != 1)
      throw new PngjException("bad chunk length " + chunk);
    mode = data[0];
  }
  


  public byte getMode()
  {
    return mode;
  }
  


  public void setMode(byte mode)
  {
    this.mode = mode;
  }
}
