package org.apache.commons.math.analysis.integration;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;



































public class RombergIntegrator
  extends UnivariateRealIntegratorImpl
{
  @Deprecated
  public RombergIntegrator(UnivariateRealFunction f)
  {
    super(f, 32);
  }
  


  public RombergIntegrator()
  {
    super(32);
  }
  
  @Deprecated
  public double integrate(double min, double max)
    throws MaxIterationsExceededException, FunctionEvaluationException, IllegalArgumentException
  {
    return integrate(f, min, max);
  }
  

  public double integrate(UnivariateRealFunction f, double min, double max)
    throws MaxIterationsExceededException, FunctionEvaluationException, IllegalArgumentException
  {
    int m = maximalIterationCount + 1;
    double[] previousRow = new double[m];
    double[] currentRow = new double[m];
    
    clearResult();
    verifyInterval(min, max);
    verifyIterationCount();
    
    TrapezoidIntegrator qtrap = new TrapezoidIntegrator();
    currentRow[0] = qtrap.stage(f, min, max, 0);
    double olds = currentRow[0];
    for (int i = 1; i <= maximalIterationCount; i++)
    {

      double[] tmpRow = previousRow;
      previousRow = currentRow;
      currentRow = tmpRow;
      
      currentRow[0] = qtrap.stage(f, min, max, i);
      for (int j = 1; j <= i; j++)
      {
        double r = (1L << 2 * j) - 1L;
        double tIJm1 = currentRow[(j - 1)];
        currentRow[j] = (tIJm1 + (tIJm1 - previousRow[(j - 1)]) / r);
      }
      double s = currentRow[i];
      if (i >= minimalIterationCount) {
        double delta = FastMath.abs(s - olds);
        double rLimit = relativeAccuracy * (FastMath.abs(olds) + FastMath.abs(s)) * 0.5D;
        if ((delta <= rLimit) || (delta <= absoluteAccuracy)) {
          setResult(s, i);
          return result;
        }
      }
      olds = s;
    }
    throw new MaxIterationsExceededException(maximalIterationCount);
  }
  
  protected void verifyIterationCount()
    throws IllegalArgumentException
  {
    super.verifyIterationCount();
    
    if (maximalIterationCount > 32) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INVALID_ITERATIONS_LIMITS, new Object[] { Integer.valueOf(0), Integer.valueOf(32) });
    }
  }
}
