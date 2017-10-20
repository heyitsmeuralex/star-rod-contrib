package org.apache.commons.math.util;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;




























public class BigReal
  implements FieldElement<BigReal>, Comparable<BigReal>, Serializable
{
  public static final BigReal ZERO = new BigReal(BigDecimal.ZERO);
  

  public static final BigReal ONE = new BigReal(BigDecimal.ONE);
  

  private static final long serialVersionUID = 4984534880991310382L;
  

  private final BigDecimal d;
  

  private RoundingMode roundingMode = RoundingMode.HALF_UP;
  

  private int scale = 64;
  


  public BigReal(BigDecimal val)
  {
    d = val;
  }
  


  public BigReal(BigInteger val)
  {
    d = new BigDecimal(val);
  }
  



  public BigReal(BigInteger unscaledVal, int scale)
  {
    d = new BigDecimal(unscaledVal, scale);
  }
  




  public BigReal(BigInteger unscaledVal, int scale, MathContext mc)
  {
    d = new BigDecimal(unscaledVal, scale, mc);
  }
  



  public BigReal(BigInteger val, MathContext mc)
  {
    d = new BigDecimal(val, mc);
  }
  


  public BigReal(char[] in)
  {
    d = new BigDecimal(in);
  }
  




  public BigReal(char[] in, int offset, int len)
  {
    d = new BigDecimal(in, offset, len);
  }
  





  public BigReal(char[] in, int offset, int len, MathContext mc)
  {
    d = new BigDecimal(in, offset, len, mc);
  }
  



  public BigReal(char[] in, MathContext mc)
  {
    d = new BigDecimal(in, mc);
  }
  


  public BigReal(double val)
  {
    d = new BigDecimal(val);
  }
  



  public BigReal(double val, MathContext mc)
  {
    d = new BigDecimal(val, mc);
  }
  


  public BigReal(int val)
  {
    d = new BigDecimal(val);
  }
  



  public BigReal(int val, MathContext mc)
  {
    d = new BigDecimal(val, mc);
  }
  


  public BigReal(long val)
  {
    d = new BigDecimal(val);
  }
  



  public BigReal(long val, MathContext mc)
  {
    d = new BigDecimal(val, mc);
  }
  


  public BigReal(String val)
  {
    d = new BigDecimal(val);
  }
  



  public BigReal(String val, MathContext mc)
  {
    d = new BigDecimal(val, mc);
  }
  





  public RoundingMode getRoundingMode()
  {
    return roundingMode;
  }
  




  public void setRoundingMode(RoundingMode roundingMode)
  {
    this.roundingMode = roundingMode;
  }
  





  public int getScale()
  {
    return scale;
  }
  




  public void setScale(int scale)
  {
    this.scale = scale;
  }
  
  public BigReal add(BigReal a)
  {
    return new BigReal(d.add(d));
  }
  
  public BigReal subtract(BigReal a)
  {
    return new BigReal(d.subtract(d));
  }
  
  public BigReal divide(BigReal a) throws ArithmeticException
  {
    return new BigReal(d.divide(d, scale, roundingMode));
  }
  
  public BigReal multiply(BigReal a)
  {
    return new BigReal(d.multiply(d));
  }
  
  public int compareTo(BigReal a)
  {
    return d.compareTo(d);
  }
  


  public double doubleValue()
  {
    return d.doubleValue();
  }
  


  public BigDecimal bigDecimalValue()
  {
    return d;
  }
  

  public boolean equals(Object other)
  {
    if (this == other) {
      return true;
    }
    
    if ((other instanceof BigReal)) {
      return d.equals(d);
    }
    return false;
  }
  

  public int hashCode()
  {
    return d.hashCode();
  }
  
  public Field<BigReal> getField()
  {
    return BigRealField.getInstance();
  }
}
