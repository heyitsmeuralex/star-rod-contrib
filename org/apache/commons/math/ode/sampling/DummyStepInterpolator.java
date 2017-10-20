package org.apache.commons.math.ode.sampling;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;













































public class DummyStepInterpolator
  extends AbstractStepInterpolator
{
  private static final long serialVersionUID = 1708010296707839488L;
  private double[] currentDerivative;
  
  public DummyStepInterpolator()
  {
    currentDerivative = null;
  }
  






  public DummyStepInterpolator(double[] y, double[] yDot, boolean forward)
  {
    super(y, forward);
    currentDerivative = yDot;
  }
  




  public DummyStepInterpolator(DummyStepInterpolator interpolator)
  {
    super(interpolator);
    currentDerivative = ((double[])currentDerivative.clone());
  }
  



  protected StepInterpolator doCopy()
  {
    return new DummyStepInterpolator(this);
  }
  








  protected void computeInterpolatedStateAndDerivatives(double theta, double oneMinusThetaH)
  {
    System.arraycopy(currentState, 0, interpolatedState, 0, currentState.length);
    System.arraycopy(currentDerivative, 0, interpolatedDerivatives, 0, currentDerivative.length);
  }
  






  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    writeBaseExternal(out);
    
    if (currentDerivative != null) {
      for (int i = 0; i < currentDerivative.length; i++) {
        out.writeDouble(currentDerivative[i]);
      }
    }
  }
  







  public void readExternal(ObjectInput in)
    throws IOException
  {
    double t = readBaseExternal(in);
    
    if (currentState == null) {
      currentDerivative = null;
    } else {
      currentDerivative = new double[currentState.length];
      for (int i = 0; i < currentDerivative.length; i++) {
        currentDerivative[i] = in.readDouble();
      }
    }
    

    setInterpolatedTime(t);
  }
}
