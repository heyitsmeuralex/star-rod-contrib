package editor.map.shape;

import editor.map.Axis;
import editor.map.MutableAngle;
import editor.map.MutablePoint;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import org.lwjgl.util.vector.Vector3f;
import util.Logger;



public class TransformMatrix
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private double[][] mat;
  
  public TransformMatrix()
  {
    mat = new double[4][4];
  }
  
  public TransformMatrix(TransformMatrix other)
  {
    mat = new double[4][4];
    for (int row = 0; row < 4; row++) {
      for (int col = 0; col < 4; col++)
        mat[row][col] = mat[row][col];
    }
  }
  
  public TransformMatrix deepCopy() {
    return new TransformMatrix(this);
  }
  
  public double get(int row, int col)
  {
    return mat[row][col];
  }
  
  public void set(double value, int row, int col)
  {
    mat[row][col] = value;
  }
  







  public void readRDP(ByteBuffer bb)
  {
    short[][] whole = new short[4][4];
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++)
        whole[j][i] = bb.getShort();
    }
    short[][] frac = new short[4][4];
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++)
        frac[j][i] = bb.getShort();
    }
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++)
      {
        int v = whole[i][j] << 16 | frac[i][j] & 0xFFFF;
        mat[i][j] = (v / 65536.0D);
      }
    }
  }
  















  public void writeRDP(RandomAccessFile raf)
    throws IOException
  {
    short[][] whole = new short[4][4];
    short[][] frac = new short[4][4];
    
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++)
      {
        int v = (int)(mat[i][j] * 65536.0D);
        whole[i][j] = ((short)(v >> 16));
        frac[i][j] = ((short)v);
      }
    }
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++)
        raf.writeShort(whole[j][i]);
    }
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        raf.writeShort(frac[j][i]);
      }
    }
  }
  
  public int hashCode() {
    return Arrays.deepHashCode(mat);
  }
  

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof TransformMatrix)) {
      return false;
    }
    TransformMatrix other = (TransformMatrix)obj;
    for (int row = 0; row < 4; row++)
      for (int col = 0; col < 4; col++)
        if (mat[row][col] != mat[row][col])
          return false;
    return true;
  }
  
  public boolean isZero()
  {
    for (int row = 0; row < 4; row++)
      for (int col = 0; col < 4; col++)
        if (mat[row][col] != 0.0D)
          return false;
    return true;
  }
  
  public void transpose()
  {
    double[][] temp = new double[4][4];
    
    for (int row = 0; row < 4; row++) {
      for (int col = 0; col < 4; col++)
        temp[row][col] = mat[col][row];
    }
    mat = temp;
  }
  
  public void setIdentity()
  {
    mat = new double[4][4];
    mat[0][0] = 1.0D;
    mat[1][1] = 1.0D;
    mat[2][2] = 1.0D;
    mat[3][3] = 1.0D;
  }
  
  public void setTranslation(double dx, double dy, double dz)
  {
    setIdentity();
    mat[0][3] = dx;
    mat[1][3] = dy;
    mat[2][3] = dz;
  }
  
  public void translate(double dx, double dy, double dz)
  {
    mat[0][3] += dx;
    mat[1][3] += dy;
    mat[2][3] += dz;
  }
  
  public void translate(Vector3f vec)
  {
    mat[0][3] += x;
    mat[1][3] += y;
    mat[2][3] += z;
  }
  
  public void translateMinus(double dx, double dy, double dz)
  {
    mat[0][3] -= dx;
    mat[1][3] -= dy;
    mat[2][3] -= dz;
  }
  
  public void translateMinus(Vector3f vec)
  {
    mat[0][3] -= x;
    mat[1][3] -= y;
    mat[2][3] -= z;
  }
  
  public void setRotation(Axis axis, double angle)
  {
    mat[3][3] = 1.0D;
    
    double cosAngle = Math.cos(Math.toRadians(angle));
    double sinAngle = Math.sin(Math.toRadians(angle));
    
    switch (1.$SwitchMap$editor$map$Axis[axis.ordinal()])
    {
    case 1: 
      mat[0][0] = 1.0D;
      mat[1][1] = cosAngle;
      mat[2][2] = cosAngle;
      mat[2][1] = sinAngle;
      mat[1][2] = (-sinAngle);
      break;
    case 2: 
      mat[0][0] = cosAngle;
      mat[2][0] = (-sinAngle);
      mat[1][1] = 1.0D;
      mat[0][2] = sinAngle;
      mat[2][2] = cosAngle;
      break;
    case 3: 
      mat[0][0] = cosAngle;
      mat[1][1] = cosAngle;
      mat[1][0] = sinAngle;
      mat[0][1] = (-sinAngle);
      mat[2][2] = 1.0D;
    }
    
  }
  
  public void rotate(Axis axis, double angle)
  {
    TransformMatrix rot = identity();
    rot.setRotation(axis, angle);
    concat(rot);
  }
  
  public void scale(double sx, double sy, double sz)
  {
    TransformMatrix scale = identity();
    scale.setScale(sx, sy, sz);
    concat(scale);
  }
  
  public void setScale(double sx, double sy, double sz)
  {
    setIdentity();
    mat[0][0] = sx;
    mat[1][1] = sy;
    mat[2][2] = sz;
  }
  



  public void setScaleAbout(double sx, double sy, double sz, double x, double y, double z)
  {
    setIdentity();
    mat[0][0] = sx;
    mat[1][1] = sy;
    mat[2][2] = sz;
    
    mat[0][3] = (x - sx * x);
    mat[1][3] = (y - sy * y);
    mat[2][3] = (z - sz * z);
  }
  
  public TransformMatrix getInverse()
  {
    TransformMatrix inverse = new TransformMatrix();
    
    double s0 = mat[0][0] * mat[1][1] - mat[1][0] * mat[0][1];
    double s1 = mat[0][0] * mat[1][2] - mat[1][0] * mat[0][2];
    double s2 = mat[0][0] * mat[1][3] - mat[1][0] * mat[0][3];
    double s3 = mat[0][1] * mat[1][2] - mat[1][1] * mat[0][2];
    double s4 = mat[0][1] * mat[1][3] - mat[1][1] * mat[0][3];
    double s5 = mat[0][2] * mat[1][3] - mat[1][2] * mat[0][3];
    
    double c5 = mat[2][2] * mat[3][3] - mat[3][2] * mat[2][3];
    double c4 = mat[2][1] * mat[3][3] - mat[3][1] * mat[2][3];
    double c3 = mat[2][1] * mat[3][2] - mat[3][1] * mat[2][2];
    double c2 = mat[2][0] * mat[3][3] - mat[3][0] * mat[2][3];
    double c1 = mat[2][0] * mat[3][2] - mat[3][0] * mat[2][2];
    double c0 = mat[2][0] * mat[3][1] - mat[3][0] * mat[2][1];
    
    double invdet = 1.0D / (s0 * c5 - s1 * c4 + s2 * c3 + s3 * c2 - s4 * c1 + s5 * c0);
    
    if (Math.abs(invdet) < 1.0E-7D)
    {
      Logger.logWarning("Could not find inverse for singular matrix.");
      return new TransformMatrix(this);
    }
    
    mat[0][0] = ((mat[1][1] * c5 - mat[1][2] * c4 + mat[1][3] * c3) * invdet);
    mat[0][1] = ((-mat[0][1] * c5 + mat[0][2] * c4 - mat[0][3] * c3) * invdet);
    mat[0][2] = ((mat[3][1] * s5 - mat[3][2] * s4 + mat[3][3] * s3) * invdet);
    mat[0][3] = ((-mat[2][1] * s5 + mat[2][2] * s4 - mat[2][3] * s3) * invdet);
    
    mat[1][0] = ((-mat[1][0] * c5 + mat[1][2] * c2 - mat[1][3] * c1) * invdet);
    mat[1][1] = ((mat[0][0] * c5 - mat[0][2] * c2 + mat[0][3] * c1) * invdet);
    mat[1][2] = ((-mat[3][0] * s5 + mat[3][2] * s2 - mat[3][3] * s1) * invdet);
    mat[1][3] = ((mat[2][0] * s5 - mat[2][2] * s2 + mat[2][3] * s1) * invdet);
    
    mat[2][0] = ((mat[1][0] * c4 - mat[1][1] * c2 + mat[1][3] * c0) * invdet);
    mat[2][1] = ((-mat[0][0] * c4 + mat[0][1] * c2 - mat[0][3] * c0) * invdet);
    mat[2][2] = ((mat[3][0] * s4 - mat[3][1] * s2 + mat[3][3] * s0) * invdet);
    mat[2][3] = ((-mat[2][0] * s4 + mat[2][1] * s2 - mat[2][3] * s0) * invdet);
    
    mat[3][0] = ((-mat[1][0] * c3 + mat[1][1] * c1 - mat[1][2] * c0) * invdet);
    mat[3][1] = ((mat[0][0] * c3 - mat[0][1] * c1 + mat[0][2] * c0) * invdet);
    mat[3][2] = ((-mat[3][0] * s3 + mat[3][1] * s1 - mat[3][2] * s0) * invdet);
    mat[3][3] = ((mat[2][0] * s3 - mat[2][1] * s1 + mat[2][2] * s0) * invdet);
    
    return inverse;
  }
  

  public void concat(TransformMatrix other)
  {
    double[][] temp = new double[4][4];
    
    for (int row = 0; row < 4; row++) {
      for (int col = 0; col < 4; col++)
      {
        double sum = 0.0D;
        for (int n = 0; n < 4; n++)
          sum += mat[n][col] * mat[row][n];
        temp[row][col] = sum;
      }
    }
    mat = temp;
  }
  
  public static TransformMatrix multiply(TransformMatrix left, TransformMatrix right)
  {
    TransformMatrix product = new TransformMatrix();
    
    for (int row = 0; row < 4; row++) {
      for (int col = 0; col < 4; col++)
      {
        double sum = 0.0D;
        for (int n = 0; n < 4; n++)
          sum += mat[n][col] * mat[row][n];
        mat[row][col] = sum;
      }
    }
    return product;
  }
  
  public static TransformMatrix identity()
  {
    TransformMatrix t = new TransformMatrix();
    mat[0][0] = 1.0D;
    mat[1][1] = 1.0D;
    mat[2][2] = 1.0D;
    mat[3][3] = 1.0D;
    return t;
  }
  
  public static TransformMatrix perspective(float vfov, float aspectRatio, float nearClip, float farClip)
  {
    float f = (float)(1.0D / Math.tan(Math.toRadians(vfov / 2.0F)));
    
    TransformMatrix t = new TransformMatrix();
    mat[0][0] = (f / aspectRatio);
    mat[1][1] = f;
    mat[2][2] = ((nearClip + farClip) / (nearClip - farClip));
    mat[2][3] = (2.0F * farClip * nearClip / (nearClip - farClip));
    mat[3][2] = -1.0D;
    return t;
  }
  
  public static TransformMatrix lookAt(Vector3f eye, Vector3f obj, Vector3f up)
  {
    Vector3f forward = new Vector3f();
    Vector3f right = new Vector3f();
    Vector3f newUp = new Vector3f();
    
    Vector3f.sub(eye, obj, forward);
    
    if (forward.length() == 0.0F)
      forward = new Vector3f(1.0F, 0.0F, 0.0F);
    forward.normalise();
    
    Vector3f.cross(up, forward, right);
    right.normalise();
    
    Vector3f.cross(forward, right, newUp);
    newUp.normalise();
    
    TransformMatrix t = new TransformMatrix();
    mat[0][0] = x;
    mat[0][1] = y;
    mat[0][2] = z;
    mat[0][3] = (-Vector3f.dot(right, eye));
    
    mat[1][0] = x;
    mat[1][1] = y;
    mat[1][2] = z;
    mat[1][3] = (-Vector3f.dot(newUp, eye));
    
    mat[2][0] = x;
    mat[2][1] = y;
    mat[2][2] = z;
    mat[2][3] = (-Vector3f.dot(forward, eye));
    
    mat[3][3] = 1.0D;
    
    return t;
  }
  
  public void put(FloatBuffer buffer)
  {
    for (int col = 0; col < 4; col++)
      for (int row = 0; row < 4; row++)
        buffer.put((float)mat[row][col]);
    buffer.rewind();
  }
  




  public void forceTransform(MutablePoint p)
  {
    int[] product = new int[3];
    
    double x = p.getX();
    double y = p.getY();
    double z = p.getZ();
    
    product[0] = ((int)(x * mat[0][0] + y * mat[0][1] + z * mat[0][2] + mat[0][3]));
    product[1] = ((int)(x * mat[1][0] + y * mat[1][1] + z * mat[1][2] + mat[1][3]));
    product[2] = ((int)(x * mat[2][0] + y * mat[2][1] + z * mat[2][2] + mat[2][3]));
    
    p.setPosition(product[0], product[1], product[2]);
  }
  





  public void forceTransform(MutablePoint src, MutablePoint dest)
  {
    int[] product = new int[3];
    
    double x = src.getX();
    double y = src.getY();
    double z = src.getZ();
    
    product[0] = ((int)(x * mat[0][0] + y * mat[0][1] + z * mat[0][2] + mat[0][3]));
    product[1] = ((int)(x * mat[1][0] + y * mat[1][1] + z * mat[1][2] + mat[1][3]));
    product[2] = ((int)(x * mat[2][0] + y * mat[2][1] + z * mat[2][2] + mat[2][3]));
    
    dest.setPosition(product[0], product[1], product[2]);
  }
  






  public void applyTransform(MutablePoint p)
  {
    int[] product = new int[3];
    
    double x = p.getX();
    double y = p.getY();
    double z = p.getZ();
    
    product[0] = ((int)(x * mat[0][0] + y * mat[0][1] + z * mat[0][2] + mat[0][3]));
    product[1] = ((int)(x * mat[1][0] + y * mat[1][1] + z * mat[1][2] + mat[1][3]));
    product[2] = ((int)(x * mat[2][0] + y * mat[2][1] + z * mat[2][2] + mat[2][3]));
    
    p.setTempPosition(product[0], product[1], product[2]);
  }
  







  public void applyTransform(MutableAngle a)
  {
    double angle = 0.0D;
    

    switch (1.$SwitchMap$editor$map$Axis[axis.ordinal()])
    {
    case 1: 
      double y = Math.sin(Math.toRadians(a.getAngle()));
      double z = -Math.cos(Math.toRadians(a.getAngle()));
      double y2 = (int)(y * mat[1][1] + z * mat[2][1]);
      double z2 = (int)(y * mat[1][2] + z * mat[2][2]);
      angle = Math.toDegrees(Math.atan2(y2, -z2));
      break;
    case 2: 
      double x = Math.cos(Math.toRadians(a.getAngle()));
      double z = -Math.sin(Math.toRadians(a.getAngle()));
      double x2 = x * mat[0][0] + z * mat[2][0];
      double z2 = x * mat[0][2] + z * mat[2][2];
      angle = Math.toDegrees(Math.atan2(-z2, x2));
      break;
    case 3: 
      double x = Math.cos(Math.toRadians(a.getAngle()));
      double y = Math.sin(Math.toRadians(a.getAngle()));
      double x2 = x * mat[0][0] + y * mat[1][0];
      double y2 = x * mat[0][1] + y * mat[1][1];
      angle = Math.toDegrees(Math.atan2(y, x));
      break;
    default: 
      throw new UnsupportedOperationException("MutableAngle axis must be a basis vector.");
    }
    
    a.setTempAngle(angle);
  }
  







  public void applyTransform(MutablePoint src, MutablePoint dest)
  {
    int[] product = new int[3];
    
    double x = src.getX();
    double y = src.getY();
    double z = src.getZ();
    
    product[0] = ((int)(x * mat[0][0] + y * mat[0][1] + z * mat[0][2] + mat[0][3]));
    product[1] = ((int)(x * mat[1][0] + y * mat[1][1] + z * mat[1][2] + mat[1][3]));
    product[2] = ((int)(x * mat[2][0] + y * mat[2][1] + z * mat[2][2] + mat[2][3]));
    
    dest.setTempPosition(product[0], product[1], product[2]);
  }
}
