package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.util.FastMath;



































public class HighamHall54Integrator
  extends EmbeddedRungeKuttaIntegrator
{
  private static final String METHOD_NAME = "Higham-Hall 5(4)";
  private static final double[] STATIC_C = { 0.2222222222222222D, 0.3333333333333333D, 0.5D, 0.6D, 1.0D, 1.0D };
  



  private static final double[][] STATIC_A = { { 0.2222222222222222D }, { 0.08333333333333333D, 0.25D }, { 0.125D, 0.0D, 0.375D }, { 0.182D, -0.27D, 0.624D, 0.064D }, { -0.55D, 1.35D, 2.4D, -7.2D, 5.0D }, { 0.08333333333333333D, 0.0D, 0.84375D, -1.3333333333333333D, 1.3020833333333333D, 0.10416666666666667D } };
  








  private static final double[] STATIC_B = { 0.08333333333333333D, 0.0D, 0.84375D, -1.3333333333333333D, 1.3020833333333333D, 0.10416666666666667D, 0.0D };
  



  private static final double[] STATIC_E = { -0.05D, 0.0D, 0.50625D, -1.2D, 0.78125D, 0.0625D, -0.1D };
  












  public HighamHall54Integrator(double minStep, double maxStep, double scalAbsoluteTolerance, double scalRelativeTolerance)
  {
    super("Higham-Hall 5(4)", false, STATIC_C, STATIC_A, STATIC_B, new HighamHall54StepInterpolator(), minStep, maxStep, scalAbsoluteTolerance, scalRelativeTolerance);
  }
  











  public HighamHall54Integrator(double minStep, double maxStep, double[] vecAbsoluteTolerance, double[] vecRelativeTolerance)
  {
    super("Higham-Hall 5(4)", false, STATIC_C, STATIC_A, STATIC_B, new HighamHall54StepInterpolator(), minStep, maxStep, vecAbsoluteTolerance, vecRelativeTolerance);
  }
  


  public int getOrder()
  {
    return 5;
  }
  




  protected double estimateError(double[][] yDotK, double[] y0, double[] y1, double h)
  {
    double error = 0.0D;
    
    for (int j = 0; j < mainSetDimension; j++) {
      double errSum = STATIC_E[0] * yDotK[0][j];
      for (int l = 1; l < STATIC_E.length; l++) {
        errSum += STATIC_E[l] * yDotK[l][j];
      }
      
      double yScale = FastMath.max(FastMath.abs(y0[j]), FastMath.abs(y1[j]));
      double tol = vecAbsoluteTolerance == null ? scalAbsoluteTolerance + scalRelativeTolerance * yScale : vecAbsoluteTolerance[j] + vecRelativeTolerance[j] * yScale;
      

      double ratio = h * errSum / tol;
      error += ratio * ratio;
    }
    

    return FastMath.sqrt(error / mainSetDimension);
  }
}
