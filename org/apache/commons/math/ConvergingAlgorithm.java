package org.apache.commons.math;

@Deprecated
public abstract interface ConvergingAlgorithm
{
  public abstract void setMaximalIterationCount(int paramInt);
  
  public abstract int getMaximalIterationCount();
  
  public abstract void resetMaximalIterationCount();
  
  public abstract void setAbsoluteAccuracy(double paramDouble);
  
  public abstract double getAbsoluteAccuracy();
  
  public abstract void resetAbsoluteAccuracy();
  
  public abstract void setRelativeAccuracy(double paramDouble);
  
  public abstract double getRelativeAccuracy();
  
  public abstract void resetRelativeAccuracy();
  
  public abstract int getIterationCount();
}
