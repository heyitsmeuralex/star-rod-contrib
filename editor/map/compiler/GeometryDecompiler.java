package editor.map.compiler;

import editor.map.Map;
import editor.map.MapObject.ShapeType;
import editor.map.mesh.TexturedMesh;
import editor.map.mesh.TexturedMesh.DisplayListModel;
import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.LightSet;
import editor.map.shape.Model;
import editor.map.shape.TransformMatrix;
import editor.map.shape.TriangleBatch;
import editor.map.shape.UV;
import editor.map.shape.commands.ChangeGeometryFlags;
import editor.map.shape.commands.FlushPipeline;
import editor.map.tree.MapObjectNode;
import editor.map.tree.MapObjectTreeModel;
import editor.map.tree.ModelTreeModel;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import util.IOUtils;
import util.Logger;
import util.Priority;











public class GeometryDecompiler
{
  private HashMap<Integer, Vertex> vertexMap;
  private Vertex[] vertexTable;
  private HashSet<String> textureList = new HashSet();
  
  private LinkedList<Model> meshList;
  
  private LinkedHashMap<Integer, LightSet> lightSets;
  
  private final Map map;
  private final ByteBuffer bb;
  private boolean foundRoot = false;
  private int[] rootAABB = new int[6];
  private static final int BASE_ADDRESS = -2145320960;
  
  public GeometryDecompiler(Map m, File source)
    throws IOException
  {
    map = m;
    bb = IOUtils.getDirectBuffer(source);
    
    meshList = new LinkedList();
    vertexTable = new Vertex[64];
    
    lightSets = new LinkedHashMap();
    
    readObjectNames();
    
    bb.position(0);
    bb.position(bb.getInt() - -2145320960);
    
    MapObjectNode<Model> root = readNode(null).getNode();
    map.modelTree = new ModelTreeModel(root);
    
    Model rootModel = (Model)root.getUserObject();
    cumulativeTransformMatrix = new TransformMatrix();
    cumulativeTransformMatrix.setIdentity();
    rootModel.updateTransformHierarchy();
    
    assignNames(map.modelTree.getRoot());
    map.modelTree.recalculateIndicies();
    
    map.lightSets.addAll(lightSets.values());
  }
  
  private void assignNames(MapObjectNode<Model> node)
  {
    Model mdl = (Model)node.getUserObject();
    
    for (int i = 0; i < node.getChildCount(); i++) {
      assignNames(node.getChildAt(i));
    }
    if (mdl.getType() != MapObject.ShapeType.ROOT) {
      name = ((String)map.modelNames.poll());
    }
  }
  

  private void readObjectNames()
  {
    map.modelNames = new LinkedList();
    map.colliderNames = new LinkedList();
    map.zoneNames = new LinkedList();
    
    bb.position(8);
    int addr = bb.getInt();
    if (addr != 0)
    {
      bb.position(addr - -2145320960);
      String name; while (!(name = getName(bb.getInt())).equals("db"))
      {
        assert (!map.modelNames.contains(name));
        map.modelNames.add(name);
      }
    }
    
    bb.position(12);
    addr = bb.getInt();
    if (addr != 0)
    {
      bb.position(addr - -2145320960);
      String name; while (!(name = getName(bb.getInt())).equals("db"))
      {
        assert (!map.colliderNames.contains(name));
        map.colliderNames.add(name);
      }
    }
    
    bb.position(16);
    addr = bb.getInt();
    if (addr != 0)
    {
      bb.position(addr - -2145320960);
      String name; while (!(name = getName(bb.getInt())).equals("db"))
      {
        assert (!map.zoneNames.contains(name));
        map.zoneNames.add(name);
      }
    }
  }
  
  private String getName(int addr)
  {
    int oldPos = bb.position();
    bb.position(addr - -2145320960);
    
    StringBuilder sb = new StringBuilder();
    byte b;
    while ((b = bb.get()) != 0) {
      sb.append((char)b);
    }
    bb.position(oldPos);
    return sb.toString();
  }
  









  private Model readNode(MapObjectNode<Model> parent)
  {
    MapObject.ShapeType type = Model.getTypeFromID(bb.getInt());
    
    if (type == MapObject.ShapeType.ROOT)
    {
      assert (!foundRoot) : "Model tree contains duplicate root.";
      foundRoot = true;
    }
    
    Model mdl = new Model(type);
    fileOffset = (bb.position() - 4);
    dumped = true;
    
    switch (1.$SwitchMap$editor$map$MapObject$ShapeType[type.ordinal()]) {
    case 1: 
      name = String.format("Root", new Object[] { Integer.valueOf(fileOffset) }); break;
    case 2: case 3: 
      name = String.format("Group %08X", new Object[] { Integer.valueOf(fileOffset) }); break;
    case 4:  name = String.format("Model %08X", new Object[] { Integer.valueOf(fileOffset) });
    }
    
    int ptrDisplayData = bb.getInt();
    int numProperties = bb.getInt();
    int ptrPropertyList = bb.getInt();
    int ptrGroupData = bb.getInt();
    
    assert (ptrDisplayData != 0);
    assert (numProperties >= 6);
    assert (ptrPropertyList != 0);
    if (type == MapObject.ShapeType.MODEL) {
      if ((!$assertionsDisabled) && (ptrGroupData != 0)) throw new AssertionError("Model nodes should not have group data.");
    } else {
      assert (ptrGroupData != 0) : "Group nodes must have have group data.";
    }
    
    if (type == MapObject.ShapeType.ROOT)
    {


      rootAABB = new int[] {bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt(), bb.getInt() };
    }
    
    bb.position(ptrDisplayData - -2145320960);
    int ptrDisplayList = bb.getInt();
    int zero = bb.getInt();
    assert (zero == 0);
    
    if (type == MapObject.ShapeType.MODEL) {
      readModelDisplayList(mdl, ptrDisplayList);
    } else {
      readGroupDisplayList(mdl, ptrDisplayList);
    }
    if (type == MapObject.ShapeType.MODEL) {
      readModelProperties(mdl, ptrPropertyList, numProperties);
    } else {
      readGroupProperties(mdl, ptrPropertyList, numProperties);
    }
    if (type != MapObject.ShapeType.MODEL) {
      readGroupData(mdl, ptrGroupData);
    }
    if (type != MapObject.ShapeType.ROOT)
    {
      getNodeparentNode = parent;
      getNodechildIndex = parent.getChildCount();
      parent.add(mdl.getNode());
    }
    
    return mdl;
  }
  
  private void readModelProperties(Model mdl, int ptrPropertyList, int num)
  {
    bb.position(ptrPropertyList - -2145320960);
    


    for (int i = 0; i < 6; i++)
    {
      int[] property = { bb.getInt(), bb.getInt(), bb.getInt() };
      assert (property[0] == 97);
      assert (property[1] == 1);
    }
    

    int[] property = { bb.getInt(), bb.getInt(), bb.getInt() };
    assert (property[0] == 94);
    assert (property[1] == 2);
    int ptrTextureName = property[2];
    
    int[][] properties = new int[num - 7][3];
    
    for (int i = 0; i < properties.length; i++)
    {
      properties[i][0] = bb.getInt();
      properties[i][1] = bb.getInt();
      properties[i][2] = bb.getInt();
    }
    
    mdl.setProperties(properties);
    















    if (ptrTextureName != 0)
    {
      bb.position(ptrTextureName - -2145320960);
      String textureName = IOUtils.readString(bb, 48);
      getMeshtextureName = textureName;
      if (!textureList.contains(textureName)) {
        textureList.add(textureName);
      }
    }
  }
  





  private void readGroupProperties(Model mdl, int ptrPropertyList, int num)
  {
    bb.position(ptrPropertyList - -2145320960);
    


    for (int i = 0; i < 6; i++)
    {
      int[] property = { bb.getInt(), bb.getInt(), bb.getInt() };
      assert (property[0] == 97);
      assert (property[1] == 1);
    }
    
    if (num > 6)
    {
      assert (num == 8) : num;
      
      int[][] properties = new int[2][3];
      properties[0] = { bb.getInt(), bb.getInt(), bb.getInt() };
      properties[1] = { bb.getInt(), bb.getInt(), bb.getInt() };
      
      assert (properties[0][0] == 96);
      assert (properties[0][1] == 0);
      assert (properties[1][0] == 96);
      assert (properties[1][1] == 0);
      
      mdl.setProperties(properties);
    }
  }
  
  private void readGroupData(Model mdl, int ptrGroupData)
  {
    bb.position(ptrGroupData - -2145320960);
    int ptrTransformMatrix = bb.getInt();
    int ptrLightSet = bb.getInt();
    numLights = bb.getInt();
    int numChildren = bb.getInt();
    int ptrChildList = bb.getInt();
    
    assert (numChildren > 0);
    assert (ptrChildList != 0);
    
    bb.position(ptrChildList - -2145320960);
    
    int[] ptrChildren = new int[numChildren];
    for (int i = 0; i < numChildren; i++) {
      ptrChildren[i] = bb.getInt();
    }
    for (int i = 0; i < numChildren; i++)
    {
      bb.position(ptrChildren[i] - -2145320960);
      readNode(mdl.getNode());
    }
    
    if (ptrTransformMatrix != 0)
    {
      bb.position(ptrTransformMatrix - -2145320960);
      localTransformMatrix.readRDP(bb);
      hasTransformMatrix = true;
    }
    
    if (ptrLightSet == 0) {
      throw new RuntimeException(String.format("Model group %s does not have a light set.\r\n", new Object[] { name }));
    }
    LightSet lightSet = (LightSet)lightSets.get(Integer.valueOf(ptrLightSet));
    if (lightSet == null)
    {
      lightSet = new LightSet();
      lightSets.put(Integer.valueOf(ptrLightSet), lightSet);
      listIndex = (lightSets.size() - 1);
      
      bb.position(ptrLightSet - -2145320960);
      lightSet.get(bb, numLights);
      name = String.format("Lights_%08X", new Object[] { Integer.valueOf(ptrLightSet) });
    }
    else if (lightSet.getLightCount() != numLights) {
      throw new RuntimeException(String.format("Inconsistent size for light set at %08X\r\n", new Object[] { Integer.valueOf(ptrLightSet) }));
    }
    lightSet = lightSet;
  }
  
  private void readModelDisplayList(Model mdl, int ptrDisplayList)
  {
    hasMesh = true;
    mdl.setMesh(new TexturedMesh());
    
    bb.position(ptrDisplayList - -2145320960);
    readMeshFromDisplayList(mdl.getMesh());
    



    meshList.add(mdl);
  }
  
  private void readGroupDisplayList(Model mdl, int ptrDisplayList)
  {
    bb.position(ptrDisplayList - -2145320960);
    
    int cmd = bb.getInt();
    while ((cmd & 0xFF000000) != -553648128)
    {
      if ((cmd & 0xFF000000) == -419430400)
      {
        hasMesh = true;
        mdl.setMesh(new TexturedMesh());
        
        bb.position(bb.position() - 4);
        readMeshFromDisplayList(mdl.getMesh());
        meshList.add(mdl);
        break;
      }
      cmd = bb.getInt();
    }
  }
  


































































  private void readMeshFromDisplayList(TexturedMesh mesh)
  {
    vertexMap = new HashMap();
    boolean readingTriangles = false;
    TriangleBatch currentBatch = new TriangleBatch();
    
    boolean done = false;
    int code = bb.getInt();
    int arg = bb.getInt();
    
    while (!done)
    {
      switch (code & 0xFF000000)
      {
      case -419430400: 
        if (readingTriangles)
        {
          displayListModel.addElement(currentBatch);
          readingTriangles = false;
          currentBatch = new TriangleBatch();
        }
        displayListModel.addElement(new FlushPipeline());
        if ((!$assertionsDisabled) && (arg != 0)) { throw new AssertionError();
        }
        break;
      case -654311424: 
        if (readingTriangles)
        {
          displayListModel.addElement(currentBatch);
          readingTriangles = false;
          currentBatch = new TriangleBatch();
        }
        
        displayListModel.addElement(ChangeGeometryFlags.getCommand(code, arg));
        break;
      

      case 16777216: 
        bb.position(bb.position() - 8);
        buildVertexTable(bb);
        break;
      
      case 100663296: 
        Triangle t1 = buildTriangle(code);
        triangles.add(t1);
        
        Triangle t2 = buildTriangle(arg);
        triangles.add(t2);
        
        readingTriangles = true;
        break;
      
      case 83886080: 
        Triangle t = buildTriangle(code);
        triangles.add(t);
        assert (arg == 0);
        
        readingTriangles = true;
        break;
      
      case -553648128: 
        displayListModel.addElement(currentBatch);
        done = true;
        if ((!$assertionsDisabled) && (arg != 0)) { throw new AssertionError();
        }
        break;
      default: 
        Logger.log("Unsupported F3DEX2 command: " + String.format("%02X", new Object[] { Byte.valueOf((byte)(code >> 6)) }), Priority.IMPORTANT);
        Logger.log("File pointer: 0x" + String.format("%08X", new Object[] { Integer.valueOf(bb.position()) }), Priority.IMPORTANT);
      }
      
      
      if (!done)
      {
        code = bb.getInt();
        arg = bb.getInt();
      }
    }
    
    mesh.updateHierarchy();
  }
  










  private Triangle buildTriangle(int command)
  {
    int index1 = command >> 16 & 0xFF;
    int index2 = command >> 8 & 0xFF;
    int index3 = command & 0xFF;
    

    Vertex v1 = vertexTable[(index1 / 2)];
    Vertex v2 = vertexTable[(index2 / 2)];
    Vertex v3 = vertexTable[(index3 / 2)];
    
    Triangle t = new Triangle(v1, v2, v3);
    return t;
  }
  









  private void buildVertexTable(ByteBuffer bb)
  {
    int cmd = bb.getInt();
    
    while ((cmd & 0xFF000000) == 16777216)
    {
      int num = cmd >>> 12 & 0xFFF;
      int end = (cmd & 0xFFF) / 2;
      int addr = bb.getInt();
      Logger.log("Adding " + num + " verticies from " + String.format("%08X", new Object[] { Integer.valueOf(addr) }), Priority.DETAIL);
      
      int start = end - num;
      for (int i = start; i < end; i++)
      {
        int vertexAddress = makeAddress(addr, i - start);
        if (!vertexMap.containsKey(Integer.valueOf(vertexAddress)))
          readVertexData(vertexAddress);
        vertexTable[i] = ((Vertex)vertexMap.get(Integer.valueOf(vertexAddress)));
      }
      
      cmd = bb.getInt();
    }
    
    bb.position(bb.position() - 4);
  }
  






  private void readVertexData(int addr)
  {
    int previousPosition = bb.position();
    bb.position(addr - -2145320960);
    
    Vertex v = new Vertex(bb.getShort(), bb.getShort(), bb.getShort());
    
    short unknown = bb.getShort();
    if (unknown != 0) {
      Logger.log("Vertex with flag " + unknown + " at " + String.format("%08X", new Object[] { Integer.valueOf(bb.position()) }), Priority.IMPORTANT);
    }
    short ucoord = bb.getShort();
    short vcoord = bb.getShort();
    uv = new UV(ucoord, vcoord);
    r = bb.get();
    g = bb.get();
    b = bb.get();
    a = bb.get();
    
    vertexMap.put(Integer.valueOf(-2145320960 + bb.position() - 16), v);
    
    bb.position(previousPosition);
  }
  




  private static final int makeAddress(int base, int offset)
  {
    return base + ((offset & 0xFF) << 4);
  }
}
