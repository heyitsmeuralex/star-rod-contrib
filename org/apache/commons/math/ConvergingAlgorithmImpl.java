package org.apache.commons.math;







/**
 * @deprecated
 */
public abstract class ConvergingAlgorithmImpl
  implements ConvergingAlgorithm
{
  protected double absoluteAccuracy;
  





  protected double relativeAccuracy;
  





  protected int maximalIterationCount;
  





  protected double defaultAbsoluteAccuracy;
  




  protected double defaultRelativeAccuracy;
  




  protected int defaultMaximalIterationCount;
  




  protected int iterationCount;
  





  @Deprecated
  protected ConvergingAlgorithmImpl(int defaultMaximalIterationCount, double defaultAbsoluteAccuracy)
  {
    this.defaultAbsoluteAccuracy = defaultAbsoluteAccuracy;
    defaultRelativeAccuracy = 1.0E-14D;
    absoluteAccuracy = defaultAbsoluteAccuracy;
    relativeAccuracy = defaultRelativeAccuracy;
    this.defaultMaximalIterationCount = defaultMaximalIterationCount;
    maximalIterationCount = defaultMaximalIterationCount;
    iterationCount = 0;
  }
  




  @Deprecated
  protected ConvergingAlgorithmImpl() {}
  



  public int getIterationCount()
  {
    return iterationCount;
  }
  
  public void setAbsoluteAccuracy(double accuracy)
  {
    absoluteAccuracy = accuracy;
  }
  
  public double getAbsoluteAccuracy()
  {
    return absoluteAccuracy;
  }
  
  public void resetAbsoluteAccuracy()
  {
    absoluteAccuracy = defaultAbsoluteAccuracy;
  }
  
  public void setMaximalIterationCount(int count)
  {
    maximalIterationCount = count;
  }
  
  public int getMaximalIterationCount()
  {
    return maximalIterationCount;
  }
  
  public void resetMaximalIterationCount()
  {
    maximalIterationCount = defaultMaximalIterationCount;
  }
  
  public void setRelativeAccuracy(double accuracy)
  {
    relativeAccuracy = accuracy;
  }
  
  public double getRelativeAccuracy()
  {
    return relativeAccuracy;
  }
  
  public void resetRelativeAccuracy()
  {
    relativeAccuracy = defaultRelativeAccuracy;
  }
  




  protected void resetIterationsCounter()
  {
    iterationCount = 0;
  }
  






  protected void incrementIterationsCounter()
    throws MaxIterationsExceededException
  {
    if (++iterationCount > maximalIterationCount) {
      throw new MaxIterationsExceededException(maximalIterationCount);
    }
  }
}
