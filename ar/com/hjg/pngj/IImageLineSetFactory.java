package ar.com.hjg.pngj;

public abstract interface IImageLineSetFactory<T extends IImageLine>
{
  public abstract IImageLineSet<T> create(ImageInfo paramImageInfo, boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3);
}
