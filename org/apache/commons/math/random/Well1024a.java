package org.apache.commons.math.random;









public class Well1024a
  extends AbstractWell
{
  private static final long serialVersionUID = 5680173464174485492L;
  






  private static final int K = 1024;
  






  private static final int M1 = 3;
  






  private static final int M2 = 24;
  






  private static final int M3 = 10;
  







  public Well1024a()
  {
    super(1024, 3, 24, 10);
  }
  


  public Well1024a(int seed)
  {
    super(1024, 3, 24, 10, seed);
  }
  



  public Well1024a(int[] seed)
  {
    super(1024, 3, 24, 10, seed);
  }
  


  public Well1024a(long seed)
  {
    super(1024, 3, 24, 10, seed);
  }
  


  protected int next(int bits)
  {
    int indexRm1 = iRm1[index];
    
    int v0 = v[index];
    int vM1 = v[i1[index]];
    int vM2 = v[i2[index]];
    int vM3 = v[i3[index]];
    
    int z0 = v[indexRm1];
    int z1 = v0 ^ vM1 ^ vM1 >>> 8;
    int z2 = vM2 ^ vM2 << 19 ^ vM3 ^ vM3 << 14;
    int z3 = z1 ^ z2;
    int z4 = z0 ^ z0 << 11 ^ z1 ^ z1 << 7 ^ z2 ^ z2 << 13;
    
    v[index] = z3;
    v[indexRm1] = z4;
    index = indexRm1;
    
    return z4 >>> 32 - bits;
  }
}
