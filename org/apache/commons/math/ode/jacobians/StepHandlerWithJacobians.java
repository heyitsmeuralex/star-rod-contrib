package org.apache.commons.math.ode.jacobians;

import org.apache.commons.math.ode.DerivativeException;

@Deprecated
public abstract interface StepHandlerWithJacobians
{
  public abstract boolean requiresDenseOutput();
  
  public abstract void reset();
  
  public abstract void handleStep(StepInterpolatorWithJacobians paramStepInterpolatorWithJacobians, boolean paramBoolean)
    throws DerivativeException;
}
