package org.apache.commons.math.distribution;

public abstract interface ExponentialDistribution
  extends ContinuousDistribution, HasDensity<Double>
{
  @Deprecated
  public abstract void setMean(double paramDouble);
  
  public abstract double getMean();
  
  public abstract double density(Double paramDouble);
}
