package editor.commands;

import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;

public class InvertNormals extends AbstractCommand
{
  private Iterable<Triangle> triangles;
  
  public InvertNormals(Iterable<Triangle> triangles)
  {
    super("Invert Normals");
    this.triangles = triangles;
  }
  
  public void exec()
  {
    super.exec();
    
    for (Triangle t : triangles)
    {
      Vertex temp = vert[1];
      vert[1] = vert[2];
      vert[2] = temp;
    }
  }
  
  public void undo()
  {
    super.undo();
    
    for (Triangle t : triangles)
    {
      Vertex temp = vert[1];
      vert[1] = vert[2];
      vert[2] = temp;
    }
  }
}
