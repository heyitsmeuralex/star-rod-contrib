package org.apache.commons.math.ode.events;

public abstract interface EventHandler
{
  public static final int STOP = 0;
  public static final int RESET_STATE = 1;
  public static final int RESET_DERIVATIVES = 2;
  public static final int CONTINUE = 3;
  
  public abstract double g(double paramDouble, double[] paramArrayOfDouble)
    throws EventException;
  
  public abstract int eventOccurred(double paramDouble, double[] paramArrayOfDouble, boolean paramBoolean)
    throws EventException;
  
  public abstract void resetState(double paramDouble, double[] paramArrayOfDouble)
    throws EventException;
}
