package org.apache.commons.math;

import org.apache.commons.math.exception.util.DummyLocalizable;
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;































public class MaxEvaluationsExceededException
  extends ConvergenceException
{
  private static final long serialVersionUID = -5921271447220129118L;
  private final int maxEvaluations;
  
  public MaxEvaluationsExceededException(int maxEvaluations)
  {
    super(LocalizedFormats.MAX_EVALUATIONS_EXCEEDED, new Object[] { Integer.valueOf(maxEvaluations) });
    this.maxEvaluations = maxEvaluations;
  }
  








  @Deprecated
  public MaxEvaluationsExceededException(int maxEvaluations, String pattern, Object... arguments)
  {
    this(maxEvaluations, new DummyLocalizable(pattern), arguments);
  }
  








  public MaxEvaluationsExceededException(int maxEvaluations, Localizable pattern, Object... arguments)
  {
    super(pattern, arguments);
    this.maxEvaluations = maxEvaluations;
  }
  


  public int getMaxEvaluations()
  {
    return maxEvaluations;
  }
}
