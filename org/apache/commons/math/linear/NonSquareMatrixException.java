package org.apache.commons.math.linear;

import org.apache.commons.math.exception.util.LocalizedFormats;




























public class NonSquareMatrixException
  extends InvalidMatrixException
{
  private static final long serialVersionUID = 8996207526636673730L;
  
  public NonSquareMatrixException(int rows, int columns)
  {
    super(LocalizedFormats.NON_SQUARE_MATRIX, new Object[] { Integer.valueOf(rows), Integer.valueOf(columns) });
  }
}
