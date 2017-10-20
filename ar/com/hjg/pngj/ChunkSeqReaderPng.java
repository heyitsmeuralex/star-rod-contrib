package ar.com.hjg.pngj;

import ar.com.hjg.pngj.chunks.ChunkFactory;
import ar.com.hjg.pngj.chunks.ChunkHelper;
import ar.com.hjg.pngj.chunks.ChunkLoadBehaviour;
import ar.com.hjg.pngj.chunks.ChunkRaw;
import ar.com.hjg.pngj.chunks.ChunksList;
import ar.com.hjg.pngj.chunks.PngChunk;
import ar.com.hjg.pngj.chunks.PngChunkIHDR;
import java.util.HashSet;
import java.util.List;
import java.util.Set;









public class ChunkSeqReaderPng
  extends ChunkSeqReader
{
  protected ImageInfo imageInfo;
  protected ImageInfo curImageInfo;
  protected Deinterlacer deinterlacer;
  protected int currentChunkGroup = -1;
  



  protected ChunksList chunksList = null;
  protected final boolean callbackMode;
  private long bytesAncChunksLoaded = 0L;
  
  private boolean checkCrc = true;
  

  private boolean includeNonBufferedChunks = false;
  
  private Set<String> chunksToSkip = new HashSet();
  private long maxTotalBytesRead = 0L;
  private long skipChunkMaxSize = 0L;
  private long maxBytesMetadata = 0L;
  private IChunkFactory chunkFactory;
  private ChunkLoadBehaviour chunkLoadBehaviour = ChunkLoadBehaviour.LOAD_CHUNK_ALWAYS;
  
  public ChunkSeqReaderPng(boolean callbackMode)
  {
    this.callbackMode = callbackMode;
    chunkFactory = new ChunkFactory();
  }
  
  private void updateAndCheckChunkGroup(String id) {
    if (id.equals("IHDR")) {
      if (currentChunkGroup < 0) {
        currentChunkGroup = 0;
      } else
        throw new PngjInputException("unexpected chunk " + id);
    } else if (id.equals("PLTE")) {
      if ((currentChunkGroup == 0) || (currentChunkGroup == 1)) {
        currentChunkGroup = 2;
      } else
        throw new PngjInputException("unexpected chunk " + id);
    } else if (id.equals("IDAT")) {
      if ((currentChunkGroup >= 0) && (currentChunkGroup <= 4)) {
        currentChunkGroup = 4;
      } else
        throw new PngjInputException("unexpected chunk " + id);
    } else if (id.equals("IEND")) {
      if (currentChunkGroup >= 4) {
        currentChunkGroup = 6;
      } else {
        throw new PngjInputException("unexpected chunk " + id);
      }
    } else if (currentChunkGroup <= 1) {
      currentChunkGroup = 1;
    } else if (currentChunkGroup <= 3) {
      currentChunkGroup = 3;
    } else {
      currentChunkGroup = 5;
    }
  }
  
  public boolean shouldSkipContent(int len, String id)
  {
    if (super.shouldSkipContent(len, id))
      return true;
    if (ChunkHelper.isCritical(id))
      return false;
    if ((maxTotalBytesRead > 0L) && (len + getBytesCount() > maxTotalBytesRead)) {
      throw new PngjInputException("Maximum total bytes to read exceeeded: " + maxTotalBytesRead + " offset:" + getBytesCount() + " len=" + len);
    }
    if (chunksToSkip.contains(id))
      return true;
    if ((skipChunkMaxSize > 0L) && (len > skipChunkMaxSize))
      return true;
    if ((maxBytesMetadata > 0L) && (len > maxBytesMetadata - bytesAncChunksLoaded))
      return true;
    switch (1.$SwitchMap$ar$com$hjg$pngj$chunks$ChunkLoadBehaviour[chunkLoadBehaviour.ordinal()]) {
    case 1: 
      if (!ChunkHelper.isSafeToCopy(id))
        return true;
      break;
    case 2: 
      return true;
    }
    
    
    return false;
  }
  
  public long getBytesChunksLoaded() {
    return bytesAncChunksLoaded;
  }
  
  public int getCurrentChunkGroup() {
    return currentChunkGroup;
  }
  
  public void setChunksToSkip(String... chunksToSkip) {
    this.chunksToSkip.clear();
    for (String c : chunksToSkip)
      this.chunksToSkip.add(c);
  }
  
  public void addChunkToSkip(String chunkToSkip) {
    chunksToSkip.add(chunkToSkip);
  }
  
  public void dontSkipChunk(String chunkToSkip) {
    chunksToSkip.remove(chunkToSkip);
  }
  
  public boolean firstChunksNotYetRead() {
    return getCurrentChunkGroup() < 4;
  }
  
  protected void postProcessChunk(ChunkReader chunkR)
  {
    super.postProcessChunk(chunkR);
    if (getChunkRawid.equals("IHDR")) {
      PngChunkIHDR ch = new PngChunkIHDR(null);
      ch.parseFromRaw(chunkR.getChunkRaw());
      imageInfo = ch.createImageInfo();
      curImageInfo = imageInfo;
      if (ch.isInterlaced())
        deinterlacer = new Deinterlacer(curImageInfo);
      chunksList = new ChunksList(imageInfo);
    }
    if ((mode == ChunkReader.ChunkReaderMode.BUFFER) && (countChunkTypeAsAncillary(getChunkRawid))) {
      bytesAncChunksLoaded += getChunkRawlen;
    }
    if ((mode == ChunkReader.ChunkReaderMode.BUFFER) || (includeNonBufferedChunks)) {
      PngChunk chunk = chunkFactory.createChunk(chunkR.getChunkRaw(), getImageInfo());
      chunksList.appendReadChunk(chunk, currentChunkGroup);
    }
    if (isDone()) {
      processEndPng();
    }
  }
  
  protected boolean countChunkTypeAsAncillary(String id) {
    return !ChunkHelper.isCritical(id);
  }
  
  protected DeflatedChunksSet createIdatSet(String id)
  {
    IdatSet ids = new IdatSet(id, getCurImgInfo(), deinterlacer);
    ids.setCallbackMode(callbackMode);
    return ids;
  }
  
  public IdatSet getIdatSet() {
    DeflatedChunksSet c = getCurReaderDeflatedSet();
    return (c instanceof IdatSet) ? (IdatSet)c : null;
  }
  
  protected boolean isIdatKind(String id)
  {
    return id.equals("IDAT");
  }
  
  public int consume(byte[] buf, int off, int len)
  {
    return super.consume(buf, off, len);
  }
  





  public void setChunkFactory(IChunkFactory chunkFactory)
  {
    this.chunkFactory = chunkFactory;
  }
  


  protected void processEndPng() {}
  


  public ImageInfo getImageInfo()
  {
    return imageInfo;
  }
  
  public boolean isInterlaced() {
    return deinterlacer != null;
  }
  
  public Deinterlacer getDeinterlacer() {
    return deinterlacer;
  }
  
  protected void startNewChunk(int len, String id, long offset)
  {
    updateAndCheckChunkGroup(id);
    super.startNewChunk(len, id, offset);
  }
  
  public void close()
  {
    if (currentChunkGroup != 6)
      currentChunkGroup = 6;
    super.close();
  }
  
  public List<PngChunk> getChunks() {
    return chunksList.getChunks();
  }
  
  public void setMaxTotalBytesRead(long maxTotalBytesRead) {
    this.maxTotalBytesRead = maxTotalBytesRead;
  }
  
  public long getSkipChunkMaxSize() {
    return skipChunkMaxSize;
  }
  
  public void setSkipChunkMaxSize(long skipChunkMaxSize) {
    this.skipChunkMaxSize = skipChunkMaxSize;
  }
  
  public long getMaxBytesMetadata() {
    return maxBytesMetadata;
  }
  
  public void setMaxBytesMetadata(long maxBytesMetadata) {
    this.maxBytesMetadata = maxBytesMetadata;
  }
  
  public long getMaxTotalBytesRead() {
    return maxTotalBytesRead;
  }
  
  protected boolean shouldCheckCrc(int len, String id)
  {
    return checkCrc;
  }
  
  public boolean isCheckCrc() {
    return checkCrc;
  }
  
  public void setCheckCrc(boolean checkCrc) {
    this.checkCrc = checkCrc;
  }
  
  public boolean isCallbackMode() {
    return callbackMode;
  }
  
  public Set<String> getChunksToSkip() {
    return chunksToSkip;
  }
  
  public void setChunkLoadBehaviour(ChunkLoadBehaviour chunkLoadBehaviour) {
    this.chunkLoadBehaviour = chunkLoadBehaviour;
  }
  
  public ImageInfo getCurImgInfo() {
    return curImageInfo;
  }
  
  public void updateCurImgInfo(ImageInfo iminfo) {
    if (!iminfo.equals(curImageInfo)) {
      curImageInfo = iminfo;
    }
    if (deinterlacer != null) {
      deinterlacer = new Deinterlacer(curImageInfo);
    }
  }
  








  public void setIncludeNonBufferedChunks(boolean includeNonBufferedChunks)
  {
    this.includeNonBufferedChunks = includeNonBufferedChunks;
  }
}
