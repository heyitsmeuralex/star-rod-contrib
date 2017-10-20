package org.apache.commons.math;

import org.apache.commons.math.exception.util.LocalizedFormats;






























public class ArgumentOutsideDomainException
  extends FunctionEvaluationException
{
  private static final long serialVersionUID = -4965972841162580234L;
  
  public ArgumentOutsideDomainException(double argument, double lower, double upper)
  {
    super(argument, LocalizedFormats.ARGUMENT_OUTSIDE_DOMAIN, new Object[] { Double.valueOf(argument), Double.valueOf(lower), Double.valueOf(upper) });
  }
}
