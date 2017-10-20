package editor.commands;

import editor.Editor;
import editor.map.MapObject;

public class CreateObject extends AbstractCommand
{
  private final MapObject obj;
  
  public CreateObject(MapObject obj)
  {
    super("Create Object");
    this.obj = obj;
  }
  

  public void exec()
  {
    super.exec();
    
    Editor.map.create(obj);
    Editor.selectionManager.createObject(obj);
  }
  

  public void undo()
  {
    super.undo();
    
    Editor.map.remove(obj);
    Editor.selectionManager.deleteObject(obj);
  }
}
