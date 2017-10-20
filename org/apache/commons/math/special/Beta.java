package org.apache.commons.math.special;

import org.apache.commons.math.MathException;
import org.apache.commons.math.util.ContinuedFraction;
import org.apache.commons.math.util.FastMath;








































public class Beta
{
  private static final double DEFAULT_EPSILON = 1.0E-14D;
  
  private Beta() {}
  
  public static double regularizedBeta(double x, double a, double b)
    throws MathException
  {
    return regularizedBeta(x, a, b, 1.0E-14D, Integer.MAX_VALUE);
  }
  














  public static double regularizedBeta(double x, double a, double b, double epsilon)
    throws MathException
  {
    return regularizedBeta(x, a, b, epsilon, Integer.MAX_VALUE);
  }
  










  public static double regularizedBeta(double x, double a, double b, int maxIterations)
    throws MathException
  {
    return regularizedBeta(x, a, b, 1.0E-14D, maxIterations);
  }
  








  public static double regularizedBeta(double x, double a, double b, double epsilon, int maxIterations)
    throws MathException
  {
    double ret;
    







    double ret;
    






    if ((Double.isNaN(x)) || (Double.isNaN(a)) || (Double.isNaN(b)) || (x < 0.0D) || (x > 1.0D) || (a <= 0.0D) || (b <= 0.0D))
    {

      ret = NaN.0D; } else { double ret;
      if (x > (a + 1.0D) / (a + b + 2.0D)) {
        ret = 1.0D - regularizedBeta(1.0D - x, b, a, epsilon, maxIterations);
      } else {
        ContinuedFraction fraction = new ContinuedFraction()
        {
          protected double getB(int n, double x)
          {
            double ret;
            double ret;
            if (n % 2 == 0) {
              double m = n / 2.0D;
              ret = m * (val$b - m) * x / ((val$a + 2.0D * m - 1.0D) * (val$a + 2.0D * m));
            }
            else {
              double m = (n - 1.0D) / 2.0D;
              ret = -((val$a + m) * (val$a + val$b + m) * x) / ((val$a + 2.0D * m) * (val$a + 2.0D * m + 1.0D));
            }
            
            return ret;
          }
          
          protected double getA(int n, double x)
          {
            return 1.0D;
          }
        };
        ret = FastMath.exp(a * FastMath.log(x) + b * FastMath.log(1.0D - x) - FastMath.log(a) - logBeta(a, b, epsilon, maxIterations)) * 1.0D / fraction.evaluate(x, epsilon, maxIterations);
      }
    }
    

    return ret;
  }
  






  public static double logBeta(double a, double b)
  {
    return logBeta(a, b, 1.0E-14D, Integer.MAX_VALUE);
  }
  






  public static double logBeta(double a, double b, double epsilon, int maxIterations)
  {
    double ret;
    





    double ret;
    





    if ((Double.isNaN(a)) || (Double.isNaN(b)) || (a <= 0.0D) || (b <= 0.0D)) {
      ret = NaN.0D;
    } else {
      ret = Gamma.logGamma(a) + Gamma.logGamma(b) - Gamma.logGamma(a + b);
    }
    

    return ret;
  }
}
