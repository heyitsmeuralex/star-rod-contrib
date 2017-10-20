package editor.commands;

import editor.map.mesh.Vertex;
import java.util.IdentityHashMap;
import java.util.Set;
import org.lwjgl.util.Color;

public class PaintVerticies
  extends AbstractCommand
{
  IdentityHashMap<Vertex, Color> oldColorMap;
  IdentityHashMap<Vertex, Color> newColorMap;
  
  public PaintVerticies(IdentityHashMap<Vertex, Color> oldColorMap, IdentityHashMap<Vertex, Color> newColorMap)
  {
    super("Painting " + newColorMap.keySet().size() + " Verticies");
    this.oldColorMap = oldColorMap;
    this.newColorMap = newColorMap;
  }
  

  public void exec()
  {
    super.exec();
    
    for (Vertex v : newColorMap.keySet())
    {
      Color newColor = (Color)newColorMap.get(v);
      r = newColor.getRedByte();
      g = newColor.getGreenByte();
      b = newColor.getBlueByte();
      a = newColor.getAlphaByte();
    }
  }
  

  public void undo()
  {
    super.undo();
    
    for (Vertex v : newColorMap.keySet())
    {
      Color oldColor = (Color)oldColorMap.get(v);
      r = oldColor.getRedByte();
      g = oldColor.getGreenByte();
      b = oldColor.getBlueByte();
      a = oldColor.getAlphaByte();
    }
  }
}
