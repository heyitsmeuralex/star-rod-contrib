package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;





































public class MullerSolver
  extends UnivariateRealSolverImpl
{
  @Deprecated
  public MullerSolver(UnivariateRealFunction f)
  {
    super(f, 100, 1.0E-6D);
  }
  



  @Deprecated
  public MullerSolver()
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
    double x0 = min;
    double y0 = f.value(x0);
    double x2 = max;
    double y2 = f.value(x2);
    double x1 = 0.5D * (x0 + x2);
    double y1 = f.value(x1);
    

    if (y0 == 0.0D) {
      return min;
    }
    if (y2 == 0.0D) {
      return max;
    }
    verifyBracketing(min, max, f);
    
    double oldx = Double.POSITIVE_INFINITY;
    for (int i = 1; i <= maximalIterationCount; i++)
    {



      double d01 = (y1 - y0) / (x1 - x0);
      double d12 = (y2 - y1) / (x2 - x1);
      double d012 = (d12 - d01) / (x2 - x0);
      double c1 = d01 + (x1 - x0) * d012;
      double delta = c1 * c1 - 4.0D * y1 * d012;
      double xplus = x1 + -2.0D * y1 / (c1 + FastMath.sqrt(delta));
      double xminus = x1 + -2.0D * y1 / (c1 - FastMath.sqrt(delta));
      

      double x = isSequence(x0, xplus, x2) ? xplus : xminus;
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
      




      boolean bisect = ((x < x1) && (x1 - x0 > 0.95D * (x2 - x0))) || ((x > x1) && (x2 - x1 > 0.95D * (x2 - x0))) || (x == x1);
      


      if (!bisect) {
        x0 = x < x1 ? x0 : x1;
        y0 = x < x1 ? y0 : y1;
        x2 = x > x1 ? x2 : x1;
        y2 = x > x1 ? y2 : y1;
        x1 = x;y1 = y;
        oldx = x;
      } else {
        double xm = 0.5D * (x0 + x2);
        double ym = f.value(xm);
        if (MathUtils.sign(y0) + MathUtils.sign(ym) == 0.0D) {
          x2 = xm;y2 = ym;
        } else {
          x0 = xm;y0 = ym;
        }
        x1 = 0.5D * (x0 + x2);
        y1 = f.value(x1);
        oldx = Double.POSITIVE_INFINITY;
      }
    }
    throw new MaxIterationsExceededException(maximalIterationCount);
  }
  

























  @Deprecated
  public double solve2(double min, double max)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    return solve2(f, min, max);
  }
  































  @Deprecated
  public double solve2(UnivariateRealFunction f, double min, double max)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    double x0 = min;
    double y0 = f.value(x0);
    double x1 = max;
    double y1 = f.value(x1);
    double x2 = 0.5D * (x0 + x1);
    double y2 = f.value(x2);
    

    if (y0 == 0.0D) return min;
    if (y1 == 0.0D) return max;
    verifyBracketing(min, max, f);
    
    double oldx = Double.POSITIVE_INFINITY;
    for (int i = 1; i <= maximalIterationCount; i++)
    {
      double q = (x2 - x1) / (x1 - x0);
      double a = q * (y2 - (1.0D + q) * y1 + q * y0);
      double b = (2.0D * q + 1.0D) * y2 - (1.0D + q) * (1.0D + q) * y1 + q * q * y0;
      double c = (1.0D + q) * y2;
      double delta = b * b - 4.0D * a * c;
      double denominator;
      double denominator;
      if (delta >= 0.0D)
      {
        double dplus = b + FastMath.sqrt(delta);
        double dminus = b - FastMath.sqrt(delta);
        denominator = FastMath.abs(dplus) > FastMath.abs(dminus) ? dplus : dminus;
      }
      else {
        denominator = FastMath.sqrt(b * b - delta);
      }
      if (denominator != 0.0D) {
        double x = x2 - 2.0D * c * (x2 - x1) / denominator;
        

        while ((x == x1) || (x == x2)) {
          x += absoluteAccuracy;
        }
      }
      
      double x = min + FastMath.random() * (max - min);
      oldx = Double.POSITIVE_INFINITY;
      
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
      

      x0 = x1;
      y0 = y1;
      x1 = x2;
      y1 = y2;
      x2 = x;
      y2 = y;
      oldx = x;
    }
    throw new MaxIterationsExceededException(maximalIterationCount);
  }
}
