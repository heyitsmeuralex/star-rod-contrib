package editor.commands;

import editor.map.MapObject;

public class SetObjectName extends AbstractCommand
{
  private final MapObject obj;
  private final String oldName;
  private final String newName;
  
  public SetObjectName(MapObject obj, String name)
  {
    super("Set Object Name");
    this.obj = obj;
    newName = name;
    oldName = name;
  }
  

  public void exec()
  {
    super.exec();
    obj.name = newName;
  }
  

  public void undo()
  {
    super.undo();
    obj.name = oldName;
  }
}
