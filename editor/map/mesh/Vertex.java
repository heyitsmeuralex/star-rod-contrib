package editor.map.mesh;

import editor.map.Axis;
import editor.map.BoundingBox;
import editor.map.MutablePoint;
import editor.map.MutablePoint.PointBackup;
import editor.map.ReversibleTransform;
import editor.map.shape.Model;
import editor.map.shape.TransformMatrix;
import editor.map.shape.UV;
import editor.selection.Selectable;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import org.lwjgl.util.vector.Vector3f;
import util.IdentityHashSet;


public class Vertex
  implements Externalizable, Selectable
{
  private static final long serialVersionUID = 1L;
  private static final int latestVersion = 0;
  private int instanceVersion = 0;
  
  public transient boolean selected = false;
  public transient boolean uvselected = false;
  public transient boolean painted = false;
  

  public transient float[] normal;
  

  private MutablePoint localPos;
  

  private MutablePoint worldPos;
  

  public boolean useLocal;
  

  public AbstractMesh parentMesh;
  

  public UV uv;
  
  public byte r = -1;
  public byte g = -1;
  public byte b = -1;
  public byte a = -1;
  
  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    instanceVersion = in.readInt();
    useLocal = in.readBoolean();
    localPos = ((MutablePoint)in.readObject());
    worldPos = ((MutablePoint)in.readObject());
    uv = ((UV)in.readObject());
    r = in.readByte();
    g = in.readByte();
    b = in.readByte();
    a = in.readByte();
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeInt(0);
    out.writeBoolean(useLocal);
    out.writeObject(localPos);
    out.writeObject(worldPos);
    out.writeObject(uv);
    out.writeByte(r);
    out.writeByte(g);
    out.writeByte(b);
    out.writeByte(a);
  }
  
  public Vertex()
  {
    uv = new UV(0, 0);
  }
  
  public Vertex(int x, int y, int z)
  {
    uv = new UV(0, 0);
    localPos = new MutablePoint(x, y, z);
    worldPos = new MutablePoint(x, y, z);
  }
  
  public Vertex deepCopy()
  {
    Vertex v = new Vertex();
    localPos = localPos.deepCopy();
    worldPos = worldPos.deepCopy();
    useLocal = useLocal;
    uv = uv.deepCopy();
    r = r;
    g = g;
    b = b;
    a = a;
    return v;
  }
  





  public MutablePoint getPosition()
  {
    return useLocal ? localPos : worldPos;
  }
  
  public MutablePoint getLocalPosition()
  {
    return localPos;
  }
  





  public Vector3f getCurrentPos()
  {
    if (useLocal) {
      return new Vector3f(localPos.getX(), localPos.getY(), localPos.getZ());
    }
    return new Vector3f(worldPos.getX(), worldPos.getY(), worldPos.getZ());
  }
  





  public int getCurrentX()
  {
    return useLocal ? localPos.getX() : worldPos.getX();
  }
  





  public int getCurrentY()
  {
    return useLocal ? localPos.getY() : worldPos.getY();
  }
  





  public int getCurrentZ()
  {
    return useLocal ? localPos.getZ() : worldPos.getZ();
  }
  




  public void forceTransform(TransformMatrix transformMatrix)
  {
    transformMatrix.forceTransform(localPos, worldPos);
  }
  




  public void round(int dg)
  {
    if ((parentMesh.parentObject instanceof Model))
    {
      if (useLocal) {
        localPos.roundTemp(dg);
      }
    }
    else {
      worldPos.roundTemp(dg);
    }
  }
  




  public byte[] getCompiledRepresentation()
  {
    byte[] bb = new byte[16];
    bb[0] = ((byte)(localPos.getX() >> 8));
    bb[1] = ((byte)localPos.getX());
    bb[2] = ((byte)(localPos.getY() >> 8));
    bb[3] = ((byte)localPos.getY());
    bb[4] = ((byte)(localPos.getZ() >> 8));
    bb[5] = ((byte)localPos.getZ());
    
    bb[8] = ((byte)(uv.getU() >> 8));
    bb[9] = ((byte)uv.getU());
    bb[10] = ((byte)(uv.getV() >> 8));
    bb[11] = ((byte)uv.getV());
    bb[12] = r;
    bb[13] = g;
    bb[14] = b;
    bb[15] = a;
    
    return bb;
  }
  





  public void addTo(BoundingBox aabb)
  {
    aabb.encompass(this);
  }
  

  public boolean transforms()
  {
    return true;
  }
  

  public void startTransformation()
  {
    getPosition().startTransform();
  }
  

  public void endTransformation()
  {
    getPosition().endTransform();
  }
  

  public void recalculateAABB()
  {
    parentMesh.parentObject.dirtyAABB = true;
  }
  

  public boolean allowRotation(Axis axis)
  {
    return true;
  }
  

  public void addPoints(IdentityHashSet<MutablePoint> positions)
  {
    positions.add(getPosition());
  }
  

  public void setSelected(boolean val)
  {
    selected = val;
  }
  

  public boolean isSelected()
  {
    return selected;
  }
  

  public ReversibleTransform createTransformer(TransformMatrix m)
  {
    return new VertexTransformer(this);
  }
  

  private static class VertexTransformer
    extends ReversibleTransform
  {
    private final Vertex v;
    
    private MutablePoint.PointBackup localBackup;
    
    private MutablePoint.PointBackup worldBackup;
    
    public VertexTransformer(Vertex v)
    {
      this.v = v;
      if (!useLocal)
      {

        if ((parentMesh.parentObject instanceof Model))
        {
          TransformMatrix globalTransform = parentMesh.parentObject).cumulativeTransformMatrix;
          globalTransform.getInverse().applyTransform(worldPos, localPos);
        }
        worldBackup = worldPos.getBackup();
      }
      
      localBackup = localPos.getBackup();
    }
    

    public void transform()
    {
      if (!v.useLocal) {
        v.worldPos.setPosition(worldBackup.newx, worldBackup.newy, worldBackup.newz);
      }
      v.localPos.setPosition(localBackup.newx, localBackup.newy, localBackup.newz);
    }
    

    public void revert()
    {
      if (!v.useLocal) {
        v.worldPos.setPosition(worldBackup.oldx, worldBackup.oldy, worldBackup.oldz);
      }
      v.localPos.setPosition(localBackup.oldx, localBackup.oldy, localBackup.oldz);
    }
  }
  

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + a;
    result = 31 * result + b;
    result = 31 * result + g;
    
    result = 31 * result + (localPos == null ? 0 : localPos.hashCode());
    result = 31 * result + r;
    result = 31 * result + (useLocal ? 1231 : 1237);
    result = 31 * result + (uv == null ? 0 : uv.hashCode());
    
    result = 31 * result + (worldPos == null ? 0 : worldPos.hashCode());
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
    Vertex other = (Vertex)obj;
    if (a != a)
      return false;
    if (b != b)
      return false;
    if (g != g)
      return false;
    if (localPos == null) {
      if (localPos != null)
        return false;
    } else if (!localPos.equals(localPos))
      return false;
    if (parentMesh == null) {
      if (parentMesh != null)
        return false;
    } else if (!parentMesh.equals(parentMesh))
      return false;
    if (r != r)
      return false;
    if (useLocal != useLocal)
      return false;
    if (uv == null) {
      if (uv != null)
        return false;
    } else if (!uv.equals(uv))
      return false;
    if (worldPos == null) {
      if (worldPos != null)
        return false;
    } else if (!worldPos.equals(worldPos))
      return false;
    return true;
  }
}
