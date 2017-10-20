package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngjExceptionInternal;
import java.io.OutputStream;





















public abstract class PngChunk
{
  public final String id;
  public final boolean crit;
  public final boolean pub;
  public final boolean safe;
  protected final ImageInfo imgInfo;
  protected ChunkRaw raw;
  private boolean priority = false;
  


  protected int chunkGroup = -1;
  






  public static enum ChunkOrderingConstraint
  {
    NONE, 
    


    BEFORE_PLTE_AND_IDAT, 
    


    AFTER_PLTE_BEFORE_IDAT, 
    


    AFTER_PLTE_BEFORE_IDAT_PLTE_REQUIRED, 
    


    BEFORE_IDAT, 
    


    AFTER_IDAT, 
    


    NA;
    
    private ChunkOrderingConstraint() {}
    public boolean mustGoBeforePLTE() { return this == BEFORE_PLTE_AND_IDAT; }
    
    public boolean mustGoBeforeIDAT()
    {
      return (this == BEFORE_IDAT) || (this == BEFORE_PLTE_AND_IDAT) || (this == AFTER_PLTE_BEFORE_IDAT);
    }
    


    public boolean mustGoAfterPLTE()
    {
      return (this == AFTER_PLTE_BEFORE_IDAT) || (this == AFTER_PLTE_BEFORE_IDAT_PLTE_REQUIRED);
    }
    
    public boolean mustGoAfterIDAT() {
      return this == AFTER_IDAT;
    }
    
    public boolean isOk(int currentChunkGroup, boolean hasplte) {
      if (this == NONE)
        return true;
      if (this == BEFORE_IDAT)
        return currentChunkGroup < 4;
      if (this == BEFORE_PLTE_AND_IDAT)
        return currentChunkGroup < 2;
      if (this == AFTER_PLTE_BEFORE_IDAT) {
        return currentChunkGroup < 4;
      }
      if (this == AFTER_IDAT)
        return currentChunkGroup > 4;
      return false;
    }
  }
  
  public PngChunk(String id, ImageInfo imgInfo) {
    this.id = id;
    this.imgInfo = imgInfo;
    crit = ChunkHelper.isCritical(id);
    pub = ChunkHelper.isPublic(id);
    safe = ChunkHelper.isSafeToCopy(id);
  }
  
  protected final ChunkRaw createEmptyChunk(int len, boolean alloc) {
    ChunkRaw c = new ChunkRaw(len, ChunkHelper.toBytes(id), alloc);
    return c;
  }
  




  public final int getChunkGroup()
  {
    return chunkGroup;
  }
  


  final void setChunkGroup(int chunkGroup)
  {
    this.chunkGroup = chunkGroup;
  }
  
  public boolean hasPriority() {
    return priority;
  }
  
  public void setPriority(boolean priority) {
    this.priority = priority;
  }
  
  final void write(OutputStream os) {
    if ((raw == null) || (raw.data == null))
      raw = createRawChunk();
    if (raw == null)
      throw new PngjExceptionInternal("null chunk ! creation failed for " + this);
    raw.writeChunk(os);
  }
  




  public abstract ChunkRaw createRawChunk();
  




  protected abstract void parseFromRaw(ChunkRaw paramChunkRaw);
  



  protected abstract boolean allowsMultiple();
  



  public ChunkRaw getRaw()
  {
    return raw;
  }
  
  void setRaw(ChunkRaw raw) {
    this.raw = raw;
  }
  


  public int getLen()
  {
    return raw != null ? raw.len : -1;
  }
  


  public long getOffset()
  {
    return raw != null ? raw.getOffset() : -1L;
  }
  



  public void invalidateRawData()
  {
    raw = null;
  }
  


  public abstract ChunkOrderingConstraint getOrderingConstraint();
  

  public String toString()
  {
    return "chunk id= " + id + " (len=" + getLen() + " offset=" + getOffset() + ")";
  }
}
