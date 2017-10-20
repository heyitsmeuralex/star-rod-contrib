package org.apache.commons.math.linear;

public abstract interface LUDecomposition
{
  public abstract RealMatrix getL();
  
  public abstract RealMatrix getU();
  
  public abstract RealMatrix getP();
  
  public abstract int[] getPivot();
  
  public abstract double getDeterminant();
  
  public abstract DecompositionSolver getSolver();
}
