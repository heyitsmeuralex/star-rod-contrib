package org.apache.commons.math.random;

import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.util.FastMath;





































public abstract class AbstractRandomGenerator
  implements RandomGenerator
{
  private double cachedNormalDeviate = NaN.0D;
  






  public AbstractRandomGenerator() {}
  





  public void clear()
  {
    cachedNormalDeviate = NaN.0D;
  }
  
  public void setSeed(int seed)
  {
    setSeed(seed);
  }
  

  public void setSeed(int[] seed)
  {
    long prime = 4294967291L;
    
    long combined = 0L;
    for (int s : seed) {
      combined = combined * 4294967291L + s;
    }
    setSeed(combined);
  }
  











  public abstract void setSeed(long paramLong);
  










  public void nextBytes(byte[] bytes)
  {
    int bytesOut = 0;
    while (bytesOut < bytes.length) {
      int randInt = nextInt();
      for (int i = 0; i < 3; i++) {
        if (i > 0) {
          randInt >>= 8;
        }
        bytes[(bytesOut++)] = ((byte)randInt);
        if (bytesOut == bytes.length) {
          return;
        }
      }
    }
  }
  













  public int nextInt()
  {
    return (int)(nextDouble() * 2.147483647E9D);
  }
  















  public int nextInt(int n)
  {
    if (n <= 0) {
      throw new NotStrictlyPositiveException(Integer.valueOf(n));
    }
    int result = (int)(nextDouble() * n);
    return result < n ? result : n - 1;
  }
  













  public long nextLong()
  {
    return (nextDouble() * 9.223372036854776E18D);
  }
  













  public boolean nextBoolean()
  {
    return nextDouble() <= 0.5D;
  }
  













  public float nextFloat()
  {
    return (float)nextDouble();
  }
  
















  public abstract double nextDouble();
  















  public double nextGaussian()
  {
    if (!Double.isNaN(cachedNormalDeviate)) {
      double dev = cachedNormalDeviate;
      cachedNormalDeviate = NaN.0D;
      return dev;
    }
    double v1 = 0.0D;
    double v2 = 0.0D;
    double s = 1.0D;
    while (s >= 1.0D) {
      v1 = 2.0D * nextDouble() - 1.0D;
      v2 = 2.0D * nextDouble() - 1.0D;
      s = v1 * v1 + v2 * v2;
    }
    if (s != 0.0D) {
      s = FastMath.sqrt(-2.0D * FastMath.log(s) / s);
    }
    cachedNormalDeviate = (v2 * s);
    return v1 * s;
  }
}
