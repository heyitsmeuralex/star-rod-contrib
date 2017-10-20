package org.apache.commons.math.ode.events;

import org.apache.commons.math.MathException;
import org.apache.commons.math.exception.util.Localizable;





























public class EventException
  extends MathException
{
  private static final long serialVersionUID = -898215297400035290L;
  
  @Deprecated
  public EventException(String specifier, Object... parts)
  {
    super(specifier, parts);
  }
  





  public EventException(Localizable specifier, Object... parts)
  {
    super(specifier, parts);
  }
  



  public EventException(Throwable cause)
  {
    super(cause);
  }
}
