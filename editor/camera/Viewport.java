package editor.camera;

import editor.map.BoundingBox;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;






public abstract class Viewport
{
  public final ViewType type;
  public AbstractCamera camera;
  public int minX;
  public int minY;
  public int maxX;
  public int maxY;
  protected int sizeX;
  protected int sizeY;
  public boolean wireframeMode;
  
  protected Viewport(ViewType type)
  {
    this(type, 0, 0, 0, 0);
  }
  
  protected Viewport(ViewType type, int minX, int minY, int maxX, int maxY)
  {
    this.type = type;
    this.minX = minX;
    this.minY = minY;
    this.maxX = maxX;
    this.maxY = maxY;
    sizeX = (maxX - minX);
    sizeY = (maxY - minY);
  }
  
  public void resize(int minX, int minY, int maxX, int maxY)
  {
    this.minX = minX;
    this.minY = minY;
    this.maxX = maxX;
    this.maxY = maxY;
    sizeX = (maxX - minX);
    sizeY = (maxY - minY);
    
    camera.resize();
  }
  
  public boolean contains(int x, int y)
  {
    boolean h = (minX <= x) && (maxX > x);
    boolean v = (minY <= y) && (maxY > y);
    return (h) && (v);
  }
  
  public void release()
  {
    camera.release();
  }
  
  public void setViewport()
  {
    GL11.glViewport(minX, minY, sizeX, sizeY);
    GL11.glScissor(minX, minY, sizeX, sizeY);
  }
  
  public abstract boolean allowsDragSelection();
  
  public abstract BoundingBox getDragSelectionVolume(Vector3f paramVector3f1, Vector3f paramVector3f2);
  
  public abstract float getScaleFactor(float paramFloat1, float paramFloat2, float paramFloat3);
  
  public abstract void render(boolean paramBoolean);
}
