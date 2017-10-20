package editor.render;

import editor.Editor;
import editor.Editor.Mode;
import editor.PaintManager;
import editor.map.BoundingBox;
import editor.map.Map;
import editor.map.MapObject;
import editor.map.hit.Collider;
import editor.map.hit.Zone;
import editor.map.marker.Marker;
import editor.map.mesh.AbstractMesh;
import editor.map.mesh.TexturedMesh;
import editor.map.mesh.TexturedMesh.DisplayListModel;
import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.Model;
import editor.map.shape.TriangleBatch;
import editor.map.shape.UV;
import editor.map.shape.commands.DisplayCommand;
import editor.map.tree.MapObjectNode;
import editor.map.tree.MapObjectTreeModel;
import editor.selection.SelectionManager;
import editor.selection.SelectionManager.SelectionMode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;
import util.Logger;
import util.Priority;






















































public abstract class Renderer
{
  private static final float NORMAL_LENGTH = 16.0F;
  private static final int SELECTED = 1;
  private static final int DOUBLE_SIDED = 2;
  public Renderer() {}
  
  public static enum RenderMode
  {
    TEXTURED,  SHADED,  WIREFRAME;
    

    private RenderMode() {}
  }
  

  private static boolean useGeometryFlags = false;
  
  private static final Vector4f[] colliderColors = { new Vector4f(0.0F, 1.0F, 1.0F, 0.2F), new Vector4f(0.0F, 1.0F, 0.0F, 0.5F), new Vector4f(0.0F, 0.4F, 1.0F, 0.2F), new Vector4f(0.0F, 1.0F, 0.0F, 0.8F) };
  








  private static final Vector4f[] zoneColors = { new Vector4f(1.0F, 0.0F, 1.0F, 0.2F), new Vector4f(1.0F, 1.0F, 0.0F, 0.5F), new Vector4f(1.0F, 0.0F, 0.4F, 0.2F), new Vector4f(1.0F, 1.0F, 0.0F, 0.8F) };
  








  public static void initializeOpenGL()
  {
    Logger.logf("Initializing OpenGL (version %s)", new Object[] { GL11.glGetString(7938) });
    
    ShaderManager.loadShaders();
    
    FloatBuffer ambientLightModel = BufferUtils.createFloatBuffer(4);
    ambientLightModel.put(1.0F).put(1.0F).put(1.0F).put(1.0F).flip();
    
    GL11.glLineWidth(1.0F);
    
    GL11.glShadeModel(7425);
    GL11.glLightModel(2899, ambientLightModel);
    
    GL11.glEnable(2903);
    GL11.glColorMaterial(1028, 4608);
    
    GL11.glEnable(3553);
    
    GL11.glEnable(2832);
    GL11.glHint(2832, 4354);
    
    GL11.glEnable(2848);
    GL11.glHint(3154, 4354);
    
    GL11.glEnable(3042);
    GL11.glBlendFunc(770, 771);
    
    GL11.glEnable(3008);
    GL11.glAlphaFunc(516, 0.0F);
    
    GL11.glEnable(2929);
    GL11.glDepthFunc(515);
    
    GL11.glCullFace(1029);
    
    if (getCapabilitiesGL_ARB_depth_clamp) {
      GL11.glEnable(34383);
    }
  }
  

  private static double nextUpdate = Double.MIN_VALUE;
  private static double omega = 3.9269908169872414D;
  public static short stipplePattern = 255;
  public static float color = 0.0F;
  
  public static void update(double time)
  {
    if (time >= nextUpdate)
    {

      stipplePattern = (short)((stipplePattern & 0xFFFF) >>> 1 | (stipplePattern & 0xFFFF) << 15);
      nextUpdate = time + 0.05D;
      
      color = 0.5F * (float)Math.sin(omega * time);
      color = 0.5F + color * color;
    }
  }
  
  public static void drawColliders(Map map)
  {
    drawTranslucentFaces(colliderTree, colliderColors);
    drawOutlines(colliderTree, colliderColors);
    
    for (Collider c : colliderTree)
    {
      if (c.shouldDraw())
      {
        if (Editor.showBoundingBoxes) {
          AABB.render();
        }
        if (Editor.showNormals)
          drawNormals(mesh);
      }
    }
  }
  
  public static void drawZones(Map map) {
    drawTranslucentFaces(zoneTree, zoneColors);
    drawOutlines(zoneTree, zoneColors);
    
    for (Zone z : zoneTree)
    {
      if (z.shouldDraw())
      {
        if (Editor.showBoundingBoxes) {
          AABB.render();
        }
        if (Editor.showNormals)
          drawNormals(mesh);
      }
    }
  }
  
  public static void drawMarkers(Map map, float alpha, boolean drawHiddenPaths) {
    if (alpha < 1.0F) {
      GL11.glDepthMask(false);
    }
    for (Marker m : markerTree)
    {
      if (m.shouldDraw())
      {
        m.render(alpha, drawHiddenPaths);
      }
    }
    GL11.glDepthMask(true);
  }
  
  private static <T extends MapObject> void drawTranslucentFaces(Iterable<T> objects, Vector4f[] colors)
  {
    GL11.glPushAttrib(262152);
    GL11.glPolygonMode(1032, 6914);
    GL11.glDisable(3553);
    GL11.glDepthMask(false);
    
    SelectionManager.SelectionMode selectionMode = Editor.selectionManager.getSelectionMode();
    

    GL11.glBegin(4);
    for (Iterator localIterator1 = objects.iterator(); localIterator1.hasNext();) { obj = (MapObject)localIterator1.next();
      
      if (obj.shouldDraw())
      {
        for (Triangle t : obj.getMesh()) { boolean selected;
          boolean selected;
          boolean selected; switch (selectionMode) {
          case OBJECT: 
            selected = selected; break;
          case TRIANGLE:  selected = selected; break;
          default:  selected = false;
          }
          
          int index = (selected ? 1 : 0) + (doubleSided ? 2 : 0);
          GL11.glColor4f(x, y, z, w);
          
          for (Vertex v : vert)
            GL11.glVertex3f(v.getCurrentX(), v.getCurrentY(), v.getCurrentZ());
        } } }
    MapObject obj;
    GL11.glEnd();
    
    GL11.glDepthMask(true);
    GL11.glPopAttrib();
  }
  
  private static <T extends MapObject> void drawOutlines(Iterable<T> objects, Vector4f[] colors)
  {
    GL11.glPushAttrib(265001);
    GL11.glPolygonMode(1032, 6913);
    GL11.glDisable(3553);
    GL11.glLineWidth(1.0F);
    
    SelectionManager.SelectionMode selectionMode = Editor.selectionManager.getSelectionMode();
    

    GL11.glBegin(4);
    for (Iterator localIterator1 = objects.iterator(); localIterator1.hasNext();) { obj = (MapObject)localIterator1.next();
      
      if (obj.shouldDraw())
      {
        for (Triangle t : obj.getMesh()) { boolean selected;
          boolean selected;
          boolean selected; switch (selectionMode) {
          case OBJECT: 
            selected = selected; break;
          case TRIANGLE:  selected = selected; break;
          default:  selected = false;
          }
          
          int index = (selected ? 1 : 0) + (doubleSided ? 2 : 0);
          GL11.glColor3f(x, y, z);
          
          for (Vertex v : vert)
            GL11.glVertex3f(v.getCurrentX(), v.getCurrentY(), v.getCurrentZ());
        } } }
    MapObject obj;
    GL11.glEnd();
    
    GL11.glPopAttrib();
  }
  
  public static void drawVerticies(Iterable<Vertex> verticies)
  {
    GL11.glPushAttrib(273177);
    GL11.glPolygonMode(1032, 6912);
    GL11.glDisable(3553);
    
    GL11.glEnable(10753);
    GL11.glPolygonOffset(-1.5F, -1.5F);
    GL11.glPointSize(8.0F);
    
    GL11.glBegin(0);
    for (Vertex v : verticies) {
      drawVertex(v);
    }
    for (Vertex v : Editor.tempVerticies)
      drawVertex(v);
    GL11.glEnd();
    
    GL11.glPopAttrib();
  }
  
  private static void drawVertex(Vertex v)
  {
    if (selected) {
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
    } else {
      GL11.glColor3f(1.0F, 1.0F, 0.0F);
    }
    GL11.glVertex3f(v.getCurrentX(), v.getCurrentY(), v.getCurrentZ());
  }
  
  private static Comparator<MapObject> DEPTH_COMPARATOR = new Comparator()
  {
    public int compare(MapObject obj1, MapObject obj2)
    {
      return distance - distance;
    }
  };
  


  public static void drawModels(Map map, RenderMode mode)
  {
    useGeometryFlags = Editor.useGeometryFlags;
    
    SortedSet<Model> nodeset = new TreeSet(DEPTH_COMPARATOR);
    Stack<MapObjectNode<Model>> nodes = new Stack();
    nodes.push(modelTree.getRoot());
    
    while (!nodeset.isEmpty()) {}
    



    while (!nodes.isEmpty())
    {
      MapObjectNode<Model> node = (MapObjectNode)nodes.pop();
      Model mdl = (Model)node.getUserObject();
      
      if ((mdl.shouldDraw()) && (!useAlpha(renderMode))) {
        drawModel(mdl, mode);
      }
      for (int i = 0; i < node.getChildCount(); i++) {
        nodes.push(node.getChildAt(i));
      }
    }
    nodes.push(modelTree.getRoot());
    
    GL11.glDepthMask(false);
    GL11.glBlendFunc(770, 771);
    


    while (!nodes.isEmpty())
    {
      MapObjectNode<Model> node = (MapObjectNode)nodes.pop();
      Model mdl = (Model)node.getUserObject();
      
      if ((mdl.shouldDraw()) && (useAlpha(renderMode))) {
        drawModel(mdl, mode);
      }
      for (int i = 0; i < node.getChildCount(); i++) {
        nodes.push(node.getChildAt(i));
      }
    }
    GL11.glDepthMask(true);
  }
  
  private static boolean useAlpha(int renderMode)
  {
    switch (renderMode)
    {
    case 17: 
    case 19: 
    case 20: 
    case 26: 
    case 34: 
    case 46: 
      return true;
    }
    return false;
  }
  

  private static void drawModel(Model mdl, RenderMode mode)
  {
    switch (2.$SwitchMap$editor$render$Renderer$RenderMode[mode.ordinal()])
    {
    case 1: 
      drawModelWireframe(mdl);
      break;
    
    case 2: 
      drawModelFilled(mdl, true);
      break;
    
    case 3: 
      drawModelFilled(mdl, false);
    }
    
    
    if (Editor.showBoundingBoxes) {
      AABB.render();
    }
    if (Editor.showNormals) {
      drawNormals(mdl.getMesh());
    }
  }
  
  private static void drawModelWireframe(Model mdl) {
    GL11.glPushAttrib(265001);
    GL11.glDisable(3553);
    GL11.glLineWidth(1.0F);
    
    SelectionManager.SelectionMode selectionMode = Editor.selectionManager.getSelectionMode();
    


    GL11.glDepthMask(false);
    GL11.glPolygonMode(1032, 6914);
    GL11.glColor4f(0.8F, 0.0F, 0.0F, 0.5F);
    
    GL11.glBegin(4);
    
    for (Triangle t : mdl.getMesh()) { boolean selected;
      boolean selected;
      boolean selected; switch (selectionMode) {
      case OBJECT: 
        selected = mdl.selected; break;
      case TRIANGLE:  selected = selected; break;
      default:  selected = false;
      }
      
      if (selected)
      {
        for (Vertex v : vert)
          GL11.glVertex3f(v.getCurrentX(), v.getCurrentY(), v.getCurrentZ());
      }
    }
    GL11.glEnd();
    GL11.glDepthMask(true);
    

    GL11.glPolygonMode(1032, 6913);
    GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
    
    GL11.glBegin(4);
    for (Triangle t : mdl.getMesh()) { boolean selected;
      boolean selected;
      boolean selected; switch (selectionMode) {
      case OBJECT: 
        selected = mdl.selected; break;
      case TRIANGLE:  selected = selected; break;
      default:  selected = false;
      }
      
      if (selected)
      {
        GL11.glColor4f(1.0F, 0.0F, 0.0F, 1.0F);
        for (Vertex v : vert) {
          GL11.glVertex3f(v.getCurrentX(), v.getCurrentY(), v.getCurrentZ());
        }
      } else {
        drawTexturedTriangle(t);
      } }
    GL11.glEnd();
    
    GL11.glPopAttrib();
  }
  
  private static void drawModelFilled(Model mdl, boolean textured)
  {
    GL11.glPushAttrib(270412);
    GL11.glPolygonMode(1032, 6914);
    

    GL20.glUseProgram(ShaderManager.modelShader);
    
    if (textured) {
      mdl.setShaderTextureParameters();
    } else {
      GL20.glUniform1i(ShaderManager.model_textured, 0);
    }
    SelectionManager.SelectionMode selectionMode = Editor.selectionManager.getSelectionMode();
    DisplayCommand cmd;
    Triangle t;
    boolean hasSelectedTriangles; Object cmd; Triangle t; if (Editor.getEditorMode() == Editor.Mode.Modify)
    {
      Iterator localIterator1;
      if (selectionMode == SelectionManager.SelectionMode.OBJECT)
      {
        if (mdl.selected) {
          GL20.glUniform1i(ShaderManager.model_selected, 1);
        } else {
          GL20.glUniform1i(ShaderManager.model_selected, 0);
        }
        for (localIterator1 = getMeshdisplayListModel.iterator(); localIterator1.hasNext();) { cmd = (DisplayCommand)localIterator1.next();
          
          if ((cmd instanceof TriangleBatch))
          {
            GL11.glBegin(4);
            for (Iterator localIterator3 = triangles.iterator(); localIterator3.hasNext();) { t = (Triangle)localIterator3.next();
              drawTexturedTriangle(t); }
            GL11.glEnd();
          } else if (useGeometryFlags) {
            cmd.doGL();
          }
          
        }
      }
      else
      {
        GL20.glUniform1i(ShaderManager.model_selected, 0);
        hasSelectedTriangles = false;
        
        for (DisplayCommand cmd : getMeshdisplayListModel)
        {
          if ((cmd instanceof TriangleBatch))
          {
            GL11.glBegin(4);
            for (Triangle t : triangles)
            {
              if (!selected) {
                drawTexturedTriangle(t);
              } else
                hasSelectedTriangles = true;
            }
            GL11.glEnd();
          } else if (useGeometryFlags) {
            cmd.doGL();
          }
        }
        

        if (hasSelectedTriangles)
        {
          GL20.glUniform1i(ShaderManager.model_selected, 1);
          
          for (cmd = getMeshdisplayListModel.iterator(); cmd.hasNext();) { cmd = (DisplayCommand)cmd.next();
            
            if ((cmd instanceof TriangleBatch))
            {
              GL11.glBegin(4);
              for (t = triangles.iterator(); t.hasNext();) { t = (Triangle)t.next();
                if (selected)
                  drawTexturedTriangle(t); }
              GL11.glEnd();
            } else if (useGeometryFlags) {
              ((DisplayCommand)cmd).doGL();
            }
            
          }
        }
      }
    }
    else
    {
      GL20.glUniform1i(ShaderManager.model_selected, 0);
      
      for (DisplayCommand cmd : getMeshdisplayListModel)
      {
        if ((cmd instanceof TriangleBatch))
        {
          GL11.glBegin(4);
          for (cmd = triangles.iterator(); ((Iterator)cmd).hasNext();) { t = (Triangle)((Iterator)cmd).next();
            drawTexturedTriangle(t); }
          GL11.glEnd();
        } else if (useGeometryFlags) {
          cmd.doGL();
        }
      }
    }
    Triangle t;
    GL20.glUseProgram(0);
    
    GL11.glPopAttrib();
    GL11.glPushAttrib(8200);
    

    GL11.glDisable(2884);
    GL11.glDisable(3553);
    GL11.glEnable(10754);
    GL11.glPolygonMode(1032, 6913);
    GL11.glLineWidth(1.0F);
    
    GL11.glBegin(4);
    for (Triangle t : mdl.getMesh()) { boolean selected;
      boolean selected;
      boolean selected; switch (selectionMode) {
      case OBJECT: 
        selected = mdl.selected; break;
      case TRIANGLE:  selected = selected; break;
      default:  selected = (selected) || (mdl.selected);
      }
      
      if (selected)
      {
        GL11.glColor3f(1.0F, 0.0F, 0.0F);
        cmd = vert;t = cmd.length; for (t = 0; t < t; t++) { Vertex v = cmd[t];
          GL11.glVertex3f(v.getCurrentX(), v.getCurrentY(), v.getCurrentZ());
        }
      } else if (Editor.edgeHighlights)
      {
        GL11.glColor3f(0.0F, 0.0F, 1.0F);
        cmd = vert;t = cmd.length; for (t = 0; t < t; t++) { Vertex v = cmd[t];
          GL11.glVertex3f(v.getCurrentX(), v.getCurrentY(), v.getCurrentZ());
        }
      } }
    GL11.glEnd();
    
    GL11.glPopAttrib();
  }
  
  private static void drawTexturedTriangle(Triangle t)
  {
    for (Vertex v : vert)
    {
      float r = (r & 0xFF) / 255.0F;
      float g = (g & 0xFF) / 255.0F;
      float b = (b & 0xFF) / 255.0F;
      float a = (a & 0xFF) / 255.0F;
      
      GL11.glColor4f(r, g, b, a);
      
      GL11.glTexCoord2f(uv.getU(), uv.getV());
      GL11.glVertex3f(v.getCurrentX(), v.getCurrentY(), v.getCurrentZ());
    }
  }
  
  private static void drawNormals(AbstractMesh mesh)
  {
    GL11.glPushAttrib(265001);
    GL11.glDisable(3553);
    
    GL11.glLineWidth(1.0F);
    GL11.glPointSize(3.0F);
    
    GL11.glColor3f(1.0F, 1.0F, 0.0F);
    
    GL11.glBegin(1);
    for (Triangle t : mesh)
    {
      float[] centroid = t.getCentroid();
      float[] normal = t.getNormal();
      
      if (normal != null)
      {

        GL11.glVertex3f(centroid[0], centroid[1], centroid[2]);
        GL11.glVertex3f(centroid[0] + 16.0F * normal[0], centroid[1] + 16.0F * normal[1], centroid[2] + 16.0F * normal[2]);
        



        if (doubleSided)
        {
          GL11.glVertex3f(centroid[0], centroid[1], centroid[2]);
          GL11.glVertex3f(centroid[0] - 16.0F * normal[0], centroid[1] - 16.0F * normal[1], centroid[2] - 16.0F * normal[2]);
        }
      }
    }
    

    GL11.glEnd();
    
    GL11.glPopAttrib();
  }
  
  public static void drawStipple(Vector3f a, Vector3f b, float dashLength)
  {
    drawStipple(x, y, z, x, y, z, dashLength);
  }
  
  public static void drawStipple(float ax, float ay, float az, float bx, float by, float bz, float dashLength)
  {
    float dx = bx - ax;
    float dy = by - ay;
    float dz = bz - az;
    
    float len = (float)Math.sqrt(dx * dx + dy * dy + dz * dz);
    int n = Math.round(len / dashLength);
    if (n > 200) n = 200;
    if (n % 2 == 0) { n++;
    }
    dx /= n;
    dy /= n;
    dz /= n;
    
    GL11.glBegin(1);
    for (int i = 0; i <= n; i++)
      GL11.glVertex3f(ax + i * dx, ay + i * dy, az + i * dz);
    GL11.glEnd();
  }
  

  public static void drawAxes(float lineWidth)
  {
    GL11.glPushAttrib(4065);
    GL11.glDisable(3553);
    GL11.glLineWidth(lineWidth);
    
    GL11.glBegin(1);
    
    GL11.glColor3f(1.0F, 0.0F, 0.0F);
    GL11.glVertex3f(-32768.0F, 0.0F, 0.0F);
    GL11.glVertex3f(32767.0F, 0.0F, 0.0F);
    
    GL11.glColor3f(0.0F, 1.0F, 0.0F);
    GL11.glVertex3f(0.0F, -32768.0F, 0.0F);
    GL11.glVertex3f(0.0F, 32767.0F, 0.0F);
    
    GL11.glColor3f(0.0F, 0.0F, 1.0F);
    GL11.glVertex3f(0.0F, 0.0F, -32768.0F);
    GL11.glVertex3f(0.0F, 0.0F, 32767.0F);
    
    GL11.glEnd();
    GL11.glPopAttrib();
  }
  
  public static void drawPaintSphere(Vector3f center)
  {
    GL11.glPushAttrib(4089);
    GL11.glPolygonMode(1032, 6913);
    GL11.glDisable(3553);
    
    GL11.glLineWidth(1.0F);
    GL11.glPointSize(10.0F);
    
    GL11.glColor3f(color, 1.0F, color);
    
    GL11.glTranslatef(x, y, z);
    
    float radius1 = PaintManager.getInnerBrushRadius();
    
    if (PaintManager.shouldDrawInnerRadius())
    {
      Sphere sphere1 = new Sphere();
      sphere1.draw(radius1, 8, 8);
    }
    
    float radius2 = PaintManager.getOuterBrushRadius();
    Sphere sphere2 = new Sphere();
    sphere2.draw(radius2, 8, 8);
    GL11.glTranslatef(-x, -y, -z);
    
    GL11.glDisable(2929);
    GL11.glBegin(0);
    GL11.glVertex3f(x, y, z);
    GL11.glEnd();
    
    GL11.glPopAttrib();
  }
  
  public static void renderCube(float x, float y, float z, float r)
  {
    GL11.glPushMatrix();
    GL11.glTranslatef(x, y, z);
    
    GL11.glPushAttrib(4073);
    GL11.glPolygonMode(1032, 6914);
    
    GL11.glBegin(4);
    


    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(-r, -r, -r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(-r, r, -r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(-r, r, r);
    
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(-r, -r, -r);
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(-r, -r, r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(-r, r, r);
    


    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(r, -r, -r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(r, r, -r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(r, r, r);
    
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(r, -r, -r);
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(r, -r, r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(r, r, r);
    


    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(-r, -r, r);
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(r, -r, r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(r, r, r);
    
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(-r, -r, r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(-r, r, r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(r, r, r);
    


    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(-r, -r, -r);
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(-r, -r, r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(r, -r, r);
    
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(-r, -r, -r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(r, -r, -r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(r, -r, r);
    


    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(-r, r, -r);
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(-r, r, r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(r, r, r);
    
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(-r, r, -r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(r, r, -r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(r, r, r);
    


    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(-r, -r, -r);
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(r, -r, -r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(r, r, -r);
    
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(-r, -r, -r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(-r, r, -r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(r, r, -r);
    
    GL11.glEnd();
    GL11.glPopMatrix();
    GL11.glPopAttrib();
  }
  
  public static void renderSelectionBox(Vector3f startPoint, Vector3f endPoint)
  {
    GL11.glPushAttrib(4081);
    GL11.glDisable(2929);
    GL11.glDisable(3553);
    GL11.glLineWidth(1.0F);
    GL11.glPointSize(8.0F);
    
    GL11.glColor3f(1.0F, 1.0F, 1.0F);
    
    GL11.glBegin(3);
    GL11.glVertex3f(x, y, z);
    GL11.glVertex3f(x, y, z);
    GL11.glVertex3f(x, y, z);
    GL11.glVertex3f(x, y, z);
    GL11.glVertex3f(x, y, z);
    GL11.glVertex3f(x, y, z);
    GL11.glVertex3f(x, y, z);
    GL11.glVertex3f(x, y, z);
    GL11.glVertex3f(x, y, z);
    GL11.glVertex3f(x, y, z);
    GL11.glEnd();
    
    GL11.glBegin(1);
    GL11.glVertex3f(x, y, z);
    GL11.glVertex3f(x, y, z);
    GL11.glVertex3f(x, y, z);
    GL11.glVertex3f(x, y, z);
    GL11.glVertex3f(x, y, z);
    GL11.glVertex3f(x, y, z);
    GL11.glEnd();
    
    GL11.glPopAttrib();
  }
  
  public static String readShader(File f)
  {
    StringBuilder sb = new StringBuilder();
    
    BufferedReader reader = null;
    try
    {
      reader = new BufferedReader(new FileReader(f));
      String line;
      while ((line = reader.readLine()) != null)
        sb.append(line).append('\n');
      reader.close();
    }
    catch (IOException e)
    {
      Logger.log("Could not read shader file " + f.getName(), Priority.ERROR);
    }
    
    return sb.toString();
  }
  
  public static void drawPrimitivePreview(TriangleBatch previewTriangles)
  {
    if (previewTriangles == null) {
      return;
    }
    GL11.glPushAttrib(265001);
    GL11.glDisable(3553);
    GL11.glLineWidth(2.0F);
    
    GL11.glDepthMask(false);
    GL11.glDepthFunc(519);
    
    GL11.glPolygonMode(1032, 6913);
    GL11.glColor3f(1.0F, 1.0F, 0.0F);
    
    GL11.glBegin(4);
    for (Triangle t : triangles)
    {
      for (Vertex v : vert)
        GL11.glVertex3f(v.getCurrentX(), v.getCurrentY(), v.getCurrentZ());
    }
    GL11.glEnd();
    
    GL11.glDepthMask(true);
    GL11.glPopAttrib();
  }
}
