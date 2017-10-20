package editor.map.compiler.obj;

import data.texture.EditorTexture;
import editor.map.MapObject;
import editor.map.MapObject.HitType;
import editor.map.hit.Collider;
import editor.map.hit.Zone;
import editor.map.mesh.BasicMesh;
import editor.map.mesh.TexturedMesh;
import editor.map.mesh.TexturedMesh.DisplayListModel;
import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.Model;
import editor.map.shape.TriangleBatch;
import editor.map.shape.UV;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Vector3f;
import util.Logger;
import util.Priority;

public class ObjImporter
{
  private List<Vertex> vertexTable;
  private List<Vector3f> normalTable;
  private List<float[]> uvTable;
  
  public ObjImporter() {}
  
  public List<Collider> readColliders(File f)
    throws IOException
  {
    BufferedReader in = new BufferedReader(new FileReader(f));
    

    vertexTable = new ArrayList();
    normalTable = new ArrayList();
    uvTable = new ArrayList();
    
    List<Collider> colliderList = new ArrayList();
    Collider currentCollider = null;
    TriangleBatch currentBatch = null;
    String line;
    while ((line = in.readLine()) != null)
    {
      line = cleanLine(line);
      if (!line.isEmpty())
      {

        String[] tokens = line.split(" ");
        
        if (tokens[0].equals("g"))
        {
          if (currentCollider != null) {
            currentCollider.updateMeshHierarchy();
          }
          Collider c = new Collider(MapObject.HitType.HIT);
          name = tokens[1];
          
          colliderList.add(c);
          currentCollider = c;
          currentBatch = mesh.batch;

        }
        else if (tokens[0].equals("v")) {
          readVertex(tokens);
        } else if (tokens[0].equals("vn")) {
          readNormal(tokens);
        } else if (tokens[0].equals("vt")) {
          readUV(tokens);
        } else if ((tokens[0].equals("f")) && (tokens.length == 4)) {
          triangles.add(readTriangle(tokens));
        } else {
          Logger.log("Unsupported OBJ keyword: " + line);
        } } }
    in.close();
    
    if (currentCollider != null) {
      currentCollider.updateMeshHierarchy();
    }
    
    fixNormals(colliderList);
    
    return colliderList;
  }
  
  public List<Zone> readZones(File f) throws IOException
  {
    BufferedReader in = new BufferedReader(new FileReader(f));
    

    vertexTable = new ArrayList();
    normalTable = new ArrayList();
    uvTable = new ArrayList();
    
    List<Zone> zoneList = new ArrayList();
    Zone currentZone = null;
    TriangleBatch currentBatch = null;
    String line;
    while ((line = in.readLine()) != null)
    {
      line = cleanLine(line);
      if (!line.isEmpty())
      {

        String[] tokens = line.split(" ");
        
        if (tokens[0].equals("g"))
        {
          if (currentZone != null) {
            currentZone.updateMeshHierarchy();
          }
          Zone z = new Zone(MapObject.HitType.HIT);
          name = tokens[1];
          
          zoneList.add(z);
          currentZone = z;
          currentBatch = mesh.batch;

        }
        else if (tokens[0].equals("v")) {
          readVertex(tokens);
        } else if (tokens[0].equals("vn")) {
          readNormal(tokens);
        } else if (tokens[0].equals("vt")) {
          readUV(tokens);
        } else if ((tokens[0].equals("f")) && (tokens.length == 4)) {
          triangles.add(readTriangle(tokens));
        } else {
          Logger.log("Unsupported OBJ keyword: " + line);
        } } }
    in.close();
    
    if (currentZone != null) {
      currentZone.updateMeshHierarchy();
    }
    
    fixNormals(zoneList);
    
    return zoneList;
  }
  
  public List<Model> readModels(File f) throws IOException
  {
    BufferedReader in = new BufferedReader(new FileReader(f));
    

    vertexTable = new ArrayList();
    normalTable = new ArrayList();
    uvTable = new ArrayList();
    
    List<Model> modelList = new ArrayList();
    Model currentModel = null;
    TriangleBatch currentBatch = new TriangleBatch();
    EditorTexture currentTexture = null;
    String line;
    while ((line = in.readLine()) != null)
    {
      line = cleanLine(line);
      if (!line.isEmpty())
      {

        String[] tokens = line.split(" ");
        
        if (tokens[0].equals("g"))
        {
          if (currentModel != null) {
            currentModel.updateMeshHierarchy();
          }
          Model mdl = Model.createBasicModel();
          modelList.add(mdl);
          
          TriangleBatch batch = new TriangleBatch();
          getMeshdisplayListModel.addElement(batch);
          



          name = tokens[1];
          
          currentModel = mdl;
          currentBatch = batch;
          currentTexture = null;
        }
        else if (tokens[0].equals("usemtl"))
        {
          String texName = tokens[1].substring(2);
          currentModel.getMesh().setTexture(texName);
          currentTexture = getMeshtexture;
        }
        else if (tokens[0].equals("v")) {
          readVertex(tokens);
        } else if (tokens[0].equals("vn")) {
          readNormal(tokens);
        } else if (tokens[0].equals("vt")) {
          readUV(tokens);
        } else if ((tokens[0].equals("f")) && (tokens.length == 4))
        {
          float uScale = EditorTexture.getScaleU(currentTexture);
          float vScale = EditorTexture.getScaleV(currentTexture);
          triangles.add(readTriangle(tokens, uScale, vScale));
        }
        else {
          Logger.log("Unsupported OBJ keyword: " + line);
        } } }
    in.close();
    
    if (currentModel != null) {
      currentModel.updateMeshHierarchy();
    }
    
    fixNormals(modelList);
    
    return modelList;
  }
  
  private String cleanLine(String line)
  {
    line = line.replaceAll(" +|\t+", " ").trim();
    if (line.contains("#"))
    {
      if (line.startsWith("#")) {
        return "";
      }
      line = line.substring(0, line.indexOf("#")).trim();
    }
    
    return line;
  }
  
  private void readVertex(String[] tokens)
  {
    int x = (int)Math.round(Double.parseDouble(tokens[1]));
    int y = (int)Math.round(Double.parseDouble(tokens[2]));
    int z = (int)Math.round(Double.parseDouble(tokens[3]));
    Vertex v = new Vertex(x, y, z);
    normal = new float[3];
    vertexTable.add(v);
  }
  
  private void readNormal(String[] tokens)
  {
    float nx = (float)Double.parseDouble(tokens[1]);
    float ny = (float)Double.parseDouble(tokens[2]);
    float nz = (float)Double.parseDouble(tokens[3]);
    normalTable.add(new Vector3f(nx, ny, nz));
  }
  
  private void readUV(String[] tokens)
  {
    float u = (float)Double.parseDouble(tokens[1]);
    float v = (float)Double.parseDouble(tokens[2]);
    uvTable.add(new float[] { u, v });
  }
  
  private Triangle readTriangle(String[] tokens)
  {
    return readTriangle(tokens, 1.0F, 1.0F);
  }
  
  private Triangle readTriangle(String[] tokens, float uScale, float vScale)
  {
    Vertex[] verts = new Vertex[3];
    
    for (int i = 0; i < 3; i++)
    {
      String[] vertTokens = tokens[(i + 1)].split("/");
      


      switch (vertTokens.length)
      {
      case 1: 
        int vertIndex = Integer.parseInt(vertTokens[0]);
        vertIndex = vertIndex < 0 ? vertexTable.size() + vertIndex : vertIndex - 1;
        verts[i] = ((Vertex)vertexTable.get(vertIndex));
        break;
      case 2: 
        int vertIndex = Integer.parseInt(vertTokens[0]);
        vertIndex = vertIndex < 0 ? vertexTable.size() + vertIndex : vertIndex - 1;
        int uvIndex = Integer.parseInt(vertTokens[1]);
        uvIndex = uvIndex < 0 ? uvTable.size() + uvIndex : uvIndex - 1;
        verts[i] = ((Vertex)vertexTable.get(vertIndex));
        float[] currentUV; float[] currentUV; if (uvTable.size() > uvIndex) {
          currentUV = (float[])uvTable.get(uvIndex);
        } else
          currentUV = new float[] { 0.0F, 0.0F };
        uv = new UV(currentUV[0] * uScale, currentUV[1] * vScale);
        break;
      case 3: 
        int vertIndex = Integer.parseInt(vertTokens[0]);
        vertIndex = vertIndex < 0 ? vertexTable.size() + vertIndex : vertIndex - 1;
        verts[i] = ((Vertex)vertexTable.get(vertIndex));
        
        int normIndex = Integer.parseInt(vertTokens[2]);
        normIndex = normIndex < 0 ? normalTable.size() + normIndex : normIndex - 1;
        Vector3f normal = (Vector3f)normalTable.get(normIndex);
        normal[0] = x;
        normal[1] = y;
        normal[2] = z;
        
        if (!vertTokens[1].isEmpty())
        {
          int uvIndex = Integer.parseInt(vertTokens[1]);
          uvIndex = uvIndex < 0 ? uvTable.size() + uvIndex : uvIndex - 1;
          float[] currentUV; float[] currentUV; if (uvTable.size() > uvIndex) {
            currentUV = (float[])uvTable.get(uvIndex);
          } else
            currentUV = new float[] { 0.0F, 0.0F };
          uv = new UV(currentUV[0] * uScale, currentUV[1] * vScale);
        }
        break;
      }
    }
    return new Triangle(verts[0], verts[1], verts[2]);
  }
  
  private void fixNormals(Iterable<? extends MapObject> objs)
  {
    int fixedNormals = 0;
    for (MapObject obj : objs) {
      for (Triangle t : obj.getMesh())
      {
        float[] currentNormal = t.getNormal();
        if (currentNormal == null) {
          currentNormal = new float[] { 0.0F, 1.0F, 0.0F };
        }
        
        float[] importedNormal = new float[3];
        for (Vertex v : vert)
        {
          importedNormal[0] += normal[0];
          importedNormal[1] += normal[1];
          importedNormal[2] += normal[2];
        }
        

        float dotProduct = currentNormal[0] * importedNormal[0] + currentNormal[1] * importedNormal[1] + currentNormal[2] * importedNormal[2];
        


        if (dotProduct < 0.0F)
        {
          Vertex temp = vert[1];
          vert[1] = vert[2];
          vert[2] = temp;
          
          fixedNormals++;
        }
      }
    }
    if (fixedNormals > 0) {
      Logger.log("Fixed normals for " + fixedNormals + " triangles.", Priority.STANDARD);
    }
  }
}
