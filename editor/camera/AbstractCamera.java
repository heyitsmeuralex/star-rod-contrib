package editor.camera;

import editor.map.Axis;
import editor.map.BoundingBox;
import editor.map.shape.TransformMatrix;
import editor.selection.PickRay;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector3f;

















public abstract class AbstractCamera
{
  protected Viewport view;
  protected Vector3f pos;
  protected float pitch;
  protected float yaw;
  public static final float NEAR_CLIP = 1.0F;
  public static final float FAR_CLIP = 65535.0F;
  public FloatBuffer viewBuffer;
  public FloatBuffer projBuffer;
  
  public abstract void recalculateProjectionMatrix();
  
  public void setView()
  {
    GL11.glMatrixMode(5889);
    GL11.glLoadMatrix(projBuffer);
    GL11.glMatrixMode(5888);
    GL11.glLoadIdentity();
  }
  
  public abstract void reset();
  
  public abstract void resize();
  
  public abstract void centerOn(BoundingBox paramBoundingBox);
  
  public abstract void handleMovementInput(float paramFloat);
  
  public abstract Vector3f getTranslationVector(int paramInt1, int paramInt2);
  
  public abstract Axis getRotationAxis();
  
  public abstract Vector3f getForward(float paramFloat);
  
  public abstract Vector3f getUp(float paramFloat);
  
  public abstract Vector3f getRight(float paramFloat);
  
  public abstract void drawBackground();
  
  protected AbstractCamera() {
    viewBuffer = BufferUtils.createFloatBuffer(16);
    projBuffer = BufferUtils.createFloatBuffer(16);
  }
  
  public Vector3f getPosition()
  {
    return new Vector3f(pos.x, pos.y, pos.z);
  }
  
  public void release() {}
  
  public void setPosition(Vector3f pos)
  {
    this.pos = new Vector3f();
    posx = x;
    posy = y;
    posz = z;
  }
  
  public void setRotation(Vector3f rotation)
  {
    pitch = x;
    yaw = y;
  }
  

  public void applyTransform()
  {
    TransformMatrix m = TransformMatrix.identity();
    m.translate(new Vector3f(-pos.x, -pos.y, -pos.z));
    m.rotate(Axis.Y, yaw);
    m.rotate(Axis.X, pitch);
    
    m.put(viewBuffer);
    










    GL11.glLoadMatrix(viewBuffer);
  }
  




  public PickRay getPickRay()
  {
    FloatBuffer buffer = getMousePosition(false);
    Vector3f pickPoint = new Vector3f(buffer.get(), buffer.get(), buffer.get());
    Vector3f direction = new Vector3f();
    
    x -= pos.x;
    y -= pos.y;
    z -= pos.z;
    direction.normalise();
    
    return new PickRay(new Vector3f(pos), direction, view);
  }
  






  public FloatBuffer getMousePosition(boolean useDepth)
  {
    int mouseX = Mouse.getX();
    int mouseY = Mouse.getY();
    
    IntBuffer viewport = BufferUtils.createIntBuffer(16);
    viewport.put(view.minX);
    viewport.put(view.minY);
    viewport.put(view.sizeX);
    viewport.put(view.sizeY);
    viewport.rewind();
    
    float winX = mouseX;
    float winY = mouseY;
    FloatBuffer winZ = BufferUtils.createFloatBuffer(1);
    
    if (useDepth)
    {

      GL11.glReadPixels(mouseX, mouseY, 1, 1, 6402, 5126, winZ);
    }
    else
    {
      winZ.put(0.0F);
      winZ.rewind();
    }
    
    FloatBuffer position = BufferUtils.createFloatBuffer(3);
    GLU.gluUnProject(winX, winY, winZ.get(), viewBuffer, projBuffer, viewport, position);
    return position;
  }
}
