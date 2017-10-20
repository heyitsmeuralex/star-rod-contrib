package editor.commands;

import editor.Editor;
import editor.map.MapObject;
import editor.selection.Selection;
import editor.selection.SelectionManager;
import java.util.ArrayList;

public class CloneObjects extends AbstractCommand
{
  private ArrayList<MapObject> copies;
  private ArrayList<MapObject> originals;
  
  public CloneObjects(Selection<MapObject> selection)
  {
    super("Clone Selection");
    copies = new ArrayList();
    originals = new ArrayList();
    
    for (MapObject obj : list)
    {
      System.out.println(obj);
      originals.add(obj);
      copies.add(obj.deepCopy());
    }
  }
  

  public void exec()
  {
    super.exec();
    Editor.selectionManager.clearObjectSelection();
    
    for (MapObject obj : copies)
    {
      Editor.map.create(obj);
      Editor.selectionManager.createObject(obj);
    }
  }
  

  public void undo()
  {
    super.undo();
    Editor.selectionManager.clearObjectSelection();
    
    for (MapObject obj : copies)
    {
      Editor.map.remove(obj);
      Editor.selectionManager.deleteObject(obj);
    }
    
    for (MapObject obj : originals) {
      Editor.selectionManager.selectObject(obj);
    }
  }
}
