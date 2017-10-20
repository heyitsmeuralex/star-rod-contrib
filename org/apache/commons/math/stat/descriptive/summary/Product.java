package org.apache.commons.math.stat.descriptive.summary;

import java.io.Serializable;
import org.apache.commons.math.stat.descriptive.AbstractStorelessUnivariateStatistic;
import org.apache.commons.math.stat.descriptive.WeightedEvaluation;
import org.apache.commons.math.util.FastMath;






































public class Product
  extends AbstractStorelessUnivariateStatistic
  implements Serializable, WeightedEvaluation
{
  private static final long serialVersionUID = 2824226005990582538L;
  private long n;
  private double value;
  
  public Product()
  {
    n = 0L;
    value = NaN.0D;
  }
  





  public Product(Product original)
  {
    copy(original, this);
  }
  



  public void increment(double d)
  {
    if (n == 0L) {
      value = d;
    } else {
      value *= d;
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
    double product = NaN.0D;
    if (test(values, begin, length)) {
      product = 1.0D;
      for (int i = begin; i < begin + length; i++) {
        product *= values[i];
      }
    }
    return product;
  }
  




























  public double evaluate(double[] values, double[] weights, int begin, int length)
  {
    double product = NaN.0D;
    if (test(values, weights, begin, length)) {
      product = 1.0D;
      for (int i = begin; i < begin + length; i++) {
        product *= FastMath.pow(values[i], weights[i]);
      }
    }
    return product;
  }
  






















  public double evaluate(double[] values, double[] weights)
  {
    return evaluate(values, weights, 0, values.length);
  }
  




  public Product copy()
  {
    Product result = new Product();
    copy(this, result);
    return result;
  }
  







  public static void copy(Product source, Product dest)
  {
    dest.setData(source.getDataRef());
    n = n;
    value = value;
  }
}
