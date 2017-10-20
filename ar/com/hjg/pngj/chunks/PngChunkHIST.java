package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngHelperInternal;
import ar.com.hjg.pngj.PngjException;






public class PngChunkHIST
  extends PngChunkSingle
{
  public static final String ID = "hIST";
  private int[] hist = new int[0];
  
  public PngChunkHIST(ImageInfo info) {
    super("hIST", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.AFTER_PLTE_BEFORE_IDAT;
  }
  
  public void parseFromRaw(ChunkRaw c)
  {
    if (!imgInfo.indexed)
      throw new PngjException("only indexed images accept a HIST chunk");
    int nentries = data.length / 2;
    hist = new int[nentries];
    for (int i = 0; i < hist.length; i++) {
      hist[i] = PngHelperInternal.readInt2fromBytes(data, i * 2);
    }
  }
  
  public ChunkRaw createRawChunk()
  {
    if (!imgInfo.indexed)
      throw new PngjException("only indexed images accept a HIST chunk");
    ChunkRaw c = null;
    c = createEmptyChunk(hist.length * 2, true);
    for (int i = 0; i < hist.length; i++) {
      PngHelperInternal.writeInt2tobytes(hist[i], data, i * 2);
    }
    return c;
  }
  
  public int[] getHist() {
    return hist;
  }
  
  public void setHist(int[] hist) {
    this.hist = hist;
  }
}
