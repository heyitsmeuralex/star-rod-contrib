package editor.ui.dialogs;

import editor.map.marker.Marker.MarkerType;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import net.miginfocom.swing.MigLayout;


public class MarkerOptionsPanel
  extends JPanel
  implements ActionListener
{
  private static final long serialVersionUID = 1L;
  private static final Marker.MarkerType DEFAULT_TYPE = Marker.MarkerType.Position;
  private Marker.MarkerType markerType = DEFAULT_TYPE;
  
  private final JComboBox<Marker.MarkerType> typeComboBox;
  
  private final JTextField nameField;
  
  private static final String DEFAULT_NAME = "Marker";
  private static MarkerOptionsPanel instance = null;
  
  public static MarkerOptionsPanel getInstance()
  {
    if (instance == null) {
      instance = new MarkerOptionsPanel();
    }
    return instance;
  }
  
  private MarkerOptionsPanel()
  {
    typeComboBox = new JComboBox(Marker.MarkerType.values());
    typeComboBox.removeItem(Marker.MarkerType.Root);
    typeComboBox.setSelectedItem(markerType);
    typeComboBox.setActionCommand("choose_primitive_type");
    typeComboBox.addActionListener(this);
    
    nameField = new JTextField("Marker");
    
    setLayout(new MigLayout("fill, hidemode 3"));
    add(new JLabel("Name"), "w 25%");
    add(nameField, "pushx, growx, wrap");
    add(new JLabel("Type"), "w 25%");
    add(typeComboBox, "pushx, growx, wrap, gapbottom 8");
    
    setType(DEFAULT_TYPE);
  }
  

  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals("choose_primitive_type"))
    {
      setType((Marker.MarkerType)typeComboBox.getSelectedItem());
    }
  }
  






  private void setType(Marker.MarkerType newType)
  {
    markerType = newType;
    






    Window w = SwingUtilities.getWindowAncestor(this);
    if (w != null) w.pack();
  }
  
  public static String getMarkerName()
  {
    String name = instancenameField.getText();
    return name.isEmpty() ? "Marker" : name;
  }
  
  public static Marker.MarkerType getMarkerType()
  {
    return instancemarkerType;
  }
}
