package editor.commands;

import editor.Editor;
import editor.map.Map;
import editor.map.MapObject.HitType;
import editor.map.hit.Collider;
import editor.map.hit.HitObject;
import editor.map.hit.Zone;
import editor.selection.SelectionManager;
import java.util.List;

public abstract class ConvertHitObjects<T extends HitObject, S extends HitObject> extends AbstractCommand
{
  private final List<T> oldObjects;
  private final List<S> newObjects;
  
  public static class ConvertColliders extends ConvertHitObjects<Collider, Zone>
  {
    public ConvertColliders(List<Collider> c)
    {
      super("Convert Collider to Zone");
    }
    

    protected Zone convert(Collider c)
    {
      Zone z = new Zone(MapObject.HitType.HIT);
      name = name;
      mesh = mesh.deepCopy();
      z.updateMeshHierarchy();
      dirtyAABB = true;
      return z;
    }
  }
  
  public static class ConvertZones extends ConvertHitObjects<Zone, Collider>
  {
    public ConvertZones(List<Zone> z)
    {
      super("Convert Zone to Collider");
    }
    

    protected Collider convert(Zone z)
    {
      Collider c = new Collider(MapObject.HitType.HIT);
      name = name;
      mesh = mesh.deepCopy();
      c.updateMeshHierarchy();
      dirtyAABB = true;
      return c;
    }
  }
  
  public ConvertHitObjects(List<T> objs, String name)
  {
    super(name);
    
    oldObjects = new java.util.ArrayList(objs.size());
    newObjects = new java.util.ArrayList(objs.size());
    
    for (T obj : objs)
    {
      if (obj.hasMesh())
      {
        oldObjects.add(obj);
        newObjects.add(convert(obj));
      }
    }
  }
  

  protected abstract S convert(T paramT);
  
  public boolean shouldExec()
  {
    return !newObjects.isEmpty();
  }
  

  public void exec()
  {
    super.exec();
    
    for (int i = oldObjects.size() - 1; i >= 0; i--)
    {
      HitObject obj = (HitObject)oldObjects.get(i);
      Editor.map.remove(obj);
      Editor.selectionManager.deleteObject(obj);
    }
    
    for (int i = 0; i < newObjects.size(); i++)
    {
      HitObject obj = (HitObject)newObjects.get(i);
      Editor.map.create(obj);
      Editor.selectionManager.createObject(obj);
    }
  }
  

  public void undo()
  {
    super.undo();
    
    for (int i = newObjects.size() - 1; i >= 0; i--)
    {
      HitObject obj = (HitObject)newObjects.get(i);
      Editor.map.remove(obj);
      Editor.selectionManager.deleteObject(obj);
    }
    
    for (int i = 0; i < oldObjects.size(); i++)
    {
      HitObject obj = (HitObject)oldObjects.get(i);
      Editor.map.add(obj);
      Editor.selectionManager.createObject(obj);
    }
  }
}
