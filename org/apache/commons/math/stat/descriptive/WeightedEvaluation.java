package org.apache.commons.math.stat.descriptive;

public abstract interface WeightedEvaluation
{
  public abstract double evaluate(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2);
  
  public abstract double evaluate(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, int paramInt1, int paramInt2);
}
