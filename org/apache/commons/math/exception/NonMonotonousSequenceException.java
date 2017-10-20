package org.apache.commons.math.exception;

import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.MathUtils.OrderDirection;















































public class NonMonotonousSequenceException
  extends MathIllegalNumberException
{
  private static final long serialVersionUID = 3596849179428944575L;
  private final MathUtils.OrderDirection direction;
  private final boolean strict;
  private final int index;
  private final Number previous;
  
  public NonMonotonousSequenceException(Number wrong, Number previous, int index)
  {
    this(wrong, previous, index, MathUtils.OrderDirection.INCREASING, true);
  }
  














  public NonMonotonousSequenceException(Number wrong, Number previous, int index, MathUtils.OrderDirection direction, boolean strict)
  {
    super(strict ? LocalizedFormats.NOT_STRICTLY_DECREASING_SEQUENCE : direction == MathUtils.OrderDirection.INCREASING ? LocalizedFormats.NOT_INCREASING_SEQUENCE : strict ? LocalizedFormats.NOT_STRICTLY_INCREASING_SEQUENCE : LocalizedFormats.NOT_DECREASING_SEQUENCE, wrong, new Object[] { previous, Integer.valueOf(index), Integer.valueOf(index - 1) });
    







    this.direction = direction;
    this.strict = strict;
    this.index = index;
    this.previous = previous;
  }
  


  public MathUtils.OrderDirection getDirection()
  {
    return direction;
  }
  

  public boolean getStrict()
  {
    return strict;
  }
  



  public int getIndex()
  {
    return index;
  }
  

  public Number getPrevious()
  {
    return previous;
  }
}
