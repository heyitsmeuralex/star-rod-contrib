package editor.ui.elements;

import editor.ui.SwingGUI;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.AbstractAction;
import javax.swing.JTextField;








public class HexTextField
  extends JTextField
{
  private static final long serialVersionUID = 1L;
  private int value;
  private final String displayFormat;
  
  public HexTextField(HexUpdateListener listener)
  {
    this(8, listener);
  }
  
  public HexTextField(int digits, final HexUpdateListener listener)
  {
    setMargin(SwingGUI.TEXTBOX_INSETS);
    
    displayFormat = ("%0" + digits + "X");
    
    addActionListener(new AbstractAction()
    {
      private static final long serialVersionUID = 1L;
      
      public void actionPerformed(ActionEvent e)
      {
        try {
          int v = (int)Long.parseLong(getText(), 16);
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
            int v = (int)Long.parseLong(getText(), 16);
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
    setText(String.format(displayFormat, new Object[] { Integer.valueOf(v) }));
  }
  
  public static abstract interface HexUpdateListener
  {
    public abstract void handleUpdate(int paramInt);
  }
}
