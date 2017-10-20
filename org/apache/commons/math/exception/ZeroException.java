package org.apache.commons.math.exception;

import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;

























public class ZeroException
  extends MathIllegalNumberException
{
  private static final long serialVersionUID = -1960874856936000015L;
  
  public ZeroException()
  {
    this(null);
  }
  




  public ZeroException(Localizable specific)
  {
    super(specific, LocalizedFormats.ZERO_NOT_ALLOWED, Integer.valueOf(0), new Object[0]);
  }
}
