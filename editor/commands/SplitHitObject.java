package editor.commands;

import editor.Editor;
import editor.map.Map;
import editor.map.hit.Collider;
import editor.map.hit.Zone;
import editor.map.mesh.AbstractMesh;
import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.TriangleBatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import util.IdentityHashSet;

public abstract class SplitHitObject<T extends editor.map.hit.HitObject> extends AbstractCommand
{
  private final T newObject;
  private final IdentityHashSet<TriangleBatch> oldBatches;
  private final HashMap<TriangleBatch, List<Triangle>> oldBatchLists;
  private final HashMap<TriangleBatch, List<Triangle>> newBatchLists;
  
  public static class SplitCollider extends SplitHitObject<Collider>
  {
    public SplitCollider(List<Triangle> splitTriangles)
    {
      super("Split Collider");
    }
    

    protected Collider createObject(TriangleBatch batch)
    {
      Collider c = new Collider(editor.map.MapObject.HitType.HIT);
      name = "Split Collider";
      mesh.batch = batch;
      c.updateMeshHierarchy();
      dirtyAABB = true;
      return c;
    }
  }
  
  public static class SplitZone extends SplitHitObject<Zone>
  {
    public SplitZone(List<Triangle> splitTriangles)
    {
      super("Split Zone");
    }
    

    protected Zone createObject(TriangleBatch batch)
    {
      Zone z = new Zone(editor.map.MapObject.HitType.HIT);
      name = "Split Zone";
      mesh.batch = batch;
      z.updateMeshHierarchy();
      dirtyAABB = true;
      return z;
    }
  }
  
  public SplitHitObject(List<Triangle> splitTriangles, String name)
  {
    super(name);
    
    oldBatches = new IdentityHashSet(splitTriangles.size());
    oldBatchLists = new HashMap();
    newBatchLists = new HashMap();
    
    for (Triangle t : splitTriangles) {
      oldBatches.add(parentBatch);
    }
    for (??? = oldBatches.iterator(); ???.hasNext();) { batch = (TriangleBatch)???.next();
      
      List<Triangle> newTriangles = new ArrayList(triangles);
      newTriangles.removeAll(splitTriangles);
      
      oldBatchLists.put(batch, new ArrayList(triangles));
      newBatchLists.put(batch, new ArrayList(newTriangles));
    }
    

    Object newTriangles = new ArrayList(splitTriangles.size());
    for (TriangleBatch batch = splitTriangles.iterator(); batch.hasNext();) { t = (Triangle)batch.next();
      ((List)newTriangles).add(t.deepCopy());
    }
    Triangle t;
    HashMap<Vertex, Vertex> vertexMap = new HashMap();
    for (Triangle t : (List)newTriangles)
    {
      for (int i = 0; i < 3; i++)
        vertexMap.put(vert[i], vert[i]);
    }
    for (Triangle t : (List)newTriangles)
    {
      for (int i = 0; i < 3; i++) {
        vert[i] = ((Vertex)vertexMap.get(vert[i]));
      }
    }
    TriangleBatch batch = new TriangleBatch();
    triangles = ((List)newTriangles);
    newObject = createObject(batch);
  }
  

  protected abstract T createObject(TriangleBatch paramTriangleBatch);
  
  public void exec()
  {
    super.exec();
    
    Editor.map.create(newObject);
    Editor.selectionManager.createObject(newObject);
    
    for (TriangleBatch batch : oldBatches)
    {
      triangles = ((List)newBatchLists.get(batch));
      parentMesh.parentObject.dirtyAABB = true;
    }
  }
  

  public void undo()
  {
    super.undo();
    
    Editor.map.remove(newObject);
    Editor.selectionManager.deleteObject(newObject);
    
    for (TriangleBatch batch : oldBatches)
    {
      triangles = ((List)oldBatchLists.get(batch));
      parentMesh.parentObject.dirtyAABB = true;
    }
  }
}
