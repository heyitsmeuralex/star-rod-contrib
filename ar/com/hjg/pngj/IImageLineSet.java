package ar.com.hjg.pngj;

public abstract interface IImageLineSet<T extends IImageLine>
{
  public abstract IImageLine getImageLine(int paramInt);
  
  public abstract IImageLine getImageLineRawNum(int paramInt);
  
  public abstract boolean hasImageLine(int paramInt);
  
  public abstract int size();
}
