package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjException;








public class PngChunkTRNS
  extends PngChunkSingle
{
  public static final String ID = "tRNS";
  private int gray;
  private int red;
  private int green;
  private int blue;
  private int[] paletteAlpha = new int[0];
  
  public PngChunkTRNS(ImageInfo info) {
    super("tRNS", info);
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
      c = createEmptyChunk(paletteAlpha.length, true);
      for (int n = 0; n < len; n++) {
        data[n] = ((byte)paletteAlpha[n]);
      }
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
      int nentries = data.length;
      paletteAlpha = new int[nentries];
      for (int n = 0; n < nentries; n++) {
        paletteAlpha[n] = (data[n] & 0xFF);
      }
    } else {
      red = PngHelperInternal.readInt2fromBytes(data, 0);
      green = PngHelperInternal.readInt2fromBytes(data, 2);
      blue = PngHelperInternal.readInt2fromBytes(data, 4);
    }
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
  
  public int getRGB888() {
    if ((imgInfo.greyscale) || (imgInfo.indexed))
      throw new PngjException("only rgb or rgba images support this");
    return red << 16 | green << 8 | blue;
  }
  
  public void setGray(int g) {
    if (!imgInfo.greyscale)
      throw new PngjException("only grayscale images support this");
    gray = g;
  }
  
  public int getGray() {
    if (!imgInfo.greyscale)
      throw new PngjException("only grayscale images support this");
    return gray;
  }
  





  public void setEntryPalAlpha(int idx, int val)
  {
    paletteAlpha[idx] = val;
  }
  
  public void setNentriesPalAlpha(int len) {
    paletteAlpha = new int[len];
  }
  


  public void setPalAlpha(int[] palAlpha)
  {
    if (!imgInfo.indexed)
      throw new PngjException("only indexed images support this");
    paletteAlpha = palAlpha;
  }
  


  public int[] getPalletteAlpha()
  {
    return paletteAlpha;
  }
  


  public void setIndexEntryAsTransparent(int palAlphaIndex)
  {
    if (!imgInfo.indexed)
      throw new PngjException("only indexed images support this");
    paletteAlpha = new int[] { palAlphaIndex + 1 };
    for (int i = 0; i < palAlphaIndex; i++)
      paletteAlpha[i] = 255;
    paletteAlpha[palAlphaIndex] = 0;
  }
}
