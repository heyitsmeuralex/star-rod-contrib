package org.apache.commons.math.random;

import java.io.Serializable;





















































public abstract class AbstractWell
  extends BitsStreamGenerator
  implements Serializable
{
  private static final long serialVersionUID = -817701723016583596L;
  protected int index;
  protected final int[] v;
  protected final int[] iRm1;
  protected final int[] iRm2;
  protected final int[] i1;
  protected final int[] i2;
  protected final int[] i3;
  
  protected AbstractWell(int k, int m1, int m2, int m3)
  {
    this(k, m1, m2, m3, System.currentTimeMillis());
  }
  






  protected AbstractWell(int k, int m1, int m2, int m3, int seed)
  {
    this(k, m1, m2, m3, new int[] { seed });
  }
  











  protected AbstractWell(int k, int m1, int m2, int m3, int[] seed)
  {
    int w = 32;
    int r = (k + 32 - 1) / 32;
    v = new int[r];
    index = 0;
    


    iRm1 = new int[r];
    iRm2 = new int[r];
    i1 = new int[r];
    i2 = new int[r];
    i3 = new int[r];
    for (int j = 0; j < r; j++) {
      iRm1[j] = ((j + r - 1) % r);
      iRm2[j] = ((j + r - 2) % r);
      i1[j] = ((j + m1) % r);
      i2[j] = ((j + m2) % r);
      i3[j] = ((j + m3) % r);
    }
    

    setSeed(seed);
  }
  







  protected AbstractWell(int k, int m1, int m2, int m3, long seed)
  {
    this(k, m1, m2, m3, new int[] { (int)(seed >>> 32), (int)(seed & 0xFFFFFFFF) });
  }
  





  public void setSeed(int seed)
  {
    setSeed(new int[] { seed });
  }
  







  public void setSeed(int[] seed)
  {
    if (seed == null) {
      setSeed(System.currentTimeMillis());
      return;
    }
    
    System.arraycopy(seed, 0, v, 0, Math.min(seed.length, v.length));
    
    if (seed.length < v.length) {
      for (int i = seed.length; i < v.length; i++) {
        long l = v[(i - seed.length)];
        v[i] = ((int)(1812433253L * (l ^ l >> 30) + i & 0xFFFFFFFF));
      }
    }
    
    index = 0;
  }
  






  public void setSeed(long seed)
  {
    setSeed(new int[] { (int)(seed >>> 32), (int)(seed & 0xFFFFFFFF) });
  }
  
  protected abstract int next(int paramInt);
}
