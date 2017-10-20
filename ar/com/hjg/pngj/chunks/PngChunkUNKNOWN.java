package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;




public class PngChunkUNKNOWN
  extends PngChunkMultiple
{
  public PngChunkUNKNOWN(String id, ImageInfo info)
  {
    super(id, info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.NONE;
  }
  
  public ChunkRaw createRawChunk()
  {
    return raw;
  }
  


  public void parseFromRaw(ChunkRaw c) {}
  

  public byte[] getData()
  {
    return raw.data;
  }
  
  public void setData(byte[] data)
  {
    raw.data = data;
  }
}
