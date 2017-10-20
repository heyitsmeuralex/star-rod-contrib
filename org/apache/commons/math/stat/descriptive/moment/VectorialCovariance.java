package org.apache.commons.math.stat.descriptive.moment;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.linear.MatrixUtils;
import org.apache.commons.math.linear.RealMatrix;



































public class VectorialCovariance
  implements Serializable
{
  private static final long serialVersionUID = 4118372414238930270L;
  private final double[] sums;
  private final double[] productsSums;
  private final boolean isBiasCorrected;
  private long n;
  
  public VectorialCovariance(int dimension, boolean isBiasCorrected)
  {
    sums = new double[dimension];
    productsSums = new double[dimension * (dimension + 1) / 2];
    n = 0L;
    this.isBiasCorrected = isBiasCorrected;
  }
  



  public void increment(double[] v)
    throws DimensionMismatchException
  {
    if (v.length != sums.length) {
      throw new DimensionMismatchException(v.length, sums.length);
    }
    int k = 0;
    for (int i = 0; i < v.length; i++) {
      sums[i] += v[i];
      for (int j = 0; j <= i; j++) {
        productsSums[(k++)] += v[i] * v[j];
      }
    }
    n += 1L;
  }
  




  public RealMatrix getResult()
  {
    int dimension = sums.length;
    RealMatrix result = MatrixUtils.createRealMatrix(dimension, dimension);
    
    if (n > 1L) {
      double c = 1.0D / (n * (isBiasCorrected ? n - 1L : n));
      int k = 0;
      for (int i = 0; i < dimension; i++) {
        for (int j = 0; j <= i; j++) {
          double e = c * (n * productsSums[(k++)] - sums[i] * sums[j]);
          result.setEntry(i, j, e);
          result.setEntry(j, i, e);
        }
      }
    }
    
    return result;
  }
  




  public long getN()
  {
    return n;
  }
  


  public void clear()
  {
    n = 0L;
    Arrays.fill(sums, 0.0D);
    Arrays.fill(productsSums, 0.0D);
  }
  

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (isBiasCorrected ? 1231 : 1237);
    result = 31 * result + (int)(n ^ n >>> 32);
    result = 31 * result + Arrays.hashCode(productsSums);
    result = 31 * result + Arrays.hashCode(sums);
    return result;
  }
  

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (!(obj instanceof VectorialCovariance))
      return false;
    VectorialCovariance other = (VectorialCovariance)obj;
    if (isBiasCorrected != isBiasCorrected)
      return false;
    if (n != n)
      return false;
    if (!Arrays.equals(productsSums, productsSums))
      return false;
    if (!Arrays.equals(sums, sums))
      return false;
    return true;
  }
}
