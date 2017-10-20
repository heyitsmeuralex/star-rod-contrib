package editor.map.primitive;

import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.TriangleBatch;
import editor.map.shape.UV;
import editor.ui.elements.LabeledIntegerSpinner;
import java.util.List;

public class RingGenerator extends ShapeGenerator
{
  private final LabeledIntegerSpinner innerRadiusSpinner;
  private final LabeledIntegerSpinner outerRadiusSpinner;
  private final LabeledIntegerSpinner divisionsSpinner;
  private final LabeledIntegerSpinner angleSpinner;
  private final LabeledIntegerSpinner thicknessSpinner;
  
  public RingGenerator()
  {
    super(ShapeGenerator.Primitive.RING);
    
    innerRadiusSpinner = new LabeledIntegerSpinner("Inner Radius", 0, 5000, 100);
    outerRadiusSpinner = new LabeledIntegerSpinner("Outer Radius", 10, 5000, 200);
    divisionsSpinner = new LabeledIntegerSpinner("Divisions", 3, 64, 12);
    angleSpinner = new LabeledIntegerSpinner("Arc Angle", 1, 360, 360);
    thicknessSpinner = new LabeledIntegerSpinner("Thickness", 0, 1000, 20);
  }
  

  public void addFields(editor.ui.dialogs.PrimitiveOptionsPanel panel)
  {
    panel.addSpinner(innerRadiusSpinner);
    panel.addSpinner(outerRadiusSpinner);
    panel.addSpinner(divisionsSpinner);
    panel.addSpinner(angleSpinner);
    panel.addSpinner(thicknessSpinner);
  }
  

  public void setVisible(boolean b)
  {
    innerRadiusSpinner.setVisible(b);
    outerRadiusSpinner.setVisible(b);
    divisionsSpinner.setVisible(b);
    angleSpinner.setVisible(b);
    thicknessSpinner.setVisible(b);
  }
  

  public TriangleBatch generate(int centerX, int centerY, int centerZ)
  {
    int innerRadius = innerRadiusSpinner.getValue();
    int outerRadius = outerRadiusSpinner.getValue();
    int divisions = divisionsSpinner.getValue();
    int angle = angleSpinner.getValue();
    int thickness = thicknessSpinner.getValue();
    
    return generate(divisions, innerRadius, outerRadius, angle, thickness, centerX, centerY, centerZ);
  }
  


  private static TriangleBatch generate(int divs, int innerR, int outerR, int angle, int thickness, int centerX, int centerY, int centerZ)
  {
    Vertex[][] rings = new Vertex[divs + 1][5];
    
    double angleRad = Math.toRadians(angle);
    
    for (int i = 0; i <= divs; i++)
    {
      double X = Math.sin(angleRad * i / divs);
      double Z = Math.cos(angleRad * i / divs);
      
      int inX = (int)(innerR * X);
      int inZ = (int)(innerR * Z);
      
      int outX = (int)(outerR * X);
      int outZ = (int)(outerR * Z);
      
      rings[i][0] = new Vertex(centerX + inX, centerY + thickness, centerZ + inZ);
      rings[i][1] = new Vertex(centerX + outX, centerY + thickness, centerZ + outZ);
      rings[i][2] = new Vertex(centerX + outX, centerY, centerZ + outZ);
      rings[i][3] = new Vertex(centerX + inX, centerY, centerZ + inZ);
      rings[i][4] = new Vertex(centerX + inX, centerY + thickness, centerZ + inZ);
      
      int u = (int)(1024.0D * (i - divs / 2.0D));
      for (int j = 0; j < 5; j++) {
        uv = new UV(u, 1024 * (j - 2));
      }
    }
    TriangleBatch batch = new TriangleBatch();
    
    for (int i = 0; i < divs; i++)
    {
      triangles.add(new Triangle(rings[i][0], rings[i][1], rings[(i + 1)][0]));
      triangles.add(new Triangle(rings[(i + 1)][0], rings[i][1], rings[(i + 1)][1]));
      
      if (thickness != 0)
      {
        for (int j = 1; j < 4; j++)
        {
          triangles.add(new Triangle(rings[i][j], rings[i][(j + 1)], rings[(i + 1)][j]));
          triangles.add(new Triangle(rings[(i + 1)][(j + 1)], rings[(i + 1)][j], rings[i][(j + 1)]));
        }
      }
    }
    
    if ((thickness != 0) && (angle != 360))
    {
      Vertex[][] caps = new Vertex[4][2];
      
      for (int k = 0; k < 4; k++)
      {
        caps[k][0] = rings[0][k].deepCopy();
        0uv = new UV(1024 * (k % 2), 0);
      }
      
      int u1 = (int)(1024.0D * (-1.0D - divs / 2.0D));
      int u2 = (int)(1024.0D * (-2.0D - divs / 2.0D));
      00uv = new UV(u1, 0);
      10uv = new UV(u1, 1024);
      20uv = new UV(u2, 1024);
      30uv = new UV(u2, 0);
      
      for (int k = 0; k < 4; k++)
      {
        caps[k][1] = rings[divs][k].deepCopy();
        1uv = new UV(1024 * (k % 2), 0);
      }
      
      u1 = (int)(1024.0D * (1.0D + divs / 2.0D));
      u2 = (int)(1024.0D * (2.0D + divs / 2.0D));
      01uv = new UV(u1, 0);
      11uv = new UV(u1, 1024);
      21uv = new UV(u2, 1024);
      31uv = new UV(u2, 0);
      
      triangles.add(new Triangle(caps[1][0], caps[0][0], caps[2][0]));
      triangles.add(new Triangle(caps[3][0], caps[2][0], caps[0][0]));
      
      triangles.add(new Triangle(caps[0][1], caps[1][1], caps[2][1]));
      triangles.add(new Triangle(caps[2][1], caps[3][1], caps[0][1]));
    }
    
    return batch;
  }
}
