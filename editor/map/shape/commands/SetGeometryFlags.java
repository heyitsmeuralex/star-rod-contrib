package editor.map.shape.commands;

import org.lwjgl.opengl.GL11;

public class SetGeometryFlags
  extends ChangeGeometryFlags
{
  public SetGeometryFlags()
  {
    this(-637534209, 0);
  }
  
  public SetGeometryFlags(int r, int s)
  {
    assert (r == -637534209);
    setFlags(s);
  }
  


  public void doGL()
  {
    if (smoothShading) GL11.glShadeModel(7425);
    if (cullBack) { GL11.glEnable(2884);
    }
  }
  
  public String toString()
  {
    return String.format("Set geometry flags: %08X", new Object[] { Integer.valueOf(getFlags()) });
  }
  

  public DisplayCommand.CmdType getType()
  {
    return DisplayCommand.CmdType.SetGeometryFlags;
  }
  

  public int[] getF3DEX2Command()
  {
    return new int[] { -637534209, getFlags() };
  }
  

  public DisplayCommand deepCopy()
  {
    return new SetGeometryFlags(-637534209, getFlags());
  }
}
