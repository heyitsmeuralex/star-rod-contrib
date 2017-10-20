package org.apache.commons.math.fraction;

import java.math.BigInteger;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;


































public class ProperBigFractionFormat
  extends BigFractionFormat
{
  private static final long serialVersionUID = -6337346779577272307L;
  private NumberFormat wholeFormat;
  
  public ProperBigFractionFormat()
  {
    this(getDefaultNumberFormat());
  }
  





  public ProperBigFractionFormat(NumberFormat format)
  {
    this(format, (NumberFormat)format.clone(), (NumberFormat)format.clone());
  }
  








  public ProperBigFractionFormat(NumberFormat wholeFormat, NumberFormat numeratorFormat, NumberFormat denominatorFormat)
  {
    super(numeratorFormat, denominatorFormat);
    setWholeFormat(wholeFormat);
  }
  












  public StringBuffer format(BigFraction fraction, StringBuffer toAppendTo, FieldPosition pos)
  {
    pos.setBeginIndex(0);
    pos.setEndIndex(0);
    
    BigInteger num = fraction.getNumerator();
    BigInteger den = fraction.getDenominator();
    BigInteger whole = num.divide(den);
    num = num.remainder(den);
    
    if (!BigInteger.ZERO.equals(whole)) {
      getWholeFormat().format(whole, toAppendTo, pos);
      toAppendTo.append(' ');
      if (num.compareTo(BigInteger.ZERO) < 0) {
        num = num.negate();
      }
    }
    getNumeratorFormat().format(num, toAppendTo, pos);
    toAppendTo.append(" / ");
    getDenominatorFormat().format(den, toAppendTo, pos);
    
    return toAppendTo;
  }
  



  public NumberFormat getWholeFormat()
  {
    return wholeFormat;
  }
  













  public BigFraction parse(String source, ParsePosition pos)
  {
    BigFraction ret = super.parse(source, pos);
    if (ret != null) {
      return ret;
    }
    
    int initialIndex = pos.getIndex();
    

    parseAndIgnoreWhitespace(source, pos);
    

    BigInteger whole = parseNextBigInteger(source, pos);
    if (whole == null)
    {


      pos.setIndex(initialIndex);
      return null;
    }
    

    parseAndIgnoreWhitespace(source, pos);
    

    BigInteger num = parseNextBigInteger(source, pos);
    if (num == null)
    {


      pos.setIndex(initialIndex);
      return null;
    }
    
    if (num.compareTo(BigInteger.ZERO) < 0)
    {
      pos.setIndex(initialIndex);
      return null;
    }
    

    int startIndex = pos.getIndex();
    char c = parseNextCharacter(source, pos);
    switch (c)
    {

    case '\000': 
      return new BigFraction(num);
    

    case '/': 
      break;
    

    default: 
      pos.setIndex(initialIndex);
      pos.setErrorIndex(startIndex);
      return null;
    }
    
    
    parseAndIgnoreWhitespace(source, pos);
    

    BigInteger den = parseNextBigInteger(source, pos);
    if (den == null)
    {


      pos.setIndex(initialIndex);
      return null;
    }
    
    if (den.compareTo(BigInteger.ZERO) < 0)
    {
      pos.setIndex(initialIndex);
      return null;
    }
    
    boolean wholeIsNeg = whole.compareTo(BigInteger.ZERO) < 0;
    if (wholeIsNeg) {
      whole = whole.negate();
    }
    num = whole.multiply(den).add(num);
    if (wholeIsNeg) {
      num = num.negate();
    }
    
    return new BigFraction(num, den);
  }
  





  public void setWholeFormat(NumberFormat format)
  {
    if (format == null) {
      throw new NullArgumentException(LocalizedFormats.WHOLE_FORMAT);
    }
    wholeFormat = format;
  }
}
