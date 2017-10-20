package org.apache.commons.math.stat.descriptive.summary;

import java.io.Serializable;
import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;







































public class Sum
  extends AbstractStorelessUnivariateStatistic
  implements Serializable
{
  private static final long serialVersionUID = -8231831954703408316L;
  private long n;
  private double value;
  
  public Sum()
  {
    n = 0L;
    value = NaN.0D;
  }
  





  public Sum(Sum original)
  {
    copy(original, this);
  }
  



  public void increment(double d)
  {
    if (n == 0L) {
      value = d;
    } else {
      value += d;
    }
    n += 1L;
  }
  



  public double getResult()
  {
    return value;
  }
  


  public long getN()
  {
    return n;
  }
  



  public void clear()
  {
    value = NaN.0D;
    n = 0L;
  }
  














  public double evaluate(double[] values, int begin, int length)
  {
    double sum = NaN.0D;
    if (test(values, begin, length)) {
      sum = 0.0D;
      for (int i = begin; i < begin + length; i++) {
        sum += values[i];
      }
    }
    return sum;
  }
  



























  public double evaluate(double[] values, double[] weights, int begin, int length)
  {
    double sum = NaN.0D;
    if (test(values, weights, begin, length)) {
      sum = 0.0D;
      for (int i = begin; i < begin + length; i++) {
        sum += values[i] * weights[i];
      }
    }
    return sum;
  }
  





















  public double evaluate(double[] values, double[] weights)
  {
    return evaluate(values, weights, 0, values.length);
  }
  



  public Sum copy()
  {
    Sum result = new Sum();
    copy(this, result);
    return result;
  }
  







  public static void copy(Sum source, Sum dest)
  {
    dest.setData(source.getDataRef());
    n = n;
    value = value;
  }
}
