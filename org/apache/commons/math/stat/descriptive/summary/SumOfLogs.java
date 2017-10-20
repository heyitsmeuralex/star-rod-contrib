package org.apache.commons.math.stat.descriptive.summary;

import java.io.Serializable;
import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math.util.FastMath;














































public class SumOfLogs
  extends AbstractStorelessUnivariateStatistic
  implements Serializable
{
  private static final long serialVersionUID = -370076995648386763L;
  private int n;
  private double value;
  
  public SumOfLogs()
  {
    value = 0.0D;
    n = 0;
  }
  





  public SumOfLogs(SumOfLogs original)
  {
    copy(original, this);
  }
  



  public void increment(double d)
  {
    value += FastMath.log(d);
    n += 1;
  }
  



  public double getResult()
  {
    if (n > 0) {
      return value;
    }
    return NaN.0D;
  }
  



  public long getN()
  {
    return n;
  }
  



  public void clear()
  {
    value = 0.0D;
    n = 0;
  }
  

















  public double evaluate(double[] values, int begin, int length)
  {
    double sumLog = NaN.0D;
    if (test(values, begin, length)) {
      sumLog = 0.0D;
      for (int i = begin; i < begin + length; i++) {
        sumLog += FastMath.log(values[i]);
      }
    }
    return sumLog;
  }
  



  public SumOfLogs copy()
  {
    SumOfLogs result = new SumOfLogs();
    copy(this, result);
    return result;
  }
  







  public static void copy(SumOfLogs source, SumOfLogs dest)
  {
    dest.setData(source.getDataRef());
    n = n;
    value = value;
  }
}
