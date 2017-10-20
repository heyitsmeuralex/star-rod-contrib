package org.apache.commons.math;

import org.apache.commons.math.exception.util.LocalizedFormats;




























public class DuplicateSampleAbscissaException
  extends MathException
{
  private static final long serialVersionUID = -2271007547170169872L;
  
  public DuplicateSampleAbscissaException(double abscissa, int i1, int i2)
  {
    super(LocalizedFormats.DUPLICATED_ABSCISSA, new Object[] { Double.valueOf(abscissa), Integer.valueOf(i1), Integer.valueOf(i2) });
  }
  




  public double getDuplicateAbscissa()
  {
    return ((Double)getArguments()[0]).doubleValue();
  }
}
