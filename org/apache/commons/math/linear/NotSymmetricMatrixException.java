package org.apache.commons.math.linear;

import org.apache.commons.math.MathException;
import org.apache.commons.math.exception.util.LocalizedFormats;




























public class NotSymmetricMatrixException
  extends MathException
{
  private static final long serialVersionUID = -7012803946709786097L;
  
  public NotSymmetricMatrixException()
  {
    super(LocalizedFormats.NOT_SYMMETRIC_MATRIX, new Object[0]);
  }
}
