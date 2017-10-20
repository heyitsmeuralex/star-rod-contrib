package org.apache.commons.math.exception;

import org.apache.commons.math.exception.util.Localizable;



























public class NotStrictlyPositiveException
  extends NumberIsTooSmallException
{
  private static final long serialVersionUID = -7824848630829852237L;
  
  public NotStrictlyPositiveException(Number value)
  {
    super(value, Integer.valueOf(0), false);
  }
  





  public NotStrictlyPositiveException(Localizable specific, Number value)
  {
    super(specific, value, Integer.valueOf(0), false);
  }
}
