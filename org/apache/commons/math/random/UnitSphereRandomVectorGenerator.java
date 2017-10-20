package org.apache.commons.math.random;

import org.apache.commons.math.util.FastMath;



































public class UnitSphereRandomVectorGenerator
  implements RandomVectorGenerator
{
  private final RandomGenerator rand;
  private final int dimension;
  
  public UnitSphereRandomVectorGenerator(int dimension, RandomGenerator rand)
  {
    this.dimension = dimension;
    this.rand = rand;
  }
  




  public UnitSphereRandomVectorGenerator(int dimension)
  {
    this(dimension, new MersenneTwister());
  }
  

  public double[] nextVector()
  {
    double[] v = new double[dimension];
    double normSq;
    do
    {
      normSq = 0.0D;
      for (int i = 0; i < dimension; i++) {
        double comp = 2.0D * rand.nextDouble() - 1.0D;
        v[i] = comp;
        normSq += comp * comp;
      }
    } while (normSq > 1.0D);
    
    double f = 1.0D / FastMath.sqrt(normSq);
    for (int i = 0; i < dimension; i++) {
      v[i] *= f;
    }
    
    return v;
  }
}
