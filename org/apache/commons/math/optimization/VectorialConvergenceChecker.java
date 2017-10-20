package org.apache.commons.math.optimization;

public abstract interface VectorialConvergenceChecker
{
  public abstract boolean converged(int paramInt, VectorialPointValuePair paramVectorialPointValuePair1, VectorialPointValuePair paramVectorialPointValuePair2);
}
