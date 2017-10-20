package editor.camera;

import editor.Editor;
import editor.map.Axis;
import editor.map.BoundingBox;
import editor.map.hit.CameraController;
import editor.map.shape.TransformMatrix;
import editor.selection.SelectablePoint;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;



public class PreviewCamera
  extends PerspectiveCamera
{
  private static final float SPEED = 160.0F;
  private static final float YAW_SPEED = 0.5F;
  private Vector3f lookAt = new Vector3f();
  private static final Vector3f UP = new Vector3f(0.0F, 1.0F, 0.0F);
  
  public PreviewCamera(Viewport view)
  {
    super(view);
  }
  
  public void updatePosition()
  {
    CameraController controller = Editor.getPreviewController();
    if (controller == null) {
      return;
    }
    controller.updateTarget();
    
    double L = controller.getTargetBoomLength();
    if (Math.abs(L) < 0.1D) {
      L = Math.signum(L) * 0.1D;
    }
    double thetaRad = Math.toRadians(boomPitch);
    double phiRad = -Math.toRadians(viewPitch);
    
    Vector3f targetPoint = controller.getTargetPoint();
    double yawRad = controller.getTargetYaw();
    

    pos.x = (x - (float)(L * Math.cos(thetaRad) * Math.sin(yawRad)));
    pos.y = (y + (float)(L * Math.sin(thetaRad)));
    pos.z = (z + (float)(L * Math.cos(thetaRad) * Math.cos(yawRad)));
    
    double R = L * Math.cos(thetaRad);
    double H = pos.y - y;
    

    lookAt.x = (pos.x + (float)((R * Math.cos(phiRad) + H * Math.sin(phiRad)) * Math.sin(yawRad)));
    lookAt.y = (pos.y + (float)(R * Math.sin(phiRad) - H * Math.cos(phiRad)));
    lookAt.z = (pos.z - (float)((R * Math.cos(phiRad) + H * Math.sin(phiRad)) * Math.cos(yawRad)));
  }
  

  public void applyTransform()
  {
    TransformMatrix m = TransformMatrix.lookAt(pos, lookAt, UP);
    m.put(viewBuffer);
    GL11.glLoadMatrix(viewBuffer);
  }
  

  public void recalculateProjectionMatrix()
  {
    recalculateProjectionMatrix(25.0F, 1.48F, 16.0F, 4096.0F);
  }
  

  public void reset()
  {
    recalculateProjectionMatrix();
  }
  

  public void resize()
  {
    super.resize();
  }
  


  public void centerOn(BoundingBox aabb) {}
  

  public void handleMovementInput(float deltaTime)
  {
    CameraController controller = Editor.getPreviewController();
    if (controller == null) {
      return;
    }
    float vr = 0.0F;
    float vf = 0.0F;
    

    if (Keyboard.isKeyDown(17))
      vf += 160.0F * deltaTime;
    if (Keyboard.isKeyDown(31))
      vf -= 160.0F * deltaTime;
    if (Keyboard.isKeyDown(30))
      vr -= 160.0F * deltaTime;
    if (Keyboard.isKeyDown(32)) {
      vr += 160.0F * deltaTime;
    }
    double yawRad = controller.getTargetYaw();
    int vx = (int)Math.round(vr * Math.cos(yawRad) + vf * Math.sin(yawRad));
    int vz = (int)Math.round(vr * Math.sin(yawRad) - vf * Math.cos(yawRad));
    
    samplePoint.setX(samplePoint.getX() + vx);
    samplePoint.setZ(samplePoint.getZ() + vz);
    
    float vy = 0.0F;
    
    if (Keyboard.isKeyDown(203))
      vy += 0.5F * deltaTime;
    if (Keyboard.isKeyDown(205)) {
      vy -= 0.5F * deltaTime;
    }
    

    controller.setTargetYaw(yawRad + vy);
  }
  


  public Vector3f getTranslationVector(int dx, int dy)
  {
    return new Vector3f();
  }
  

  public Axis getRotationAxis()
  {
    return null;
  }
}
