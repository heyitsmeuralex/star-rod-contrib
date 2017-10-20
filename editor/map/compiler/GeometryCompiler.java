package editor.map.compiler;

import data.yay0.Yay0Helper;
import editor.map.BoundingBox;
import editor.map.Map;
import editor.map.MapObject.ShapeType;
import editor.map.MutablePoint;
import editor.map.mesh.TexturedMesh;
import editor.map.mesh.TexturedMesh.DisplayListModel;
import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.LightSet;
import editor.map.shape.Model;
import editor.map.shape.TransformMatrix;
import editor.map.shape.TriangleBatch;
import editor.map.shape.commands.DisplayCommand;
import editor.map.tree.MapObjectNode;
import editor.map.tree.MapObjectTreeModel;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import main.Directories;
import org.apache.commons.io.FileUtils;
import util.Logger;
import util.Priority;




















public class GeometryCompiler
{
  private static final int RAM_BASE = -2145320960;
  private RandomAccessFile raf;
  private int vertexTableBase;
  private ArrayList<Vertex> vertexTable;
  private HashMap<Vertex, Integer> vertexMap;
  private ArrayList<TransformMatrix> matrixTable;
  private HashMap<TransformMatrix, Integer> matrixMap;
  private HashMap<String, Integer> textureNameMap;
  private final int buildSize;
  
  public GeometryCompiler(Map map)
    throws IOException
  {
    File build_dec = new File(Directories.MOD_MAP_BUILD + name + "_shape_dec");
    if (build_dec.exists()) {
      build_dec.delete();
    }
    MapObjectNode<Model> rootNode = modelTree.getRoot();
    finalizeBoundingBoxes(rootNode);
    
    raf = new RandomAccessFile(build_dec, "rw");
    raf.seek(32L);
    

    textureNameMap = new HashMap();
    for (Model mdl : modelTree)
    {
      TexturedMesh m = mdl.getMesh();
      if ((!textureNameMap.containsKey(textureName)) && (!textureName.isEmpty()))
      {
        textureNameMap.put(textureName, Integer.valueOf((int)raf.getFilePointer()));
        raf.write(textureName.getBytes());
        raf.write(0);
        raf.seek((int)raf.getFilePointer() + 3 & 0xFFFFFFFC);
      }
    }
    raf.seek((int)raf.getFilePointer() + 15 & 0xFFFFFFF0);
    

    vertexTable = new ArrayList();
    vertexMap = new HashMap();
    buildVertexTable(rootNode);
    

    vertexTableBase = ((int)raf.getFilePointer());
    for (Vertex v : vertexTable)
      raf.write(v.getCompiledRepresentation());
    raf.seek((int)raf.getFilePointer() + 15 & 0xFFFFFFF0);
    

    for (LightSet lightSet : lightSets)
    {
      address = (-2145320960 + (int)raf.getFilePointer());
      lightSet.write(raf);
    }
    

    matrixTable = new ArrayList();
    addUniqueMatricies(rootNode, new HashSet());
    

    matrixMap = new HashMap();
    for (TransformMatrix m : matrixTable)
    {
      matrixMap.put(m, Integer.valueOf((int)raf.getFilePointer()));
      m.writeRDP(raf);
    }
    

    writeDisplayList(rootNode);
    raf.seek((int)raf.getFilePointer() + 15 & 0xFFFFFFF0);
    

    int modelTreeRoot = writeModelTree(rootNode);
    

    raf.seek(0L);
    raf.writeInt(-2145320960 + modelTreeRoot);
    raf.writeInt(-2145320960 + vertexTableBase);
    

    raf.seek(raf.length());
    int nextAlignedOffset = (int)raf.length() + 15 & 0xFFFFFFF0;
    for (int i = 0; i < nextAlignedOffset - raf.length(); i += 4) {
      raf.writeInt(0);
    }
    raf.close();
    
    byte[] complete = FileUtils.readFileToByteArray(build_dec);
    byte[] encoded = Yay0Helper.encode(complete);
    
    buildSize = complete.length;
    File buildFile = new File(Directories.MOD_MAP_BUILD + name + "_shape");
    FileUtils.writeByteArrayToFile(buildFile, encoded);
  }
  
  public int getBuildSize()
  {
    return buildSize;
  }
  

  private void finalizeBoundingBoxes(MapObjectNode<Model> node)
  {
    for (int i = 0; i < node.getChildCount(); i++) {
      finalizeBoundingBoxes(node.getChildAt(i));
    }
    Model mdl = (Model)node.getUserObject();
    localAABB = new BoundingBox();
    
    if (hasMesh) {
      mdl.calculateLocalAABB();
    }
    for (int i = 0; i < node.getChildCount(); i++)
    {
      Model child = (Model)node.getChildAt(i).getUserObject();
      localAABB.encompass(localAABB);
    }
  }
  






  private void addUniqueMatricies(MapObjectNode<Model> node, HashSet<TransformMatrix> matrixSet)
  {
    Model mdl = (Model)node.getUserObject();
    
    if (mdl.getType() != MapObject.ShapeType.MODEL)
    {
      Model group = (Model)node.getUserObject();
      if ((hasTransformMatrix) && (!matrixSet.contains(localTransformMatrix)))
      {
        matrixTable.add(localTransformMatrix);
        matrixSet.add(localTransformMatrix);
      }
    }
    
    for (int i = 0; i < node.getChildCount(); i++)
    {
      addUniqueMatricies(node.getChildAt(i), matrixSet);
    }
  }
  





  private void buildVertexTable(MapObjectNode<Model> node)
  {
    Model mdl = (Model)node.getUserObject();
    
    for (int i = 0; i < node.getChildCount(); i++) {
      buildVertexTable(node.getChildAt(i));
    }
    if (mdl.hasMesh())
    {
      for (Triangle t : mdl.getMesh()) {
        for (Vertex v : vert)
          addVertexToTable(v);
      }
    }
  }
  
  private void addVertexToTable(Vertex v) {
    if (!vertexMap.containsKey(v))
    {
      vertexMap.put(v, Integer.valueOf(vertexTable.size()));
      vertexTable.add(v);
    }
  }
  






  private void writeDisplayList(MapObjectNode<Model> node)
    throws IOException
  {
    for (int i = 0; i < node.getChildCount(); i++)
    {
      MapObjectNode<Model> child = node.getChildAt(i);
      writeDisplayList(child);
    }
    
    Model mdl = (Model)node.getUserObject();
    

    if (hasTransformMatrix)
    {
      raf.writeInt(-633864192);
      raf.writeInt(-2145320960 + ((Integer)matrixMap.get(localTransformMatrix)).intValue());
    }
    

    for (int i = 0; i < node.getChildCount(); i++)
    {
      MapObjectNode<Model> child = node.getChildAt(i);
      
      raf.writeInt(-570425344);
      raf.writeInt(-2145320960 + getUserObjectc_DisplayListOffset);
    }
    
    if (mdl.hasMesh())
    {


      c_DisplayListOffset = ((int)raf.getFilePointer());
      writeMeshDisplayList(mdl.getMesh());
    }
    

    if (hasTransformMatrix)
    {
      raf.writeInt(-667418622);
      raf.writeInt(64);
    }
    
    raf.writeInt(-553648128);
    raf.writeInt(0);
  }
  








  private void writeMeshDisplayList(TexturedMesh mesh)
    throws IOException
  {
    for (int i = 0; i < displayListModel.size(); i++)
    {
      DisplayCommand cmd = (DisplayCommand)displayListModel.getElementAt(i);
      if ((cmd instanceof TriangleBatch))
      {
        writeTriangleList(triangles);
      }
      else
      {
        int[] v = cmd.getF3DEX2Command();
        raf.writeInt(v[0]);
        raf.writeInt(v[1]);
      }
    }
    

















    raf.writeInt(-553648128);
    raf.writeInt(0);
  }
  









  private void writeTriangleList(List<Triangle> triangleList)
    throws IOException
  {
    if ((triangleList == null) || (triangleList.size() == 0)) { return;
    }
    ArrayList<VertexBatch> batchList = getTriangleBatches(triangleList);
    for (VertexBatch batch : batchList) {
      writeTriangleBatch(batch);
    }
  }
  





  private ArrayList<VertexBatch> getTriangleBatches(List<Triangle> triangleList)
  {
    ArrayList<VertexBatch> batchList = new ArrayList();
    VertexBatch batch = new VertexBatch();
    batchList.add(batch);
    

    for (int i = 0; i < triangleList.size(); i++)
    {
      Triangle t = (Triangle)triangleList.get(i);
      if (!batch.addTriangle(t))
      {
        batch = new VertexBatch();
        batchList.add(batch);
        i--;
      }
    }
    
    return batchList;
  }
  
  private void writeTriangleBatch(VertexBatch batch) throws IOException
  {
    ArrayList<Integer> indexList = new ArrayList();
    for (Vertex v : vertexSet) {
      indexList.add(vertexMap.get(v));
    }
    Collections.sort(indexList);
    
    Object indexGroups = new ArrayList();
    IntegerRange currentRange = new IntegerRange(((Integer)indexList.get(0)).intValue());
    ((ArrayList)indexGroups).add(currentRange);
    
    for (Integer i : indexList)
    {
      if (i.intValue() != end)
      {

        if (i.intValue() == end + 1)
        {
          end += 1;
        } else {
          currentRange = new IntegerRange(i.intValue());
          ((ArrayList)indexGroups).add(currentRange);
        }
      }
    }
    Object vertexBuffer = new ArrayList();
    HashMap<Vertex, Integer> vertexBufferMap = new HashMap();
    

    for (IntegerRange range : (ArrayList)indexGroups)
    {
      for (int i = start; i <= end; i++)
      {
        Vertex v = (Vertex)vertexTable.get(i);
        vertexBufferMap.put(v, Integer.valueOf(((ArrayList)vertexBuffer).size()));
        ((ArrayList)vertexBuffer).add(v);
      }
      
      int cmd = 16777216;
      cmd |= range.length() << 12;
      cmd |= 2 * ((ArrayList)vertexBuffer).size();
      raf.writeInt(cmd);
      raf.writeInt(-2145320960 + vertexTableBase + start * 16);
    }
    

    for (int draws = 0; draws < triangleList.size();)
    {



      if (triangleList.size() - draws == 1)
      {
        Triangle t = (Triangle)triangleList.get(draws);
        int code = 83886080;
        code |= 2 * ((Integer)vertexBufferMap.get(vert[0])).intValue() << 16;
        code |= 2 * ((Integer)vertexBufferMap.get(vert[1])).intValue() << 8;
        code |= 2 * ((Integer)vertexBufferMap.get(vert[2])).intValue();
        raf.writeInt(code);
        raf.writeInt(0);
        draws++;
      }
      else
      {
        Triangle t = (Triangle)triangleList.get(draws);
        int code = 100663296;
        code |= 2 * ((Integer)vertexBufferMap.get(vert[0])).intValue() << 16;
        code |= 2 * ((Integer)vertexBufferMap.get(vert[1])).intValue() << 8;
        code |= 2 * ((Integer)vertexBufferMap.get(vert[2])).intValue();
        raf.writeInt(code);
        draws++;
        
        t = (Triangle)triangleList.get(draws);
        code = 0;
        code |= 2 * ((Integer)vertexBufferMap.get(vert[0])).intValue() << 16;
        code |= 2 * ((Integer)vertexBufferMap.get(vert[1])).intValue() << 8;
        code |= 2 * ((Integer)vertexBufferMap.get(vert[2])).intValue();
        raf.writeInt(code);
        draws++;
      }
    }
  }
  

  private class IntegerRange
  {
    public int start;
    public int end;
    
    public IntegerRange(int start)
    {
      this(start, start);
    }
    
    public IntegerRange(int start, int end)
    {
      this.start = start;
      this.end = end;
      if (end < start) {
        throw new RuntimeException("Invalid range generated: " + start + " to " + end);
      }
    }
    
    public int length() {
      return 1 + end - start;
    }
  }
  



  private class VertexBatch
  {
    public ArrayList<Triangle> triangleList;
    

    public HashSet<Vertex> vertexSet;
    

    public static final int VERTEX_BUFFER_SIZE = 32;
    


    public VertexBatch()
    {
      triangleList = new ArrayList(16);
      vertexSet = new HashSet(64);
    }
    
    public boolean addTriangle(Triangle t)
    {
      ArrayList<Vertex> added = new ArrayList(5);
      for (Vertex v : vert) {
        addVertex(added, v);
      }
      if (vertexSet.size() > 32)
      {
        for (??? = added.iterator(); ((Iterator)???).hasNext();) { Vertex v = (Vertex)((Iterator)???).next();
          vertexSet.remove(v); }
        return false;
      }
      triangleList.add(t);
      return true;
    }
    

    private void addVertex(ArrayList<Vertex> added, Vertex v)
    {
      if (!vertexSet.contains(v))
      {
        added.add(v);
        if (!vertexSet.add(v)) {
          throw new RuntimeException("Collision detected when creating vertex batch!");
        }
      }
    }
  }
  






  private int writeModelTree(MapObjectNode<Model> node)
    throws IOException
  {
    Model mdl = (Model)node.getUserObject();
    int nodePosition = -1;
    
    if (mdl.getType() == MapObject.ShapeType.MODEL)
    {
      int propertiesAddress = (int)raf.getFilePointer();
      int numProperties = writeModelProperties(mdl);
      
      raf.writeInt(-2145320960 + c_DisplayListOffset);
      raf.writeInt(0);
      nodePosition = (int)raf.getFilePointer();
      raf.writeInt(2);
      raf.writeInt(-2145320960 + (int)raf.getFilePointer() - 12);
      raf.writeInt(numProperties);
      raf.writeInt(-2145320960 + propertiesAddress);
      raf.writeInt(0);
    }
    else
    {
      ArrayList<Integer> childOffsets = new ArrayList();
      

      for (int i = 0; i < node.getChildCount(); i++)
      {
        MapObjectNode<Model> child = node.getChildAt(i);
        childOffsets.add(Integer.valueOf(writeModelTree(child)));
      }
      
      int propertiesOffset = (int)raf.getFilePointer();
      int numProperties = writeGroupProperties(mdl);
      
      int childListOffset = (int)raf.getFilePointer();
      for (Integer i : childOffsets) {
        raf.writeInt(-2145320960 + i.intValue());
      }
      if (hasTransformMatrix) {
        raf.writeInt(-2145320960 + ((Integer)matrixMap.get(localTransformMatrix)).intValue());
      } else {
        raf.writeInt(0);
      }
      raf.writeInt(lightSet.address);
      raf.writeInt(lightSet.getLightCount());
      
      raf.writeInt(childOffsets.size());
      raf.writeInt(-2145320960 + childListOffset);
      
      raf.writeInt(-2145320960 + c_DisplayListOffset);
      raf.writeInt(0);
      
      nodePosition = (int)raf.getFilePointer();
      raf.writeInt(Model.getIDFromType(mdl.getType()));
      raf.writeInt(-2145320960 + (int)raf.getFilePointer() - 12);
      
      raf.writeInt(numProperties);
      raf.writeInt(-2145320960 + propertiesOffset);
      raf.writeInt(-2145320960 + (int)raf.getFilePointer() - 44);
    }
    
    Logger.log(String.format("Wrote %s to %08X", new Object[] { mdl.toString(), Integer.valueOf(nodePosition) }), Priority.DETAIL);
    return nodePosition;
  }
  
  private int writeGroupProperties(Model mdl)
    throws IOException
  {
    writeBoundingBox(AABB);
    
    int[][] properties = mdl.getProperties();
    for (int i = 0; i < properties.length; i++)
    {
      raf.writeInt(properties[i][0]);
      raf.writeInt(properties[i][1]);
      raf.writeInt(properties[i][2]);
    }
    
    return 6 + properties.length;
  }
  
  private int writeModelProperties(Model mdl) throws IOException
  {
    writeBoundingBox(localAABB);
    

    raf.writeInt(94);
    raf.writeInt(2);
    if (getMeshtextureName.isEmpty()) {
      raf.writeInt(0);
    } else {
      raf.writeInt(-2145320960 + ((Integer)textureNameMap.get(getMeshtextureName)).intValue());
    }
    int[][] properties = mdl.getProperties();
    for (int i = 0; i < properties.length; i++)
    {
      raf.writeInt(properties[i][0]);
      raf.writeInt(properties[i][1]);
      raf.writeInt(properties[i][2]);
    }
    
    return 7 + properties.length;
  }
  
  private void writeBoundingBox(BoundingBox AABB) throws IOException
  {
    raf.writeInt(97);
    raf.writeInt(1);
    raf.writeFloat(min.getX());
    raf.writeInt(97);
    raf.writeInt(1);
    raf.writeFloat(min.getY());
    raf.writeInt(97);
    raf.writeInt(1);
    raf.writeFloat(min.getZ());
    
    raf.writeInt(97);
    raf.writeInt(1);
    raf.writeFloat(max.getX());
    raf.writeInt(97);
    raf.writeInt(1);
    raf.writeFloat(max.getY());
    raf.writeInt(97);
    raf.writeInt(1);
    raf.writeFloat(max.getZ());
  }
}
