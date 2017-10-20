package editor.commands;

import java.util.ArrayList;

public class CommandBatch extends AbstractCommand
{
  private ArrayList<AbstractCommand> commands;
  private final boolean hasName;
  
  public CommandBatch()
  {
    this("Command Batch", false);
  }
  
  public CommandBatch(String name)
  {
    this(name, true);
  }
  
  private CommandBatch(String name, boolean hasName)
  {
    super(name, !hasName);
    commands = new ArrayList();
    this.hasName = hasName;
  }
  
  public void addCommand(AbstractCommand c)
  {
    commands.add(c);
    
    if (hasName) {
      c.silence();
    }
  }
  
  public void exec()
  {
    super.exec();
    for (int i = 0; i < commands.size(); i++)
    {
      AbstractCommand cmd = (AbstractCommand)commands.get(i);
      if (cmd.getState() != AbstractCommand.STATE.EXECUTED) {
        cmd.exec();
      }
    }
  }
  
  public void undo()
  {
    super.undo();
    for (int i = commands.size() - 1; i >= 0; i--)
    {
      ((AbstractCommand)commands.get(i)).undo();
    }
  }
}
