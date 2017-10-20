package ar.com.hjg.pngj.chunks;

public abstract interface ChunkPredicate
{
  public abstract boolean match(PngChunk paramPngChunk);
}
