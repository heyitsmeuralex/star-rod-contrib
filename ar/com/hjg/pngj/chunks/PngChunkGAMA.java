package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjException;





public class PngChunkGAMA
  extends PngChunkSingle
{
  public static final String ID = "gAMA";
  private double gamma;
  
  public PngChunkGAMA(ImageInfo info)
  {
    super("gAMA", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.BEFORE_PLTE_AND_IDAT;
  }
  
  public ChunkRaw createRawChunk()
  {
    ChunkRaw c = createEmptyChunk(4, true);
    int g = (int)(gamma * 100000.0D + 0.5D);
    PngHelperInternal.writeInt4tobytes(g, data, 0);
    return c;
  }
  
  public void parseFromRaw(ChunkRaw chunk)
  {
    if (len != 4)
      throw new PngjException("bad chunk " + chunk);
    int g = PngHelperInternal.readInt4fromBytes(data, 0);
    gamma = (g / 100000.0D);
  }
  
  public double getGamma() {
    return gamma;
  }
  
  public void setGamma(double gamma) {
    this.gamma = gamma;
  }
}
