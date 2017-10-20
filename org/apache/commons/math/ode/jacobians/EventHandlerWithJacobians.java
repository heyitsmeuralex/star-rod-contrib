package org.apache.commons.math.ode.jacobians;

import org.apache.commons.math.ode.events.EventException;

@Deprecated
public abstract interface EventHandlerWithJacobians
{
  public static final int STOP = 0;
  public static final int RESET_STATE = 1;
  public static final int RESET_DERIVATIVES = 2;
  public static final int CONTINUE = 3;
  
  public abstract double g(double paramDouble, double[] paramArrayOfDouble, double[][] paramArrayOfDouble1, double[][] paramArrayOfDouble2)
    throws EventException;
  
  public abstract int eventOccurred(double paramDouble, double[] paramArrayOfDouble, double[][] paramArrayOfDouble1, double[][] paramArrayOfDouble2, boolean paramBoolean)
    throws EventException;
  
  public abstract void resetState(double paramDouble, double[] paramArrayOfDouble, double[][] paramArrayOfDouble1, double[][] paramArrayOfDouble2)
    throws EventException;
}
