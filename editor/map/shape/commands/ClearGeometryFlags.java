package editor.map.shape.commands;

import org.lwjgl.opengl.GL11;

public class ClearGeometryFlags
  extends ChangeGeometryFlags
{
  public ClearGeometryFlags()
  {
    this(-637534209, 0);
  }
  
  public ClearGeometryFlags(int r, int s)
  {
    assert (s == 0);
    setFlags((r | 0xFF000000) ^ 0xFFFFFFFF);
  }
  


  public void doGL()
  {
    if (smoothShading) GL11.glShadeModel(7424);
    if (cullBack) { GL11.glDisable(2884);
    }
  }
  
  public String toString()
  {
    return String.format("Clear geometry flags: %08X", new Object[] { Integer.valueOf(getFlags()) });
  }
  

  public DisplayCommand.CmdType getType()
  {
    return DisplayCommand.CmdType.SetGeometryFlags;
  }
  

  public int[] getF3DEX2Command()
  {
    int flags = (getFlags() ^ 0xFFFFFFFF) & 0xFFFFFF;
    return new int[] { 0xD9000000 | flags, 0 };
  }
  

  public DisplayCommand deepCopy()
  {
    return new ClearGeometryFlags(0xD9000000 | getFlags() ^ 0xFFFFFFFF, 0);
  }
}
