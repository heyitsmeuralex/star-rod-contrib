package org.apache.commons.math;

import org.apache.commons.math.exception.util.DummyLocalizable;
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;































public class MaxIterationsExceededException
  extends ConvergenceException
{
  private static final long serialVersionUID = -7821226672760574694L;
  private final int maxIterations;
  
  public MaxIterationsExceededException(int maxIterations)
  {
    super(LocalizedFormats.MAX_ITERATIONS_EXCEEDED, new Object[] { Integer.valueOf(maxIterations) });
    this.maxIterations = maxIterations;
  }
  








  @Deprecated
  public MaxIterationsExceededException(int maxIterations, String pattern, Object... arguments)
  {
    this(maxIterations, new DummyLocalizable(pattern), arguments);
  }
  








  public MaxIterationsExceededException(int maxIterations, Localizable pattern, Object... arguments)
  {
    super(pattern, arguments);
    this.maxIterations = maxIterations;
  }
  


  public int getMaxIterations()
  {
    return maxIterations;
  }
}
