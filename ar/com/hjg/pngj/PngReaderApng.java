package ar.com.hjg.pngj;

import ar.com.hjg.pngj.chunks.ChunkRaw;
import ar.com.hjg.pngj.chunks.ChunksList;
import ar.com.hjg.pngj.chunks.PngChunk;
import ar.com.hjg.pngj.chunks.PngChunkACTL;
import ar.com.hjg.pngj.chunks.PngChunkFCTL;
import java.io.File;
import java.io.InputStream;
import java.util.List;


public class PngReaderApng
  extends PngReaderByte
{
  public PngReaderApng(File file)
  {
    super(file);
    dontSkipChunk("fcTL");
  }
  
  public PngReaderApng(InputStream inputStream) {
    super(inputStream);
    dontSkipChunk("fcTL");
  }
  
  private Boolean apngKind = null;
  private boolean firsIdatApngFrame = false;
  

  protected PngChunkACTL actlChunk;
  

  private PngChunkFCTL fctlChunk;
  
  protected int frameNum = -1;
  
  public boolean isApng() {
    if (apngKind == null)
    {
      actlChunk = ((PngChunkACTL)getChunksList().getById1("acTL"));
      apngKind = Boolean.valueOf(actlChunk != null);
      firsIdatApngFrame = (fctlChunk != null);
    }
    
    return apngKind.booleanValue();
  }
  
  public void advanceToFrame(int frame)
  {
    if (frame < frameNum)
      throw new PngjInputException("Cannot go backwards");
    if (frame >= getApngNumFrames())
      throw new PngjInputException("Frame out of range " + frame);
    if (frame > frameNum) {
      addChunkToSkip("IDAT");
      addChunkToSkip("fdAT");
      while (((frameNum < frame ? 1 : 0) & (!chunkseq.isDone() ? 1 : 0)) != 0)
        if (streamFeeder.feed(chunkseq) <= 0)
          break;
    }
    if (frame == frameNum) {
      dontSkipChunk("IDAT");
      dontSkipChunk("fdAT");
      rowNum = -1;
      imlinesSet = null;
      do {
        if ((chunkseq.isDone()) || (chunkseq.getCurChunkReader().isFromDeflatedSet())) break;
      } while (streamFeeder.feed(chunkseq) > 0);
    }
    else {
      throw new PngjInputException("unexpected error seeking from frame " + frame);
    }
  }
  



  public boolean hasExtraStillImage()
  {
    return (isApng()) && (!firsIdatApngFrame);
  }
  


  public int getApngNumFrames()
  {
    if (isApng()) {
      return actlChunk.getNumFrames();
    }
    return 0;
  }
  


  public int getApngNumPlays()
  {
    if (isApng()) {
      return actlChunk.getNumPlays();
    }
    return -1;
  }
  

  public IImageLine readRow()
  {
    return super.readRow();
  }
  

  public boolean hasMoreRows()
  {
    return super.hasMoreRows();
  }
  

  public IImageLine readRow(int nrow)
  {
    return super.readRow(nrow);
  }
  

  public IImageLineSet<? extends IImageLine> readRows()
  {
    return super.readRows();
  }
  

  public IImageLineSet<? extends IImageLine> readRows(int nRows, int rowOffset, int rowStep)
  {
    return super.readRows(nRows, rowOffset, rowStep);
  }
  

  public void readSkippingAllRows()
  {
    super.readSkippingAllRows();
  }
  
  protected ChunkSeqReaderPng createChunkSeqReader()
  {
    ChunkSeqReaderPng cr = new ChunkSeqReaderPng(false)
    {
      public boolean shouldSkipContent(int len, String id)
      {
        return super.shouldSkipContent(len, id);
      }
      
      protected boolean isIdatKind(String id)
      {
        return (id.equals("IDAT")) || (id.equals("fdAT"));
      }
      
      protected DeflatedChunksSet createIdatSet(String id)
      {
        IdatSet ids = new IdatSet(id, getCurImgInfo(), deinterlacer);
        ids.setCallbackMode(callbackMode);
        return ids;
      }
      

      protected void startNewChunk(int len, String id, long offset)
      {
        super.startNewChunk(len, id, offset);
      }
      
      protected void postProcessChunk(ChunkReader chunkR)
      {
        super.postProcessChunk(chunkR);
        if (getChunkRawid.equals("fcTL")) {
          frameNum += 1;
          List<PngChunk> chunkslist = chunkseq.getChunks();
          fctlChunk = ((PngChunkFCTL)chunkslist.get(chunkslist.size() - 1));
          
          if (chunkR.getChunkRaw().getOffset() != fctlChunk.getRaw().getOffset())
            throw new PngjInputException("something went wrong");
          ImageInfo frameInfo = fctlChunk.getEquivImageInfo();
          getChunkseq().updateCurImgInfo(frameInfo);
        }
      }
      

      protected boolean countChunkTypeAsAncillary(String id)
      {
        return (super.countChunkTypeAsAncillary(id)) && (!id.equals(Boolean.valueOf(id.equals("fdAT"))));
      }
      
    };
    return cr;
  }
  


  public int getFrameNum()
  {
    return frameNum;
  }
  

  public void end()
  {
    super.end();
  }
  
  public PngChunkFCTL getFctl() {
    return fctlChunk;
  }
}
