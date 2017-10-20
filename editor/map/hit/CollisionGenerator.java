package editor.map.hit;

import editor.map.MapObject.HitType;
import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.TriangleBatch;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import org.lwjgl.util.vector.Vector3f;
import util.Logger;

public class CollisionGenerator
{
  private static final int MIN_HEIGHT = 64;
  public CollisionGenerator() {}
  
  public static enum ColliderType
  {
    MESH("Copy Mesh"), 
    CONVEX_HULL("Convex Hull"), 
    CONCAVE_HULL("Concave Hull"), 
    PROJECTION("Projection"), 
    FLOOR("Copy Floor"), 
    WALLS("Copy Walls");
    
    private final String name;
    
    private ColliderType(String name)
    {
      this.name = name;
    }
    
    public String toString()
    {
      return name;
    }
  }
  


  public static Collider getMeshCollider(Iterable<Triangle> triangles)
  {
    Collider c = new Collider(MapObject.HitType.HIT);
    name = "Mesh Collider";
    mesh.batch = getMeshTriangles(triangles);
    c.updateMeshHierarchy();
    dirtyAABB = true;
    return c;
  }
  
  public static TriangleBatch getMeshTriangles(Iterable<Triangle> triangles)
  {
    TriangleBatch batch = new TriangleBatch();
    
    for (Triangle t : triangles)
    {
      Triangle copy = t.basicCopy();
      triangles.add(copy);
      parentBatch = batch;
    }
    
    return batch;
  }
  
  public static Collider getFloorCollider(Iterable<Triangle> triangles)
  {
    Collider c = new Collider(MapObject.HitType.HIT);
    name = "Floor Collider";
    mesh.batch = getFloorTriangles(triangles);
    c.updateMeshHierarchy();
    dirtyAABB = true;
    return c;
  }
  
  public static Collider getWallCollider(Iterable<Triangle> triangles)
  {
    Collider c = new Collider(MapObject.HitType.HIT);
    name = "Floor Collider";
    mesh.batch = getWallTriangles(triangles);
    c.updateMeshHierarchy();
    dirtyAABB = true;
    return c;
  }
  
  public static TriangleBatch getFloorTriangles(Iterable<Triangle> triangles)
  {
    TriangleBatch batch = new TriangleBatch();
    Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
    double threshold = Math.sqrt(3.0D) / 2.0D;
    
    for (Triangle t : triangles)
    {
      float[] tn = t.getNormal();
      if (tn != null)
      {

        Vector3f normal = new Vector3f(tn[0], tn[1], tn[2]);
        if (Vector3f.dot(normal, up) > threshold)
        {
          Triangle copy = t.basicCopy();
          triangles.add(copy);
          parentBatch = batch;
        }
      }
    }
    return batch;
  }
  
  public static TriangleBatch getWallTriangles(Iterable<Triangle> triangles)
  {
    TriangleBatch batch = new TriangleBatch();
    Vector3f up = new Vector3f(0.0F, 1.0F, 0.0F);
    double threshold = 0.5D;
    
    for (Triangle t : triangles)
    {
      float[] tn = t.getNormal();
      if (tn != null)
      {

        Vector3f normal = new Vector3f(tn[0], tn[1], tn[2]);
        float inner = Vector3f.dot(normal, up);
        if ((inner < threshold) && (inner > -threshold))
        {
          Triangle copy = t.basicCopy();
          triangles.add(copy);
          parentBatch = batch;
        }
      }
    }
    return batch;
  }
  
  public static Collider getProjectedCollider(Iterable<Triangle> triangles)
  {
    TriangleBatch batch = getProjectedTriangles(triangles);
    if (batch == null) {
      return null;
    }
    Collider c = new Collider(MapObject.HitType.HIT);
    name = "Projected Collider";
    mesh.batch = batch;
    c.updateMeshHierarchy();
    dirtyAABB = true;
    return c;
  }
  
  public static TriangleBatch getProjectedTriangles(Iterable<Triangle> triangles)
  {
    VertexSet uniqueVertexSet = getUniqueVertexSet(triangles);
    if (uniqueVertexSet == null) {
      return null;
    }
    Iterable<SimpleVertex> hull = getConvexHull(verts);
    if (hull == null) {
      return null;
    }
    Iterable<SimpleTriangle> delaunayTriangleList = getDelaunayTriangulation(verts);
    return convertTriangles(delaunayTriangleList, miny);
  }
  
  public static Collider getConvexHullCollider(Iterable<Triangle> triangles)
  {
    TriangleBatch batch = getConvexHullTriangles(triangles);
    if (batch == null) {
      return null;
    }
    Collider c = new Collider(MapObject.HitType.HIT);
    name = "Convex Hull Collider";
    mesh.batch = batch;
    c.updateMeshHierarchy();
    dirtyAABB = true;
    return c;
  }
  
  public static TriangleBatch getConvexHullTriangles(Iterable<Triangle> triangles)
  {
    VertexSet uniqueVertexSet = getUniqueVertexSet(triangles);
    if (uniqueVertexSet == null) {
      return null;
    }
    Iterable<SimpleVertex> hull = getConvexHull(verts);
    if (hull == null) {
      return null;
    }
    return getTrianglesFromHull(hull, miny, maxy);
  }
  
  public static Collider getConcaveHullCollider(Iterable<Triangle> triangles, int threshold)
  {
    TriangleBatch batch = getConcaveHullTriangles(triangles, threshold);
    if (batch == null) {
      return null;
    }
    Collider c = new Collider(MapObject.HitType.HIT);
    name = "Concave Hull Collider";
    mesh.batch = batch;
    c.updateMeshHierarchy();
    dirtyAABB = true;
    return c;
  }
  
  public static TriangleBatch getConcaveHullTriangles(Iterable<Triangle> triangles, int threshold)
  {
    VertexSet uniqueVertexSet = getUniqueVertexSet(triangles);
    if (uniqueVertexSet == null) {
      return null;
    }
    Iterable<SimpleVertex> hull = getConcaveHull(verts, shortestEdgeLength, threshold);
    if (hull == null) {
      return null;
    }
    return getTrianglesFromHull(hull, miny, maxy);
  }
  
  private static class VertexSet
  {
    public HashSet<CollisionGenerator.SimpleVertex> verts = new HashSet();
    public int miny = Integer.MAX_VALUE;
    public int maxy = Integer.MIN_VALUE;
    double shortestEdgeLength = Double.MAX_VALUE;
    
    private VertexSet() {}
  }
  
  private static VertexSet getUniqueVertexSet(Iterable<Triangle> triangles) { VertexSet set = new VertexSet(null);
    

    for (Triangle t : triangles)
    {
      for (Vertex v : vert)
      {
        verts.add(new SimpleVertex(v.getCurrentX(), 0, v.getCurrentZ()));
        
        int y = v.getCurrentY();
        if (y > maxy) maxy = y;
        if (y < miny) { miny = y;
        }
      }
      double dist = getDistance(vert[0], vert[1]);
      if (dist < shortestEdgeLength) { shortestEdgeLength = dist;
      }
      dist = getDistance(vert[1], vert[2]);
      if (dist < shortestEdgeLength) { shortestEdgeLength = dist;
      }
      dist = getDistance(vert[2], vert[0]);
      if (dist < shortestEdgeLength) { shortestEdgeLength = dist;
      }
    }
    if (verts.size() < 3)
    {
      Logger.log("Could not generate hull collider, not enough unique verticies!", util.Priority.WARNING);
      return null;
    }
    
    return set;
  }
  
  private static TriangleBatch getTrianglesFromHull(Iterable<SimpleVertex> hull, int miny, int maxy)
  {
    ArrayList<Vertex> top = new ArrayList();
    ArrayList<Vertex> bottom = new ArrayList();
    
    if (maxy - miny < 64) {
      maxy = miny + 64;
    }
    int vertexCount = 0;
    for (SimpleVertex v : hull)
    {
      top.add(new Vertex(x, maxy, z));
      bottom.add(new Vertex(x, miny, z));
      vertexCount++;
    }
    
    TriangleBatch batch = new TriangleBatch();
    
    for (int i = 0; i < vertexCount - 1; i++)
    {

      Triangle t1 = new Triangle((Vertex)top.get(i + 1), (Vertex)top.get(i), (Vertex)bottom.get(i));
      Triangle t2 = new Triangle((Vertex)bottom.get(i), (Vertex)bottom.get(i + 1), (Vertex)top.get(i + 1));
      
      triangles.add(t1);
      triangles.add(t2);
    }
    
    return batch;
  }
  




  private static List<SimpleVertex> getConvexHull(Iterable<SimpleVertex> uniqueVertexSet)
  {
    List<SimpleVertex> hull = new ArrayList();
    SimpleVertex startVertex = null;
    double relativeAngle = 0.0D;
    
    for (SimpleVertex sv : uniqueVertexSet)
    {
      if ((startVertex == null) || (x > x) || ((x == x) && (z > z))) {
        startVertex = sv;
      }
    }
    SimpleVertex currentVertex = startVertex;
    hull.add(currentVertex);
    
    do
    {
      SimpleVertex nextVertex = null;
      double smallestAngle = Double.POSITIVE_INFINITY;
      double furthestLength = 0.0D;
      
      for (SimpleVertex v : uniqueVertexSet)
      {
        if (v != currentVertex)
        {

          double angle = Math.toDegrees(Math.atan2(x - x, z - z));
          if (angle < 0.0D) { angle += 360.0D;
          }
          angle -= relativeAngle;
          if (angle < 0.0D) { angle += 360.0D;
          }
          if (angle < smallestAngle)
          {
            nextVertex = v;
            smallestAngle = angle;
            double dx = x - x;
            double dz = z - z;
            furthestLength = dx * dx + dz * dz;
          } else if (Math.abs(angle - smallestAngle) < 1.0E-6D) {
            double dx = x - x;
            double dz = z - z;
            double length = dx * dx + dz * dz;
            if (length > furthestLength)
            {
              nextVertex = v;
              smallestAngle = angle;
              furthestLength = length;
            }
          }
        }
      }
      hull.add(nextVertex);
      currentVertex = nextVertex;
      
      relativeAngle += smallestAngle;
      if (relativeAngle >= 360.0D) { relativeAngle -= 360.0D;
      }
      if (hull.size() >= 1024)
      {
        Logger.log("Failed to build convex hull, vertex overflow!", util.Priority.WARNING);
        return null;
      }
      
    } while (currentVertex != startVertex);
    
    return hull;
  }
  
  private static List<SimpleVertex> getConcaveHull(Iterable<SimpleVertex> uniqueVertexSet, double shortestEdgeLength, int threshold)
  {
    List<SimpleVertex> convexHull = getConvexHull(uniqueVertexSet);
    
    Set<SimpleVertex> boundarySet = new HashSet(convexHull);
    LinkedList<SimpleEdge> edgeList = new LinkedList();
    

    for (int i = 0; i < convexHull.size() - 1; i++)
      edgeList.add(new SimpleEdge((SimpleVertex)convexHull.get(i), (SimpleVertex)convexHull.get(i + 1)));
    edgeList.add(new SimpleEdge((SimpleVertex)convexHull.get(convexHull.size() - 1), (SimpleVertex)convexHull.get(0)));
    
    PriorityQueue<SimpleEdge> sortedEdgeQueue = new PriorityQueue(edgeList);
    
    while (!sortedEdgeQueue.isEmpty())
    {
      SimpleEdge edge = (SimpleEdge)sortedEdgeQueue.poll();
      
      if (edge.getLength() >= threshold)
      {
        SimpleVertex bestVertex = null;
        double smallestAngle = 180.0D;
        

        double angle12 = Math.toDegrees(Math.atan2(v2.x - v1.x, v2.z - v1.z));
        double angle21 = angle12 - 180.0D;
        
        if (angle12 < 0.0D) angle12 += 360.0D;
        if (angle21 < 0.0D) { angle21 += 360.0D;
        }
        for (SimpleVertex v : uniqueVertexSet)
        {
          if ((getDistance(v, v1) < edge.getLength()) && (getDistance(v, v2) < edge.getLength()) && 
          

            (!boundarySet.contains(v)))
          {

            double angle1 = Math.toDegrees(Math.atan2(x - v1.x, z - v1.z));
            double angle2 = Math.toDegrees(Math.atan2(x - v2.x, z - v2.z));
            
            if (angle1 < 0.0D) angle1 += 360.0D;
            if (angle2 < 0.0D) { angle2 += 360.0D;
            }
            double rel1 = angle1 - angle12;
            double rel2 = angle2 - angle21;
            
            if (rel1 >= 360.0D) rel1 -= 360.0D;
            if (rel2 >= 360.0D) { rel2 -= 360.0D;
            }
            rel1 = Math.abs(rel1);
            rel2 = Math.abs(rel2);
            
            double largerAngle = rel1 > rel2 ? rel1 : rel2;
            
            if (largerAngle <= 90.1D)
            {

              if (largerAngle < smallestAngle)
              {
                smallestAngle = largerAngle;
                bestVertex = v;
              }
            }
          }
        }
        if (bestVertex != null)
        {
          SimpleEdge replacement1 = new SimpleEdge(v1, bestVertex);
          SimpleEdge replacement2 = new SimpleEdge(bestVertex, v2);
          
          boolean intersects = false;
          for (SimpleEdge e : edgeList)
          {
            if (e != edge)
            {

              if ((doEdgesIntersect2D(e, replacement1)) || (doEdgesIntersect2D(e, replacement2)))
                intersects = true;
            }
          }
          if (!intersects)
          {

            int i = edgeList.indexOf(edge);
            edgeList.remove(i);
            edgeList.add(i, replacement2);
            edgeList.add(i, replacement1);
            
            sortedEdgeQueue.add(replacement1);
            sortedEdgeQueue.add(replacement2);
            
            boundarySet.add(bestVertex);
          }
        }
      }
    }
    List<SimpleVertex> concaveHull = new LinkedList();
    
    concaveHull.add(get0v1);
    for (int i = 0; i < edgeList.size() - 1; i++) {
      concaveHull.add(getv2);
    }
    return concaveHull;
  }
  
  private static boolean doEdgesIntersect2D(SimpleEdge e1, SimpleEdge e2)
  {
    int dx = v1.x - v1.x;
    int dz = v1.z - v1.z;
    
    int rx = v2.x - v1.x;
    int rz = v2.z - v1.z;
    
    int sx = v2.x - v1.x;
    int sz = v2.z - v1.z;
    

    double RcS = rx * sz - rz * sx;
    double DcR = dx * rz - dz * rx;
    double DcS = dx * sz - dz * sx;
    
    double epsilon = 1.0E-6D;
    

    if (Math.abs(RcS) <= 1.0E-5D)
    {

      if (Math.abs(DcR) <= 1.0E-5D)
      {
        double r2 = rx * rx + rz * rz;
        double t0 = (dx * rx + dz * rz) / r2;
        double t1 = t0 + (sx * rx + sz * rz) / r2;
        

        if (((t0 > epsilon) && (t0 < 1.0D - epsilon)) || ((t1 > epsilon) && (t1 < 1.0D - epsilon))) {
          return true;
        }
      }
    }
    else
    {
      double t = DcS / RcS;
      double u = DcR / RcS;
      
      return (epsilon < t) && (t < 1.0D - epsilon) && (epsilon < u) && (u < 1.0D - epsilon);
    }
    
    return false;
  }
  
  private static Iterable<SimpleTriangle> getDelaunayTriangulation(Iterable<SimpleVertex> uniqueVertexSet)
  {
    int minX = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    
    int minZ = Integer.MAX_VALUE;
    int maxZ = Integer.MIN_VALUE;
    
    for (SimpleVertex v : uniqueVertexSet)
    {
      if (x < minX) minX = x;
      if (x > maxX) maxX = x;
      if (z < minZ) minZ = z;
      if (z > maxZ) { maxZ = z;
      }
    }
    int sizeX = maxX - minX;
    int sizeZ = maxZ - minZ;
    
    if (sizeX < 16) sizeX = 16;
    if (sizeZ < 16) { sizeZ = 16;
    }
    int centerX = minX + sizeX / 2;
    int centerZ = minZ + sizeZ / 2;
    SimpleVertex enc3;
    SimpleVertex enc1;
    SimpleVertex enc2;
    SimpleVertex enc3; if (sizeX > sizeZ)
    {
      SimpleVertex enc1 = new SimpleVertex(centerX + sizeX, 0, centerZ - sizeZ);
      SimpleVertex enc2 = new SimpleVertex(centerX - sizeX, 0, centerZ - sizeZ);
      enc3 = new SimpleVertex(centerX, 0, centerZ + 4 * sizeZ);
    } else {
      enc1 = new SimpleVertex(centerX - sizeX, 0, centerZ + sizeZ);
      enc2 = new SimpleVertex(centerX - sizeX, 0, centerZ - sizeZ);
      enc3 = new SimpleVertex(centerX + 4 * sizeX, 0, centerZ);
    }
    
    LinkedList<SimpleTriangle> triangleList = new LinkedList();
    
    SimpleTriangle enclosingTriangle = new SimpleTriangle(enc1, enc2, enc3);
    enclosingTriangle.makeCCW();
    triangleList.add(enclosingTriangle);
    
    for (SimpleVertex v : uniqueVertexSet) {
      addVertexToDelaunayTriangulation(triangleList, v);
    }
    Object i = triangleList.iterator();
    while (((Iterator)i).hasNext())
    {
      SimpleTriangle t = (SimpleTriangle)((Iterator)i).next();
      
      if ((v1 == enc1) || (v1 == enc2) || (v1 == enc3) || (v2 == enc1) || (v2 == enc2) || (v2 == enc3) || (v3 == enc1) || (v3 == enc2) || (v3 == enc3))
      {

        ((Iterator)i).remove();
      }
    }
    return triangleList;
  }
  
  private static void addVertexToDelaunayTriangulation(Collection<SimpleTriangle> triangleList, SimpleVertex v)
  {
    HashMap<SimpleEdge, SimpleEdge> edgeMap = new HashMap();
    
    Iterator<SimpleTriangle> i = triangleList.iterator();
    SimpleTriangle t; while (i.hasNext())
    {
      t = (SimpleTriangle)i.next();
      
      if (t.circumcircleContains(v))
      {
        SimpleEdge e1 = new SimpleEdge(v1, v2);
        SimpleEdge e2 = new SimpleEdge(v2, v3);
        SimpleEdge e3 = new SimpleEdge(v3, v1);
        
        if (edgeMap.containsKey(e1)) {
          getignore = true;
        } else {
          edgeMap.put(e1, e1);
        }
        if (edgeMap.containsKey(e2)) {
          getignore = true;
        } else {
          edgeMap.put(e2, e2);
        }
        if (edgeMap.containsKey(e3)) {
          getignore = true;
        } else {
          edgeMap.put(e3, e3);
        }
        i.remove();
      }
    }
    
    for (SimpleEdge e : edgeMap.keySet())
    {
      if (!ignore)
      {
        SimpleTriangle t = new SimpleTriangle(v, v1, v2);
        t.makeCCW();
        triangleList.add(t);
      }
    }
  }
  
  private static TriangleBatch convertTriangles(Iterable<SimpleTriangle> simpleTriangles, int height)
  {
    TriangleBatch batch = new TriangleBatch();
    HashMap<Vertex, Vertex> vertexSet = new HashMap();
    
    for (SimpleTriangle st : simpleTriangles)
    {
      Vertex v1 = new Vertex(v1.x, height, v1.z);
      if (vertexSet.containsKey(v1)) {
        v1 = (Vertex)vertexSet.get(v1);
      } else {
        vertexSet.put(v1, v1);
      }
      Vertex v2 = new Vertex(v2.x, height, v2.z);
      if (vertexSet.containsKey(v2)) {
        v2 = (Vertex)vertexSet.get(v2);
      } else {
        vertexSet.put(v2, v2);
      }
      Vertex v3 = new Vertex(v3.x, height, v3.z);
      if (vertexSet.containsKey(v3)) {
        v3 = (Vertex)vertexSet.get(v3);
      } else {
        vertexSet.put(v3, v3);
      }
      Triangle t = new Triangle(v2, v1, v3);
      triangles.add(t);
    }
    
    return batch;
  }
  
  private static double getDistance(Vertex v1, Vertex v2)
  {
    int dx = v1.getCurrentX() - v2.getCurrentX();
    int dy = v1.getCurrentY() - v2.getCurrentY();
    int dz = v1.getCurrentZ() - v2.getCurrentZ();
    
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }
  
  private static double getDistance(SimpleVertex v1, SimpleVertex v2)
  {
    int dx = x - x;
    int dy = y - y;
    int dz = z - z;
    
    return Math.sqrt(dx * dx + dy * dy + dz * dz);
  }
  
  private static class SimpleVertex {
    public final int x;
    public final int y;
    public final int z;
    
    public SimpleVertex(int x, int y, int z) {
      this.x = x;
      this.y = y;
      this.z = z;
    }
    
    public String toString()
    {
      return x + " " + y + " " + z;
    }
    

    public int hashCode()
    {
      int prime = 31;
      int result = 1;
      result = 31 * result + x;
      result = 31 * result + y;
      result = 31 * result + z;
      return result;
    }
    

    public boolean equals(Object obj)
    {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      SimpleVertex other = (SimpleVertex)obj;
      if (x != x)
        return false;
      if (y != y)
        return false;
      if (z != z)
        return false;
      return true;
    }
  }
  
  private static class SimpleEdge implements Comparable<SimpleEdge>
  {
    public boolean ignore;
    public final CollisionGenerator.SimpleVertex v1;
    public final CollisionGenerator.SimpleVertex v2;
    
    public SimpleEdge(CollisionGenerator.SimpleVertex v1, CollisionGenerator.SimpleVertex v2) {
      this.v1 = v1;
      this.v2 = v2;
    }
    
    public double getLength()
    {
      int dx = v1.x - v2.x;
      int dz = v1.z - v2.z;
      return Math.sqrt(dx * dx + dz * dz);
    }
    

    public int hashCode()
    {
      return v1.hashCode() + v2.hashCode();
    }
    

    public boolean equals(Object obj)
    {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      SimpleEdge other = (SimpleEdge)obj;
      
      if ((v1.equals(v1)) && (v2.equals(v2))) {
        return true;
      }
      if ((v1.equals(v2)) && (v2.equals(v1))) {
        return true;
      }
      return false;
    }
    


    public int compareTo(SimpleEdge other)
    {
      return (int)(other.getLength() - getLength());
    }
  }
  
  private static class SimpleTriangle {
    CollisionGenerator.SimpleVertex v1;
    CollisionGenerator.SimpleVertex v2;
    CollisionGenerator.SimpleVertex v3;
    
    public SimpleTriangle(CollisionGenerator.SimpleVertex v1, CollisionGenerator.SimpleVertex v2, CollisionGenerator.SimpleVertex v3) {
      this.v1 = v1;
      this.v2 = v2;
      this.v3 = v3;
    }
    
    public void makeCCW()
    {
      if ((v3.x - v1.x) * (v2.z - v1.z) > (v2.x - v1.x) * (v3.z - v1.z))
      {
        CollisionGenerator.SimpleVertex temp = v1;
        v1 = v3;
        v3 = temp;
      }
    }
    
    public boolean circumcircleContains(CollisionGenerator.SimpleVertex v)
    {
      int m11 = v1.x - x;
      int m12 = v2.x - x;
      int m13 = v3.x - x;
      int m21 = v1.z - z;
      int m22 = v2.z - z;
      int m23 = v3.z - z;
      long m31 = v1.x * v1.x - x * x + (v1.z * v1.z - z * z);
      long m32 = v2.x * v2.x - x * x + (v2.z * v2.z - z * z);
      long m33 = v3.x * v3.x - x * x + (v3.z * v3.z - z * z);
      
      long c1 = m11 * (m22 * m33 - m23 * m32);
      long c2 = m21 * (m12 * m33 - m32 * m13);
      long c3 = m31 * (m12 * m23 - m13 * m22);
      
      return c1 - c2 + c3 > 0L;
    }
  }
}
