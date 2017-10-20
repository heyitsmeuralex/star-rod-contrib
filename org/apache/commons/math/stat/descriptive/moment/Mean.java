package org.apache.commons.math.stat.descriptive.moment;

import java.io.Serializable;
import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math.stat.descriptive.WeightedEvaluation;
import org.apache.commons.math.stat.descriptive.summary.Sum;





























































public class Mean
  extends AbstractStorelessUnivariateStatistic
  implements Serializable, WeightedEvaluation
{
  private static final long serialVersionUID = -1296043746617791564L;
  protected FirstMoment moment;
  protected boolean incMoment;
  
  public Mean()
  {
    incMoment = true;
    moment = new FirstMoment();
  }
  




  public Mean(FirstMoment m1)
  {
    moment = m1;
    incMoment = false;
  }
  





  public Mean(Mean original)
  {
    copy(original, this);
  }
  



  public void increment(double d)
  {
    if (incMoment) {
      moment.increment(d);
    }
  }
  



  public void clear()
  {
    if (incMoment) {
      moment.clear();
    }
  }
  



  public double getResult()
  {
    return moment.m1;
  }
  


  public long getN()
  {
    return moment.getN();
  }
  
















  public double evaluate(double[] values, int begin, int length)
  {
    if (test(values, begin, length)) {
      Sum sum = new Sum();
      double sampleSize = length;
      

      double xbar = sum.evaluate(values, begin, length) / sampleSize;
      

      double correction = 0.0D;
      for (int i = begin; i < begin + length; i++) {
        correction += values[i] - xbar;
      }
      return xbar + correction / sampleSize;
    }
    return NaN.0D;
  }
  





























  public double evaluate(double[] values, double[] weights, int begin, int length)
  {
    if (test(values, weights, begin, length)) {
      Sum sum = new Sum();
      

      double sumw = sum.evaluate(weights, begin, length);
      double xbarw = sum.evaluate(values, weights, begin, length) / sumw;
      

      double correction = 0.0D;
      for (int i = begin; i < begin + length; i++) {
        correction += weights[i] * (values[i] - xbarw);
      }
      return xbarw + correction / sumw;
    }
    return NaN.0D;
  }
  























  public double evaluate(double[] values, double[] weights)
  {
    return evaluate(values, weights, 0, values.length);
  }
  



  public Mean copy()
  {
    Mean result = new Mean();
    copy(this, result);
    return result;
  }
  








  public static void copy(Mean source, Mean dest)
  {
    dest.setData(source.getDataRef());
    incMoment = incMoment;
    moment = moment.copy();
  }
}
