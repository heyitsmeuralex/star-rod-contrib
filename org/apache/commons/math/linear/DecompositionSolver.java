package org.apache.commons.math.linear;

public abstract interface DecompositionSolver
{
  public abstract double[] solve(double[] paramArrayOfDouble)
    throws IllegalArgumentException, InvalidMatrixException;
  
  public abstract RealVector solve(RealVector paramRealVector)
    throws IllegalArgumentException, InvalidMatrixException;
  
  public abstract RealMatrix solve(RealMatrix paramRealMatrix)
    throws IllegalArgumentException, InvalidMatrixException;
  
  public abstract boolean isNonSingular();
  
  public abstract RealMatrix getInverse()
    throws InvalidMatrixException;
}
