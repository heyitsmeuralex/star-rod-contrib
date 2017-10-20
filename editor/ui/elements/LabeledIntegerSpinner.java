package editor.ui.elements;

import java.awt.Color;
import java.awt.Font;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;


public class LabeledIntegerSpinner
  extends JComponent
{
  private static final long serialVersionUID = 1L;
  private JSpinner spinner;
  
  public int getValue()
  {
    return ((Integer)spinner.getValue()).intValue();
  }
  
  public void setValue(int val)
  {
    spinner.setValue(Integer.valueOf(val));
  }
  
  public void setMaximum(int val)
  {
    SpinnerNumberModel model = (SpinnerNumberModel)spinner.getModel();
    model.setMaximum(Integer.valueOf(val));
  }
  
  public LabeledIntegerSpinner(String msg, int minValue, int maxValue, int initialValue)
  {
    spinner = new JSpinner();
    spinner.setFont(spinner.getFont().deriveFont(12.0F));
    
    SpinnerModel model = new SpinnerNumberModel(initialValue, minValue, maxValue, 1);
    spinner.setModel(model);
    

    int minimum = minValue;
    
    JLabel lbl = new JLabel(msg);
    lbl.setFont(lbl.getFont().deriveFont(12.0F));
    
    setLayout(new MigLayout("insets 0, fill"));
    add(lbl, "pushx");
    add(spinner, "w 50%");
  }
  
  public LabeledIntegerSpinner(String msg, Color c, int minValue, int maxValue, int initialValue)
  {
    spinner = new JSpinner();
    spinner.setFont(spinner.getFont().deriveFont(12.0F));
    
    SpinnerModel model = new SpinnerNumberModel(initialValue, minValue, maxValue, 1);
    spinner.setModel(model);
    

    int minimum = minValue;
    
    JLabel lbl = new JLabel(msg);
    lbl.setFont(lbl.getFont().deriveFont(12.0F));
    lbl.setForeground(c);
    
    setLayout(new MigLayout("insets 0, fill"));
    add(lbl, "pushx");
    add(spinner, "w 72!");
  }
  
  public void addChangeListener(ChangeListener listener)
  {
    spinner.addChangeListener(listener);
  }
}
