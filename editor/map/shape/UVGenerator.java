package editor.map.shape;

import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.selection.Selection;
import org.lwjgl.util.vector.Vector3f;

public class UVGenerator
{
  private final Projection projection;
  private final ProjectionAxis axis;
  
  public static enum ProjectionAxis
  {
    X,  Y,  Z,  NORMAL;
    private ProjectionAxis() {} } public static enum Projection { PLANAR,  POLAR,  CYLINDRICAL,  SPHERICAL;
    
    private Projection() {}
  }
  
  public UVGenerator(Projection projection, ProjectionAxis axis)
  {
    this.projection = projection;
    this.axis = axis;
  }
  
  public void generateUVs(java.util.Collection<Triangle> triangles) { Vector3f normal;
    Vector3f normal;
    Vector3f normal;
    Vector3f normal;
    switch (1.$SwitchMap$editor$map$shape$UVGenerator$ProjectionAxis[axis.ordinal()])
    {
    case 1: 
      normal = new Vector3f(1.0F, 0.0F, 0.0F);
      break;
    case 2: 
    default: 
      normal = new Vector3f(0.0F, 1.0F, 0.0F);
      break;
    case 3: 
      normal = new Vector3f(0.0F, 0.0F, 1.0F);
      break;
    case 4: 
      normal = new Vector3f();
      int count = 0;
      for (Triangle t : triangles)
      {
        float[] tnorm = t.getNormal();
        if (tnorm != null)
        {
          x += tnorm[0];
          y += tnorm[1];
          z += tnorm[2];
          count++;
        }
      }
      if (count > 0)
      {
        x /= count;
        y /= count;
        z /= count;
        
        if (normal.length() < 0.001D) {
          normal = new Vector3f(0.0F, 1.0F, 0.0F);
        } else {
          normal.normalise();
        }
      } else {
        normal = new Vector3f(0.0F, 1.0F, 0.0F);
      }
      break;
    }
    normal.negate();
    
    Vector3f worldUp = new Vector3f(0.0F, 1.0F, 0.0F);
    if (Math.abs(Vector3f.dot(normal, worldUp)) > 0.99D) {
      worldUp = new Vector3f(1.0F, 0.0F, 0.0F);
    }
    Vector3f right = new Vector3f();
    Vector3f.cross(worldUp, normal, right);
    
    Vector3f up = new Vector3f();
    Vector3f.cross(normal, right, up);
    
    switch (1.$SwitchMap$editor$map$shape$UVGenerator$Projection[projection.ordinal()])
    {
    case 1: 
    default: 
      generatePlanarUVs(triangles, up, right);
      break;
    case 2: 
      generatePolarUVs(triangles, up, right);
      break;
    case 3: 
      generateCylindicalUVs(triangles, normal, up, right);
      break;
    case 4: 
      generateSphericalUVs(triangles, normal, up, right);
    }
    
  }
  



  private void generatePlanarUVs(Iterable<Triangle> triangles, Vector3f up, Vector3f right)
  {
    util.IdentityHashSet<Vertex> vertexSet = new util.IdentityHashSet();
    for (Triangle t : triangles)
    {
      for (Vertex v : vert) {
        vertexSet.add(v);
      }
    }
    Object uvList = new Selection();
    editor.map.BoundingBox aabb = new editor.map.BoundingBox();
    for (??? = vertexSet.iterator(); ((java.util.Iterator)???).hasNext();) { Vertex v = (Vertex)((java.util.Iterator)???).next();
      
      ((Selection)uvList).addWithoutSelecting(uv);
      aabb.encompass(v);
    }
    
    Vector3f center = aabb.getCenter();
    Vector3f relative = new Vector3f();
    
    ((Selection)uvList).startDirectTransformation();
    for (Vertex vert : vertexSet)
    {
      Vector3f.sub(vert.getCurrentPos(), center, relative);
      float u = Vector3f.dot(relative, right);
      float v = Vector3f.dot(relative, up);
      uv.setPosition((int)(32.0F * u), (int)(32.0F * v));
    }
    ((Selection)uvList).endDirectTransformation();
  }
  
  private void generatePolarUVs(Iterable<Triangle> triangles, Vector3f up, Vector3f right)
  {
    util.IdentityHashSet<Vertex> vertexSet = new util.IdentityHashSet();
    for (Triangle t : triangles)
    {
      for (Vertex v : vert) {
        vertexSet.add(v);
      }
    }
    Object uvList = new Selection();
    editor.map.BoundingBox aabb = new editor.map.BoundingBox();
    for (??? = vertexSet.iterator(); ((java.util.Iterator)???).hasNext();) { Vertex v = (Vertex)((java.util.Iterator)???).next();
      
      ((Selection)uvList).addWithoutSelecting(uv);
      aabb.encompass(v);
    }
    
    Vector3f center = aabb.getCenter();
    Vector3f relative = new Vector3f();
    
    ((Selection)uvList).startDirectTransformation();
    for (Vertex vert : vertexSet)
    {
      Vector3f.sub(vert.getCurrentPos(), center, relative);
      float u = Vector3f.dot(relative, right);
      float v = Vector3f.dot(relative, up);
      
      double r = Math.sqrt(u * u + v * v);
      double phi = 180.0D + Math.toDegrees(Math.atan2(v, u));
      
      uv.setPosition((int)(32.0D * phi), (int)(32.0D * r));
    }
    ((Selection)uvList).endDirectTransformation();
  }
  
  private void generateCylindicalUVs(Iterable<Triangle> triangles, Vector3f forward, Vector3f up, Vector3f right)
  {
    util.IdentityHashSet<Vertex> vertexSet = new util.IdentityHashSet();
    for (Triangle t : triangles)
    {
      for (Vertex v : vert) {
        vertexSet.add(v);
      }
    }
    Object uvList = new Selection();
    editor.map.BoundingBox aabb = new editor.map.BoundingBox();
    for (??? = vertexSet.iterator(); ((java.util.Iterator)???).hasNext();) { Vertex v = (Vertex)((java.util.Iterator)???).next();
      
      ((Selection)uvList).addWithoutSelecting(uv);
      aabb.encompass(v);
    }
    
    Vector3f center = aabb.getCenter();
    Vector3f relative = new Vector3f();
    
    ((Selection)uvList).startDirectTransformation();
    for (Vertex vert : vertexSet)
    {
      Vector3f.sub(vert.getCurrentPos(), center, relative);
      
      float x = Vector3f.dot(relative, right);
      float y = Vector3f.dot(relative, up);
      float z = Vector3f.dot(relative, forward);
      
      double azimuth = Math.toDegrees(Math.atan2(y, x));
      
      uv.setPosition((int)(8.0D * azimuth), (int)(8.0F * z));
    }
    ((Selection)uvList).endDirectTransformation();
  }
  
  private void generateSphericalUVs(Iterable<Triangle> triangles, Vector3f forward, Vector3f up, Vector3f right)
  {
    util.IdentityHashSet<Vertex> vertexSet = new util.IdentityHashSet();
    for (Triangle t : triangles)
    {
      for (Vertex v : vert) {
        vertexSet.add(v);
      }
    }
    Object uvList = new Selection();
    editor.map.BoundingBox aabb = new editor.map.BoundingBox();
    for (??? = vertexSet.iterator(); ((java.util.Iterator)???).hasNext();) { Vertex v = (Vertex)((java.util.Iterator)???).next();
      
      ((Selection)uvList).addWithoutSelecting(uv);
      aabb.encompass(v);
    }
    
    Vector3f center = aabb.getCenter();
    Vector3f relative = new Vector3f();
    
    ((Selection)uvList).startDirectTransformation();
    for (Vertex vert : vertexSet)
    {
      Vector3f.sub(vert.getCurrentPos(), center, relative);
      
      float x = Vector3f.dot(relative, right);
      float y = Vector3f.dot(relative, up);
      float z = Vector3f.dot(relative, forward);
      
      double radius = Math.sqrt(x * x + y * y);
      double azimuth = 180.0D + Math.toDegrees(Math.atan2(y, x));
      double polar = 180.0D + Math.toDegrees(Math.atan2(radius, z));
      
      uv.setPosition((int)(8.0D * azimuth), (int)(8.0D * polar));
    }
    ((Selection)uvList).endDirectTransformation();
  }
}
