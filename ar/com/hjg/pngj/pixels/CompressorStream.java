package ar.com.hjg.pngj.pixels;

import ar.com.hjg.pngj.IDatChunkWriter;
import java.io.OutputStream;



















public abstract class CompressorStream
  extends OutputStream
{
  protected IDatChunkWriter idatChunkWriter;
  public final int blockLen;
  public final long totalbytes;
  boolean closed = false;
  protected boolean done = false;
  protected long bytesIn = 0L;
  protected long bytesOut = 0L;
  protected int block = -1;
  
  private byte[] firstBytes;
  
  protected boolean storeFirstByte = false;
  





  public CompressorStream(IDatChunkWriter idatCw, int blockLen, long totalbytes)
  {
    idatChunkWriter = idatCw;
    if (blockLen < 0)
      blockLen = 4096;
    if (totalbytes < 0L)
      totalbytes = Long.MAX_VALUE;
    if ((blockLen < 1) || (totalbytes < 1L))
      throw new RuntimeException(" maxBlockLen or totalLen invalid");
    this.blockLen = blockLen;
    this.totalbytes = totalbytes;
  }
  

  public void close()
  {
    done();
    if (idatChunkWriter != null) idatChunkWriter.close();
    closed = true;
  }
  


  public abstract void done();
  


  public final void write(byte[] data)
  {
    write(data, 0, data.length);
  }
  
  public final void write(byte[] data, int off, int len)
  {
    block += 1;
    if (len <= blockLen) {
      mywrite(data, off, len);
      if ((storeFirstByte) && (block < firstBytes.length)) {
        firstBytes[block] = data[off];
      }
    } else {
      while (len > 0) {
        mywrite(data, off, blockLen);
        off += blockLen;
        len -= blockLen;
      }
    }
    if (bytesIn >= totalbytes) {
      done();
    }
  }
  




  public abstract void mywrite(byte[] paramArrayOfByte, int paramInt1, int paramInt2);
  



  public final double getCompressionRatio()
  {
    return bytesOut == 0L ? 1.0D : bytesOut / bytesIn;
  }
  


  public final long getBytesRaw()
  {
    return bytesIn;
  }
  


  public final long getBytesCompressed()
  {
    return bytesOut;
  }
  
  public boolean isClosed() {
    return closed;
  }
  
  public boolean isDone() {
    return done;
  }
  
  public byte[] getFirstBytes() {
    return firstBytes;
  }
  
  public void setStoreFirstByte(boolean storeFirstByte, int nblocks) {
    this.storeFirstByte = storeFirstByte;
    if (this.storeFirstByte) {
      if ((firstBytes == null) || (firstBytes.length < nblocks))
        firstBytes = new byte[nblocks];
    } else
      firstBytes = null;
  }
  
  public void reset() {
    done();
    bytesIn = 0L;
    bytesOut = 0L;
    block = -1;
    done = false;
  }
  
  public void write(int i)
  {
    write(new byte[] { (byte)i });
  }
}
