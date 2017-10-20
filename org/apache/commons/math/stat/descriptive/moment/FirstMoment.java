package org.apache.commons.math.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;




























































public class FirstMoment
  extends AbstractStorelessUnivariateStatistic
  implements Serializable
{
  private static final long serialVersionUID = 6112755307178490473L;
  protected long n;
  protected double m1;
  protected double dev;
  protected double nDev;
  
  public FirstMoment()
  {
    n = 0L;
    m1 = NaN.0D;
    dev = NaN.0D;
    nDev = NaN.0D;
  }
  






  public FirstMoment(FirstMoment original)
  {
    copy(original, this);
  }
  



  public void increment(double d)
  {
    if (n == 0L) {
      m1 = 0.0D;
    }
    n += 1L;
    double n0 = n;
    dev = (d - m1);
    nDev = (dev / n0);
    m1 += nDev;
  }
  



  public void clear()
  {
    m1 = NaN.0D;
    n = 0L;
    dev = NaN.0D;
    nDev = NaN.0D;
  }
  



  public double getResult()
  {
    return m1;
  }
  


  public long getN()
  {
    return n;
  }
  



  public FirstMoment copy()
  {
    FirstMoment result = new FirstMoment();
    copy(this, result);
    return result;
  }
  







  public static void copy(FirstMoment source, FirstMoment dest)
  {
    dest.setData(source.getDataRef());
    n = n;
    m1 = m1;
    dev = dev;
    nDev = nDev;
  }
}
