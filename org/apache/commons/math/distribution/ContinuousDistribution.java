package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;

public abstract interface ContinuousDistribution
  extends Distribution
{
  public abstract double inverseCumulativeProbability(double paramDouble)
    throws MathException;
}
