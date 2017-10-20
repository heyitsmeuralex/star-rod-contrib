package editor.ui;

import java.io.PrintStream;
import javax.swing.JLabel;
import javax.swing.JPanel;
import main.config.Config;
import net.miginfocom.swing.MigLayout;

public class PreferencesPanel extends JPanel
{
  private static final long serialVersionUID = 1L;
  
  public PreferencesPanel()
  {
    setLayout(new MigLayout("fill"));
    
    add(new JLabel("Coming soon..."));
  }
  










  public void useConfigValues(Config cfg) {}
  










  public void setConfigValues(Config cfg)
  {
    System.out.println("Setting config values...");
  }
}
