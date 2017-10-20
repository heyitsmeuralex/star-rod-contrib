package editor.commands;

import editor.Editor;
import editor.map.MapObject;
import editor.map.tree.MapObjectTreeModel;
import editor.ui.SwingGUI;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBoxMenuItem;


public class HideObjectTree
  extends AbstractCommand
{
  private final List<MapObject> objs;
  private final boolean[] wasHidden;
  private final JCheckBoxMenuItem checkBox;
  private final boolean hiding;
  
  public HideObjectTree(String name, MapObjectTreeModel<?> tree, JCheckBoxMenuItem checkBox, boolean hide)
  {
    super("Show " + name);
    objs = new ArrayList(64);
    
    for (MapObject obj : tree) {
      objs.add(obj);
    }
    wasHidden = new boolean[objs.size()];
    for (int i = 0; i < objs.size(); i++) {
      wasHidden[i] = objs.get(i)).hidden;
    }
    this.checkBox = checkBox;
    hiding = hide;
  }
  

  public boolean modifiesMap()
  {
    return false;
  }
  

  public void exec()
  {
    super.exec();
    
    for (MapObject obj : objs) {
      hidden = hiding;
    }
    checkBox.setSelected(!hiding);
    Editor.gui.repaintObjectPanel();
  }
  

  public void undo()
  {
    super.undo();
    
    for (int i = 0; i < objs.size(); i++) {
      objs.get(i)).hidden = wasHidden[i];
    }
    checkBox.setSelected(hiding);
    Editor.gui.repaintObjectPanel();
  }
}
