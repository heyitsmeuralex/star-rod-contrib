package org.apache.commons.math.optimization.linear;

import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.optimization.OptimizationException;


























public class NoFeasibleSolutionException
  extends OptimizationException
{
  private static final long serialVersionUID = -3044253632189082760L;
  
  public NoFeasibleSolutionException()
  {
    super(LocalizedFormats.NO_FEASIBLE_SOLUTION, new Object[0]);
  }
}
