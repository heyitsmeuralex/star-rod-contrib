package ar.com.hjg.pngj;

public abstract interface IImageLine
{
  public abstract void readFromPngRaw(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3);
  
  public abstract void endReadFromPngRaw();
  
  public abstract void writeToPngRaw(byte[] paramArrayOfByte);
}
