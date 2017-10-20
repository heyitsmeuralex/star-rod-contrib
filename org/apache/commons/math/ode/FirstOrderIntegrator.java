package org.apache.commons.math.ode;

public abstract interface FirstOrderIntegrator
  extends ODEIntegrator
{
  public abstract double integrate(FirstOrderDifferentialEquations paramFirstOrderDifferentialEquations, double paramDouble1, double[] paramArrayOfDouble1, double paramDouble2, double[] paramArrayOfDouble2)
    throws DerivativeException, IntegratorException;
}
