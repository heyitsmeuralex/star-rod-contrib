package org.apache.commons.math.distribution;

import java.io.Serializable;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.special.Beta;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;

































public class PascalDistributionImpl
  extends AbstractIntegerDistribution
  implements PascalDistribution, Serializable
{
  private static final long serialVersionUID = 6751309484392813623L;
  private int numberOfSuccesses;
  private double probabilityOfSuccess;
  
  public PascalDistributionImpl(int r, double p)
  {
    setNumberOfSuccessesInternal(r);
    setProbabilityOfSuccessInternal(p);
  }
  



  public int getNumberOfSuccesses()
  {
    return numberOfSuccesses;
  }
  



  public double getProbabilityOfSuccess()
  {
    return probabilityOfSuccess;
  }
  






  @Deprecated
  public void setNumberOfSuccesses(int successes)
  {
    setNumberOfSuccessesInternal(successes);
  }
  





  private void setNumberOfSuccessesInternal(int successes)
  {
    if (successes < 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NEGATIVE_NUMBER_OF_SUCCESSES, new Object[] { Integer.valueOf(successes) });
    }
    

    numberOfSuccesses = successes;
  }
  






  @Deprecated
  public void setProbabilityOfSuccess(double p)
  {
    setProbabilityOfSuccessInternal(p);
  }
  





  private void setProbabilityOfSuccessInternal(double p)
  {
    if ((p < 0.0D) || (p > 1.0D)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OUT_OF_RANGE_SIMPLE, new Object[] { Double.valueOf(p), Double.valueOf(0.0D), Double.valueOf(1.0D) });
    }
    
    probabilityOfSuccess = p;
  }
  







  protected int getDomainLowerBound(double p)
  {
    return -1;
  }
  








  protected int getDomainUpperBound(double p)
  {
    return 2147483646;
  }
  


  public double cumulativeProbability(int x)
    throws MathException
  {
    double ret;
    

    double ret;
    
    if (x < 0) {
      ret = 0.0D;
    } else {
      ret = Beta.regularizedBeta(probabilityOfSuccess, numberOfSuccesses, x + 1);
    }
    
    return ret;
  }
  

  public double probability(int x)
  {
    double ret;
    
    double ret;
    
    if (x < 0) {
      ret = 0.0D;
    } else {
      ret = MathUtils.binomialCoefficientDouble(x + numberOfSuccesses - 1, numberOfSuccesses - 1) * FastMath.pow(probabilityOfSuccess, numberOfSuccesses) * FastMath.pow(1.0D - probabilityOfSuccess, x);
    }
    


    return ret;
  }
  





  public int inverseCumulativeProbability(double p)
    throws MathException
  {
    int ret;
    



    int ret;
    



    if (p == 0.0D) {
      ret = -1; } else { int ret;
      if (p == 1.0D) {
        ret = Integer.MAX_VALUE;
      } else {
        ret = super.inverseCumulativeProbability(p);
      }
    }
    return ret;
  }
  







  public int getSupportLowerBound()
  {
    return 0;
  }
  










  public int getSupportUpperBound()
  {
    return Integer.MAX_VALUE;
  }
  









  public double getNumericalMean()
  {
    double p = getProbabilityOfSuccess();
    double r = getNumberOfSuccesses();
    return r * p / (1.0D - p);
  }
  









  public double getNumericalVariance()
  {
    double p = getProbabilityOfSuccess();
    double r = getNumberOfSuccesses();
    double pInv = 1.0D - p;
    return r * p / (pInv * pInv);
  }
}
