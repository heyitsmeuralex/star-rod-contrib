package org.apache.commons.math.random;

import java.util.Random;



























public class RandomAdaptor
  extends Random
  implements RandomGenerator
{
  private static final long serialVersionUID = 2306581345647615033L;
  private final RandomGenerator randomGenerator;
  
  private RandomAdaptor()
  {
    randomGenerator = null;
  }
  



  public RandomAdaptor(RandomGenerator randomGenerator)
  {
    this.randomGenerator = randomGenerator;
  }
  






  public static Random createAdaptor(RandomGenerator randomGenerator)
  {
    return new RandomAdaptor(randomGenerator);
  }
  









  public boolean nextBoolean()
  {
    return randomGenerator.nextBoolean();
  }
  








  public void nextBytes(byte[] bytes)
  {
    randomGenerator.nextBytes(bytes);
  }
  









  public double nextDouble()
  {
    return randomGenerator.nextDouble();
  }
  









  public float nextFloat()
  {
    return randomGenerator.nextFloat();
  }
  










  public double nextGaussian()
  {
    return randomGenerator.nextGaussian();
  }
  









  public int nextInt()
  {
    return randomGenerator.nextInt();
  }
  











  public int nextInt(int n)
  {
    return randomGenerator.nextInt(n);
  }
  









  public long nextLong()
  {
    return randomGenerator.nextLong();
  }
  
  public void setSeed(int seed)
  {
    if (randomGenerator != null) {
      randomGenerator.setSeed(seed);
    }
  }
  
  public void setSeed(int[] seed)
  {
    if (randomGenerator != null) {
      randomGenerator.setSeed(seed);
    }
  }
  

  public void setSeed(long seed)
  {
    if (randomGenerator != null) {
      randomGenerator.setSeed(seed);
    }
  }
}
