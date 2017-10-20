package data.sprites.editor;

import java.text.ParseException;
import javax.swing.JFormattedTextField;
import javax.swing.JSpinner;
import javax.swing.JSpinner.DefaultEditor;
import javax.swing.SpinnerNumberModel;
import javax.swing.text.DefaultFormatter;
import javax.swing.text.DefaultFormatterFactory;
import shared.SwingUtils;

public class HexSpinner
  extends JSpinner
{
  private static final long serialVersionUID = 1L;
  
  public HexSpinner(int minValue, int maxValue, int initialValue)
  {
    SwingUtils.setFontSize(this, 12.0F);
    setModel(new SpinnerNumberModel(initialValue, minValue, maxValue, 1));
    
    JSpinner.DefaultEditor editor = (JSpinner.DefaultEditor)getEditor();
    JFormattedTextField tf = editor.getTextField();
    
    DefaultFormatterFactory ff = (DefaultFormatterFactory)tf.getFormatterFactory();
    ff.setDefaultFormatter(new HexFormatter(null));
    
    setValue(Integer.valueOf(initialValue));
  }
  
  private static class HexFormatter extends DefaultFormatter {
    private static final long serialVersionUID = 1L;
    
    private HexFormatter() {}
    
    public Object stringToValue(String text) throws ParseException {
      try {
        return Integer.valueOf((int)Long.parseLong(text, 16));
      } catch (NumberFormatException nfe) {
        throw new ParseException(text, 0);
      }
    }
    
    public String valueToString(Object value) throws ParseException
    {
      return 
        Long.toHexString(((Integer)value).intValue()).toUpperCase();
    }
  }
  
  public void setMaximum(int val)
  {
    SpinnerNumberModel model = (SpinnerNumberModel)getModel();
    model.setMaximum(Integer.valueOf(val));
  }
  

  public Integer getValue()
  {
    return (Integer)super.getValue();
  }
}
