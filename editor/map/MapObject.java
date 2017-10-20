package editor.map;

import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.TransformMatrix;
import editor.map.tree.MapObjectNode;
import editor.selection.PickRay;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
import util.IdentityHashSet;

public abstract class MapObject implements editor.selection.Selectable, Externalizable
{
  private static final long serialVersionUID = 1L;
  private static final int latestVersion = 1;
  
  public MapObject() {}
  
  public abstract boolean shouldTryTrianglePick(PickRay paramPickRay);
  
  private int instanceVersion = 1;
  


  public abstract boolean shouldTryPick(PickRay paramPickRay);
  

  public transient BoundingBox AABB = new BoundingBox();
  

  public abstract editor.selection.PickRay.PickHit tryPick(PickRay paramPickRay);
  

  public abstract boolean hasMesh();
  
  public transient boolean dirtyAABB = true;
  




  public boolean dumped = false;
  


  public int fileOffset;
  

  public String name;
  

  public transient boolean hidden = false;
  public transient boolean selected = false;
  

  public transient int distance = 0;
  


  public abstract editor.map.mesh.AbstractMesh getMesh();
  

  public abstract void updateMeshHierarchy();
  

  public abstract MapObject deepCopy();
  

  public abstract MapObjectType getObjectType();
  

  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    instanceVersion = in.readInt();
    
    name = in.readUTF();
    hidden = in.readBoolean();
    dumped = in.readBoolean();
    fileOffset = in.readInt();
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeInt(1);
    
    out.writeUTF(name);
    out.writeBoolean(hidden);
    out.writeBoolean(dumped);
    out.writeInt(fileOffset);
  }
  
  public boolean shouldDraw()
  {
    return (!hidden) && (hasMesh());
  }
  
  public boolean hasCamera()
  {
    return false;
  }
  

  public void addTo(BoundingBox aabb)
  {
    aabb.encompass(AABB);
  }
  

  public boolean transforms()
  {
    return true;
  }
  

  public void startTransformation()
  {
    if (!hasMesh()) {
      return;
    }
    for (Triangle t : getMesh()) {
      for (Vertex v : vert) {
        v.getPosition().startTransform();
      }
    }
  }
  


  public void endTransformation()
  {
    if (!hasMesh()) {
      return;
    }
    for (Triangle t : getMesh()) {
      for (Vertex v : vert) {
        v.getPosition().endTransform();
      }
    }
  }
  


  public void recalculateAABB()
  {
    AABB.clear();
    if (hasMesh()) {
      AABB.encompass(getMesh());
    }
  }
  
  public boolean allowRotation(Axis axis)
  {
    return true;
  }
  

  public void addPoints(IdentityHashSet<MutablePoint> positionSet)
  {
    positionSet.add(AABB.min);
    positionSet.add(AABB.max);
    
    if (!hasMesh()) {
      return;
    }
    for (Triangle t : getMesh())
    {
      t.addPoints(positionSet);
      for (Vertex v : vert) {
        positionSet.add(v.getPosition());
      }
    }
  }
  
  public ReversibleTransform createTransformer(TransformMatrix m)
  {
    if (!hasMesh()) {
      return null;
    }
    PointListBackup backup = new PointListBackup();
    
    for (Triangle t : getMesh()) {
      for (Vertex v : vert)
        backup.addPoint(v.getPosition().getBackup());
    }
    backup.addPoint(AABB.min.getBackup());
    backup.addPoint(AABB.max.getBackup());
    
    return backup;
  }
  

  public void setSelected(boolean val)
  {
    selected = val;
  }
  

  public boolean isSelected()
  {
    return selected;
  }
  

  public abstract boolean allowsPopup();
  

  public abstract boolean allowsChildren();
  
  public abstract MapObjectNode<? extends MapObject> getNode();
  
  public boolean shouldIterate()
  {
    return hasMesh();
  }
  
  public static class DeleteComparator implements java.util.Comparator<MapObject>
  {
    public DeleteComparator() {}
    
    public int compare(MapObject o1, MapObject o2) {
      MapObjectNode node1 = o1.getNode();
      MapObjectNode node2 = o2.getNode();
      
      return node2.compareTo(node1);
    }
  }
  




  public static Set<MapObject> getSetWithDescendents(Iterable<MapObject> objs)
  {
    HashSet<MapObject> set = new HashSet();
    Stack<MapObjectNode<?>> nodes = new Stack();
    
    for (MapObject obj : objs)
    {
      nodes.push(obj.getNode());
      
      while (!nodes.isEmpty())
      {
        MapObjectNode<?> node = (MapObjectNode)nodes.pop();
        if (node.isRoot())
        {
          for (int i = 0; i < node.getChildCount(); i++) {
            nodes.push(node.getChildAt(i));
          }
        } else if (!set.contains(node.getUserObject()))
        {
          set.add(node.getUserObject());
          
          for (int i = 0; i < node.getChildCount(); i++) {
            nodes.push(node.getChildAt(i));
          }
        }
      }
    }
    return set;
  }
  
  public static enum MapObjectType
  {
    MODEL("Model"), 
    COLLIDER("Collider"), 
    ZONE("Zone"), 
    MARKER("Marker");
    
    private final String name;
    
    private MapObjectType(String name)
    {
      this.name = name;
    }
    

    public String toString()
    {
      return name;
    }
  }
  
  public static enum ShapeType
  {
    ROOT("Root"), 
    MODEL("Model"), 
    GROUP("Group"), 
    SPECIAL("Special Group");
    
    private final String name;
    
    private ShapeType(String name)
    {
      this.name = name;
    }
    



    public String toString() { return name; }
  }
  
  public static enum HitType {
    ROOT,  HIT,  GROUP;
    
    private HitType() {}
  }
}
