package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;





public class PngChunkIEND
  extends PngChunkSingle
{
  public static final String ID = "IEND";
  
  public PngChunkIEND(ImageInfo info)
  {
    super("IEND", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.NA;
  }
  
  public ChunkRaw createRawChunk()
  {
    ChunkRaw c = new ChunkRaw(0, ChunkHelper.b_IEND, false);
    return c;
  }
  
  public void parseFromRaw(ChunkRaw c) {}
}
