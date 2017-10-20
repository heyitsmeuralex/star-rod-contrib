package org.apache.commons.math.distribution;

import java.io.Serializable;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.util.FastMath;































public abstract class AbstractIntegerDistribution
  extends AbstractDistribution
  implements IntegerDistribution, Serializable
{
  private static final long serialVersionUID = -1146319659338487221L;
  protected final RandomDataImpl randomData = new RandomDataImpl();
  









  protected AbstractIntegerDistribution() {}
  









  public double cumulativeProbability(double x)
    throws MathException
  {
    return cumulativeProbability((int)FastMath.floor(x));
  }
  













  public double cumulativeProbability(double x0, double x1)
    throws MathException
  {
    if (x0 > x1) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, new Object[] { Double.valueOf(x0), Double.valueOf(x1) });
    }
    
    if (FastMath.floor(x0) < x0) {
      return cumulativeProbability((int)FastMath.floor(x0) + 1, (int)FastMath.floor(x1));
    }
    
    return cumulativeProbability((int)FastMath.floor(x0), (int)FastMath.floor(x1));
  }
  











  public abstract double cumulativeProbability(int paramInt)
    throws MathException;
  










  public double probability(double x)
  {
    double fl = FastMath.floor(x);
    if (fl == x) {
      return probability((int)x);
    }
    return 0.0D;
  }
  










  public double cumulativeProbability(int x0, int x1)
    throws MathException
  {
    if (x0 > x1) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, new Object[] { Integer.valueOf(x0), Integer.valueOf(x1) });
    }
    
    return cumulativeProbability(x1) - cumulativeProbability(x0 - 1);
  }
  









  public int inverseCumulativeProbability(double p)
    throws MathException
  {
    if ((p < 0.0D) || (p > 1.0D)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OUT_OF_RANGE_SIMPLE, new Object[] { Double.valueOf(p), Double.valueOf(0.0D), Double.valueOf(1.0D) });
    }
    



    int x0 = getDomainLowerBound(p);
    int x1 = getDomainUpperBound(p);
    
    while (x0 < x1) {
      int xm = x0 + (x1 - x0) / 2;
      double pm = checkedCumulativeProbability(xm);
      if (pm > p)
      {
        if (xm == x1)
        {

          x1--;
        }
        else {
          x1 = xm;
        }
        
      }
      else if (xm == x0)
      {

        x0++;
      }
      else {
        x0 = xm;
      }
    }
    


    double pm = checkedCumulativeProbability(x0);
    while (pm > p) {
      x0--;
      pm = checkedCumulativeProbability(x0);
    }
    
    return x0;
  }
  





  public void reseedRandomGenerator(long seed)
  {
    randomData.reSeed(seed);
  }
  







  public int sample()
    throws MathException
  {
    return randomData.nextInversionDeviate(this);
  }
  








  public int[] sample(int sampleSize)
    throws MathException
  {
    if (sampleSize <= 0) {
      MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_SAMPLE_SIZE, new Object[] { Integer.valueOf(sampleSize) });
    }
    int[] out = new int[sampleSize];
    for (int i = 0; i < sampleSize; i++) {
      out[i] = sample();
    }
    return out;
  }
  








  private double checkedCumulativeProbability(int argument)
    throws MathException
  {
    double result = NaN.0D;
    result = cumulativeProbability(argument);
    if (Double.isNaN(result)) {
      throw new MathException(LocalizedFormats.DISCRETE_CUMULATIVE_PROBABILITY_RETURNED_NAN, new Object[] { Integer.valueOf(argument) });
    }
    return result;
  }
  









  protected abstract int getDomainLowerBound(double paramDouble);
  








  protected abstract int getDomainUpperBound(double paramDouble);
  








  public boolean isSupportLowerBoundInclusive()
  {
    return true;
  }
  







  public boolean isSupportUpperBoundInclusive()
  {
    return true;
  }
}
