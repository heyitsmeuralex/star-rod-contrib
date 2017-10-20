package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;

public abstract interface Distribution
{
  public abstract double cumulativeProbability(double paramDouble)
    throws MathException;
  
  public abstract double cumulativeProbability(double paramDouble1, double paramDouble2)
    throws MathException;
}
