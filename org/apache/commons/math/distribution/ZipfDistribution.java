package org.apache.commons.math.distribution;

public abstract interface ZipfDistribution
  extends IntegerDistribution
{
  public abstract int getNumberOfElements();
  
  @Deprecated
  public abstract void setNumberOfElements(int paramInt);
  
  public abstract double getExponent();
  
  @Deprecated
  public abstract void setExponent(double paramDouble);
}
