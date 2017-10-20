package ar.com.hjg.pngj.chunks;

import ar.com.hjg.pngj.ImageInfo;































public class ChunkCopyBehaviour
{
  public static final int COPY_NONE = 0;
  public static final int COPY_PALETTE = 1;
  public static final int COPY_ALL_SAFE = 4;
  public static final int COPY_ALL = 8;
  public static final int COPY_PHYS = 16;
  public static final int COPY_TEXTUAL = 32;
  public static final int COPY_TRANSPARENCY = 64;
  public static final int COPY_UNKNOWN = 128;
  public static final int COPY_ALMOSTALL = 256;
  
  public ChunkCopyBehaviour() {}
  
  private static boolean maskMatch(int v, int mask)
  {
    return (v & mask) != 0;
  }
  







  public static ChunkPredicate createPredicate(final int copyFromMask, ImageInfo imgInfo)
  {
    new ChunkPredicate() {
      public boolean match(PngChunk chunk) {
        if (crit) {
          if (id.equals("PLTE")) {
            if ((val$imgInfo.indexed) && (ChunkCopyBehaviour.maskMatch(copyFromMask, 1)))
              return true;
            if ((!val$imgInfo.greyscale) && (ChunkCopyBehaviour.maskMatch(copyFromMask, 8)))
              return true;
          }
        } else {
          boolean text = chunk instanceof PngChunkTextVar;
          boolean safe = safe;
          
          if (ChunkCopyBehaviour.maskMatch(copyFromMask, 8))
            return true;
          if ((safe) && (ChunkCopyBehaviour.maskMatch(copyFromMask, 4)))
            return true;
          if ((id.equals("tRNS")) && (ChunkCopyBehaviour.maskMatch(copyFromMask, 64)))
          {
            return true; }
          if ((id.equals("pHYs")) && (ChunkCopyBehaviour.maskMatch(copyFromMask, 16)))
          {
            return true; }
          if ((text) && (ChunkCopyBehaviour.maskMatch(copyFromMask, 32)))
            return true;
          if ((ChunkCopyBehaviour.maskMatch(copyFromMask, 256)) && (!ChunkHelper.isUnknown(chunk)) && (!text) && (!id.equals("hIST")) && (!id.equals("tIME")))
          {

            return true; }
          if ((ChunkCopyBehaviour.maskMatch(copyFromMask, 128)) && (ChunkHelper.isUnknown(chunk)))
          {
            return true; }
        }
        return false;
      }
    };
  }
}
