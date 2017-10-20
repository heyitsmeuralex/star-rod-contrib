package org.apache.commons.math.analysis.integration;

import org.apache.commons.math.ConvergingAlgorithmImpl;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;





























public abstract class UnivariateRealIntegratorImpl
  extends ConvergingAlgorithmImpl
  implements UnivariateRealIntegrator
{
  private static final long serialVersionUID = 6248808456637441533L;
  protected int minimalIterationCount;
  protected int defaultMinimalIterationCount;
  protected boolean resultComputed = false;
  






  protected double result;
  





  @Deprecated
  protected UnivariateRealFunction f;
  






  @Deprecated
  protected UnivariateRealIntegratorImpl(UnivariateRealFunction f, int defaultMaximalIterationCount)
    throws IllegalArgumentException
  {
    super(defaultMaximalIterationCount, 1.0E-15D);
    if (f == null) {
      throw new NullArgumentException(LocalizedFormats.FUNCTION);
    }
    
    this.f = f;
    

    setRelativeAccuracy(1.0E-6D);
    defaultMinimalIterationCount = 3;
    minimalIterationCount = defaultMinimalIterationCount;
    
    verifyIterationCount();
  }
  






  protected UnivariateRealIntegratorImpl(int defaultMaximalIterationCount)
    throws IllegalArgumentException
  {
    super(defaultMaximalIterationCount, 1.0E-15D);
    

    setRelativeAccuracy(1.0E-6D);
    defaultMinimalIterationCount = 3;
    minimalIterationCount = defaultMinimalIterationCount;
    
    verifyIterationCount();
  }
  




  public double getResult()
    throws IllegalStateException
  {
    if (resultComputed) {
      return result;
    }
    throw MathRuntimeException.createIllegalStateException(LocalizedFormats.NO_RESULT_AVAILABLE, new Object[0]);
  }
  






  protected final void setResult(double newResult, int iterationCount)
  {
    result = newResult;
    this.iterationCount = iterationCount;
    resultComputed = true;
  }
  


  protected final void clearResult()
  {
    iterationCount = 0;
    resultComputed = false;
  }
  
  public void setMinimalIterationCount(int count)
  {
    minimalIterationCount = count;
  }
  
  public int getMinimalIterationCount()
  {
    return minimalIterationCount;
  }
  
  public void resetMinimalIterationCount()
  {
    minimalIterationCount = defaultMinimalIterationCount;
  }
  






  protected void verifyInterval(double lower, double upper)
    throws IllegalArgumentException
  {
    if (lower >= upper) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.ENDPOINTS_NOT_AN_INTERVAL, new Object[] { Double.valueOf(lower), Double.valueOf(upper) });
    }
  }
  





  protected void verifyIterationCount()
    throws IllegalArgumentException
  {
    if ((minimalIterationCount <= 0) || (maximalIterationCount <= minimalIterationCount)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INVALID_ITERATIONS_LIMITS, new Object[] { Integer.valueOf(minimalIterationCount), Integer.valueOf(maximalIterationCount) });
    }
  }
}
