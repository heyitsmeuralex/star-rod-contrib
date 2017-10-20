package org.apache.commons.math.geometry;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Locale;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.CompositeFormat;
























































public class Vector3DFormat
  extends CompositeFormat
{
  private static final long serialVersionUID = -5447606608652576301L;
  private static final String DEFAULT_PREFIX = "{";
  private static final String DEFAULT_SUFFIX = "}";
  private static final String DEFAULT_SEPARATOR = "; ";
  private final String prefix;
  private final String suffix;
  private final String separator;
  private final String trimmedPrefix;
  private final String trimmedSuffix;
  private final String trimmedSeparator;
  private final NumberFormat format;
  
  public Vector3DFormat()
  {
    this("{", "}", "; ", getDefaultNumberFormat());
  }
  



  public Vector3DFormat(NumberFormat format)
  {
    this("{", "}", "; ", format);
  }
  






  public Vector3DFormat(String prefix, String suffix, String separator)
  {
    this(prefix, suffix, separator, getDefaultNumberFormat());
  }
  








  public Vector3DFormat(String prefix, String suffix, String separator, NumberFormat format)
  {
    this.prefix = prefix;
    this.suffix = suffix;
    this.separator = separator;
    trimmedPrefix = prefix.trim();
    trimmedSuffix = suffix.trim();
    trimmedSeparator = separator.trim();
    this.format = format;
  }
  




  public static Locale[] getAvailableLocales()
  {
    return NumberFormat.getAvailableLocales();
  }
  



  public String getPrefix()
  {
    return prefix;
  }
  



  public String getSuffix()
  {
    return suffix;
  }
  



  public String getSeparator()
  {
    return separator;
  }
  



  public NumberFormat getFormat()
  {
    return format;
  }
  



  public static Vector3DFormat getInstance()
  {
    return getInstance(Locale.getDefault());
  }
  




  public static Vector3DFormat getInstance(Locale locale)
  {
    return new Vector3DFormat(getDefaultNumberFormat(locale));
  }
  






  public static String formatVector3D(Vector3D v)
  {
    return getInstance().format(v);
  }
  









  public StringBuffer format(Vector3D vector, StringBuffer toAppendTo, FieldPosition pos)
  {
    pos.setBeginIndex(0);
    pos.setEndIndex(0);
    

    toAppendTo.append(prefix);
    

    formatDouble(vector.getX(), format, toAppendTo, pos);
    toAppendTo.append(separator);
    formatDouble(vector.getY(), format, toAppendTo, pos);
    toAppendTo.append(separator);
    formatDouble(vector.getZ(), format, toAppendTo, pos);
    

    toAppendTo.append(suffix);
    
    return toAppendTo;
  }
  















  public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos)
  {
    if ((obj instanceof Vector3D)) {
      return format((Vector3D)obj, toAppendTo, pos);
    }
    
    throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.CANNOT_FORMAT_INSTANCE_AS_3D_VECTOR, new Object[] { obj.getClass().getName() });
  }
  







  public Vector3D parse(String source)
    throws ParseException
  {
    ParsePosition parsePosition = new ParsePosition(0);
    Vector3D result = parse(source, parsePosition);
    if (parsePosition.getIndex() == 0) {
      throw MathRuntimeException.createParseException(parsePosition.getErrorIndex(), LocalizedFormats.UNPARSEABLE_3D_VECTOR, new Object[] { source });
    }
    

    return result;
  }
  





  public Vector3D parse(String source, ParsePosition pos)
  {
    int initialIndex = pos.getIndex();
    

    parseAndIgnoreWhitespace(source, pos);
    if (!parseFixedstring(source, trimmedPrefix, pos)) {
      return null;
    }
    

    parseAndIgnoreWhitespace(source, pos);
    Number x = parseNumber(source, format, pos);
    if (x == null)
    {

      pos.setIndex(initialIndex);
      return null;
    }
    

    parseAndIgnoreWhitespace(source, pos);
    if (!parseFixedstring(source, trimmedSeparator, pos)) {
      return null;
    }
    parseAndIgnoreWhitespace(source, pos);
    Number y = parseNumber(source, format, pos);
    if (y == null)
    {

      pos.setIndex(initialIndex);
      return null;
    }
    

    parseAndIgnoreWhitespace(source, pos);
    if (!parseFixedstring(source, trimmedSeparator, pos)) {
      return null;
    }
    parseAndIgnoreWhitespace(source, pos);
    Number z = parseNumber(source, format, pos);
    if (z == null)
    {

      pos.setIndex(initialIndex);
      return null;
    }
    

    parseAndIgnoreWhitespace(source, pos);
    if (!parseFixedstring(source, trimmedSuffix, pos)) {
      return null;
    }
    
    return new Vector3D(x.doubleValue(), y.doubleValue(), z.doubleValue());
  }
  








  public Object parseObject(String source, ParsePosition pos)
  {
    return parse(source, pos);
  }
}
