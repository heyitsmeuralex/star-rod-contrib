package org.apache.commons.math.ode;

public abstract interface SecondOrderIntegrator
  extends ODEIntegrator
{
  public abstract void integrate(SecondOrderDifferentialEquations paramSecondOrderDifferentialEquations, double paramDouble1, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double paramDouble2, double[] paramArrayOfDouble3, double[] paramArrayOfDouble4)
    throws DerivativeException, IntegratorException;
}
