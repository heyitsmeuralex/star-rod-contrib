package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.util.FastMath;
































public class BisectionSolver
  extends UnivariateRealSolverImpl
{
  @Deprecated
  public BisectionSolver(UnivariateRealFunction f)
  {
    super(f, 100, 1.0E-6D);
  }
  



  public BisectionSolver()
  {
    super(100, 1.0E-6D);
  }
  
  @Deprecated
  public double solve(double min, double max, double initial)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    return solve(f, min, max);
  }
  
  @Deprecated
  public double solve(double min, double max)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    return solve(f, min, max);
  }
  



  @Deprecated
  public double solve(UnivariateRealFunction f, double min, double max, double initial)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    return solve(f, min, max);
  }
  

  public double solve(int maxEval, UnivariateRealFunction f, double min, double max, double initial)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    return solve(maxEval, f, min, max);
  }
  

  public double solve(int maxEval, UnivariateRealFunction f, double min, double max)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    setMaximalIterationCount(maxEval);
    return solve(f, min, max);
  }
  




  @Deprecated
  public double solve(UnivariateRealFunction f, double min, double max)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    clearResult();
    verifyInterval(min, max);
    



    int i = 0;
    while (i < maximalIterationCount) {
      double m = UnivariateRealSolverUtils.midpoint(min, max);
      double fmin = f.value(min);
      double fm = f.value(m);
      
      if (fm * fmin > 0.0D)
      {
        min = m;
      }
      else {
        max = m;
      }
      
      if (FastMath.abs(max - min) <= absoluteAccuracy) {
        m = UnivariateRealSolverUtils.midpoint(min, max);
        setResult(m, i);
        return m;
      }
      i++;
    }
    
    throw new MaxIterationsExceededException(maximalIterationCount);
  }
}
