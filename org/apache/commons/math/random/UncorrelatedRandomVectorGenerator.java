package org.apache.commons.math.random;

import java.util.Arrays;
import org.apache.commons.math.exception.DimensionMismatchException;










































public class UncorrelatedRandomVectorGenerator
  implements RandomVectorGenerator
{
  private final NormalizedRandomGenerator generator;
  private final double[] mean;
  private final double[] standardDeviation;
  
  public UncorrelatedRandomVectorGenerator(double[] mean, double[] standardDeviation, NormalizedRandomGenerator generator)
  {
    if (mean.length != standardDeviation.length) {
      throw new DimensionMismatchException(mean.length, standardDeviation.length);
    }
    this.mean = ((double[])mean.clone());
    this.standardDeviation = ((double[])standardDeviation.clone());
    this.generator = generator;
  }
  







  public UncorrelatedRandomVectorGenerator(int dimension, NormalizedRandomGenerator generator)
  {
    mean = new double[dimension];
    standardDeviation = new double[dimension];
    Arrays.fill(standardDeviation, 1.0D);
    this.generator = generator;
  }
  



  public double[] nextVector()
  {
    double[] random = new double[mean.length];
    for (int i = 0; i < random.length; i++) {
      random[i] = (mean[i] + standardDeviation[i] * generator.nextNormalizedDouble());
    }
    
    return random;
  }
}
