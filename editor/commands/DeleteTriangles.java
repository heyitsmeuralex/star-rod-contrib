package editor.commands;

import editor.map.mesh.Triangle;
import editor.map.shape.TriangleBatch;
import editor.selection.Selection;
import java.util.ArrayList;
import java.util.List;

public class DeleteTriangles extends AbstractCommand
{
  private final ArrayList<Triangle> targets;
  private final Selection<Triangle> selection;
  
  public DeleteTriangles(Selection<Triangle> selection)
  {
    super("Delete Assorted Selection");
    this.selection = selection;
    targets = ((ArrayList)list.clone());
  }
  

  public void exec()
  {
    super.exec();
    
    for (Triangle t : targets)
    {
      parentBatch.triangles.remove(t);
    }
    selection.removeAndDeselect(targets);
    selection.updateAABB();
  }
  

  public void undo()
  {
    super.undo();
    for (Triangle t : targets)
    {
      parentBatch.triangles.add(t);
    }
    selection.addAndSelect(targets);
    selection.updateAABB();
  }
}
