package org.apache.commons.math.random;









public class Well19937c
  extends AbstractWell
{
  private static final long serialVersionUID = -7203498180754925124L;
  






  private static final int K = 19937;
  






  private static final int M1 = 70;
  






  private static final int M2 = 179;
  






  private static final int M3 = 449;
  







  public Well19937c()
  {
    super(19937, 70, 179, 449);
  }
  


  public Well19937c(int seed)
  {
    super(19937, 70, 179, 449, seed);
  }
  



  public Well19937c(int[] seed)
  {
    super(19937, 70, 179, 449, seed);
  }
  


  public Well19937c(long seed)
  {
    super(19937, 70, 179, 449, seed);
  }
  


  protected int next(int bits)
  {
    int indexRm1 = iRm1[index];
    int indexRm2 = iRm2[index];
    
    int v0 = v[index];
    int vM1 = v[i1[index]];
    int vM2 = v[i2[index]];
    int vM3 = v[i3[index]];
    
    int z0 = 0x80000000 & v[indexRm1] ^ 0x7FFFFFFF & v[indexRm2];
    int z1 = v0 ^ v0 << 25 ^ vM1 ^ vM1 >>> 27;
    int z2 = vM2 >>> 9 ^ vM3 ^ vM3 >>> 1;
    int z3 = z1 ^ z2;
    int z4 = z0 ^ z1 ^ z1 << 9 ^ z2 ^ z2 << 21 ^ z3 ^ z3 >>> 21;
    
    v[index] = z3;
    v[indexRm1] = z4;
    v[indexRm2] &= 0x80000000;
    index = indexRm1;
    



    z4 ^= z4 << 7 & 0xE46E1700;
    z4 ^= z4 << 15 & 0x9B868000;
    
    return z4 >>> 32 - bits;
  }
}
