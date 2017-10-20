package ar.com.hjg.pngj;

public abstract interface IImageLineArray
{
  public abstract ImageInfo getImageInfo();
  
  public abstract FilterType getFilterType();
  
  public abstract int getSize();
  
  public abstract int getElem(int paramInt);
}
