package org.apache.commons.math.random;

import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.util.FastMath;
























public abstract class BitsStreamGenerator
  implements RandomGenerator
{
  private double nextGaussian;
  
  public BitsStreamGenerator()
  {
    nextGaussian = NaN.0D;
  }
  



  public abstract void setSeed(int paramInt);
  



  public abstract void setSeed(int[] paramArrayOfInt);
  


  public abstract void setSeed(long paramLong);
  


  protected abstract int next(int paramInt);
  


  public boolean nextBoolean()
  {
    return next(1) != 0;
  }
  
  public void nextBytes(byte[] bytes)
  {
    int i = 0;
    int iEnd = bytes.length - 3;
    while (i < iEnd) {
      int random = next(32);
      bytes[i] = ((byte)(random & 0xFF));
      bytes[(i + 1)] = ((byte)(random >> 8 & 0xFF));
      bytes[(i + 2)] = ((byte)(random >> 16 & 0xFF));
      bytes[(i + 3)] = ((byte)(random >> 24 & 0xFF));
      i += 4;
    }
    int random = next(32);
    while (i < bytes.length) {
      bytes[(i++)] = ((byte)(random & 0xFF));
      random >>= 8;
    }
  }
  
  public double nextDouble()
  {
    long high = next(26) << 26;
    int low = next(26);
    return (high | low) * 2.220446049250313E-16D;
  }
  
  public float nextFloat()
  {
    return next(23) * 1.1920929E-7F;
  }
  

  public double nextGaussian()
  {
    double random;
    if (Double.isNaN(nextGaussian))
    {
      double x = nextDouble();
      double y = nextDouble();
      double alpha = 6.283185307179586D * x;
      double r = FastMath.sqrt(-2.0D * FastMath.log(y));
      double random = r * FastMath.cos(alpha);
      nextGaussian = (r * FastMath.sin(alpha));
    }
    else {
      random = nextGaussian;
      nextGaussian = NaN.0D;
    }
    
    return random;
  }
  

  public int nextInt()
  {
    return next(32);
  }
  
  public int nextInt(int n)
    throws IllegalArgumentException
  {
    if (n < 1) {
      throw new NotStrictlyPositiveException(Integer.valueOf(n));
    }
    

    int mask = n;
    mask |= mask >> 1;
    mask |= mask >> 2;
    mask |= mask >> 4;
    mask |= mask >> 8;
    mask |= mask >> 16;
    for (;;)
    {
      int random = next(32) & mask;
      if (random < n) {
        return random;
      }
    }
  }
  

  public long nextLong()
  {
    long high = next(32) << 32;
    long low = next(32) & 0xFFFFFFFF;
    return high | low;
  }
}
