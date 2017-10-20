package org.apache.commons.math.ode.sampling;

import org.apache.commons.math.ode.DerivativeException;

public abstract interface FixedStepHandler
{
  public abstract void handleStep(double paramDouble, double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, boolean paramBoolean)
    throws DerivativeException;
}
