package editor.commands;

import editor.Editor;
import editor.map.MapObject;
import editor.map.tree.MapObjectNode;
import editor.ui.SwingGUI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class HideObjects extends AbstractCommand
{
  private final ArrayList<MapObject> hideList;
  
  public HideObjects(List<MapObject> objs)
  {
    super("Hide Objects");
    
    Set<MapObject> hideSet = getSetWithDescendents(objs);
    hideList = new ArrayList(hideSet);
  }
  




  private static Set<MapObject> getSetWithDescendents(Iterable<MapObject> objs)
  {
    HashSet<MapObject> set = new HashSet();
    Stack<MapObjectNode<?>> nodes = new Stack();
    
    for (MapObject obj : objs)
    {
      nodes.push(obj.getNode());
      while (!nodes.isEmpty())
      {
        MapObjectNode<?> node = (MapObjectNode)nodes.pop();
        MapObject current = node.getUserObject();
        if (node.isRoot())
        {
          for (int i = 0; i < node.getChildCount(); i++) {
            nodes.push(node.getChildAt(i));
          }
        } else if (!set.contains(current))
        {
          set.add(current);
          
          if (!current.hasMesh())
          {
            for (int i = 0; i < node.getChildCount(); i++) {
              nodes.push(node.getChildAt(i));
            }
          }
        }
      }
    }
    return set;
  }
  

  public boolean shouldExec()
  {
    return hideList.iterator().hasNext();
  }
  

  public boolean modifiesMap()
  {
    return false;
  }
  

  public void exec()
  {
    super.exec();
    
    for (MapObject obj : hideList)
      hidden = (!hidden);
    Editor.gui.repaintObjectPanel();
  }
  

  public void undo()
  {
    super.undo();
    
    for (MapObject obj : hideList)
      hidden = (!hidden);
    Editor.gui.repaintObjectPanel();
  }
}
