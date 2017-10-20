package org.apache.commons.math.geometry;

import org.apache.commons.math.MathException;
import org.apache.commons.math.exception.util.Localizable;

































public class NotARotationMatrixException
  extends MathException
{
  private static final long serialVersionUID = 5647178478658937642L;
  
  @Deprecated
  public NotARotationMatrixException(String specifier, Object... parts)
  {
    super(specifier, parts);
  }
  






  public NotARotationMatrixException(Localizable specifier, Object... parts)
  {
    super(specifier, parts);
  }
}
