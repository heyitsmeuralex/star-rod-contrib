package org.apache.commons.math.special;

import org.apache.commons.math.MathException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.util.ContinuedFraction;
import org.apache.commons.math.util.FastMath;































public class Gamma
{
  public static final double GAMMA = 0.5772156649015329D;
  private static final double DEFAULT_EPSILON = 1.0E-14D;
  private static final double[] LANCZOS = { 0.9999999999999971D, 57.15623566586292D, -59.59796035547549D, 14.136097974741746D, -0.4919138160976202D, 3.399464998481189E-5D, 4.652362892704858E-5D, -9.837447530487956E-5D, 1.580887032249125E-4D, -2.1026444172410488E-4D, 2.1743961811521265E-4D, -1.643181065367639E-4D, 8.441822398385275E-5D, -2.6190838401581408E-5D, 3.6899182659531625E-6D };
  


















  private static final double HALF_LOG_2_PI = 0.5D * FastMath.log(6.283185307179586D);
  




  private static final double C_LIMIT = 49.0D;
  




  private static final double S_LIMIT = 1.0E-5D;
  




  private Gamma() {}
  




  public static double logGamma(double x)
  {
    double ret;
    



    double ret;
    



    if ((Double.isNaN(x)) || (x <= 0.0D)) {
      ret = NaN.0D;
    } else {
      double g = 4.7421875D;
      
      double sum = 0.0D;
      for (int i = LANCZOS.length - 1; i > 0; i--) {
        sum += LANCZOS[i] / (x + i);
      }
      sum += LANCZOS[0];
      
      double tmp = x + g + 0.5D;
      ret = (x + 0.5D) * FastMath.log(tmp) - tmp + HALF_LOG_2_PI + FastMath.log(sum / x);
    }
    

    return ret;
  }
  








  public static double regularizedGammaP(double a, double x)
    throws MathException
  {
    return regularizedGammaP(a, x, 1.0E-14D, Integer.MAX_VALUE);
  }
  










  public static double regularizedGammaP(double a, double x, double epsilon, int maxIterations)
    throws MathException
  {
    double ret;
    









    double ret;
    









    if ((Double.isNaN(a)) || (Double.isNaN(x)) || (a <= 0.0D) || (x < 0.0D)) {
      ret = NaN.0D; } else { double ret;
      if (x == 0.0D) {
        ret = 0.0D; } else { double ret;
        if (x >= a + 1.0D)
        {

          ret = 1.0D - regularizedGammaQ(a, x, epsilon, maxIterations);
        }
        else {
          double n = 0.0D;
          double an = 1.0D / a;
          double sum = an;
          while ((FastMath.abs(an / sum) > epsilon) && (n < maxIterations) && (sum < Double.POSITIVE_INFINITY))
          {
            n += 1.0D;
            an *= x / (a + n);
            

            sum += an;
          }
          if (n >= maxIterations)
            throw new MaxIterationsExceededException(maxIterations);
          double ret; if (Double.isInfinite(sum)) {
            ret = 1.0D;
          } else
            ret = FastMath.exp(-x + a * FastMath.log(x) - logGamma(a)) * sum;
        }
      }
    }
    return ret;
  }
  








  public static double regularizedGammaQ(double a, double x)
    throws MathException
  {
    return regularizedGammaQ(a, x, 1.0E-14D, Integer.MAX_VALUE);
  }
  









  public static double regularizedGammaQ(double a, double x, double epsilon, int maxIterations)
    throws MathException
  {
    double ret;
    







    double ret;
    







    if ((Double.isNaN(a)) || (Double.isNaN(x)) || (a <= 0.0D) || (x < 0.0D)) {
      ret = NaN.0D; } else { double ret;
      if (x == 0.0D) {
        ret = 1.0D; } else { double ret;
        if (x < a + 1.0D)
        {

          ret = 1.0D - regularizedGammaP(a, x, epsilon, maxIterations);
        }
        else {
          ContinuedFraction cf = new ContinuedFraction()
          {
            protected double getA(int n, double x)
            {
              return 2.0D * n + 1.0D - val$a + x;
            }
            
            protected double getB(int n, double x)
            {
              return n * (val$a - n);
            }
            
          };
          ret = 1.0D / cf.evaluate(x, epsilon, maxIterations);
          ret = FastMath.exp(-x + a * FastMath.log(x) - logGamma(a)) * ret;
        }
      } }
    return ret;
  }
  




















  public static double digamma(double x)
  {
    if ((x > 0.0D) && (x <= 1.0E-5D))
    {

      return -0.5772156649015329D - 1.0D / x;
    }
    
    if (x >= 49.0D)
    {
      double inv = 1.0D / (x * x);
      


      return FastMath.log(x) - 0.5D / x - inv * (0.08333333333333333D + inv * (0.008333333333333333D - inv / 252.0D));
    }
    
    return digamma(x + 1.0D) - 1.0D / x;
  }
  









  public static double trigamma(double x)
  {
    if ((x > 0.0D) && (x <= 1.0E-5D)) {
      return 1.0D / (x * x);
    }
    
    if (x >= 49.0D) {
      double inv = 1.0D / (x * x);
      



      return 1.0D / x + inv / 2.0D + inv / x * (0.16666666666666666D - inv * (0.03333333333333333D + inv / 42.0D));
    }
    
    return trigamma(x + 1.0D) + 1.0D / (x * x);
  }
}
