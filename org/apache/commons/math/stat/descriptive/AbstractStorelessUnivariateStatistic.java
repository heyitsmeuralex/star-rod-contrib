package org.apache.commons.math.stat.descriptive;

import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.MathUtils;












































public abstract class AbstractStorelessUnivariateStatistic
  extends AbstractUnivariateStatistic
  implements StorelessUnivariateStatistic
{
  public AbstractStorelessUnivariateStatistic() {}
  
  public double evaluate(double[] values)
  {
    if (values == null) {
      throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
    }
    return evaluate(values, 0, values.length);
  }
  





















  public double evaluate(double[] values, int begin, int length)
  {
    if (test(values, begin, length)) {
      clear();
      incrementAll(values, begin, length);
    }
    return getResult();
  }
  





  public abstract StorelessUnivariateStatistic copy();
  





  public abstract void clear();
  




  public abstract double getResult();
  




  public abstract void increment(double paramDouble);
  




  public void incrementAll(double[] values)
  {
    if (values == null) {
      throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
    }
    incrementAll(values, 0, values.length);
  }
  











  public void incrementAll(double[] values, int begin, int length)
  {
    if (test(values, begin, length)) {
      int k = begin + length;
      for (int i = begin; i < k; i++) {
        increment(values[i]);
      }
    }
  }
  







  public boolean equals(Object object)
  {
    if (object == this) {
      return true;
    }
    if (!(object instanceof AbstractStorelessUnivariateStatistic)) {
      return false;
    }
    AbstractStorelessUnivariateStatistic stat = (AbstractStorelessUnivariateStatistic)object;
    return (MathUtils.equalsIncludingNaN(stat.getResult(), getResult())) && (MathUtils.equalsIncludingNaN((float)stat.getN(), (float)getN()));
  }
  






  public int hashCode()
  {
    return 31 * (31 + MathUtils.hash(getResult())) + MathUtils.hash(getN());
  }
}
