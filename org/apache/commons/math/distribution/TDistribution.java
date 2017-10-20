package org.apache.commons.math.distribution;

public abstract interface TDistribution
  extends ContinuousDistribution
{
  @Deprecated
  public abstract void setDegreesOfFreedom(double paramDouble);
  
  public abstract double getDegreesOfFreedom();
}
