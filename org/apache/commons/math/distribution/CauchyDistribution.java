package org.apache.commons.math.distribution;

public abstract interface CauchyDistribution
  extends ContinuousDistribution
{
  public abstract double getMedian();
  
  public abstract double getScale();
  
  @Deprecated
  public abstract void setMedian(double paramDouble);
  
  @Deprecated
  public abstract void setScale(double paramDouble);
}
