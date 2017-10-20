package editor.map.compiler.obj;

import data.texture.EditorTexture;
import editor.map.hit.Collider;
import editor.map.hit.Zone;
import editor.map.mesh.BasicMesh;
import editor.map.mesh.TexturedMesh;
import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.Model;
import editor.map.shape.UV;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import util.IOUtils;

public class ObjExporter
{
  private final PrintWriter pw;
  private final IdentityHashMap<Vertex, Integer> vertexMap;
  private final ArrayList<Vertex> vertexTable;
  
  public ObjExporter(File f) throws FileNotFoundException
  {
    pw = IOUtils.getBufferedPrintWriter(f);
    vertexMap = new IdentityHashMap();
    vertexTable = new ArrayList();
  }
  
  public void writeModels(Iterable<Model> models, String mtlFilename)
  {
    if (!mtlFilename.isEmpty())
    {
      pw.println("mtllib " + mtlFilename);
      pw.println("");
    }
    
    for (Model mdl : models)
    {
      if (mdl.hasMesh())
      {
        exportTexturedMesh(name, mdl.getMesh());
      }
    }
  }
  
  public void exportTexturedMesh(String name, TexturedMesh mesh) {
    int start = vertexTable.size();
    
    for (Triangle t : mesh)
    {
      float[] faceNormal = t.getNormal();
      if (faceNormal == null) {
        faceNormal = new float[] { 0.0F, 1.0F, 0.0F };
      }
      for (Vertex v : vert)
      {
        if (!vertexMap.containsKey(v))
        {
          vertexTable.add(v);
          vertexMap.put(v, Integer.valueOf(vertexTable.size()));
          normal = new float[3];
        }
        normal[0] += faceNormal[0];
        normal[1] += faceNormal[1];
        normal[2] += faceNormal[2];
      }
    }
    
    for (int i = start; i < vertexTable.size(); i++) {
      pw.println("v  " + 
        ((Vertex)vertexTable.get(i)).getCurrentX() + " " + 
        ((Vertex)vertexTable.get(i)).getCurrentY() + " " + 
        ((Vertex)vertexTable.get(i)).getCurrentZ());
    }
    for (int i = start; i < vertexTable.size(); i++) {
      pw.println("vn  " + 
        vertexTable.get(i)).normal[0] + " " + 
        vertexTable.get(i)).normal[1] + " " + 
        vertexTable.get(i)).normal[2]);
    }
    float uScale = EditorTexture.getScaleU(texture);
    float vScale = EditorTexture.getScaleV(texture);
    
    for (int i = start; i < vertexTable.size(); i++) {
      pw.println("vt  " + 
        vertexTable.get(i)).uv.getU() / uScale + " " + 
        vertexTable.get(i)).uv.getV() / vScale);
    }
    pw.println("");
    
    pw.println("g " + name);
    if (textured) {
      pw.println("usemtl m_" + textureName);
    }
    for (Triangle t : mesh)
    {
      int i = ((Integer)vertexMap.get(vert[0])).intValue();
      int j = ((Integer)vertexMap.get(vert[1])).intValue();
      int k = ((Integer)vertexMap.get(vert[2])).intValue();
      
      pw.println("f  " + i + "/" + i + " " + j + "/" + j + " " + k + "/" + k);
    }
    



    pw.println("");
  }
  
  public void writeColliders(Iterable<Collider> colliders)
  {
    for (Collider c : colliders)
    {
      if (c.hasMesh())
      {
        exportBasicMesh(name, mesh);
      }
    }
  }
  
  public void writeZones(Iterable<Zone> zones) {
    for (Zone z : zones)
    {
      if (z.hasMesh())
      {
        exportBasicMesh(name, mesh);
      }
    }
  }
  
  public void exportBasicMesh(String name, BasicMesh mesh) {
    int start = vertexTable.size();
    
    for (Triangle t : mesh)
    {
      float[] faceNormal = t.getNormal();
      if (faceNormal == null)
        faceNormal = new float[] { 0.0F, 1.0F, 0.0F };
      for (Vertex v : vert)
      {
        if (!vertexMap.containsKey(v))
        {
          vertexTable.add(v);
          vertexMap.put(v, Integer.valueOf(vertexTable.size()));
          normal = new float[3];
        }
        normal[0] += faceNormal[0];
        normal[1] += faceNormal[1];
        normal[2] += faceNormal[2];
      }
    }
    
    for (int i = start; i < vertexTable.size(); i++) {
      pw.println("v  " + 
        ((Vertex)vertexTable.get(i)).getCurrentX() + " " + 
        ((Vertex)vertexTable.get(i)).getCurrentY() + " " + 
        ((Vertex)vertexTable.get(i)).getCurrentZ());
    }
    for (int i = start; i < vertexTable.size(); i++) {
      pw.println("vn  " + 
        vertexTable.get(i)).normal[0] + " " + 
        vertexTable.get(i)).normal[1] + " " + 
        vertexTable.get(i)).normal[2]);
    }
    pw.println("");
    
    pw.println("g " + name);
    for (Triangle t : mesh)
    {
      int i = ((Integer)vertexMap.get(vert[0])).intValue();
      int j = ((Integer)vertexMap.get(vert[1])).intValue();
      int k = ((Integer)vertexMap.get(vert[2])).intValue();
      
      pw.println("f  " + i + "/" + i + " " + j + "/" + j + " " + k + "/" + k);
    }
    



    pw.println("");
  }
  
  public void close()
  {
    pw.close();
  }
}
