package main.config;

import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;




public class DumpOptionsPanel
  extends JPanel
{
  private static final long serialVersionUID = 1L;
  private final List<ConfigCheckBox> optCheckBoxes;
  
  public void setValues(Config cfg)
  {
    for (ConfigCheckBox cb : optCheckBoxes)
    {
      boolean b = cfg.getBoolean(opt);
      cb.setSelected(b);
    }
  }
  
  public void getValues(Config cfg)
  {
    for (ConfigCheckBox cb : optCheckBoxes)
    {
      boolean b = cb.isSelected();
      cfg.setBoolean(opt, b);
    }
  }
  

  public DumpOptionsPanel()
  {
    optCheckBoxes = new ArrayList();
    
    setLayout(new MigLayout("wrap 3", "[fill]8[fill,grow]"));
    
    ConfigCheckBox cb = new ConfigCheckBox(Options.CleanDump);
    optCheckBoxes.add(cb);
    add(cb, "wrap");
    
    cb = new ConfigCheckBox(Options.DumpReports);
    optCheckBoxes.add(cb);
    add(cb, "wrap");
    
    add(new JLabel("Select assets to dump:"), "wrap");
    
    JPanel checkBoxPanel = new JPanel();
    checkBoxPanel.setLayout(new MigLayout("wrap 3", "[fill]8[fill,grow]"));
    checkBoxPanel.setBorder(BorderFactory.createEtchedBorder(0));
    
    cb = new ConfigCheckBox(Options.DumpMaps);
    optCheckBoxes.add(cb);
    checkBoxPanel.add(cb, "sg checkbox");
    
    cb = new ConfigCheckBox(Options.DumpBattles);
    optCheckBoxes.add(cb);
    checkBoxPanel.add(cb, "sg checkbox");
    
    cb = new ConfigCheckBox(Options.DumpMoves);
    optCheckBoxes.add(cb);
    checkBoxPanel.add(cb, "sg checkbox");
    
    cb = new ConfigCheckBox(Options.DumpTextures);
    optCheckBoxes.add(cb);
    checkBoxPanel.add(cb, "sg checkbox");
    
    cb = new ConfigCheckBox(Options.DumpIcons);
    optCheckBoxes.add(cb);
    checkBoxPanel.add(cb, "sg checkbox");
    
    cb = new ConfigCheckBox(Options.DumpSprites);
    optCheckBoxes.add(cb);
    checkBoxPanel.add(cb, "sg checkbox");
    
    cb = new ConfigCheckBox(Options.DumpStrings);
    optCheckBoxes.add(cb);
    checkBoxPanel.add(cb, "sg checkbox");
    
    cb = new ConfigCheckBox(Options.DumpTables);
    optCheckBoxes.add(cb);
    checkBoxPanel.add(cb, "sg checkbox");
    
    add(checkBoxPanel, "wrap");
    
    cb = new ConfigCheckBox(Options.UseTabIndents);
    optCheckBoxes.add(cb);
    add(cb, "wrap");
    
    cb = new ConfigCheckBox(Options.PrintLineOffsets);
    optCheckBoxes.add(cb);
    add(cb, "wrap");
  }
}
