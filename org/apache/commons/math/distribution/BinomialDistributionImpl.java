package org.apache.commons.math.distribution;

import java.io.Serializable;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.special.Beta;
import org.apache.commons.math.util.FastMath;


































public class BinomialDistributionImpl
  extends AbstractIntegerDistribution
  implements BinomialDistribution, Serializable
{
  private static final long serialVersionUID = 6751309484392813623L;
  private int numberOfTrials;
  private double probabilityOfSuccess;
  
  public BinomialDistributionImpl(int trials, double p)
  {
    setNumberOfTrialsInternal(trials);
    setProbabilityOfSuccessInternal(p);
  }
  




  public int getNumberOfTrials()
  {
    return numberOfTrials;
  }
  




  public double getProbabilityOfSuccess()
  {
    return probabilityOfSuccess;
  }
  







  @Deprecated
  public void setNumberOfTrials(int trials)
  {
    setNumberOfTrialsInternal(trials);
  }
  






  private void setNumberOfTrialsInternal(int trials)
  {
    if (trials < 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NEGATIVE_NUMBER_OF_TRIALS, new Object[] { Integer.valueOf(trials) });
    }
    
    numberOfTrials = trials;
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
    return numberOfTrials;
  }
  


  public double cumulativeProbability(int x)
    throws MathException
  {
    double ret;
    

    double ret;
    

    if (x < 0) {
      ret = 0.0D; } else { double ret;
      if (x >= numberOfTrials) {
        ret = 1.0D;
      } else {
        ret = 1.0D - Beta.regularizedBeta(getProbabilityOfSuccess(), x + 1.0D, numberOfTrials - x);
      }
    }
    return ret;
  }
  


  public double probability(int x)
  {
    double ret;
    
    double ret;
    
    if ((x < 0) || (x > numberOfTrials)) {
      ret = 0.0D;
    } else {
      ret = FastMath.exp(SaddlePointExpansion.logBinomialProbability(x, numberOfTrials, probabilityOfSuccess, 1.0D - probabilityOfSuccess));
    }
    

    return ret;
  }
  















  public int inverseCumulativeProbability(double p)
    throws MathException
  {
    if (p == 0.0D) {
      return -1;
    }
    if (p == 1.0D) {
      return Integer.MAX_VALUE;
    }
    

    return super.inverseCumulativeProbability(p);
  }
  








  public int getSupportLowerBound()
  {
    return 0;
  }
  







  public int getSupportUpperBound()
  {
    return getNumberOfTrials();
  }
  









  public double getNumericalMean()
  {
    return getNumberOfTrials() * getProbabilityOfSuccess();
  }
  









  public double getNumericalVariance()
  {
    double p = getProbabilityOfSuccess();
    return getNumberOfTrials() * p * (1.0D - p);
  }
}
