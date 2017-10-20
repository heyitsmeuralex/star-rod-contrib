package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;




































public class RiddersSolver
  extends UnivariateRealSolverImpl
{
  @Deprecated
  public RiddersSolver(UnivariateRealFunction f)
  {
    super(f, 100, 1.0E-6D);
  }
  



  @Deprecated
  public RiddersSolver()
  {
    super(100, 1.0E-6D);
  }
  
  @Deprecated
  public double solve(double min, double max)
    throws ConvergenceException, FunctionEvaluationException
  {
    return solve(f, min, max);
  }
  
  @Deprecated
  public double solve(double min, double max, double initial)
    throws ConvergenceException, FunctionEvaluationException
  {
    return solve(f, min, max, initial);
  }
  
















  public double solve(int maxEval, UnivariateRealFunction f, double min, double max, double initial)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    setMaximalIterationCount(maxEval);
    return solve(f, min, max, initial);
  }
  

















  @Deprecated
  public double solve(UnivariateRealFunction f, double min, double max, double initial)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    if (f.value(min) == 0.0D) return min;
    if (f.value(max) == 0.0D) return max;
    if (f.value(initial) == 0.0D) { return initial;
    }
    verifyBracketing(min, max, f);
    verifySequence(min, initial, max);
    if (isBracketing(min, initial, f)) {
      return solve(f, min, initial);
    }
    return solve(f, initial, max);
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
    double x1 = min;
    double y1 = f.value(x1);
    double x2 = max;
    double y2 = f.value(x2);
    

    if (y1 == 0.0D) {
      return min;
    }
    if (y2 == 0.0D) {
      return max;
    }
    verifyBracketing(min, max, f);
    
    int i = 1;
    double oldx = Double.POSITIVE_INFINITY;
    while (i <= maximalIterationCount)
    {
      double x3 = 0.5D * (x1 + x2);
      double y3 = f.value(x3);
      if (FastMath.abs(y3) <= functionValueAccuracy) {
        setResult(x3, i);
        return result;
      }
      double delta = 1.0D - y1 * y2 / (y3 * y3);
      double correction = MathUtils.sign(y2) * MathUtils.sign(y3) * (x3 - x1) / FastMath.sqrt(delta);
      
      double x = x3 - correction;
      double y = f.value(x);
      

      double tolerance = FastMath.max(relativeAccuracy * FastMath.abs(x), absoluteAccuracy);
      if (FastMath.abs(x - oldx) <= tolerance) {
        setResult(x, i);
        return result;
      }
      if (FastMath.abs(y) <= functionValueAccuracy) {
        setResult(x, i);
        return result;
      }
      


      if (correction > 0.0D) {
        if (MathUtils.sign(y1) + MathUtils.sign(y) == 0.0D) {
          x2 = x;
          y2 = y;
        } else {
          x1 = x;
          x2 = x3;
          y1 = y;
          y2 = y3;
        }
      }
      else if (MathUtils.sign(y2) + MathUtils.sign(y) == 0.0D) {
        x1 = x;
        y1 = y;
      } else {
        x1 = x3;
        x2 = x;
        y1 = y3;
        y2 = y;
      }
      
      oldx = x;
      i++;
    }
    throw new MaxIterationsExceededException(maximalIterationCount);
  }
}
