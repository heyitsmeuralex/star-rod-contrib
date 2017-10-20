package org.apache.commons.math.linear;

import org.apache.commons.math.MathException;
import org.apache.commons.math.exception.util.LocalizedFormats;




























public class NotPositiveDefiniteMatrixException
  extends MathException
{
  private static final long serialVersionUID = 4122929125438624648L;
  
  public NotPositiveDefiniteMatrixException()
  {
    super(LocalizedFormats.NOT_POSITIVE_DEFINITE_MATRIX, new Object[0]);
  }
}
