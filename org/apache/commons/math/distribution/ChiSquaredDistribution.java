package org.apache.commons.math.distribution;

public abstract interface ChiSquaredDistribution
  extends ContinuousDistribution, HasDensity<Double>
{
  @Deprecated
  public abstract void setDegreesOfFreedom(double paramDouble);
  
  public abstract double getDegreesOfFreedom();
  
  public abstract double density(Double paramDouble);
}
