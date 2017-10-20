package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.sampling.StepInterpolator;














































class HighamHall54StepInterpolator
  extends RungeKuttaStepInterpolator
{
  private static final long serialVersionUID = -3583240427587318654L;
  
  public HighamHall54StepInterpolator() {}
  
  public HighamHall54StepInterpolator(HighamHall54StepInterpolator interpolator)
  {
    super(interpolator);
  }
  

  protected StepInterpolator doCopy()
  {
    return new HighamHall54StepInterpolator(this);
  }
  




  protected void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH)
    throws DerivativeException
  {
    double theta2 = theta * theta;
    
    double b0 = h * (-0.08333333333333333D + theta * (1.0D + theta * (-3.75D + theta * (5.333333333333333D + theta * -5.0D / 2.0D))));
    double b2 = h * (-0.84375D + theta2 * (14.34375D + theta * (-30.375D + theta * 135.0D / 8.0D)));
    double b3 = h * (1.3333333333333333D + theta2 * (-22.0D + theta * (50.666666666666664D + theta * -30.0D)));
    double b4 = h * (-1.3020833333333333D + theta2 * (11.71875D + theta * (-26.041666666666668D + theta * 125.0D / 8.0D)));
    double b5 = h * (-0.10416666666666667D + theta2 * (-0.3125D + theta * 5.0D / 12.0D));
    double bDot0 = 1.0D + theta * (-7.5D + theta * (16.0D - 10.0D * theta));
    double bDot2 = theta * (28.6875D + theta * (-91.125D + 67.5D * theta));
    double bDot3 = theta * (-44.0D + theta * (152.0D - 120.0D * theta));
    double bDot4 = theta * (23.4375D + theta * (-78.125D + 62.5D * theta));
    double bDot5 = theta * 5.0D / 8.0D * (2.0D * theta - 1.0D);
    
    for (int i = 0; i < interpolatedState.length; i++) {
      double yDot0 = yDotK[0][i];
      double yDot2 = yDotK[2][i];
      double yDot3 = yDotK[3][i];
      double yDot4 = yDotK[4][i];
      double yDot5 = yDotK[5][i];
      interpolatedState[i] = (currentState[i] + b0 * yDot0 + b2 * yDot2 + b3 * yDot3 + b4 * yDot4 + b5 * yDot5);
      
      interpolatedDerivatives[i] = (bDot0 * yDot0 + bDot2 * yDot2 + bDot3 * yDot3 + bDot4 * yDot4 + bDot5 * yDot5);
    }
  }
}
