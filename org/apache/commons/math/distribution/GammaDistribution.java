package org.apache.commons.math.distribution;

public abstract interface GammaDistribution
  extends ContinuousDistribution, HasDensity<Double>
{
  @Deprecated
  public abstract void setAlpha(double paramDouble);
  
  public abstract double getAlpha();
  
  @Deprecated
  public abstract void setBeta(double paramDouble);
  
  public abstract double getBeta();
  
  public abstract double density(Double paramDouble);
}
