package org.apache.commons.math.distribution;

import java.io.Serializable;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;







































public class HypergeometricDistributionImpl
  extends AbstractIntegerDistribution
  implements HypergeometricDistribution, Serializable
{
  private static final long serialVersionUID = -436928820673516179L;
  private int numberOfSuccesses;
  private int populationSize;
  private int sampleSize;
  
  public HypergeometricDistributionImpl(int populationSize, int numberOfSuccesses, int sampleSize)
  {
    if (numberOfSuccesses > populationSize) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NUMBER_OF_SUCCESS_LARGER_THAN_POPULATION_SIZE, new Object[] { Integer.valueOf(numberOfSuccesses), Integer.valueOf(populationSize) });
    }
    


    if (sampleSize > populationSize) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.SAMPLE_SIZE_LARGER_THAN_POPULATION_SIZE, new Object[] { Integer.valueOf(sampleSize), Integer.valueOf(populationSize) });
    }
    



    setPopulationSizeInternal(populationSize);
    setSampleSizeInternal(sampleSize);
    setNumberOfSuccessesInternal(numberOfSuccesses);
  }
  








  public double cumulativeProbability(int x)
  {
    int[] domain = getDomain(populationSize, numberOfSuccesses, sampleSize);
    double ret; double ret; if (x < domain[0]) {
      ret = 0.0D; } else { double ret;
      if (x >= domain[1]) {
        ret = 1.0D;
      } else {
        ret = innerCumulativeProbability(domain[0], x, 1, populationSize, numberOfSuccesses, sampleSize);
      }
    }
    
    return ret;
  }
  








  private int[] getDomain(int n, int m, int k)
  {
    return new int[] { getLowerDomain(n, m, k), getUpperDomain(m, k) };
  }
  








  protected int getDomainLowerBound(double p)
  {
    return getLowerDomain(populationSize, numberOfSuccesses, sampleSize);
  }
  








  protected int getDomainUpperBound(double p)
  {
    return getUpperDomain(sampleSize, numberOfSuccesses);
  }
  








  private int getLowerDomain(int n, int m, int k)
  {
    return FastMath.max(0, m - (n - k));
  }
  




  public int getNumberOfSuccesses()
  {
    return numberOfSuccesses;
  }
  




  public int getPopulationSize()
  {
    return populationSize;
  }
  




  public int getSampleSize()
  {
    return sampleSize;
  }
  







  private int getUpperDomain(int m, int k)
  {
    return FastMath.min(k, m);
  }
  







  public double probability(int x)
  {
    int[] domain = getDomain(populationSize, numberOfSuccesses, sampleSize);
    double ret; double ret; if ((x < domain[0]) || (x > domain[1])) {
      ret = 0.0D;
    } else {
      double p = sampleSize / populationSize;
      double q = (populationSize - sampleSize) / populationSize;
      double p1 = SaddlePointExpansion.logBinomialProbability(x, numberOfSuccesses, p, q);
      
      double p2 = SaddlePointExpansion.logBinomialProbability(sampleSize - x, populationSize - numberOfSuccesses, p, q);
      

      double p3 = SaddlePointExpansion.logBinomialProbability(sampleSize, populationSize, p, q);
      
      ret = FastMath.exp(p1 + p2 - p3);
    }
    
    return ret;
  }
  









  private double probability(int n, int m, int k, int x)
  {
    return FastMath.exp(MathUtils.binomialCoefficientLog(m, x) + MathUtils.binomialCoefficientLog(n - m, k - x) - MathUtils.binomialCoefficientLog(n, k));
  }
  








  @Deprecated
  public void setNumberOfSuccesses(int num)
  {
    setNumberOfSuccessesInternal(num);
  }
  





  private void setNumberOfSuccessesInternal(int num)
  {
    if (num < 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NEGATIVE_NUMBER_OF_SUCCESSES, new Object[] { Integer.valueOf(num) });
    }
    
    numberOfSuccesses = num;
  }
  






  @Deprecated
  public void setPopulationSize(int size)
  {
    setPopulationSizeInternal(size);
  }
  





  private void setPopulationSizeInternal(int size)
  {
    if (size <= 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_POPULATION_SIZE, new Object[] { Integer.valueOf(size) });
    }
    
    populationSize = size;
  }
  






  @Deprecated
  public void setSampleSize(int size)
  {
    setSampleSizeInternal(size);
  }
  




  private void setSampleSizeInternal(int size)
  {
    if (size < 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_SAMPLE_SIZE, new Object[] { Integer.valueOf(size) });
    }
    
    sampleSize = size;
  }
  








  public double upperCumulativeProbability(int x)
  {
    int[] domain = getDomain(populationSize, numberOfSuccesses, sampleSize);
    double ret; double ret; if (x < domain[0]) {
      ret = 1.0D; } else { double ret;
      if (x > domain[1]) {
        ret = 0.0D;
      } else {
        ret = innerCumulativeProbability(domain[1], x, -1, populationSize, numberOfSuccesses, sampleSize);
      }
    }
    return ret;
  }
  














  private double innerCumulativeProbability(int x0, int x1, int dx, int n, int m, int k)
  {
    double ret = probability(n, m, k, x0);
    while (x0 != x1) {
      x0 += dx;
      ret += probability(n, m, k, x0);
    }
    return ret;
  }
  











  public int getSupportLowerBound()
  {
    return FastMath.max(0, getSampleSize() + getNumberOfSuccesses() - getPopulationSize());
  }
  











  public int getSupportUpperBound()
  {
    return FastMath.min(getNumberOfSuccesses(), getSampleSize());
  }
  










  protected double getNumericalMean()
  {
    return getSampleSize() * getNumberOfSuccesses() / getPopulationSize();
  }
  










  public double getNumericalVariance()
  {
    double N = getPopulationSize();
    double m = getNumberOfSuccesses();
    double n = getSampleSize();
    return n * m * (N - n) * (N - m) / (N * N * (N - 1.0D));
  }
}
