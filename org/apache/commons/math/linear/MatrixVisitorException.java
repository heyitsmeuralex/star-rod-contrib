package org.apache.commons.math.linear;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.DummyLocalizable;
import org.apache.commons.math.exception.util.Localizable;


























public class MatrixVisitorException
  extends MathRuntimeException
{
  private static final long serialVersionUID = 3814333035048617048L;
  
  public MatrixVisitorException(String pattern, Object[] arguments)
  {
    super(new DummyLocalizable(pattern), arguments);
  }
  





  public MatrixVisitorException(Localizable pattern, Object[] arguments)
  {
    super(pattern, arguments);
  }
}
