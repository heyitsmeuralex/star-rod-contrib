package org.apache.commons.math.fraction;

import java.io.Serializable;
import java.math.BigInteger;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;



























public class Fraction
  extends Number
  implements FieldElement<Fraction>, Comparable<Fraction>, Serializable
{
  public static final Fraction TWO = new Fraction(2, 1);
  

  public static final Fraction ONE = new Fraction(1, 1);
  

  public static final Fraction ZERO = new Fraction(0, 1);
  

  public static final Fraction FOUR_FIFTHS = new Fraction(4, 5);
  

  public static final Fraction ONE_FIFTH = new Fraction(1, 5);
  

  public static final Fraction ONE_HALF = new Fraction(1, 2);
  

  public static final Fraction ONE_QUARTER = new Fraction(1, 4);
  

  public static final Fraction ONE_THIRD = new Fraction(1, 3);
  

  public static final Fraction THREE_FIFTHS = new Fraction(3, 5);
  

  public static final Fraction THREE_QUARTERS = new Fraction(3, 4);
  

  public static final Fraction TWO_FIFTHS = new Fraction(2, 5);
  

  public static final Fraction TWO_QUARTERS = new Fraction(2, 4);
  

  public static final Fraction TWO_THIRDS = new Fraction(2, 3);
  

  public static final Fraction MINUS_ONE = new Fraction(-1, 1);
  


  private static final long serialVersionUID = 3698073679419233275L;
  


  private final int denominator;
  

  private final int numerator;
  


  public Fraction(double value)
    throws FractionConversionException
  {
    this(value, 1.0E-5D, 100);
  }
  
















  public Fraction(double value, double epsilon, int maxIterations)
    throws FractionConversionException
  {
    this(value, epsilon, Integer.MAX_VALUE, maxIterations);
  }
  














  public Fraction(double value, int maxDenominator)
    throws FractionConversionException
  {
    this(value, 0.0D, maxDenominator, 100);
  }
  































  private Fraction(double value, double epsilon, int maxDenominator, int maxIterations)
    throws FractionConversionException
  {
    long overflow = 2147483647L;
    double r0 = value;
    long a0 = FastMath.floor(r0);
    if (a0 > overflow) {
      throw new FractionConversionException(value, a0, 1L);
    }
    


    if (FastMath.abs(a0 - value) < epsilon) {
      numerator = ((int)a0);
      denominator = 1;
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
      if ((n < maxIterations) && (FastMath.abs(convergent - value) > epsilon) && (q2 < maxDenominator)) {
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
      numerator = ((int)p2);
      denominator = ((int)q2);
    } else {
      numerator = ((int)p1);
      denominator = ((int)q1);
    }
  }
  





  public Fraction(int num)
  {
    this(num, 1);
  }
  






  public Fraction(int num, int den)
  {
    if (den == 0) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.ZERO_DENOMINATOR_IN_FRACTION, new Object[] { Integer.valueOf(num), Integer.valueOf(den) });
    }
    
    if (den < 0) {
      if ((num == Integer.MIN_VALUE) || (den == Integer.MIN_VALUE)) {
        throw MathRuntimeException.createArithmeticException(LocalizedFormats.OVERFLOW_IN_FRACTION, new Object[] { Integer.valueOf(num), Integer.valueOf(den) });
      }
      
      num = -num;
      den = -den;
    }
    
    int d = MathUtils.gcd(num, den);
    if (d > 1) {
      num /= d;
      den /= d;
    }
    

    if (den < 0) {
      num = -num;
      den = -den;
    }
    numerator = num;
    denominator = den;
  }
  

  public Fraction abs()
  {
    Fraction ret;
    
    Fraction ret;
    if (numerator >= 0) {
      ret = this;
    } else {
      ret = negate();
    }
    return ret;
  }
  





  public int compareTo(Fraction object)
  {
    long nOd = numerator * denominator;
    long dOn = denominator * numerator;
    return nOd > dOn ? 1 : nOd < dOn ? -1 : 0;
  }
  





  public double doubleValue()
  {
    return numerator / denominator;
  }
  









  public boolean equals(Object other)
  {
    if (this == other) {
      return true;
    }
    if ((other instanceof Fraction))
    {

      Fraction rhs = (Fraction)other;
      return (numerator == numerator) && (denominator == denominator);
    }
    
    return false;
  }
  





  public float floatValue()
  {
    return (float)doubleValue();
  }
  



  public int getDenominator()
  {
    return denominator;
  }
  



  public int getNumerator()
  {
    return numerator;
  }
  




  public int hashCode()
  {
    return 37 * (629 + numerator) + denominator;
  }
  





  public int intValue()
  {
    return (int)doubleValue();
  }
  





  public long longValue()
  {
    return doubleValue();
  }
  



  public Fraction negate()
  {
    if (numerator == Integer.MIN_VALUE) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.OVERFLOW_IN_FRACTION, new Object[] { Integer.valueOf(numerator), Integer.valueOf(denominator) });
    }
    
    return new Fraction(-numerator, denominator);
  }
  



  public Fraction reciprocal()
  {
    return new Fraction(denominator, numerator);
  }
  









  public Fraction add(Fraction fraction)
  {
    return addSub(fraction, true);
  }
  




  public Fraction add(int i)
  {
    return new Fraction(numerator + i * denominator, denominator);
  }
  









  public Fraction subtract(Fraction fraction)
  {
    return addSub(fraction, false);
  }
  




  public Fraction subtract(int i)
  {
    return new Fraction(numerator - i * denominator, denominator);
  }
  









  private Fraction addSub(Fraction fraction, boolean isAdd)
  {
    if (fraction == null) {
      throw new NullArgumentException(LocalizedFormats.FRACTION);
    }
    
    if (numerator == 0) {
      return isAdd ? fraction : fraction.negate();
    }
    if (numerator == 0) {
      return this;
    }
    

    int d1 = MathUtils.gcd(denominator, denominator);
    if (d1 == 1)
    {
      int uvp = MathUtils.mulAndCheck(numerator, denominator);
      int upv = MathUtils.mulAndCheck(numerator, denominator);
      return new Fraction(isAdd ? MathUtils.addAndCheck(uvp, upv) : MathUtils.subAndCheck(uvp, upv), MathUtils.mulAndCheck(denominator, denominator));
    }
    





    BigInteger uvp = BigInteger.valueOf(numerator).multiply(BigInteger.valueOf(denominator / d1));
    
    BigInteger upv = BigInteger.valueOf(numerator).multiply(BigInteger.valueOf(denominator / d1));
    
    BigInteger t = isAdd ? uvp.add(upv) : uvp.subtract(upv);
    

    int tmodd1 = t.mod(BigInteger.valueOf(d1)).intValue();
    int d2 = tmodd1 == 0 ? d1 : MathUtils.gcd(tmodd1, d1);
    

    BigInteger w = t.divide(BigInteger.valueOf(d2));
    if (w.bitLength() > 31) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.NUMERATOR_OVERFLOW_AFTER_MULTIPLY, new Object[] { w });
    }
    
    return new Fraction(w.intValue(), MathUtils.mulAndCheck(denominator / d1, denominator / d2));
  }
  











  public Fraction multiply(Fraction fraction)
  {
    if (fraction == null) {
      throw new NullArgumentException(LocalizedFormats.FRACTION);
    }
    if ((numerator == 0) || (numerator == 0)) {
      return ZERO;
    }
    

    int d1 = MathUtils.gcd(numerator, denominator);
    int d2 = MathUtils.gcd(numerator, denominator);
    return getReducedFraction(MathUtils.mulAndCheck(numerator / d1, numerator / d2), MathUtils.mulAndCheck(denominator / d2, denominator / d1));
  }
  






  public Fraction multiply(int i)
  {
    return new Fraction(numerator * i, denominator);
  }
  









  public Fraction divide(Fraction fraction)
  {
    if (fraction == null) {
      throw new NullArgumentException(LocalizedFormats.FRACTION);
    }
    if (numerator == 0) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.ZERO_FRACTION_TO_DIVIDE_BY, new Object[] { Integer.valueOf(numerator), Integer.valueOf(denominator) });
    }
    

    return multiply(fraction.reciprocal());
  }
  




  public Fraction divide(int i)
  {
    return new Fraction(numerator, denominator * i);
  }
  










  public static Fraction getReducedFraction(int numerator, int denominator)
  {
    if (denominator == 0) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.ZERO_DENOMINATOR_IN_FRACTION, new Object[] { Integer.valueOf(numerator), Integer.valueOf(denominator) });
    }
    
    if (numerator == 0) {
      return ZERO;
    }
    
    if ((denominator == Integer.MIN_VALUE) && ((numerator & 0x1) == 0)) {
      numerator /= 2;denominator /= 2;
    }
    if (denominator < 0) {
      if ((numerator == Integer.MIN_VALUE) || (denominator == Integer.MIN_VALUE))
      {
        throw MathRuntimeException.createArithmeticException(LocalizedFormats.OVERFLOW_IN_FRACTION, new Object[] { Integer.valueOf(numerator), Integer.valueOf(denominator) });
      }
      
      numerator = -numerator;
      denominator = -denominator;
    }
    
    int gcd = MathUtils.gcd(numerator, denominator);
    numerator /= gcd;
    denominator /= gcd;
    return new Fraction(numerator, denominator);
  }
  









  public String toString()
  {
    String str = null;
    if (denominator == 1) {
      str = Integer.toString(numerator);
    } else if (numerator == 0) {
      str = "0";
    } else {
      str = numerator + " / " + denominator;
    }
    return str;
  }
  
  public FractionField getField()
  {
    return FractionField.getInstance();
  }
}
