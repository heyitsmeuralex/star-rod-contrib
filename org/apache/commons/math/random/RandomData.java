package org.apache.commons.math.random;

import java.util.Collection;

public abstract interface RandomData
{
  public abstract String nextHexString(int paramInt);
  
  public abstract int nextInt(int paramInt1, int paramInt2);
  
  public abstract long nextLong(long paramLong1, long paramLong2);
  
  public abstract String nextSecureHexString(int paramInt);
  
  public abstract int nextSecureInt(int paramInt1, int paramInt2);
  
  public abstract long nextSecureLong(long paramLong1, long paramLong2);
  
  public abstract long nextPoisson(double paramDouble);
  
  public abstract double nextGaussian(double paramDouble1, double paramDouble2);
  
  public abstract double nextExponential(double paramDouble);
  
  public abstract double nextUniform(double paramDouble1, double paramDouble2);
  
  public abstract int[] nextPermutation(int paramInt1, int paramInt2);
  
  public abstract Object[] nextSample(Collection<?> paramCollection, int paramInt);
}
