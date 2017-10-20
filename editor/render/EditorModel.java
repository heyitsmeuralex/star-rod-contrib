package editor.render;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import util.Logger;
import util.Priority;









public class EditorModel
{
  private ArrayList<Triangle> triangleList;
  
  public EditorModel(File f)
  {
    triangleList = new ArrayList();
    try {
      BufferedReader reader = new BufferedReader(new FileReader(f));Throwable localThrowable3 = null;
      try { readFromObj(reader);
      }
      catch (Throwable localThrowable1)
      {
        localThrowable3 = localThrowable1;throw localThrowable1;
      } finally {
        if (reader != null) if (localThrowable3 != null) try { reader.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else reader.close();
      } } catch (IOException e) { Logger.log("Failed to load resource " + f.getName(), Priority.ERROR);
    }
  }
  
  public EditorModel(String resourceName)
  {
    triangleList = new ArrayList();
    
    InputStream is = EditorModel.class.getResourceAsStream(resourceName);
    if (is == null)
    {
      Logger.log("Unable to find resource " + resourceName, Priority.ERROR);
      return;
    }
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(is));Throwable localThrowable3 = null;
      try { readFromObj(reader);
      }
      catch (Throwable localThrowable1)
      {
        localThrowable3 = localThrowable1;throw localThrowable1;
      } finally {
        if (reader != null) if (localThrowable3 != null) try { reader.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else reader.close();
      } } catch (IOException e) { Logger.log("Failed to load resource " + resourceName, Priority.ERROR);
    }
  }
  
  private void readFromObj(BufferedReader reader) throws IOException
  {
    ArrayList<Vertex> vertexTable = new ArrayList(512);
    vertexTable.add(new Vertex(null));
    
    float r = 1.0F;float g = 1.0F;float b = 1.0F;
    
    String line;
    while ((line = reader.readLine()) != null)
    {
      if (line.startsWith("v"))
      {
        while (line.contains("  "))
          line = line.replaceAll("  ", " ");
        String[] tokens = line.split(" ");
        
        Vertex v = new Vertex(null);
        position.x = Float.parseFloat(tokens[1]);
        position.y = (-Float.parseFloat(tokens[2]));
        position.z = Float.parseFloat(tokens[3]);
        r = r;
        g = g;
        b = b;
        vertexTable.add(v);
      }
      
      if (line.startsWith("c"))
      {
        while (line.contains("  "))
          line = line.replaceAll("  ", " ");
        String[] tokens = line.split(" ");
        
        r = Float.parseFloat(tokens[1]);
        g = Float.parseFloat(tokens[2]);
        b = Float.parseFloat(tokens[3]);
      }
      
      if (line.startsWith("f"))
      {
        while (line.contains("  "))
          line = line.replaceAll("  ", " ");
        String[] tokens = line.split(" ");
        
        Triangle t = new Triangle(null);
        v1 = ((Vertex)vertexTable.get(Integer.parseInt(tokens[1])));
        v2 = ((Vertex)vertexTable.get(Integer.parseInt(tokens[2])));
        v3 = ((Vertex)vertexTable.get(Integer.parseInt(tokens[3])));
        
        Vector3f norm = new Vector3f();
        Vector3f diff21 = new Vector3f();
        Vector3f diff31 = new Vector3f();
        Vector3f.sub(v2.position, v1.position, diff21);
        Vector3f.sub(v3.position, v1.position, diff31);
        Vector3f.cross(diff21, diff31, norm);
        norm = norm;
        
        triangleList.add(t);
      }
    }
  }
  
  public void render(boolean useVertexColor)
  {
    GL11.glBegin(4);
    for (Triangle t : triangleList)
    {
      GL11.glNormal3f(norm.x, norm.y, norm.z);
      t.render(useVertexColor);
    }
    GL11.glEnd();
  }
  
  private class Vertex
  {
    Vector3f position = new Vector3f();
    float r;
    
    private Vertex() {}
    float g;
    
    public void render(boolean useVertexColor) { if (useVertexColor) GL11.glColor3f(r, g, b);
      GL11.glVertex3f(position.x, position.y, position.z); }
    
    float b; }
  
  private class Triangle { EditorModel.Vertex v1;
    EditorModel.Vertex v2;
    EditorModel.Vertex v3;
    Vector3f norm = new Vector3f();
    
    private Triangle() {}
    
    public void render(boolean useVertexColor) { v1.render(useVertexColor);
      v2.render(useVertexColor);
      v3.render(useVertexColor);
    }
  }
}
