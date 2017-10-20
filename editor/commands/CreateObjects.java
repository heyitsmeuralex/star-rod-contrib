package editor.commands;

import editor.Editor;
import editor.map.MapObject;

public class CreateObjects extends AbstractCommand
{
  private final Iterable<? extends MapObject> objs;
  
  public CreateObjects(Iterable<? extends MapObject> objs)
  {
    super("Create Objects");
    this.objs = objs;
  }
  

  public void exec()
  {
    super.exec();
    
    for (MapObject obj : objs)
    {
      Editor.map.create(obj);
      Editor.selectionManager.createObject(obj);
    }
  }
  

  public void undo()
  {
    super.undo();
    
    for (MapObject obj : objs)
    {
      Editor.map.remove(obj);
      Editor.selectionManager.deleteObject(obj);
    }
  }
}
