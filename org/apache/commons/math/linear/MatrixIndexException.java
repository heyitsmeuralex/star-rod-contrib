package org.apache.commons.math.linear;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.DummyLocalizable;
import org.apache.commons.math.exception.util.Localizable;




























public class MatrixIndexException
  extends MathRuntimeException
{
  private static final long serialVersionUID = 8120540015829487660L;
  
  @Deprecated
  public MatrixIndexException(String pattern, Object... arguments)
  {
    this(new DummyLocalizable(pattern), arguments);
  }
  





  public MatrixIndexException(Localizable pattern, Object... arguments)
  {
    super(pattern, arguments);
  }
}
