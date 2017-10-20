package main.config;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;


public class BuildOptionsPanel
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
  

  public BuildOptionsPanel()
  {
    optCheckBoxes = new ArrayList();
    
    setLayout(new MigLayout("wrap 3", "[fill]8[fill,grow]"));
    
    ConfigCheckBox cb = new ConfigCheckBox(Options.CompressBattleData);
    optCheckBoxes.add(cb);
    add(cb, "wrap");
    
    cb = new ConfigCheckBox(Options.BuildTextures);
    optCheckBoxes.add(cb);
    add(cb, "wrap");
    
    cb = new ConfigCheckBox(Options.BuildBackgrounds);
    optCheckBoxes.add(cb);
    add(cb, "wrap");
    
    cb = new ConfigCheckBox(Options.BuildSpriteSheets);
    optCheckBoxes.add(cb);
    add(cb, "wrap");
    
    cb = new ConfigCheckBox(Options.ClearJapaneseStrings);
    optCheckBoxes.add(cb);
    add(cb, "wrap");
    
    cb = new ConfigCheckBox(Options.EnableDebugCode);
    optCheckBoxes.add(cb);
    add(cb, "wrap");
    
    cb = new ConfigCheckBox(Options.SkipIntroLogos);
    optCheckBoxes.add(cb);
    add(cb, "wrap");
    
    cb = new ConfigCheckBox(Options.DisableDemoReel);
    optCheckBoxes.add(cb);
    add(cb, "wrap");
    
    cb = new ConfigCheckBox(Options.CompressModPackage);
    optCheckBoxes.add(cb);
    add(cb, "wrap");
  }
}
