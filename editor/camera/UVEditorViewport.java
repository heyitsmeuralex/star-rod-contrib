package editor.camera;

import editor.Editor;
import editor.Editor.Mode;
import editor.map.BoundingBox;
import editor.map.mesh.TexturedMesh;
import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.Model;
import editor.map.shape.UV;
import editor.render.Renderer;
import editor.render.ShaderManager;
import editor.selection.Selection;
import editor.selection.SelectionManager;
import java.util.LinkedList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Vector3f;



























public class UVEditorViewport
  extends Viewport
{
  private Model mdl = null;
  private List<Triangle> triangles = new LinkedList();
  
  public UVEditorViewport()
  {
    super(ViewType.FRONT);
    wireframeMode = true;
    camera = new OrthographicCamera(this, ViewType.FRONT);
  }
  
  public UVEditorViewport(int minX, int minY, int maxX, int maxY)
  {
    super(ViewType.FRONT, minX, minY, maxX, maxY);
    wireframeMode = true;
    camera = new OrthographicCamera(this, ViewType.FRONT);
  }
  
  public void setModel(Model mdl)
  {
    this.mdl = mdl;
  }
  
  public boolean setTriangles(List<Triangle> newTriangles)
  {
    boolean changed = triangles.size() != newTriangles.size();
    if (!changed)
    {
      for (int i = 0; i < triangles.size(); i++)
      {
        changed = !((Triangle)triangles.get(i)).equals(newTriangles.get(i));
        if (changed) {
          break;
        }
      }
    }
    triangles = newTriangles;
    
    if (changed)
    {
      BoundingBox aabb = new BoundingBox();
      for (Triangle t : newTriangles) {
        for (Vertex v : vert)
          aabb.encompass(uv.getU(), uv.getV(), 0);
      }
      camera.centerOn(aabb);
    }
    
    return changed;
  }
  

  public float getScaleFactor(float x, float y, float z)
  {
    return 2.0F * ((OrthographicCamera)camera).getZoomLevel();
  }
  

  public void render(boolean isActive)
  {
    setViewport();
    camera.setView();
    camera.applyTransform();
    
    if ((mdl != null) && (mdl.getMesh().textured)) {
      drawBackground();
    }
    if (Editor.gridEnabled)
    {
      GL11.glDisable(2929);
      camera.drawBackground();
      GL11.glEnable(2929);
    }
    
    GL11.glClear(256);
    
    if (triangles != null) {
      drawUVs();
    }
    if (Editor.showAxes) {
      Renderer.drawAxes(1.0F);
    }
    if (Editor.getEditorMode() == Editor.Mode.UvEdit) {
      selectionManageruvSelection.render(this);
    }
    if (Editor.draggingBox) {
      Renderer.renderSelectionBox(Editor.dragBoxStartPoint, Editor.dragBoxEndPoint);
    }
  }
  
  private void drawBackground() {
    GL11.glPushAttrib(265001);
    GL11.glPolygonMode(1032, 6914);
    
    GL11.glEnable(3553);
    GL20.glUseProgram(ShaderManager.modelShader);
    GL20.glUniform1i(ShaderManager.model_selected, 0);
    mdl.setShaderTextureParameters();
    
    Vector3f pos = camera.getPosition();
    float zoom = ((OrthographicCamera)camera).getZoomLevel();
    float fovX = sizeX / 2.0F * zoom;
    float fovY = sizeY / 2.0F * zoom;
    

    float uMin = x - fovX;
    float uMax = x + fovX;
    float vMin = y - fovY;
    float vMax = y + fovY;
    if (uMin < -32768.0F) uMin = -32768.0F;
    if (uMax > 32767.0F) uMax = 32767.0F;
    if (vMin < -32768.0F) vMin = -32768.0F;
    if (vMax > 32767.0F) { vMax = 32767.0F;
    }
    GL11.glColor3f(1.0F, 1.0F, 1.0F);
    GL11.glBegin(7);
    
    GL11.glTexCoord2f(uMin, vMax);
    GL11.glVertex3f(uMin, vMax, -1.0F);
    
    GL11.glTexCoord2f(uMax, vMax);
    GL11.glVertex3f(uMax, vMax, -1.0F);
    
    GL11.glTexCoord2f(uMax, vMin);
    GL11.glVertex3f(uMax, vMin, -1.0F);
    
    GL11.glTexCoord2f(uMin, vMin);
    GL11.glVertex3f(uMin, vMin, -1.0F);
    
    GL11.glEnd();
    GL20.glUseProgram(0);
    GL11.glPopAttrib();
  }
  
  private void drawUVs()
  {
    GL11.glPushAttrib(265009);
    GL11.glDisable(3553);
    
    GL11.glPointSize(10.0F);
    GL11.glLineWidth(2.0F);
    GL11.glPolygonMode(1032, 6913);
    
    GL11.glColor3f(1.0F, 1.0F, 1.0F);
    GL11.glBegin(4);
    for (Triangle t : triangles)
      for (Vertex v : vert)
        GL11.glVertex3f(uv.getU(), uv.getV(), 1.0F);
    GL11.glEnd();
    
    GL11.glBegin(0);
    for (Triangle t : triangles)
      for (Vertex v : vert)
        drawUVCoordinate(uv);
    GL11.glEnd();
    
    GL11.glPopAttrib();
  }
  
  private void drawUVCoordinate(UV uv)
  {
    if (selected) {
      GL11.glColor3f(1.0F, 0.0F, 0.0F);
    } else {
      GL11.glColor3f(1.0F, 1.0F, 0.0F);
    }
    GL11.glVertex3f(uv.getU(), uv.getV(), 2.0F);
  }
  

  public boolean allowsDragSelection()
  {
    return true;
  }
  

  public BoundingBox getDragSelectionVolume(Vector3f start, Vector3f end)
  {
    BoundingBox selectionVolume = new BoundingBox();
    Vector3f clampedStart = new Vector3f(start);
    Vector3f clampedEnd = new Vector3f(end);
    
    z = Float.MAX_VALUE;
    z = -3.4028235E38F;
    
    selectionVolume.encompass((int)x, (int)y, (int)z);
    selectionVolume.encompass((int)x, (int)y, (int)z);
    
    return selectionVolume;
  }
}
