package org.apache.commons.math.fraction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;


























public class BigFraction
  extends Number
  implements FieldElement<BigFraction>, Comparable<BigFraction>, Serializable
{
  public static final BigFraction TWO = new BigFraction(2);
  

  public static final BigFraction ONE = new BigFraction(1);
  

  public static final BigFraction ZERO = new BigFraction(0);
  

  public static final BigFraction MINUS_ONE = new BigFraction(-1);
  

  public static final BigFraction FOUR_FIFTHS = new BigFraction(4, 5);
  

  public static final BigFraction ONE_FIFTH = new BigFraction(1, 5);
  

  public static final BigFraction ONE_HALF = new BigFraction(1, 2);
  

  public static final BigFraction ONE_QUARTER = new BigFraction(1, 4);
  

  public static final BigFraction ONE_THIRD = new BigFraction(1, 3);
  

  public static final BigFraction THREE_FIFTHS = new BigFraction(3, 5);
  

  public static final BigFraction THREE_QUARTERS = new BigFraction(3, 4);
  

  public static final BigFraction TWO_FIFTHS = new BigFraction(2, 5);
  

  public static final BigFraction TWO_QUARTERS = new BigFraction(2, 4);
  

  public static final BigFraction TWO_THIRDS = new BigFraction(2, 3);
  

  private static final long serialVersionUID = -5630213147331578515L;
  

  private static final BigInteger ONE_HUNDRED_DOUBLE = BigInteger.valueOf(100L);
  



  private final BigInteger numerator;
  



  private final BigInteger denominator;
  




  public BigFraction(BigInteger num)
  {
    this(num, BigInteger.ONE);
  }
  







  public BigFraction(BigInteger num, BigInteger den)
  {
    if (num == null) {
      throw new NullPointerException(LocalizedFormats.NUMERATOR.getSourceString());
    }
    if (den == null) {
      throw new NullPointerException(LocalizedFormats.DENOMINATOR.getSourceString());
    }
    if (BigInteger.ZERO.equals(den)) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
    }
    if (BigInteger.ZERO.equals(num)) {
      numerator = BigInteger.ZERO;
      denominator = BigInteger.ONE;
    }
    else
    {
      BigInteger gcd = num.gcd(den);
      if (BigInteger.ONE.compareTo(gcd) < 0) {
        num = num.divide(gcd);
        den = den.divide(gcd);
      }
      

      if (BigInteger.ZERO.compareTo(den) > 0) {
        num = num.negate();
        den = den.negate();
      }
      

      numerator = num;
      denominator = den;
    }
  }
  




















  public BigFraction(double value)
    throws IllegalArgumentException
  {
    if (Double.isNaN(value)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NAN_VALUE_CONVERSION, new Object[0]);
    }
    if (Double.isInfinite(value)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INFINITE_VALUE_CONVERSION, new Object[0]);
    }
    

    long bits = Double.doubleToLongBits(value);
    long sign = bits & 0x8000000000000000;
    long exponent = bits & 0x7FF0000000000000;
    long m = bits & 0xFFFFFFFFFFFFF;
    if (exponent != 0L)
    {
      m |= 0x10000000000000;
    }
    if (sign != 0L) {
      m = -m;
    }
    int k = (int)(exponent >> 52) - 1075;
    while (((m & 0x1FFFFFFFFFFFFE) != 0L) && ((m & 1L) == 0L)) {
      m >>= 1;
      k++;
    }
    
    if (k < 0) {
      numerator = BigInteger.valueOf(m);
      denominator = BigInteger.ZERO.flipBit(-k);
    } else {
      numerator = BigInteger.valueOf(m).multiply(BigInteger.ZERO.flipBit(k));
      denominator = BigInteger.ONE;
    }
  }
  






















  public BigFraction(double value, double epsilon, int maxIterations)
    throws FractionConversionException
  {
    this(value, epsilon, Integer.MAX_VALUE, maxIterations);
  }
  


































  private BigFraction(double value, double epsilon, int maxDenominator, int maxIterations)
    throws FractionConversionException
  {
    long overflow = 2147483647L;
    double r0 = value;
    long a0 = FastMath.floor(r0);
    if (a0 > overflow) {
      throw new FractionConversionException(value, a0, 1L);
    }
    


    if (FastMath.abs(a0 - value) < epsilon) {
      numerator = BigInteger.valueOf(a0);
      denominator = BigInteger.ONE;
      return;
    }
    
    long p0 = 1L;
    long q0 = 0L;
    long p1 = a0;
    long q1 = 1L;
    
    long p2 = 0L;
    long q2 = 1L;
    
    int n = 0;
    boolean stop = false;
    do {
      n++;
      double r1 = 1.0D / (r0 - a0);
      long a1 = FastMath.floor(r1);
      p2 = a1 * p1 + p0;
      q2 = a1 * q1 + q0;
      if ((p2 > overflow) || (q2 > overflow)) {
        throw new FractionConversionException(value, p2, q2);
      }
      
      double convergent = p2 / q2;
      if ((n < maxIterations) && (FastMath.abs(convergent - value) > epsilon) && (q2 < maxDenominator))
      {

        p0 = p1;
        p1 = p2;
        q0 = q1;
        q1 = q2;
        a0 = a1;
        r0 = r1;
      } else {
        stop = true;
      }
    } while (!stop);
    
    if (n >= maxIterations) {
      throw new FractionConversionException(value, maxIterations);
    }
    
    if (q2 < maxDenominator) {
      numerator = BigInteger.valueOf(p2);
      denominator = BigInteger.valueOf(q2);
    } else {
      numerator = BigInteger.valueOf(p1);
      denominator = BigInteger.valueOf(q1);
    }
  }
  
















  public BigFraction(double value, int maxDenominator)
    throws FractionConversionException
  {
    this(value, 0.0D, maxDenominator, 100);
  }
  








  public BigFraction(int num)
  {
    this(BigInteger.valueOf(num), BigInteger.ONE);
  }
  










  public BigFraction(int num, int den)
  {
    this(BigInteger.valueOf(num), BigInteger.valueOf(den));
  }
  







  public BigFraction(long num)
  {
    this(BigInteger.valueOf(num), BigInteger.ONE);
  }
  










  public BigFraction(long num, long den)
  {
    this(BigInteger.valueOf(num), BigInteger.valueOf(den));
  }
  



















  public static BigFraction getReducedFraction(int numerator, int denominator)
  {
    if (numerator == 0) {
      return ZERO;
    }
    
    return new BigFraction(numerator, denominator);
  }
  






  public BigFraction abs()
  {
    return BigInteger.ZERO.compareTo(numerator) <= 0 ? this : negate();
  }
  











  public BigFraction add(BigInteger bg)
  {
    return new BigFraction(numerator.add(denominator.multiply(bg)), denominator);
  }
  









  public BigFraction add(int i)
  {
    return add(BigInteger.valueOf(i));
  }
  









  public BigFraction add(long l)
  {
    return add(BigInteger.valueOf(l));
  }
  










  public BigFraction add(BigFraction fraction)
  {
    if (fraction == null) {
      throw new NullPointerException(LocalizedFormats.FRACTION.getSourceString());
    }
    if (ZERO.equals(fraction)) {
      return this;
    }
    
    BigInteger num = null;
    BigInteger den = null;
    
    if (denominator.equals(denominator)) {
      num = numerator.add(numerator);
      den = denominator;
    } else {
      num = numerator.multiply(denominator).add(numerator.multiply(denominator));
      den = denominator.multiply(denominator);
    }
    return new BigFraction(num, den);
  }
  












  public BigDecimal bigDecimalValue()
  {
    return new BigDecimal(numerator).divide(new BigDecimal(denominator));
  }
  














  public BigDecimal bigDecimalValue(int roundingMode)
  {
    return new BigDecimal(numerator).divide(new BigDecimal(denominator), roundingMode);
  }
  














  public BigDecimal bigDecimalValue(int scale, int roundingMode)
  {
    return new BigDecimal(numerator).divide(new BigDecimal(denominator), scale, roundingMode);
  }
  










  public int compareTo(BigFraction object)
  {
    BigInteger nOd = numerator.multiply(denominator);
    BigInteger dOn = denominator.multiply(numerator);
    return nOd.compareTo(dOn);
  }
  













  public BigFraction divide(BigInteger bg)
  {
    if (BigInteger.ZERO.equals(bg)) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
    }
    return new BigFraction(numerator, denominator.multiply(bg));
  }
  











  public BigFraction divide(int i)
  {
    return divide(BigInteger.valueOf(i));
  }
  











  public BigFraction divide(long l)
  {
    return divide(BigInteger.valueOf(l));
  }
  










  public BigFraction divide(BigFraction fraction)
  {
    if (fraction == null) {
      throw new NullPointerException(LocalizedFormats.FRACTION.getSourceString());
    }
    if (BigInteger.ZERO.equals(numerator)) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.ZERO_DENOMINATOR, new Object[0]);
    }
    
    return multiply(fraction.reciprocal());
  }
  









  public double doubleValue()
  {
    return numerator.doubleValue() / denominator.doubleValue();
  }
  















  public boolean equals(Object other)
  {
    boolean ret = false;
    
    if (this == other) {
      ret = true;
    } else if ((other instanceof BigFraction)) {
      BigFraction rhs = ((BigFraction)other).reduce();
      BigFraction thisOne = reduce();
      ret = (numerator.equals(numerator)) && (denominator.equals(denominator));
    }
    
    return ret;
  }
  









  public float floatValue()
  {
    return numerator.floatValue() / denominator.floatValue();
  }
  






  public BigInteger getDenominator()
  {
    return denominator;
  }
  






  public int getDenominatorAsInt()
  {
    return denominator.intValue();
  }
  






  public long getDenominatorAsLong()
  {
    return denominator.longValue();
  }
  






  public BigInteger getNumerator()
  {
    return numerator;
  }
  






  public int getNumeratorAsInt()
  {
    return numerator.intValue();
  }
  






  public long getNumeratorAsLong()
  {
    return numerator.longValue();
  }
  








  public int hashCode()
  {
    return 37 * (629 + numerator.hashCode()) + denominator.hashCode();
  }
  









  public int intValue()
  {
    return numerator.divide(denominator).intValue();
  }
  









  public long longValue()
  {
    return numerator.divide(denominator).longValue();
  }
  









  public BigFraction multiply(BigInteger bg)
  {
    if (bg == null) {
      throw new NullPointerException();
    }
    return new BigFraction(bg.multiply(numerator), denominator);
  }
  









  public BigFraction multiply(int i)
  {
    return multiply(BigInteger.valueOf(i));
  }
  









  public BigFraction multiply(long l)
  {
    return multiply(BigInteger.valueOf(l));
  }
  









  public BigFraction multiply(BigFraction fraction)
  {
    if (fraction == null) {
      throw new NullPointerException(LocalizedFormats.FRACTION.getSourceString());
    }
    if ((numerator.equals(BigInteger.ZERO)) || (numerator.equals(BigInteger.ZERO)))
    {
      return ZERO;
    }
    return new BigFraction(numerator.multiply(numerator), denominator.multiply(denominator));
  }
  








  public BigFraction negate()
  {
    return new BigFraction(numerator.negate(), denominator);
  }
  







  public double percentageValue()
  {
    return numerator.divide(denominator).multiply(ONE_HUNDRED_DOUBLE).doubleValue();
  }
  










  public BigFraction pow(int exponent)
  {
    if (exponent < 0) {
      return new BigFraction(denominator.pow(-exponent), numerator.pow(-exponent));
    }
    return new BigFraction(numerator.pow(exponent), denominator.pow(exponent));
  }
  









  public BigFraction pow(long exponent)
  {
    if (exponent < 0L) {
      return new BigFraction(MathUtils.pow(denominator, -exponent), MathUtils.pow(numerator, -exponent));
    }
    
    return new BigFraction(MathUtils.pow(numerator, exponent), MathUtils.pow(denominator, exponent));
  }
  










  public BigFraction pow(BigInteger exponent)
  {
    if (exponent.compareTo(BigInteger.ZERO) < 0) {
      BigInteger eNeg = exponent.negate();
      return new BigFraction(MathUtils.pow(denominator, eNeg), MathUtils.pow(numerator, eNeg));
    }
    
    return new BigFraction(MathUtils.pow(numerator, exponent), MathUtils.pow(denominator, exponent));
  }
  










  public double pow(double exponent)
  {
    return FastMath.pow(numerator.doubleValue(), exponent) / FastMath.pow(denominator.doubleValue(), exponent);
  }
  







  public BigFraction reciprocal()
  {
    return new BigFraction(denominator, numerator);
  }
  







  public BigFraction reduce()
  {
    BigInteger gcd = numerator.gcd(denominator);
    return new BigFraction(numerator.divide(gcd), denominator.divide(gcd));
  }
  









  public BigFraction subtract(BigInteger bg)
  {
    if (bg == null) {
      throw new NullPointerException();
    }
    return new BigFraction(numerator.subtract(denominator.multiply(bg)), denominator);
  }
  









  public BigFraction subtract(int i)
  {
    return subtract(BigInteger.valueOf(i));
  }
  










  public BigFraction subtract(long l)
  {
    return subtract(BigInteger.valueOf(l));
  }
  









  public BigFraction subtract(BigFraction fraction)
  {
    if (fraction == null) {
      throw new NullPointerException(LocalizedFormats.FRACTION.getSourceString());
    }
    if (ZERO.equals(fraction)) {
      return this;
    }
    
    BigInteger num = null;
    BigInteger den = null;
    if (denominator.equals(denominator)) {
      num = numerator.subtract(numerator);
      den = denominator;
    } else {
      num = numerator.multiply(denominator).subtract(numerator.multiply(denominator));
      den = denominator.multiply(denominator);
    }
    return new BigFraction(num, den);
  }
  










  public String toString()
  {
    String str = null;
    if (BigInteger.ONE.equals(denominator)) {
      str = numerator.toString();
    } else if (BigInteger.ZERO.equals(numerator)) {
      str = "0";
    } else {
      str = numerator + " / " + denominator;
    }
    return str;
  }
  
  public BigFractionField getField()
  {
    return BigFractionField.getInstance();
  }
}
