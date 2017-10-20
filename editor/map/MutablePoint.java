package editor.map;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.lwjgl.util.vector.Vector3f;

public class MutablePoint implements Externalizable
{
  private static final long serialVersionUID = 1L;
  private int x;
  private int y;
  private int z;
  private transient int tempx;
  private transient int tempy;
  private transient int tempz;
  private transient boolean transforming = false;
  
  public transient long lastModified = -1L;
  


  public MutablePoint() {}
  


  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    x = in.readInt();
    y = in.readInt();
    z = in.readInt();
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeInt(x);
    out.writeInt(y);
    out.writeInt(z);
  }
  
  public MutablePoint(float x, float y, float z)
  {
    setPosition((int)x, (int)y, (int)z);
  }
  
  public MutablePoint(int x, int y, int z)
  {
    setPosition(x, y, z);
  }
  
  public MutablePoint(Vector3f v)
  {
    setPosition(x, y, z);
  }
  
  public void setPosition(float x, float y, float z)
  {
    setPosition((int)x, (int)y, (int)z);
  }
  
  public void setPosition(int x, int y, int z)
  {
    this.x = x;
    this.y = y;
    this.z = z;
  }
  
  public void setPosition(Vector3f v)
  {
    setPosition(x, y, z);
  }
  
  public void setPosition(MutablePoint p)
  {
    setPosition(p.getX(), p.getY(), p.getZ());
  }
  
  public void startTransform()
  {
    transforming = true;
    tempx = x;
    tempy = y;
    tempz = z;
  }
  
  public void endTransform()
  {
    transforming = false;
  }
  



  public boolean isTransforming()
  {
    return transforming;
  }
  
  public void roundTemp(int dg)
  {
    tempx = (dg * Math.round(x / dg));
    tempy = (dg * Math.round(y / dg));
    tempz = (dg * Math.round(z / dg));
  }
  
  public void setTempTranslation(Vector3f translation)
  {
    setTempTranslation((int)x, (int)y, (int)z);
  }
  
  public void setTempTranslation(int dx, int dy, int dz)
  {
    tempx = (x + dx);
    tempy = (y + dy);
    tempz = (z + dz);
  }
  
  public void setTempScale(Vector3f origin, Vector3f scale)
  {
    tempx = ((int)(x + x * (x - x)));
    tempy = ((int)(y + y * (y - y)));
    tempz = ((int)(z + z * (z - z)));
  }
  
  public void setTempPosition(float x, float y, float z)
  {
    setTempPosition((int)x, (int)y, (int)z);
  }
  
  public void setTempPosition(int x, int y, int z)
  {
    tempx = x;
    tempy = y;
    tempz = z;
  }
  
  public int get(int axis)
  {
    switch (axis) {
    case 0: 
      return getX();
    case 1:  return getY();
    case 2:  return getZ();
    }
    throw new IllegalArgumentException("Invalid axis value " + axis);
  }
  

  public int getX() { return transforming ? tempx : x; }
  public int getY() { return transforming ? tempy : y; }
  public int getZ() { return transforming ? tempz : z; }
  
  public int getBaseX() { return x; }
  public int getBaseY() { return y; }
  public int getBaseZ() { return z; }
  
  public int getTempX() { return tempx; }
  public int getTempY() { return tempy; }
  public int getTempZ() { return tempz; }
  
  public void set(int axis, int val)
  {
    switch (axis) {
    case 0: 
      setX(val); break;
    case 1:  setY(val); break;
    case 2:  setZ(val); break;
    default: 
      throw new IllegalArgumentException("Invalid axis value " + axis);
    }
  }
  
  public void setX(int val) { x = val; }
  public void setY(int val) { y = val; }
  public void setZ(int val) { z = val; }
  
  public void setTempX(int val) { tempx = val; }
  public void setTempY(int val) { tempy = val; }
  public void setTempZ(int val) { tempz = val; }
  

  public Vector3f getVector()
  {
    if (transforming) {
      return new Vector3f(tempx, tempy, tempz);
    }
    return new Vector3f(x, y, z);
  }
  
  public MutablePoint deepCopy()
  {
    return new MutablePoint(x, y, z);
  }
  


  public PointBackup getBackup() { return new PointBackup(this, null); }
  
  public static class PointBackup {
    public final MutablePoint pos;
    public final int oldx;
    public final int oldy;
    public final int oldz;
    public final int newx;
    public final int newy;
    public final int newz;
    
    private PointBackup(MutablePoint p) { pos = p;
      oldx = x;
      oldy = y;
      oldz = z;
      newx = tempx;
      newy = tempy;
      newz = tempz;
    }
  }
  
  public String toString()
  {
    return "(" + x + ", " + y + ", " + z + ")";
  }
  

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + x;
    result = 31 * result + y;
    result = 31 * result + z;
    return result;
  }
  

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MutablePoint other = (MutablePoint)obj;
    if (x != x)
      return false;
    if (y != y)
      return false;
    if (z != z)
      return false;
    return true;
  }
}
