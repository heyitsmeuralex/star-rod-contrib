package org.apache.commons.math.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math.util.FastMath;




































public class Skewness
  extends AbstractStorelessUnivariateStatistic
  implements Serializable
{
  private static final long serialVersionUID = 7101857578996691352L;
  protected ThirdMoment moment = null;
  




  protected boolean incMoment;
  




  public Skewness()
  {
    incMoment = true;
    moment = new ThirdMoment();
  }
  



  public Skewness(ThirdMoment m3)
  {
    incMoment = false;
    moment = m3;
  }
  





  public Skewness(Skewness original)
  {
    copy(original, this);
  }
  



  public void increment(double d)
  {
    if (incMoment) {
      moment.increment(d);
    }
  }
  








  public double getResult()
  {
    if (moment.n < 3L) {
      return NaN.0D;
    }
    double variance = moment.m2 / (moment.n - 1L);
    if (variance < 1.0E-19D) {
      return 0.0D;
    }
    double n0 = moment.getN();
    return n0 * moment.m3 / ((n0 - 1.0D) * (n0 - 2.0D) * FastMath.sqrt(variance) * variance);
  }
  




  public long getN()
  {
    return moment.getN();
  }
  



  public void clear()
  {
    if (incMoment) {
      moment.clear();
    }
  }
  



















  public double evaluate(double[] values, int begin, int length)
  {
    double skew = NaN.0D;
    
    if ((test(values, begin, length)) && (length > 2)) {
      Mean mean = new Mean();
      
      double m = mean.evaluate(values, begin, length);
      



      double accum = 0.0D;
      double accum2 = 0.0D;
      for (int i = begin; i < begin + length; i++) {
        double d = values[i] - m;
        accum += d * d;
        accum2 += d;
      }
      double variance = (accum - accum2 * accum2 / length) / (length - 1);
      
      double accum3 = 0.0D;
      for (int i = begin; i < begin + length; i++) {
        double d = values[i] - m;
        accum3 += d * d * d;
      }
      accum3 /= variance * FastMath.sqrt(variance);
      

      double n0 = length;
      

      skew = n0 / ((n0 - 1.0D) * (n0 - 2.0D)) * accum3;
    }
    return skew;
  }
  



  public Skewness copy()
  {
    Skewness result = new Skewness();
    copy(this, result);
    return result;
  }
  







  public static void copy(Skewness source, Skewness dest)
  {
    dest.setData(source.getDataRef());
    moment = new ThirdMoment(moment.copy());
    incMoment = incMoment;
  }
}
