package editor.map.shape.commands;

import java.io.Serializable;

public class FlushPipeline
  extends DisplayCommand
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  public FlushPipeline() {}
  
  public String toString()
  {
    return "RDP Pipe Sync";
  }
  

  public DisplayCommand.CmdType getType()
  {
    return DisplayCommand.CmdType.PipeSync;
  }
  

  public int[] getF3DEX2Command()
  {
    return new int[] { -419430400, 0 };
  }
  

  public DisplayCommand deepCopy()
  {
    return new FlushPipeline();
  }
}
