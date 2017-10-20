package org.apache.commons.math.optimization.univariate;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.optimization.GoalType;




























































public class BracketFinder
{
  private static final double EPS_MIN = 1.0E-21D;
  private static final double GOLD = 1.618034D;
  private final double growLimit;
  private final int maxIterations;
  private int iterations;
  private int evaluations;
  private double lo;
  private double hi;
  private double mid;
  private double fLo;
  private double fHi;
  private double fMid;
  
  public BracketFinder()
  {
    this(100.0D, 50);
  }
  







  public BracketFinder(double growLimit, int maxIterations)
  {
    if (growLimit <= 0.0D) {
      throw new NotStrictlyPositiveException(Double.valueOf(growLimit));
    }
    if (maxIterations <= 0) {
      throw new NotStrictlyPositiveException(Integer.valueOf(maxIterations));
    }
    
    this.growLimit = growLimit;
    this.maxIterations = maxIterations;
  }
  













  public void search(UnivariateRealFunction func, GoalType goal, double xA, double xB)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    reset();
    boolean isMinim = goal == GoalType.MINIMIZE;
    
    double fA = eval(func, xA);
    double fB = eval(func, xB);
    if (isMinim ? fA < fB : fA > fB)
    {

      double tmp = xA;
      xA = xB;
      xB = tmp;
      
      tmp = fA;
      fA = fB;
      fB = tmp;
    }
    
    double xC = xB + 1.618034D * (xB - xA);
    double fC = eval(func, xC);
    
    while (isMinim ? fC < fB : fC > fB) {
      if (++iterations > maxIterations) {
        throw new MaxIterationsExceededException(maxIterations);
      }
      
      double tmp1 = (xB - xA) * (fB - fC);
      double tmp2 = (xB - xC) * (fB - fA);
      
      double val = tmp2 - tmp1;
      double denom = Math.abs(val) < 1.0E-21D ? 2.0E-21D : 2.0D * val;
      
      double w = xB - ((xB - xC) * tmp2 - (xB - xA) * tmp1) / denom;
      double wLim = xB + growLimit * (xC - xB);
      
      double fW;
      if ((w - xC) * (xB - w) > 0.0D) {
        double fW = eval(func, w);
        if (isMinim ? fW < fC : fW > fC)
        {

          xA = xB;
          xB = w;
          fA = fB;
          fB = fW;
          break; }
        if (isMinim ? fW > fB : fW < fB)
        {

          xC = w;
          fC = fW;
          break;
        }
        w = xC + 1.618034D * (xC - xB);
        fW = eval(func, w); } else { double fW;
        if ((w - wLim) * (wLim - xC) >= 0.0D) {
          w = wLim;
          fW = eval(func, w);
        } else if ((w - wLim) * (xC - w) > 0.0D) {
          double fW = eval(func, w);
          if (isMinim ? fW < fC : fW > fC)
          {

            xB = xC;
            xC = w;
            w = xC + 1.618034D * (xC - xB);
            fB = fC;
            fC = fW;
            fW = eval(func, w);
          }
        } else {
          w = xC + 1.618034D * (xC - xB);
          fW = eval(func, w);
        }
      }
      xA = xB;
      xB = xC;
      xC = w;
      fA = fB;
      fB = fC;
      fC = fW;
    }
    
    lo = xA;
    mid = xB;
    hi = xC;
    fLo = fA;
    fMid = fB;
    fHi = fC;
  }
  


  public int getIterations()
  {
    return iterations;
  }
  

  public int getEvaluations()
  {
    return evaluations;
  }
  



  public double getLo()
  {
    return lo;
  }
  



  public double getFLow()
  {
    return fLo;
  }
  



  public double getHi()
  {
    return hi;
  }
  



  public double getFHi()
  {
    return fHi;
  }
  



  public double getMid()
  {
    return mid;
  }
  



  public double getFMid()
  {
    return fMid;
  }
  






  private double eval(UnivariateRealFunction f, double x)
    throws FunctionEvaluationException
  {
    evaluations += 1;
    return f.value(x);
  }
  


  private void reset()
  {
    iterations = 0;
    evaluations = 0;
  }
}
