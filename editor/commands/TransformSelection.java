package editor.commands;

import editor.Editor;
import editor.map.ReversibleTransform;
import editor.map.shape.TransformMatrix;
import editor.selection.Selectable;
import editor.selection.Selection;
import editor.ui.SwingGUI;
import java.util.LinkedList;

public class TransformSelection<T extends Selectable> extends AbstractCommand
{
  private LinkedList<ReversibleTransform> transformerList;
  private Selection<T> selection;
  
  public TransformSelection(Selection<T> selection, TransformMatrix m)
  {
    super("Transform Selection");
    this.selection = selection;
    
    transformerList = new LinkedList();
    for (T item : list)
    {
      if (item.transforms())
      {

        ReversibleTransform t = item.createTransformer(m);
        
        if (t != null) {
          transformerList.add(t);
        }
        item.endTransformation();
      }
    }
  }
  
  public void exec()
  {
    super.exec();
    
    for (ReversibleTransform t : transformerList) {
      t.transform();
    }
    selection.updateAABB();
    Editor.gui.updateInfoPanel();
  }
  

  public void undo()
  {
    super.undo();
    
    for (ReversibleTransform t : transformerList) {
      t.revert();
    }
    selection.updateAABB();
    Editor.gui.updateInfoPanel();
  }
}
