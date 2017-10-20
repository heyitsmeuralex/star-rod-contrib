package org.apache.commons.math.distribution;

public abstract interface WeibullDistribution
  extends ContinuousDistribution
{
  public abstract double getShape();
  
  public abstract double getScale();
  
  @Deprecated
  public abstract void setShape(double paramDouble);
  
  @Deprecated
  public abstract void setScale(double paramDouble);
}
