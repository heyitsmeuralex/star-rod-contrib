package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;





public class PngChunkACTL
  extends PngChunkSingle
{
  public static final String ID = "acTL";
  private int numFrames;
  private int numPlays;
  
  public PngChunkACTL(ImageInfo info)
  {
    super("acTL", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.BEFORE_IDAT;
  }
  
  public ChunkRaw createRawChunk()
  {
    ChunkRaw c = createEmptyChunk(8, true);
    PngHelperInternal.writeInt4tobytes(numFrames, data, 0);
    PngHelperInternal.writeInt4tobytes(numPlays, data, 4);
    return c;
  }
  
  public void parseFromRaw(ChunkRaw chunk)
  {
    numFrames = PngHelperInternal.readInt4fromBytes(data, 0);
    numPlays = PngHelperInternal.readInt4fromBytes(data, 4);
  }
  
  public int getNumFrames() {
    return numFrames;
  }
  
  public void setNumFrames(int numFrames) {
    this.numFrames = numFrames;
  }
  
  public int getNumPlays() {
    return numPlays;
  }
  
  public void setNumPlays(int numPlays) {
    this.numPlays = numPlays;
  }
}
