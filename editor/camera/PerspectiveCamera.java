package editor.camera;

import editor.Editor;
import editor.input.MouseManager;
import editor.map.Axis;
import editor.map.BoundingBox;
import editor.map.Map;
import editor.map.shape.TransformMatrix;
import editor.render.TextureManager;
import java.nio.FloatBuffer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;




public class PerspectiveCamera
  extends AbstractCamera
{
  private float vfov;
  private float hfov;
  private float aspectRatio;
  public static final float DEFAULT_HFOV = 105.0F;
  public static final float DEFAULT_VFOV = 70.0F;
  private static final float MAX_PITCH = 80.0F;
  private static final float SPEED = 320.0F;
  
  public PerspectiveCamera(Viewport view)
  {
    this(new Vector3f(50.0F, 100.0F, 50.0F), new Vector3f(45.0F, -45.0F, 0.0F), view);
  }
  
  public PerspectiveCamera(Vector3f pos, Vector3f rotation, Viewport view)
  {
    this.view = view;
    setPosition(pos);
    setRotation(rotation);
    hfov = 105.0F;
    vfov = 70.0F;
  }
  
  public void release()
  {
    Mouse.setGrabbed(false);
  }
  
  public void reset()
  {
    setPosition(new Vector3f(50.0F, 100.0F, 50.0F));
    setRotation(new Vector3f(45.0F, -45.0F, 0.0F));
    recalculateProjectionMatrix();
  }
  

  public void centerOn(BoundingBox aabb)
  {
    Vector3f min = aabb.getMin();
    Vector3f max = aabb.getMax();
    float sizeX = x - x;
    float sizeY = y - y;
    float sizeZ = z - z;
    float size = Math.max(Math.max(sizeX, sizeY), sizeZ);
    size = Math.max(size, 100.0F);
    size = Math.min(size, 500.0F);
    

    Vector3f center = aabb.getCenter();
    float dX = pos.x - x;
    float dY = pos.y - y;
    float dZ = pos.z - z;
    float dist = (float)Math.sqrt(dX * dX + dY * dY + dZ * dZ);
    pos.x = (x + size / dist * dX);
    pos.y = (y + size / dist * dY);
    pos.z = (z + size / dist * dZ);
    

    float r = (float)Math.sqrt(dX * dX + dZ * dZ);
    yaw = ((int)Math.toDegrees(Math.atan2(-dX, dZ)));
    pitch = ((int)Math.toDegrees(Math.atan2(dY, r)));
    if (pitch < -80.0F) pitch = -80.0F;
    if (pitch > 80.0F) pitch = 80.0F;
  }
  
  public void resize()
  {
    aspectRatio = (view.sizeX / view.sizeY);
    vfov = ((float)Math.toDegrees(2.0D * Math.atan(Math.tan(Math.toRadians(hfov / 2.0F)) / aspectRatio)));
    
    recalculateProjectionMatrix();
  }
  
  public void recalculateProjectionMatrix()
  {
    if (Editor.useGameFOV) {
      recalculateProjectionMatrix(50.0F, aspectRatio, 16.0F, 4096.0F);
    } else {
      recalculateProjectionMatrix(vfov, aspectRatio, 1.0F, 65535.0F);
    }
  }
  
  protected TransformMatrix perspective() {
    if (Editor.useGameFOV) {
      return TransformMatrix.perspective(50.0F, aspectRatio, 16.0F, 4096.0F);
    }
    return TransformMatrix.perspective(vfov, aspectRatio, 1.0F, 65535.0F);
  }
  
  protected void recalculateProjectionMatrix(float vfov, float aspectRatio, float nearClip, float farClip)
  {
    float f = (float)(1.0D / Math.tan(Math.toRadians(vfov / 2.0F)));
    
    projBuffer.put(f / aspectRatio);
    projBuffer.put(0.0F);
    projBuffer.put(0.0F);
    projBuffer.put(0.0F);
    
    projBuffer.put(0.0F);
    projBuffer.put(f);
    projBuffer.put(0.0F);
    projBuffer.put(0.0F);
    
    projBuffer.put(0.0F);
    projBuffer.put(0.0F);
    projBuffer.put((farClip + nearClip) / (nearClip - farClip));
    projBuffer.put(-1.0F);
    
    projBuffer.put(0.0F);
    projBuffer.put(0.0F);
    projBuffer.put(2.0F * farClip * nearClip / (nearClip - farClip));
    projBuffer.put(0.0F);
    
    projBuffer.rewind();
  }
  

  public Vector3f getForward(float length)
  {
    float sinP = (float)Math.sin(Math.toRadians(pitch));
    float cosP = (float)Math.cos(Math.toRadians(pitch));
    float sinY = (float)Math.sin(Math.toRadians(yaw));
    float cosY = (float)Math.cos(Math.toRadians(yaw));
    
    return new Vector3f(length * cosP * sinY, length * -sinP, length * -cosP * cosY);
  }
  

  public Vector3f getUp(float length)
  {
    float sinP = -(float)Math.sin(Math.toRadians(pitch));
    float cosP = (float)Math.cos(Math.toRadians(pitch));
    float sinY = (float)Math.sin(Math.toRadians(yaw));
    float cosY = (float)Math.cos(Math.toRadians(yaw));
    
    return new Vector3f(length * sinP * sinY, length * cosP, length * -sinP * cosY);
  }
  

  public Vector3f getRight(float length)
  {
    float sinY = (float)Math.sin(Math.toRadians(yaw));
    float cosY = (float)Math.cos(Math.toRadians(yaw));
    
    return new Vector3f(length * cosY, 0.0F, length * sinY);
  }
  
  public Vector3f getTranslationVector(int dx, int dy)
  {
    float sinP = (float)Math.sin(Math.toRadians(pitch));
    float cosP = (float)Math.cos(Math.toRadians(pitch));
    float sinY = (float)Math.sin(Math.toRadians(yaw));
    float cosY = (float)Math.cos(Math.toRadians(yaw));
    

    return new Vector3f(dx * cosY + sinY * sinP * dy, dy * cosP, dx * sinY - cosY * sinP * dy);
  }
  




  public Axis getRotationAxis()
  {
    return Axis.X;
  }
  

  public void handleMovementInput(float deltaTime)
  {
    boolean moveEnabled = Keyboard.isKeyDown(42);
    Mouse.setGrabbed(moveEnabled);
    
    if (moveEnabled)
    {
      yaw += MouseManager.getFrameDX() * 0.16F;
      if (yaw > 360.0F) yaw -= 360.0F;
      if (yaw < 0.0F) { yaw += 360.0F;
      }
      pitch -= MouseManager.getFrameDY() * 0.16F;
      if (pitch < -80.0F) pitch = -80.0F;
      if (pitch > 80.0F) { pitch = 80.0F;
      }
      int vx = 0;int vz = 0;
      
      boolean moveForward = Keyboard.isKeyDown(17);
      boolean moveBackward = Keyboard.isKeyDown(31);
      boolean moveLeft = Keyboard.isKeyDown(30);
      boolean moveRight = Keyboard.isKeyDown(32);
      


      float mod = 1.0F;
      if (Mouse.isButtonDown(0)) mod *= 2.5F;
      if (Mouse.isButtonDown(1)) { mod *= 0.4F;
      }
      if (moveForward) vz = (int)(vz - 320.0F * deltaTime * mod);
      if (moveBackward) { vz = (int)(vz + 320.0F * deltaTime * mod);
      }
      if (moveLeft) vx = (int)(vx - 320.0F * deltaTime * mod);
      if (moveRight) { vx = (int)(vx + 320.0F * deltaTime * mod);
      }
      moveFromLook(vx, 0.0F, vz);
    }
  }
  
  private void moveFromLook(float dx, float dy, float dz)
  {
    Vector3f tmp4_1 = pos;41z = ((float)(41z + (dx * (float)Math.cos(Math.toRadians(yaw - 90.0F)) + dz * Math.cos(Math.toRadians(yaw))))); Vector3f 
      tmp51_48 = pos;5148x = ((float)(5148x - (dx * (float)Math.sin(Math.toRadians(yaw - 90.0F)) + dz * Math.sin(Math.toRadians(yaw))))); Vector3f 
      tmp98_95 = pos;9895y = ((float)(9895y + (dy * (float)Math.sin(Math.toRadians(pitch - 90.0F)) + dz * Math.sin(Math.toRadians(pitch)))));
  }
  

  public void drawBackground()
  {
    int bgTexID = maphasBackground ? mapglBackgroundTexID : TextureManager.glNoBackgoundTexID;
    
    GL11.glPushAttrib(3561);
    GL11.glPolygonMode(1032, 6914);
    GL11.glBindTexture(3553, bgTexID);
    GL11.glEnable(3553);
    
    GL11.glMatrixMode(5888);
    GL11.glLoadIdentity();
    GL11.glMatrixMode(5889);
    GL11.glLoadIdentity();
    GL11.glOrtho(0.0D, 1.0D, 1.0D, 0.0D, -1.0D, 1.0D);
    
    float left = yaw / 360.0F;
    float right = (yaw + hfov + 360.0F) / 360.0F;
    
    GL11.glColor3f(1.0F, 1.0F, 1.0F);
    GL11.glBegin(7);
    GL11.glTexCoord2f(left, 1.0F);
    GL11.glVertex2f(0.0F, 0.0F);
    
    GL11.glTexCoord2f(right, 1.0F);
    GL11.glVertex2f(1.0F, 0.0F);
    
    GL11.glTexCoord2f(right, 0.0F);
    GL11.glVertex2f(1.0F, 1.0F);
    
    GL11.glTexCoord2f(left, 0.0F);
    GL11.glVertex2f(0.0F, 1.0F);
    GL11.glEnd();
    GL11.glPopAttrib();
  }
}
