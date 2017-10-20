package editor.map.hit;

import editor.commands.AbstractCommand;
import editor.map.MapObject.HitType;
import editor.map.MapObject.MapObjectType;
import editor.map.MutablePoint;
import editor.map.tree.MapObjectNode;
import editor.selection.SelectablePoint;
import editor.ui.info.ZoneInfoPanel;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import util.IdentityHashSet;

public class Zone extends HitObject implements java.io.Externalizable
{
  private static final long serialVersionUID = 1L;
  private static final int latestVersion = 0;
  private int instanceVersion = 0;
  
  private MapObjectNode<Zone> node;
  
  public boolean hasCameraData = false;
  public transient CameraController cam = null;
  

  public transient int c_CameraOffset = -1;
  



  public Zone() {}
  


  public void readExternal(ObjectInput in)
    throws java.io.IOException, ClassNotFoundException
  {
    super.readExternal(in);
    instanceVersion = in.readInt();
    
    node = ((MapObjectNode)in.readObject());
    
    hasCameraData = in.readBoolean();
    
    if (hasCameraData)
    {
      cam = ((CameraController)in.readObject());
      cam.parent = this;
    } else {
      cam = new CameraController(this);
    }
  }
  
  public void writeExternal(ObjectOutput out) throws java.io.IOException
  {
    super.writeExternal(out);
    out.writeInt(0);
    
    out.writeObject(node);
    
    out.writeBoolean(hasCameraData);
    if (hasCameraData) {
      out.writeObject(cam);
    }
  }
  
  public MapObject.MapObjectType getObjectType()
  {
    return MapObject.MapObjectType.ZONE;
  }
  
  public Zone(MapObject.HitType type)
  {
    super(type);
    instanceVersion = 0;
    
    node = new MapObjectNode(this);
    cam = new CameraController(this);
  }
  
  public static Zone makeDummyRoot()
  {
    Zone z = new Zone(MapObject.HitType.ROOT);
    name = "Root";
    return z;
  }
  

  public Zone deepCopy()
  {
    Zone z = new Zone(getType());
    AABB = AABB.deepCopy();
    dirtyAABB = dirtyAABB;
    
    name = name;
    
    mesh = mesh.deepCopy();
    z.updateMeshHierarchy();
    
    hasCameraData = hasCameraData;
    cam = new CameraController(z, cam.getData());
    
    return z;
  }
  

  public MapObjectNode<Zone> getNode()
  {
    return node;
  }
  

  public boolean hasCamera()
  {
    return hasCameraData;
  }
  

  public void addPoints(IdentityHashSet<MutablePoint> positionSet)
  {
    super.addPoints(positionSet);
    
    if (hasCameraData)
    {
      cam.posA.addPoints(positionSet);
      cam.posB.addPoints(positionSet);
      cam.posC.addPoints(positionSet);
    }
  }
  

  public static final class SetZoneName
    extends AbstractCommand
  {
    private final Zone z;
    
    private final String oldName;
    
    private final String newName;
    
    public SetZoneName(Zone z, String s)
    {
      super();
      this.z = z;
      oldName = name;
      newName = s;
    }
    

    public boolean shouldExec()
    {
      return (!newName.isEmpty()) && (!newName.equals(oldName));
    }
    

    public void exec()
    {
      super.exec();
      z.name = newName;
      ZoneInfoPanel.instance().updateFields(z);
    }
    

    public void undo()
    {
      super.undo();
      z.name = oldName;
      ZoneInfoPanel.instance().updateFields(z);
    }
  }
  
  public static final class ToggleCamera extends AbstractCommand
  {
    private final Zone z;
    
    public ToggleCamera(Zone z)
    {
      super();
      this.z = z;
    }
    

    public void exec()
    {
      super.exec();
      z.hasCameraData = (!z.hasCameraData);
      ZoneInfoPanel.instance().updateFields(z);
    }
    

    public void undo()
    {
      super.undo();
      z.hasCameraData = (!z.hasCameraData);
      ZoneInfoPanel.instance().updateFields(z);
    }
  }
}
