package org.apache.commons.math.fraction;

import java.io.Serializable;
import org.apache.commons.math.Field;

































public class BigFractionField
  implements Field<BigFraction>, Serializable
{
  private static final long serialVersionUID = -1699294557189741703L;
  
  private BigFractionField() {}
  
  public static BigFractionField getInstance()
  {
    return LazyHolder.INSTANCE;
  }
  
  public BigFraction getOne()
  {
    return BigFraction.ONE;
  }
  
  public BigFraction getZero()
  {
    return BigFraction.ZERO;
  }
  




  private static class LazyHolder
  {
    private static final BigFractionField INSTANCE = new BigFractionField(null);
    

    private LazyHolder() {}
  }
  

  private Object readResolve()
  {
    return LazyHolder.INSTANCE;
  }
}
