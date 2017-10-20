package org.apache.commons.math.distribution;

import java.io.Serializable;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.special.Erf;
import org.apache.commons.math.util.FastMath;































public class NormalDistributionImpl
  extends AbstractContinuousDistribution
  implements NormalDistribution, Serializable
{
  public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9D;
  private static final long serialVersionUID = 8589540077390120676L;
  private static final double SQRT2PI = FastMath.sqrt(6.283185307179586D);
  

  private double mean = 0.0D;
  

  private double standardDeviation = 1.0D;
  


  private final double solverAbsoluteAccuracy;
  



  public NormalDistributionImpl(double mean, double sd)
  {
    this(mean, sd, 1.0E-9D);
  }
  









  public NormalDistributionImpl(double mean, double sd, double inverseCumAccuracy)
  {
    setMeanInternal(mean);
    setStandardDeviationInternal(sd);
    solverAbsoluteAccuracy = inverseCumAccuracy;
  }
  



  public NormalDistributionImpl()
  {
    this(0.0D, 1.0D);
  }
  



  public double getMean()
  {
    return mean;
  }
  




  @Deprecated
  public void setMean(double mean)
  {
    setMeanInternal(mean);
  }
  



  private void setMeanInternal(double newMean)
  {
    mean = newMean;
  }
  



  public double getStandardDeviation()
  {
    return standardDeviation;
  }
  





  @Deprecated
  public void setStandardDeviation(double sd)
  {
    setStandardDeviationInternal(sd);
  }
  




  private void setStandardDeviationInternal(double sd)
  {
    if (sd <= 0.0D) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_STANDARD_DEVIATION, new Object[] { Double.valueOf(sd) });
    }
    

    standardDeviation = sd;
  }
  






  @Deprecated
  public double density(Double x)
  {
    return density(x.doubleValue());
  }
  







  public double density(double x)
  {
    double x0 = x - mean;
    return FastMath.exp(-x0 * x0 / (2.0D * standardDeviation * standardDeviation)) / (standardDeviation * SQRT2PI);
  }
  







  public double cumulativeProbability(double x)
    throws MathException
  {
    double dev = x - mean;
    if (FastMath.abs(dev) > 40.0D * standardDeviation) {
      return dev < 0.0D ? 0.0D : 1.0D;
    }
    return 0.5D * (1.0D + Erf.erf(dev / (standardDeviation * FastMath.sqrt(2.0D))));
  }
  








  protected double getSolverAbsoluteAccuracy()
  {
    return solverAbsoluteAccuracy;
  }
  














  public double inverseCumulativeProbability(double p)
    throws MathException
  {
    if (p == 0.0D) {
      return Double.NEGATIVE_INFINITY;
    }
    if (p == 1.0D) {
      return Double.POSITIVE_INFINITY;
    }
    return super.inverseCumulativeProbability(p);
  }
  






  public double sample()
    throws MathException
  {
    return randomData.nextGaussian(mean, standardDeviation);
  }
  



  protected double getDomainLowerBound(double p)
  {
    double ret;
    


    double ret;
    


    if (p < 0.5D) {
      ret = -1.7976931348623157E308D;
    } else {
      ret = mean;
    }
    
    return ret;
  }
  



  protected double getDomainUpperBound(double p)
  {
    double ret;
    


    double ret;
    


    if (p < 0.5D) {
      ret = mean;
    } else {
      ret = Double.MAX_VALUE;
    }
    
    return ret;
  }
  



  protected double getInitialDomain(double p)
  {
    double ret;
    


    double ret;
    

    if (p < 0.5D) {
      ret = mean - standardDeviation; } else { double ret;
      if (p > 0.5D) {
        ret = mean + standardDeviation;
      } else {
        ret = mean;
      }
    }
    return ret;
  }
  








  public double getSupportLowerBound()
  {
    return Double.NEGATIVE_INFINITY;
  }
  








  public double getSupportUpperBound()
  {
    return Double.POSITIVE_INFINITY;
  }
  








  public double getNumericalVariance()
  {
    double s = getStandardDeviation();
    return s * s;
  }
}
