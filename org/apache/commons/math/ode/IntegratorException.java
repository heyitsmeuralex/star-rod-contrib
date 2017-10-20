package org.apache.commons.math.ode;

import org.apache.commons.math.MathException;
import org.apache.commons.math.exception.util.Localizable;






























public class IntegratorException
  extends MathException
{
  private static final long serialVersionUID = -1607588949778036796L;
  
  @Deprecated
  public IntegratorException(String specifier, Object... parts)
  {
    super(specifier, parts);
  }
  





  public IntegratorException(Localizable specifier, Object... parts)
  {
    super(specifier, parts);
  }
  



  public IntegratorException(Throwable cause)
  {
    super(cause);
  }
}
