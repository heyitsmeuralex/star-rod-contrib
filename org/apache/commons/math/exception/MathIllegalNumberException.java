package org.apache.commons.math.exception;

import org.apache.commons.math.exception.util.Localizable;






































public class MathIllegalNumberException
  extends MathIllegalArgumentException
{
  private static final long serialVersionUID = -7447085893598031110L;
  private final Number argument;
  
  protected MathIllegalNumberException(Localizable specific, Localizable general, Number wrong, Object... arguments)
  {
    super(specific, general, new Object[] { wrong, arguments });
    argument = wrong;
  }
  








  protected MathIllegalNumberException(Localizable general, Number wrong, Object... arguments)
  {
    super(general, new Object[] { wrong, arguments });
    argument = wrong;
  }
  


  public Number getArgument()
  {
    return argument;
  }
}
