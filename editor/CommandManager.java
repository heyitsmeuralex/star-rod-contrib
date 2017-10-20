package editor;

import editor.commands.AbstractCommand;
import java.util.Stack;
import util.EvictingStack;
import util.Logger;


public class CommandManager
{
  private EvictingStack<AbstractCommand> undoStack;
  private Stack<AbstractCommand> redoStack;
  
  public CommandManager(int undoSize)
  {
    undoStack = new EvictingStack(undoSize);
    redoStack = new Stack();
  }
  
  public void executeCommand(AbstractCommand cmd)
  {
    if (!cmd.shouldExec()) {
      return;
    }
    cmd.exec();
    undoStack.push(cmd);
    redoStack.clear();
    
    if (cmd.modifiesMap()) {
      mapmodified = true;
    }
  }
  





  public void pushCommand(AbstractCommand cmd)
  {
    undoStack.push(cmd);
    redoStack.clear();
  }
  
  public void action_Undo()
  {
    if (undoStack.size() > 0) {
      AbstractCommand cmd = (AbstractCommand)undoStack.pop();
      cmd.undo();
      redoStack.push(cmd);
    } else {
      Logger.log("Can't undo any more.");
    }
  }
  
  public void action_Redo()
  {
    if (redoStack.size() > 0) {
      AbstractCommand cmd = (AbstractCommand)redoStack.pop();
      cmd.exec();
      undoStack.push(cmd);
    } else {
      Logger.log("Can't redo anything.");
    }
  }
}
