package org.apache.commons.math.distribution;

import java.io.Serializable;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.special.Gamma;
import org.apache.commons.math.util.FastMath;







































public class WeibullDistributionImpl
  extends AbstractContinuousDistribution
  implements WeibullDistribution, Serializable
{
  public static final double DEFAULT_INVERSE_ABSOLUTE_ACCURACY = 1.0E-9D;
  private static final long serialVersionUID = 8589540077390120676L;
  private double shape;
  private double scale;
  private final double solverAbsoluteAccuracy;
  private double numericalMean = NaN.0D;
  

  private boolean numericalMeanIsCalculated = false;
  

  private double numericalVariance = NaN.0D;
  

  private boolean numericalVarianceIsCalculated = false;
  





  public WeibullDistributionImpl(double alpha, double beta)
  {
    this(alpha, beta, 1.0E-9D);
  }
  









  public WeibullDistributionImpl(double alpha, double beta, double inverseCumAccuracy)
  {
    setShapeInternal(alpha);
    setScaleInternal(beta);
    solverAbsoluteAccuracy = inverseCumAccuracy;
  }
  

  public double cumulativeProbability(double x)
  {
    double ret;
    
    double ret;
    
    if (x <= 0.0D) {
      ret = 0.0D;
    } else {
      ret = 1.0D - FastMath.exp(-FastMath.pow(x / scale, shape));
    }
    return ret;
  }
  



  public double getShape()
  {
    return shape;
  }
  



  public double getScale()
  {
    return scale;
  }
  







  public double density(double x)
  {
    if (x < 0.0D) {
      return 0.0D;
    }
    
    double xscale = x / scale;
    double xscalepow = FastMath.pow(xscale, shape - 1.0D);
    





    double xscalepowshape = xscalepow * xscale;
    
    return shape / scale * xscalepow * FastMath.exp(-xscalepowshape);
  }
  













  public double inverseCumulativeProbability(double p)
  {
    if ((p < 0.0D) || (p > 1.0D))
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OUT_OF_RANGE_SIMPLE, new Object[] { Double.valueOf(p), Double.valueOf(0.0D), Double.valueOf(1.0D) });
    double ret;
    double ret; if (p == 0.0D) {
      ret = 0.0D; } else { double ret;
      if (p == 1.0D) {
        ret = Double.POSITIVE_INFINITY;
      } else
        ret = scale * FastMath.pow(-FastMath.log(1.0D - p), 1.0D / shape);
    }
    return ret;
  }
  




  @Deprecated
  public void setShape(double alpha)
  {
    setShapeInternal(alpha);
    invalidateParameterDependentMoments();
  }
  


  private void setShapeInternal(double alpha)
  {
    if (alpha <= 0.0D) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_SHAPE, new Object[] { Double.valueOf(alpha) });
    }
    

    shape = alpha;
  }
  




  @Deprecated
  public void setScale(double beta)
  {
    setScaleInternal(beta);
    invalidateParameterDependentMoments();
  }
  


  private void setScaleInternal(double beta)
  {
    if (beta <= 0.0D) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_SCALE, new Object[] { Double.valueOf(beta) });
    }
    

    scale = beta;
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
    return FastMath.pow(scale * FastMath.log(2.0D), 1.0D / shape);
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
  








  protected double calculateNumericalMean()
  {
    double sh = getShape();
    double sc = getScale();
    
    return sc * FastMath.exp(Gamma.logGamma(1.0D + 1.0D / sh));
  }
  









  private double calculateNumericalVariance()
  {
    double sh = getShape();
    double sc = getScale();
    double mn = getNumericalMean();
    
    return sc * sc * FastMath.exp(Gamma.logGamma(1.0D + 2.0D / sh)) - mn * mn;
  }
  







  public double getNumericalMean()
  {
    if (!numericalMeanIsCalculated) {
      numericalMean = calculateNumericalMean();
      numericalMeanIsCalculated = true;
    }
    
    return numericalMean;
  }
  







  public double getNumericalVariance()
  {
    if (!numericalVarianceIsCalculated) {
      numericalVariance = calculateNumericalVariance();
      numericalVarianceIsCalculated = true;
    }
    
    return numericalVariance;
  }
  


  private void invalidateParameterDependentMoments()
  {
    numericalMeanIsCalculated = false;
    numericalVarianceIsCalculated = false;
  }
}
