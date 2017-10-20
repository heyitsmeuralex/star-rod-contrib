package editor.map.primitive;

import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.TriangleBatch;
import editor.map.shape.UV;
import editor.ui.dialogs.PrimitiveOptionsPanel;
import editor.ui.elements.LabeledIntegerSpinner;
import java.util.List;
import javax.swing.JCheckBox;
import shared.SwingUtils;

public class CylinderGenerator extends ShapeGenerator
{
  private final LabeledIntegerSpinner radiusSpinner;
  private final LabeledIntegerSpinner heightSpinner;
  private final LabeledIntegerSpinner facesSpinner;
  private final JCheckBox topCheckbox;
  private final JCheckBox bottomCheckbox;
  
  public CylinderGenerator()
  {
    super(ShapeGenerator.Primitive.CYLINDER);
    
    radiusSpinner = new LabeledIntegerSpinner("Radius", 10, 5000, 50);
    heightSpinner = new LabeledIntegerSpinner("Height", 10, 5000, 200);
    facesSpinner = new LabeledIntegerSpinner("Faces", 3, 64, 12);
    topCheckbox = new JCheckBox(" Create Top Cap");
    topCheckbox.setSelected(false);
    SwingUtils.setFontSize(topCheckbox, 12.0F);
    bottomCheckbox = new JCheckBox(" Create Bottom Cap");
    bottomCheckbox.setSelected(false);
    SwingUtils.setFontSize(bottomCheckbox, 12.0F);
  }
  

  public void addFields(PrimitiveOptionsPanel panel)
  {
    panel.addSpinner(radiusSpinner);
    panel.addSpinner(heightSpinner);
    panel.addSpinner(facesSpinner);
    panel.addCheckBox(topCheckbox);
    panel.addCheckBox(bottomCheckbox);
  }
  

  public void setVisible(boolean b)
  {
    radiusSpinner.setVisible(b);
    heightSpinner.setVisible(b);
    facesSpinner.setVisible(b);
    topCheckbox.setVisible(b);
    bottomCheckbox.setVisible(b);
  }
  

  public TriangleBatch generate(int centerX, int centerY, int centerZ)
  {
    int radius = radiusSpinner.getValue();
    int height = heightSpinner.getValue();
    int faces = facesSpinner.getValue();
    boolean includeTop = topCheckbox.isSelected();
    boolean includeBottom = bottomCheckbox.isSelected();
    centerY += heightSpinner.getValue() / 2;
    
    return generate(radius, height, faces, includeTop, includeBottom, centerX, centerY, centerZ);
  }
  



  private static TriangleBatch generate(int radius, int height, int faces, boolean includeTop, boolean includeBottom, int centerX, int centerY, int centerZ)
  {
    Vertex[][] side = new Vertex[faces + 1][2];
    Vertex[][] caps = new Vertex[faces + 1][2];
    Vertex[] capCenter = new Vertex[2];
    
    for (int i = 0; i <= faces; i++) {
      for (int k = 0; k < 2; k++)
      {



        side[i][k] = new Vertex(centerX + (int)(radius * Math.sin(6.283185307179586D * i / faces)), centerY + (int)((k - 0.5D) * height), centerZ - (int)(radius * Math.cos(6.283185307179586D * i / faces)));
        uv = new UV(4 * (i * 1024 / faces), k * 1024);
      }
    }
    for (int i = 0; i <= faces; i++) {
      for (int k = 0; k < 2; k++)
      {



        caps[i][k] = new Vertex(centerX + (int)(radius * Math.sin(6.283185307179586D * i / faces)), centerY + (int)((k - 0.5D) * height), centerZ - (int)(radius * Math.cos(6.283185307179586D * i / faces)));
        

        uv = new UV((int)(512.0D * (Math.sin(6.283185307179586D * i / faces) + 1.0D)), (int)(512.0D * (Math.cos(6.283185307179586D * i / faces) + 1.0D)) + 3 * (2 * k - 1) * 1024 / 2);
      }
    }
    capCenter[0] = new Vertex(centerX, centerY - (int)(0.5D * height), centerZ);
    0uv = new UV(512, 64512);
    capCenter[1] = new Vertex(centerX, centerY + (int)(0.5D * height), centerZ);
    1uv = new UV(512, 2048);
    
    TriangleBatch batch = new TriangleBatch();
    

    for (int i = 0; i < faces; i++)
    {
      triangles.add(new Triangle(side[i][0], side[i][1], side[(i + 1)][0]));
      triangles.add(new Triangle(side[(i + 1)][0], side[i][1], side[(i + 1)][1]));
    }
    
    if (includeBottom)
    {
      for (int i = 0; i < faces; i++) {
        triangles.add(new Triangle(capCenter[0], caps[i][0], caps[(i + 1)][0]));
      }
    }
    if (includeTop)
    {
      for (int i = 0; i < faces; i++) {
        triangles.add(new Triangle(capCenter[1], caps[(i + 1)][1], caps[i][1]));
      }
    }
    return batch;
  }
}
