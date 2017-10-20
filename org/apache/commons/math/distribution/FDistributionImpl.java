package org.apache.commons.math.distribution;

import java.io.Serializable;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.special.Beta;
import org.apache.commons.math.util.FastMath;









































public class FDistributionImpl
  extends AbstractContinuousDistribution
  implements FDistribution, Serializable
{
  public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9D;
  private static final long serialVersionUID = -8516354193418641566L;
  private double numeratorDegreesOfFreedom;
  private double denominatorDegreesOfFreedom;
  private final double solverAbsoluteAccuracy;
  
  public FDistributionImpl(double numeratorDegreesOfFreedom, double denominatorDegreesOfFreedom)
  {
    this(numeratorDegreesOfFreedom, denominatorDegreesOfFreedom, 1.0E-9D);
  }
  









  public FDistributionImpl(double numeratorDegreesOfFreedom, double denominatorDegreesOfFreedom, double inverseCumAccuracy)
  {
    setNumeratorDegreesOfFreedomInternal(numeratorDegreesOfFreedom);
    setDenominatorDegreesOfFreedomInternal(denominatorDegreesOfFreedom);
    solverAbsoluteAccuracy = inverseCumAccuracy;
  }
  







  public double density(double x)
  {
    double nhalf = numeratorDegreesOfFreedom / 2.0D;
    double mhalf = denominatorDegreesOfFreedom / 2.0D;
    double logx = FastMath.log(x);
    double logn = FastMath.log(numeratorDegreesOfFreedom);
    double logm = FastMath.log(denominatorDegreesOfFreedom);
    double lognxm = FastMath.log(numeratorDegreesOfFreedom * x + denominatorDegreesOfFreedom);
    return FastMath.exp(nhalf * logn + nhalf * logx - logx + mhalf * logm - nhalf * lognxm - mhalf * lognxm - Beta.logBeta(nhalf, mhalf));
  }
  





  public double cumulativeProbability(double x)
    throws MathException
  {
    double ret;
    



    double ret;
    



    if (x <= 0.0D) {
      ret = 0.0D;
    } else {
      double n = numeratorDegreesOfFreedom;
      double m = denominatorDegreesOfFreedom;
      
      ret = Beta.regularizedBeta(n * x / (m + n * x), 0.5D * n, 0.5D * m);
    }
    

    return ret;
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
    return 0.0D;
  }
  









  protected double getDomainUpperBound(double p)
  {
    return Double.MAX_VALUE;
  }
  








  protected double getInitialDomain(double p)
  {
    double ret = 1.0D;
    double d = denominatorDegreesOfFreedom;
    if (d > 2.0D)
    {
      ret = d / (d - 2.0D);
    }
    return ret;
  }
  






  @Deprecated
  public void setNumeratorDegreesOfFreedom(double degreesOfFreedom)
  {
    setNumeratorDegreesOfFreedomInternal(degreesOfFreedom);
  }
  





  private void setNumeratorDegreesOfFreedomInternal(double degreesOfFreedom)
  {
    if (degreesOfFreedom <= 0.0D) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_DEGREES_OF_FREEDOM, new Object[] { Double.valueOf(degreesOfFreedom) });
    }
    
    numeratorDegreesOfFreedom = degreesOfFreedom;
  }
  



  public double getNumeratorDegreesOfFreedom()
  {
    return numeratorDegreesOfFreedom;
  }
  






  @Deprecated
  public void setDenominatorDegreesOfFreedom(double degreesOfFreedom)
  {
    setDenominatorDegreesOfFreedomInternal(degreesOfFreedom);
  }
  





  private void setDenominatorDegreesOfFreedomInternal(double degreesOfFreedom)
  {
    if (degreesOfFreedom <= 0.0D) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_DEGREES_OF_FREEDOM, new Object[] { Double.valueOf(degreesOfFreedom) });
    }
    
    denominatorDegreesOfFreedom = degreesOfFreedom;
  }
  



  public double getDenominatorDegreesOfFreedom()
  {
    return denominatorDegreesOfFreedom;
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
    double denominatorDF = getDenominatorDegreesOfFreedom();
    
    if (denominatorDF > 2.0D) {
      return denominatorDF / (denominatorDF - 2.0D);
    }
    
    return NaN.0D;
  }
  
















  public double getNumericalVariance()
  {
    double denominatorDF = getDenominatorDegreesOfFreedom();
    
    if (denominatorDF > 4.0D) {
      double numeratorDF = getNumeratorDegreesOfFreedom();
      double denomDFMinusTwo = denominatorDF - 2.0D;
      
      return 2.0D * (denominatorDF * denominatorDF) * (numeratorDF + denominatorDF - 2.0D) / (numeratorDF * (denomDFMinusTwo * denomDFMinusTwo) * (denominatorDF - 4.0D));
    }
    

    return NaN.0D;
  }
}
