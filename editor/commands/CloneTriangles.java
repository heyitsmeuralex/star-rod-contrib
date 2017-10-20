package editor.commands;

import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.selection.Selection;
import java.util.ArrayList;

public class CloneTriangles extends AbstractCommand
{
  private ArrayList<Triangle> copies;
  private ArrayList<Triangle> originals;
  private final Selection<Triangle> selection;
  
  public CloneTriangles(Selection<Triangle> selection)
  {
    super("Clone Selection");
    this.selection = selection;
    copies = new ArrayList();
    originals = new ArrayList();
    
    for (Triangle t : list)
    {
      originals.add(t);
      
      Triangle copy = t.deepCopy();
      parentBatch = parentBatch;
      
      vert[0].parentMesh = vert[0].parentMesh;
      vert[1].parentMesh = vert[1].parentMesh;
      vert[2].parentMesh = vert[2].parentMesh;
      
      copies.add(copy);
    }
  }
  

  public void exec()
  {
    super.exec();
    selection.clear();
    
    for (Triangle t : copies) {
      parentBatch.triangles.add(t);
    }
    selection.addAndSelect(copies);
  }
  

  public void undo()
  {
    super.undo();
    selection.clear();
    
    for (Triangle t : copies) {
      parentBatch.triangles.remove(t);
    }
    selection.addAndSelect(originals);
  }
}
