package editor.map.primitive;

import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.TriangleBatch;
import editor.ui.dialogs.PrimitiveOptionsPanel;
import editor.ui.elements.LabeledIntegerSpinner;
import java.util.List;

public class CubeGenerator extends ShapeGenerator
{
  private final LabeledIntegerSpinner sizeSpinner;
  
  public CubeGenerator()
  {
    super(ShapeGenerator.Primitive.CUBE);
    sizeSpinner = new LabeledIntegerSpinner("Size", 10, 5000, 200);
  }
  

  public void addFields(PrimitiveOptionsPanel panel)
  {
    panel.addSpinner(sizeSpinner);
  }
  

  public void setVisible(boolean b)
  {
    sizeSpinner.setVisible(b);
  }
  

  public TriangleBatch generate(int centerX, int centerY, int centerZ)
  {
    int size = sizeSpinner.getValue();
    centerY += sizeSpinner.getValue() / 2;
    
    return generate(size, centerX, centerY, centerZ);
  }
  
  private static TriangleBatch generate(int size, int centerX, int centerY, int centerZ)
  {
    Vertex[][] side = new Vertex[5][2];
    Vertex[][] top = new Vertex[2][2];
    Vertex[][] bottom = new Vertex[2][2];
    
    side[0][0] = new Vertex(centerX - (int)(0.5D * size), centerY - (int)(0.5D * size), centerZ - (int)(0.5D * size));
    


    side[1][0] = new Vertex(centerX + (int)(0.5D * size), centerY - (int)(0.5D * size), centerZ - (int)(0.5D * size));
    


    side[2][0] = new Vertex(centerX + (int)(0.5D * size), centerY - (int)(0.5D * size), centerZ + (int)(0.5D * size));
    


    side[3][0] = new Vertex(centerX - (int)(0.5D * size), centerY - (int)(0.5D * size), centerZ + (int)(0.5D * size));
    


    side[4][0] = new Vertex(centerX - (int)(0.5D * size), centerY - (int)(0.5D * size), centerZ - (int)(0.5D * size));
    



    for (int l = 0; l < 5; l++)
    {



      side[l][1] = new Vertex(side[l][0].getCurrentX(), centerY + (int)(0.5D * size), side[l][0].getCurrentZ());
      
      for (int k = 0; k < 2; k++) {
        uv = new editor.map.shape.UV(l * 1024, k * 1024);
      }
    }
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++)
      {
        top[i][j] = new Vertex(centerX + (int)((i - 0.5D) * size), centerY + (int)(0.5D * size), centerZ + (int)((j - 0.5D) * size));
        



        uv = new editor.map.shape.UV(i * 1024, (j + 1) * 1024 + 512);
      }
    }
    for (int i = 0; i < 2; i++) {
      for (int j = 0; j < 2; j++)
      {
        bottom[i][j] = new Vertex(centerX + (int)((i - 0.5D) * size), centerY - (int)(0.5D * size), centerZ + (int)((j - 0.5D) * size));
        



        uv = new editor.map.shape.UV(i * 1024, (j - 1) * 1024 - 512);
      }
    }
    TriangleBatch batch = new TriangleBatch();
    
    for (int i = 0; i < 4; i++)
    {
      triangles.add(new Triangle(side[i][0], side[i][1], side[(i + 1)][0]));
      triangles.add(new Triangle(side[(i + 1)][0], side[i][1], side[(i + 1)][1]));
    }
    
    triangles.add(new Triangle(top[0][0], top[0][1], top[1][0]));
    triangles.add(new Triangle(top[1][0], top[0][1], top[1][1]));
    
    triangles.add(new Triangle(bottom[0][0], bottom[1][0], bottom[0][1]));
    triangles.add(new Triangle(bottom[1][0], bottom[1][1], bottom[0][1]));
    
    return batch;
  }
}
