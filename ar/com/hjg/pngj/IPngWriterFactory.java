package ar.com.hjg.pngj;

import java.io.OutputStream;

public abstract interface IPngWriterFactory
{
  public abstract PngWriter createPngWriter(OutputStream paramOutputStream, ImageInfo paramImageInfo);
}
