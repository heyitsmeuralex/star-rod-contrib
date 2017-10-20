package editor.commands;

import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.TriangleBatch;
import java.util.ArrayList;
import java.util.List;

public class SeparateVerticies extends AbstractCommand
{
  private List<Triangle> triangles;
  private List<Vertex> oldVerticies;
  private List<Vertex> newVerticies;
  
  public SeparateVerticies(List<Triangle> triangles)
  {
    super("Separate Verticies");
    
    this.triangles = triangles;
    oldVerticies = new ArrayList(triangles.size() * 3);
    newVerticies = new ArrayList(triangles.size() * 3);
    
    for (Triangle t : triangles)
    {
      for (i = 0; i < 3; i++)
      {
        oldVerticies.add(vert[i]);
      }
    }
    int i;
    for (Triangle t : triangles)
    {
      for (Vertex v : vert)
      {
        Vertex v2 = v.deepCopy();
        parentMesh = parentBatch.parentMesh;
        newVerticies.add(v2);
      }
    }
  }
  

  public void exec()
  {
    super.exec();
    
    for (int i = 0; i < triangles.size(); i++)
    {
      Triangle t = (Triangle)triangles.get(i);
      
      for (int j = 0; j < 3; j++)
      {
        Vertex v = (Vertex)newVerticies.get(3 * i + j);
        vert[j] = v;
      }
    }
  }
  

  public void undo()
  {
    super.undo();
    
    for (int i = 0; i < triangles.size(); i++)
    {
      Triangle t = (Triangle)triangles.get(i);
      
      for (int j = 0; j < 3; j++)
      {
        Vertex v = (Vertex)oldVerticies.get(3 * i + j);
        vert[j] = v;
      }
    }
  }
}
