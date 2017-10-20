package editor.ui.elements;

import editor.ui.SwingGUI;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.AbstractAction;
import javax.swing.JTextField;







public class IntTextField
  extends JTextField
{
  private static final long serialVersionUID = 1L;
  private int value;
  
  public IntTextField(final IntUpdateListener listener)
  {
    setMargin(SwingGUI.TEXTBOX_INSETS);
    
    addActionListener(new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e)
      {
        try {
          int v = Integer.parseInt(getText());
          if (v != value)
          {
            value = v;
            listener.handleUpdate(v);
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
            int v = Integer.parseInt(getText());
            if (v != value)
            {
              value = v;
              listener.handleUpdate(v);
            }
          }
          catch (NumberFormatException localNumberFormatException) {}
          setValue(value);
        }
      }
    });
  }
  
  public void setValue(int v)
  {
    value = v;
    setText(v + "");
  }
  
  public static abstract interface IntUpdateListener
  {
    public abstract void handleUpdate(int paramInt);
  }
}
