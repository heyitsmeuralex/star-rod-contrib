package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;

public abstract interface IntegerDistribution
  extends DiscreteDistribution
{
  public abstract double probability(int paramInt);
  
  public abstract double cumulativeProbability(int paramInt)
    throws MathException;
  
  public abstract double cumulativeProbability(int paramInt1, int paramInt2)
    throws MathException;
  
  public abstract int inverseCumulativeProbability(double paramDouble)
    throws MathException;
}
