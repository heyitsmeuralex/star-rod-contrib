package org.apache.commons.math.ode.nonstiff;















































public class EulerIntegrator
  extends RungeKuttaIntegrator
{
  private static final double[] STATIC_C = new double[0];
  


  private static final double[][] STATIC_A = new double[0][];
  


  private static final double[] STATIC_B = { 1.0D };
  





  public EulerIntegrator(double step)
  {
    super("Euler", STATIC_C, STATIC_A, STATIC_B, new EulerStepInterpolator(), step);
  }
}
