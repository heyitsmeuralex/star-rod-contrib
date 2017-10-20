package org.apache.commons.math.util;

import java.io.Serializable;
import org.apache.commons.math.MathException;
import org.apache.commons.math.exception.util.LocalizedFormats;





























public class DefaultTransformer
  implements NumberTransformer, Serializable
{
  private static final long serialVersionUID = 4019938025047800455L;
  
  public DefaultTransformer() {}
  
  public double transform(Object o)
    throws MathException
  {
    if (o == null) {
      throw new MathException(LocalizedFormats.OBJECT_TRANSFORMATION, new Object[0]);
    }
    
    if ((o instanceof Number)) {
      return ((Number)o).doubleValue();
    }
    try
    {
      return Double.valueOf(o.toString()).doubleValue();
    } catch (NumberFormatException e) {
      throw new MathException(e, LocalizedFormats.CANNOT_TRANSFORM_TO_DOUBLE, new Object[] { e.getMessage() });
    }
  }
  


  public boolean equals(Object other)
  {
    if (this == other) {
      return true;
    }
    if (other == null) {
      return false;
    }
    return other instanceof DefaultTransformer;
  }
  


  public int hashCode()
  {
    return 401993047;
  }
}
