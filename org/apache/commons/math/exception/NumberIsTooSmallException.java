package org.apache.commons.math.exception;

import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;






































public class NumberIsTooSmallException
  extends MathIllegalNumberException
{
  private static final long serialVersionUID = -6100997100383932834L;
  private final Number min;
  private final boolean boundIsAllowed;
  
  public NumberIsTooSmallException(Number wrong, Number min, boolean boundIsAllowed)
  {
    this(null, wrong, min, boundIsAllowed);
  }
  










  public NumberIsTooSmallException(Localizable specific, Number wrong, Number min, boolean boundIsAllowed)
  {
    super(specific, boundIsAllowed ? LocalizedFormats.NUMBER_TOO_SMALL : LocalizedFormats.NUMBER_TOO_SMALL_BOUND_EXCLUDED, wrong, new Object[] { min });
    




    this.min = min;
    this.boundIsAllowed = boundIsAllowed;
  }
  


  public boolean getBoundIsAllowed()
  {
    return boundIsAllowed;
  }
  


  public Number getMin()
  {
    return min;
  }
}
