package org.apache.commons.math.ode.nonstiff;

import org.apache.commons.math.ode.DerivativeException;
import org.apache.commons.math.ode.sampling.StepInterpolator;



























































class ClassicalRungeKuttaStepInterpolator
  extends RungeKuttaStepInterpolator
{
  private static final long serialVersionUID = -6576285612589783992L;
  
  public ClassicalRungeKuttaStepInterpolator() {}
  
  public ClassicalRungeKuttaStepInterpolator(ClassicalRungeKuttaStepInterpolator interpolator)
  {
    super(interpolator);
  }
  

  protected StepInterpolator doCopy()
  {
    return new ClassicalRungeKuttaStepInterpolator(this);
  }
  



  protected void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH)
    throws DerivativeException
  {
    double fourTheta = 4.0D * theta;
    double oneMinusTheta = 1.0D - theta;
    double oneMinus2Theta = 1.0D - 2.0D * theta;
    double s = oneMinusThetaH / 6.0D;
    double coeff1 = s * ((-fourTheta + 5.0D) * theta - 1.0D);
    double coeff23 = s * ((fourTheta - 2.0D) * theta - 2.0D);
    double coeff4 = s * ((-fourTheta - 1.0D) * theta - 1.0D);
    double coeffDot1 = oneMinusTheta * oneMinus2Theta;
    double coeffDot23 = 2.0D * theta * oneMinusTheta;
    double coeffDot4 = -theta * oneMinus2Theta;
    for (int i = 0; i < interpolatedState.length; i++) {
      double yDot1 = yDotK[0][i];
      double yDot23 = yDotK[1][i] + yDotK[2][i];
      double yDot4 = yDotK[3][i];
      interpolatedState[i] = (currentState[i] + coeff1 * yDot1 + coeff23 * yDot23 + coeff4 * yDot4);
      
      interpolatedDerivatives[i] = (coeffDot1 * yDot1 + coeffDot23 * yDot23 + coeffDot4 * yDot4);
    }
  }
}
