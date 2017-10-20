package org.apache.commons.math.stat.descriptive;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NotPositiveException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;




































public abstract class AbstractUnivariateStatistic
  implements UnivariateStatistic
{
  private double[] storedData;
  
  public AbstractUnivariateStatistic() {}
  
  public void setData(double[] values)
  {
    storedData = (values == null ? null : (double[])values.clone());
  }
  



  public double[] getData()
  {
    return storedData == null ? null : (double[])storedData.clone();
  }
  



  protected double[] getDataRef()
  {
    return storedData;
  }
  






  public void setData(double[] values, int begin, int length)
  {
    storedData = new double[length];
    System.arraycopy(values, begin, storedData, 0, length);
  }
  






  public double evaluate()
  {
    return evaluate(storedData);
  }
  


  public double evaluate(double[] values)
  {
    test(values, 0, 0);
    return evaluate(values, 0, values.length);
  }
  










  public abstract double evaluate(double[] paramArrayOfDouble, int paramInt1, int paramInt2);
  









  public abstract UnivariateStatistic copy();
  









  protected boolean test(double[] values, int begin, int length)
  {
    if (values == null) {
      throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
    }
    
    if (begin < 0) {
      throw new NotPositiveException(LocalizedFormats.START_POSITION, Integer.valueOf(begin));
    }
    
    if (length < 0) {
      throw new NotPositiveException(LocalizedFormats.LENGTH, Integer.valueOf(length));
    }
    
    if (begin + length > values.length) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.SUBARRAY_ENDS_AFTER_ARRAY_END, new Object[0]);
    }
    

    if (length == 0) {
      return false;
    }
    
    return true;
  }
  


































  protected boolean test(double[] values, double[] weights, int begin, int length)
  {
    if (weights == null) {
      throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
    }
    
    if (weights.length != values.length) {
      throw new DimensionMismatchException(weights.length, values.length);
    }
    
    boolean containsPositiveWeight = false;
    for (int i = begin; i < begin + length; i++) {
      if (Double.isNaN(weights[i])) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NAN_ELEMENT_AT_INDEX, new Object[] { Integer.valueOf(i) });
      }
      
      if (Double.isInfinite(weights[i])) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INFINITE_ARRAY_ELEMENT, new Object[] { Double.valueOf(weights[i]), Integer.valueOf(i) });
      }
      
      if (weights[i] < 0.0D) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NEGATIVE_ELEMENT_AT_INDEX, new Object[] { Integer.valueOf(i), Double.valueOf(weights[i]) });
      }
      
      if ((!containsPositiveWeight) && (weights[i] > 0.0D)) {
        containsPositiveWeight = true;
      }
    }
    
    if (!containsPositiveWeight) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.WEIGHT_AT_LEAST_ONE_NON_ZERO, new Object[0]);
    }
    

    return test(values, begin, length);
  }
}
