package org.apache.commons.math.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math.stat.descriptive.WeightedEvaluation;





























































public class Variance
  extends AbstractStorelessUnivariateStatistic
  implements Serializable, WeightedEvaluation
{
  private static final long serialVersionUID = -9111962718267217978L;
  protected SecondMoment moment = null;
  





  protected boolean incMoment = true;
  





  private boolean isBiasCorrected = true;
  



  public Variance()
  {
    moment = new SecondMoment();
  }
  





  public Variance(SecondMoment m2)
  {
    incMoment = false;
    moment = m2;
  }
  







  public Variance(boolean isBiasCorrected)
  {
    moment = new SecondMoment();
    this.isBiasCorrected = isBiasCorrected;
  }
  








  public Variance(boolean isBiasCorrected, SecondMoment m2)
  {
    incMoment = false;
    moment = m2;
    this.isBiasCorrected = isBiasCorrected;
  }
  





  public Variance(Variance original)
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
    if (moment.n == 0L)
      return NaN.0D;
    if (moment.n == 1L) {
      return 0.0D;
    }
    if (isBiasCorrected) {
      return moment.m2 / (moment.n - 1.0D);
    }
    return moment.m2 / moment.n;
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
  
















  public double evaluate(double[] values)
  {
    if (values == null) {
      throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
    }
    return evaluate(values, 0, values.length);
  }
  





















  public double evaluate(double[] values, int begin, int length)
  {
    double var = NaN.0D;
    
    if (test(values, begin, length)) {
      clear();
      if (length == 1) {
        var = 0.0D;
      } else if (length > 1) {
        Mean mean = new Mean();
        double m = mean.evaluate(values, begin, length);
        var = evaluate(values, m, begin, length);
      }
    }
    return var;
  }
  











































  public double evaluate(double[] values, double[] weights, int begin, int length)
  {
    double var = NaN.0D;
    
    if (test(values, weights, begin, length)) {
      clear();
      if (length == 1) {
        var = 0.0D;
      } else if (length > 1) {
        Mean mean = new Mean();
        double m = mean.evaluate(values, weights, begin, length);
        var = evaluate(values, weights, m, begin, length);
      }
    }
    return var;
  }
  





































  public double evaluate(double[] values, double[] weights)
  {
    return evaluate(values, weights, 0, values.length);
  }
  



























  public double evaluate(double[] values, double mean, int begin, int length)
  {
    double var = NaN.0D;
    
    if (test(values, begin, length)) {
      if (length == 1) {
        var = 0.0D;
      } else if (length > 1) {
        double accum = 0.0D;
        double dev = 0.0D;
        double accum2 = 0.0D;
        for (int i = begin; i < begin + length; i++) {
          dev = values[i] - mean;
          accum += dev * dev;
          accum2 += dev;
        }
        double len = length;
        if (isBiasCorrected) {
          var = (accum - accum2 * accum2 / len) / (len - 1.0D);
        } else {
          var = (accum - accum2 * accum2 / len) / len;
        }
      }
    }
    return var;
  }
  
























  public double evaluate(double[] values, double mean)
  {
    return evaluate(values, mean, 0, values.length);
  }
  














































  public double evaluate(double[] values, double[] weights, double mean, int begin, int length)
  {
    double var = NaN.0D;
    
    if (test(values, weights, begin, length)) {
      if (length == 1) {
        var = 0.0D;
      } else if (length > 1) {
        double accum = 0.0D;
        double dev = 0.0D;
        double accum2 = 0.0D;
        for (int i = begin; i < begin + length; i++) {
          dev = values[i] - mean;
          accum += weights[i] * (dev * dev);
          accum2 += weights[i] * dev;
        }
        
        double sumWts = 0.0D;
        for (int i = 0; i < weights.length; i++) {
          sumWts += weights[i];
        }
        
        if (isBiasCorrected) {
          var = (accum - accum2 * accum2 / sumWts) / (sumWts - 1.0D);
        } else {
          var = (accum - accum2 * accum2 / sumWts) / sumWts;
        }
      }
    }
    return var;
  }
  








































  public double evaluate(double[] values, double[] weights, double mean)
  {
    return evaluate(values, weights, mean, 0, values.length);
  }
  


  public boolean isBiasCorrected()
  {
    return isBiasCorrected;
  }
  


  public void setBiasCorrected(boolean biasCorrected)
  {
    isBiasCorrected = biasCorrected;
  }
  



  public Variance copy()
  {
    Variance result = new Variance();
    copy(this, result);
    return result;
  }
  







  public static void copy(Variance source, Variance dest)
  {
    if ((source == null) || (dest == null))
    {
      throw new NullArgumentException();
    }
    dest.setData(source.getDataRef());
    moment = moment.copy();
    isBiasCorrected = isBiasCorrected;
    incMoment = incMoment;
  }
}
