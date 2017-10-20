package editor.commands;

import editor.Editor;
import editor.map.Map;
import editor.map.MapObject;
import editor.selection.Selection;
import editor.selection.SelectionManager;
import java.util.ArrayList;
import java.util.Set;

public class DeleteObjects extends AbstractCommand
{
  private final ArrayList<MapObject> deleteList;
  
  public DeleteObjects(Selection<MapObject> selection)
  {
    super("Delete Assorted Selection");
    
    Set<MapObject> deleteSet = MapObject.getSetWithDescendents(list);
    deleteList = new ArrayList(deleteSet);
    java.util.Collections.sort(deleteList, new editor.map.MapObject.DeleteComparator());
  }
  

  public void exec()
  {
    super.exec();
    
    for (MapObject obj : deleteList)
    {
      Editor.map.remove(obj);
      Editor.selectionManager.deleteObject(obj);
    }
  }
  

  public void undo()
  {
    super.undo();
    
    for (int i = deleteList.size() - 1; i >= 0; i--)
    {
      MapObject obj = (MapObject)deleteList.get(i);
      Editor.map.add(obj);
      Editor.selectionManager.createObject(obj);
    }
  }
}
