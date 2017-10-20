package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;
import ar.com.hjg.pngj.PngjException;








public class PngChunkPLTE
  extends PngChunkSingle
{
  public static final String ID = "PLTE";
  private int nentries = 0;
  
  private int[] entries;
  

  public PngChunkPLTE(ImageInfo info)
  {
    super("PLTE", info);
  }
  
  public PngChunk.ChunkOrderingConstraint getOrderingConstraint()
  {
    return PngChunk.ChunkOrderingConstraint.NA;
  }
  
  public ChunkRaw createRawChunk()
  {
    int len = 3 * nentries;
    int[] rgb = new int[3];
    ChunkRaw c = createEmptyChunk(len, true);
    int n = 0; for (int i = 0; n < nentries; n++) {
      getEntryRgb(n, rgb);
      data[(i++)] = ((byte)rgb[0]);
      data[(i++)] = ((byte)rgb[1]);
      data[(i++)] = ((byte)rgb[2]);
    }
    return c;
  }
  
  public void parseFromRaw(ChunkRaw chunk)
  {
    setNentries(len / 3);
    int n = 0; for (int i = 0; n < nentries; n++) {
      setEntry(n, data[(i++)] & 0xFF, data[(i++)] & 0xFF, data[(i++)] & 0xFF);
    }
  }
  
  public void setNentries(int n)
  {
    nentries = n;
    if ((nentries < 1) || (nentries > 256))
      throw new PngjException("invalid pallette - nentries=" + nentries);
    if ((entries == null) || (entries.length != nentries)) {
      entries = new int[nentries];
    }
  }
  
  public int getNentries() {
    return nentries;
  }
  
  public void setEntry(int n, int r, int g, int b) {
    entries[n] = (r << 16 | g << 8 | b);
  }
  
  public int getEntry(int n) {
    return entries[n];
  }
  
  public void getEntryRgb(int n, int[] rgb) {
    getEntryRgb(n, rgb, 0);
  }
  
  public void getEntryRgb(int n, int[] rgb, int offset) {
    int v = entries[n];
    rgb[(offset + 0)] = ((v & 0xFF0000) >> 16);
    rgb[(offset + 1)] = ((v & 0xFF00) >> 8);
    rgb[(offset + 2)] = (v & 0xFF);
  }
  
  public int minBitDepth() {
    if (nentries <= 2)
      return 1;
    if (nentries <= 4)
      return 2;
    if (nentries <= 16) {
      return 4;
    }
    return 8;
  }
}
