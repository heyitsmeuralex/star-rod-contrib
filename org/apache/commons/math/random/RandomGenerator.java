package org.apache.commons.math.random;

public abstract interface RandomGenerator
{
  public abstract void setSeed(int paramInt);
  
  public abstract void setSeed(int[] paramArrayOfInt);
  
  public abstract void setSeed(long paramLong);
  
  public abstract void nextBytes(byte[] paramArrayOfByte);
  
  public abstract int nextInt();
  
  public abstract int nextInt(int paramInt);
  
  public abstract long nextLong();
  
  public abstract boolean nextBoolean();
  
  public abstract float nextFloat();
  
  public abstract double nextDouble();
  
  public abstract double nextGaussian();
}
