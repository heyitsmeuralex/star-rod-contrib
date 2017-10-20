package editor.commands;

import editor.Editor;
import editor.map.Map;
import editor.map.hit.Collider;
import editor.map.hit.HitObject;
import editor.map.hit.Zone;
import editor.map.shape.TriangleBatch;
import java.util.List;

public abstract class JoinHitObjects<T extends HitObject> extends AbstractCommand
{
  private List<T> objs;
  private final TriangleBatch oldBatch;
  private final TriangleBatch newBatch;
  
  public static class JoinColliders extends JoinHitObjects<Collider>
  {
    public JoinColliders(List<Collider> colliders)
    {
      super("Join Colliders", null);
    }
  }
  
  public static class JoinZones extends JoinHitObjects<Zone>
  {
    public JoinZones(List<Zone> colliders)
    {
      super("Join Zones", null);
    }
  }
  
  private JoinHitObjects(List<T> objs, String name)
  {
    super(name);
    this.objs = objs;
    
    oldBatch = get0mesh.batch;
    newBatch = new TriangleBatch();
    
    for (HitObject hit : objs) {
      newBatch.triangles.addAll(mesh.batch.triangles);
    }
  }
  
  public void exec()
  {
    super.exec();
    
    HitObject first = (HitObject)objs.get(0);
    mesh.batch = newBatch;
    dirtyAABB = true;
    
    for (int i = 1; i < objs.size(); i++)
    {
      HitObject obj = (HitObject)objs.get(i);
      Editor.map.remove(obj);
      Editor.selectionManager.deleteObject(obj);
    }
  }
  

  public void undo()
  {
    super.undo();
    
    HitObject first = (HitObject)objs.get(0);
    mesh.batch = oldBatch;
    dirtyAABB = true;
    
    for (int i = objs.size() - 1; i >= 1; i--)
    {
      HitObject obj = (HitObject)objs.get(i);
      Editor.map.add(obj);
      Editor.selectionManager.createObject(obj);
    }
  }
}
