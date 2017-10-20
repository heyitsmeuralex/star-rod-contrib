package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjException;

public class PngChunkCHRM
  extends PngChunkSingle
{
  public static final String ID = "cHRM";
  private double whitex;
  private double whitey;
  private double redx;
  private double redy;
  private double greenx;
  private double greeny;
  private double bluex;
  private double bluey;
  
  public PngChunkCHRM(ImageInfo info)
  {
    super("cHRM", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.AFTER_PLTE_BEFORE_IDAT;
  }
  
  public ChunkRaw createRawChunk()
  {
    ChunkRaw c = null;
    c = createEmptyChunk(32, true);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(whitex), data, 0);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(whitey), data, 4);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(redx), data, 8);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(redy), data, 12);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(greenx), data, 16);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(greeny), data, 20);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(bluex), data, 24);
    PngHelperInternal.writeInt4tobytes(PngHelperInternal.doubleToInt100000(bluey), data, 28);
    return c;
  }
  
  public void parseFromRaw(ChunkRaw c)
  {
    if (len != 32)
      throw new PngjException("bad chunk " + c);
    whitex = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(data, 0));
    whitey = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(data, 4));
    redx = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(data, 8));
    redy = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(data, 12));
    greenx = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(data, 16));
    greeny = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(data, 20));
    bluex = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(data, 24));
    bluey = PngHelperInternal.intToDouble100000(PngHelperInternal.readInt4fromBytes(data, 28));
  }
  
  public void setChromaticities(double whitex, double whitey, double redx, double redy, double greenx, double greeny, double bluex, double bluey)
  {
    this.whitex = whitex;
    this.redx = redx;
    this.greenx = greenx;
    this.bluex = bluex;
    this.whitey = whitey;
    this.redy = redy;
    this.greeny = greeny;
    this.bluey = bluey;
  }
  
  public double[] getChromaticities() {
    return new double[] { whitex, whitey, redx, redy, greenx, greeny, bluex, bluey };
  }
}
