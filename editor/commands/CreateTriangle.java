package editor.commands;

import editor.camera.ViewType;
import editor.map.mesh.AbstractMesh;
import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.TriangleBatch;
import editor.map.shape.UV;
import java.util.Iterator;
import java.util.List;
import org.lwjgl.util.vector.Vector3f;

public class CreateTriangle
  extends AbstractCommand
{
  private Vertex lastVertex;
  private Triangle newTriangle;
  private TriangleBatch batch = null;
  private boolean abort = false;
  
  public CreateTriangle(List<Vertex> verticies, Vertex lastVertex, ViewType type)
  {
    super("Create Triangle");
    
    this.lastVertex = lastVertex;
    newTriangle = new Triangle((Vertex)verticies.get(0), (Vertex)verticies.get(1), (Vertex)verticies.get(2));
    
    for (Object localObject = parentMesh.iterator(); ((Iterator)localObject).hasNext();) { t = (Triangle)((Iterator)localObject).next();
      for (Vertex v : vert)
      {
        if (v == lastVertex)
          batch = parentBatch;
      }
    }
    localObject = newTriangle.vert;Triangle t = localObject.length; for (Triangle localTriangle1 = 0; localTriangle1 < t; localTriangle1++) { Vertex vertex = localObject[localTriangle1];
      
      switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()]) {
      case 1: 
        uv = new UV(16 * vertex.getCurrentX(), 16 * vertex.getCurrentY()); break;
      case 2:  uv = new UV(16 * vertex.getCurrentZ(), 16 * vertex.getCurrentY()); break;
      case 3:  uv = new UV(16 * vertex.getCurrentX(), 16 * vertex.getCurrentZ()); break;
      case 4: 
        throw new UnsupportedOperationException();
      }
      
    }
    float[] tn = newTriangle.getNormal();
    if (tn == null) {
      abort = true;
    }
    else {
      Vector3f normal = new Vector3f(tn[0], tn[1], tn[2]);
      Vector3f view = null;
      switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()]) {
      case 1: 
        view = new Vector3f(0.0F, 0.0F, 1.0F); break;
      case 2:  view = new Vector3f(1.0F, 0.0F, 0.0F); break;
      case 3:  view = new Vector3f(0.0F, 1.0F, 0.0F); break;
      case 4: 
        throw new UnsupportedOperationException();
      }
      if (Vector3f.dot(view, normal) < 0.0F)
      {
        Vertex temp = newTriangle.vert[0];
        newTriangle.vert[0] = newTriangle.vert[1];
        newTriangle.vert[1] = temp;
      }
    }
  }
  

  public boolean shouldExec()
  {
    return (batch != null) && (!abort);
  }
  

  public void exec()
  {
    super.exec();
    
    batch.triangles.add(newTriangle);
    lastVertex.parentMesh.updateHierarchy();
  }
  

  public void undo()
  {
    super.undo();
    
    batch.triangles.remove(newTriangle);
    lastVertex.parentMesh.updateHierarchy();
  }
}
