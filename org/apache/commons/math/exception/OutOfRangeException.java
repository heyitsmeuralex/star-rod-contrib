package org.apache.commons.math.exception;

import org.apache.commons.math.exception.util.LocalizedFormats;


































public class OutOfRangeException
  extends MathIllegalNumberException
{
  private static final long serialVersionUID = 111601815794403609L;
  private final Number lo;
  private final Number hi;
  
  public OutOfRangeException(Number wrong, Number lo, Number hi)
  {
    super(LocalizedFormats.OUT_OF_RANGE_SIMPLE, wrong, new Object[] { lo, hi });
    this.lo = lo;
    this.hi = hi;
  }
  

  public Number getLo()
  {
    return lo;
  }
  

  public Number getHi()
  {
    return hi;
  }
}
