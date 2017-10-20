package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;
































public class NewtonSolver
  extends UnivariateRealSolverImpl
{
  @Deprecated
  public NewtonSolver(DifferentiableUnivariateRealFunction f)
  {
    super(f, 100, 1.0E-6D);
  }
  



  @Deprecated
  public NewtonSolver()
  {
    super(100, 1.0E-6D);
  }
  
  @Deprecated
  public double solve(double min, double max)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    return solve(f, min, max);
  }
  
  @Deprecated
  public double solve(double min, double max, double startValue)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    return solve(f, min, max, startValue);
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
    return solve(f, min, max, UnivariateRealSolverUtils.midpoint(min, max));
  }
  















  public double solve(int maxEval, UnivariateRealFunction f, double min, double max, double startValue)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    setMaximalIterationCount(maxEval);
    return solve(f, min, max, startValue);
  }
  















  @Deprecated
  public double solve(UnivariateRealFunction f, double min, double max, double startValue)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    try
    {
      UnivariateRealFunction derivative = ((DifferentiableUnivariateRealFunction)f).derivative();
      
      clearResult();
      verifySequence(min, startValue, max);
      
      double x0 = startValue;
      

      int i = 0;
      while (i < maximalIterationCount)
      {
        double x1 = x0 - f.value(x0) / derivative.value(x0);
        if (FastMath.abs(x1 - x0) <= absoluteAccuracy) {
          setResult(x1, i);
          return x1;
        }
        
        x0 = x1;
        i++;
      }
      
      throw new MaxIterationsExceededException(maximalIterationCount);
    } catch (ClassCastException cce) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.FUNCTION_NOT_DIFFERENTIABLE, new Object[0]);
    }
  }
}
