package org.apache.commons.math.optimization.univariate;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.util.FastMath;



























public class BrentOptimizer
  extends AbstractUnivariateRealOptimizer
{
  private static final double GOLDEN_SECTION = 0.5D * (3.0D - FastMath.sqrt(5.0D));
  


  public BrentOptimizer()
  {
    setMaxEvaluations(1000);
    setMaximalIterationCount(100);
    setAbsoluteAccuracy(1.0E-11D);
    setRelativeAccuracy(1.0E-9D);
  }
  

  protected double doOptimize()
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    return localMin(getGoalType() == GoalType.MINIMIZE, getMin(), getStartValue(), getMax(), getRelativeAccuracy(), getAbsoluteAccuracy());
  }
  


























  private double localMin(boolean isMinim, double lo, double mid, double hi, double eps, double t)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    if (eps <= 0.0D) {
      throw new NotStrictlyPositiveException(Double.valueOf(eps));
    }
    if (t <= 0.0D)
      throw new NotStrictlyPositiveException(Double.valueOf(t));
    double b;
    double a;
    double b;
    if (lo < hi) {
      double a = lo;
      b = hi;
    } else {
      a = hi;
      b = lo;
    }
    
    double x = mid;
    double v = x;
    double w = x;
    double d = 0.0D;
    double e = 0.0D;
    double fx = computeObjectiveValue(x);
    if (!isMinim) {
      fx = -fx;
    }
    double fv = fx;
    double fw = fx;
    for (;;)
    {
      double m = 0.5D * (a + b);
      double tol1 = eps * FastMath.abs(x) + t;
      double tol2 = 2.0D * tol1;
      

      if (FastMath.abs(x - m) > tol2 - 0.5D * (b - a)) {
        double p = 0.0D;
        double q = 0.0D;
        double r = 0.0D;
        double u = 0.0D;
        
        if (FastMath.abs(e) > tol1) {
          r = (x - w) * (fx - fv);
          q = (x - v) * (fx - fw);
          p = (x - v) * q - (x - w) * r;
          q = 2.0D * (q - r);
          
          if (q > 0.0D) {
            p = -p;
          } else {
            q = -q;
          }
          
          r = e;
          e = d;
          
          if ((p > q * (a - x)) && (p < q * (b - x)) && (FastMath.abs(p) < FastMath.abs(0.5D * q * r)))
          {


            d = p / q;
            u = x + d;
            

            if ((u - a < tol2) || (b - u < tol2)) {
              if (x <= m) {
                d = tol1;
              } else {
                d = -tol1;
              }
            }
          }
          else {
            if (x < m) {
              e = b - x;
            } else {
              e = a - x;
            }
            d = GOLDEN_SECTION * e;
          }
        }
        else {
          if (x < m) {
            e = b - x;
          } else {
            e = a - x;
          }
          d = GOLDEN_SECTION * e;
        }
        

        if (FastMath.abs(d) < tol1) {
          if (d >= 0.0D) {
            u = x + tol1;
          } else {
            u = x - tol1;
          }
        } else {
          u = x + d;
        }
        
        double fu = computeObjectiveValue(u);
        if (!isMinim) {
          fu = -fu;
        }
        

        if (fu <= fx) {
          if (u < x) {
            b = x;
          } else {
            a = x;
          }
          v = w;
          fv = fw;
          w = x;
          fw = fx;
          x = u;
          fx = fu;
        } else {
          if (u < x) {
            a = u;
          } else {
            b = u;
          }
          if ((fu <= fw) || (w == x)) {
            v = w;
            fv = fw;
            w = u;
            fw = fu;
          } else if ((fu <= fv) || (v == x) || (v == w)) {
            v = u;
            fv = fu;
          }
        }
      } else {
        setFunctionValue(isMinim ? fx : -fx);
        return x;
      }
      incrementIterationsCounter();
    }
  }
}
