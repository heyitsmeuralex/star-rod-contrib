package org.apache.commons.math.ode.nonstiff;









































public class MidpointIntegrator
  extends RungeKuttaIntegrator
{
  private static final double[] STATIC_C = { 0.5D };
  



  private static final double[][] STATIC_A = { { 0.5D } };
  



  private static final double[] STATIC_B = { 0.0D, 1.0D };
  





  public MidpointIntegrator(double step)
  {
    super("midpoint", STATIC_C, STATIC_A, STATIC_B, new MidpointStepInterpolator(), step);
  }
}
