package org.apache.commons.math.linear;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.DummyLocalizable;
import org.apache.commons.math.exception.util.Localizable;






























public class InvalidMatrixException
  extends MathRuntimeException
{
  private static final long serialVersionUID = -2068020346562029801L;
  
  @Deprecated
  public InvalidMatrixException(String pattern, Object... arguments)
  {
    this(new DummyLocalizable(pattern), arguments);
  }
  





  public InvalidMatrixException(Localizable pattern, Object... arguments)
  {
    super(pattern, arguments);
  }
  





  public InvalidMatrixException(Throwable cause)
  {
    super(cause);
  }
}
