package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;


public abstract class PngChunkSingle
  extends PngChunk
{
  protected PngChunkSingle(String id, ImageInfo imgInfo)
  {
    super(id, imgInfo);
  }
  
  public final boolean allowsMultiple() {
    return false;
  }
  
  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (id == null ? 0 : id.hashCode());
    return result;
  }
  
  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PngChunkSingle other = (PngChunkSingle)obj;
    if (id == null) {
      if (id != null)
        return false;
    } else if (!id.equals(id))
      return false;
    return true;
  }
}
