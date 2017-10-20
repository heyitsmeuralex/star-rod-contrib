package editor.ui.elements;

import java.awt.Color;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import net.miginfocom.swing.MigLayout;
import shared.SwingUtils;



public class LabeledDoubleSpinner
  extends JComponent
{
  private static final long serialVersionUID = 1L;
  private JSpinner spinner;
  
  public double getValue()
  {
    return ((Double)spinner.getValue()).doubleValue();
  }
  
  public void setValue(double val)
  {
    spinner.setValue(Double.valueOf(val));
  }
  
  public LabeledDoubleSpinner(String msg, double minValue, double maxValue, double initialValue, double step)
  {
    spinner = new JSpinner();
    SwingUtils.setFontSize(spinner, 12.0F);
    
    SpinnerModel model = new SpinnerNumberModel(initialValue, minValue, maxValue, step);
    spinner.setModel(model);
    
    JLabel lbl = new JLabel(msg);
    SwingUtils.setFontSize(lbl, 12.0F);
    
    setLayout(new MigLayout("insets 0, fill"));
    add(lbl, "pushx");
    add(spinner, "w 72!");
  }
  
  public LabeledDoubleSpinner(String msg, Color c, double minValue, double maxValue, double initialValue, double step)
  {
    spinner = new JSpinner();
    SwingUtils.setFontSize(spinner, 12.0F);
    
    SpinnerModel model = new SpinnerNumberModel(initialValue, minValue, maxValue, step);
    spinner.setModel(model);
    
    JLabel lbl = new JLabel(msg);
    SwingUtils.setFontSize(lbl, 12.0F);
    lbl.setForeground(c);
    
    setLayout(new MigLayout("insets 0, fill"));
    add(lbl, "pushx");
    add(spinner, "w 72!");
  }
}
