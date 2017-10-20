package org.apache.commons.math.fraction;

import java.io.Serializable;
import org.apache.commons.math.Field;

































public class FractionField
  implements Field<Fraction>, Serializable
{
  private static final long serialVersionUID = -1257768487499119313L;
  
  private FractionField() {}
  
  public static FractionField getInstance()
  {
    return LazyHolder.INSTANCE;
  }
  
  public Fraction getOne()
  {
    return Fraction.ONE;
  }
  
  public Fraction getZero()
  {
    return Fraction.ZERO;
  }
  




  private static class LazyHolder
  {
    private static final FractionField INSTANCE = new FractionField(null);
    

    private LazyHolder() {}
  }
  

  private Object readResolve()
  {
    return LazyHolder.INSTANCE;
  }
}
