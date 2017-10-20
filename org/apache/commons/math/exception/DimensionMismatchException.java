package org.apache.commons.math.exception;

import org.apache.commons.math.exception.util.LocalizedFormats;































public class DimensionMismatchException
  extends MathIllegalNumberException
{
  private static final long serialVersionUID = -8415396756375798143L;
  private final int dimension;
  
  public DimensionMismatchException(int wrong, int expected)
  {
    super(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, Integer.valueOf(wrong), new Object[] { Integer.valueOf(expected) });
    dimension = expected;
  }
  


  public int getDimension()
  {
    return dimension;
  }
}
