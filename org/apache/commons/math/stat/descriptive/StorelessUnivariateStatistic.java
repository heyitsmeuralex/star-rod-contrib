package org.apache.commons.math.stat.descriptive;

public abstract interface StorelessUnivariateStatistic
  extends UnivariateStatistic
{
  public abstract void increment(double paramDouble);
  
  public abstract void incrementAll(double[] paramArrayOfDouble);
  
  public abstract void incrementAll(double[] paramArrayOfDouble, int paramInt1, int paramInt2);
  
  public abstract double getResult();
  
  public abstract long getN();
  
  public abstract void clear();
  
  public abstract StorelessUnivariateStatistic copy();
}
