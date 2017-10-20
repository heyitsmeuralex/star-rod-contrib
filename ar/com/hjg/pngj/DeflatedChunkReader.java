package ar.com.hjg.pngj;

import ar.com.hjg.pngj.chunks.ChunkRaw;





public class DeflatedChunkReader
  extends ChunkReader
{
  protected final DeflatedChunksSet deflatedChunksSet;
  protected boolean alsoBuffer = false;
  
  protected boolean skipBytes = false;
  protected byte[] skippedBytes;
  protected int seqNumExpected = -1;
  
  public DeflatedChunkReader(int clen, String chunkid, boolean checkCrc, long offsetInPng, DeflatedChunksSet iDatSet)
  {
    super(clen, chunkid, offsetInPng, ChunkReader.ChunkReaderMode.PROCESS);
    deflatedChunksSet = iDatSet;
    if (chunkid.equals("fdAT")) {
      skipBytes = true;
      skippedBytes = new byte[4];
    }
    iDatSet.appendNewChunk(this);
  }
  



  protected void processData(int offsetInchunk, byte[] buf, int off, int len)
  {
    if ((skipBytes) && (offsetInchunk < 4))
      for (int oc = offsetInchunk; (oc < 4) && (len > 0); len--) {
        skippedBytes[oc] = buf[off];oc++;off++;
      }
    if (len > 0) {
      deflatedChunksSet.processBytes(buf, off, len);
      if (alsoBuffer) {
        System.arraycopy(buf, off, getChunkRawdata, read, len);
      }
    }
  }
  



  protected void chunkDone()
  {
    if ((skipBytes) && (getChunkRawid.equals("fdAT")) && 
      (seqNumExpected >= 0)) {
      int seqNum = PngHelperInternal.readInt4fromBytes(skippedBytes, 0);
      if (seqNum != seqNumExpected) {
        throw new PngjInputException("bad chunk sequence for fDAT chunk " + seqNum + " expected " + seqNumExpected);
      }
    }
  }
  

  public boolean isFromDeflatedSet()
  {
    return true;
  }
  


  public void setAlsoBuffer()
  {
    if (read > 0)
      throw new RuntimeException("too late");
    alsoBuffer = true;
    getChunkRaw().allocData();
  }
  
  public void setSeqNumExpected(int seqNumExpected)
  {
    this.seqNumExpected = seqNumExpected;
  }
}
