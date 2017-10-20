package editor.ui.elements;

import editor.ui.SwingGUI;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.JTextField;


public class FloatTextField
  extends JTextField
{
  private static final long serialVersionUID = 1L;
  private float value;
  
  public FloatTextField(final Consumer<Float> listener)
  {
    setMargin(SwingGUI.TEXTBOX_INSETS);
    
    addActionListener(new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e)
      {
        try {
          float f = Float.parseFloat(getText());
          if (f != value)
          {
            value = f;
            listener.accept(Float.valueOf(f));
          }
        }
        catch (NumberFormatException localNumberFormatException) {}
        setValue(value);
      }
      
    });
    addFocusListener(new FocusListener()
    {
      public void focusGained(FocusEvent e) {}
      
      public void focusLost(FocusEvent e) {
        if (!e.isTemporary())
        {
          try
          {
            float f = Float.parseFloat(getText());
            if (f != value)
            {
              value = f;
              listener.accept(Float.valueOf(f));
            }
          }
          catch (NumberFormatException localNumberFormatException) {}
          setValue(value);
        }
      }
    });
  }
  
  public void setValue(float f)
  {
    value = f;
    setText(f + "");
  }
}
