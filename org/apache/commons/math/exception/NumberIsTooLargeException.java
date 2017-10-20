package org.apache.commons.math.exception;

import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;






































public class NumberIsTooLargeException
  extends MathIllegalNumberException
{
  private static final long serialVersionUID = 4330003017885151975L;
  private final Number max;
  private final boolean boundIsAllowed;
  
  public NumberIsTooLargeException(Number wrong, Number max, boolean boundIsAllowed)
  {
    this(null, wrong, max, boundIsAllowed);
  }
  









  public NumberIsTooLargeException(Localizable specific, Number wrong, Number max, boolean boundIsAllowed)
  {
    super(specific, boundIsAllowed ? LocalizedFormats.NUMBER_TOO_LARGE : LocalizedFormats.NUMBER_TOO_LARGE_BOUND_EXCLUDED, wrong, new Object[] { max });
    




    this.max = max;
    this.boundIsAllowed = boundIsAllowed;
  }
  


  public boolean getBoundIsAllowed()
  {
    return boundIsAllowed;
  }
  


  public Number getMax()
  {
    return max;
  }
}
