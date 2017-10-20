package org.apache.commons.math.distribution;

import java.io.Serializable;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;













































public abstract class AbstractDistribution
  implements Distribution, Serializable
{
  private static final long serialVersionUID = -38038050983108802L;
  
  protected AbstractDistribution() {}
  
  public double cumulativeProbability(double x0, double x1)
    throws MathException
  {
    if (x0 > x1) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.LOWER_ENDPOINT_ABOVE_UPPER_ENDPOINT, new Object[] { Double.valueOf(x0), Double.valueOf(x1) });
    }
    

    return cumulativeProbability(x1) - cumulativeProbability(x0);
  }
}
