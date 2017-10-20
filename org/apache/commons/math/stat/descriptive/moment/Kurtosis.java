package org.apache.commons.math.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math.util.FastMath;

















































public class Kurtosis
  extends AbstractStorelessUnivariateStatistic
  implements Serializable
{
  private static final long serialVersionUID = 2784465764798260919L;
  protected FourthMoment moment;
  protected boolean incMoment;
  
  public Kurtosis()
  {
    incMoment = true;
    moment = new FourthMoment();
  }
  




  public Kurtosis(FourthMoment m4)
  {
    incMoment = false;
    moment = m4;
  }
  





  public Kurtosis(Kurtosis original)
  {
    copy(original, this);
  }
  



  public void increment(double d)
  {
    if (incMoment) {
      moment.increment(d);
    } else {
      throw MathRuntimeException.createIllegalStateException(LocalizedFormats.CANNOT_INCREMENT_STATISTIC_CONSTRUCTED_FROM_EXTERNAL_MOMENTS, new Object[0]);
    }
  }
  




  public double getResult()
  {
    double kurtosis = NaN.0D;
    if (moment.getN() > 3L) {
      double variance = moment.m2 / (moment.n - 1L);
      if ((moment.n <= 3L) || (variance < 1.0E-19D)) {
        kurtosis = 0.0D;
      } else {
        double n = moment.n;
        kurtosis = (n * (n + 1.0D) * moment.m4 - 3.0D * moment.m2 * moment.m2 * (n - 1.0D)) / ((n - 1.0D) * (n - 2.0D) * (n - 3.0D) * variance * variance);
      }
    }
    


    return kurtosis;
  }
  



  public void clear()
  {
    if (incMoment) {
      moment.clear();
    } else {
      throw MathRuntimeException.createIllegalStateException(LocalizedFormats.CANNOT_CLEAR_STATISTIC_CONSTRUCTED_FROM_EXTERNAL_MOMENTS, new Object[0]);
    }
  }
  



  public long getN()
  {
    return moment.getN();
  }
  



















  public double evaluate(double[] values, int begin, int length)
  {
    double kurt = NaN.0D;
    
    if ((test(values, begin, length)) && (length > 3))
    {

      Variance variance = new Variance();
      variance.incrementAll(values, begin, length);
      double mean = moment.m1;
      double stdDev = FastMath.sqrt(variance.getResult());
      


      double accum3 = 0.0D;
      for (int i = begin; i < begin + length; i++) {
        accum3 += FastMath.pow(values[i] - mean, 4.0D);
      }
      accum3 /= FastMath.pow(stdDev, 4.0D);
      

      double n0 = length;
      
      double coefficientOne = n0 * (n0 + 1.0D) / ((n0 - 1.0D) * (n0 - 2.0D) * (n0 - 3.0D));
      
      double termTwo = 3.0D * FastMath.pow(n0 - 1.0D, 2.0D) / ((n0 - 2.0D) * (n0 - 3.0D));
      


      kurt = coefficientOne * accum3 - termTwo;
    }
    return kurt;
  }
  



  public Kurtosis copy()
  {
    Kurtosis result = new Kurtosis();
    copy(this, result);
    return result;
  }
  







  public static void copy(Kurtosis source, Kurtosis dest)
  {
    dest.setData(source.getDataRef());
    moment = moment.copy();
    incMoment = incMoment;
  }
}
