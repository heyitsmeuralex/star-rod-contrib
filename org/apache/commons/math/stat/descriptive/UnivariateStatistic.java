package org.apache.commons.math.stat.descriptive;

public abstract interface UnivariateStatistic
{
  public abstract double evaluate(double[] paramArrayOfDouble);
  
  public abstract double evaluate(double[] paramArrayOfDouble, int paramInt1, int paramInt2);
  
  public abstract UnivariateStatistic copy();
}
