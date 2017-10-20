package org.apache.commons.math.linear;

public abstract interface CholeskyDecomposition
{
  public abstract RealMatrix getL();
  
  public abstract RealMatrix getLT();
  
  public abstract double getDeterminant();
  
  public abstract DecompositionSolver getSolver();
}
