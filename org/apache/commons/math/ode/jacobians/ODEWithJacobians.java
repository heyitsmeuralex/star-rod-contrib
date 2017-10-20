package org.apache.commons.math.ode.jacobians;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.FirstOrderDifferentialEquations;

@Deprecated
public abstract interface ODEWithJacobians
  extends FirstOrderDifferentialEquations
{
  public abstract int getParametersDimension();
  
  public abstract void computeJacobians(double paramDouble, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[][] paramArrayOfDouble3, double[][] paramArrayOfDouble4)
    throws DerivativeException;
}
