package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjException;





public class PngChunkPHYS
  extends PngChunkSingle
{
  public static final String ID = "pHYs";
  private long pixelsxUnitX;
  private long pixelsxUnitY;
  private int units;
  
  public PngChunkPHYS(ImageInfo info)
  {
    super("pHYs", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.BEFORE_IDAT;
  }
  
  public ChunkRaw createRawChunk()
  {
    ChunkRaw c = createEmptyChunk(9, true);
    PngHelperInternal.writeInt4tobytes((int)pixelsxUnitX, data, 0);
    PngHelperInternal.writeInt4tobytes((int)pixelsxUnitY, data, 4);
    data[8] = ((byte)units);
    return c;
  }
  
  public void parseFromRaw(ChunkRaw chunk)
  {
    if (len != 9)
      throw new PngjException("bad chunk length " + chunk);
    pixelsxUnitX = PngHelperInternal.readInt4fromBytes(data, 0);
    if (pixelsxUnitX < 0L)
      pixelsxUnitX += 4294967296L;
    pixelsxUnitY = PngHelperInternal.readInt4fromBytes(data, 4);
    if (pixelsxUnitY < 0L)
      pixelsxUnitY += 4294967296L;
    units = PngHelperInternal.readInt1fromByte(data, 8);
  }
  
  public long getPixelsxUnitX() {
    return pixelsxUnitX;
  }
  
  public void setPixelsxUnitX(long pixelsxUnitX) {
    this.pixelsxUnitX = pixelsxUnitX;
  }
  
  public long getPixelsxUnitY() {
    return pixelsxUnitY;
  }
  
  public void setPixelsxUnitY(long pixelsxUnitY) {
    this.pixelsxUnitY = pixelsxUnitY;
  }
  
  public int getUnits() {
    return units;
  }
  
  public void setUnits(int units) {
    this.units = units;
  }
  




  public double getAsDpi()
  {
    if ((units != 1) || (pixelsxUnitX != pixelsxUnitY))
      return -1.0D;
    return pixelsxUnitX * 0.0254D;
  }
  


  public double[] getAsDpi2()
  {
    if (units != 1)
      return new double[] { -1.0D, -1.0D };
    return new double[] { pixelsxUnitX * 0.0254D, pixelsxUnitY * 0.0254D };
  }
  
  public void setAsDpi(double dpi) {
    units = 1;
    pixelsxUnitX = ((dpi / 0.0254D + 0.5D));
    pixelsxUnitY = pixelsxUnitX;
  }
  
  public void setAsDpi2(double dpix, double dpiy) {
    units = 1;
    pixelsxUnitX = ((dpix / 0.0254D + 0.5D));
    pixelsxUnitY = ((dpiy / 0.0254D + 0.5D));
  }
}
