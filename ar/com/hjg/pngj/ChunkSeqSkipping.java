package ar.com.hjg.pngj;

import ar.com.hjg.pngj.chunks.ChunkRaw;
import java.util.ArrayList;
import java.util.List;







public class ChunkSeqSkipping
  extends ChunkSeqReader
{
  private List<ChunkRaw> chunks = new ArrayList();
  private boolean skip = true;
  


  public ChunkSeqSkipping(boolean skipAll)
  {
    super(true);
    skip = skipAll;
  }
  
  public ChunkSeqSkipping() {
    this(true);
  }
  
  protected ChunkReader createChunkReaderForNewChunk(String id, int len, long offset, boolean skip) {
    new ChunkReader(len, id, offset, skip ? ChunkReader.ChunkReaderMode.SKIP : ChunkReader.ChunkReaderMode.PROCESS)
    {
      protected void chunkDone() {
        postProcessChunk(this);
      }
      
      protected void processData(int offsetinChhunk, byte[] buf, int off, int len)
      {
        processChunkContent(getChunkRaw(), offsetinChhunk, buf, off, len);
      }
    };
  }
  


  protected void processChunkContent(ChunkRaw chunkRaw, int offsetinChhunk, byte[] buf, int off, int len) {}
  

  protected void postProcessChunk(ChunkReader chunkR)
  {
    super.postProcessChunk(chunkR);
    chunks.add(chunkR.getChunkRaw());
  }
  
  protected boolean shouldSkipContent(int len, String id)
  {
    return skip;
  }
  
  protected boolean isIdatKind(String id)
  {
    return false;
  }
  
  public List<ChunkRaw> getChunks() {
    return chunks;
  }
}
