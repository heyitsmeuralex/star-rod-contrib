package editor.map.hit;

import editor.commands.AbstractCommand;
import editor.map.MapObject.HitType;
import editor.map.MapObject.MapObjectType;
import editor.map.tree.MapObjectNode;
import editor.ui.info.ColliderInfoPanel;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class Collider extends HitObject implements java.io.Externalizable
{
  private static final long serialVersionUID = 1L;
  private static final int latestVersion = 0;
  private int instanceVersion = 0;
  

  private MapObjectNode<Collider> node;
  

  public int flags;
  


  public Collider() {}
  

  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    super.readExternal(in);
    instanceVersion = in.readInt();
    
    node = ((MapObjectNode)in.readObject());
    flags = in.readInt();
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    super.writeExternal(out);
    out.writeInt(0);
    
    out.writeObject(node);
    out.writeInt(flags);
  }
  

  public MapObject.MapObjectType getObjectType()
  {
    return MapObject.MapObjectType.COLLIDER;
  }
  

  public Collider(MapObject.HitType type)
  {
    super(type);
    instanceVersion = 0;
    
    node = new MapObjectNode(this);
  }
  
  public static Collider makeDummyRoot()
  {
    Collider c = new Collider(MapObject.HitType.ROOT);
    name = "Root";
    return c;
  }
  

  public Collider deepCopy()
  {
    Collider c = new Collider(getType());
    AABB = AABB.deepCopy();
    dirtyAABB = dirtyAABB;
    name = name;
    
    mesh = mesh.deepCopy();
    c.updateMeshHierarchy();
    
    flags = flags;
    
    return c;
  }
  

  public MapObjectNode<Collider> getNode()
  {
    return node;
  }
  

  public static final class SetColliderName
    extends AbstractCommand
  {
    private final Collider c;
    
    private final String oldName;
    
    private final String newName;
    
    public SetColliderName(Collider c, String s)
    {
      super();
      this.c = c;
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
      c.name = newName;
      ColliderInfoPanel.instance().updateFields(c);
    }
    

    public void undo()
    {
      super.undo();
      c.name = oldName;
      ColliderInfoPanel.instance().updateFields(c);
    }
  }
  
  public static final class SetColliderFlags extends AbstractCommand
  {
    private final Collider c;
    private final int oldValue;
    private final int newValue;
    
    public SetColliderFlags(Collider c, int val)
    {
      super();
      this.c = c;
      oldValue = flags;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      c.flags = newValue;
      ColliderInfoPanel.instance().updateFields(c);
    }
    

    public void undo()
    {
      super.undo();
      c.flags = oldValue;
      ColliderInfoPanel.instance().updateFields(c);
    }
  }
}
