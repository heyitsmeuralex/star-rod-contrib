package org.apache.commons.math.exception;

import org.apache.commons.math.exception.util.Localizable;



























public class NotPositiveException
  extends NumberIsTooSmallException
{
  private static final long serialVersionUID = -2250556892093726375L;
  
  public NotPositiveException(Number value)
  {
    super(value, Integer.valueOf(0), true);
  }
  





  public NotPositiveException(Localizable specific, Number value)
  {
    super(specific, value, Integer.valueOf(0), true);
  }
}
