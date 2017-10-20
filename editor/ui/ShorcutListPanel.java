package editor.ui;

import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import net.miginfocom.swing.MigLayout;


public class ShorcutListPanel
  extends JPanel
{
  private static final long serialVersionUID = 1L;
  
  public ShorcutListPanel()
  {
    setLayout(new MigLayout("fill"));
    
    JTabbedPane tabs = new JTabbedPane();
    
    tabs.addTab("Editor", getEditorTab());
    tabs.addTab("Selection", getSelectionTab());
    tabs.addTab("Transform", getTransformTab());
    tabs.addTab("Viewports", getViewportsTab());
    tabs.addTab("Grid", getGridTab());
    
    add(tabs, "grow, w 320!, h 320!");
  }
  
  private void addHeader(JPanel panel, String text)
  {
    boolean first = panel.getComponentCount() == 0;
    String fmt = first ? "span, wrap" : "span, wrap, gaptop 8";
    
    JLabel lbl = new JLabel(text);
    lbl.setFont(new Font(lbl.getFont().getFontName(), 1, 12));
    
    panel.add(lbl, fmt);
  }
  
  private void addShortcut(JPanel panel, String name, String keys)
  {
    addShortcut(panel, name, keys, "");
  }
  
  private void addShortcut(JPanel panel, String desc, String keys, String tip)
  {
    String lblText = desc + "*";
    JLabel lbl = new JLabel(lblText);
    if (!tip.isEmpty())
      lbl.setToolTipText(tip);
    panel.add(lbl);
    panel.add(new JLabel(keys), "wrap");
  }
  
  private JPanel getEditorTab()
  {
    JPanel tab = new JPanel(new MigLayout("fillx", "[50%][50%]"));
    
    addHeader(tab, "General");
    addShortcut(tab, "Save", "Ctrl + S");
    addShortcut(tab, "Quit", "Esc");
    addShortcut(tab, "Undo", "Ctrl + X");
    addShortcut(tab, "Redo", "Ctrl + Y");
    
    addHeader(tab, "Set Transform Tab");
    addShortcut(tab, "Models", "1");
    addShortcut(tab, "Colliders", "2");
    addShortcut(tab, "Zones", "3");
    addShortcut(tab, "Markers", "4");
    
    return tab;
  }
  
  private JPanel getSelectionTab()
  {
    JPanel tab = new JPanel(new MigLayout("fillx", "[50%][50%]"));
    
    addHeader(tab, "Change Selection");
    addShortcut(tab, "Select", "Left Click");
    addShortcut(tab, "Select Add/Remove", "Ctrl + Left Click");
    addShortcut(tab, "Select All", "Ctrl + A");
    addShortcut(tab, "Find Object", "Ctrl + F");
    addShortcut(tab, "Copy Objects", "Ctrl + C", "You can even copy/paste objects between maps.");
    addShortcut(tab, "Paste Objects", "Ctrl + V");
    addShortcut(tab, "Delete Selected", "Delete");
    
    addHeader(tab, "Modify Properties of Selection");
    addShortcut(tab, "Hide Selected", "H");
    addShortcut(tab, "Open UV Editor", "U", "Must have models or model triangles selected.");
    addShortcut(tab, "Toggle Camera Preview", "P", "Must have an object with a camera controller selected.");
    addShortcut(tab, "Toggle Double Sided", "L", "<html>Toggles triangle type for selected zones and colliders.<br>Useful for making one-way walls.</html>");
    


    return tab;
  }
  
  private JPanel getTransformTab()
  {
    JPanel tab = new JPanel(new MigLayout("fillx", "[50%][50%]"));
    
    addHeader(tab, "Transforming Selected Objects");
    addShortcut(tab, "Translate", "Left Mouse Drag");
    addShortcut(tab, "Rotate", "Right Mouse Drag");
    addShortcut(tab, "Scale", "Space + Left Mouse Drag");
    addShortcut(tab, "Uniform Scale", "Space + Right Mouse Drag");
    addShortcut(tab, "Clone", "Any above + Alt");
    
    addHeader(tab, "Special Transformations");
    addShortcut(tab, "Flip Along X", "Shift + X");
    addShortcut(tab, "Flip Along Y", "Shift + Y");
    addShortcut(tab, "Filp Along Z", "Shift + Z");
    addShortcut(tab, "Flip Normals", "Shift + N");
    addShortcut(tab, "Open Transform Menu", "Ctrl + T");
    
    addShortcut(tab, "Nudge to Grid", "Ctrl + N", "Moves any selected verticies to the nearest grid point.");
    
    return tab;
  }
  
  private JPanel getViewportsTab()
  {
    JPanel tab = new JPanel(new MigLayout("fillx", "[50%][50%]"));
    
    addHeader(tab, "Moving the Camera");
    addShortcut(tab, "Pan (2D View)", "W/A/S/D");
    addShortcut(tab, "Zoom (2D View)", "Mousewheel or Arrow Keys");
    addShortcut(tab, "Move (3D View)", "Hold Shift + W/A/S/D");
    addShortcut(tab, "Center on Selection", "C");
    
    addHeader(tab, "Rendering Options");
    addShortcut(tab, "Toggle 4-View", "F");
    addShortcut(tab, "Toggle Wireframe", "T");
    addShortcut(tab, "Toggle Edge Highlights", "E");
    addShortcut(tab, "Toggle Normals", "N");
    
    addHeader(tab, "Object Visibility");
    addShortcut(tab, "Toggle Models", "F1");
    addShortcut(tab, "Toggle Colliders", "F2");
    addShortcut(tab, "Toggle Zones", "F3");
    addShortcut(tab, "Toggle Markers", "F4");
    
    return tab;
  }
  
  private JPanel getGridTab()
  {
    JPanel tab = new JPanel(new MigLayout("fillx", "[50%][50%]"));
    
    addHeader(tab, "Grid Options");
    addShortcut(tab, "Toggle Grid", "G");
    addShortcut(tab, "Switch Grid Type", "Shift + G", "<html>Switches between \"powers of 2\" (binary) and \"powers of 10\" (decimal) grids.<br>Most in-game geometry follows a decimal grid.</html>");
    

    addShortcut(tab, "Increase Grid Spacing", "+");
    addShortcut(tab, "Decrease Grid Spacing", "-");
    
    addHeader(tab, "Snap Options");
    addShortcut(tab, "Snap Translation", "7");
    addShortcut(tab, "Snap Rotation", "8");
    addShortcut(tab, "Snap Scale", "9");
    addShortcut(tab, "Toggle Scale Snap Mode", "0", "<html>Switches the rescale snap method between:<br>a. increments of 10%<br>b. nearest multiple of grid spacing</html>");
    



    return tab;
  }
}
