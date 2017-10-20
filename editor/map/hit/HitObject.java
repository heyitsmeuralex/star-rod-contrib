package editor.map.hit;

import editor.map.MapObject;
import editor.map.MapObject.HitType;
import editor.map.mesh.AbstractMesh;
import editor.map.mesh.BasicMesh;
import editor.map.mesh.Triangle;
import editor.selection.PickRay;
import editor.selection.PickRay.PickHit;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;




public abstract class HitObject
  extends MapObject
  implements Externalizable
{
  private static final long serialVersionUID = 1L;
  private MapObject.HitType type;
  public BasicMesh mesh;
  public transient int c_TriangleOffset;
  public transient int c_ChildIndex = -1;
  public transient int c_NextIndex = -1;
  


  public HitObject() {}
  


  public HitObject(MapObject.HitType type)
  {
    this.type = type;
    mesh = new BasicMesh();
  }
  
  public MapObject.HitType getType()
  {
    return type;
  }
  
  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    super.readExternal(in);
    
    type = ((MapObject.HitType)in.readObject());
    mesh = ((BasicMesh)in.readObject());
    mesh.parentObject = this;
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    super.writeExternal(out);
    out.writeObject(type);
    out.writeObject(mesh);
  }
  

  public boolean transforms()
  {
    return type == MapObject.HitType.HIT;
  }
  

  public boolean shouldTryPick(PickRay ray)
  {
    return (hasMesh()) && (PickRay.intersects(ray, AABB));
  }
  

  public PickRay.PickHit tryPick(PickRay ray)
  {
    PickRay.PickHit nearestHit = new PickRay.PickHit(ray, Float.MAX_VALUE);
    for (Triangle t : mesh)
    {
      PickRay.PickHit hit = PickRay.getIntersection(ray, t);
      if (dist < dist)
        nearestHit = hit;
    }
    obj = this;
    return nearestHit;
  }
  

  public boolean shouldTryTrianglePick(PickRay ray)
  {
    return hasMesh();
  }
  

  public boolean hasMesh()
  {
    return type == MapObject.HitType.HIT;
  }
  

  public boolean shouldIterate()
  {
    return (hasMesh()) && (type != MapObject.HitType.ROOT);
  }
  

  public AbstractMesh getMesh()
  {
    return mesh;
  }
  

  public void updateMeshHierarchy()
  {
    mesh.parentObject = this;
    mesh.updateHierarchy();
  }
  

  public boolean allowsPopup()
  {
    return type != MapObject.HitType.HIT;
  }
  

  public boolean allowsChildren()
  {
    return type != MapObject.HitType.HIT;
  }
  

  public String toString()
  {
    return name;
  }
}
