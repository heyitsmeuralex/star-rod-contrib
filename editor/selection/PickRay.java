package editor.selection;

import editor.camera.Viewport;
import editor.map.BoundingBox;
import editor.map.MutablePoint;
import editor.map.mesh.Vertex;
import editor.map.shape.UV;
import org.lwjgl.util.vector.Vector3f;

public class PickRay
{
  public final Vector3f origin;
  private final Vector3f direction;
  private final Viewport pickView;
  
  public static class PickHit
  {
    public final float dist;
    public final Vector3f point;
    public Object obj = null;
    
    public PickHit(PickRay ray)
    {
      this(ray, Float.MAX_VALUE);
    }
    
    public PickHit(PickRay ray, float dist)
    {
      this.dist = dist;
      
      if (dist < Float.MAX_VALUE)
      {
        float hx = origin.x + dist * direction.x;
        float hy = origin.y + dist * direction.y;
        float hz = origin.z + dist * direction.z;
        point = new Vector3f(hx, hy, hz);
      }
      else {
        point = null;
      }
    }
    
    public boolean missed() {
      return dist == Float.MAX_VALUE;
    }
  }
  
  public PickRay(Vector3f start, Vector3f direction, Viewport pickView)
  {
    origin = start;
    this.direction = direction;
    this.pickView = pickView;
  }
  
  public static boolean intersects(PickRay ray, BoundingBox aabb)
  {
    return !getIntersection(ray, aabb).missed();
  }
  
  public static PickHit getIntersection(PickRay ray, BoundingBox aabb)
  {
    if (aabb.isEmpty()) { return new PickHit(ray, Float.MAX_VALUE);
    }
    Vector3f invDir = new Vector3f(1.0F / direction.x, 1.0F / direction.y, 1.0F / direction.z);
    boolean signDirX = x < 0.0F;
    boolean signDirY = y < 0.0F;
    boolean signDirZ = z < 0.0F;
    Vector3f min = aabb.getMin();
    Vector3f max = aabb.getMax();
    Vector3f bbox = signDirX ? max : min;
    float tmin = (x - origin.x) * x;
    bbox = signDirX ? min : max;
    float tmax = (x - origin.x) * x;
    bbox = signDirY ? max : min;
    float tymin = (y - origin.y) * y;
    bbox = signDirY ? min : max;
    float tymax = (y - origin.y) * y;
    
    if ((tmin > tymax) || (tymin > tmax))
      return new PickHit(ray, Float.MAX_VALUE);
    if (tymin > tmin)
      tmin = tymin;
    if (tymax < tmax) {
      tmax = tymax;
    }
    bbox = signDirZ ? max : min;
    float tzmin = (z - origin.z) * z;
    bbox = signDirZ ? min : max;
    float tzmax = (z - origin.z) * z;
    
    if ((tmin > tzmax) || (tzmin > tmax))
      return new PickHit(ray, Float.MAX_VALUE);
    if (tzmin > tmin)
      tmin = tzmin;
    if (tzmax < tmax) {
      tmax = tzmax;
    }
    
    return new PickHit(ray, tmin);
  }
  





  public static PickHit getIntersection(PickRay ray, editor.map.mesh.Triangle t)
  {
    Vector3f vertex1 = vert[0].getPosition().getVector();
    Vector3f vertex2 = vert[1].getPosition().getVector();
    Vector3f vertex3 = vert[2].getPosition().getVector();
    

    Vector3f edge1 = null;Vector3f edge2 = null;
    
    edge1 = Vector3f.sub(vertex2, vertex1, edge1);
    edge2 = Vector3f.sub(vertex3, vertex1, edge2);
    

    Vector3f directionCrossEdge2 = null;
    directionCrossEdge2 = Vector3f.cross(direction, edge2, directionCrossEdge2);
    
    float determinant = Vector3f.dot(directionCrossEdge2, edge1);
    
    if ((determinant > -1.0E-7F) && (determinant < 1.0E-7F)) {
      return new PickHit(ray, Float.MAX_VALUE);
    }
    
    float inverseDeterminant = 1.0F / determinant;
    

    Vector3f distanceVector = null;
    distanceVector = Vector3f.sub(origin, vertex1, distanceVector);
    
    float triangleU = Vector3f.dot(directionCrossEdge2, distanceVector);
    triangleU *= inverseDeterminant;
    

    if ((triangleU < 0.0F) || (triangleU > 1.0F)) {
      return new PickHit(ray, Float.MAX_VALUE);
    }
    

    Vector3f distanceCrossEdge1 = null;
    distanceCrossEdge1 = Vector3f.cross(distanceVector, edge1, distanceCrossEdge1);
    
    float triangleV = Vector3f.dot(direction, distanceCrossEdge1);
    triangleV *= inverseDeterminant;
    

    if ((triangleV < 0.0F) || (triangleU + triangleV > 1.0F)) {
      return new PickHit(ray, Float.MAX_VALUE);
    }
    

    float rayDistance = Vector3f.dot(distanceCrossEdge1, edge2);
    rayDistance *= inverseDeterminant;
    

    if (rayDistance < 0.0F) {
      rayDistance *= -1.0F;
      return new PickHit(ray, Float.MAX_VALUE);
    }
    
    return new PickHit(ray, rayDistance);
  }
  
  public static PickHit getPointIntersection(PickRay ray, int x, int y, int z, float pointScale)
  {
    float radius = pointScale * (0.22F + 2.0F * pickView.getScaleFactor(x, y, z));
    float relx = origin.x - x;
    float rely = origin.y - y;
    float relz = origin.z - z;
    
    switch (1.$SwitchMap$editor$camera$ViewType[pickView.type.ordinal()])
    {
    case 1: 
      if (Math.sqrt(relx * relx + rely * rely) < radius) {
        return new PickHit(ray, z);
      }
      return new PickHit(ray);
    
    case 2: 
      if (Math.sqrt(relx * relx + relz * relz) < radius) {
        return new PickHit(ray, y);
      }
      return new PickHit(ray);
    
    case 3: 
      if (Math.sqrt(rely * rely + rely * rely) < radius) {
        return new PickHit(ray, x);
      }
      return new PickHit(ray);
    }
    
    return getSphereIntersection(ray, x, y, z, radius);
  }
  


  public static PickHit getIntersection(PickRay ray, UV uv)
  {
    float radius = 0.22F + 2.0F * pickView.getScaleFactor(uv.getU(), uv.getV(), 2.0F);
    float relx = origin.x - uv.getU();
    float rely = origin.y - uv.getV();
    
    float dist = (float)Math.sqrt(relx * relx + rely * rely);
    
    if (dist < radius) {
      return new PickHit(ray, dist);
    }
    return new PickHit(ray);
  }
  
  private static PickHit getSphereIntersection(PickRay ray, float x, float y, float z, float r)
  {
    Vector3f relative = new Vector3f();
    x = (origin.x - x);
    y = (origin.y - y);
    z = (origin.z - z);
    
    float a = Vector3f.dot(direction, direction);
    float b = 2.0F * Vector3f.dot(direction, relative);
    float c = Vector3f.dot(relative, relative) - r * r;
    
    float discriminant = b * b - 4.0F * a * c;
    

    if (discriminant < 0.0F) {
      return new PickHit(ray, Float.MAX_VALUE);
    }
    
    float distSqrt = (float)Math.sqrt(discriminant);
    float q;
    float q; if (b < 0.0F) {
      q = (-b - distSqrt) / 2.0F;
    } else {
      q = (-b + distSqrt) / 2.0F;
    }
    
    float t0 = q / a;
    float t1 = c / q;
    

    if (t0 > t1)
    {

      float temp = t0;
      t0 = t1;
      t1 = temp;
    }
    


    if (t1 < 0.0F) {
      return new PickHit(ray, Float.MAX_VALUE);
    }
    
    if (t0 < 0.0F) {
      return new PickHit(ray, t1);
    }
    return new PickHit(ray, t0);
  }
}
