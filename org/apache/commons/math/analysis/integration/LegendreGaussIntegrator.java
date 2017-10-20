package org.apache.commons.math.analysis.integration;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;












































public class LegendreGaussIntegrator
  extends UnivariateRealIntegratorImpl
{
  private static final double[] ABSCISSAS_2 = { -1.0D / FastMath.sqrt(3.0D), 1.0D / FastMath.sqrt(3.0D) };
  




  private static final double[] WEIGHTS_2 = { 1.0D, 1.0D };
  




  private static final double[] ABSCISSAS_3 = { -FastMath.sqrt(0.6D), 0.0D, FastMath.sqrt(0.6D) };
  





  private static final double[] WEIGHTS_3 = { 0.5555555555555556D, 0.8888888888888888D, 0.5555555555555556D };
  





  private static final double[] ABSCISSAS_4 = { -FastMath.sqrt((15.0D + 2.0D * FastMath.sqrt(30.0D)) / 35.0D), -FastMath.sqrt((15.0D - 2.0D * FastMath.sqrt(30.0D)) / 35.0D), FastMath.sqrt((15.0D - 2.0D * FastMath.sqrt(30.0D)) / 35.0D), FastMath.sqrt((15.0D + 2.0D * FastMath.sqrt(30.0D)) / 35.0D) };
  






  private static final double[] WEIGHTS_4 = { (90.0D - 5.0D * FastMath.sqrt(30.0D)) / 180.0D, (90.0D + 5.0D * FastMath.sqrt(30.0D)) / 180.0D, (90.0D + 5.0D * FastMath.sqrt(30.0D)) / 180.0D, (90.0D - 5.0D * FastMath.sqrt(30.0D)) / 180.0D };
  






  private static final double[] ABSCISSAS_5 = { -FastMath.sqrt((35.0D + 2.0D * FastMath.sqrt(70.0D)) / 63.0D), -FastMath.sqrt((35.0D - 2.0D * FastMath.sqrt(70.0D)) / 63.0D), 0.0D, FastMath.sqrt((35.0D - 2.0D * FastMath.sqrt(70.0D)) / 63.0D), FastMath.sqrt((35.0D + 2.0D * FastMath.sqrt(70.0D)) / 63.0D) };
  







  private static final double[] WEIGHTS_5 = { (322.0D - 13.0D * FastMath.sqrt(70.0D)) / 900.0D, (322.0D + 13.0D * FastMath.sqrt(70.0D)) / 900.0D, 0.5688888888888889D, (322.0D + 13.0D * FastMath.sqrt(70.0D)) / 900.0D, (322.0D - 13.0D * FastMath.sqrt(70.0D)) / 900.0D };
  





  private final double[] abscissas;
  




  private final double[] weights;
  





  public LegendreGaussIntegrator(int n, int defaultMaximalIterationCount)
    throws IllegalArgumentException
  {
    super(defaultMaximalIterationCount);
    switch (n) {
    case 2: 
      abscissas = ABSCISSAS_2;
      weights = WEIGHTS_2;
      break;
    case 3: 
      abscissas = ABSCISSAS_3;
      weights = WEIGHTS_3;
      break;
    case 4: 
      abscissas = ABSCISSAS_4;
      weights = WEIGHTS_4;
      break;
    case 5: 
      abscissas = ABSCISSAS_5;
      weights = WEIGHTS_5;
      break;
    default: 
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.N_POINTS_GAUSS_LEGENDRE_INTEGRATOR_NOT_SUPPORTED, new Object[] { Integer.valueOf(n), Integer.valueOf(2), Integer.valueOf(5) });
    }
    
  }
  


  @Deprecated
  public double integrate(double min, double max)
    throws ConvergenceException, FunctionEvaluationException, IllegalArgumentException
  {
    return integrate(f, min, max);
  }
  

  public double integrate(UnivariateRealFunction f, double min, double max)
    throws ConvergenceException, FunctionEvaluationException, IllegalArgumentException
  {
    clearResult();
    verifyInterval(min, max);
    verifyIterationCount();
    

    double oldt = stage(f, min, max, 1);
    
    int n = 2;
    for (int i = 0; i < maximalIterationCount; i++)
    {

      double t = stage(f, min, max, n);
      

      double delta = FastMath.abs(t - oldt);
      double limit = FastMath.max(absoluteAccuracy, relativeAccuracy * (FastMath.abs(oldt) + FastMath.abs(t)) * 0.5D);
      



      if ((i + 1 >= minimalIterationCount) && (delta <= limit)) {
        setResult(t, i);
        return result;
      }
      

      double ratio = FastMath.min(4.0D, FastMath.pow(delta / limit, 0.5D / abscissas.length));
      n = FastMath.max((int)(ratio * n), n + 1);
      oldt = t;
    }
    

    throw new MaxIterationsExceededException(maximalIterationCount);
  }
  













  private double stage(UnivariateRealFunction f, double min, double max, int n)
    throws FunctionEvaluationException
  {
    double step = (max - min) / n;
    double halfStep = step / 2.0D;
    

    double midPoint = min + halfStep;
    double sum = 0.0D;
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < abscissas.length; j++) {
        sum += weights[j] * f.value(midPoint + halfStep * abscissas[j]);
      }
      midPoint += step;
    }
    
    return halfStep * sum;
  }
}
