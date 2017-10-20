package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;






public class PngChunkIDAT
  extends PngChunkMultiple
{
  public static final String ID = "IDAT";
  
  public PngChunkIDAT(ImageInfo i)
  {
    super("IDAT", i);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.NA;
  }
  
  public ChunkRaw createRawChunk()
  {
    return null;
  }
  
  public void parseFromRaw(ChunkRaw c) {}
}
