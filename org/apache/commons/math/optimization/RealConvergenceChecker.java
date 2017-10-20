package org.apache.commons.math.optimization;

public abstract interface RealConvergenceChecker
{
  public abstract boolean converged(int paramInt, RealPointValuePair paramRealPointValuePair1, RealPointValuePair paramRealPointValuePair2);
}
