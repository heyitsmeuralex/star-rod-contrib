package ar.com.hjg.pngj;

import ar.com.hjg.pngj.chunks.ChunkRaw;
import ar.com.hjg.pngj.chunks.PngChunk;

public abstract interface IChunkFactory
{
  public abstract PngChunk createChunk(ChunkRaw paramChunkRaw, ImageInfo paramImageInfo);
}
