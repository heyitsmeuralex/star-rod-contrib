package org.apache.commons.math.ode;

public abstract interface SecondOrderDifferentialEquations
{
  public abstract int getDimension();
  
  public abstract void computeSecondDerivatives(double paramDouble, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3)
    throws DerivativeException;
}
