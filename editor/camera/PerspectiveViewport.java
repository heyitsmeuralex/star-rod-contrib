package editor.camera;

import editor.Editor;
import editor.Editor.Mode;
import editor.PaintManager;
import editor.map.BoundingBox;
import editor.map.MapObject;
import editor.map.hit.CameraController;
import editor.render.Renderer;
import editor.render.Renderer.RenderMode;
import editor.selection.PickRay.PickHit;
import editor.selection.Selection;
import editor.selection.SelectionManager;
import editor.selection.SelectionManager.SelectionMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

public class PerspectiveViewport extends Viewport
{
  public PerspectiveViewport()
  {
    super(ViewType.PERSPECTIVE);
    wireframeMode = false;
    camera = new PerspectiveCamera(this);
  }
  
  public PerspectiveViewport(int minX, int minY, int maxX, int maxY)
  {
    super(ViewType.PERSPECTIVE, minX, minY, maxX, maxY);
    wireframeMode = false;
    camera = new PerspectiveCamera(this);
  }
  

  public float getScaleFactor(float x, float y, float z)
  {
    float dx = camera.pos.x - x;
    float dy = camera.pos.y - y;
    float dz = camera.pos.z - z;
    
    float distance = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
    return 0.06F + (float)Math.pow(distance / 120.0D, 0.8D);
  }
  

  public void render(boolean isActive)
  {
    Editor.Mode editorMode = Editor.getEditorMode();
    
    setViewport();
    GL11.glClear(16384);
    
    if (!wireframeMode) {
      camera.drawBackground();
    }
    GL11.glClear(256);
    camera.setView();
    camera.applyTransform();
    Renderer.RenderMode renderMode;
    Renderer.RenderMode renderMode;
    if (editorMode == Editor.Mode.VertexPaint) {
      renderMode = wireframeMode ? Renderer.RenderMode.WIREFRAME : PaintManager.getRenderMode();
    } else {
      renderMode = wireframeMode ? Renderer.RenderMode.WIREFRAME : Renderer.RenderMode.TEXTURED;
    }
    Renderer.drawModels(Editor.map, renderMode);
    switch (1.$SwitchMap$editor$Editor$Mode[editorMode.ordinal()])
    {
    case 1: 
    case 2: 
    case 3: 
      Renderer.drawColliders(Editor.map);
      Renderer.drawZones(Editor.map);
      Renderer.drawMarkers(Editor.map, 1.0F, true);
      break;
    }
    
    
    if (Editor.selectionManager.getSelectionMode() == SelectionManager.SelectionMode.VERTEX) {
      Renderer.drawVerticies(Editor.selectionManager.getVerticies());
    }
    
    CameraController cam = Editor.getPreviewController();
    switch (1.$SwitchMap$editor$Editor$Mode[editorMode.ordinal()])
    {
    case 1: 
      selectionManagercurrentSelection.render(this);
      

      break;
    case 4: 
      if ((isActive) && (Editor.paintPickHit != null) && (!Editor.paintPickHit.missed()))
        Renderer.drawPaintSphere(paintPickHitpoint);
      break;
    case 2: 
      selectionManagerpointSelection.render(this);
      break;
    case 3: 
      selectionManagerpointSelection.render(this);
      if ((cam != null) && (parent.hasCamera())) {
        cam.drawHelpers(true);
      }
      break;
    }
    
    if (Editor.previewTriangles) {
      Renderer.drawPrimitivePreview(Editor.getPreviewTriangles());
    }
    if (Editor.showAxes) {
      Renderer.drawAxes(2.0F);
    }
  }
  
  public boolean allowsDragSelection()
  {
    return false;
  }
  

  public BoundingBox getDragSelectionVolume(Vector3f start, Vector3f end)
  {
    return null;
  }
}
