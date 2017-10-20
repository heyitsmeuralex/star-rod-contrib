package editor.map.primitive;

import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.TriangleBatch;
import editor.map.shape.UV;
import editor.ui.dialogs.PrimitiveOptionsPanel;
import editor.ui.elements.LabeledIntegerSpinner;

public class SphereGenerator extends ShapeGenerator
{
  private final LabeledIntegerSpinner radiusSpinner;
  private final LabeledIntegerSpinner latFacesSpinner;
  private final LabeledIntegerSpinner lonFacesSpinner;
  
  public SphereGenerator()
  {
    super(ShapeGenerator.Primitive.SPHERE);
    
    radiusSpinner = new LabeledIntegerSpinner("Radius", 10, 5000, 200);
    latFacesSpinner = new LabeledIntegerSpinner("Vertical Divisions", 3, 32, 12);
    lonFacesSpinner = new LabeledIntegerSpinner("Radial Divisions", 3, 32, 12);
  }
  

  public void addFields(PrimitiveOptionsPanel panel)
  {
    panel.addSpinner(radiusSpinner);
    panel.addSpinner(latFacesSpinner);
    panel.addSpinner(lonFacesSpinner);
  }
  

  public void setVisible(boolean b)
  {
    radiusSpinner.setVisible(b);
    latFacesSpinner.setVisible(b);
    lonFacesSpinner.setVisible(b);
  }
  

  public TriangleBatch generate(int centerX, int centerY, int centerZ)
  {
    int radius = radiusSpinner.getValue();
    int latFaces = latFacesSpinner.getValue();
    int lonFaces = lonFacesSpinner.getValue();
    
    return generate(radius, latFaces, lonFaces, centerX, centerY, centerZ);
  }
  
  private static TriangleBatch generate(int radius, int latfaces, int longfaces, int centerX, int centerY, int centerZ)
  {
    Vertex[][] rings = new Vertex[latfaces + 1][longfaces + 1];
    
    for (int j = 0; j <= longfaces; j++)
    {
      rings[0][j] = new Vertex(centerX, centerY + radius, centerZ);
      0uv = new UV(1024 * j / longfaces, 0);
      rings[latfaces][j] = new Vertex(centerX, centerY - radius, centerZ);
      uv = new UV(1024 * j / longfaces, 1024);
    }
    
    for (int i = 1; i < latfaces; i++) {
      for (int j = 0; j <= longfaces; j++)
      {
        double phi = 6.283185307179586D * j / longfaces;
        double theta = 3.141592653589793D * i / latfaces;
        
        rings[i][j] = new Vertex(centerX + 
          (int)(radius * Math.cos(phi) * Math.sin(theta)), centerY + 
          (int)(radius * Math.cos(theta)), centerZ + 
          (int)(radius * Math.sin(phi) * Math.sin(theta)));
        
        uv = new UV(1024 * j / longfaces, 1024 * i / latfaces);
      }
    }
    

    TriangleBatch batch = new TriangleBatch();
    
    for (int i = 0; i < latfaces; i++) {
      for (int j = 0; j < longfaces; j++)
      {
        triangles.add(new Triangle(rings[i][j], rings[i][(j + 1)], rings[(i + 1)][j]));
        triangles.add(new Triangle(rings[i][(j + 1)], rings[(i + 1)][(j + 1)], rings[(i + 1)][j]));
      }
    }
    return batch;
  }
}
