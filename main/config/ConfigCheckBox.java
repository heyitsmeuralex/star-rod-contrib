package main.config;

import javax.swing.JCheckBox;

public class ConfigCheckBox extends JCheckBox
{
  private static final long serialVersionUID = 1L;
  public final Options opt;
  
  public ConfigCheckBox(Options opt)
  {
    super(checkBoxLabel);
    
    if (type != Options.Type.Boolean) {
      throw new RuntimeException("ConfigCheckBox is not compatible with option: " + opt);
    }
    this.opt = opt;
    
    if (!checkBoxDesc.isEmpty()) {
      super.setToolTipText(checkBoxDesc);
    }
  }
}
