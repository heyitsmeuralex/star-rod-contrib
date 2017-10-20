package org.apache.commons.math.distribution;

import java.io.Serializable;
import org.apache.commons.math.MathException;




































public class ChiSquaredDistributionImpl
  extends AbstractContinuousDistribution
  implements ChiSquaredDistribution, Serializable
{
  public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9D;
  private static final long serialVersionUID = -8352658048349159782L;
  private GammaDistribution gamma;
  private final double solverAbsoluteAccuracy;
  
  public ChiSquaredDistributionImpl(double df)
  {
    this(df, new GammaDistributionImpl(df / 2.0D, 2.0D));
  }
  








  @Deprecated
  public ChiSquaredDistributionImpl(double df, GammaDistribution g)
  {
    setGammaInternal(g);
    setDegreesOfFreedomInternal(df);
    solverAbsoluteAccuracy = 1.0E-9D;
  }
  








  public ChiSquaredDistributionImpl(double df, double inverseCumAccuracy)
  {
    gamma = new GammaDistributionImpl(df / 2.0D, 2.0D);
    setDegreesOfFreedomInternal(df);
    solverAbsoluteAccuracy = inverseCumAccuracy;
  }
  




  @Deprecated
  public void setDegreesOfFreedom(double degreesOfFreedom)
  {
    setDegreesOfFreedomInternal(degreesOfFreedom);
  }
  


  private void setDegreesOfFreedomInternal(double degreesOfFreedom)
  {
    gamma.setAlpha(degreesOfFreedom / 2.0D);
  }
  



  public double getDegreesOfFreedom()
  {
    return gamma.getAlpha() * 2.0D;
  }
  






  @Deprecated
  public double density(Double x)
  {
    return density(x.doubleValue());
  }
  







  public double density(double x)
  {
    return gamma.density(Double.valueOf(x));
  }
  





  public double cumulativeProbability(double x)
    throws MathException
  {
    return gamma.cumulativeProbability(x);
  }
  













  public double inverseCumulativeProbability(double p)
    throws MathException
  {
    if (p == 0.0D) {
      return 0.0D;
    }
    if (p == 1.0D) {
      return Double.POSITIVE_INFINITY;
    }
    return super.inverseCumulativeProbability(p);
  }
  









  protected double getDomainLowerBound(double p)
  {
    return Double.MIN_VALUE * gamma.getBeta();
  }
  




  protected double getDomainUpperBound(double p)
  {
    double ret;
    



    double ret;
    



    if (p < 0.5D)
    {
      ret = getDegreesOfFreedom();
    }
    else {
      ret = Double.MAX_VALUE;
    }
    
    return ret;
  }
  




  protected double getInitialDomain(double p)
  {
    double ret;
    



    double ret;
    


    if (p < 0.5D)
    {
      ret = getDegreesOfFreedom() * 0.5D;
    }
    else {
      ret = getDegreesOfFreedom();
    }
    
    return ret;
  }
  






  @Deprecated
  public void setGamma(GammaDistribution g)
  {
    setGammaInternal(g);
  }
  




  private void setGammaInternal(GammaDistribution g)
  {
    gamma = g;
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
    return Double.POSITIVE_INFINITY;
  }
  








  public double getNumericalMean()
  {
    return getDegreesOfFreedom();
  }
  








  public double getNumericalVariance()
  {
    return 2.0D * getDegreesOfFreedom();
  }
}
