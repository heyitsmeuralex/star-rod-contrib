package org.apache.commons.math.optimization.direct;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealConvergenceChecker;
import org.apache.commons.math.optimization.RealPointValuePair;
import org.apache.commons.math.optimization.general.AbstractScalarDifferentiableOptimizer;
import org.apache.commons.math.optimization.univariate.AbstractUnivariateRealOptimizer;
import org.apache.commons.math.optimization.univariate.BracketFinder;
import org.apache.commons.math.optimization.univariate.BrentOptimizer;





































public class PowellOptimizer
  extends AbstractScalarDifferentiableOptimizer
{
  public static final double DEFAULT_LS_RELATIVE_TOLERANCE = 1.0E-7D;
  public static final double DEFAULT_LS_ABSOLUTE_TOLERANCE = 1.0E-11D;
  private final LineSearch line;
  
  public PowellOptimizer()
  {
    this(1.0E-7D, 1.0E-11D);
  }
  







  public PowellOptimizer(double lsRelativeTolerance)
  {
    this(lsRelativeTolerance, 1.0E-11D);
  }
  







  public PowellOptimizer(double lsRelativeTolerance, double lsAbsoluteTolerance)
  {
    line = new LineSearch(lsRelativeTolerance, lsAbsoluteTolerance);
  }
  



  protected RealPointValuePair doOptimize()
    throws FunctionEvaluationException, OptimizationException
  {
    double[] guess = (double[])point.clone();
    int n = guess.length;
    
    double[][] direc = new double[n][n];
    for (int i = 0; i < n; i++) {
      direc[i][i] = 1.0D;
    }
    
    double[] x = guess;
    double fVal = computeObjectiveValue(x);
    double[] x1 = (double[])x.clone();
    for (;;) {
      incrementIterationsCounter();
      
      double fX = fVal;
      double fX2 = 0.0D;
      double delta = 0.0D;
      int bigInd = 0;
      double alphaMin = 0.0D;
      
      for (int i = 0; i < n; i++) {
        double[] d = copyOf(direc[i], n);
        
        fX2 = fVal;
        
        line.search(x, d);
        fVal = line.getValueAtOptimum();
        alphaMin = line.getOptimum();
        double[][] result = newPointAndDirection(x, d, alphaMin);
        x = result[0];
        
        if (fX2 - fVal > delta) {
          delta = fX2 - fVal;
          bigInd = i;
        }
      }
      
      RealPointValuePair previous = new RealPointValuePair(x1, fX);
      RealPointValuePair current = new RealPointValuePair(x, fVal);
      if (getConvergenceChecker().converged(getIterations(), previous, current)) {
        if (goal == GoalType.MINIMIZE) {
          return fVal < fX ? current : previous;
        }
        return fVal > fX ? current : previous;
      }
      

      double[] d = new double[n];
      double[] x2 = new double[n];
      for (int i = 0; i < n; i++) {
        x[i] -= x1[i];
        x2[i] = (2.0D * x[i] - x1[i]);
      }
      
      x1 = (double[])x.clone();
      fX2 = computeObjectiveValue(x2);
      
      if (fX > fX2) {
        double t = 2.0D * (fX + fX2 - 2.0D * fVal);
        double temp = fX - fVal - delta;
        t *= temp * temp;
        temp = fX - fX2;
        t -= delta * temp * temp;
        
        if (t < 0.0D) {
          line.search(x, d);
          fVal = line.getValueAtOptimum();
          alphaMin = line.getOptimum();
          double[][] result = newPointAndDirection(x, d, alphaMin);
          x = result[0];
          
          int lastInd = n - 1;
          direc[bigInd] = direc[lastInd];
          direc[lastInd] = result[1];
        }
      }
    }
  }
  












  private double[][] newPointAndDirection(double[] p, double[] d, double optimum)
  {
    int n = p.length;
    double[][] result = new double[2][n];
    double[] nP = result[0];
    double[] nD = result[1];
    for (int i = 0; i < n; i++) {
      d[i] *= optimum;
      p[i] += nD[i];
    }
    return result;
  }
  






  private class LineSearch
  {
    private final AbstractUnivariateRealOptimizer optim = new BrentOptimizer();
    


    private final BracketFinder bracket = new BracketFinder();
    


    private double optimum = NaN.0D;
    


    private double valueAtOptimum = NaN.0D;
    




    public LineSearch(double relativeTolerance, double absoluteTolerance)
    {
      optim.setRelativeAccuracy(relativeTolerance);
      optim.setAbsoluteAccuracy(absoluteTolerance);
    }
    









    public void search(final double[] p, final double[] d)
      throws OptimizationException, FunctionEvaluationException
    {
      optimum = NaN.0D;
      valueAtOptimum = NaN.0D;
      try
      {
        final int n = p.length;
        UnivariateRealFunction f = new UnivariateRealFunction()
        {
          public double value(double alpha) throws FunctionEvaluationException
          {
            double[] x = new double[n];
            for (int i = 0; i < n; i++) {
              x[i] = (p[i] + alpha * d[i]);
            }
            
            double obj = computeObjectiveValue(x);
            return obj;
          }
          
        };
        bracket.search(f, goal, 0.0D, 1.0D);
        optimum = optim.optimize(f, goal, bracket.getLo(), bracket.getHi(), bracket.getMid());
        


        valueAtOptimum = optim.getFunctionValue();
      } catch (MaxIterationsExceededException e) {
        throw new OptimizationException(e);
      }
    }
    


    public double getOptimum()
    {
      return optimum;
    }
    

    public double getValueAtOptimum()
    {
      return valueAtOptimum;
    }
  }
  






  private double[] copyOf(double[] source, int newLen)
  {
    double[] output = new double[newLen];
    System.arraycopy(source, 0, output, 0, Math.min(source.length, newLen));
    return output;
  }
}
