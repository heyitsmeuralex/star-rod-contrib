package editor.ui.elements;

import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JComponent;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import net.miginfocom.swing.MigLayout;

public class LabeledSlider
  extends JComponent
  implements ChangeListener
{
  private static final long serialVersionUID = 1L;
  private JTextField textField;
  private JSlider slider;
  
  public int getValue()
  {
    return slider.getValue();
  }
  
  public void setValue(int value)
  {
    slider.setValue(value);
  }
  
  public void setMaximum(int value)
  {
    slider.setMaximum(value);
  }
  
  public LabeledSlider(int minValue, int maxValue, int initialValue)
  {
    this(minValue, maxValue, initialValue, 3);
  }
  
  public LabeledSlider(int minValue, int maxValue, int initialValue, final int digits)
  {
    slider = new JSlider(minValue, maxValue, initialValue);
    slider.addChangeListener(this);
    slider.setMajorTickSpacing(0);
    slider.setMinorTickSpacing(0);
    slider.setPaintTicks(true);
    
    textField = new JTextField(Integer.toString(initialValue), 3);
    textField.setFont(textField.getFont().deriveFont(12.0F));
    textField.setHorizontalAlignment(0);
    
    final String pattern = minValue < 0 ? "[-\\d]+" : "\\d+";
    
    textField.addKeyListener(new KeyAdapter()
    {

      public void keyReleased(KeyEvent ke)
      {
        String text = textField.getText();
        if (text.isEmpty())
        {
          textField.setText("" + slider.getMinimum());
          slider.setValue(slider.getMinimum());
          return;
        }
        if ((!text.matches(pattern)) || (text.length() > digits)) {
          textField.setText(Integer.toString(slider.getValue()));
          return;
        }
        if (!text.equals("-")) {
          slider.setValue(Integer.parseInt(text));
        }
      }
    });
    setLayout(new MigLayout("insets 0"));
    add(slider);
    add(textField, String.format("w %d!", new Object[] { Integer.valueOf(5 * digits + 25) }));
  }
  
  public void addChangeListener(ChangeListener l)
  {
    slider.addChangeListener(l);
  }
  

  public void stateChanged(ChangeEvent e)
  {
    textField.setText(Integer.toString(slider.getValue()));
  }
}
