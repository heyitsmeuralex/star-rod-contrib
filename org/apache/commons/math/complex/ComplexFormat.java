package org.apache.commons.math.complex;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.CompositeFormat;





































public class ComplexFormat
  extends CompositeFormat
{
  private static final long serialVersionUID = -3343698360149467646L;
  private static final String DEFAULT_IMAGINARY_CHARACTER = "i";
  private String imaginaryCharacter;
  private NumberFormat imaginaryFormat;
  private NumberFormat realFormat;
  
  public ComplexFormat()
  {
    this("i", getDefaultNumberFormat());
  }
  




  public ComplexFormat(NumberFormat format)
  {
    this("i", format);
  }
  





  public ComplexFormat(NumberFormat realFormat, NumberFormat imaginaryFormat)
  {
    this("i", realFormat, imaginaryFormat);
  }
  




  public ComplexFormat(String imaginaryCharacter)
  {
    this(imaginaryCharacter, getDefaultNumberFormat());
  }
  





  public ComplexFormat(String imaginaryCharacter, NumberFormat format)
  {
    this(imaginaryCharacter, format, (NumberFormat)format.clone());
  }
  









  public ComplexFormat(String imaginaryCharacter, NumberFormat realFormat, NumberFormat imaginaryFormat)
  {
    setImaginaryCharacter(imaginaryCharacter);
    setImaginaryFormat(imaginaryFormat);
    setRealFormat(realFormat);
  }
  




  public static Locale[] getAvailableLocales()
  {
    return NumberFormat.getAvailableLocales();
  }
  






  public static String formatComplex(Complex c)
  {
    return getInstance().format(c);
  }
  










  public StringBuffer format(Complex complex, StringBuffer toAppendTo, FieldPosition pos)
  {
    pos.setBeginIndex(0);
    pos.setEndIndex(0);
    

    double re = complex.getReal();
    formatDouble(re, getRealFormat(), toAppendTo, pos);
    

    double im = complex.getImaginary();
    if (im < 0.0D) {
      toAppendTo.append(" - ");
      formatDouble(-im, getImaginaryFormat(), toAppendTo, pos);
      toAppendTo.append(getImaginaryCharacter());
    } else if ((im > 0.0D) || (Double.isNaN(im))) {
      toAppendTo.append(" + ");
      formatDouble(im, getImaginaryFormat(), toAppendTo, pos);
      toAppendTo.append(getImaginaryCharacter());
    }
    
    return toAppendTo;
  }
  















  public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
  {
    StringBuffer ret = null;
    
    if ((obj instanceof Complex)) {
      ret = format((Complex)obj, toAppendTo, pos);
    } else if ((obj instanceof Number)) {
      ret = format(new Complex(((Number)obj).doubleValue(), 0.0D), toAppendTo, pos);
    }
    else {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.CANNOT_FORMAT_INSTANCE_AS_COMPLEX, new Object[] { obj.getClass().getName() });
    }
    


    return ret;
  }
  



  public String getImaginaryCharacter()
  {
    return imaginaryCharacter;
  }
  



  public NumberFormat getImaginaryFormat()
  {
    return imaginaryFormat;
  }
  



  public static ComplexFormat getInstance()
  {
    return getInstance(Locale.getDefault());
  }
  




  public static ComplexFormat getInstance(Locale locale)
  {
    NumberFormat f = getDefaultNumberFormat(locale);
    return new ComplexFormat(f);
  }
  



  public NumberFormat getRealFormat()
  {
    return realFormat;
  }
  






  public Complex parse(String source)
    throws ParseException
  {
    ParsePosition parsePosition = new ParsePosition(0);
    Complex result = parse(source, parsePosition);
    if (parsePosition.getIndex() == 0) {
      throw MathRuntimeException.createParseException(parsePosition.getErrorIndex(), LocalizedFormats.UNPARSEABLE_COMPLEX_NUMBER, new Object[] { source });
    }
    

    return result;
  }
  






  public Complex parse(String source, ParsePosition pos)
  {
    int initialIndex = pos.getIndex();
    

    parseAndIgnoreWhitespace(source, pos);
    

    Number re = parseNumber(source, getRealFormat(), pos);
    if (re == null)
    {

      pos.setIndex(initialIndex);
      return null;
    }
    

    int startIndex = pos.getIndex();
    char c = parseNextCharacter(source, pos);
    int sign = 0;
    switch (c)
    {

    case '\000': 
      return new Complex(re.doubleValue(), 0.0D);
    case '-': 
      sign = -1;
      break;
    case '+': 
      sign = 1;
      break;
    


    default: 
      pos.setIndex(initialIndex);
      pos.setErrorIndex(startIndex);
      return null;
    }
    
    
    parseAndIgnoreWhitespace(source, pos);
    

    Number im = parseNumber(source, getRealFormat(), pos);
    if (im == null)
    {

      pos.setIndex(initialIndex);
      return null;
    }
    

    if (!parseFixedstring(source, getImaginaryCharacter(), pos)) {
      return null;
    }
    
    return new Complex(re.doubleValue(), im.doubleValue() * sign);
  }
  









  public Object parseObject(String source, ParsePosition pos)
  {
    return parse(source, pos);
  }
  





  public void setImaginaryCharacter(String imaginaryCharacter)
  {
    if ((imaginaryCharacter == null) || (imaginaryCharacter.length() == 0)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.EMPTY_STRING_FOR_IMAGINARY_CHARACTER, new Object[0]);
    }
    
    this.imaginaryCharacter = imaginaryCharacter;
  }
  




  public void setImaginaryFormat(NumberFormat imaginaryFormat)
  {
    if (imaginaryFormat == null) {
      throw new NullArgumentException(LocalizedFormats.IMAGINARY_FORMAT);
    }
    this.imaginaryFormat = imaginaryFormat;
  }
  




  public void setRealFormat(NumberFormat realFormat)
  {
    if (realFormat == null) {
      throw new NullArgumentException(LocalizedFormats.REAL_FORMAT);
    }
    this.realFormat = realFormat;
  }
}
