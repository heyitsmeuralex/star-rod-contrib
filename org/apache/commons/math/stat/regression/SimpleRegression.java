package org.apache.commons.math.stat.regression;

import java.io.Serializable;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;




















































public class SimpleRegression
  implements Serializable
{
  private static final long serialVersionUID = -3004689053607543335L;
  private TDistribution distribution;
  private double sumX = 0.0D;
  

  private double sumXX = 0.0D;
  

  private double sumY = 0.0D;
  

  private double sumYY = 0.0D;
  

  private double sumXY = 0.0D;
  

  private long n = 0L;
  

  private double xbar = 0.0D;
  

  private double ybar = 0.0D;
  




  public SimpleRegression()
  {
    this(new TDistributionImpl(1.0D));
  }
  








  @Deprecated
  public SimpleRegression(TDistribution t)
  {
    setDistribution(t);
  }
  






  public SimpleRegression(int degrees)
  {
    setDistribution(new TDistributionImpl(degrees));
  }
  












  public void addData(double x, double y)
  {
    if (n == 0L) {
      xbar = x;
      ybar = y;
    } else {
      double dx = x - xbar;
      double dy = y - ybar;
      sumXX += dx * dx * n / (n + 1.0D);
      sumYY += dy * dy * n / (n + 1.0D);
      sumXY += dx * dy * n / (n + 1.0D);
      xbar += dx / (n + 1.0D);
      ybar += dy / (n + 1.0D);
    }
    sumX += x;
    sumY += y;
    n += 1L;
    
    if (n > 2L) {
      distribution.setDegreesOfFreedom(n - 2L);
    }
  }
  













  public void removeData(double x, double y)
  {
    if (n > 0L) {
      double dx = x - xbar;
      double dy = y - ybar;
      sumXX -= dx * dx * n / (n - 1.0D);
      sumYY -= dy * dy * n / (n - 1.0D);
      sumXY -= dx * dy * n / (n - 1.0D);
      xbar -= dx / (n - 1.0D);
      ybar -= dy / (n - 1.0D);
      sumX -= x;
      sumY -= y;
      n -= 1L;
      
      if (n > 2L) {
        distribution.setDegreesOfFreedom(n - 2L);
      }
    }
  }
  















  public void addData(double[][] data)
  {
    for (int i = 0; i < data.length; i++) {
      addData(data[i][0], data[i][1]);
    }
  }
  













  public void removeData(double[][] data)
  {
    for (int i = 0; (i < data.length) && (n > 0L); i++) {
      removeData(data[i][0], data[i][1]);
    }
  }
  


  public void clear()
  {
    sumX = 0.0D;
    sumXX = 0.0D;
    sumY = 0.0D;
    sumYY = 0.0D;
    sumXY = 0.0D;
    n = 0L;
  }
  




  public long getN()
  {
    return n;
  }
  
















  public double predict(double x)
  {
    double b1 = getSlope();
    return getIntercept(b1) + b1 * x;
  }
  















  public double getIntercept()
  {
    return getIntercept(getSlope());
  }
  















  public double getSlope()
  {
    if (n < 2L) {
      return NaN.0D;
    }
    if (FastMath.abs(sumXX) < 4.9E-323D) {
      return NaN.0D;
    }
    return sumXY / sumXX;
  }
  




























  public double getSumSquaredErrors()
  {
    return FastMath.max(0.0D, sumYY - sumXY * sumXY / sumXX);
  }
  









  public double getTotalSumSquares()
  {
    if (n < 2L) {
      return NaN.0D;
    }
    return sumYY;
  }
  






  public double getXSumSquares()
  {
    if (n < 2L) {
      return NaN.0D;
    }
    return sumXX;
  }
  




  public double getSumOfCrossProducts()
  {
    return sumXY;
  }
  















  public double getRegressionSumSquares()
  {
    return getRegressionSumSquares(getSlope());
  }
  









  public double getMeanSquareError()
  {
    if (n < 3L) {
      return NaN.0D;
    }
    return getSumSquaredErrors() / (n - 2L);
  }
  













  public double getR()
  {
    double b1 = getSlope();
    double result = FastMath.sqrt(getRSquare());
    if (b1 < 0.0D) {
      result = -result;
    }
    return result;
  }
  













  public double getRSquare()
  {
    double ssto = getTotalSumSquares();
    return (ssto - getSumSquaredErrors()) / ssto;
  }
  










  public double getInterceptStdErr()
  {
    return FastMath.sqrt(getMeanSquareError() * (1.0D / n + xbar * xbar / sumXX));
  }
  











  public double getSlopeStdErr()
  {
    return FastMath.sqrt(getMeanSquareError() / sumXX);
  }
  




















  public double getSlopeConfidenceInterval()
    throws MathException
  {
    return getSlopeConfidenceInterval(0.05D);
  }
  






























  public double getSlopeConfidenceInterval(double alpha)
    throws MathException
  {
    if ((alpha >= 1.0D) || (alpha <= 0.0D)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OUT_OF_BOUND_SIGNIFICANCE_LEVEL, new Object[] { Double.valueOf(alpha), Double.valueOf(0.0D), Double.valueOf(1.0D) });
    }
    

    return getSlopeStdErr() * distribution.inverseCumulativeProbability(1.0D - alpha / 2.0D);
  }
  




















  public double getSignificance()
    throws MathException
  {
    return 2.0D * (1.0D - distribution.cumulativeProbability(FastMath.abs(getSlope()) / getSlopeStdErr()));
  }
  










  private double getIntercept(double slope)
  {
    return (sumY - slope * sumX) / n;
  }
  





  private double getRegressionSumSquares(double slope)
  {
    return slope * slope * sumXX;
  }
  





  @Deprecated
  public void setDistribution(TDistribution value)
  {
    distribution = value;
    

    if (n > 2L) {
      distribution.setDegreesOfFreedom(n - 2L);
    }
  }
}
