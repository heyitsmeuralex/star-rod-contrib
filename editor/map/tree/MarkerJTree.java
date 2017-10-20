package editor.map.tree;

import editor.map.marker.Marker;
import editor.ui.MapObjectPanel;
import editor.ui.SwingGUI;
import java.awt.event.ActionEvent;
import javax.swing.JPopupMenu;


public class MarkerJTree
  extends MapObjectJTree<Marker>
{
  private static final long serialVersionUID = 1L;
  
  public MarkerJTree(SwingGUI gui, MapObjectPanel panel)
  {
    super(gui, panel);
    setRootVisible(false);
  }
  
  protected JPopupMenu createPopupMenu(JPopupMenu popupMenu)
  {
    return popupMenu;
  }
  
  public void actionPerformed(ActionEvent e) {}
}
