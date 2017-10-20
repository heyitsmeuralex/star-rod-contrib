package org.apache.commons.math.distribution;

public abstract interface DiscreteDistribution
  extends Distribution
{
  public abstract double probability(double paramDouble);
}
