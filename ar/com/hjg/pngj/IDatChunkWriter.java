package ar.com.hjg.pngj;

import ar.com.hjg.pngj.chunks.ChunkHelper;
import ar.com.hjg.pngj.chunks.ChunkRaw;
import java.io.OutputStream;








public class IDatChunkWriter
{
  private static final int MAX_LEN_DEFAULT = 32768;
  private final OutputStream outputStream;
  private final int maxChunkLen;
  private byte[] buf;
  private int offset = 0;
  private int availLen;
  private long totalBytesWriten = 0L;
  private int chunksWriten = 0;
  
  public IDatChunkWriter(OutputStream outputStream) {
    this(outputStream, 0);
  }
  
  public IDatChunkWriter(OutputStream outputStream, int maxChunkLength) {
    this.outputStream = outputStream;
    maxChunkLen = (maxChunkLength > 0 ? maxChunkLength : 32768);
    buf = new byte[maxChunkLen];
    availLen = (maxChunkLen - offset);
    postReset();
  }
  
  public IDatChunkWriter(OutputStream outputStream, byte[] b) {
    this.outputStream = outputStream;
    buf = (b != null ? b : new byte[32768]);
    maxChunkLen = b.length;
    availLen = (maxChunkLen - offset);
    postReset();
  }
  
  protected byte[] getChunkId() {
    return ChunkHelper.b_IDAT;
  }
  




  public final void flush()
  {
    if ((offset > 0) && (offset >= minLenToWrite())) {
      ChunkRaw c = new ChunkRaw(offset, getChunkId(), false);
      data = buf;
      c.writeChunk(outputStream);
      totalBytesWriten += len + 12;
      chunksWriten += 1;
      offset = 0;
      availLen = maxChunkLen;
      postReset();
    }
  }
  
  public int getOffset() {
    return offset;
  }
  
  public int getAvailLen() {
    return availLen;
  }
  
  public void incrementOffset(int n)
  {
    offset += n;
    availLen -= n;
    if (availLen < 0)
      throw new PngjOutputException("Anomalous situation");
    if (availLen == 0) {
      flush();
    }
  }
  


  public void write(byte[] b, int o, int len)
  {
    while (len > 0) {
      int n = len <= availLen ? len : availLen;
      System.arraycopy(b, o, buf, offset, n);
      incrementOffset(n);
      len -= n;
      o += n;
    }
  }
  

  protected void postReset() {}
  

  protected int minLenToWrite()
  {
    return 1;
  }
  
  public void close() {
    flush();
    offset = 0;
    buf = null;
  }
  



  public byte[] getBuf()
  {
    return buf;
  }
  
  public long getTotalBytesWriten() {
    return totalBytesWriten;
  }
  
  public int getChunksWriten() {
    return chunksWriten;
  }
}
