package editor.map.shape.commands;

import editor.commands.AbstractCommand;
import editor.map.shape.Model;
import editor.ui.info.ModelInfoPanel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;






public abstract class ChangeGeometryFlags
  extends DisplayCommand
{
  protected transient boolean cullBack;
  protected transient boolean useLighting;
  protected transient boolean smoothShading;
  
  public ChangeGeometryFlags() {}
  
  public static ChangeGeometryFlags getCommand(int r, int s)
  {
    if (r == -637534209) {
      return new SetGeometryFlags(r, s);
    }
    return new ClearGeometryFlags(r, s);
  }
  
  protected void setFlags(int flags)
  {
    cullBack = ((flags & 0x400) != 0);
    useLighting = ((flags & 0x20000) != 0);
    smoothShading = ((flags & 0x200000) != 0);
  }
  
  protected int getFlags()
  {
    int s = 0;
    if (cullBack) s |= 0x400;
    if (useLighting) s |= 0x20000;
    if (smoothShading) s |= 0x200000;
    return s;
  }
  
  public static final class GeometryFlagsPanel
    extends JPanel
  {
    private static final long serialVersionUID = 1L;
    private final JCheckBox cullBackCheckBox;
    private final JCheckBox useLightingCheckBox;
    private final JCheckBox smoothShadingCheckBox;
    
    public GeometryFlagsPanel(ChangeGeometryFlags cmd)
    {
      cullBackCheckBox = new JCheckBox();
      cullBackCheckBox.setSelected(cullBack);
      
      useLightingCheckBox = new JCheckBox();
      useLightingCheckBox.setSelected(useLighting);
      
      smoothShadingCheckBox = new JCheckBox();
      smoothShadingCheckBox.setSelected(smoothShading);
      
      setLayout(new MigLayout("fill, wrap 2"));
      
      add(cullBackCheckBox);
      add(new JLabel("Cull Back Faces"), "push");
      
      add(useLightingCheckBox);
      add(new JLabel("Enable Lighting"), "push");
      
      add(smoothShadingCheckBox);
      add(new JLabel("Use Smooth Shading"), "push");
    }
  }
  
  public final class SetFlags extends AbstractCommand { private final Model mdl;
    private final boolean oldCullBack;
    private final boolean oldUseLight;
    private final boolean oldSmoothShade;
    private final boolean newCullBack;
    private final boolean newUseLight;
    private final boolean newSmoothShade;
    
    public SetFlags(Model mdl, ChangeGeometryFlags.GeometryFlagsPanel panel) { super();
      this.mdl = mdl;
      
      oldCullBack = cullBack;
      oldUseLight = useLighting;
      oldSmoothShade = smoothShading;
      
      newCullBack = ChangeGeometryFlags.GeometryFlagsPanel.access$000(panel).isSelected();
      newUseLight = ChangeGeometryFlags.GeometryFlagsPanel.access$100(panel).isSelected();
      newSmoothShade = ChangeGeometryFlags.GeometryFlagsPanel.access$200(panel).isSelected();
    }
    

    public boolean shouldExec()
    {
      return (oldCullBack != newCullBack) || (oldUseLight != newUseLight) || (oldSmoothShade != newSmoothShade);
    }
    



    public void exec()
    {
      super.exec();
      cullBack = newCullBack;
      useLighting = newUseLight;
      smoothShading = newSmoothShade;
      ModelInfoPanel.instance().updateDisplayTab(mdl);
    }
    

    public void undo()
    {
      super.undo();
      cullBack = oldCullBack;
      useLighting = oldUseLight;
      smoothShading = oldSmoothShade;
      ModelInfoPanel.instance().updateDisplayTab(mdl);
    }
  }
}
