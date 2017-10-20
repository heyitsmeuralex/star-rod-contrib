package org.apache.commons.math.ode.sampling;

import java.io.Externalizable;
import org.apache.commons.math.ode.DerivativeException;

public abstract interface StepInterpolator
  extends Externalizable
{
  public abstract double getPreviousTime();
  
  public abstract double getCurrentTime();
  
  public abstract double getInterpolatedTime();
  
  public abstract void setInterpolatedTime(double paramDouble);
  
  public abstract double[] getInterpolatedState()
    throws DerivativeException;
  
  public abstract double[] getInterpolatedDerivatives()
    throws DerivativeException;
  
  public abstract boolean isForward();
  
  public abstract StepInterpolator copy()
    throws DerivativeException;
}
