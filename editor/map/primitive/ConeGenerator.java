package editor.map.primitive;

import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.TriangleBatch;
import editor.map.shape.UV;
import editor.ui.dialogs.PrimitiveOptionsPanel;
import editor.ui.elements.LabeledIntegerSpinner;
import java.util.List;
import javax.swing.JCheckBox;

public class ConeGenerator extends ShapeGenerator
{
  private final LabeledIntegerSpinner radiusSpinner;
  private final LabeledIntegerSpinner heightSpinner;
  private final LabeledIntegerSpinner facesSpinner;
  private final JCheckBox bottomCheckbox;
  
  public ConeGenerator()
  {
    super(ShapeGenerator.Primitive.CONE);
    
    radiusSpinner = new LabeledIntegerSpinner("Radius", 10, 5000, 50);
    heightSpinner = new LabeledIntegerSpinner("Height", 10, 5000, 200);
    facesSpinner = new LabeledIntegerSpinner("Faces", 3, 64, 12);
    bottomCheckbox = new JCheckBox(" Create Bottom Cap");
    bottomCheckbox.setSelected(false);
    bottomCheckbox.setFont(bottomCheckbox.getFont().deriveFont(12.0F));
  }
  

  public void addFields(PrimitiveOptionsPanel panel)
  {
    panel.addSpinner(radiusSpinner);
    panel.addSpinner(heightSpinner);
    panel.addSpinner(facesSpinner);
    panel.addCheckBox(bottomCheckbox);
  }
  

  public void setVisible(boolean b)
  {
    radiusSpinner.setVisible(b);
    heightSpinner.setVisible(b);
    facesSpinner.setVisible(b);
    bottomCheckbox.setVisible(b);
  }
  

  public TriangleBatch generate(int centerX, int centerY, int centerZ)
  {
    int radius = radiusSpinner.getValue();
    int height = heightSpinner.getValue();
    int faces = facesSpinner.getValue();
    boolean includeCap = bottomCheckbox.isSelected();
    centerY += heightSpinner.getValue() / 2;
    
    return generate(radius, height, faces, includeCap, centerX, centerY, centerZ);
  }
  
  private static TriangleBatch generate(int radius, int height, int faces, boolean includeCap, int centerX, int centerY, int centerZ)
  {
    Vertex[] caps = new Vertex[faces + 1];
    Vertex[] capCenter = new Vertex[2];
    
    for (int i = 0; i <= faces; i++)
    {



      caps[i] = new Vertex(centerX + (int)(radius * Math.sin(6.283185307179586D * i / faces)), centerY - (int)(0.5D * height), centerZ - (int)(radius * Math.cos(6.283185307179586D * i / faces)));
      

      uv = new UV((int)(512.0D * (Math.sin(6.283185307179586D * i / faces) + 1.0D)), (int)(512.0D * (Math.cos(6.283185307179586D * i / faces) + 1.0D)));
    }
    
    capCenter[0] = new Vertex(centerX, centerY - (int)(0.5D * height), centerZ);
    0uv = new UV(512, 512);
    capCenter[1] = new Vertex(centerX, centerY + (int)(0.5D * height), centerZ);
    1uv = new UV(512, 512);
    
    TriangleBatch batch = new TriangleBatch();
    

    if (includeCap)
    {
      for (int i = 0; i < faces; i++) {
        triangles.add(new Triangle(capCenter[0], caps[i], caps[(i + 1)]));
      }
    }
    
    for (int i = 0; i < faces; i++) {
      triangles.add(new Triangle(capCenter[1], caps[(i + 1)], caps[i]));
    }
    return batch;
  }
}
