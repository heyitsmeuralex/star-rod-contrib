package org.apache.commons.math.ode;

public abstract interface FirstOrderDifferentialEquations
{
  public abstract int getDimension();
  
  public abstract void computeDerivatives(double paramDouble, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
    throws DerivativeException;
}
