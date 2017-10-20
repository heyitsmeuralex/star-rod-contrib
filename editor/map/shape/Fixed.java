package editor.map.shape;

import java.io.PrintStream;



public final class Fixed
  implements Comparable<Fixed>
{
  public static final Fixed MIN = new Fixed(Integer.MIN_VALUE);
  public static final Fixed MAX = new Fixed(Integer.MAX_VALUE);
  public static final Fixed STEP = new Fixed(1);
  private final int v;
  
  public static void main(String[] args)
  {
    Fixed a = new Fixed((short)0, (short)1);
    
    Fixed b = new Fixed(2147418112);
    Fixed c = new Fixed(Integer.MAX_VALUE);
    Fixed d = new Fixed(Integer.MIN_VALUE);
    Fixed e = new Fixed(-2147418113);
    System.out.println(a.toDouble());
    System.out.println(b.toDouble());
    System.out.println(c.toDouble());
    System.out.println(d.toDouble());
    System.out.println(e.toDouble());
  }
  
  private Fixed(int v)
  {
    this.v = v;
  }
  
  public Fixed(float f)
  {
    v = ((int)(f * 65536.0F));
  }
  
  public Fixed(double d)
  {
    v = ((int)(d * 65536.0D));
  }
  
  public Fixed(short whole, short frac)
  {
    v = (whole << 16 | frac & 0xFFFF);
  }
  
  public short getWholePart()
  {
    return (short)(v >> 16);
  }
  
  public short getFracPart()
  {
    return (short)v;
  }
  
  public String toString()
  {
    return String.format("%08X", new Object[] { Integer.valueOf(v) });
  }
  

  public float toFloat()
  {
    return v / 65536.0F;
  }
  
  public double toDouble()
  {
    return v / 65536.0D;
  }
  
  public static Fixed[][] fromMatrix(double[][] mat)
  {
    Fixed[][] fmat = new Fixed[mat.length][];
    for (int i = 0; i < mat.length; i++)
    {
      fmat[i] = new Fixed[mat[i].length];
      for (int j = 0; j < mat[i].length; j++)
        fmat[i][j] = new Fixed(mat[i][j]);
    }
    return fmat;
  }
  
  public static double[][] toMatrix(Fixed[][] fmat)
  {
    double[][] mat = new double[fmat.length][];
    for (int i = 0; i < fmat.length; i++)
    {
      mat[i] = new double[fmat[i].length];
      for (int j = 0; j < fmat[i].length; j++)
        mat[i][j] = fmat[i][j].toDouble();
    }
    return mat;
  }
  
  public static Fixed neg(Fixed a)
  {
    return new Fixed(-v);
  }
  

  public static Fixed round(Fixed a)
  {
    int u = v;
    if ((u & 0x80000000) != 0) {
      u -= 32768;
    } else {
      u += 32768;
    }
    int trunc = u >> 16 << 16;
    
    return new Fixed(trunc);
  }
  

  public static Fixed floor(Fixed a)
  {
    return new Fixed(v >> 16 << 16);
  }
  

  public static Fixed ceil(Fixed a)
  {
    int u = v + 65535;
    int trunc = u >> 16 << 16;
    return new Fixed(trunc);
  }
  
  public static Fixed add(Fixed a, Fixed b)
  {
    return new Fixed(v + v);
  }
  
  public static Fixed sub(Fixed a, Fixed b)
  {
    return new Fixed(v - v);
  }
  
  public static Fixed mul(Fixed a, Fixed b)
  {
    long prod = v * v;
    return new Fixed((int)(prod >> 16));
  }
  
  public static Fixed div(Fixed a, Fixed b)
  {
    long num = v << 16;
    long div = v;
    return new Fixed((int)(num / div));
  }
  
  public static Fixed sqrt(Fixed a)
  {
    return new Fixed(Math.sqrt(a.toDouble()));
  }
  
  public static Fixed sin(Fixed a)
  {
    return new Fixed(Math.sin(a.toDouble()));
  }
  
  public static Fixed sinDeg(Fixed a)
  {
    return new Fixed(Math.sin(Math.toRadians(a.toDouble())));
  }
  
  public static Fixed cos(Fixed a)
  {
    return new Fixed(Math.cos(a.toDouble()));
  }
  
  public static Fixed cosDeg(Fixed a)
  {
    return new Fixed(Math.cos(Math.toRadians(a.toDouble())));
  }
  

  public int compareTo(Fixed other)
  {
    return v - v;
  }
  

  public int hashCode()
  {
    return v;
  }
  

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Fixed other = (Fixed)obj;
    return v == v;
  }
}
