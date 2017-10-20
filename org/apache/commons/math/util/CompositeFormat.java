package org.apache.commons.math.util;

import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.Locale;

























public abstract class CompositeFormat
  extends Format
{
  private static final long serialVersionUID = 5358685519349262494L;
  
  public CompositeFormat() {}
  
  protected static NumberFormat getDefaultNumberFormat()
  {
    return getDefaultNumberFormat(Locale.getDefault());
  }
  






  protected static NumberFormat getDefaultNumberFormat(Locale locale)
  {
    NumberFormat nf = NumberFormat.getInstance(locale);
    nf.setMaximumFractionDigits(2);
    return nf;
  }
  







  protected void parseAndIgnoreWhitespace(String source, ParsePosition pos)
  {
    parseNextCharacter(source, pos);
    pos.setIndex(pos.getIndex() - 1);
  }
  







  protected char parseNextCharacter(String source, ParsePosition pos)
  {
    int index = pos.getIndex();
    int n = source.length();
    char ret = '\000';
    
    if (index < n) {
      char c;
      do {
        c = source.charAt(index++);
      } while ((Character.isWhitespace(c)) && (index < n));
      pos.setIndex(index);
      
      if (index < n) {
        ret = c;
      }
    }
    
    return ret;
  }
  









  private Number parseNumber(String source, double value, ParsePosition pos)
  {
    Number ret = null;
    
    StringBuilder sb = new StringBuilder();
    sb.append('(');
    sb.append(value);
    sb.append(')');
    
    int n = sb.length();
    int startIndex = pos.getIndex();
    int endIndex = startIndex + n;
    if ((endIndex < source.length()) && 
      (source.substring(startIndex, endIndex).compareTo(sb.toString()) == 0)) {
      ret = Double.valueOf(value);
      pos.setIndex(endIndex);
    }
    

    return ret;
  }
  










  protected Number parseNumber(String source, NumberFormat format, ParsePosition pos)
  {
    int startIndex = pos.getIndex();
    Number number = format.parse(source, pos);
    int endIndex = pos.getIndex();
    

    if (startIndex == endIndex)
    {
      double[] special = { NaN.0D, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY };
      

      for (int i = 0; i < special.length; i++) {
        number = parseNumber(source, special[i], pos);
        if (number != null) {
          break;
        }
      }
    }
    
    return number;
  }
  








  protected boolean parseFixedstring(String source, String expected, ParsePosition pos)
  {
    int startIndex = pos.getIndex();
    int endIndex = startIndex + expected.length();
    if ((startIndex >= source.length()) || (endIndex > source.length()) || (source.substring(startIndex, endIndex).compareTo(expected) != 0))
    {


      pos.setIndex(startIndex);
      pos.setErrorIndex(startIndex);
      return false;
    }
    

    pos.setIndex(endIndex);
    return true;
  }
  



















  protected StringBuffer formatDouble(double value, NumberFormat format, StringBuffer toAppendTo, FieldPosition pos)
  {
    if ((Double.isNaN(value)) || (Double.isInfinite(value))) {
      toAppendTo.append('(');
      toAppendTo.append(value);
      toAppendTo.append(')');
    } else {
      format.format(value, toAppendTo, pos);
    }
    return toAppendTo;
  }
}
