package editor.ui.dialogs;

import editor.map.Axis;
import editor.map.BoundingBox;
import editor.map.MutablePoint;
import editor.map.shape.TransformMatrix;
import editor.selection.Selection;
import editor.ui.elements.LabeledDoubleSpinner;
import editor.ui.elements.LabeledIntegerSpinner;
import java.awt.Color;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import org.lwjgl.util.vector.Vector3f;

public class TransformSelectionPanel extends JPanel implements ActionListener
{
  private static final long serialVersionUID = 1L;
  
  public static enum TransformType
  {
    Translate,  Rotate,  Scale,  Resize,  Flip;
    private TransformType() {} } private static final TransformType DEFAULT_TYPE = TransformType.Scale;
  
  private TransformType selectedType = TransformType.Scale;
  
  private final JComboBox<TransformType> typeComboBox;
  
  private final LabeledIntegerSpinner translateSpinnerX;
  
  private final LabeledIntegerSpinner translateSpinnerY;
  
  private final LabeledIntegerSpinner translateSpinnerZ;
  
  private final JLabel rotateAxisLabel;
  
  private final JComboBox<Axis> rotateAxisBox;
  private final LabeledDoubleSpinner rotateAngleSpinner;
  private final LabeledDoubleSpinner scaleSpinnerX;
  private final LabeledDoubleSpinner scaleSpinnerY;
  private final LabeledDoubleSpinner scaleSpinnerZ;
  private final LabeledIntegerSpinner resizeSpinnerX;
  private final LabeledIntegerSpinner resizeSpinnerY;
  private final LabeledIntegerSpinner resizeSpinnerZ;
  private final JCheckBox flipCheckBoxX;
  private final JCheckBox flipCheckBoxY;
  private final JCheckBox flipCheckBoxZ;
  private int selectionSizeX;
  private int selectionSizeY;
  private int selectionSizeZ;
  private Vector3f transformOrigin = null;
  
  private Color RED = new Color(192, 0, 0);
  private Color GREEN = new Color(0, 160, 0);
  private Color BLUE = new Color(0, 0, 192);
  
  public TransformSelectionPanel()
  {
    typeComboBox = new JComboBox(TransformType.values());
    typeComboBox.setSelectedItem(selectedType);
    typeComboBox.setActionCommand("choose_transform_type");
    typeComboBox.addActionListener(this);
    

    translateSpinnerX = new LabeledIntegerSpinner("X", RED, 61440, 4096, 0);
    translateSpinnerY = new LabeledIntegerSpinner("Y", GREEN, 61440, 4096, 0);
    translateSpinnerZ = new LabeledIntegerSpinner("Z", BLUE, 61440, 4096, 0);
    
    rotateAxisLabel = new JLabel("Axis");
    rotateAxisLabel.setFont(rotateAxisLabel.getFont().deriveFont(12.0F));
    rotateAxisBox = new JComboBox(Axis.values());
    rotateAxisBox.setSelectedItem(Axis.Y);
    rotateAxisBox.setActionCommand("choose_rotation_axis");
    rotateAxisBox.addActionListener(this);
    rotateAngleSpinner = new LabeledDoubleSpinner("Angle", -180.0D, 180.0D, 0.0D, 0.001D);
    
    scaleSpinnerX = new LabeledDoubleSpinner("X", RED, 0.0D, 8192.0D, 1.0D, 0.001D);
    scaleSpinnerY = new LabeledDoubleSpinner("Y", GREEN, 0.0D, 8192.0D, 1.0D, 0.001D);
    scaleSpinnerZ = new LabeledDoubleSpinner("Z", BLUE, 0.0D, 8192.0D, 1.0D, 0.001D);
    
    resizeSpinnerX = new LabeledIntegerSpinner("X", RED, 0, 65535, 0);
    resizeSpinnerY = new LabeledIntegerSpinner("Y", GREEN, 0, 65535, 0);
    resizeSpinnerZ = new LabeledIntegerSpinner("Z", BLUE, 0, 65535, 0);
    
    flipCheckBoxX = new JCheckBox("Mirror X");
    flipCheckBoxY = new JCheckBox("Mirror Y");
    flipCheckBoxZ = new JCheckBox("Mirror Z");
    
    setLayout(new MigLayout("fill, hidemode 3"));
    add(typeComboBox, "growx, wrap, span, gapbottom 8");
    
    add(translateSpinnerX, "span, growx, wrap");
    add(translateSpinnerY, "span, growx, wrap");
    add(translateSpinnerZ, "span, growx, wrap");
    
    add(rotateAxisLabel, "pushx");
    add(rotateAxisBox, "w 72!, wrap");
    add(rotateAngleSpinner, "span, growx, wrap");
    
    add(scaleSpinnerX, "span, growx, wrap");
    add(scaleSpinnerY, "span, growx, wrap");
    add(scaleSpinnerZ, "span, growx, wrap");
    
    add(resizeSpinnerX, "span, growx, wrap");
    add(resizeSpinnerY, "span, growx, wrap");
    add(resizeSpinnerZ, "span, growx, wrap");
    
    add(flipCheckBoxX, "span, growx, wrap");
    add(flipCheckBoxY, "span, growx, wrap");
    add(flipCheckBoxZ, "span, growx, wrap");
    
    translateSpinnerX.setVisible(false);
    translateSpinnerY.setVisible(false);
    translateSpinnerZ.setVisible(false);
    
    rotateAxisLabel.setVisible(false);
    rotateAxisBox.setVisible(false);
    rotateAngleSpinner.setVisible(false);
    
    scaleSpinnerX.setVisible(false);
    scaleSpinnerY.setVisible(false);
    scaleSpinnerZ.setVisible(false);
    
    resizeSpinnerX.setVisible(false);
    resizeSpinnerY.setVisible(false);
    resizeSpinnerZ.setVisible(false);
    
    flipCheckBoxX.setVisible(false);
    flipCheckBoxY.setVisible(false);
    flipCheckBoxZ.setVisible(false);
    
    setType(DEFAULT_TYPE);
  }
  

  public void actionPerformed(ActionEvent e)
  {
    if (e.getActionCommand().equals("choose_transform_type"))
    {
      setType((TransformType)typeComboBox.getSelectedItem());
    }
  }
  

  public void setTransformType(TransformType newType)
  {
    typeComboBox.setSelectedItem(newType);
  }
  
  public void setSelection(Selection<?> selection)
  {
    transformOrigin = selection.getCenter();
    
    selectionSizeX = (aabb.max.getX() - aabb.min.getX());
    selectionSizeY = (aabb.max.getY() - aabb.min.getY());
    selectionSizeZ = (aabb.max.getZ() - aabb.min.getZ());
    
    resizeSpinnerX.setValue(selectionSizeX);
    resizeSpinnerY.setValue(selectionSizeY);
    resizeSpinnerZ.setValue(selectionSizeZ);
  }
  
  private void setType(TransformType newType)
  {
    switch (1.$SwitchMap$editor$ui$dialogs$TransformSelectionPanel$TransformType[selectedType.ordinal()])
    {
    case 1: 
      translateSpinnerX.setVisible(false);
      translateSpinnerY.setVisible(false);
      translateSpinnerZ.setVisible(false);
      break;
    case 2: 
      rotateAxisLabel.setVisible(false);
      rotateAxisBox.setVisible(false);
      rotateAngleSpinner.setVisible(false);
      break;
    case 3: 
      scaleSpinnerX.setVisible(false);
      scaleSpinnerY.setVisible(false);
      scaleSpinnerZ.setVisible(false);
      break;
    case 4: 
      resizeSpinnerX.setVisible(false);
      resizeSpinnerY.setVisible(false);
      resizeSpinnerZ.setVisible(false);
      break;
    case 5: 
      flipCheckBoxX.setVisible(false);
      flipCheckBoxY.setVisible(false);
      flipCheckBoxZ.setVisible(false);
    }
    
    
    selectedType = newType;
    
    switch (1.$SwitchMap$editor$ui$dialogs$TransformSelectionPanel$TransformType[selectedType.ordinal()])
    {
    case 1: 
      translateSpinnerX.setVisible(true);
      translateSpinnerY.setVisible(true);
      translateSpinnerZ.setVisible(true);
      break;
    case 2: 
      rotateAxisLabel.setVisible(true);
      rotateAxisBox.setVisible(true);
      rotateAngleSpinner.setVisible(true);
      break;
    case 3: 
      scaleSpinnerX.setVisible(true);
      scaleSpinnerY.setVisible(true);
      scaleSpinnerZ.setVisible(true);
      break;
    case 4: 
      resizeSpinnerX.setVisible(true);
      resizeSpinnerY.setVisible(true);
      resizeSpinnerZ.setVisible(true);
      break;
    case 5: 
      flipCheckBoxX.setVisible(true);
      flipCheckBoxY.setVisible(true);
      flipCheckBoxZ.setVisible(true);
    }
    
    
    Window w = javax.swing.SwingUtilities.getWindowAncestor(this);
    if (w != null) { w.pack();
    }
  }
  
  public TransformMatrix createTransformMatrix()
  {
    TransformMatrix m = new TransformMatrix();
    

    switch (1.$SwitchMap$editor$ui$dialogs$TransformSelectionPanel$TransformType[selectedType.ordinal()])
    {
    case 1: 
      if ((translateSpinnerX.getValue() == 0) && 
        (translateSpinnerY.getValue() == 0) && 
        (translateSpinnerZ.getValue() == 0)) {
        return null;
      }
      m.setTranslation(translateSpinnerX.getValue(), translateSpinnerY.getValue(), translateSpinnerZ.getValue());
      break;
    
    case 2: 
      if (rotateAngleSpinner.getValue() == 0.0D) {
        return null;
      }
      TransformMatrix r = new TransformMatrix();
      r.setRotation((Axis)rotateAxisBox.getSelectedItem(), rotateAngleSpinner.getValue());
      
      m.setTranslation(-transformOrigin.x, -transformOrigin.y, -transformOrigin.z);
      m.concat(r);
      m.translate(transformOrigin);
      break;
    
    case 3: 
      if ((scaleSpinnerX.getValue() == 1.0D) && 
        (scaleSpinnerY.getValue() == 1.0D) && 
        (scaleSpinnerZ.getValue() == 1.0D)) {
        return null;
      }
      double sx = scaleSpinnerX.getValue();
      double sy = scaleSpinnerY.getValue();
      double sz = scaleSpinnerZ.getValue();
      
      TransformMatrix scale = new TransformMatrix();
      scale.setScale(sx, sy, sz);
      
      m.setTranslation(-transformOrigin.x, -transformOrigin.y, -transformOrigin.z);
      m.concat(scale);
      m.translate(transformOrigin);
      break;
    
    case 4: 
      if ((resizeSpinnerX.getValue() == selectionSizeX) && 
        (resizeSpinnerY.getValue() == selectionSizeY) && 
        (resizeSpinnerZ.getValue() == selectionSizeZ)) {
        return null;
      }
      double sx = selectionSizeX == 0 ? 1.0D : resizeSpinnerX.getValue() / selectionSizeX;
      double sy = selectionSizeY == 0 ? 1.0D : resizeSpinnerY.getValue() / selectionSizeY;
      double sz = selectionSizeZ == 0 ? 1.0D : resizeSpinnerZ.getValue() / selectionSizeZ;
      
      TransformMatrix rescale = new TransformMatrix();
      rescale.setScale(sx, sy, sz);
      
      m.setTranslation(-transformOrigin.x, -transformOrigin.y, -transformOrigin.z);
      m.concat(rescale);
      m.translate(transformOrigin);
      break;
    
    case 5: 
      if ((!flipCheckBoxX.isSelected()) && 
        (!flipCheckBoxY.isSelected()) && 
        (!flipCheckBoxZ.isSelected())) {
        return null;
      }
      double sx = flipCheckBoxX.isSelected() ? -1.0D : 1.0D;
      double sy = flipCheckBoxY.isSelected() ? -1.0D : 1.0D;
      double sz = flipCheckBoxZ.isSelected() ? -1.0D : 1.0D;
      
      TransformMatrix flip = new TransformMatrix();
      flip.setScale(sx, sy, sz);
      
      m.setTranslation(-transformOrigin.x, -transformOrigin.y, -transformOrigin.z);
      m.concat(flip);
      m.translate(transformOrigin);
    }
    
    
    return m;
  }
}
