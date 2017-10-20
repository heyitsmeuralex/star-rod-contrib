package ar.com.hjg.pngj;

import ar.com.hjg.pngj.chunks.ChunkHelper;
import ar.com.hjg.pngj.chunks.ChunkRaw;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;








public class ChunkSeqReader
  implements IBytesConsumer
{
  protected static final int SIGNATURE_LEN = 8;
  protected final boolean withSignature;
  private byte[] buf0 = new byte[8];
  private int buf0len = 0;
  
  private boolean signatureDone = false;
  private boolean done = false;
  
  private int chunkCount = 0;
  
  private long bytesCount = 0L;
  

  private DeflatedChunksSet curReaderDeflatedSet;
  

  private ChunkReader curChunkReader;
  
  private long idatBytes;
  

  public ChunkSeqReader()
  {
    this(true);
  }
  


  public ChunkSeqReader(boolean withSignature)
  {
    this.withSignature = withSignature;
    signatureDone = (!withSignature);
  }
  

















  public int consume(byte[] buffer, int offset, int len)
  {
    if (done)
      return -1;
    if (len == 0)
      return 0;
    if (len < 0)
      throw new PngjInputException("Bad len: " + len);
    int processed = 0;
    if (signatureDone) {
      if ((curChunkReader == null) || (curChunkReader.isDone())) {
        int read0 = 8 - buf0len;
        if (read0 > len)
          read0 = len;
        System.arraycopy(buffer, offset, buf0, buf0len, read0);
        buf0len += read0;
        processed += read0;
        bytesCount += read0;
        

        if (buf0len == 8) {
          chunkCount += 1;
          int clen = PngHelperInternal.readInt4fromBytes(buf0, 0);
          String cid = ChunkHelper.toString(buf0, 4, 4);
          startNewChunk(clen, cid, bytesCount - 8L);
          buf0len = 0;
        }
      } else {
        int read1 = curChunkReader.feedBytes(buffer, offset, len);
        processed += read1;
        bytesCount += read1;
      }
    } else {
      int read = 8 - buf0len;
      if (read > len)
        read = len;
      System.arraycopy(buffer, offset, buf0, buf0len, read);
      buf0len += read;
      if (buf0len == 8) {
        checkSignature(buf0);
        buf0len = 0;
        signatureDone = true;
      }
      processed += read;
      bytesCount += read;
    }
    return processed;
  }
  






  public boolean feedAll(byte[] buf, int off, int len)
  {
    while (len > 0) {
      int n = consume(buf, off, len);
      if (n < 1)
        return false;
      len -= n;
      off += n;
    }
    return true;
  }
  












  protected void startNewChunk(int len, String id, long offset)
  {
    if (id.equals("IDAT"))
      idatBytes += len;
    boolean checkCrc = shouldCheckCrc(len, id);
    boolean skip = shouldSkipContent(len, id);
    boolean isIdatType = isIdatKind(id);
    


    boolean forCurrentIdatSet = false;
    if (curReaderDeflatedSet != null)
      forCurrentIdatSet = curReaderDeflatedSet.ackNextChunkId(id);
    if ((isIdatType) && (!skip)) {
      if (!forCurrentIdatSet) {
        if ((curReaderDeflatedSet != null) && (!curReaderDeflatedSet.isDone()))
          throw new PngjInputException("new IDAT-like chunk when previous was not done");
        curReaderDeflatedSet = createIdatSet(id);
      }
      curChunkReader = new DeflatedChunkReader(len, id, checkCrc, offset, curReaderDeflatedSet)
      {
        protected void chunkDone() {
          super.chunkDone();
          postProcessChunk(this);
        }
      };
    }
    else {
      curChunkReader = createChunkReaderForNewChunk(id, len, offset, skip);
      if (!checkCrc) {
        curChunkReader.setCrcCheck(false);
      }
    }
  }
  










  protected ChunkReader createChunkReaderForNewChunk(String id, int len, long offset, boolean skip)
  {
    new ChunkReader(len, id, offset, skip ? ChunkReader.ChunkReaderMode.SKIP : ChunkReader.ChunkReaderMode.BUFFER)
    {
      protected void chunkDone() {
        postProcessChunk(this);
      }
      
      protected void processData(int offsetinChhunk, byte[] buf, int off, int len)
      {
        throw new PngjExceptionInternal("should never happen");
      }
    };
  }
  






  protected void postProcessChunk(ChunkReader chunkR)
  {
    if (chunkCount == 1) {
      String cid = firstChunkId();
      if ((cid != null) && (!cid.equals(getChunkRawid))) {
        throw new PngjInputException("Bad first chunk: " + getChunkRawid + " expected: " + firstChunkId());
      }
    }
    if (getChunkRawid.equals(endChunkId())) {
      done = true;
    }
  }
  

  protected DeflatedChunksSet createIdatSet(String id)
  {
    return new DeflatedChunksSet(id, 1024, 1024);
  }
  








  protected boolean isIdatKind(String id)
  {
    return false;
  }
  






  protected boolean shouldSkipContent(int len, String id)
  {
    return false;
  }
  
  protected boolean shouldCheckCrc(int len, String id) {
    return true;
  }
  




  protected void checkSignature(byte[] buf)
  {
    if (!Arrays.equals(buf, PngHelperInternal.getPngIdSignature())) {
      throw new PngjInputException("Bad PNG signature");
    }
  }
  



  public boolean isSignatureDone()
  {
    return signatureDone;
  }
  


  public boolean isDone()
  {
    return done;
  }
  


  public long getBytesCount()
  {
    return bytesCount;
  }
  


  public int getChunkCount()
  {
    return chunkCount;
  }
  




  public ChunkReader getCurChunkReader()
  {
    return curChunkReader;
  }
  


  public DeflatedChunksSet getCurReaderDeflatedSet()
  {
    return curReaderDeflatedSet;
  }
  


  public void close()
  {
    if (curReaderDeflatedSet != null)
      curReaderDeflatedSet.close();
    done = true;
  }
  



  public boolean isAtChunkBoundary()
  {
    return (bytesCount == 0L) || (bytesCount == 8L) || (done) || (curChunkReader == null) || (curChunkReader.isDone());
  }
  





  protected String firstChunkId()
  {
    return "IHDR";
  }
  




  public long getIdatBytes()
  {
    return idatBytes;
  }
  




  protected String endChunkId()
  {
    return "IEND";
  }
  

  public void feedFromFile(File f)
  {
    try
    {
      feedFromInputStream(new FileInputStream(f), true);
    } catch (FileNotFoundException e) {
      throw new PngjInputException(e.getMessage());
    }
  }
  





  public void feedFromInputStream(InputStream is, boolean closeStream)
  {
    BufferedStreamFeeder sf = new BufferedStreamFeeder(is);
    sf.setCloseStream(closeStream);
    try {
      sf.feedAll(this);
    } finally {
      close();
      sf.close();
    }
  }
  
  public void feedFromInputStream(InputStream is) {
    feedFromInputStream(is, true);
  }
}
