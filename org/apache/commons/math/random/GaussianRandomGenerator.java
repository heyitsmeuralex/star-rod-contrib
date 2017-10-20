package org.apache.commons.math.random;















public class GaussianRandomGenerator
  implements NormalizedRandomGenerator
{
  private final RandomGenerator generator;
  













  public GaussianRandomGenerator(RandomGenerator generator)
  {
    this.generator = generator;
  }
  


  public double nextNormalizedDouble()
  {
    return generator.nextGaussian();
  }
}
