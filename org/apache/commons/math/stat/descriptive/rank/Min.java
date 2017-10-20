package org.apache.commons.math.stat.descriptive.rank;

import java.io.Serializable;
import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;








































public class Min
  extends AbstractStorelessUnivariateStatistic
  implements Serializable
{
  private static final long serialVersionUID = -2941995784909003131L;
  private long n;
  private double value;
  
  public Min()
  {
    n = 0L;
    value = NaN.0D;
  }
  





  public Min(Min original)
  {
    copy(original, this);
  }
  



  public void increment(double d)
  {
    if ((d < value) || (Double.isNaN(value))) {
      value = d;
    }
    n += 1L;
  }
  



  public void clear()
  {
    value = NaN.0D;
    n = 0L;
  }
  



  public double getResult()
  {
    return value;
  }
  


  public long getN()
  {
    return n;
  }
  






















  public double evaluate(double[] values, int begin, int length)
  {
    double min = NaN.0D;
    if (test(values, begin, length)) {
      min = values[begin];
      for (int i = begin; i < begin + length; i++) {
        if (!Double.isNaN(values[i])) {
          min = min < values[i] ? min : values[i];
        }
      }
    }
    return min;
  }
  



  public Min copy()
  {
    Min result = new Min();
    copy(this, result);
    return result;
  }
  







  public static void copy(Min source, Min dest)
  {
    dest.setData(source.getDataRef());
    n = n;
    value = value;
  }
}
