package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;







































public class UnivariateRealSolverUtils
{
  private UnivariateRealSolverUtils() {}
  
  public static double solve(UnivariateRealFunction f, double x0, double x1)
    throws ConvergenceException, FunctionEvaluationException
  {
    setup(f);
    return LazyHolder.FACTORY.newDefaultSolver().solve(f, x0, x1);
  }
  
















  public static double solve(UnivariateRealFunction f, double x0, double x1, double absoluteAccuracy)
    throws ConvergenceException, FunctionEvaluationException
  {
    setup(f);
    UnivariateRealSolver solver = LazyHolder.FACTORY.newDefaultSolver();
    solver.setAbsoluteAccuracy(absoluteAccuracy);
    return solver.solve(f, x0, x1);
  }
  








































  public static double[] bracket(UnivariateRealFunction function, double initial, double lowerBound, double upperBound)
    throws ConvergenceException, FunctionEvaluationException
  {
    return bracket(function, initial, lowerBound, upperBound, Integer.MAX_VALUE);
  }
  




































  public static double[] bracket(UnivariateRealFunction function, double initial, double lowerBound, double upperBound, int maximumIterations)
    throws ConvergenceException, FunctionEvaluationException
  {
    if (function == null) {
      throw new NullArgumentException(LocalizedFormats.FUNCTION);
    }
    if (maximumIterations <= 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INVALID_MAX_ITERATIONS, new Object[] { Integer.valueOf(maximumIterations) });
    }
    
    if ((initial < lowerBound) || (initial > upperBound) || (lowerBound >= upperBound)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INVALID_BRACKETING_PARAMETERS, new Object[] { Double.valueOf(lowerBound), Double.valueOf(initial), Double.valueOf(upperBound) });
    }
    

    double a = initial;
    double b = initial;
    

    int numIterations = 0;
    double fa;
    double fb;
    do { a = FastMath.max(a - 1.0D, lowerBound);
      b = FastMath.min(b + 1.0D, upperBound);
      fa = function.value(a);
      
      fb = function.value(b);
      numIterations++;
    } while ((fa * fb > 0.0D) && (numIterations < maximumIterations) && ((a > lowerBound) || (b < upperBound)));
    

    if (fa * fb > 0.0D) {
      throw new ConvergenceException(LocalizedFormats.FAILED_BRACKETING, new Object[] { Integer.valueOf(numIterations), Integer.valueOf(maximumIterations), Double.valueOf(initial), Double.valueOf(lowerBound), Double.valueOf(upperBound), Double.valueOf(a), Double.valueOf(b), Double.valueOf(fa), Double.valueOf(fb) });
    }
    



    return new double[] { a, b };
  }
  






  public static double midpoint(double a, double b)
  {
    return (a + b) * 0.5D;
  }
  




  private static void setup(UnivariateRealFunction f)
  {
    if (f == null) {
      throw new NullArgumentException(LocalizedFormats.FUNCTION);
    }
  }
  




  private static class LazyHolder
  {
    private static final UnivariateRealSolverFactory FACTORY = ;
    
    private LazyHolder() {}
  }
}
