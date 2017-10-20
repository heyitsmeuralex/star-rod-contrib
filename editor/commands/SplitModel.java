package editor.commands;

import editor.Editor;
import editor.map.mesh.TexturedMesh;
import editor.map.mesh.TexturedMesh.DisplayListModel;
import editor.map.mesh.Triangle;
import editor.map.shape.Model;
import editor.map.shape.TriangleBatch;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import util.IdentityHashSet;

public class SplitModel extends AbstractCommand
{
  private final Model newModel;
  private final IdentityHashSet<TriangleBatch> oldBatches;
  private final HashMap<TriangleBatch, List<Triangle>> oldBatchLists;
  private final HashMap<TriangleBatch, List<Triangle>> newBatchLists;
  
  public SplitModel(List<Triangle> splitTriangles)
  {
    super("Split Model");
    
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
    HashMap<editor.map.mesh.Vertex, editor.map.mesh.Vertex> vertexMap = new HashMap();
    for (Triangle t : (List)newTriangles)
    {
      for (int i = 0; i < 3; i++)
        vertexMap.put(vert[i], vert[i]);
    }
    for (Triangle t : (List)newTriangles)
    {
      for (int i = 0; i < 3; i++) {
        vert[i] = ((editor.map.mesh.Vertex)vertexMap.get(vert[i]));
      }
    }
    TriangleBatch batch = new TriangleBatch();
    triangles = ((List)newTriangles);
    
    newModel = Model.createBasicModel();
    newModel.name = "Split Model";
    newModel.getMesh().displayListModel.addElement(batch);
    
    newModel.updateMeshHierarchy();
    newModel.dirtyAABB = true;
    newModel.updateTransformHierarchy();
  }
  

  public void exec()
  {
    super.exec();
    
    Editor.map.create(newModel);
    Editor.selectionManager.createObject(newModel);
    
    for (TriangleBatch batch : oldBatches)
    {
      triangles = ((List)newBatchLists.get(batch));
      TexturedMesh mesh = (TexturedMesh)parentMesh;
      displayListModel.setDirty();
      parentObject.dirtyAABB = true;
    }
  }
  

  public void undo()
  {
    super.undo();
    
    Editor.map.remove(newModel);
    Editor.selectionManager.deleteObject(newModel);
    
    for (TriangleBatch batch : oldBatches)
    {
      triangles = ((List)oldBatchLists.get(batch));
      TexturedMesh mesh = (TexturedMesh)parentMesh;
      displayListModel.setDirty();
      parentObject.dirtyAABB = true;
    }
  }
}
