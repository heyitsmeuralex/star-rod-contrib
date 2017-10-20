package org.apache.commons.math.ode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.ode.sampling.StepHandler;
import org.apache.commons.math.ode.sampling.StepInterpolator;
import org.apache.commons.math.util.FastMath;



























































































public class ContinuousOutputModel
  implements StepHandler, Serializable
{
  private static final long serialVersionUID = -1417964919405031606L;
  private double initialTime;
  private double finalTime;
  private boolean forward;
  private int index;
  private List<StepInterpolator> steps;
  
  public ContinuousOutputModel()
  {
    steps = new ArrayList();
    reset();
  }
  








  public void append(ContinuousOutputModel model)
    throws DerivativeException
  {
    if (steps.size() == 0) {
      return;
    }
    
    if (steps.size() == 0) {
      initialTime = initialTime;
      forward = forward;
    }
    else {
      if (getInterpolatedState().length != model.getInterpolatedState().length) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(getInterpolatedState().length), Integer.valueOf(model.getInterpolatedState().length) });
      }
      


      if ((forward ^ forward)) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.PROPAGATION_DIRECTION_MISMATCH, new Object[0]);
      }
      

      StepInterpolator lastInterpolator = (StepInterpolator)steps.get(index);
      double current = lastInterpolator.getCurrentTime();
      double previous = lastInterpolator.getPreviousTime();
      double step = current - previous;
      double gap = model.getInitialTime() - current;
      if (FastMath.abs(gap) > 0.001D * FastMath.abs(step)) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.HOLE_BETWEEN_MODELS_TIME_RANGES, new Object[] { Double.valueOf(FastMath.abs(gap)) });
      }
    }
    


    for (StepInterpolator interpolator : steps) {
      steps.add(interpolator.copy());
    }
    
    index = (steps.size() - 1);
    finalTime = ((StepInterpolator)steps.get(index)).getCurrentTime();
  }
  






  public boolean requiresDenseOutput()
  {
    return true;
  }
  



  public void reset()
  {
    initialTime = NaN.0D;
    finalTime = NaN.0D;
    forward = true;
    index = 0;
    steps.clear();
  }
  








  public void handleStep(StepInterpolator interpolator, boolean isLast)
    throws DerivativeException
  {
    if (steps.size() == 0) {
      initialTime = interpolator.getPreviousTime();
      forward = interpolator.isForward();
    }
    
    steps.add(interpolator.copy());
    
    if (isLast) {
      finalTime = interpolator.getCurrentTime();
      index = (steps.size() - 1);
    }
  }
  




  public double getInitialTime()
  {
    return initialTime;
  }
  



  public double getFinalTime()
  {
    return finalTime;
  }
  





  public double getInterpolatedTime()
  {
    return ((StepInterpolator)steps.get(index)).getInterpolatedTime();
  }
  













  public void setInterpolatedTime(double time)
  {
    int iMin = 0;
    StepInterpolator sMin = (StepInterpolator)steps.get(iMin);
    double tMin = 0.5D * (sMin.getPreviousTime() + sMin.getCurrentTime());
    
    int iMax = steps.size() - 1;
    StepInterpolator sMax = (StepInterpolator)steps.get(iMax);
    double tMax = 0.5D * (sMax.getPreviousTime() + sMax.getCurrentTime());
    


    if (locatePoint(time, sMin) <= 0) {
      index = iMin;
      sMin.setInterpolatedTime(time);
      return;
    }
    if (locatePoint(time, sMax) >= 0) {
      index = iMax;
      sMax.setInterpolatedTime(time);
      return;
    }
    

    while (iMax - iMin > 5)
    {

      StepInterpolator si = (StepInterpolator)steps.get(index);
      int location = locatePoint(time, si);
      if (location < 0) {
        iMax = index;
        tMax = 0.5D * (si.getPreviousTime() + si.getCurrentTime());
      } else if (location > 0) {
        iMin = index;
        tMin = 0.5D * (si.getPreviousTime() + si.getCurrentTime());
      }
      else {
        si.setInterpolatedTime(time);
        return;
      }
      

      int iMed = (iMin + iMax) / 2;
      StepInterpolator sMed = (StepInterpolator)steps.get(iMed);
      double tMed = 0.5D * (sMed.getPreviousTime() + sMed.getCurrentTime());
      
      if ((FastMath.abs(tMed - tMin) < 1.0E-6D) || (FastMath.abs(tMax - tMed) < 1.0E-6D))
      {
        index = iMed;

      }
      else
      {
        double d12 = tMax - tMed;
        double d23 = tMed - tMin;
        double d13 = tMax - tMin;
        double dt1 = time - tMax;
        double dt2 = time - tMed;
        double dt3 = time - tMin;
        double iLagrange = (dt2 * dt3 * d23 * iMax - dt1 * dt3 * d13 * iMed + dt1 * dt2 * d12 * iMin) / (d12 * d23 * d13);
        


        index = ((int)FastMath.rint(iLagrange));
      }
      

      int low = FastMath.max(iMin + 1, (9 * iMin + iMax) / 10);
      int high = FastMath.min(iMax - 1, (iMin + 9 * iMax) / 10);
      if (index < low) {
        index = low;
      } else if (index > high) {
        index = high;
      }
    }
    


    index = iMin;
    while ((index <= iMax) && (locatePoint(time, (StepInterpolator)steps.get(index)) > 0)) {
      index += 1;
    }
    
    ((StepInterpolator)steps.get(index)).setInterpolatedTime(time);
  }
  





  public double[] getInterpolatedState()
    throws DerivativeException
  {
    return ((StepInterpolator)steps.get(index)).getInterpolatedState();
  }
  






  private int locatePoint(double time, StepInterpolator interval)
  {
    if (forward) {
      if (time < interval.getPreviousTime())
        return -1;
      if (time > interval.getCurrentTime()) {
        return 1;
      }
      return 0;
    }
    
    if (time > interval.getPreviousTime())
      return -1;
    if (time < interval.getCurrentTime()) {
      return 1;
    }
    return 0;
  }
}
