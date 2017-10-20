package org.apache.commons.math.ode;

import org.apache.commons.math.MathException;
import org.apache.commons.math.exception.util.DummyLocalizable;
import org.apache.commons.math.exception.util.Localizable;





























public class DerivativeException
  extends MathException
{
  private static final long serialVersionUID = 5666710788967425123L;
  
  public DerivativeException(String specifier, Object... parts)
  {
    this(new DummyLocalizable(specifier), parts);
  }
  





  public DerivativeException(Localizable specifier, Object... parts)
  {
    super(specifier, parts);
  }
  


  public DerivativeException(Throwable cause)
  {
    super(cause);
  }
}
