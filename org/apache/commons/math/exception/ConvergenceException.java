package org.apache.commons.math.exception;

import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;

























public class ConvergenceException
  extends MathIllegalStateException
{
  private static final long serialVersionUID = 4330003017885151975L;
  
  public ConvergenceException()
  {
    this(null);
  }
  



  public ConvergenceException(Localizable specific)
  {
    this(specific, new Object[] { LocalizedFormats.CONVERGENCE_FAILED, null });
  }
  







  public ConvergenceException(Localizable specific, Object... args)
  {
    super(specific, LocalizedFormats.CONVERGENCE_FAILED, args);
  }
}
