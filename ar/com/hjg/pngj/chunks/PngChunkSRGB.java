package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjException;







public class PngChunkSRGB
  extends PngChunkSingle
{
  public static final String ID = "sRGB";
  public static final int RENDER_INTENT_Perceptual = 0;
  public static final int RENDER_INTENT_Relative_colorimetric = 1;
  public static final int RENDER_INTENT_Saturation = 2;
  public static final int RENDER_INTENT_Absolute_colorimetric = 3;
  private int intent;
  
  public PngChunkSRGB(ImageInfo info)
  {
    super("sRGB", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.BEFORE_PLTE_AND_IDAT;
  }
  
  public void parseFromRaw(ChunkRaw c)
  {
    if (len != 1)
      throw new PngjException("bad chunk length " + c);
    intent = PngHelperInternal.readInt1fromByte(data, 0);
  }
  
  public ChunkRaw createRawChunk()
  {
    ChunkRaw c = null;
    c = createEmptyChunk(1, true);
    data[0] = ((byte)intent);
    return c;
  }
  
  public int getIntent() {
    return intent;
  }
  
  public void setIntent(int intent) {
    this.intent = intent;
  }
}
