package org.apache.commons.math.distribution;

public abstract interface PascalDistribution
  extends IntegerDistribution
{
  public abstract int getNumberOfSuccesses();
  
  public abstract double getProbabilityOfSuccess();
  
  @Deprecated
  public abstract void setNumberOfSuccesses(int paramInt);
  
  @Deprecated
  public abstract void setProbabilityOfSuccess(double paramDouble);
}
