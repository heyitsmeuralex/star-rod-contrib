package org.apache.commons.math.stat.descriptive.moment;

import java.io.Serializable;













































public class SecondMoment
  extends FirstMoment
  implements Serializable
{
  private static final long serialVersionUID = 3942403127395076445L;
  protected double m2;
  
  public SecondMoment()
  {
    m2 = NaN.0D;
  }
  





  public SecondMoment(SecondMoment original)
  {
    super(original);
    m2 = m2;
  }
  



  public void increment(double d)
  {
    if (n < 1L) {
      m1 = (this.m2 = 0.0D);
    }
    super.increment(d);
    m2 += (n - 1.0D) * dev * nDev;
  }
  



  public void clear()
  {
    super.clear();
    m2 = NaN.0D;
  }
  



  public double getResult()
  {
    return m2;
  }
  



  public SecondMoment copy()
  {
    SecondMoment result = new SecondMoment();
    copy(this, result);
    return result;
  }
  







  public static void copy(SecondMoment source, SecondMoment dest)
  {
    FirstMoment.copy(source, dest);
    m2 = m2;
  }
}
