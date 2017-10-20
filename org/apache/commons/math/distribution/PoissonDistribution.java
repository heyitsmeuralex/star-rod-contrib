package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;

public abstract interface PoissonDistribution
  extends IntegerDistribution
{
  public abstract double getMean();
  
  @Deprecated
  public abstract void setMean(double paramDouble);
  
  public abstract double normalApproximateProbability(int paramInt)
    throws MathException;
}
