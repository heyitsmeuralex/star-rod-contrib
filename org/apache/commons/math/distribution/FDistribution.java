package org.apache.commons.math.distribution;

public abstract interface FDistribution
  extends ContinuousDistribution
{
  @Deprecated
  public abstract void setNumeratorDegreesOfFreedom(double paramDouble);
  
  public abstract double getNumeratorDegreesOfFreedom();
  
  @Deprecated
  public abstract void setDenominatorDegreesOfFreedom(double paramDouble);
  
  public abstract double getDenominatorDegreesOfFreedom();
}
