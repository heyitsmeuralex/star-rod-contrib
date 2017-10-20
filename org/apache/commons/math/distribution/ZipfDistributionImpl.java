package org.apache.commons.math.distribution;

import java.io.Serializable;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;




































public class ZipfDistributionImpl
  extends AbstractIntegerDistribution
  implements ZipfDistribution, Serializable
{
  private static final long serialVersionUID = -140627372283420404L;
  private int numberOfElements;
  private double exponent;
  
  public ZipfDistributionImpl(int numberOfElements, double exponent)
    throws IllegalArgumentException
  {
    setNumberOfElementsInternal(numberOfElements);
    setExponentInternal(exponent);
  }
  




  public int getNumberOfElements()
  {
    return numberOfElements;
  }
  








  @Deprecated
  public void setNumberOfElements(int n)
  {
    setNumberOfElementsInternal(n);
  }
  






  private void setNumberOfElementsInternal(int n)
    throws IllegalArgumentException
  {
    if (n <= 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { Integer.valueOf(n), Integer.valueOf(0) });
    }
    
    numberOfElements = n;
  }
  




  public double getExponent()
  {
    return exponent;
  }
  








  @Deprecated
  public void setExponent(double s)
  {
    setExponentInternal(s);
  }
  







  private void setExponentInternal(double s)
    throws IllegalArgumentException
  {
    if (s <= 0.0D) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_EXPONENT, new Object[] { Double.valueOf(s) });
    }
    

    exponent = s;
  }
  





  public double probability(int x)
  {
    if ((x <= 0) || (x > numberOfElements)) {
      return 0.0D;
    }
    
    return 1.0D / FastMath.pow(x, exponent) / generalizedHarmonic(numberOfElements, exponent);
  }
  







  public double cumulativeProbability(int x)
  {
    if (x <= 0)
      return 0.0D;
    if (x >= numberOfElements) {
      return 1.0D;
    }
    
    return generalizedHarmonic(x, exponent) / generalizedHarmonic(numberOfElements, exponent);
  }
  









  protected int getDomainLowerBound(double p)
  {
    return 0;
  }
  








  protected int getDomainUpperBound(double p)
  {
    return numberOfElements;
  }
  









  private double generalizedHarmonic(int n, double m)
  {
    double value = 0.0D;
    for (int k = n; k > 0; k--) {
      value += 1.0D / FastMath.pow(k, m);
    }
    return value;
  }
  







  public int getSupportLowerBound()
  {
    return 1;
  }
  







  public int getSupportUpperBound()
  {
    return getNumberOfElements();
  }
  












  protected double getNumericalMean()
  {
    int N = getNumberOfElements();
    double s = getExponent();
    
    double Hs1 = generalizedHarmonic(N, s - 1.0D);
    double Hs = generalizedHarmonic(N, s);
    
    return Hs1 / Hs;
  }
  













  protected double getNumericalVariance()
  {
    int N = getNumberOfElements();
    double s = getExponent();
    
    double Hs2 = generalizedHarmonic(N, s - 2.0D);
    double Hs1 = generalizedHarmonic(N, s - 1.0D);
    double Hs = generalizedHarmonic(N, s);
    
    return Hs2 / Hs - Hs1 * Hs1 / (Hs * Hs);
  }
}
