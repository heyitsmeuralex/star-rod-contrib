package org.apache.commons.math.linear;

public abstract interface QRDecomposition
{
  public abstract RealMatrix getR();
  
  public abstract RealMatrix getQ();
  
  public abstract RealMatrix getQT();
  
  public abstract RealMatrix getH();
  
  public abstract DecompositionSolver getSolver();
}
