package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjException;






public class PngChunkFDAT
  extends PngChunkMultiple
{
  public static final String ID = "fdAT";
  private int seqNum;
  private byte[] buffer;
  int datalen;
  
  public PngChunkFDAT(ImageInfo info)
  {
    super("fdAT", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.AFTER_IDAT;
  }
  
  public ChunkRaw createRawChunk()
  {
    if (buffer == null)
      throw new PngjException("not buffered");
    ChunkRaw c = createEmptyChunk(datalen + 4, false);
    data = buffer;
    return c;
  }
  
  public void parseFromRaw(ChunkRaw chunk)
  {
    seqNum = PngHelperInternal.readInt4fromBytes(data, 0);
    datalen = (len - 4);
    buffer = data;
  }
  
  public int getSeqNum() {
    return seqNum;
  }
  
  public void setSeqNum(int seqNum) {
    this.seqNum = seqNum;
  }
  
  public byte[] getBuffer() {
    return buffer;
  }
  
  public void setBuffer(byte[] buffer) {
    this.buffer = buffer;
  }
  
  public int getDatalen() {
    return datalen;
  }
  
  public void setDatalen(int datalen) {
    this.datalen = datalen;
  }
}
