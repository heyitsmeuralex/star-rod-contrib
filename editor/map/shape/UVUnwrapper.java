package editor.map.shape;

import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.selection.Selection;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.lwjgl.util.vector.Vector3f;




public class UVUnwrapper
{
  List<UnwrapFace> faces = new ArrayList();
  HashMap<UnwrapEdge, UnwrapEdge> edges = new HashMap();
  HashMap<UnwrapVert, UnwrapVert> verts = new HashMap();
  
  public UVUnwrapper(Iterable<Triangle> triangles)
  {
    float[] center = new float[3];
    for (Triangle t : triangles)
    {
      faces.add(new UnwrapFace(t, null));
      float[] triCenter = t.getCentroid();
      center[0] += triCenter[0];
      center[1] += triCenter[1];
      center[2] += triCenter[2];
    }
    
    for (UnwrapVert v : verts.keySet())
    {
      double dx = meshVertex.getCurrentX() - center[0];
      double dy = meshVertex.getCurrentY() - center[1];
      double dz = meshVertex.getCurrentZ() - center[2];
      
      radius = Math.sqrt(dx * dx + dz * dz);
      double azimuth = 180.0D + Math.toDegrees(Math.atan2(dz, dx));
      double polar = 180.0D + Math.toDegrees(Math.atan2(radius, dy));
      



      u = (meshVertex.uv.getU() / 32.0D);
      v = (meshVertex.uv.getV() / 32.0D);
    }
    double radius;
    float kE = 0.01F;
    float kA = 1.0E-5F;
    
    int maxIterations = 10000;
    

    double forceTolerance = 1.0E-9D;
    
    for (int i = 0; i < maxIterations; i++)
    {
      for (UnwrapVert v : verts.keySet())
      {
        force[0] = 0.0D;
        force[1] = 0.0D;
      }
      double maxForce = 0.0D;
      
      for (UnwrapFace f : faces) {
        f.addForces(kA);
      }
      for (UnwrapEdge e : edges.keySet()) {
        e.addForces(kE);
      }
      for (UnwrapVert v : verts.keySet())
      {
        u += force[0];
        v += force[1];
        double mag = Math.sqrt(force[0] * force[0] + force[1] * force[1]);
        if (mag > maxForce) {
          maxForce = mag;
        }
      }
      System.out.printf("Step %d: Max force = %e\r\n", new Object[] { Integer.valueOf(i), Double.valueOf(maxForce) });
      if (maxForce < forceTolerance) {
        break;
      }
    }
    Selection<UV> uvList = new Selection();
    for (UnwrapVert v : verts.keySet())
      uvList.addWithoutSelecting(meshVertex.uv);
    uvList.startDirectTransformation();
    for (UnwrapVert v : verts.keySet())
      meshVertex.uv.setPosition((short)(int)(16.0D * u), (short)(int)(16.0D * v));
    uvList.endDirectTransformation();
  }
  














  private class UnwrapFace
  {
    private UVUnwrapper.UnwrapVert v1;
    












    private UVUnwrapper.UnwrapVert v2;
    












    private UVUnwrapper.UnwrapVert v3;
    












    private final double Aeq;
    













    private UnwrapFace(Triangle t)
    {
      v1 = new UVUnwrapper.UnwrapVert(UVUnwrapper.this, vert[0], null);
      if (verts.containsKey(v1)) {
        v1 = ((UVUnwrapper.UnwrapVert)verts.get(v1));
      } else {
        verts.put(v1, v1);
      }
      v2 = new UVUnwrapper.UnwrapVert(UVUnwrapper.this, vert[1], null);
      if (verts.containsKey(v2)) {
        v2 = ((UVUnwrapper.UnwrapVert)verts.get(v2));
      } else {
        verts.put(v2, v2);
      }
      v3 = new UVUnwrapper.UnwrapVert(UVUnwrapper.this, vert[2], null);
      if (verts.containsKey(v3)) {
        v3 = ((UVUnwrapper.UnwrapVert)verts.get(v3));
      } else {
        verts.put(v3, v3);
      }
      UVUnwrapper.UnwrapEdge e1 = new UVUnwrapper.UnwrapEdge(UVUnwrapper.this, v1, v2, null);
      if (edges.containsKey(e1)) {
        e1 = (UVUnwrapper.UnwrapEdge)edges.get(e1);
      } else {
        edges.put(e1, e1);
      }
      UVUnwrapper.UnwrapEdge e2 = new UVUnwrapper.UnwrapEdge(UVUnwrapper.this, v2, v3, null);
      if (edges.containsKey(e2)) {
        e2 = (UVUnwrapper.UnwrapEdge)edges.get(e2);
      } else {
        edges.put(e2, e2);
      }
      UVUnwrapper.UnwrapEdge e3 = new UVUnwrapper.UnwrapEdge(UVUnwrapper.this, v3, v1, null);
      if (edges.containsKey(e3)) {
        e3 = (UVUnwrapper.UnwrapEdge)edges.get(e3);
      } else {
        edges.put(e3, e3);
      }
      Aeq = t.getArea();
    }
    
    public double getProjectedArea()
    {
      double Au = v2.u - v1.u;
      double Av = v2.v - v1.v;
      
      double Bu = v3.u - v1.u;
      double Bv = v3.v - v1.v;
      
      double mag = Math.abs(Au * Bv - Av * Bu);
      return mag / 2.0D;
    }
    
    public void addForces(double kA)
    {
      double A = getProjectedArea();
      if (A == 0.0D)
        A = 1.0D;
      double mag = -kA * (A - Aeq);
      
      double cu = (v1.u + v2.u + v3.u) / 3.0D;
      double cv = (v1.v + v2.v + v3.v) / 3.0D;
      


      double du = v1.u - cu;
      double dv = v1.v - cv;
      double dl = Math.sqrt(du * du + dv * dv);
      v1.force[0] += mag * du / dl;
      v1.force[1] += mag * dv / dl;
      
      du = v2.u - cu;
      dv = v2.v - cv;
      dl = Math.sqrt(du * du + dv * dv);
      v2.force[0] += mag * du / dl;
      v2.force[1] += mag * dv / dl;
      
      du = v3.u - cu;
      dv = v3.v - cv;
      dl = Math.sqrt(du * du + dv * dv);
      v3.force[0] += mag * du / dl;
      v3.force[1] += mag * dv / dl;
    }
    



    public String toString()
    {
      return String.format("%s : %s : %s", new Object[] { v1, v2, v3 });
    }
  }
  
  private class UnwrapEdge
  {
    private final UVUnwrapper.UnwrapVert v1;
    private final UVUnwrapper.UnwrapVert v2;
    private final double Leq;
    
    private UnwrapEdge(UVUnwrapper.UnwrapVert v1, UVUnwrapper.UnwrapVert v2) {
      this.v1 = v1;
      this.v2 = v2;
      
      Vector3f rel = new Vector3f();
      Vector3f.sub(meshVertex.getCurrentPos(), meshVertex.getCurrentPos(), rel);
      Leq = rel.length();
    }
    
    public void addForces(double kE)
    {
      double du = v2.u - v1.u;
      double dv = v2.v - v1.v;
      double L = Math.sqrt(du * du + dv * dv);
      if (L == 0.0D)
        L = 1.0D;
      double mag = kE * (1.0D - Leq / L);
      v1.force[0] += du * mag;
      v1.force[1] += dv * mag;
      v2.force[0] += -du * mag;
      v2.force[1] += -dv * mag;
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
      UnwrapEdge other = (UnwrapEdge)obj;
      if ((v1 == v1) && (v2 == v2))
        return true;
      if ((v1 == v2) && (v2 == v1))
        return true;
      return true;
    }
    

    public String toString()
    {
      return String.format("%s --> %s", new Object[] { v1, v2 });
    }
  }
  
  private class UnwrapVert
  {
    final Vertex meshVertex;
    double u;
    double v;
    double[] force;
    
    private UnwrapVert(Vertex v) {
      meshVertex = v;
      force = new double[2];
    }
    

    public int hashCode()
    {
      return meshVertex.hashCode();
    }
    

    public boolean equals(Object obj)
    {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      UnwrapVert other = (UnwrapVert)obj;
      return meshVertex == meshVertex;
    }
    

    public String toString()
    {
      return String.format("(%f, %f)", new Object[] { Double.valueOf(u), Double.valueOf(v) });
    }
  }
}
