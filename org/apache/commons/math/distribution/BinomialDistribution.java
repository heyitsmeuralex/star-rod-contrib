package org.apache.commons.math.distribution;

public abstract interface BinomialDistribution
  extends IntegerDistribution
{
  public abstract int getNumberOfTrials();
  
  public abstract double getProbabilityOfSuccess();
  
  @Deprecated
  public abstract void setNumberOfTrials(int paramInt);
  
  @Deprecated
  public abstract void setProbabilityOfSuccess(double paramDouble);
}
