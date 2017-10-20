package editor.commands;

import util.Logger;

public abstract class AbstractCommand {
  private STATE state;
  
  public static enum STATE { READY,  EXECUTED,  UNDONE;
    
    private STATE() {}
  }
  
  public AbstractCommand(String name)
  {
    this(name, false);
  }
  
  public AbstractCommand(String name, boolean silent)
  {
    state = STATE.READY;
    this.name = name;
    this.silent = silent;
  }
  
  public void silence()
  {
    silent = true;
  }
  
  private final String name;
  private boolean silent;
  public boolean shouldExec() {
    return true;
  }
  
  public boolean modifiesMap()
  {
    return true;
  }
  
  public void exec()
  {
    state = STATE.EXECUTED;
    if (!silent) Logger.log("Exec: " + name);
  }
  
  public void undo()
  {
    state = STATE.UNDONE;
    if (!silent) Logger.log("Undo: " + name);
  }
  
  public STATE getState()
  {
    return state;
  }
}
