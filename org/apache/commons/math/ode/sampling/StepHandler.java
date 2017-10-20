package org.apache.commons.math.ode.sampling;

import org.apache.commons.math.ode.DerivativeException;

public abstract interface StepHandler
{
  public abstract boolean requiresDenseOutput();
  
  public abstract void reset();
  
  public abstract void handleStep(StepInterpolator paramStepInterpolator, boolean paramBoolean)
    throws DerivativeException;
}
