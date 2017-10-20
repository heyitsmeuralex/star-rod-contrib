package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjException;




public class PngChunkBKGD
  extends PngChunkSingle
{
  public static final String ID = "bKGD";
  private int gray;
  private int red;
  private int green;
  private int blue;
  private int paletteIndex;
  
  public PngChunkBKGD(ImageInfo info)
  {
    super("bKGD", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.AFTER_PLTE_BEFORE_IDAT;
  }
  
  public ChunkRaw createRawChunk()
  {
    ChunkRaw c = null;
    if (imgInfo.greyscale) {
      c = createEmptyChunk(2, true);
      PngHelperInternal.writeInt2tobytes(gray, data, 0);
    } else if (imgInfo.indexed) {
      c = createEmptyChunk(1, true);
      data[0] = ((byte)paletteIndex);
    } else {
      c = createEmptyChunk(6, true);
      PngHelperInternal.writeInt2tobytes(red, data, 0);
      PngHelperInternal.writeInt2tobytes(green, data, 0);
      PngHelperInternal.writeInt2tobytes(blue, data, 0);
    }
    return c;
  }
  
  public void parseFromRaw(ChunkRaw c)
  {
    if (imgInfo.greyscale) {
      gray = PngHelperInternal.readInt2fromBytes(data, 0);
    } else if (imgInfo.indexed) {
      paletteIndex = (data[0] & 0xFF);
    } else {
      red = PngHelperInternal.readInt2fromBytes(data, 0);
      green = PngHelperInternal.readInt2fromBytes(data, 2);
      blue = PngHelperInternal.readInt2fromBytes(data, 4);
    }
  }
  




  public void setGray(int gray)
  {
    if (!imgInfo.greyscale)
      throw new PngjException("only gray images support this");
    this.gray = gray;
  }
  
  public int getGray() {
    if (!imgInfo.greyscale)
      throw new PngjException("only gray images support this");
    return gray;
  }
  



  public void setPaletteIndex(int i)
  {
    if (!imgInfo.indexed)
      throw new PngjException("only indexed (pallete) images support this");
    paletteIndex = i;
  }
  
  public int getPaletteIndex() {
    if (!imgInfo.indexed)
      throw new PngjException("only indexed (pallete) images support this");
    return paletteIndex;
  }
  



  public void setRGB(int r, int g, int b)
  {
    if ((imgInfo.greyscale) || (imgInfo.indexed))
      throw new PngjException("only rgb or rgba images support this");
    red = r;
    green = g;
    blue = b;
  }
  
  public int[] getRGB() {
    if ((imgInfo.greyscale) || (imgInfo.indexed))
      throw new PngjException("only rgb or rgba images support this");
    return new int[] { red, green, blue };
  }
}
