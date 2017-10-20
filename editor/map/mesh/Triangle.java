package editor.map.mesh;

import editor.map.Axis;
import editor.map.BoundingBox;
import editor.map.MutablePoint;
import editor.map.PointListBackup;
import editor.map.ReversibleTransform;
import editor.map.shape.TransformMatrix;
import editor.map.shape.TriangleBatch;
import editor.selection.Selectable;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.lwjgl.util.vector.Vector3f;
import util.IdentityHashSet;


public class Triangle
  implements Externalizable, Selectable
{
  private static final long serialVersionUID = 1L;
  private static final int latestVersion = 1;
  private int instanceVersion = 1;
  
  public Vertex[] vert;
  public boolean doubleSided = false;
  
  public transient TriangleBatch parentBatch;
  public transient boolean selected = false;
  


  public Triangle() {}
  


  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    instanceVersion = in.readInt();
    
    vert = new Vertex[3];
    vert[0] = ((Vertex)in.readObject());
    vert[1] = ((Vertex)in.readObject());
    vert[2] = ((Vertex)in.readObject());
    
    if (instanceVersion >= 1) {
      doubleSided = in.readBoolean();
    }
  }
  
  public void writeExternal(ObjectOutput out) throws IOException
  {
    out.writeInt(1);
    
    out.writeObject(vert[0]);
    out.writeObject(vert[1]);
    out.writeObject(vert[2]);
    
    out.writeBoolean(doubleSided);
  }
  
  public Triangle(Vertex v1, Vertex v2, Vertex v3)
  {
    instanceVersion = 1;
    vert = new Vertex[3];
    vert[0] = v1;
    vert[1] = v2;
    vert[2] = v3;
  }
  
  public void setParent(TriangleBatch b)
  {
    parentBatch = b;
  }
  



  public Triangle deepCopy()
  {
    return new Triangle(vert[0].deepCopy(), vert[1].deepCopy(), vert[2].deepCopy());
  }
  
  public Triangle basicCopy()
  {
    Vertex v0 = new Vertex(vert[0].getCurrentX(), vert[0].getCurrentY(), vert[0].getCurrentZ());
    Vertex v1 = new Vertex(vert[1].getCurrentX(), vert[1].getCurrentY(), vert[1].getCurrentZ());
    Vertex v2 = new Vertex(vert[2].getCurrentX(), vert[2].getCurrentY(), vert[2].getCurrentZ());
    return new Triangle(v0, v1, v2);
  }
  
  public Vector3f getCenter()
  {
    float centerx = (vert[0].getCurrentX() + vert[1].getCurrentX() + vert[2].getCurrentX()) / 3;
    float centery = (vert[0].getCurrentY() + vert[1].getCurrentY() + vert[2].getCurrentY()) / 3;
    float centerz = (vert[0].getCurrentZ() + vert[1].getCurrentZ() + vert[2].getCurrentZ()) / 3;
    
    return new Vector3f(centerx, centery, centerz);
  }
  
  public float[] getCentroid()
  {
    float[] centroid = new float[3];
    
    centroid[0] = ((vert[0].getCurrentX() + vert[1].getCurrentX() + vert[2].getCurrentX()) / 3.0F);
    centroid[1] = ((vert[0].getCurrentY() + vert[1].getCurrentY() + vert[2].getCurrentY()) / 3.0F);
    centroid[2] = ((vert[0].getCurrentZ() + vert[1].getCurrentZ() + vert[2].getCurrentZ()) / 3.0F);
    
    return centroid;
  }
  
  public float[] getNormal()
  {
    float[] normal = new float[3];
    
    float Ax = vert[1].getCurrentX() - vert[0].getCurrentX();
    float Ay = vert[1].getCurrentY() - vert[0].getCurrentY();
    float Az = vert[1].getCurrentZ() - vert[0].getCurrentZ();
    
    float Bx = vert[2].getCurrentX() - vert[0].getCurrentX();
    float By = vert[2].getCurrentY() - vert[0].getCurrentY();
    float Bz = vert[2].getCurrentZ() - vert[0].getCurrentZ();
    
    normal[0] = (Ay * Bz - Az * By);
    normal[1] = (Az * Bx - Ax * Bz);
    normal[2] = (Ax * By - Ay * Bx);
    
    double mag = Math.sqrt(normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]);
    if (Math.abs(mag) < 1.0E-6D) {
      return null;
    }
    int tmp217_216 = 0; float[] tmp217_215 = normal;tmp217_215[tmp217_216] = ((float)(tmp217_215[tmp217_216] / mag)); int 
      tmp227_226 = 1; float[] tmp227_225 = normal;tmp227_225[tmp227_226] = ((float)(tmp227_225[tmp227_226] / mag)); int 
      tmp237_236 = 2; float[] tmp237_235 = normal;tmp237_235[tmp237_236] = ((float)(tmp237_235[tmp237_236] / mag));
    
    return normal;
  }
  
  public float getArea()
  {
    float Ax = vert[1].getCurrentX() - vert[0].getCurrentX();
    float Ay = vert[1].getCurrentY() - vert[0].getCurrentY();
    float Az = vert[1].getCurrentZ() - vert[0].getCurrentZ();
    
    float Bx = vert[2].getCurrentX() - vert[0].getCurrentX();
    float By = vert[2].getCurrentY() - vert[0].getCurrentY();
    float Bz = vert[2].getCurrentZ() - vert[0].getCurrentZ();
    
    float[] cross = new float[3];
    cross[0] = (Ay * Bz - Az * By);
    cross[1] = (Az * Bx - Ax * Bz);
    cross[2] = (Ax * By - Ay * Bx);
    
    double mag = Math.sqrt(cross[0] * cross[0] + cross[1] * cross[1] + cross[2] * cross[2]);
    return (float)(mag / 2.0D);
  }
  

  public void addTo(BoundingBox aabb)
  {
    aabb.encompass(vert[0]);
    aabb.encompass(vert[1]);
    aabb.encompass(vert[2]);
  }
  

  public boolean transforms()
  {
    return true;
  }
  

  public void startTransformation()
  {
    vert[0].getPosition().startTransform();
    vert[1].getPosition().startTransform();
    vert[2].getPosition().startTransform();
  }
  

  public void endTransformation()
  {
    vert[0].getPosition().endTransform();
    vert[1].getPosition().endTransform();
    vert[2].getPosition().endTransform();
  }
  

  public void recalculateAABB()
  {
    parentBatch.parentMesh.parentObject.dirtyAABB = true;
  }
  

  public boolean allowRotation(Axis axis)
  {
    return true;
  }
  

  public void addPoints(IdentityHashSet<MutablePoint> positions)
  {
    positions.add(vert[0].getPosition());
    positions.add(vert[1].getPosition());
    positions.add(vert[2].getPosition());
  }
  

  public void setSelected(boolean val)
  {
    selected = val;
  }
  

  public boolean isSelected()
  {
    return selected;
  }
  
  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (vert[0] == null ? 0 : vert[0].hashCode());
    result = 31 * result + (vert[1] == null ? 0 : vert[1].hashCode());
    result = 31 * result + (vert[2] == null ? 0 : vert[2].hashCode());
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
    Triangle other = (Triangle)obj;
    if (vert[0] == null) {
      if (vert[0] != null)
        return false;
    } else if (!vert[0].equals(vert[0]))
      return false;
    if (vert[1] == null) {
      if (vert[1] != null)
        return false;
    } else if (!vert[1].equals(vert[1]))
      return false;
    if (vert[2] == null) {
      if (vert[2] != null)
        return false;
    } else if (!vert[2].equals(vert[2]))
      return false;
    return true;
  }
  

  public ReversibleTransform createTransformer(TransformMatrix m)
  {
    PointListBackup backups = new PointListBackup();
    backups.addPoint(vert[0].getPosition().getBackup());
    backups.addPoint(vert[1].getPosition().getBackup());
    backups.addPoint(vert[2].getPosition().getBackup());
    return backups;
  }
}
