package ar.com.hjg.pngj;

import ar.com.hjg.pngj.chunks.ChunkLoadBehaviour;
import ar.com.hjg.pngj.chunks.ChunksList;
import ar.com.hjg.pngj.chunks.PngMetadata;
import java.io.File;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;



































































public class PngReader
{
  public static final long MAX_TOTAL_BYTES_READ_DEFAULT = 901001001L;
  public static final long MAX_BYTES_METADATA_DEFAULT = 5024024L;
  public static final long MAX_CHUNK_SIZE_SKIP = 2024024L;
  public final ImageInfo imgInfo;
  public final boolean interlaced;
  protected final ChunkSeqReaderPng chunkseq;
  protected final BufferedStreamFeeder streamFeeder;
  protected final PngMetadata metadata;
  protected int rowNum = -1;
  




  protected IImageLineSet<? extends IImageLine> imlinesSet;
  




  private IImageLineSetFactory<? extends IImageLine> imageLineSetFactory;
  




  CRC32 idatCrca;
  



  Adler32 idatCrcb;
  




  public PngReader(InputStream inputStream)
  {
    this(inputStream, true);
  }
  






  public PngReader(InputStream inputStream, boolean shouldCloseStream)
  {
    streamFeeder = new BufferedStreamFeeder(inputStream);
    streamFeeder.setCloseStream(shouldCloseStream);
    chunkseq = createChunkSeqReader();
    try {
      streamFeeder.setFailIfNoFeed(true);
      if (!streamFeeder.feedFixed(chunkseq, 36))
        throw new PngjInputException("error reading first 21 bytes");
      imgInfo = chunkseq.getImageInfo();
      interlaced = (chunkseq.getDeinterlacer() != null);
      setMaxBytesMetadata(5024024L);
      setMaxTotalBytesRead(901001001L);
      setSkipChunkMaxSize(2024024L);
      chunkseq.addChunkToSkip("fdAT");
      chunkseq.addChunkToSkip("fcTL");
      metadata = new PngMetadata(chunkseq.chunksList);
      

      setLineSetFactory(ImageLineSetDefault.getFactoryInt());
      rowNum = -1;
    } catch (RuntimeException e) {
      streamFeeder.close();
      chunkseq.close();
      throw e;
    }
  }
  






  public PngReader(File file)
  {
    this(PngHelperInternal.istreamFromFile(file), true);
  }
  












  protected void readFirstChunks()
  {
    while (chunkseq.currentChunkGroup < 4) {
      if (streamFeeder.feed(chunkseq) <= 0) {
        throw new PngjInputException("premature ending reading first chunks");
      }
    }
  }
  





  public void setChunkLoadBehaviour(ChunkLoadBehaviour chunkLoadBehaviour)
  {
    chunkseq.setChunkLoadBehaviour(chunkLoadBehaviour);
  }
  









  public ChunksList getChunksList()
  {
    return getChunksList(true);
  }
  
  public ChunksList getChunksList(boolean forceLoadingOfFirstChunks) {
    if ((forceLoadingOfFirstChunks) && (chunkseq.firstChunksNotYetRead()))
      readFirstChunks();
    return chunkseq.chunksList;
  }
  
  int getCurrentChunkGroup() {
    return chunkseq.currentChunkGroup;
  }
  




  public PngMetadata getMetadata()
  {
    if (chunkseq.firstChunksNotYetRead())
      readFirstChunks();
    return metadata;
  }
  






  public IImageLine readRow()
  {
    return readRow(rowNum + 1);
  }
  


  public boolean hasMoreRows()
  {
    return rowNum < getCurImgInforows - 1;
  }
  


  public IImageLine readRow(int nrow)
  {
    if (chunkseq.firstChunksNotYetRead())
      readFirstChunks();
    if (!interlaced) {
      if (imlinesSet == null)
        imlinesSet = createLineSet(true, -1, 0, 1);
      IImageLine line = imlinesSet.getImageLine(nrow);
      if (nrow == rowNum)
        return line;
      if (nrow < rowNum)
        throw new PngjInputException("rows must be read in increasing order: " + nrow);
      while (rowNum < nrow) {
        while (!chunkseq.getIdatSet().isRowReady())
          if (streamFeeder.feed(chunkseq) < 1)
            throw new PngjInputException("premature ending");
        rowNum += 1;
        chunkseq.getIdatSet().updateCrcs(new Checksum[] { idatCrca, idatCrcb });
        if (rowNum == nrow) {
          line.readFromPngRaw(chunkseq.getIdatSet().getUnfilteredRow(), getCurImgInfobytesPerRow + 1, 0, 1);
          
          line.endReadFromPngRaw();
        }
        chunkseq.getIdatSet().advanceToNextRow();
      }
      return line;
    }
    if (imlinesSet == null) {
      imlinesSet = createLineSet(false, getCurImgInforows, 0, 1);
      loadAllInterlaced(getCurImgInforows, 0, 1);
    }
    rowNum = nrow;
    return imlinesSet.getImageLine(nrow);
  }
  




  public IImageLineSet<? extends IImageLine> readRows()
  {
    return readRows(getCurImgInforows, 0, 1);
  }
  








  public IImageLineSet<? extends IImageLine> readRows(int nRows, int rowOffset, int rowStep)
  {
    if (chunkseq.firstChunksNotYetRead())
      readFirstChunks();
    if (nRows < 0)
      nRows = (getCurImgInforows - rowOffset) / rowStep;
    if ((rowStep < 1) || (rowOffset < 0) || (nRows == 0) || (nRows * rowStep + rowOffset > getCurImgInforows))
    {
      throw new PngjInputException("bad args"); }
    if (rowNum >= rowOffset)
      throw new PngjInputException("readRows cannot be mixed with readRow");
    imlinesSet = createLineSet(false, nRows, rowOffset, rowStep);
    if (!interlaced) {
      int m = -1;
      while (m < nRows - 1) {
        while (!chunkseq.getIdatSet().isRowReady())
          if (streamFeeder.feed(chunkseq) < 1)
            throw new PngjInputException("Premature ending");
        rowNum += 1;
        chunkseq.getIdatSet().updateCrcs(new Checksum[] { idatCrca, idatCrcb });
        m = (rowNum - rowOffset) / rowStep;
        if ((rowNum >= rowOffset) && (rowStep * m + rowOffset == rowNum)) {
          IImageLine line = imlinesSet.getImageLine(rowNum);
          line.readFromPngRaw(chunkseq.getIdatSet().getUnfilteredRow(), getCurImgInfobytesPerRow + 1, 0, 1);
          
          line.endReadFromPngRaw();
        }
        chunkseq.getIdatSet().advanceToNextRow();
      }
    } else {
      loadAllInterlaced(nRows, rowOffset, rowStep);
    }
    chunkseq.getIdatSet().done();
    return imlinesSet;
  }
  







  public void setLineSetFactory(IImageLineSetFactory<? extends IImageLine> factory)
  {
    imageLineSetFactory = factory;
  }
  





  protected IImageLineSet<? extends IImageLine> createLineSet(boolean singleCursor, int nlines, int noffset, int step)
  {
    return imageLineSetFactory.create(getCurImgInfo(), singleCursor, nlines, noffset, step);
  }
  
  protected void loadAllInterlaced(int nRows, int rowOffset, int rowStep) {
    IdatSet idat = chunkseq.getIdatSet();
    int nread = 0;
    do {
      while (!chunkseq.getIdatSet().isRowReady())
        if (streamFeeder.feed(chunkseq) <= 0)
          break;
      if (!chunkseq.getIdatSet().isRowReady())
        throw new PngjInputException("Premature ending?");
      chunkseq.getIdatSet().updateCrcs(new Checksum[] { idatCrca, idatCrcb });
      int rowNumreal = rowinfo.rowNreal;
      boolean inset = imlinesSet.hasImageLine(rowNumreal);
      if (inset) {
        imlinesSet.getImageLine(rowNumreal).readFromPngRaw(idat.getUnfilteredRow(), rowinfo.buflen, rowinfo.oX, rowinfo.dX);
        
        nread++;
      }
      idat.advanceToNextRow();
    } while ((nread < nRows) || (!idat.isDone()));
    idat.done();
    int i = 0; for (int j = rowOffset; i < nRows; j += rowStep) {
      imlinesSet.getImageLine(j).endReadFromPngRaw();i++;
    }
  }
  




  public void readSkippingAllRows()
  {
    chunkseq.addChunkToSkip("IDAT");
    chunkseq.addChunkToSkip("fdAT");
    if (chunkseq.firstChunksNotYetRead())
      readFirstChunks();
    end();
  }
  



  public void setMaxTotalBytesRead(long maxTotalBytesToRead)
  {
    chunkseq.setMaxTotalBytesRead(maxTotalBytesToRead);
  }
  



  public void setMaxBytesMetadata(long maxBytesMetadata)
  {
    chunkseq.setMaxBytesMetadata(maxBytesMetadata);
  }
  




  public void setSkipChunkMaxSize(long skipChunkMaxSize)
  {
    chunkseq.setSkipChunkMaxSize(skipChunkMaxSize);
  }
  




  public void setChunksToSkip(String... chunksToSkip)
  {
    chunkseq.setChunksToSkip(chunksToSkip);
  }
  
  public void addChunkToSkip(String chunkToSkip) {
    chunkseq.addChunkToSkip(chunkToSkip);
  }
  
  public void dontSkipChunk(String chunkToSkip) {
    chunkseq.dontSkipChunk(chunkToSkip);
  }
  





  public void setShouldCloseStream(boolean shouldCloseStream)
  {
    streamFeeder.setCloseStream(shouldCloseStream);
  }
  









  public void end()
  {
    try
    {
      if (chunkseq.firstChunksNotYetRead())
        readFirstChunks();
      if ((chunkseq.getIdatSet() != null) && (!chunkseq.getIdatSet().isDone()))
        chunkseq.getIdatSet().done();
      while (!chunkseq.isDone())
        if (streamFeeder.feed(chunkseq) <= 0)
          break;
    } finally {
      close();
    }
  }
  



  public void close()
  {
    try
    {
      if (chunkseq != null)
        chunkseq.close();
    } catch (Exception e) {
      PngHelperInternal.LOGGER.warning("error closing chunk sequence:" + e.getMessage());
    }
    if (streamFeeder != null) {
      streamFeeder.close();
    }
  }
  

  public boolean isInterlaced()
  {
    return interlaced;
  }
  



  public void setCrcCheckDisabled()
  {
    chunkseq.setCheckCrc(false);
  }
  


  public ChunkSeqReaderPng getChunkseq()
  {
    return chunkseq;
  }
  
  protected ChunkSeqReaderPng createChunkSeqReader()
  {
    return new ChunkSeqReaderPng(false);
  }
  




  public void prepareSimpleDigestComputation()
  {
    if (idatCrca == null) {
      idatCrca = new CRC32();
    } else
      idatCrca.reset();
    if (idatCrcb == null) {
      idatCrcb = new Adler32();
    } else
      idatCrcb.reset();
    imgInfo.updateCrc(idatCrca);
    idatCrcb.update((byte)imgInfo.rows);
  }
  
  long getSimpleDigest() {
    if (idatCrca == null) {
      return 0L;
    }
    return idatCrca.getValue() ^ idatCrcb.getValue() << 31;
  }
  








  public String getSimpleDigestHex()
  {
    return String.format("%016X", new Object[] { Long.valueOf(getSimpleDigest()) });
  }
  


  public String toString()
  {
    return imgInfo.toString() + " interlaced=" + interlaced;
  }
  




  public String toStringCompact()
  {
    return imgInfo.toStringBrief() + (interlaced ? "i" : "");
  }
  
  public ImageInfo getImgInfo() {
    return imgInfo;
  }
  
  public ImageInfo getCurImgInfo() {
    return chunkseq.getCurImgInfo();
  }
}
