package org.apache.commons.math.optimization.linear;

import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.optimization.OptimizationException;


























public class UnboundedSolutionException
  extends OptimizationException
{
  private static final long serialVersionUID = 940539497277290619L;
  
  public UnboundedSolutionException()
  {
    super(LocalizedFormats.UNBOUNDED_SOLUTION, new Object[0]);
  }
}
