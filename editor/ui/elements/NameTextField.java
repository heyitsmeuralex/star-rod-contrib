package editor.ui.elements;

import editor.ui.SwingGUI;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.AbstractAction;
import javax.swing.JTextField;







public class NameTextField
  extends JTextField
{
  private static final long serialVersionUID = 1L;
  private String value;
  
  public NameTextField(final NameUpdateListener listener)
  {
    setMargin(SwingGUI.TEXTBOX_INSETS);
    
    addActionListener(new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e)
      {
        try {
          String s = getText();
          if ((!s.isEmpty()) && (!s.equals(value)))
          {
            value = s;
            listener.handleUpdate(s);
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
            String s = getText();
            if ((!s.isEmpty()) && (!s.equals(value)))
            {
              value = s;
              listener.handleUpdate(s);
            }
          }
          catch (NumberFormatException localNumberFormatException) {}
          setValue(value);
        }
      }
    });
  }
  
  public void setValue(String s)
  {
    value = s;
    setText(s);
  }
  
  public static abstract interface NameUpdateListener
  {
    public abstract void handleUpdate(String paramString);
  }
}
