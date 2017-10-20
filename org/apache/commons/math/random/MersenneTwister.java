package org.apache.commons.math.random;

import java.io.Serializable;
import org.apache.commons.math.util.FastMath;


















































































public class MersenneTwister
  extends BitsStreamGenerator
  implements Serializable
{
  private static final long serialVersionUID = 8661194735290153518L;
  private static final int N = 624;
  private static final int M = 397;
  private static final int[] MAG01 = { 0, -1727483681 };
  


  private int[] mt;
  

  private int mti;
  


  public MersenneTwister()
  {
    mt = new int['ɰ'];
    setSeed(System.currentTimeMillis());
  }
  


  public MersenneTwister(int seed)
  {
    mt = new int['ɰ'];
    setSeed(seed);
  }
  



  public MersenneTwister(int[] seed)
  {
    mt = new int['ɰ'];
    setSeed(seed);
  }
  


  public MersenneTwister(long seed)
  {
    mt = new int['ɰ'];
    setSeed(seed);
  }
  






  public void setSeed(int seed)
  {
    long longMT = seed;
    mt[0] = ((int)longMT);
    for (mti = 1; mti < 624; mti += 1)
    {

      longMT = 1812433253L * (longMT ^ longMT >> 30) + mti & 0xFFFFFFFF;
      mt[mti] = ((int)longMT);
    }
  }
  







  public void setSeed(int[] seed)
  {
    if (seed == null) {
      setSeed(System.currentTimeMillis());
      return;
    }
    
    setSeed(19650218);
    int i = 1;
    int j = 0;
    
    for (int k = FastMath.max(624, seed.length); k != 0; k--) {
      long l0 = mt[i] & 0x7FFFFFFF | (mt[i] < 0 ? 2147483648L : 0L);
      long l1 = mt[(i - 1)] & 0x7FFFFFFF | (mt[(i - 1)] < 0 ? 2147483648L : 0L);
      long l = (l0 ^ (l1 ^ l1 >> 30) * 1664525L) + seed[j] + j;
      mt[i] = ((int)(l & 0xFFFFFFFF));
      i++;j++;
      if (i >= 624) {
        mt[0] = mt['ɯ'];
        i = 1;
      }
      if (j >= seed.length) {
        j = 0;
      }
    }
    
    for (int k = 623; k != 0; k--) {
      long l0 = mt[i] & 0x7FFFFFFF | (mt[i] < 0 ? 2147483648L : 0L);
      long l1 = mt[(i - 1)] & 0x7FFFFFFF | (mt[(i - 1)] < 0 ? 2147483648L : 0L);
      long l = (l0 ^ (l1 ^ l1 >> 30) * 1566083941L) - i;
      mt[i] = ((int)(l & 0xFFFFFFFF));
      i++;
      if (i >= 624) {
        mt[0] = mt['ɯ'];
        i = 1;
      }
    }
    
    mt[0] = Integer.MIN_VALUE;
  }
  






  public void setSeed(long seed)
  {
    setSeed(new int[] { (int)(seed >>> 32), (int)(seed & 0xFFFFFFFF) });
  }
  












  protected int next(int bits)
  {
    if (mti >= 624) {
      int mtNext = mt[0];
      for (int k = 0; k < 227; k++) {
        int mtCurr = mtNext;
        mtNext = mt[(k + 1)];
        int y = mtCurr & 0x80000000 | mtNext & 0x7FFFFFFF;
        mt[k] = (mt[(k + 397)] ^ y >>> 1 ^ MAG01[(y & 0x1)]);
      }
      for (int k = 227; k < 623; k++) {
        int mtCurr = mtNext;
        mtNext = mt[(k + 1)];
        int y = mtCurr & 0x80000000 | mtNext & 0x7FFFFFFF;
        mt[k] = (mt[(k + 65309)] ^ y >>> 1 ^ MAG01[(y & 0x1)]);
      }
      int y = mtNext & 0x80000000 | mt[0] & 0x7FFFFFFF;
      mt['ɯ'] = (mt['ƌ'] ^ y >>> 1 ^ MAG01[(y & 0x1)]);
      
      mti = 0;
    }
    
    int y = mt[(mti++)];
    

    y ^= y >>> 11;
    y ^= y << 7 & 0x9D2C5680;
    y ^= y << 15 & 0xEFC60000;
    y ^= y >>> 18;
    
    return y >>> 32 - bits;
  }
}
