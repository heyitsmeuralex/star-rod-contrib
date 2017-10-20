package org.apache.commons.math.distribution;

public abstract interface NormalDistribution
  extends ContinuousDistribution, HasDensity<Double>
{
  public abstract double getMean();
  
  @Deprecated
  public abstract void setMean(double paramDouble);
  
  public abstract double getStandardDeviation();
  
  @Deprecated
  public abstract void setStandardDeviation(double paramDouble);
  
  public abstract double density(Double paramDouble);
}
