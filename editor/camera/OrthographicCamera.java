package editor.camera;

import editor.Editor;
import editor.Grid;
import editor.input.MouseManager;
import editor.map.Axis;
import editor.map.BoundingBox;
import editor.selection.PickRay;
import java.nio.FloatBuffer;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;













public class OrthographicCamera
  extends AbstractCamera
{
  private static final float SPEED = 900.0F;
  private static final float MINIMUM_ZOOM = 0.07F;
  private static final float MAXIMUM_ZOOM = 200.0F;
  private float zoomLevel = 1.0F;
  
  private final ViewType type;
  
  public OrthographicCamera(Viewport view, ViewType type)
  {
    this(new Vector3f(0.0F, 0.0F, 0.0F), view, type);
  }
  

  public OrthographicCamera(Vector3f pos, Viewport view, ViewType type)
  {
    this.view = view;
    this.type = type;
    reset();
    
    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()])
    {
    case 1: 
      pitch = 90.0F;
      yaw = 0.0F;
      break;
    case 2: 
      pitch = 0.0F;
      yaw = -90.0F;
      break;
    case 3: 
      pitch = 0.0F;
      yaw = 0.0F;
      break;
    }
    
  }
  

  public float getZoomLevel()
  {
    return zoomLevel;
  }
  
  public void resize()
  {
    recalculateProjectionMatrix();
  }
  
  public void recalculateProjectionMatrix()
  {
    float right = view.sizeX / 2 * zoomLevel;
    float top = view.sizeY / 2 * zoomLevel;
    float near = 1.0F;
    float far = 131072.0F;
    
    projBuffer.put(1.0F / right);
    projBuffer.put(0.0F);
    projBuffer.put(0.0F);
    projBuffer.put(0.0F);
    
    projBuffer.put(0.0F);
    projBuffer.put(1.0F / top);
    projBuffer.put(0.0F);
    projBuffer.put(0.0F);
    
    projBuffer.put(0.0F);
    projBuffer.put(0.0F);
    projBuffer.put(-2.0F / (far - near));
    projBuffer.put(0.0F);
    
    projBuffer.put(0.0F);
    projBuffer.put(0.0F);
    projBuffer.put(-(far + near) / (far - near));
    projBuffer.put(1.0F);
    
    projBuffer.rewind();
  }
  
  public void reset()
  {
    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()]) {
    case 1: 
      setPosition(new Vector3f(0.0F, 65536.0F, 0.0F));
      break;
    case 2: 
      setPosition(new Vector3f(65536.0F, 0.0F, 0.0F));
      break;
    case 3: 
      setPosition(new Vector3f(0.0F, 0.0F, 65536.0F));
      break;
    }
    
    
    zoomLevel = 1.0F;
    recalculateProjectionMatrix();
  }
  

  public void setPosition(Vector3f newPos)
  {
    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()])
    {
    case 1: 
      super.setPosition(new Vector3f(x, 65536.0F, z));
      break;
    case 2: 
      super.setPosition(new Vector3f(65536.0F, y, z));
      break;
    case 3: 
      super.setPosition(new Vector3f(x, y, 65536.0F));
      break;
    }
    
  }
  







  public void centerOn(BoundingBox aabb)
  {
    Vector3f min = aabb.getMin();
    Vector3f max = aabb.getMax();
    float sx = (float)Math.max(x - x, 100.0D);
    float sy = (float)Math.max(y - y, 100.0D);
    float sz = (float)Math.max(z - z, 100.0D);
    
    Vector3f center = aabb.getCenter();
    
    int vx = view.maxX - view.minX;
    int vy = view.maxY - view.minY;
    

    float zoomX = 100.0F;
    float zoomY = 100.0F;
    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()])
    {
    case 1: 
      setPosition(new Vector3f(x, 65536.0F, z));
      setRotation(new Vector3f(90.0F, 0.0F, 0.0F));
      zoomX = sx * 1.2F / vx;
      zoomY = sz * 1.2F / vy;
      break;
    case 2: 
      setPosition(new Vector3f(65536.0F, y, z));
      setRotation(new Vector3f(0.0F, -90.0F, 0.0F));
      zoomX = sy * 1.2F / vy;
      zoomY = sz * 1.2F / vx;
      break;
    case 3: 
      setPosition(new Vector3f(x, y, 65536.0F));
      setRotation(new Vector3f(0.0F, 0.0F, 0.0F));
      zoomX = sx * 1.2F / vx;
      zoomY = sy * 1.2F / vy;
      break;
    }
    
    

    if (zoomX >= zoomY) {
      setZoom(zoomX);
    } else {
      setZoom(zoomY);
    }
  }
  
  private void setZoom(float newLevel) {
    if (newLevel < 0.07F) {
      zoomLevel = 0.07F;
    } else if (newLevel > 200.0F) {
      zoomLevel = 200.0F;
    } else {
      zoomLevel = newLevel;
    }
    recalculateProjectionMatrix();
  }
  
  public void handleMovementInput(float deltaTime)
  {
    int vh = 0;
    int vv = 0;
    

    float dw = Math.signum(MouseManager.getFrameDW());
    















    float dh = (Mouse.getX() - (view.minX + view.sizeX / 2)) / view.sizeX;
    float dv = (Mouse.getY() - (view.minY + view.sizeY / 2)) / view.sizeY;
    








    float dz = 1.0F + 0.1F * Math.abs(dw);
    

    if (dw > 0.0F)
    {
      zoomLevel /= dz;
      if (zoomLevel < 0.07F) {
        zoomLevel = 0.07F;
      } else {
        vh = (int)(vh + dh * zoomLevel * 100.0F);
        vv = (int)(vv - dv * zoomLevel * 100.0F);
      }
    }
    

    if (dw < 0.0F)
    {
      zoomLevel *= dz;
      if (zoomLevel > 200.0F) {
        zoomLevel = 200.0F;
      }
    }
    if (dw != 0.0F) {
      recalculateProjectionMatrix();
    }
    
    if (Keyboard.isKeyDown(17))
      vv = (int)(vv - 900.0F * deltaTime * zoomLevel);
    if (Keyboard.isKeyDown(31))
      vv = (int)(vv + 900.0F * deltaTime * zoomLevel);
    if (Keyboard.isKeyDown(30))
      vh = (int)(vh - 900.0F * deltaTime * zoomLevel);
    if (Keyboard.isKeyDown(32)) {
      vh = (int)(vh + 900.0F * deltaTime * zoomLevel);
    }
    
    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()]) {
    case 1: 
      pos.x += vh;
      pos.z += vv;
      break;
    case 2: 
      pos.z -= vh;
      pos.y -= vv;
      break;
    case 3: 
      pos.x += vh;
      pos.y -= vv;
      break;
    }
    
  }
  





  public void drawBackground()
  {
    GL11.glPushAttrib(4065);
    GL11.glDisable(3553);
    GL11.glLineWidth(1.5F);
    
    GL11.glColor3f(0.15F, 0.15F, 0.15F);
    
    GL11.glBegin(1);
    
    int hmin = 0;int hmax = 0;
    int vmin = 0;int vmax = 0;
    

    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()])
    {
    case 1: 
      hmin = (int)(pos.x - view.sizeX / 2 * zoomLevel);
      hmax = (int)(pos.x + view.sizeX / 2 * zoomLevel);
      vmin = (int)(pos.z - view.sizeY / 2 * zoomLevel);
      vmax = (int)(pos.z + view.sizeY / 2 * zoomLevel);
      break;
    
    case 2: 
      hmin = (int)(pos.z - view.sizeX / 2 * zoomLevel);
      hmax = (int)(pos.z + view.sizeX / 2 * zoomLevel);
      vmin = (int)(pos.y - view.sizeY / 2 * zoomLevel);
      vmax = (int)(pos.y + view.sizeY / 2 * zoomLevel);
      break;
    
    case 3: 
      hmin = (int)(pos.x - view.sizeX / 2 * zoomLevel);
      hmax = (int)(pos.x + view.sizeX / 2 * zoomLevel);
      vmin = (int)(pos.y - view.sizeY / 2 * zoomLevel);
      vmax = (int)(pos.y + view.sizeY / 2 * zoomLevel);
      break;
    }
    
    


    int spacing = Editor.grid.getSpacing(hmax - hmin);
    
    if (hmax > 32767) hmax = 32767;
    if (hmin < 32768) hmin = 32768;
    if (vmax > 32767) vmax = 32767;
    if (vmin < 32768) { vmin = 32768;
    }
    
    hmax = spacing * (int)Math.floor(hmax / spacing);
    hmin = spacing * (int)Math.ceil(hmin / spacing);
    vmax = spacing * (int)Math.floor(vmax / spacing);
    vmin = spacing * (int)Math.ceil(vmin / spacing);
    

    for (int i = hmin; i <= hmax; i += spacing)
    {
      if (i != 0)
      {
        switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()])
        {
        case 1: 
          GL11.glVertex3f(i, 0.0F, -32768.0F);
          GL11.glVertex3f(i, 0.0F, 32767.0F);
          break;
        
        case 2: 
          GL11.glVertex3f(0.0F, -32768.0F, i);
          GL11.glVertex3f(0.0F, 32767.0F, i);
          break;
        
        case 3: 
          GL11.glVertex3f(i, -32768.0F, 0.0F);
          GL11.glVertex3f(i, 32767.0F, 0.0F);
        }
        
      }
    }
    



    for (int i = vmin; i <= vmax; i += spacing)
    {
      if (i != 0)
      {
        switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()])
        {
        case 1: 
          GL11.glVertex3f(-32768.0F, 0.0F, i);
          GL11.glVertex3f(32767.0F, 0.0F, i);
          break;
        
        case 2: 
          GL11.glVertex3f(0.0F, i, -32768.0F);
          GL11.glVertex3f(0.0F, i, 32767.0F);
          break;
        
        case 3: 
          GL11.glVertex3f(-32768.0F, i, 0.0F);
          GL11.glVertex3f(32767.0F, i, 0.0F);
        }
        
      }
    }
    


    GL11.glEnd();
    

    GL11.glLineWidth(2.0F);
    GL11.glColor3f(0.35F, 0.35F, 0.35F);
    GL11.glBegin(3);
    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()])
    {
    case 1: 
      GL11.glVertex3f(-32768.0F, 0.0F, 32767.0F);
      GL11.glVertex3f(32767.0F, 0.0F, 32767.0F);
      GL11.glVertex3f(32767.0F, 0.0F, -32768.0F);
      GL11.glVertex3f(-32768.0F, 0.0F, -32768.0F);
      GL11.glVertex3f(-32768.0F, 0.0F, 32767.0F);
      break;
    
    case 2: 
      GL11.glVertex3f(0.0F, -32768.0F, 32767.0F);
      GL11.glVertex3f(0.0F, 32767.0F, 32767.0F);
      GL11.glVertex3f(0.0F, 32767.0F, -32768.0F);
      GL11.glVertex3f(0.0F, -32768.0F, -32768.0F);
      GL11.glVertex3f(0.0F, -32768.0F, 32767.0F);
      break;
    
    case 3: 
      GL11.glVertex3f(-32768.0F, 32767.0F, 0.0F);
      GL11.glVertex3f(32767.0F, 32767.0F, 0.0F);
      GL11.glVertex3f(32767.0F, -32768.0F, 0.0F);
      GL11.glVertex3f(-32768.0F, -32768.0F, 0.0F);
      GL11.glVertex3f(-32768.0F, 32767.0F, 0.0F);
      break;
    }
    
    


    GL11.glEnd();
    GL11.glPopAttrib();
  }
  
  public PickRay getPickRay()
  {
    FloatBuffer clickBuffer = getMousePosition(false);
    Vector3f clickPosition = new Vector3f();
    x = clickBuffer.get();
    y = clickBuffer.get();
    z = clickBuffer.get();
    
    Vector3f origin = null;
    Vector3f direction = null;
    
    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()]) {
    case 1: 
      origin = new Vector3f(x, pos.y, z);
      direction = new Vector3f(0.0F, -1.0F, 0.0F);
      break;
    case 2: 
      origin = new Vector3f(pos.x, y, z);
      direction = new Vector3f(-1.0F, 0.0F, 0.0F);
      break;
    case 3: 
      origin = new Vector3f(x, y, pos.z);
      direction = new Vector3f(0.0F, 0.0F, -1.0F);
      break;
    }
    
    

    return new PickRay(origin, direction, view);
  }
  

  public Vector3f getForward(float length)
  {
    Vector3f vec = null;
    
    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()])
    {
    case 3: 
      vec = new Vector3f(0.0F, 0.0F, -length);
      break;
    case 2: 
      vec = new Vector3f(length, 0.0F, 0.0F);
      break;
    case 1: 
      vec = new Vector3f(0.0F, length, 0.0F);
      break;
    }
    
    

    return vec;
  }
  

  public Vector3f getUp(float length)
  {
    Vector3f vec = null;
    
    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()])
    {
    case 3: 
      vec = new Vector3f(0.0F, length, 0.0F);
      break;
    case 2: 
      vec = new Vector3f(0.0F, length, 0.0F);
      break;
    case 1: 
      vec = new Vector3f(0.0F, 0.0F, -length);
      break;
    }
    
    

    return vec;
  }
  

  public Vector3f getRight(float length)
  {
    Vector3f vec = null;
    
    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()])
    {
    case 3: 
      vec = new Vector3f(length, 0.0F, 0.0F);
      break;
    case 2: 
      vec = new Vector3f(0.0F, 0.0F, -length);
      break;
    case 1: 
      vec = new Vector3f(length, 0.0F, 0.0F);
      break;
    }
    
    

    return vec;
  }
  
  public Vector3f getTranslationVector(int dx, int dy)
  {
    Vector3f vec = null;
    
    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()])
    {
    case 3: 
      vec = new Vector3f(zoomLevel * dx, zoomLevel * dy, 0.0F);
      break;
    case 2: 
      vec = new Vector3f(0.0F, zoomLevel * dy, -zoomLevel * dx);
      break;
    case 1: 
      vec = new Vector3f(zoomLevel * dx, 0.0F, -zoomLevel * dy);
      break;
    }
    
    

    return vec;
  }
  
  public Axis getRotationAxis()
  {
    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()])
    {
    case 3: 
      return Axis.Z;
    case 2: 
      return Axis.X;
    case 1: 
      return Axis.Y;
    }
    return Axis.X;
  }
}
