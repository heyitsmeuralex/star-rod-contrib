package org.apache.commons.math;

import org.apache.commons.math.exception.util.LocalizedFormats;






























/**
 * @deprecated
 */
public class DimensionMismatchException
  extends MathException
{
  private static final long serialVersionUID = -1316089546353786411L;
  private final int dimension1;
  private final int dimension2;
  
  public DimensionMismatchException(int dimension1, int dimension2)
  {
    super(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(dimension1), Integer.valueOf(dimension2) });
    this.dimension1 = dimension1;
    this.dimension2 = dimension2;
  }
  



  public int getDimension1()
  {
    return dimension1;
  }
  



  public int getDimension2()
  {
    return dimension2;
  }
}
