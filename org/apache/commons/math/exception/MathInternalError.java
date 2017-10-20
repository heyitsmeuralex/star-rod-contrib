package org.apache.commons.math.exception;

import org.apache.commons.math.exception.util.LocalizedFormats;



























public class MathInternalError
  extends MathIllegalStateException
{
  private static final long serialVersionUID = -6276776513966934846L;
  private static final String REPORT_URL = "https://issues.apache.org/jira/browse/MATH";
  
  public MathInternalError()
  {
    super(LocalizedFormats.INTERNAL_ERROR, new Object[] { "https://issues.apache.org/jira/browse/MATH" });
  }
  



  public MathInternalError(Throwable cause)
  {
    super(LocalizedFormats.INTERNAL_ERROR, new Object[] { "https://issues.apache.org/jira/browse/MATH" });
  }
}
