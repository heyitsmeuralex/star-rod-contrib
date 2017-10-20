package editor.map.primitive;

import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.TriangleBatch;
import editor.map.shape.UV;
import editor.ui.dialogs.PrimitiveOptionsPanel;
import editor.ui.elements.LabeledIntegerSpinner;
import java.awt.Font;
import java.util.List;
import javax.swing.JCheckBox;

public class SpiralRampGenerator extends ShapeGenerator
{
  private final LabeledIntegerSpinner innerRadiusSpinner;
  private final LabeledIntegerSpinner outerRadiusSpinner;
  private final LabeledIntegerSpinner divisionsSpinner;
  private final LabeledIntegerSpinner angleSpinner;
  private final LabeledIntegerSpinner riseSpinner;
  private final LabeledIntegerSpinner sideHeightSpinner;
  private final JCheckBox makeBottomCheckbox;
  
  public SpiralRampGenerator()
  {
    super(ShapeGenerator.Primitive.SPIRAL_RAMP);
    
    riseSpinner = new LabeledIntegerSpinner("Step Rise", 1, 500, 10);
    innerRadiusSpinner = new LabeledIntegerSpinner("Inner Radius", 0, 5000, 100);
    outerRadiusSpinner = new LabeledIntegerSpinner("Outer Radius", 10, 5000, 200);
    divisionsSpinner = new LabeledIntegerSpinner("Divisions", 2, 256, 24);
    angleSpinner = new LabeledIntegerSpinner("Arc Angle", 15, 1440, 360);
    
    sideHeightSpinner = new LabeledIntegerSpinner("Side Height", 0, 500, 0);
    
    makeBottomCheckbox = new JCheckBox(" Create Bottom Faces");
    makeBottomCheckbox.setSelected(false);
    makeBottomCheckbox.setFont(makeBottomCheckbox.getFont().deriveFont(12.0F));
  }
  


  public void addFields(PrimitiveOptionsPanel panel)
  {
    panel.addSpinner(riseSpinner);
    panel.addSpinner(innerRadiusSpinner);
    panel.addSpinner(outerRadiusSpinner);
    panel.addSpinner(divisionsSpinner);
    panel.addSpinner(angleSpinner);
    panel.addSpinner(sideHeightSpinner);
    panel.addCheckBox(makeBottomCheckbox);
  }
  

  public void setVisible(boolean b)
  {
    riseSpinner.setVisible(b);
    innerRadiusSpinner.setVisible(b);
    outerRadiusSpinner.setVisible(b);
    divisionsSpinner.setVisible(b);
    angleSpinner.setVisible(b);
    sideHeightSpinner.setVisible(b);
    makeBottomCheckbox.setVisible(b);
  }
  

  public TriangleBatch generate(int centerX, int centerY, int centerZ)
  {
    int steps = divisionsSpinner.getValue();
    int stepRise = riseSpinner.getValue();
    int innerRadius = innerRadiusSpinner.getValue();
    int outerRadius = outerRadiusSpinner.getValue();
    int angle = angleSpinner.getValue();
    
    int sideHeight = sideHeightSpinner.getValue();
    boolean makeBottom = makeBottomCheckbox.isSelected();
    
    if (innerRadius == outerRadius) {
      outerRadius = innerRadius + 50;
    }
    return generate(steps, stepRise, innerRadius, outerRadius, angle, sideHeight, makeBottom, centerX, centerY, centerZ);
  }
  



  private static TriangleBatch generate(int steps, int stepRise, int innerR, int outerR, int angle, int sideHeight, boolean makeBottom, int centerX, int centerY, int centerZ)
  {
    int N = steps + 1;
    double angleRad = Math.toRadians(angle);
    
    int uOffset = 0;
    int vOffset = 1536;
    
    int overflow = 1024 * steps - 32767;
    if (overflow > 0)
    {
      uOffset = 64512 * (1 + overflow / 1024);
      uOffset = uOffset < 32768 ? 32768 : uOffset;
    }
    
    Vertex[] ringI = new Vertex[N];
    Vertex[] ringO = new Vertex[N];
    
    for (int i = 0; i < N; i++)
    {
      int H = i * stepRise;
      double theta = angleRad * i / steps;
      double X = Math.sin(theta);
      double Z = Math.cos(theta);
      
      ringI[i] = new Vertex(centerX + (int)(innerR * X), centerY + H, centerZ - (int)(innerR * Z));
      



      ringO[i] = new Vertex(centerX + (int)(outerR * X), centerY + H, centerZ - (int)(outerR * Z));
      



      uv = new UV(i * 1024 + uOffset, 0);
      uv = new UV(i * 1024 + uOffset, 1024);
    }
    
    TriangleBatch batch = new TriangleBatch();
    
    for (int i = 0; i < N - 1; i++)
    {
      triangles.add(new Triangle(ringO[i], ringI[i], ringI[(i + 1)]));
      triangles.add(new Triangle(ringO[i], ringI[(i + 1)], ringO[(i + 1)]));
    }
    
    if (sideHeight > 0)
    {
      Vertex[] upperI = new Vertex[N];
      Vertex[] upperO = new Vertex[N];
      
      for (int i = 0; i < N; i++)
      {
        upperI[i] = ringI[i].deepCopy();
        upperO[i] = ringO[i].deepCopy();
        
        uv = new UV(i * 1024 + uOffset, vOffset);
        uv = new UV(i * 1024 + uOffset, vOffset);
      }
      
      float uvScale = 1024 / (outerR - innerR);
      
      Vertex[] lowerI = new Vertex[N];
      Vertex[] lowerO = new Vertex[N];
      for (int i = 0; i < steps + 1; i++)
      {



        lowerI[i] = new Vertex(upperI[i].getCurrentX(), upperI[i].getCurrentY() - sideHeight, upperI[i].getCurrentZ());
        lowerO[i] = new Vertex(upperO[i]
          .getCurrentX(), upperO[i]
          .getCurrentY() - sideHeight, upperO[i]
          .getCurrentZ());
        
        uv = new UV(uv.getU() + uOffset, vOffset + uvScale * sideHeight);
        uv = new UV(uv.getU() + uOffset, vOffset + uvScale * sideHeight);
      }
      
      for (int i = 0; i < steps; i++)
      {
        triangles.add(new Triangle(lowerI[(i + 1)], upperI[i], lowerI[i]));
        triangles.add(new Triangle(upperI[(i + 1)], upperI[i], lowerI[(i + 1)]));
        
        triangles.add(new Triangle(upperO[i], lowerO[(i + 1)], lowerO[i]));
        triangles.add(new Triangle(upperO[i], upperO[(i + 1)], lowerO[(i + 1)]));
      }
      
      if (makeBottom)
      {
        Vertex[] bottomI = new Vertex[steps + 1];
        Vertex[] bottomO = new Vertex[steps + 1];
        
        for (int i = 0; i < steps + 1; i++)
        {
          bottomI[i] = lowerI[i].deepCopy();
          bottomO[i] = lowerO[i].deepCopy();
          
          uv = new UV(i * 1024 + uOffset, -vOffset);
          uv = new UV(i * 1024 + uOffset, 1024 - vOffset);
        }
        
        for (int i = 0; i < steps; i++)
        {
          triangles.add(new Triangle(bottomI[i], bottomO[i], bottomI[(i + 1)]));
          triangles.add(new Triangle(bottomI[(i + 1)], bottomO[i], bottomO[(i + 1)]));
        }
      }
    }
    
    return batch;
  }
}
