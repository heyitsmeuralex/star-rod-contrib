package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.apache.commons.math.util.FastMath;











































class GillStepInterpolator
  extends RungeKuttaStepInterpolator
{
  private static final double TWO_MINUS_SQRT_2 = 2.0D - FastMath.sqrt(2.0D);
  

  private static final double TWO_PLUS_SQRT_2 = 2.0D + FastMath.sqrt(2.0D);
  





  private static final long serialVersionUID = -107804074496313322L;
  






  public GillStepInterpolator() {}
  






  public GillStepInterpolator(GillStepInterpolator interpolator)
  {
    super(interpolator);
  }
  

  protected StepInterpolator doCopy()
  {
    return new GillStepInterpolator(this);
  }
  




  protected void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH)
    throws DerivativeException
  {
    double twoTheta = 2.0D * theta;
    double fourTheta = 4.0D * theta;
    double s = oneMinusThetaH / 6.0D;
    double oMt = 1.0D - theta;
    double soMt = s * oMt;
    double c23 = soMt * (1.0D + twoTheta);
    double coeff1 = soMt * (1.0D - fourTheta);
    double coeff2 = c23 * TWO_MINUS_SQRT_2;
    double coeff3 = c23 * TWO_PLUS_SQRT_2;
    double coeff4 = s * (1.0D + theta * (1.0D + fourTheta));
    double coeffDot1 = theta * (twoTheta - 3.0D) + 1.0D;
    double cDot23 = theta * oMt;
    double coeffDot2 = cDot23 * TWO_MINUS_SQRT_2;
    double coeffDot3 = cDot23 * TWO_PLUS_SQRT_2;
    double coeffDot4 = theta * (twoTheta - 1.0D);
    
    for (int i = 0; i < interpolatedState.length; i++) {
      double yDot1 = yDotK[0][i];
      double yDot2 = yDotK[1][i];
      double yDot3 = yDotK[2][i];
      double yDot4 = yDotK[3][i];
      interpolatedState[i] = (currentState[i] - coeff1 * yDot1 - coeff2 * yDot2 - coeff3 * yDot3 - coeff4 * yDot4);
      
      interpolatedDerivatives[i] = (coeffDot1 * yDot1 + coeffDot2 * yDot2 + coeffDot3 * yDot3 + coeffDot4 * yDot4);
    }
  }
}
