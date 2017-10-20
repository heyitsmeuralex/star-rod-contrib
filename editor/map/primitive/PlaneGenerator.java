package editor.map.primitive;

import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.TriangleBatch;
import editor.ui.dialogs.PrimitiveOptionsPanel;
import editor.ui.elements.LabeledIntegerSpinner;
import java.util.List;

public class PlaneGenerator extends ShapeGenerator
{
  private final LabeledIntegerSpinner sizeSpinner;
  private final LabeledIntegerSpinner xSpinner;
  private final LabeledIntegerSpinner zSpinner;
  
  public PlaneGenerator()
  {
    super(ShapeGenerator.Primitive.PLANAR_GRID);
    
    sizeSpinner = new LabeledIntegerSpinner("Size", 10, 5000, 200);
    xSpinner = new LabeledIntegerSpinner("X Segments", 1, 64, 4);
    zSpinner = new LabeledIntegerSpinner("Z Segments", 1, 64, 4);
  }
  

  public void addFields(PrimitiveOptionsPanel panel)
  {
    panel.addSpinner(sizeSpinner);
    panel.addSpinner(xSpinner);
    panel.addSpinner(zSpinner);
  }
  

  public void setVisible(boolean b)
  {
    sizeSpinner.setVisible(b);
    xSpinner.setVisible(b);
    zSpinner.setVisible(b);
  }
  

  public TriangleBatch generate(int centerX, int centerY, int centerZ)
  {
    int numX = xSpinner.getValue();
    int numZ = zSpinner.getValue();
    int size = sizeSpinner.getValue();
    
    return generate(numX, numZ, size, size, centerX, centerY, centerZ);
  }
  
  private static TriangleBatch generate(int numX, int numZ, int sizeX, int sizeZ, int centerX, int centerY, int centerZ)
  {
    Vertex[][] grid = new Vertex[numX + 1][numZ + 1];
    for (int i = 0; i <= numX; i++) {
      for (int j = 0; j <= numZ; j++)
      {
        Vertex v = new Vertex(centerX + (int)(sizeX * (i / numX - 0.5D)), centerY, centerZ + (int)(sizeZ * (j / numZ - 0.5D)));
        



        uv = new editor.map.shape.UV((int)(1024.0D * (i - numX / 2.0D)), (int)(1024.0D * (j - numZ / 2.0D)));
        

        grid[i][j] = v;
      }
    }
    TriangleBatch batch = new TriangleBatch();
    
    for (int i = 0; i < numX; i++) {
      for (int j = 0; j < numZ; j++)
      {
        Triangle t1 = new Triangle(grid[i][(j + 1)], grid[(i + 1)][j], grid[i][j]);
        Triangle t2 = new Triangle(grid[(i + 1)][j], grid[i][(j + 1)], grid[(i + 1)][(j + 1)]);
        triangles.add(t1);
        triangles.add(t2);
      }
    }
    return batch;
  }
}
