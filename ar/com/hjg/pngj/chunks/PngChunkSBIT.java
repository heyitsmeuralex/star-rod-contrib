package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjException;





public class PngChunkSBIT
  extends PngChunkSingle
{
  public static final String ID = "sBIT";
  private int graysb;
  private int alphasb;
  private int redsb;
  private int greensb;
  private int bluesb;
  
  public PngChunkSBIT(ImageInfo info)
  {
    super("sBIT", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.BEFORE_PLTE_AND_IDAT;
  }
  
  private int getCLen() {
    int len = imgInfo.greyscale ? 1 : 3;
    if (imgInfo.alpha)
      len++;
    return len;
  }
  
  public void parseFromRaw(ChunkRaw c)
  {
    if (len != getCLen())
      throw new PngjException("bad chunk length " + c);
    if (imgInfo.greyscale) {
      graysb = PngHelperInternal.readInt1fromByte(data, 0);
      if (imgInfo.alpha)
        alphasb = PngHelperInternal.readInt1fromByte(data, 1);
    } else {
      redsb = PngHelperInternal.readInt1fromByte(data, 0);
      greensb = PngHelperInternal.readInt1fromByte(data, 1);
      bluesb = PngHelperInternal.readInt1fromByte(data, 2);
      if (imgInfo.alpha) {
        alphasb = PngHelperInternal.readInt1fromByte(data, 3);
      }
    }
  }
  
  public ChunkRaw createRawChunk() {
    ChunkRaw c = null;
    c = createEmptyChunk(getCLen(), true);
    if (imgInfo.greyscale) {
      data[0] = ((byte)graysb);
      if (imgInfo.alpha)
        data[1] = ((byte)alphasb);
    } else {
      data[0] = ((byte)redsb);
      data[1] = ((byte)greensb);
      data[2] = ((byte)bluesb);
      if (imgInfo.alpha)
        data[3] = ((byte)alphasb);
    }
    return c;
  }
  
  public void setGraysb(int gray) {
    if (!imgInfo.greyscale)
      throw new PngjException("only greyscale images support this");
    graysb = gray;
  }
  
  public int getGraysb() {
    if (!imgInfo.greyscale)
      throw new PngjException("only greyscale images support this");
    return graysb;
  }
  
  public void setAlphasb(int a) {
    if (!imgInfo.alpha)
      throw new PngjException("only images with alpha support this");
    alphasb = a;
  }
  
  public int getAlphasb() {
    if (!imgInfo.alpha)
      throw new PngjException("only images with alpha support this");
    return alphasb;
  }
  



  public void setRGB(int r, int g, int b)
  {
    if ((imgInfo.greyscale) || (imgInfo.indexed))
      throw new PngjException("only rgb or rgba images support this");
    redsb = r;
    greensb = g;
    bluesb = b;
  }
  
  public int[] getRGB() {
    if ((imgInfo.greyscale) || (imgInfo.indexed))
      throw new PngjException("only rgb or rgba images support this");
    return new int[] { redsb, greensb, bluesb };
  }
}
