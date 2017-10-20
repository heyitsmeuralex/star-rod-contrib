package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.sampling.StepInterpolator;





























































class ThreeEighthesStepInterpolator
  extends RungeKuttaStepInterpolator
{
  private static final long serialVersionUID = -3345024435978721931L;
  
  public ThreeEighthesStepInterpolator() {}
  
  public ThreeEighthesStepInterpolator(ThreeEighthesStepInterpolator interpolator)
  {
    super(interpolator);
  }
  

  protected StepInterpolator doCopy()
  {
    return new ThreeEighthesStepInterpolator(this);
  }
  




  protected void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH)
    throws DerivativeException
  {
    double fourTheta2 = 4.0D * theta * theta;
    double s = oneMinusThetaH / 8.0D;
    double coeff1 = s * (1.0D - 7.0D * theta + 2.0D * fourTheta2);
    double coeff2 = 3.0D * s * (1.0D + theta - fourTheta2);
    double coeff3 = 3.0D * s * (1.0D + theta);
    double coeff4 = s * (1.0D + theta + fourTheta2);
    double coeffDot3 = 0.75D * theta;
    double coeffDot1 = coeffDot3 * (4.0D * theta - 5.0D) + 1.0D;
    double coeffDot2 = coeffDot3 * (5.0D - 6.0D * theta);
    double coeffDot4 = coeffDot3 * (2.0D * theta - 1.0D);
    
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
