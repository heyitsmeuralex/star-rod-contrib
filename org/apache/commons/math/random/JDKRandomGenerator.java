package org.apache.commons.math.random;

import java.util.Random;





















public class JDKRandomGenerator
  extends Random
  implements RandomGenerator
{
  private static final long serialVersionUID = -7745277476784028798L;
  
  public JDKRandomGenerator() {}
  
  public void setSeed(int seed)
  {
    setSeed(seed);
  }
  

  public void setSeed(int[] seed)
  {
    long prime = 4294967291L;
    
    long combined = 0L;
    for (int s : seed) {
      combined = combined * 4294967291L + s;
    }
    setSeed(combined);
  }
}
