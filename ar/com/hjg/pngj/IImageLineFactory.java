package ar.com.hjg.pngj;

public abstract interface IImageLineFactory<T extends IImageLine>
{
  public abstract T createImageLine(ImageInfo paramImageInfo);
}
