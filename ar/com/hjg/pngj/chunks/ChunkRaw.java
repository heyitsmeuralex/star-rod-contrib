package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjBadCrcException;
import ar.com.hjg.pngj.PngjException;
import ar.com.hjg.pngj.PngjOutputException;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.util.zip.CRC32;




















public class ChunkRaw
{
  public final int len;
  public final byte[] idbytes;
  public final String id;
  public byte[] data = null;
  


  private long offset = 0L;
  




  public byte[] crcval = new byte[4];
  private CRC32 crcengine;
  
  public ChunkRaw(int len, String id, boolean alloc)
  {
    this.len = len;
    this.id = id;
    idbytes = ChunkHelper.toBytes(id);
    for (int i = 0; i < 4; i++) {
      if ((idbytes[i] < 65) || (idbytes[i] > 122) || ((idbytes[i] > 90) && (idbytes[i] < 97)))
        throw new PngjException("Bad id chunk: must be ascii letters " + id);
    }
    if (alloc)
      allocData();
  }
  
  public ChunkRaw(int len, byte[] idbytes, boolean alloc) {
    this(len, ChunkHelper.toString(idbytes), alloc);
  }
  
  public void allocData() {
    if ((data == null) || (data.length < len)) {
      data = new byte[len];
    }
  }
  

  private void computeCrcForWriting()
  {
    crcengine = new CRC32();
    crcengine.update(idbytes, 0, 4);
    if (len > 0)
      crcengine.update(data, 0, len);
    PngHelperInternal.writeInt4tobytes((int)crcengine.getValue(), crcval, 0);
  }
  




  public void writeChunk(OutputStream os)
  {
    writeChunkHeader(os);
    if (len > 0) {
      if (data == null)
        throw new PngjOutputException("cannot write chunk, raw chunk data is null [" + id + "]");
      PngHelperInternal.writeBytes(os, data, 0, len);
    }
    computeCrcForWriting();
    writeChunkCrc(os);
  }
  
  public void writeChunkHeader(OutputStream os) {
    if (idbytes.length != 4)
      throw new PngjOutputException("bad chunkid [" + id + "]");
    PngHelperInternal.writeInt4(os, len);
    PngHelperInternal.writeBytes(os, idbytes);
  }
  
  public void writeChunkCrc(OutputStream os) {
    PngHelperInternal.writeBytes(os, crcval, 0, 4);
  }
  
  public void checkCrc() {
    int crcComputed = (int)crcengine.getValue();
    int crcExpected = PngHelperInternal.readInt4fromBytes(crcval, 0);
    if (crcComputed != crcExpected) {
      throw new PngjBadCrcException("chunk: " + toString() + " expected=" + crcExpected + " read=" + crcComputed);
    }
  }
  
  public void updateCrc(byte[] buf, int off, int len) {
    if (crcengine == null)
      crcengine = new CRC32();
    crcengine.update(buf, off, len);
  }
  
  ByteArrayInputStream getAsByteStream() {
    return new ByteArrayInputStream(data);
  }
  


  public long getOffset()
  {
    return offset;
  }
  
  public void setOffset(long offset) {
    this.offset = offset;
  }
  
  public String toString() {
    return "chunkid=" + ChunkHelper.toString(idbytes) + " len=" + len;
  }
  
  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (id == null ? 0 : id.hashCode());
    result = 31 * result + (int)(offset ^ offset >>> 32);
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
    ChunkRaw other = (ChunkRaw)obj;
    if (id == null) {
      if (id != null)
        return false;
    } else if (!id.equals(id))
      return false;
    if (offset != offset)
      return false;
    return true;
  }
}
