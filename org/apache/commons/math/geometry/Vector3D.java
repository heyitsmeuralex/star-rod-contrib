package org.apache.commons.math.geometry;

import java.io.Serializable;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;



























public class Vector3D
  implements Serializable
{
  public static final Vector3D ZERO = new Vector3D(0.0D, 0.0D, 0.0D);
  

  public static final Vector3D PLUS_I = new Vector3D(1.0D, 0.0D, 0.0D);
  

  public static final Vector3D MINUS_I = new Vector3D(-1.0D, 0.0D, 0.0D);
  

  public static final Vector3D PLUS_J = new Vector3D(0.0D, 1.0D, 0.0D);
  

  public static final Vector3D MINUS_J = new Vector3D(0.0D, -1.0D, 0.0D);
  

  public static final Vector3D PLUS_K = new Vector3D(0.0D, 0.0D, 1.0D);
  

  public static final Vector3D MINUS_K = new Vector3D(0.0D, 0.0D, -1.0D);
  


  public static final Vector3D NaN = new Vector3D(NaN.0D, NaN.0D, NaN.0D);
  


  public static final Vector3D POSITIVE_INFINITY = new Vector3D(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
  


  public static final Vector3D NEGATIVE_INFINITY = new Vector3D(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY);
  


  private static final Vector3DFormat DEFAULT_FORMAT = Vector3DFormat.getInstance();
  



  private static final long serialVersionUID = 5133268763396045979L;
  



  private final double x;
  


  private final double y;
  


  private final double z;
  



  public Vector3D(double x, double y, double z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  







  public Vector3D(double alpha, double delta)
  {
    double cosDelta = FastMath.cos(delta);
    x = (FastMath.cos(alpha) * cosDelta);
    y = (FastMath.sin(alpha) * cosDelta);
    z = FastMath.sin(delta);
  }
  





  public Vector3D(double a, Vector3D u)
  {
    x = (a * x);
    y = (a * y);
    z = (a * z);
  }
  







  public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2)
  {
    x = (a1 * x + a2 * x);
    y = (a1 * y + a2 * y);
    z = (a1 * z + a2 * z);
  }
  










  public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2, double a3, Vector3D u3)
  {
    x = (a1 * x + a2 * x + a3 * x);
    y = (a1 * y + a2 * y + a3 * y);
    z = (a1 * z + a2 * z + a3 * z);
  }
  












  public Vector3D(double a1, Vector3D u1, double a2, Vector3D u2, double a3, Vector3D u3, double a4, Vector3D u4)
  {
    x = (a1 * x + a2 * x + a3 * x + a4 * x);
    y = (a1 * y + a2 * y + a3 * y + a4 * y);
    z = (a1 * z + a2 * z + a3 * z + a4 * z);
  }
  



  public double getX()
  {
    return x;
  }
  



  public double getY()
  {
    return y;
  }
  



  public double getZ()
  {
    return z;
  }
  


  public double getNorm1()
  {
    return FastMath.abs(x) + FastMath.abs(y) + FastMath.abs(z);
  }
  


  public double getNorm()
  {
    return FastMath.sqrt(x * x + y * y + z * z);
  }
  


  public double getNormSq()
  {
    return x * x + y * y + z * z;
  }
  


  public double getNormInf()
  {
    return FastMath.max(FastMath.max(FastMath.abs(x), FastMath.abs(y)), FastMath.abs(z));
  }
  



  public double getAlpha()
  {
    return FastMath.atan2(y, x);
  }
  



  public double getDelta()
  {
    return FastMath.asin(z / getNorm());
  }
  



  public Vector3D add(Vector3D v)
  {
    return new Vector3D(x + x, y + y, z + z);
  }
  




  public Vector3D add(double factor, Vector3D v)
  {
    return new Vector3D(x + factor * x, y + factor * y, z + factor * z);
  }
  



  public Vector3D subtract(Vector3D v)
  {
    return new Vector3D(x - x, y - y, z - z);
  }
  




  public Vector3D subtract(double factor, Vector3D v)
  {
    return new Vector3D(x - factor * x, y - factor * y, z - factor * z);
  }
  



  public Vector3D normalize()
  {
    double s = getNorm();
    if (s == 0.0D) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.CANNOT_NORMALIZE_A_ZERO_NORM_VECTOR, new Object[0]);
    }
    return scalarMultiply(1.0D / s);
  }
  















  public Vector3D orthogonal()
  {
    double threshold = 0.6D * getNorm();
    if (threshold == 0.0D) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
    }
    
    if ((x >= -threshold) && (x <= threshold)) {
      double inverse = 1.0D / FastMath.sqrt(y * y + z * z);
      return new Vector3D(0.0D, inverse * z, -inverse * y); }
    if ((y >= -threshold) && (y <= threshold)) {
      double inverse = 1.0D / FastMath.sqrt(x * x + z * z);
      return new Vector3D(-inverse * z, 0.0D, inverse * x);
    }
    double inverse = 1.0D / FastMath.sqrt(x * x + y * y);
    return new Vector3D(inverse * y, -inverse * x, 0.0D);
  }
  












  public static double angle(Vector3D v1, Vector3D v2)
  {
    double normProduct = v1.getNorm() * v2.getNorm();
    if (normProduct == 0.0D) {
      throw MathRuntimeException.createArithmeticException(LocalizedFormats.ZERO_NORM, new Object[0]);
    }
    
    double dot = dotProduct(v1, v2);
    double threshold = normProduct * 0.9999D;
    if ((dot < -threshold) || (dot > threshold))
    {
      Vector3D v3 = crossProduct(v1, v2);
      if (dot >= 0.0D) {
        return FastMath.asin(v3.getNorm() / normProduct);
      }
      return 3.141592653589793D - FastMath.asin(v3.getNorm() / normProduct);
    }
    

    return FastMath.acos(dot / normProduct);
  }
  



  public Vector3D negate()
  {
    return new Vector3D(-x, -y, -z);
  }
  



  public Vector3D scalarMultiply(double a)
  {
    return new Vector3D(a * x, a * y, a * z);
  }
  



  public boolean isNaN()
  {
    return (Double.isNaN(x)) || (Double.isNaN(y)) || (Double.isNaN(z));
  }
  





  public boolean isInfinite()
  {
    return (!isNaN()) && ((Double.isInfinite(x)) || (Double.isInfinite(y)) || (Double.isInfinite(z)));
  }
  




















  public boolean equals(Object other)
  {
    if (this == other) {
      return true;
    }
    
    if ((other instanceof Vector3D)) {
      Vector3D rhs = (Vector3D)other;
      if (rhs.isNaN()) {
        return isNaN();
      }
      
      return (x == x) && (y == y) && (z == z);
    }
    return false;
  }
  







  public int hashCode()
  {
    if (isNaN()) {
      return 8;
    }
    return 31 * (23 * MathUtils.hash(x) + 19 * MathUtils.hash(y) + MathUtils.hash(z));
  }
  




  public static double dotProduct(Vector3D v1, Vector3D v2)
  {
    return x * x + y * y + z * z;
  }
  




  public static Vector3D crossProduct(Vector3D v1, Vector3D v2)
  {
    return new Vector3D(y * z - z * y, z * x - x * z, x * y - y * x);
  }
  









  public static double distance1(Vector3D v1, Vector3D v2)
  {
    double dx = FastMath.abs(x - x);
    double dy = FastMath.abs(y - y);
    double dz = FastMath.abs(z - z);
    return dx + dy + dz;
  }
  







  public static double distance(Vector3D v1, Vector3D v2)
  {
    double dx = x - x;
    double dy = y - y;
    double dz = z - z;
    return FastMath.sqrt(dx * dx + dy * dy + dz * dz);
  }
  







  public static double distanceInf(Vector3D v1, Vector3D v2)
  {
    double dx = FastMath.abs(x - x);
    double dy = FastMath.abs(y - y);
    double dz = FastMath.abs(z - z);
    return FastMath.max(FastMath.max(dx, dy), dz);
  }
  







  public static double distanceSq(Vector3D v1, Vector3D v2)
  {
    double dx = x - x;
    double dy = y - y;
    double dz = z - z;
    return dx * dx + dy * dy + dz * dz;
  }
  



  public String toString()
  {
    return DEFAULT_FORMAT.format(this);
  }
}
