package org.apache.commons.math.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math.stat.descriptive.StorelessUnivariateStatistic;
import org.apache.commons.math.stat.descriptive.summary.SumOfLogs;
import org.apache.commons.math.util.FastMath;













































public class GeometricMean
  extends AbstractStorelessUnivariateStatistic
  implements Serializable
{
  private static final long serialVersionUID = -8178734905303459453L;
  private StorelessUnivariateStatistic sumOfLogs;
  
  public GeometricMean()
  {
    sumOfLogs = new SumOfLogs();
  }
  






  public GeometricMean(GeometricMean original)
  {
    copy(original, this);
  }
  



  public GeometricMean(SumOfLogs sumOfLogs)
  {
    this.sumOfLogs = sumOfLogs;
  }
  



  public GeometricMean copy()
  {
    GeometricMean result = new GeometricMean();
    copy(this, result);
    return result;
  }
  



  public void increment(double d)
  {
    sumOfLogs.increment(d);
  }
  



  public double getResult()
  {
    if (sumOfLogs.getN() > 0L) {
      return FastMath.exp(sumOfLogs.getResult() / sumOfLogs.getN());
    }
    return NaN.0D;
  }
  




  public void clear()
  {
    sumOfLogs.clear();
  }
  

















  public double evaluate(double[] values, int begin, int length)
  {
    return FastMath.exp(sumOfLogs.evaluate(values, begin, length) / length);
  }
  



  public long getN()
  {
    return sumOfLogs.getN();
  }
  











  public void setSumLogImpl(StorelessUnivariateStatistic sumLogImpl)
  {
    checkEmpty();
    sumOfLogs = sumLogImpl;
  }
  




  public StorelessUnivariateStatistic getSumLogImpl()
  {
    return sumOfLogs;
  }
  







  public static void copy(GeometricMean source, GeometricMean dest)
  {
    dest.setData(source.getDataRef());
    sumOfLogs = sumOfLogs.copy();
  }
  



  private void checkEmpty()
  {
    if (getN() > 0L) {
      throw MathRuntimeException.createIllegalStateException(LocalizedFormats.VALUES_ADDED_BEFORE_CONFIGURING_STATISTIC, new Object[] { Long.valueOf(getN()) });
    }
  }
}
