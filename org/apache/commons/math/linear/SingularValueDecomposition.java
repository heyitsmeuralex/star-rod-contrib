package org.apache.commons.math.linear;

public abstract interface SingularValueDecomposition
{
  public abstract RealMatrix getU();
  
  public abstract RealMatrix getUT();
  
  public abstract RealMatrix getS();
  
  public abstract double[] getSingularValues();
  
  public abstract RealMatrix getV();
  
  public abstract RealMatrix getVT();
  
  public abstract RealMatrix getCovariance(double paramDouble)
    throws IllegalArgumentException;
  
  public abstract double getNorm();
  
  public abstract double getConditionNumber();
  
  public abstract int getRank();
  
  public abstract DecompositionSolver getSolver();
}
