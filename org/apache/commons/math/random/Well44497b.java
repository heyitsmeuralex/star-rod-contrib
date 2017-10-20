package org.apache.commons.math.random;









public class Well44497b
  extends AbstractWell
{
  private static final long serialVersionUID = 4032007538246675492L;
  






  private static final int K = 44497;
  






  private static final int M1 = 23;
  






  private static final int M2 = 481;
  






  private static final int M3 = 229;
  







  public Well44497b()
  {
    super(44497, 23, 481, 229);
  }
  


  public Well44497b(int seed)
  {
    super(44497, 23, 481, 229, seed);
  }
  



  public Well44497b(int[] seed)
  {
    super(44497, 23, 481, 229, seed);
  }
  


  public Well44497b(long seed)
  {
    super(44497, 23, 481, 229, seed);
  }
  




  protected int next(int bits)
  {
    int indexRm1 = iRm1[index];
    int indexRm2 = iRm2[index];
    
    int v0 = v[index];
    int vM1 = v[i1[index]];
    int vM2 = v[i2[index]];
    int vM3 = v[i3[index]];
    

    int z0 = 0x8000 & v[indexRm1] ^ 0x7FFF & v[indexRm2];
    int z1 = v0 ^ v0 << 24 ^ vM1 ^ vM1 >>> 30;
    int z2 = vM2 ^ vM2 << 10 ^ vM3 << 26;
    int z3 = z1 ^ z2;
    int z2Prime = (z2 << 9 ^ z2 >>> 23) & 0xFBFFFFFF;
    int z2Second = (z2 & 0x20000) != 0 ? z2Prime ^ 0xB729FCEC : z2Prime;
    int z4 = z0 ^ z1 ^ z1 >>> 20 ^ z2Second ^ z3;
    
    v[index] = z3;
    v[indexRm1] = z4;
    v[indexRm2] &= 0x8000;
    index = indexRm1;
    


    z4 ^= z4 << 7 & 0x93DD1400;
    z4 ^= z4 << 15 & 0xFA118000;
    
    return z4 >>> 32 - bits;
  }
}
