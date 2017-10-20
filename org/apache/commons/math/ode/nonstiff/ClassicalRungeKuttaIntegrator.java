package org.apache.commons.math.ode.nonstiff;












































public class ClassicalRungeKuttaIntegrator
  extends RungeKuttaIntegrator
{
  private static final double[] STATIC_C = { 0.5D, 0.5D, 1.0D };
  



  private static final double[][] STATIC_A = { { 0.5D }, { 0.0D, 0.5D }, { 0.0D, 0.0D, 1.0D } };
  





  private static final double[] STATIC_B = { 0.16666666666666666D, 0.3333333333333333D, 0.3333333333333333D, 0.16666666666666666D };
  






  public ClassicalRungeKuttaIntegrator(double step)
  {
    super("classical Runge-Kutta", STATIC_C, STATIC_A, STATIC_B, new ClassicalRungeKuttaStepInterpolator(), step);
  }
}
