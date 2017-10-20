package ar.com.hjg.pngj;

import ar.com.hjg.pngj.chunks.ChunkRaw;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

































public class DeflatedChunksSet
{
  protected byte[] row;
  private int rowfilled;
  private int rowlen;
  private int rown;
  
  private static enum State
  {
    WAITING_FOR_INPUT, 
    ROW_READY, 
    
    WORK_DONE, 
    
    TERMINATED;
    
    private State() {}
    public boolean isDone() { return (this == WORK_DONE) || (this == TERMINATED); }
    
    public boolean isTerminated()
    {
      return this == TERMINATED;
    }
  }
  
  State state = State.WAITING_FOR_INPUT;
  
  private Inflater inf;
  
  private final boolean infOwn;
  
  private DeflatedChunkReader curChunk;
  private boolean callbackMode = true;
  private long nBytesIn = 0L;
  private long nBytesOut = 0L;
  int chunkNum = -1;
  int firstChunqSeqNum = -1;
  




  public final String chunkid;
  




  public DeflatedChunksSet(String chunkid, int initialRowLen, int maxRowLen, Inflater inflater, byte[] buffer)
  {
    this.chunkid = chunkid;
    rowlen = initialRowLen;
    if ((initialRowLen < 1) || (maxRowLen < initialRowLen))
      throw new PngjException("bad inital row len " + initialRowLen);
    if (inflater != null) {
      inf = inflater;
      infOwn = false;
    } else {
      inf = new Inflater();
      infOwn = true;
    }
    row = ((buffer != null) && (buffer.length >= initialRowLen) ? buffer : new byte[maxRowLen]);
    rown = -1;
    state = State.WAITING_FOR_INPUT;
    try {
      prepareForNextRow(initialRowLen);
    } catch (RuntimeException e) {
      close();
      throw e;
    }
  }
  
  public DeflatedChunksSet(String chunkid, int initialRowLen, int maxRowLen) {
    this(chunkid, initialRowLen, maxRowLen, null, null);
  }
  
  protected void appendNewChunk(DeflatedChunkReader cr)
  {
    if (!chunkid.equals(getChunkRawid)) {
      throw new PngjInputException("Bad chunk inside IdatSet, id:" + getChunkRawid + ", expected:" + chunkid);
    }
    curChunk = cr;
    chunkNum += 1;
    if (firstChunqSeqNum >= 0) {
      cr.setSeqNumExpected(chunkNum + firstChunqSeqNum);
    }
  }
  








  protected void processBytes(byte[] buf, int off, int len)
  {
    nBytesIn += len;
    
    if ((len < 1) || (state.isDone()))
      return;
    if (state == State.ROW_READY)
      throw new PngjInputException("this should only be called if waitingForMoreInput");
    if ((inf.needsDictionary()) || (!inf.needsInput()))
      throw new RuntimeException("should not happen");
    inf.setInput(buf, off, len);
    

    if (isCallbackMode()) {
      while (inflateData()) {
        int nextRowLen = processRowCallback();
        prepareForNextRow(nextRowLen);
        if (isDone())
          processDoneCallback();
      }
    }
    inflateData();
  }
  





  private boolean inflateData()
  {
    try
    {
      if (state == State.ROW_READY)
        throw new PngjException("invalid state");
      if (state.isDone())
        return false;
      int ninflated = 0;
      if ((row == null) || (row.length < rowlen))
        row = new byte[rowlen];
      if ((rowfilled < rowlen) && (!inf.finished())) {
        try {
          ninflated = inf.inflate(row, rowfilled, rowlen - rowfilled);
        } catch (DataFormatException e) {
          throw new PngjInputException("error decompressing zlib stream ", e);
        }
        rowfilled += ninflated;
        nBytesOut += ninflated;
      }
      State nextstate = null;
      if (rowfilled == rowlen) {
        nextstate = State.ROW_READY;
      } else if (!inf.finished()) {
        nextstate = State.WAITING_FOR_INPUT;
      } else if (rowfilled > 0) {
        nextstate = State.ROW_READY;
      } else {
        nextstate = State.WORK_DONE;
      }
      state = nextstate;
      if (state == State.ROW_READY) {
        preProcessRow();
        return true;
      }
    } catch (RuntimeException e) {
      close();
      throw e;
    }
    return false;
  }
  






  protected void preProcessRow() {}
  





  protected int processRowCallback()
  {
    throw new PngjInputException("not implemented");
  }
  





  protected void processDoneCallback() {}
  




  public byte[] getInflatedRow()
  {
    return row;
  }
  








  public void prepareForNextRow(int len)
  {
    rowfilled = 0;
    rown += 1;
    if (len < 1) {
      rowlen = 0;
      done();
    } else if (inf.finished()) {
      rowlen = 0;
      done();
    } else {
      state = State.WAITING_FOR_INPUT;
      rowlen = len;
      if (!callbackMode) {
        inflateData();
      }
    }
  }
  



  public boolean isWaitingForMoreInput()
  {
    return state == State.WAITING_FOR_INPUT;
  }
  




  public boolean isRowReady()
  {
    return state == State.ROW_READY;
  }
  





  public boolean isDone()
  {
    return state.isDone();
  }
  
  public boolean isTerminated() {
    return state.isTerminated();
  }
  



  public boolean ackNextChunkId(String id)
  {
    if (state.isTerminated())
      return false;
    if (id.equals(chunkid)) {
      return true;
    }
    if (!allowOtherChunksInBetween(id)) {
      if (state.isDone()) {
        if (!isTerminated())
          terminate();
        return false;
      }
      throw new PngjInputException("Unexpected chunk " + id + " while " + chunkid + " set is not done");
    }
    

    return true;
  }
  
  protected void terminate()
  {
    close();
  }
  


  public void close()
  {
    try
    {
      if (!state.isTerminated()) {
        state = State.TERMINATED;
      }
      if ((infOwn) && (inf != null)) {
        inf.end();
        inf = null;
      }
    }
    catch (Exception e) {}
  }
  



  public void done()
  {
    if (!isDone()) {
      state = State.WORK_DONE;
    }
  }
  


  public int getRowLen()
  {
    return rowlen;
  }
  
  public int getRowFilled()
  {
    return rowfilled;
  }
  






  public int getRown()
  {
    return rown;
  }
  







  public boolean allowOtherChunksInBetween(String id)
  {
    return false;
  }
  


  public boolean isCallbackMode()
  {
    return callbackMode;
  }
  
  public void setCallbackMode(boolean callbackMode) {
    this.callbackMode = callbackMode;
  }
  
  public long getBytesIn()
  {
    return nBytesIn;
  }
  
  public long getBytesOut()
  {
    return nBytesOut;
  }
  
  public String toString()
  {
    StringBuilder sb = new StringBuilder("idatSet : " + curChunk.getChunkRaw().id + " state=" + state + " rows=" + rown + " bytes=" + nBytesIn + "/" + nBytesOut);
    

    return sb.toString();
  }
}
