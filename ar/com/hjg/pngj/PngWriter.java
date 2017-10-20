package ar.com.hjg.pngj;

import ar.com.hjg.pngj.chunks.ChunkCopyBehaviour;
import ar.com.hjg.pngj.chunks.ChunkPredicate;
import ar.com.hjg.pngj.chunks.ChunkRaw;
import ar.com.hjg.pngj.chunks.ChunksList;
import ar.com.hjg.pngj.chunks.ChunksListForWrite;
import ar.com.hjg.pngj.chunks.PngChunk;
import ar.com.hjg.pngj.chunks.PngChunkIEND;
import ar.com.hjg.pngj.chunks.PngChunkIHDR;
import ar.com.hjg.pngj.chunks.PngMetadata;
import ar.com.hjg.pngj.pixels.PixelsWriter;
import ar.com.hjg.pngj.pixels.PixelsWriterDefault;
import java.io.File;
import java.io.OutputStream;
import java.util.List;
import java.util.logging.Logger;








public class PngWriter
{
  public final ImageInfo imgInfo;
  protected int rowNum = -1;
  


  private final ChunksListForWrite chunksList;
  


  private final PngMetadata metadata;
  


  protected int currentChunkGroup = -1;
  
  private int passes = 1;
  private int currentpass = 0;
  
  private boolean shouldCloseStream = true;
  
  private int idatMaxSize = 0;
  

  protected PixelsWriter pixelsWriter;
  
  private final OutputStream os;
  
  private ChunkPredicate copyFromPredicate = null;
  private ChunksList copyFromList = null;
  
  protected StringBuilder debuginfo = new StringBuilder();
  








  public PngWriter(File file, ImageInfo imgInfo, boolean allowoverwrite)
  {
    this(PngHelperInternal.ostreamFromFile(file, allowoverwrite), imgInfo);
    setShouldCloseStream(true);
  }
  


  public PngWriter(File file, ImageInfo imgInfo)
  {
    this(file, imgInfo, true);
  }
  







  public PngWriter(OutputStream outputStream, ImageInfo imgInfo)
  {
    os = outputStream;
    this.imgInfo = imgInfo;
    
    chunksList = new ChunksListForWrite(imgInfo);
    metadata = new PngMetadata(chunksList);
    pixelsWriter = createPixelsWriter(imgInfo);
    setCompLevel(9);
  }
  
  private void initIdat() {
    pixelsWriter.setOs(os);
    pixelsWriter.setIdatMaxSize(idatMaxSize);
    writeSignatureAndIHDR();
    writeFirstChunks();
  }
  
  private void writeEndChunk() {
    currentChunkGroup = 6;
    PngChunkIEND c = new PngChunkIEND(imgInfo);
    c.createRawChunk().writeChunk(os);
    chunksList.getChunks().add(c);
  }
  
  private void writeFirstChunks() {
    if (currentChunkGroup >= 4)
      return;
    int nw = 0;
    currentChunkGroup = 1;
    queueChunksFromOther();
    nw = chunksList.writeChunks(os, currentChunkGroup);
    currentChunkGroup = 2;
    nw = chunksList.writeChunks(os, currentChunkGroup);
    if ((nw > 0) && (imgInfo.greyscale))
      throw new PngjOutputException("cannot write palette for this format");
    if ((nw == 0) && (imgInfo.indexed))
      throw new PngjOutputException("missing palette");
    currentChunkGroup = 3;
    nw = chunksList.writeChunks(os, currentChunkGroup);
  }
  
  private void writeLastChunks() {
    currentChunkGroup = 5;
    queueChunksFromOther();
    chunksList.writeChunks(os, currentChunkGroup);
    
    List<PngChunk> pending = chunksList.getQueuedChunks();
    if (!pending.isEmpty()) {
      throw new PngjOutputException(pending.size() + " chunks were not written! Eg: " + ((PngChunk)pending.get(0)).toString());
    }
  }
  


  private void writeSignatureAndIHDR()
  {
    PngHelperInternal.writeBytes(os, PngHelperInternal.getPngIdSignature());
    currentChunkGroup = 0;
    PngChunkIHDR ihdr = new PngChunkIHDR(imgInfo);
    
    ihdr.createRawChunk().writeChunk(os);
    chunksList.getChunks().add(ihdr);
  }
  
  private void queueChunksFromOther() {
    if ((copyFromList == null) || (copyFromPredicate == null))
      return;
    boolean idatDone = currentChunkGroup >= 4;
    

    for (PngChunk chunk : copyFromList.getChunks()) {
      if (getRawdata != null)
      {
        int groupOri = chunk.getChunkGroup();
        if (((groupOri > 4) || (!idatDone)) && 
        
          ((groupOri < 4) || (idatDone)) && (
          
          (!crit) || (id.equals("PLTE"))))
        {

          boolean copy = copyFromPredicate.match(chunk);
          if (copy)
          {
            if ((chunksList.getEquivalent(chunk).isEmpty()) && (chunksList.getQueuedEquivalent(chunk).isEmpty()))
            {
              chunksList.queue(chunk);
            }
          }
        }
      }
    }
  }
  






  public void queueChunk(PngChunk chunk)
  {
    for (PngChunk other : chunksList.getQueuedEquivalent(chunk)) {
      getChunksList().removeChunk(other);
    }
    chunksList.queue(chunk);
  }
  

















  public void copyChunksFrom(ChunksList chunks, int copyMask)
  {
    copyChunksFrom(chunks, ChunkCopyBehaviour.createPredicate(copyMask, imgInfo));
  }
  


  public void copyChunksFrom(ChunksList chunks)
  {
    copyChunksFrom(chunks, 8);
  }
  







  public void copyChunksFrom(ChunksList chunks, ChunkPredicate predicate)
  {
    if ((copyFromList != null) && (chunks != null))
      PngHelperInternal.LOGGER.warning("copyChunksFrom should only be called once");
    if (predicate == null)
      throw new PngjOutputException("copyChunksFrom requires a predicate");
    copyFromList = chunks;
    copyFromPredicate = predicate;
  }
  







  public double computeCompressionRatio()
  {
    if (currentChunkGroup < 5)
      throw new PngjOutputException("must be called after end()");
    return pixelsWriter.getCompression();
  }
  


  public void end()
  {
    if ((rowNum != imgInfo.rows - 1) || (!pixelsWriter.isDone()))
      throw new PngjOutputException("all rows have not been written");
    try {
      if (pixelsWriter != null)
        pixelsWriter.close();
      if (currentChunkGroup < 5)
        writeLastChunks();
      if (currentChunkGroup < 6)
        writeEndChunk();
    } finally {
      close();
    }
  }
  







  public void close()
  {
    if (pixelsWriter != null)
      pixelsWriter.close();
    if ((shouldCloseStream) && (os != null)) {
      try {
        os.close();
      } catch (Exception e) {
        PngHelperInternal.LOGGER.warning("Error closing writer " + e.toString());
      }
    }
  }
  

  public ChunksListForWrite getChunksList()
  {
    return chunksList;
  }
  


  public PngMetadata getMetadata()
  {
    return metadata;
  }
  





  public void setFilterType(FilterType filterType)
  {
    pixelsWriter.setFilterType(filterType);
  }
  






  public void setCompLevel(int complevel)
  {
    pixelsWriter.setDeflaterCompLevel(Integer.valueOf(complevel));
  }
  


  public void setFilterPreserve(boolean filterPreserve)
  {
    if (filterPreserve) {
      pixelsWriter.setFilterType(FilterType.FILTER_PRESERVE);
    } else if (pixelsWriter.getFilterType() == null) {
      pixelsWriter.setFilterType(FilterType.FILTER_DEFAULT);
    }
  }
  





  public void setIdatMaxSize(int idatMaxSize)
  {
    this.idatMaxSize = idatMaxSize;
  }
  




  public void setShouldCloseStream(boolean shouldCloseStream)
  {
    this.shouldCloseStream = shouldCloseStream;
  }
  




  public void writeRow(IImageLine imgline)
  {
    writeRow(imgline, rowNum + 1);
  }
  


  public void writeRows(IImageLineSet<? extends IImageLine> imglines)
  {
    for (int i = 0; i < imgInfo.rows; i++)
      writeRow(imglines.getImageLineRawNum(i));
  }
  
  public void writeRow(IImageLine imgline, int rownumber) {
    rowNum += 1;
    if (rowNum == imgInfo.rows)
      rowNum = 0;
    if (rownumber == imgInfo.rows)
      rownumber = 0;
    if ((rownumber >= 0) && (rowNum != rownumber)) {
      throw new PngjOutputException("rows must be written in order: expected:" + rowNum + " passed:" + rownumber);
    }
    if (rowNum == 0)
      currentpass += 1;
    if ((rownumber == 0) && (currentpass == passes)) {
      initIdat();
      currentChunkGroup = 4;
    }
    byte[] rowb = pixelsWriter.getRowb();
    imgline.writeToPngRaw(rowb);
    pixelsWriter.processRow(rowb);
  }
  



  public void writeRowInt(int[] buf)
  {
    writeRow(new ImageLineInt(imgInfo, buf));
  }
  









  protected PixelsWriter createPixelsWriter(ImageInfo imginfo)
  {
    PixelsWriterDefault pw = new PixelsWriterDefault(imginfo);
    return pw;
  }
  
  public final PixelsWriter getPixelsWriter() {
    return pixelsWriter;
  }
  
  public String getDebuginfo() {
    return debuginfo.toString();
  }
}
