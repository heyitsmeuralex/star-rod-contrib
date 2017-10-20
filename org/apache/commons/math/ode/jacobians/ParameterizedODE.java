package org.apache.commons.math.ode.jacobians;

import org.apache.commons.math.ode.FirstOrderDifferentialEquations;

@Deprecated
public abstract interface ParameterizedODE
  extends FirstOrderDifferentialEquations
{
  public abstract int getParametersDimension();
  
  public abstract void setParameter(int paramInt, double paramDouble);
}
