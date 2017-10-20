package editor.ui.info;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

public class ColliderFlagsPanel
  extends JPanel
{
  private static final long serialVersionUID = 1L;
  private final List<FlagCheckBox> checkboxes;
  
  private static enum ColliderFlags
  {
    WATER_FLOOR(1, "Enable Water Trail (floor)", "Generate ripple effects as the player moves around this collider."), 
    LAVA_FLOOR(2, "Treat as Lava (floor)", "Requires a background script to reset player position (see example maps)."), 
    SNOW_FLOOR(8, "Treat as Snow (floor)", "Generate footprints as the player moves around this collider."), 
    DOCK_WALL(4, "Sushi Docking Plane (wall)", "Players can embark/disembark Sushi through this wall."), 
    IGNORE_SHELL(32768, "Ignore Shell Collision", "Disables collision with Kooper's shell attack."), 
    IGNORE_PLAYER(65536, "Ignore Player Collision", "Disables collision with the player and partner.");
    

    private final int bit;
    private final String name;
    private final String desc;
    
    private ColliderFlags(int bit, String name, String desc)
    {
      this.bit = bit;
      this.name = name;
      this.desc = desc;
    }
  }
  
  private static class FlagCheckBox extends JCheckBox
  {
    private static final long serialVersionUID = 1L;
    public final ColliderFlagsPanel.ColliderFlags flag;
    
    public FlagCheckBox(ColliderFlagsPanel.ColliderFlags flag)
    {
      super();
      this.flag = flag;
      
      if (!ColliderFlagsPanel.ColliderFlags.access$100(flag).isEmpty()) {
        super.setToolTipText(ColliderFlagsPanel.ColliderFlags.access$100(flag));
      }
    }
  }
  


  public ColliderFlagsPanel(int flags)
  {
    checkboxes = new ArrayList();
    
    setLayout(new MigLayout("wrap 3", "[fill]8[fill,grow]"));
    
    FlagCheckBox cb = new FlagCheckBox(ColliderFlags.IGNORE_PLAYER);
    checkboxes.add(cb);
    add(cb, "wrap");
    
    cb = new FlagCheckBox(ColliderFlags.IGNORE_SHELL);
    checkboxes.add(cb);
    add(cb, "wrap");
    
    cb = new FlagCheckBox(ColliderFlags.DOCK_WALL);
    checkboxes.add(cb);
    add(cb, "wrap");
    
    cb = new FlagCheckBox(ColliderFlags.LAVA_FLOOR);
    checkboxes.add(cb);
    add(cb, "wrap");
    
    cb = new FlagCheckBox(ColliderFlags.SNOW_FLOOR);
    checkboxes.add(cb);
    add(cb, "wrap");
    
    cb = new FlagCheckBox(ColliderFlags.WATER_FLOOR);
    checkboxes.add(cb);
    add(cb, "wrap");
    
    for (FlagCheckBox box : checkboxes) {
      box.setSelected((flags & flag.bit) != 0);
    }
  }
  
  public int getValues(int flags) {
    for (FlagCheckBox cb : checkboxes)
    {
      if (cb.isSelected()) {
        flags |= flag.bit;
      } else
        flags &= (flag.bit ^ 0xFFFFFFFF);
    }
    return flags;
  }
}
