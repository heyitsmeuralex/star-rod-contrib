package org.apache.commons.math.stat.descriptive.moment;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math.DimensionMismatchException;



























public class VectorialMean
  implements Serializable
{
  private static final long serialVersionUID = 8223009086481006892L;
  private final Mean[] means;
  
  public VectorialMean(int dimension)
  {
    means = new Mean[dimension];
    for (int i = 0; i < dimension; i++) {
      means[i] = new Mean();
    }
  }
  



  public void increment(double[] v)
    throws DimensionMismatchException
  {
    if (v.length != means.length) {
      throw new DimensionMismatchException(v.length, means.length);
    }
    for (int i = 0; i < v.length; i++) {
      means[i].increment(v[i]);
    }
  }
  



  public double[] getResult()
  {
    double[] result = new double[means.length];
    for (int i = 0; i < result.length; i++) {
      result[i] = means[i].getResult();
    }
    return result;
  }
  



  public long getN()
  {
    return means.length == 0 ? 0L : means[0].getN();
  }
  

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + Arrays.hashCode(means);
    return result;
  }
  

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (!(obj instanceof VectorialMean))
      return false;
    VectorialMean other = (VectorialMean)obj;
    if (!Arrays.equals(means, means))
      return false;
    return true;
  }
}
