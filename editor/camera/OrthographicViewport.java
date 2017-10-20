package editor.camera;

import editor.Editor;
import editor.Editor.Mode;
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


public class OrthographicViewport
  extends Viewport
{
  public OrthographicViewport(ViewType type)
  {
    super(type);
    wireframeMode = true;
    camera = new OrthographicCamera(this, type);
  }
  
  public OrthographicViewport(int minX, int minY, int maxX, int maxY, ViewType type)
  {
    super(type, minX, minY, maxX, maxY);
    wireframeMode = true;
    camera = new OrthographicCamera(this, type);
  }
  

  public float getScaleFactor(float x, float y, float z)
  {
    return 2.0F * ((OrthographicCamera)camera).getZoomLevel();
  }
  

  public void render(boolean isActive)
  {
    Editor.Mode editorMode = Editor.getEditorMode();
    
    setViewport();
    camera.setView();
    camera.applyTransform();
    
    if (Editor.gridEnabled) {
      camera.drawBackground();
    }
    GL11.glClear(256);
    
    Renderer.RenderMode mode = wireframeMode ? Renderer.RenderMode.WIREFRAME : Renderer.RenderMode.TEXTURED;
    
    Renderer.drawModels(Editor.map, mode);
    switch (1.$SwitchMap$editor$Editor$Mode[editorMode.ordinal()])
    {
    case 1: 
    case 2: 
    case 3: 
      Renderer.drawColliders(Editor.map);
      Renderer.drawZones(Editor.map);
      
      float zoomLevel = ((OrthographicCamera)camera).getZoomLevel();
      float alpha = zoomLevel > 0.5F ? 1.0F : 2.0F * zoomLevel;
      Renderer.drawMarkers(Editor.map, alpha, false);
    }
    
    
    if (Editor.selectionManager.getSelectionMode() == SelectionManager.SelectionMode.VERTEX) {
      Renderer.drawVerticies(Editor.selectionManager.getVerticies());
    }
    
    CameraController cam = Editor.getPreviewController();
    switch (1.$SwitchMap$editor$Editor$Mode[editorMode.ordinal()])
    {
    case 1: 
      selectionManagercurrentSelection.render(this);
      if ((cam != null) && (parent.hasCamera()))
        cam.drawHelpers(false);
      break;
    case 4: 
      if ((isActive) && (Editor.paintPickHit != null) && (!Editor.paintPickHit.missed()))
        Renderer.drawPaintSphere(paintPickHitpoint);
      break;
    case 2: 
      GL11.glPushAttrib(2929);
      GL11.glDisable(2929);
      selectionManagerpointSelection.render(this);
      GL11.glPopAttrib();
      break;
    case 3: 
      GL11.glPushAttrib(2929);
      GL11.glDisable(2929);
      selectionManagerpointSelection.render(this);
      if ((cam != null) && (parent.hasCamera()))
        cam.drawHelpers(true);
      GL11.glPopAttrib();
      break;
    }
    
    
    if (Editor.previewTriangles) {
      Renderer.drawPrimitivePreview(Editor.getPreviewTriangles());
    }
    if (Editor.draggingBox) {
      Renderer.renderSelectionBox(Editor.dragBoxStartPoint, Editor.dragBoxEndPoint);
    }
    if (Editor.showAxes) {
      Renderer.drawAxes(1.0F);
    }
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
    
    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()])
    {
    case 1: 
      y = Float.MAX_VALUE;
      y = -3.4028235E38F;
      break;
    case 2: 
      x = Float.MAX_VALUE;
      x = -3.4028235E38F;
      break;
    case 3: 
      z = Float.MAX_VALUE;
      z = -3.4028235E38F;
    }
    
    
    selectionVolume.encompass((int)x, (int)y, (int)z);
    selectionVolume.encompass((int)x, (int)y, (int)z);
    
    return selectionVolume;
  }
}
