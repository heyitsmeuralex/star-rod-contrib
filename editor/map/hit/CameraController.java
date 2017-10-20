package editor.map.hit;

import editor.Editor;
import editor.commands.AbstractCommand;
import editor.map.BoundingBox;
import editor.map.MapObject;
import editor.map.MutablePoint;
import editor.render.Renderer;
import editor.selection.SelectablePoint;
import editor.selection.SelectablePoint.SetPointPosition;
import editor.selection.SelectionManager;
import editor.ui.info.CameraInfoPanel;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;



















public class CameraController
  implements Externalizable
{
  private static final long serialVersionUID = 1L;
  private static final int latestVersion = 0;
  private int instanceVersion = 0;
  
  public ControlType type;
  
  public float boomLength;
  
  public float boomPitch;
  
  public float viewPitch;
  public boolean flag;
  public MapObject parent;
  private Vector3f targetPos = new Vector3f();
  private double targetBoomLength = 450.0D;
  private double targetYaw = 0.0D;
  
  public SelectablePoint samplePoint;
  
  public SelectablePoint posA;
  
  public SelectablePoint posB;
  
  public SelectablePoint posC;
  
  private List<SelectablePoint> points;
  private static final int[] DEFAULT_CAMERA = { 0, 
  
    Float.floatToIntBits(450.0F), 
    Float.floatToIntBits(16.0F), 
    Float.floatToIntBits(0.0F), 
    Float.floatToIntBits(10.0F), 
    Float.floatToIntBits(50.0F), 
    Float.floatToIntBits(0.0F), 
    Float.floatToIntBits(10.0F), 
    Float.floatToIntBits(-50.0F), 
    Float.floatToIntBits(-6.0F), 0 };
  

  private static final float HELP_POINT_SIZE = 16.0F;
  

  private static final float HELP_LINE_SIZE = 3.0F;
  
  private static final float PROJECT_POINT_SIZE = 8.0F;
  
  private static final float PROJECT_LINE_SIZE = 2.0F;
  

  public CameraController()
  {
    points = new ArrayList(4);
    samplePoint = new SelectablePoint(2.0F);
  }
  
  public CameraController(MapObject parent)
  {
    this(parent, DEFAULT_CAMERA);
  }
  
  public CameraController(MapObject parent, int[] cameraData)
  {
    this.parent = parent;
    boomLength = Float.intBitsToFloat(cameraData[1]);
    boomPitch = Float.intBitsToFloat(cameraData[2]);
    





    float[] pos = {Float.intBitsToFloat(cameraData[3]), Float.intBitsToFloat(cameraData[4]), Float.intBitsToFloat(cameraData[5]), Float.intBitsToFloat(cameraData[6]), Float.intBitsToFloat(cameraData[7]), Float.intBitsToFloat(cameraData[8]) };
    
    viewPitch = Float.intBitsToFloat(cameraData[9]);
    flag = (cameraData[10] != 0);
    
    points = new ArrayList(4);
    samplePoint = new SelectablePoint(2.0F);
    

    for (int i = 0; i < 6; i++)
    {
      if (pos[i] < -32768.0F) {
        pos[i] = 0.0F;
      }
    }
    posA = new SelectablePoint(new MutablePoint(pos[0], pos[1], pos[2]), 2.0F);
    posB = new SelectablePoint(new MutablePoint(pos[3], pos[4], pos[5]), 2.0F);
    posC = new SelectablePoint(new MutablePoint(pos[1], 0.0F, pos[4]), 2.0F);
    
    setType(ControlType.getType(cameraData[0]));
    if (type == ControlType.TYPE_4) {
      posA.setY(posB.getY());
    }
  }
  
  public int[] getData() {
    if (type == ControlType.TYPE_4) {
      return new int[] { type.index, 
      
        Float.floatToIntBits(boomLength), 
        Float.floatToIntBits(boomPitch), 
        Float.floatToIntBits(posA.getX()), 
        Float.floatToIntBits(posA.getY()), 
        Float.floatToIntBits(posA.getZ()), 
        Float.floatToIntBits(posB.getX()), 
        Float.floatToIntBits(posB.getY()), 
        Float.floatToIntBits(posB.getZ()), 
        Float.floatToIntBits(viewPitch), flag ? 1 : 0 };
    }
    
    return new int[] { type.index, 
    
      Float.floatToIntBits(boomLength), 
      Float.floatToIntBits(boomPitch), 
      Float.floatToIntBits(posA.getX()), 
      Float.floatToIntBits(posC.getX()), 
      Float.floatToIntBits(posA.getZ()), 
      Float.floatToIntBits(posB.getX()), 
      Float.floatToIntBits(posC.getZ()), 
      Float.floatToIntBits(posB.getZ()), 
      Float.floatToIntBits(viewPitch), flag ? 1 : 0 };
  }
  

  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    instanceVersion = in.readInt();
    
    type = ((ControlType)in.readObject());
    flag = in.readBoolean();
    
    boomLength = in.readFloat();
    boomPitch = in.readFloat();
    viewPitch = in.readFloat();
    
    float ax = in.readFloat();
    float ay = in.readFloat();
    float az = in.readFloat();
    
    float bx = in.readFloat();
    float by = in.readFloat();
    float bz = in.readFloat();
    
    float cx = in.readFloat();
    float cy = in.readFloat();
    float cz = in.readFloat();
    
    posA = new SelectablePoint(new MutablePoint(ax, ay, az), 2.0F);
    posB = new SelectablePoint(new MutablePoint(bx, by, bz), 2.0F);
    posC = new SelectablePoint(new MutablePoint(cx, cy, cz), 2.0F);
    
    setType(type);
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeInt(0);
    
    out.writeObject(type);
    out.writeBoolean(flag);
    
    out.writeFloat(boomLength);
    out.writeFloat(boomPitch);
    out.writeFloat(viewPitch);
    
    out.writeFloat(posA.getX());
    out.writeFloat(posA.getY());
    out.writeFloat(posA.getZ());
    
    out.writeFloat(posB.getX());
    out.writeFloat(posB.getY());
    out.writeFloat(posB.getZ());
    
    out.writeFloat(posC.getX());
    out.writeFloat(posC.getY());
    out.writeFloat(posC.getZ());
  }
  




















  public void startPreview(Vector3f pos)
  {
    if (pos != null)
    {
      samplePoint.setX(Math.round(pos.getX()));
      samplePoint.setY(Math.round(parent.AABB.getCenter().y));
      samplePoint.setZ(Math.round(pos.getZ()));
    }
  }
  
  public void updateTarget()
  {
    float Ax = posA.getX();
    float Az = posA.getZ();
    float Bx = posB.getX();
    float Bz = posB.getZ();
    float Cx = posC.getX();
    float Cz = posC.getZ();
    
    switch (1.$SwitchMap$editor$map$hit$ControlType[type.ordinal()])
    {





    case 1: 
      double BAx = Bx - Ax;
      double BAz = Bz - Az;
      targetYaw = Math.atan2(BAx, -BAz);
      targetBoomLength = Math.abs(boomLength);
      
      if (!flag) {
        targetPos = samplePoint.getPosition();
      }
      else
      {
        double d2 = BAx * BAx + BAz * BAz;
        double perpdot = BAx * (samplePoint.getZ() - Bz) - BAz * (samplePoint.getX() - Bx);
        
        targetPos.y = samplePoint.getY();
        targetPos.x = ((float)(Bx - BAz * perpdot / d2));
        targetPos.z = ((float)(Bz + BAx * perpdot / d2));
      }
      
      break;
    





    case 2: 
      double PAx = samplePoint.getX() - Ax;
      double PAz = samplePoint.getZ() - Az;
      targetBoomLength = Math.abs(boomLength);
      
      if (boomLength > 0.0F)
      {
        PAx = -PAx;
        PAz = -PAz;
      }
      targetYaw = Math.atan2(PAx, -PAz);
      
      if (!flag) {
        targetPos = samplePoint.getPosition();
      }
      else {
        double PA = Math.sqrt(PAx * PAx + PAz * PAz);
        
        if (PA == 0.0D)
        {
          targetPos = samplePoint.getPosition();
        }
        else
        {
          double BAx = Bx - Ax;
          double BAz = Bz - Az;
          double BA = Math.sqrt(BAx * BAx + BAz * BAz);
          
          targetPos.y = samplePoint.getY();
          targetPos.x = ((float)(Ax - PAx * (BA / PA)));
          targetPos.z = ((float)(Az - PAz * (BA / PA)));
        }
      }
      break;
    


    case 3: 
      if (!flag)
      {
        double Kx = Ax;
        double Kz = Az;
        
        if ((Ax == Bx) && (Az == Bz))
        {
          Kx = Cx;
          Kz = Cz;
        }
        
        double BCx = Bx - Cx;
        double BCz = Bz - Cz;
        
        double BKx = Bx - Kx;
        double BKz = Bz - Kz;
        
        double BPx = Bx - samplePoint.getX();
        double BPz = Bz - samplePoint.getZ();
        
        if (BCx == 0.0D)
        {
          double Q = BCx * BKx / BCz + BKz;
          targetPos.y = samplePoint.getY();
          targetPos.x = ((float)(samplePoint.getX() - BKz * (BPz * BCx / BCz - BPx) / Q));
          targetPos.z = ((float)(samplePoint.getZ() + BKx * (BPz * BCx / BCz - BPx) / Q));
        }
        else
        {
          double Q = -(BCz * BKz / BCx) - BKx;
          targetPos.y = samplePoint.getY();
          targetPos.x = ((float)(samplePoint.getX() - BKz * (BPx * BCz / BCx - BPz) / Q));
          targetPos.z = ((float)(samplePoint.getZ() + BKx * (BPx * BCz / BCx - BPz) / Q));
        }
        
        targetBoomLength = Math.abs(boomLength); }
      break;
    










    case 4: 
      targetPos = samplePoint.getPosition();
      targetBoomLength = boomLength;
      
      break;
    




    case 5: 
      targetPos = new Vector3f(posB.getX(), posB.getY(), posB.getZ());
      double BAx = Bx - Ax;
      double BAz = Bz - Az;
      targetYaw = Math.atan2(BAx, -BAz);
      targetBoomLength = Math.abs(boomLength);
      
      break;
    





    case 6: 
      if (!flag)
      {
        double BCx = Bx - Cx;
        double BCz = Bz - Cz;
        
        double PCx = samplePoint.getX() - Cx;
        double PCz = samplePoint.getZ() - Cz;
        
        double Q = (PCx * BCx + PCz * BCz) / (BCx * BCx + BCz * BCz);
        
        targetPos.y = samplePoint.getY();
        targetPos.x = ((float)(Cx + Q * BCx));
        targetPos.z = ((float)(Cz + Q * BCz));
        
        targetBoomLength = Math.abs(boomLength);
        
        double TAx = targetPos.x - Ax;
        double TAz = targetPos.z - Az;
        
        if (boomLength < 0.0F) {
          targetYaw = Math.atan2(TAx, -TAz);
        } else {
          targetYaw = Math.atan2(-TAx, TAz);
        }
        

      }
      else
      {
        targetPos = posB.getPosition();
      }
      
      break;
    







    case 7: 
      double BAx = Bx - Ax;
      double BAz = Bz - Az;
      targetYaw = Math.atan2(BAz, BAx);
      targetBoomLength = Math.abs(boomLength);
      
      double d2 = BAx * BAx + BAz * BAz;
      double dot = BAx * (samplePoint.getX() - Ax) + BAz * (samplePoint.getZ() - Az);
      
      double qx = BAx * dot / d2;
      double qz = BAz * dot / d2;
      double sz;
      double sx;
      double sz; if (BAx * qx + BAz * qz < 0.0D)
      {
        double sx = Ax;
        sz = Az;
      } else { double sz;
        if (BAx * BAx + BAz * BAz < qx * qx + qz * qz)
        {
          double sx = Bx;
          sz = Bz;
        }
        else
        {
          sx = Ax + qx;
          sz = Az + qz;
        }
      }
      if (!flag)
      {
        sx += samplePoint.getX() - (qx + Ax);
        sz += samplePoint.getZ() - (qz + Az);
      }
      
      targetPos.y = samplePoint.getY();
      targetPos.x = ((float)sx);
      targetPos.z = ((float)sz);
      

      break;
    }
    
  }
  


  public Vector3f getTargetPoint()
  {
    return targetPos;
  }
  
  public double getTargetBoomLength()
  {
    return targetBoomLength;
  }
  
  public void setTargetYaw(double yaw)
  {
    targetYaw = yaw;
  }
  
  public double getTargetYaw()
  {
    return targetYaw;
  }
  
  public void drawHelpers(boolean previewMode)
  {
    GL11.glPushAttrib(265021);
    GL11.glDisable(3553);
    GL11.glPointSize(16.0F);
    GL11.glLineWidth(3.0F);
    
    float h = parent.AABB.getCenter().y + 50.0F;
    
    switch (1.$SwitchMap$editor$map$hit$ControlType[type.ordinal()]) {
    case 1: 
      drawHelpers0(previewMode, h); break;
    case 2:  drawHelpers1(previewMode, h); break;
    case 3:  drawHelpers2(previewMode, h); break;
    case 4: 
      break; case 5:  drawHelpers4(previewMode, h); break;
    case 6:  drawHelpers5(previewMode, h); break;
    case 7:  drawHelpers6(previewMode, h); break;
    default: 
      throw new IllegalStateException("Invalid camera control type.");
    }
    
    if (previewMode)
    {
      Vector3f target = getTargetPoint();
      
      GL11.glPointSize(16.0F);
      
      GL11.glBegin(0);
      GL11.glColor3f(Renderer.color, 1.0F, Renderer.color);
      GL11.glVertex3f(x, y, z);
      GL11.glColor3f(1.0F, 0.0F, Renderer.color);
      GL11.glVertex3f(samplePoint.getX(), samplePoint.getY(), samplePoint.getZ());
      GL11.glEnd();
      
      GL11.glEnable(2852);
      GL11.glLineStipple(1, (short)255);
      
      GL11.glBegin(1);
      GL11.glColor3f(Renderer.color, 1.0F, Renderer.color);
      GL11.glVertex3f(x, y, z);
      GL11.glColor3f(1.0F, 0.0F, Renderer.color);
      GL11.glVertex3f(samplePoint.getX(), samplePoint.getY(), samplePoint.getZ());
      GL11.glEnd();
      
      GL11.glDisable(2852);
    }
    
    GL11.glPopAttrib();
  }
  

  private void drawHelpers0(boolean previewMode, float h)
  {
    GL11.glBegin(1);
    vertexA(h);
    vertexB(h);
    GL11.glEnd();
    
    projectA(h);
    projectB(h);
    
    if ((previewMode) && (flag))
    {
      GL11.glEnable(2852);
      GL11.glLineStipple(1, (short)255);
      GL11.glBegin(1);
      
      vertexB(h);
      GL11.glVertex3f(targetPos.x, h, targetPos.z);
      
      GL11.glEnd();
      GL11.glDisable(2852);
    }
  }
  
  private void drawHelpers1(boolean previewMode, float h)
  {
    if (flag)
    {
      float dx = posB.getX() - posA.getX();
      float dz = posB.getZ() - posA.getZ();
      float r = (float)Math.sqrt(dx * dx + dz * dz);
      
      int n = 32 + Math.round((float)Math.sqrt(r));
      if (n % 2 == 1) { n++;
      }
      double dA = 6.283185307179586D / n;
      
      GL11.glBegin(1);
      vertexA(h);
      vertexB(h);
      
      for (int i = 0; i <= n; i++)
      {
        float x = r * (float)Math.sin(i * dA);
        float z = r * (float)Math.cos(i * dA);
        GL11.glVertex3f(posA.getX() + x, h, posA.getZ() + z);
      }
      GL11.glEnd();
      
      projectB(h);
    }
    
    projectA(h);
  }
  

  private void drawHelpers2(boolean previewMode, float h)
  {
    if (flag) {
      return;
    }
    if (previewMode)
    {
      Vector3f vBT = new Vector3f();
      Vector3f.sub(targetPos, posB.getPosition(), vBT);
      y = 0.0F;
      
      Vector3f vCT = new Vector3f();
      Vector3f.sub(targetPos, posC.getPosition(), vCT);
      y = 0.0F;
      
      GL11.glEnable(2852);
      GL11.glLineStipple(1, (short)255);
      GL11.glBegin(1);
      
      if (vBT.length() < vCT.length())
      {
        vertexB(h);
        GL11.glVertex3f(targetPos.x, h, targetPos.z);
      }
      else
      {
        vertexC(h);
        GL11.glVertex3f(targetPos.x, h, targetPos.z);
      }
      
      GL11.glEnd();
      GL11.glDisable(2852);
    }
    
    GL11.glBegin(1);
    vertexB(h);
    vertexC(h);
    vertexA(h);
    vertexB(h);
    
    Vector3f endPoint = new Vector3f();
    Vector3f vBA = new Vector3f();
    Vector3f.sub(posA.getPosition(), posB.getPosition(), vBA);
    y = 0.0F;
    
    if (vBA.length() > 0.0F)
    {
      vBA.normalise();
      Vector3f.cross(new Vector3f(0.0F, 64.0F, 0.0F), vBA, endPoint);
      
      vertexB(h);
      GL11.glColor3f(0.5F + Renderer.color / 2.0F, 0.5F + Renderer.color / 2.0F, 1.0F);
      GL11.glVertex3f(posB.getX() + x, h, posB.getZ() + z);
    }
    GL11.glEnd();
    
    projectA(h);
    projectB(h);
    projectC(h);
  }
  

  private void drawHelpers4(boolean previewMode, float h)
  {
    GL11.glBegin(1);
    vertexA(h);
    vertexB(posB.getY());
    GL11.glEnd();
    
    projectA(h);
    
    GL11.glBegin(0);
    vertexB(posB.getY());
    GL11.glEnd();
  }
  

  private void drawHelpers5(boolean previewMode, float h)
  {
    if (!flag)
    {
      if (previewMode)
      {
        Vector3f vBT = new Vector3f();
        Vector3f.sub(targetPos, posB.getPosition(), vBT);
        y = 0.0F;
        
        Vector3f vCT = new Vector3f();
        Vector3f.sub(targetPos, posC.getPosition(), vCT);
        y = 0.0F;
        
        GL11.glEnable(2852);
        GL11.glLineStipple(1, (short)255);
        GL11.glBegin(1);
        
        if (vBT.length() < vCT.length())
        {
          vertexB(h);
          GL11.glVertex3f(targetPos.x, h, targetPos.z);
        }
        else
        {
          vertexC(h);
          GL11.glVertex3f(targetPos.x, h, targetPos.z);
        }
        
        vertexA(h);
        GL11.glColor3f(1.0F, 1.0F, 1.0F);
        GL11.glVertex3f(targetPos.x, targetPos.y, targetPos.z);
        
        GL11.glEnd();
        GL11.glDisable(2852);
      }
      
      GL11.glBegin(1);
      vertexB(h);
      vertexC(h);
      GL11.glEnd();
      
      projectA(h);
      projectC(h);
    }
    
    projectB(h);
  }
  

  private void drawHelpers6(boolean previewMode, float h)
  {
    if ((previewMode) && (!flag))
    {
      Vector3f vBA = new Vector3f();
      Vector3f.sub(posA.getPosition(), posB.getPosition(), vBA);
      y = 0.0F;
      
      if (vBA.length() > 0.0F)
      {
        Vector3f unit = new Vector3f();
        
        vBA.normalise();
        Vector3f.cross(new Vector3f(0.0F, 1.0F, 0.0F), vBA, unit);
        
        Vector3f vBT = new Vector3f();
        Vector3f.sub(targetPos, posB.getPosition(), vBT);
        y = 0.0F;
        
        GL11.glEnable(2852);
        GL11.glLineStipple(1, (short)255);
        GL11.glBegin(1);
        
        float len = Vector3f.dot(vBT, unit);
        
        vertexB(h);
        GL11.glVertex3f(posB.getX() + x * len, h, posB.getZ() + z * len);
        
        vertexA(h);
        GL11.glVertex3f(posA.getX() + x * len, h, posA.getZ() + z * len);
        
        GL11.glEnd();
        GL11.glDisable(2852);
      }
    }
    
    GL11.glBegin(1);
    vertexA(h);
    vertexB(h);
    GL11.glEnd();
    
    projectA(h);
    projectB(h);
  }
  
  private void projectA(float h)
  {
    GL11.glColor3f(1.0F, 0.0F, 0.0F);
    project(posA, h);
  }
  
  private void projectB(float h)
  {
    GL11.glColor3f(0.0F, 0.0F, 1.0F);
    project(posB, h);
  }
  
  private void projectC(float h)
  {
    GL11.glColor3f(0.0F, 1.0F, 0.0F);
    project(posC, h);
  }
  
  private void project(SelectablePoint p, float h)
  {
    if (p.getY() != h)
    {
      GL11.glPointSize(8.0F);
      GL11.glBegin(0);
      GL11.glVertex3f(p.getX(), h, p.getZ());
      GL11.glEnd();
      GL11.glPointSize(16.0F);
      
      GL11.glLineWidth(2.0F);
      Renderer.drawStipple(p
        .getX(), p.getY(), p.getZ(), p
        .getX(), h, p.getZ(), 5.0F);
      
      GL11.glLineWidth(3.0F);
    }
    
    GL11.glBegin(0);
    GL11.glVertex3f(p.getX(), p.getY(), p.getZ());
    GL11.glEnd();
  }
  
  private void vertexA(float h)
  {
    GL11.glColor3f(1.0F, 0.0F, 0.0F);
    GL11.glVertex3f(posA.getX(), h, posA.getZ());
  }
  
  private void vertexB(float h)
  {
    GL11.glColor3f(0.0F, 0.0F, 1.0F);
    GL11.glVertex3f(posB.getX(), h, posB.getZ());
  }
  
  private void vertexC(float h)
  {
    GL11.glColor3f(0.0F, 1.0F, 0.0F);
    GL11.glVertex3f(posC.getX(), h, posC.getZ());
  }
  
  private void setType(ControlType type)
  {
    this.type = type;
    points.clear();
    points.addAll(getPointSet(this, type));
  }
  
  public void updateSelectionSet()
  {
    if (Editor.getPreviewController() == this) {
      Editor.selectionManager.usePoints(getPointSet(this, type));
    }
  }
  
  private static List<SelectablePoint> getPointSet(CameraController cam, ControlType controlType) {
    List<SelectablePoint> pointList = new ArrayList(4);
    pointList.add(samplePoint);
    
    switch (1.$SwitchMap$editor$map$hit$ControlType[controlType.ordinal()])
    {

    case 1: 
      pointList.add(posA);
      pointList.add(posB);
      break;
    

    case 2: 
      pointList.add(posA);
      
      if (flag) {
        pointList.add(posB);
      }
      
      break;
    case 3: 
      if (!flag)
      {
        pointList.add(posA);
        pointList.add(posB);
        pointList.add(posC);
      }
      



      break;
    case 4: 
      break;
    case 5: 
      pointList.add(posA);
      pointList.add(posB);
      break;
    

    case 6: 
      pointList.add(posB);
      if (!flag)
      {
        pointList.add(posA);
        pointList.add(posC);
      }
      

      break;
    case 7: 
      pointList.add(posA);
      pointList.add(posB);
      break;
    
    default: 
      throw new IllegalArgumentException("Control type " + controlType + " is not valid.");
    }
    
    return pointList;
  }
  
  public static final class SetCameraType
    extends AbstractCommand
  {
    private final CameraInfoPanel infoPanel;
    private final CameraController cam;
    private final ControlType oldType;
    private final ControlType newType;
    private final AbstractCommand selectionModCommand;
    
    public SetCameraType(CameraInfoPanel infoPanel, CameraController cam, ControlType controlType)
    {
      super();
      this.infoPanel = infoPanel;
      this.cam = cam;
      oldType = type;
      newType = controlType;
      
      List<SelectablePoint> newPoints = CameraController.getPointSet(cam, controlType);
      List<SelectablePoint> removeList = new LinkedList();
      
      for (SelectablePoint p : points)
      {
        if ((p.isSelected()) && (!newPoints.contains(p))) {
          removeList.add(p);
        }
      }
      selectionModCommand = Editor.selectionManager.getModifyPoints(null, removeList);
    }
    

    public boolean shouldExec()
    {
      return oldType != newType;
    }
    

    public void exec()
    {
      selectionModCommand.exec();
      
      super.exec();
      cam.setType(newType);
      cam.updateSelectionSet();
      
      infoPanel.updateFields(cam);
    }
    

    public void undo()
    {
      super.undo();
      cam.setType(oldType);
      cam.updateSelectionSet();
      
      infoPanel.updateFields(cam);
      
      selectionModCommand.undo();
    }
  }
  
  public static final class SetBoomLength extends AbstractCommand
  {
    private final CameraInfoPanel infoPanel;
    private final CameraController cam;
    private final float oldValue;
    private final float newValue;
    
    public SetBoomLength(CameraInfoPanel infoPanel, CameraController cam, float val)
    {
      super();
      this.infoPanel = infoPanel;
      this.cam = cam;
      oldValue = boomLength;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      cam.boomLength = newValue;
      
      infoPanel.updateFields(cam);
    }
    

    public void undo()
    {
      super.undo();
      cam.boomLength = oldValue;
      
      infoPanel.updateFields(cam);
    }
  }
  
  public static final class SetBoomPitch extends AbstractCommand
  {
    private final CameraInfoPanel infoPanel;
    private final CameraController cam;
    private final float oldValue;
    private final float newValue;
    
    public SetBoomPitch(CameraInfoPanel infoPanel, CameraController cam, float val)
    {
      super();
      this.infoPanel = infoPanel;
      this.cam = cam;
      oldValue = boomPitch;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      cam.boomPitch = newValue;
      
      infoPanel.updateFields(cam);
    }
    

    public void undo()
    {
      super.undo();
      cam.boomPitch = oldValue;
      
      infoPanel.updateFields(cam);
    }
  }
  
  public static final class SetViewPitch extends AbstractCommand
  {
    private final CameraInfoPanel infoPanel;
    private final CameraController cam;
    private final float oldValue;
    private final float newValue;
    
    public SetViewPitch(CameraInfoPanel infoPanel, CameraController cam, float val)
    {
      super();
      this.infoPanel = infoPanel;
      this.cam = cam;
      oldValue = viewPitch;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      cam.viewPitch = newValue;
      
      infoPanel.updateFields(cam);
    }
    

    public void undo()
    {
      super.undo();
      cam.viewPitch = oldValue;
      
      infoPanel.updateFields(cam);
    }
  }
  
  public static final class SetCameraPos extends SelectablePoint.SetPointPosition
  {
    private final CameraInfoPanel infoPanel;
    private final CameraController cam;
    
    public SetCameraPos(CameraInfoPanel infoPanel, CameraController cam, char pt, int axis, float val)
    {
      super(pt == 'B' ? posB : pt == 'A' ? posA : posC, axis, (int)val);
      this.infoPanel = infoPanel;
      this.cam = cam;
    }
    

    public void exec()
    {
      super.exec();
      infoPanel.updateFields(cam);
    }
    

    public void undo()
    {
      super.undo();
      infoPanel.updateFields(cam);
    }
  }
  
  public static final class SetCameraFlag extends AbstractCommand
  {
    private final CameraInfoPanel infoPanel;
    private final CameraController cam;
    private final boolean oldValue;
    private final boolean newValue;
    
    public SetCameraFlag(CameraInfoPanel infoPanel, CameraController cam, boolean val)
    {
      super();
      this.infoPanel = infoPanel;
      this.cam = cam;
      oldValue = flag;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      cam.flag = newValue;
      
      cam.setType(cam.type);
      cam.updateSelectionSet();
      infoPanel.updateFields(cam);
    }
    

    public void undo()
    {
      super.undo();
      cam.flag = oldValue;
      
      cam.setType(cam.type);
      cam.updateSelectionSet();
      infoPanel.updateFields(cam);
    }
  }
}
