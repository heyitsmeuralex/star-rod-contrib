package editor.map;

import editor.map.mesh.AbstractMesh;
import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;




public class BoundingBox
  implements Externalizable
{
  private static final long serialVersionUID = 1L;
  public MutablePoint min;
  public MutablePoint max;
  private boolean empty = true;
  
  public transient long lastRecalculated = -1L;
  
  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    empty = in.readBoolean();
    min = ((MutablePoint)in.readObject());
    max = ((MutablePoint)in.readObject());
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeBoolean(empty);
    out.writeObject(min);
    out.writeObject(max);
  }
  
  public BoundingBox()
  {
    min = new MutablePoint(0, 0, 0);
    max = new MutablePoint(0, 0, 0);
  }
  
  public BoundingBox(Vector3f min, Vector3f max)
  {
    this.min = new MutablePoint(x, y, z);
    this.max = new MutablePoint(x, y, z);
    
    empty = false;
  }
  
  public BoundingBox deepCopy()
  {
    BoundingBox bb = new BoundingBox();
    empty = empty;
    if (!empty)
    {
      min = min.deepCopy();
      max = max.deepCopy();
    }
    return bb;
  }
  
  public String toString()
  {
    return "(" + min.getX() + "," + min.getY() + "," + min.getZ() + ") to (" + max.getX() + "," + max.getY() + "," + max.getZ() + ")";
  }
  
  public void clear()
  {
    empty = true;
  }
  
  public boolean isEmpty()
  {
    return empty;
  }
  
  public Vector3f getMin()
  {
    return new Vector3f(min.getX(), min.getY(), min.getZ());
  }
  
  public Vector3f getMax()
  {
    return new Vector3f(max.getX(), max.getY(), max.getZ());
  }
  
  public Vector3f getCenter()
  {
    return new Vector3f(max
      .getX() + min.getX() >> 1, max
      .getY() + min.getY() >> 1, max
      .getZ() + min.getZ() >> 1);
  }
  
  public boolean contains(int x, int y, int z)
  {
    if ((y < min.getY()) || (y > max.getY())) return false;
    if ((x < min.getX()) || (x > max.getX())) return false;
    if ((z < min.getZ()) || (z > max.getZ())) return false;
    return true;
  }
  
  public boolean contains(Vertex v)
  {
    return contains(v.getCurrentX(), v.getCurrentY(), v.getCurrentZ());
  }
  
  public boolean contains(Vector3f vec)
  {
    return contains((int)x, (int)y, (int)z);
  }
  
  public void encompass(int x, int y, int z)
  {
    if (empty)
    {
      min.setPosition(x, y, z);
      max.setPosition(x, y, z);
      empty = false;
    } else {
      if (x < min.getX()) { min.setX(x);
      } else if (x > max.getX()) max.setX(x);
      if (y < min.getY()) { min.setY(y);
      } else if (y > max.getY()) max.setY(y);
      if (z < min.getZ()) { min.setZ(z);
      } else if (z > max.getZ()) max.setZ(z);
    }
  }
  
  public void encompass(Vertex v)
  {
    int x = v.getCurrentX();
    int y = v.getCurrentY();
    int z = v.getCurrentZ();
    
    if (empty)
    {
      min.setPosition(v.getPosition());
      max.setPosition(v.getPosition());
      empty = false;
    } else {
      if (x < min.getX()) { min.setX(x);
      } else if (x > max.getX()) max.setX(x);
      if (y < min.getY()) { min.setY(y);
      } else if (y > max.getY()) max.setY(y);
      if (z < min.getZ()) { min.setZ(z);
      } else if (z > max.getZ()) max.setZ(z);
    }
  }
  
  public void encompass(BoundingBox bb)
  {
    if (empty) { return;
    }
    if (empty)
    {
      min.setPosition(min);
      max.setPosition(max);
      empty = false;
    } else {
      if (min.getX() < min.getX()) min.setX(min.getX());
      if (max.getX() > max.getX()) max.setX(max.getX());
      if (min.getY() < min.getY()) min.setY(min.getY());
      if (max.getY() > max.getY()) max.setY(max.getY());
      if (min.getZ() < min.getZ()) min.setZ(min.getZ());
      if (max.getZ() > max.getZ()) max.setZ(max.getZ());
    }
  }
  
  public void encompass(AbstractMesh mesh)
  {
    for (Triangle t : mesh) {
      for (Vertex v : vert)
        encompass(v);
    }
  }
  
  public void render() {
    render(1.0F, 1.0F, 1.0F, 1.0F);
  }
  
  public void render(float width)
  {
    render(1.0F, 1.0F, 1.0F, width);
  }
  
  public void render(float r, float g, float b)
  {
    render(r, g, b, 1.0F);
  }
  
  public void render(float r, float g, float b, float width)
  {
    if (empty) { return;
    }
    int minx = min.getX();
    int miny = min.getY();
    int minz = min.getZ();
    
    int maxx = max.getX();
    int maxy = max.getY();
    int maxz = max.getZ();
    
    GL11.glPushAttrib(4065);
    
    GL11.glDisable(3553);
    GL11.glLineWidth(width);
    GL11.glColor3f(r, g, b);
    
    GL11.glBegin(1);
    
    GL11.glVertex3f(minx, miny, minz);
    GL11.glVertex3f(maxx, miny, minz);
    
    GL11.glVertex3f(minx, maxy, minz);
    GL11.glVertex3f(maxx, maxy, minz);
    
    GL11.glVertex3f(minx, miny, maxz);
    GL11.glVertex3f(maxx, miny, maxz);
    
    GL11.glVertex3f(minx, maxy, maxz);
    GL11.glVertex3f(maxx, maxy, maxz);
    
    GL11.glVertex3f(minx, miny, minz);
    GL11.glVertex3f(minx, maxy, minz);
    
    GL11.glVertex3f(maxx, miny, minz);
    GL11.glVertex3f(maxx, maxy, minz);
    
    GL11.glVertex3f(minx, miny, maxz);
    GL11.glVertex3f(minx, maxy, maxz);
    
    GL11.glVertex3f(maxx, miny, maxz);
    GL11.glVertex3f(maxx, maxy, maxz);
    
    GL11.glVertex3f(minx, miny, minz);
    GL11.glVertex3f(minx, miny, maxz);
    
    GL11.glVertex3f(maxx, miny, minz);
    GL11.glVertex3f(maxx, miny, maxz);
    
    GL11.glVertex3f(minx, maxy, minz);
    GL11.glVertex3f(minx, maxy, maxz);
    
    GL11.glVertex3f(maxx, maxy, minz);
    GL11.glVertex3f(maxx, maxy, maxz);
    
    GL11.glEnd();
    GL11.glPopAttrib();
  }
}
