package org.apache.commons.math.exception;

import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;





























public class NullArgumentException
  extends MathIllegalArgumentException
{
  private static final long serialVersionUID = -6024911025449780478L;
  
  public NullArgumentException()
  {
    super(LocalizedFormats.NULL_NOT_ALLOWED, new Object[0]);
  }
  


  public NullArgumentException(Localizable specific)
  {
    super(specific, LocalizedFormats.NULL_NOT_ALLOWED, new Object[0]);
  }
}
