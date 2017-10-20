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

public class RadialGridGenerator extends ShapeGenerator
{
  private final LabeledIntegerSpinner innerRadiusSpinner;
  private final LabeledIntegerSpinner outerRadiusSpinner;
  private final LabeledIntegerSpinner rSpinner;
  private final LabeledIntegerSpinner aSpinner;
  private final LabeledIntegerSpinner angleSpinner;
  private final JCheckBox fuseCheckbox;
  private final JCheckBox planarUVCheckbox;
  
  public RadialGridGenerator()
  {
    super(ShapeGenerator.Primitive.RADIAL_GRID);
    
    innerRadiusSpinner = new LabeledIntegerSpinner("Inner Radius", 0, 5000, 100);
    outerRadiusSpinner = new LabeledIntegerSpinner("Outer Radius", 10, 5000, 200);
    rSpinner = new LabeledIntegerSpinner("Radial Segments", 1, 64, 4);
    aSpinner = new LabeledIntegerSpinner("Angular Segments", 3, 64, 12);
    angleSpinner = new LabeledIntegerSpinner("Arc Angle", 1, 360, 360);
    
    fuseCheckbox = new JCheckBox(" Fuse Center Verticies");
    fuseCheckbox.setSelected(false);
    SwingUtils.setFontSize(fuseCheckbox, 12.0F);
    
    planarUVCheckbox = new JCheckBox(" Use Planar UVs");
    planarUVCheckbox.setSelected(false);
    SwingUtils.setFontSize(planarUVCheckbox, 12.0F);
  }
  

  public void addFields(PrimitiveOptionsPanel panel)
  {
    panel.addSpinner(innerRadiusSpinner);
    panel.addSpinner(outerRadiusSpinner);
    panel.addSpinner(rSpinner);
    panel.addSpinner(aSpinner);
    panel.addSpinner(angleSpinner);
    panel.addCheckBox(fuseCheckbox);
    panel.addCheckBox(planarUVCheckbox);
  }
  

  public void setVisible(boolean b)
  {
    innerRadiusSpinner.setVisible(b);
    outerRadiusSpinner.setVisible(b);
    rSpinner.setVisible(b);
    aSpinner.setVisible(b);
    angleSpinner.setVisible(b);
    fuseCheckbox.setVisible(b);
    planarUVCheckbox.setVisible(b);
  }
  

  public TriangleBatch generate(int centerX, int centerY, int centerZ)
  {
    int numR = rSpinner.getValue();
    int numA = aSpinner.getValue();
    int radius1 = innerRadiusSpinner.getValue();
    int radius2 = outerRadiusSpinner.getValue();
    int angle = angleSpinner.getValue();
    boolean fuse = fuseCheckbox.isSelected();
    boolean planarUVs = planarUVCheckbox.isSelected();
    
    return generate(numR, numA, radius1, radius2, angle, fuse, planarUVs, centerX, centerY, centerZ);
  }
  



  private static TriangleBatch generate(int numR, int numA, int radiusInner, int radiusOuter, int angle, boolean fuseCenter, boolean planarUVs, int centerX, int centerY, int centerZ)
  {
    Vertex[][] rings = new Vertex[numR + 1][numA + 1];
    
    double angleInterval = Math.toRadians(angle) / numA;
    double radInterval = (radiusOuter - radiusInner) / numR;
    
    for (int i = 0; i <= numR; i++)
    {
      if ((i == 0) && (radiusInner == 0) && (fuseCenter))
      {
        Vertex center = new Vertex(centerX, centerY, centerZ);
        uv = new UV((int)(1024.0D * (i - numR / 2.0D)), 0);
        

        for (int j = 0; j <= numA; j++) {
          rings[0][j] = center;
        }
      }
      else
      {
        double radius = radiusInner + i * radInterval;
        
        for (int j = 0; j <= numA; j++)
        {
          double X = Math.sin(angleInterval * j);
          double Z = Math.cos(angleInterval * j);
          
          rings[i][j] = new Vertex(centerX + (int)(radius * X), centerY, centerZ - (int)(radius * Z));
          


          uv = new UV((int)(1024.0D * (i - numR / 2.0D)), (int)(1024.0D * (j - numA / 2.0D)));
        }
      }
    }
    

    if (planarUVs)
    {
      for (int i = 0; i <= numR; i++) {
        for (int j = 0; j <= numA; j++)
        {


          uv = new UV(rings[i][j].getCurrentX() * 16, rings[i][j].getCurrentZ() * 16);
        }
      }
    }
    TriangleBatch batch = new TriangleBatch();
    
    for (int i = 0; i < numR; i++) {
      for (int j = 0; j < numA; j++)
      {
        Triangle t1 = new Triangle(rings[(i + 1)][(j + 1)], rings[(i + 1)][j], rings[i][j]);
        Triangle t2 = new Triangle(rings[(i + 1)][(j + 1)], rings[i][j], rings[i][(j + 1)]);
        triangles.add(t1);
        triangles.add(t2);
      }
    }
    return batch;
  }
}
