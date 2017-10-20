package org.apache.commons.math.ode.sampling;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.apache.commons.math.ode.DerivativeException;






































































public abstract class AbstractStepInterpolator
  implements StepInterpolator
{
  protected double h;
  protected double[] currentState;
  protected double interpolatedTime;
  protected double[] interpolatedState;
  protected double[] interpolatedDerivatives;
  private double globalPreviousTime;
  private double globalCurrentTime;
  private double softPreviousTime;
  private double softCurrentTime;
  private boolean finalized;
  private boolean forward;
  private boolean dirtyState;
  
  protected AbstractStepInterpolator()
  {
    globalPreviousTime = NaN.0D;
    globalCurrentTime = NaN.0D;
    softPreviousTime = NaN.0D;
    softCurrentTime = NaN.0D;
    h = NaN.0D;
    interpolatedTime = NaN.0D;
    currentState = null;
    interpolatedState = null;
    interpolatedDerivatives = null;
    finalized = false;
    forward = true;
    dirtyState = true;
  }
  





  protected AbstractStepInterpolator(double[] y, boolean forward)
  {
    globalPreviousTime = NaN.0D;
    globalCurrentTime = NaN.0D;
    softPreviousTime = NaN.0D;
    softCurrentTime = NaN.0D;
    h = NaN.0D;
    interpolatedTime = NaN.0D;
    
    currentState = y;
    interpolatedState = new double[y.length];
    interpolatedDerivatives = new double[y.length];
    
    finalized = false;
    this.forward = forward;
    dirtyState = true;
  }
  


















  protected AbstractStepInterpolator(AbstractStepInterpolator interpolator)
  {
    globalPreviousTime = globalPreviousTime;
    globalCurrentTime = globalCurrentTime;
    softPreviousTime = softPreviousTime;
    softCurrentTime = softCurrentTime;
    h = h;
    interpolatedTime = interpolatedTime;
    
    if (currentState != null) {
      currentState = ((double[])currentState.clone());
      interpolatedState = ((double[])interpolatedState.clone());
      interpolatedDerivatives = ((double[])interpolatedDerivatives.clone());
    } else {
      currentState = null;
      interpolatedState = null;
      interpolatedDerivatives = null;
    }
    
    finalized = finalized;
    forward = forward;
    dirtyState = dirtyState;
  }
  






  protected void reinitialize(double[] y, boolean isForward)
  {
    globalPreviousTime = NaN.0D;
    globalCurrentTime = NaN.0D;
    softPreviousTime = NaN.0D;
    softCurrentTime = NaN.0D;
    h = NaN.0D;
    interpolatedTime = NaN.0D;
    
    currentState = y;
    interpolatedState = new double[y.length];
    interpolatedDerivatives = new double[y.length];
    
    finalized = false;
    forward = isForward;
    dirtyState = true;
  }
  


  public StepInterpolator copy()
    throws DerivativeException
  {
    finalizeStep();
    

    return doCopy();
  }
  






  protected abstract StepInterpolator doCopy();
  





  public void shift()
  {
    globalPreviousTime = globalCurrentTime;
    softPreviousTime = globalPreviousTime;
    softCurrentTime = globalCurrentTime;
  }
  



  public void storeTime(double t)
  {
    globalCurrentTime = t;
    softCurrentTime = globalCurrentTime;
    h = (globalCurrentTime - globalPreviousTime);
    setInterpolatedTime(t);
    

    finalized = false;
  }
  










  public void setSoftPreviousTime(double softPreviousTime)
  {
    this.softPreviousTime = softPreviousTime;
  }
  









  public void setSoftCurrentTime(double softCurrentTime)
  {
    this.softCurrentTime = softCurrentTime;
  }
  




  public double getGlobalPreviousTime()
  {
    return globalPreviousTime;
  }
  




  public double getGlobalCurrentTime()
  {
    return globalCurrentTime;
  }
  




  public double getPreviousTime()
  {
    return softPreviousTime;
  }
  




  public double getCurrentTime()
  {
    return softCurrentTime;
  }
  
  public double getInterpolatedTime()
  {
    return interpolatedTime;
  }
  
  public void setInterpolatedTime(double time)
  {
    interpolatedTime = time;
    dirtyState = true;
  }
  
  public boolean isForward()
  {
    return forward;
  }
  






  protected abstract void computeInterpolatedStateAndDerivatives(double paramDouble1, double paramDouble2)
    throws DerivativeException;
  






  public double[] getInterpolatedState()
    throws DerivativeException
  {
    if (dirtyState) {
      double oneMinusThetaH = globalCurrentTime - interpolatedTime;
      double theta = h == 0.0D ? 0.0D : (h - oneMinusThetaH) / h;
      computeInterpolatedStateAndDerivatives(theta, oneMinusThetaH);
      dirtyState = false;
    }
    
    return interpolatedState;
  }
  


  public double[] getInterpolatedDerivatives()
    throws DerivativeException
  {
    if (dirtyState) {
      double oneMinusThetaH = globalCurrentTime - interpolatedTime;
      double theta = h == 0.0D ? 0.0D : (h - oneMinusThetaH) / h;
      computeInterpolatedStateAndDerivatives(theta, oneMinusThetaH);
      dirtyState = false;
    }
    
    return interpolatedDerivatives;
  }
  









































  public final void finalizeStep()
    throws DerivativeException
  {
    if (!finalized) {
      doFinalize();
      finalized = true;
    }
  }
  




  protected void doFinalize()
    throws DerivativeException
  {}
  




  public abstract void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException;
  



  public abstract void readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException;
  



  protected void writeBaseExternal(ObjectOutput out)
    throws IOException
  {
    if (currentState == null) {
      out.writeInt(-1);
    } else {
      out.writeInt(currentState.length);
    }
    out.writeDouble(globalPreviousTime);
    out.writeDouble(globalCurrentTime);
    out.writeDouble(softPreviousTime);
    out.writeDouble(softCurrentTime);
    out.writeDouble(h);
    out.writeBoolean(forward);
    
    if (currentState != null) {
      for (int i = 0; i < currentState.length; i++) {
        out.writeDouble(currentState[i]);
      }
    }
    
    out.writeDouble(interpolatedTime);
    



    try
    {
      finalizeStep();
    } catch (DerivativeException e) {
      IOException ioe = new IOException(e.getLocalizedMessage());
      ioe.initCause(e);
      throw ioe;
    }
  }
  










  protected double readBaseExternal(ObjectInput in)
    throws IOException
  {
    int dimension = in.readInt();
    globalPreviousTime = in.readDouble();
    globalCurrentTime = in.readDouble();
    softPreviousTime = in.readDouble();
    softCurrentTime = in.readDouble();
    h = in.readDouble();
    forward = in.readBoolean();
    dirtyState = true;
    
    if (dimension < 0) {
      currentState = null;
    } else {
      currentState = new double[dimension];
      for (int i = 0; i < currentState.length; i++) {
        currentState[i] = in.readDouble();
      }
    }
    

    interpolatedTime = NaN.0D;
    interpolatedState = (dimension < 0 ? null : new double[dimension]);
    interpolatedDerivatives = (dimension < 0 ? null : new double[dimension]);
    
    finalized = true;
    
    return in.readDouble();
  }
}
