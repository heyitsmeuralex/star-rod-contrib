package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;


public abstract class PngChunkMultiple
  extends PngChunk
{
  protected PngChunkMultiple(String id, ImageInfo imgInfo)
  {
    super(id, imgInfo);
  }
  
  public final boolean allowsMultiple()
  {
    return true;
  }
}
