package editor.commands;

import editor.map.mesh.Triangle;

public class ToggleDoubleSided extends AbstractCommand
{
  private Iterable<Triangle> triangles;
  
  public ToggleDoubleSided(Iterable<Triangle> triangles)
  {
    super("Toggle Double Sided");
    this.triangles = triangles;
  }
  
  public void exec()
  {
    super.exec();
    
    for (Triangle t : triangles)
    {
      doubleSided = (!doubleSided);
    }
  }
  
  public void undo()
  {
    super.undo();
    
    for (Triangle t : triangles)
    {
      doubleSided = (!doubleSided);
    }
  }
}
