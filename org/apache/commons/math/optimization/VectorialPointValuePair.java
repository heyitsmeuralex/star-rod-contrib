package org.apache.commons.math.optimization;

import java.io.Serializable;


































public class VectorialPointValuePair
  implements Serializable
{
  private static final long serialVersionUID = 1003888396256744753L;
  private final double[] point;
  private final double[] value;
  
  public VectorialPointValuePair(double[] point, double[] value)
  {
    this.point = (point == null ? null : (double[])point.clone());
    this.value = (value == null ? null : (double[])value.clone());
  }
  







  public VectorialPointValuePair(double[] point, double[] value, boolean copyArray)
  {
    this.point = (copyArray ? (double[])point.clone() : point == null ? null : point);
    

    this.value = (copyArray ? (double[])value.clone() : value == null ? null : value);
  }
  




  public double[] getPoint()
  {
    return point == null ? null : (double[])point.clone();
  }
  




  public double[] getPointRef()
  {
    return point;
  }
  


  public double[] getValue()
  {
    return value == null ? null : (double[])value.clone();
  }
  




  public double[] getValueRef()
  {
    return value;
  }
}
