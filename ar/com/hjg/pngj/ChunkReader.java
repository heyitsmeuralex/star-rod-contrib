package ar.com.hjg.pngj;

import ar.com.hjg.pngj.chunks.ChunkRaw;



















public abstract class ChunkReader
{
  public final ChunkReaderMode mode;
  private final ChunkRaw chunkRaw;
  private boolean crcCheck;
  protected int read = 0;
  private int crcn = 0;
  





  public static enum ChunkReaderMode
  {
    BUFFER, 
    


    PROCESS, 
    


    SKIP;
    



    private ChunkReaderMode() {}
  }
  


  public ChunkReader(int clen, String id, long offsetInPng, ChunkReaderMode mode)
  {
    if ((mode == null) || (id.length() != 4) || (clen < 0))
      throw new PngjExceptionInternal("Bad chunk paramenters: " + mode);
    this.mode = mode;
    chunkRaw = new ChunkRaw(clen, id, mode == ChunkReaderMode.BUFFER);
    chunkRaw.setOffset(offsetInPng);
    crcCheck = (mode != ChunkReaderMode.SKIP);
  }
  




  public ChunkRaw getChunkRaw()
  {
    return chunkRaw;
  }
  











  public final int feedBytes(byte[] buf, int off, int len)
  {
    if (len == 0)
      return 0;
    if (len < 0)
      throw new PngjException("negative length??");
    if ((read == 0) && (crcn == 0) && (crcCheck))
      chunkRaw.updateCrc(chunkRaw.idbytes, 0, 4);
    int bytesForData = chunkRaw.len - read;
    if (bytesForData > len) {
      bytesForData = len;
    }
    if ((bytesForData > 0) || (crcn == 0))
    {
      if ((crcCheck) && (mode != ChunkReaderMode.BUFFER) && (bytesForData > 0)) {
        chunkRaw.updateCrc(buf, off, bytesForData);
      }
      if (mode == ChunkReaderMode.BUFFER)
      {
        if ((chunkRaw.data != buf) && (bytesForData > 0))
        {
          System.arraycopy(buf, off, chunkRaw.data, read, bytesForData);
        }
      } else if (mode == ChunkReaderMode.PROCESS) {
        processData(read, buf, off, bytesForData);
      }
      

      read += bytesForData;
      off += bytesForData;
      len -= bytesForData;
    }
    int crcRead = 0;
    if (read == chunkRaw.len) {
      crcRead = 4 - crcn;
      if (crcRead > len)
        crcRead = len;
      if (crcRead > 0) {
        if (buf != chunkRaw.crcval)
          System.arraycopy(buf, off, chunkRaw.crcval, crcn, crcRead);
        crcn += crcRead;
        if (crcn == 4) {
          if (crcCheck) {
            if (mode == ChunkReaderMode.BUFFER) {
              chunkRaw.updateCrc(chunkRaw.data, 0, chunkRaw.len);
            }
            chunkRaw.checkCrc();
          }
          chunkDone();
        }
      }
    }
    return bytesForData + crcRead;
  }
  




  public final boolean isDone()
  {
    return crcn == 4;
  }
  




  public void setCrcCheck(boolean crcCheck)
  {
    if ((read != 0) && (crcCheck) && (!this.crcCheck))
      throw new PngjException("too late!");
    this.crcCheck = crcCheck;
  }
  





  protected abstract void processData(int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3);
  




  protected abstract void chunkDone();
  




  public boolean isFromDeflatedSet()
  {
    return false;
  }
  
  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (chunkRaw == null ? 0 : chunkRaw.hashCode());
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
    ChunkReader other = (ChunkReader)obj;
    if (chunkRaw == null) {
      if (chunkRaw != null)
        return false;
    } else if (!chunkRaw.equals(chunkRaw))
      return false;
    return true;
  }
  
  public String toString()
  {
    return chunkRaw.toString();
  }
}
