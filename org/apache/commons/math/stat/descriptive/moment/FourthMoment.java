package org.apache.commons.math.stat.descriptive.moment;

import java.io.Serializable;





















































public class FourthMoment
  extends ThirdMoment
  implements Serializable
{
  private static final long serialVersionUID = 4763990447117157611L;
  protected double m4;
  
  public FourthMoment()
  {
    m4 = NaN.0D;
  }
  






  public FourthMoment(FourthMoment original)
  {
    copy(original, this);
  }
  



  public void increment(double d)
  {
    if (n < 1L) {
      m4 = 0.0D;
      m3 = 0.0D;
      m2 = 0.0D;
      m1 = 0.0D;
    }
    
    double prevM3 = m3;
    double prevM2 = m2;
    
    super.increment(d);
    
    double n0 = n;
    
    m4 = (m4 - 4.0D * nDev * prevM3 + 6.0D * nDevSq * prevM2 + (n0 * n0 - 3.0D * (n0 - 1.0D)) * (nDevSq * nDevSq * (n0 - 1.0D) * n0));
  }
  




  public double getResult()
  {
    return m4;
  }
  



  public void clear()
  {
    super.clear();
    m4 = NaN.0D;
  }
  



  public FourthMoment copy()
  {
    FourthMoment result = new FourthMoment();
    copy(this, result);
    return result;
  }
  







  public static void copy(FourthMoment source, FourthMoment dest)
  {
    ThirdMoment.copy(source, dest);
    m4 = m4;
  }
}
