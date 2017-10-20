package editor.map.shape;

import editor.map.Axis;
import editor.map.BoundingBox;
import editor.map.MutablePoint;
import editor.map.PointListBackup;
import editor.map.ReversibleTransform;
import editor.selection.Selectable;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import util.IdentityHashSet;






public class UV
  implements Selectable, Externalizable
{
  private static final long serialVersionUID = 1L;
  private MutablePoint texCoordinate;
  public transient boolean selected;
  
  public UV() {}
  
  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    texCoordinate = ((MutablePoint)in.readObject());
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeObject(texCoordinate);
  }
  
  public UV(float u, float v)
  {
    texCoordinate = new MutablePoint(u, v, 0.0F);
    selected = false;
  }
  
  public UV(int u, int v)
  {
    texCoordinate = new MutablePoint(u, v, 0);
    selected = false;
  }
  
  public short getU()
  {
    return (short)texCoordinate.getX();
  }
  
  public short getV()
  {
    return (short)texCoordinate.getY();
  }
  
  public void setPosition(int u, int v)
  {
    if (texCoordinate.isTransforming()) {
      texCoordinate.setTempPosition(u, v, 0);
    }
  }
  



  public void round(int dg)
  {
    texCoordinate.roundTemp(dg);
  }
  

  public void addTo(BoundingBox aabb)
  {
    aabb.encompass((short)texCoordinate.getX(), (short)texCoordinate.getY(), 0);
  }
  

  public boolean transforms()
  {
    return true;
  }
  

  public void startTransformation()
  {
    texCoordinate.startTransform();
  }
  

  public void endTransformation()
  {
    texCoordinate.endTransform();
  }
  

  public void recalculateAABB() {}
  

  public boolean allowRotation(Axis axis)
  {
    return true;
  }
  

  public void addPoints(IdentityHashSet<MutablePoint> positions)
  {
    positions.add(texCoordinate);
  }
  

  public void setSelected(boolean val)
  {
    selected = val;
  }
  

  public boolean isSelected()
  {
    return selected;
  }
  
  public UV deepCopy()
  {
    return new UV(getU(), getV());
  }
  

  public ReversibleTransform createTransformer(TransformMatrix m)
  {
    return new PointListBackup(texCoordinate.getBackup());
  }
  
  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    
    result = 31 * result + (texCoordinate == null ? 0 : texCoordinate.hashCode());
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
    UV other = (UV)obj;
    if (texCoordinate == null) {
      if (texCoordinate != null)
        return false;
    } else if (!texCoordinate.equals(texCoordinate))
      return false;
    return true;
  }
}
