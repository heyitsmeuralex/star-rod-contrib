package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.special.Beta;
import org.apache.commons.math.special.Gamma;
import org.apache.commons.math.util.FastMath;



















































public class BetaDistributionImpl
  extends AbstractContinuousDistribution
  implements BetaDistribution
{
  public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9D;
  private static final long serialVersionUID = -1221965979403477668L;
  private double alpha;
  private double beta;
  private double z;
  private final double solverAbsoluteAccuracy;
  
  public BetaDistributionImpl(double alpha, double beta, double inverseCumAccuracy)
  {
    this.alpha = alpha;
    this.beta = beta;
    z = NaN.0D;
    solverAbsoluteAccuracy = inverseCumAccuracy;
  }
  




  public BetaDistributionImpl(double alpha, double beta)
  {
    this(alpha, beta, 1.0E-9D);
  }
  


  @Deprecated
  public void setAlpha(double alpha)
  {
    this.alpha = alpha;
    z = NaN.0D;
  }
  
  public double getAlpha()
  {
    return alpha;
  }
  


  @Deprecated
  public void setBeta(double beta)
  {
    this.beta = beta;
    z = NaN.0D;
  }
  
  public double getBeta()
  {
    return beta;
  }
  


  private void recomputeZ()
  {
    if (Double.isNaN(z)) {
      z = (Gamma.logGamma(alpha) + Gamma.logGamma(beta) - Gamma.logGamma(alpha + beta));
    }
  }
  






  @Deprecated
  public double density(Double x)
  {
    return density(x.doubleValue());
  }
  







  public double density(double x)
  {
    recomputeZ();
    if ((x < 0.0D) || (x > 1.0D))
      return 0.0D;
    if (x == 0.0D) {
      if (alpha < 1.0D) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.CANNOT_COMPUTE_BETA_DENSITY_AT_0_FOR_SOME_ALPHA, new Object[] { Double.valueOf(alpha) });
      }
      
      return 0.0D; }
    if (x == 1.0D) {
      if (beta < 1.0D) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.CANNOT_COMPUTE_BETA_DENSITY_AT_1_FOR_SOME_BETA, new Object[] { Double.valueOf(beta) });
      }
      
      return 0.0D;
    }
    double logX = FastMath.log(x);
    double log1mX = FastMath.log1p(-x);
    return FastMath.exp((alpha - 1.0D) * logX + (beta - 1.0D) * log1mX - z);
  }
  

  public double inverseCumulativeProbability(double p)
    throws MathException
  {
    if (p == 0.0D)
      return 0.0D;
    if (p == 1.0D) {
      return 1.0D;
    }
    return super.inverseCumulativeProbability(p);
  }
  


  protected double getInitialDomain(double p)
  {
    return p;
  }
  

  protected double getDomainLowerBound(double p)
  {
    return 0.0D;
  }
  

  protected double getDomainUpperBound(double p)
  {
    return 1.0D;
  }
  
  public double cumulativeProbability(double x) throws MathException
  {
    if (x <= 0.0D)
      return 0.0D;
    if (x >= 1.0D) {
      return 1.0D;
    }
    return Beta.regularizedBeta(x, alpha, beta);
  }
  

  public double cumulativeProbability(double x0, double x1)
    throws MathException
  {
    return cumulativeProbability(x1) - cumulativeProbability(x0);
  }
  







  protected double getSolverAbsoluteAccuracy()
  {
    return solverAbsoluteAccuracy;
  }
  







  public double getSupportLowerBound()
  {
    return 0.0D;
  }
  







  public double getSupportUpperBound()
  {
    return 1.0D;
  }
  









  public double getNumericalMean()
  {
    double a = getAlpha();
    return a / (a + getBeta());
  }
  










  public double getNumericalVariance()
  {
    double a = getAlpha();
    double b = getBeta();
    double alphabetasum = a + b;
    return a * b / (alphabetasum * alphabetasum * (alphabetasum + 1.0D));
  }
}
