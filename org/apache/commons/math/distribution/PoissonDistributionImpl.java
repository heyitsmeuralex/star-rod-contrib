package org.apache.commons.math.distribution;

import java.io.Serializable;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.special.Gamma;
import org.apache.commons.math.util.FastMath;














































public class PoissonDistributionImpl
  extends AbstractIntegerDistribution
  implements PoissonDistribution, Serializable
{
  public static final int DEFAULT_MAX_ITERATIONS = 10000000;
  public static final double DEFAULT_EPSILON = 1.0E-12D;
  private static final long serialVersionUID = -3349935121172596109L;
  private NormalDistribution normal;
  private double mean;
  private int maxIterations = 10000000;
  



  private double epsilon = 1.0E-12D;
  






  public PoissonDistributionImpl(double p)
  {
    this(p, new NormalDistributionImpl());
  }
  








  public PoissonDistributionImpl(double p, double epsilon, int maxIterations)
  {
    setMean(p);
    this.epsilon = epsilon;
    this.maxIterations = maxIterations;
  }
  






  public PoissonDistributionImpl(double p, double epsilon)
  {
    setMean(p);
    this.epsilon = epsilon;
  }
  






  public PoissonDistributionImpl(double p, int maxIterations)
  {
    setMean(p);
    this.maxIterations = maxIterations;
  }
  












  @Deprecated
  public PoissonDistributionImpl(double p, NormalDistribution z)
  {
    setNormalAndMeanInternal(z, p);
  }
  




  public double getMean()
  {
    return mean;
  }
  







  @Deprecated
  public void setMean(double p)
  {
    setNormalAndMeanInternal(normal, p);
  }
  







  private void setNormalAndMeanInternal(NormalDistribution z, double p)
  {
    if (p <= 0.0D) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_POISSON_MEAN, new Object[] { Double.valueOf(p) });
    }
    
    mean = p;
    normal = z;
    normal.setMean(p);
    normal.setStandardDeviation(FastMath.sqrt(p));
  }
  


  public double probability(int x)
  {
    double ret;
    

    double ret;
    
    if ((x < 0) || (x == Integer.MAX_VALUE)) {
      ret = 0.0D; } else { double ret;
      if (x == 0) {
        ret = FastMath.exp(-mean);
      } else {
        ret = FastMath.exp(-SaddlePointExpansion.getStirlingError(x) - SaddlePointExpansion.getDeviancePart(x, mean)) / FastMath.sqrt(6.283185307179586D * x);
      }
    }
    
    return ret;
  }
  








  public double cumulativeProbability(int x)
    throws MathException
  {
    if (x < 0) {
      return 0.0D;
    }
    if (x == Integer.MAX_VALUE) {
      return 1.0D;
    }
    return Gamma.regularizedGammaQ(x + 1.0D, mean, epsilon, maxIterations);
  }
  














  public double normalApproximateProbability(int x)
    throws MathException
  {
    return normal.cumulativeProbability(x + 0.5D);
  }
  
















  public int sample()
    throws MathException
  {
    return (int)FastMath.min(randomData.nextPoisson(mean), 2147483647L);
  }
  








  protected int getDomainLowerBound(double p)
  {
    return 0;
  }
  








  protected int getDomainUpperBound(double p)
  {
    return Integer.MAX_VALUE;
  }
  








  @Deprecated
  public void setNormal(NormalDistribution value)
  {
    setNormalAndMeanInternal(value, mean);
  }
  







  public int getSupportLowerBound()
  {
    return 0;
  }
  










  public int getSupportUpperBound()
  {
    return Integer.MAX_VALUE;
  }
  







  public double getNumericalVariance()
  {
    return getMean();
  }
}
