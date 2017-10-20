package editor.map.compiler;

import editor.map.BoundingBox;
import editor.map.Map;
import editor.map.hit.Collider;
import editor.map.hit.HitObject;
import editor.map.hit.Zone;
import editor.map.mesh.AbstractMesh;
import editor.map.mesh.BasicMesh;
import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.TriangleBatch;
import editor.map.tree.MapObjectNode;
import editor.map.tree.MapObjectTreeModel;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import main.Directories;
import org.apache.commons.io.FileUtils;
import org.lwjgl.util.vector.Vector3f;

public class CollisionCompiler
{
  public CollisionCompiler(Map map) throws IOException
  {
    File build_dec = new File(Directories.MOD_MAP_BUILD + name + "_hit_dec");
    if (build_dec.exists())
      build_dec.delete();
    RandomAccessFile raf = new RandomAccessFile(build_dec, "rw");
    
    int colliderHeaderOffset = compileColliders(raf, map);
    int zoneHeaderOffset = compileZones(raf, map);
    
    raf.seek(0L);
    raf.writeInt(colliderHeaderOffset);
    raf.writeInt(zoneHeaderOffset);
    raf.close();
    
    byte[] complete = FileUtils.readFileToByteArray(build_dec);
    byte[] encoded = data.yay0.Yay0Helper.encode(complete);
    
    File build = new File(Directories.MOD_MAP_BUILD + name + "_hit");
    FileUtils.writeByteArrayToFile(build, encoded);
  }
  
  private int compileColliders(RandomAccessFile raf, Map map) throws IOException
  {
    MapObjectNode<Collider> colliderRoot = colliderTree.getRoot();
    int num = colliderRoot.countDescendents();
    ArrayList<Collider> colliderList = new ArrayList(num);
    
    constructArray(colliderList, colliderRoot);
    finalizeBoundingBoxes(colliderRoot);
    


    ArrayList<Vertex> vertexList = new ArrayList();
    HashMap<Vertex, Integer> vertexMap = new HashMap();
    
    for (Iterator localIterator1 = colliderList.iterator(); localIterator1.hasNext();) { c = (Collider)localIterator1.next();
      for (localIterator2 = c.getMesh().iterator(); localIterator2.hasNext();) { t = (Triangle)localIterator2.next();
        for (Vertex v : vert)
          if (!vertexMap.containsKey(v))
          {
            vertexMap.put(v, Integer.valueOf(vertexList.size()));
            vertexList.add(v); } } }
    Collider c;
    Iterator localIterator2;
    if (vertexList.size() > 1024)
    {
      String err = "Maximum number of verticies exceeded: (" + vertexList.size() + " / 1024).";
      util.Logger.log("Collision Compile Error: " + err, util.Priority.ERROR);
      throw new BuildException(err);
    }
    


    int colliderVertexOffset = 16;
    raf.seek(colliderVertexOffset);
    
    for (Vertex v : vertexList)
    {
      raf.writeShort((short)v.getCurrentX());
      raf.writeShort((short)v.getCurrentY());
      raf.writeShort((short)v.getCurrentZ());
    }
    
    if ((raf.length() & 0x3) == 2L) {
      raf.writeShort(0);
    }
    

    for (int i = colliderList.size() - 1; i >= 0; i--)
    {
      Collider c = (Collider)colliderList.get(i);
      c_TriangleOffset = ((int)raf.getFilePointer());
      
      for (Triangle t : c.getMesh())
      {
        int index1 = ((Integer)vertexMap.get(vert[0])).intValue() & 0x3FF;
        int index2 = ((Integer)vertexMap.get(vert[1])).intValue() & 0x3FF;
        int index3 = ((Integer)vertexMap.get(vert[2])).intValue() & 0x3FF;
        
        int triangle = doubleSided ? 0 : 1073741824;
        triangle |= index1;
        triangle |= index2 << 10;
        triangle |= index3 << 20;
        
        raf.writeInt(triangle);
      }
    }
    


    int colliderMeshOffset = (int)raf.getFilePointer();
    short aabbOffset = 0;
    for (Triangle t = colliderList.iterator(); t.hasNext();) { c = (Collider)t.next();
      
      raf.writeShort(aabbOffset);
      raf.writeShort(c_NextIndex);
      raf.writeShort(c_ChildIndex);
      
      if (((Collider)c).hasMesh())
      {
        int triangleCount = mesh.batch.triangles.size();
        raf.writeShort(triangleCount);
        raf.writeInt(c_TriangleOffset);
      }
      else
      {
        raf.writeShort(0);
        raf.writeInt(0);
      }
      
      aabbOffset = (short)(aabbOffset + 7);
    }
    


    int colliderBoundingOffset = (int)raf.getFilePointer();
    for (Object c = colliderList.iterator(); ((Iterator)c).hasNext();) { Collider c = (Collider)((Iterator)c).next();
      
      Vector3f min = AABB.getMin();
      Vector3f max = AABB.getMax();
      raf.writeFloat(x);
      raf.writeFloat(y);
      raf.writeFloat(z);
      raf.writeFloat(x);
      raf.writeFloat(y);
      raf.writeFloat(z);
      raf.writeInt(flags);
    }
    


    int colliderHeaderOffset = (int)raf.getFilePointer();
    
    raf.writeShort(colliderList.size());
    raf.writeShort(0);
    raf.writeInt(colliderMeshOffset);
    
    raf.writeShort(vertexList.size());
    raf.writeShort(0);
    raf.writeInt(colliderVertexOffset);
    
    raf.writeShort(colliderList.size() * 7);
    raf.writeShort(0);
    raf.writeInt(colliderBoundingOffset);
    
    while ((raf.length() & 0xF) != 0L) {
      raf.write(0);
    }
    return colliderHeaderOffset;
  }
  
  private int compileZones(RandomAccessFile raf, Map map) throws IOException
  {
    MapObjectNode<Zone> zoneRoot = zoneTree.getRoot();
    int num = zoneRoot.countDescendents();
    ArrayList<Zone> zoneList = new ArrayList(num);
    
    constructArray(zoneList, zoneRoot);
    finalizeBoundingBoxes(zoneRoot);
    


    ArrayList<Vertex> vertexList = new ArrayList();
    HashMap<Vertex, Integer> vertexMap = new HashMap();
    
    for (Iterator localIterator1 = zoneList.iterator(); localIterator1.hasNext();) { z = (Zone)localIterator1.next();
      for (localIterator2 = z.getMesh().iterator(); localIterator2.hasNext();) { t = (Triangle)localIterator2.next();
        for (Vertex v : vert)
          if (!vertexMap.containsKey(v))
          {
            vertexMap.put(v, Integer.valueOf(vertexList.size()));
            vertexList.add(v);
          } } }
    Zone z;
    Iterator localIterator2;
    Triangle t;
    int areaVertexOffset = (int)raf.getFilePointer();
    
    for (Vertex v : vertexList)
    {
      raf.writeShort((short)v.getCurrentX());
      raf.writeShort((short)v.getCurrentY());
      raf.writeShort((short)v.getCurrentZ());
    }
    
    if ((raf.length() & 0x3) == 2L) {
      raf.writeShort(0);
    }
    int index3;
    int triangle;
    for (int i = zoneList.size() - 1; i >= 0; i--)
    {
      Zone z = (Zone)zoneList.get(i);
      c_TriangleOffset = ((int)raf.getFilePointer());
      for (Triangle t : z.getMesh())
      {
        int index1 = ((Integer)vertexMap.get(vert[0])).intValue() & 0x3FF;
        int index2 = ((Integer)vertexMap.get(vert[1])).intValue() & 0x3FF;
        index3 = ((Integer)vertexMap.get(vert[2])).intValue() & 0x3FF;
        
        triangle = doubleSided ? 0 : 1073741824;
        triangle |= index1;
        triangle |= index2 << 10;
        triangle |= index3 << 20;
        
        raf.writeInt(triangle);
      }
    }
    


    int areaMeshOffset = (int)raf.getFilePointer();
    int cameraOffset = 0;
    for (Zone z : zoneList)
    {
      if (hasCameraData)
      {
        raf.writeShort(cameraOffset);
        c_CameraOffset = (cameraOffset * 4);
        cameraOffset += 11;
      }
      else
      {
        raf.writeShort(-1);
      }
      
      raf.writeShort(c_NextIndex);
      raf.writeShort(c_ChildIndex);
      
      triangleCount = mesh.batch.triangles.size();
      raf.writeShort(triangleCount);
      
      if (triangleCount > 0) {
        raf.writeInt(c_TriangleOffset);
      } else {
        raf.writeInt(0);
      }
    }
    int triangleCount;
    int zoneDataOffset = (int)raf.getFilePointer();
    int zoneDataSize = 0;
    for (Zone z : zoneList)
    {
      if (c_CameraOffset >= 0)
      {
        raf.seek(zoneDataOffset + c_CameraOffset);
        for (int i : cam.getData())
          raf.writeInt(i);
        zoneDataSize += 44;
      }
    }
    
    raf.seek(zoneDataOffset + zoneDataSize);
    


    int zoneHeaderOffset = (int)raf.getFilePointer();
    
    raf.writeShort(zoneList.size());
    raf.writeShort(0);
    raf.writeInt(areaMeshOffset);
    
    raf.writeShort(vertexList.size());
    raf.writeShort(0);
    raf.writeInt(areaVertexOffset);
    
    raf.writeShort(zoneDataSize / 4);
    raf.writeShort(0);
    raf.writeInt(zoneDataOffset);
    
    while ((raf.length() & 0xF) != 0L) {
      raf.write(0);
    }
    return zoneHeaderOffset;
  }
  
  private <T extends HitObject> void constructArray(ArrayList<T> hitObjects, MapObjectNode<T> node)
  {
    for (int i = 0; i < node.getChildCount(); i++)
    {
      MapObjectNode<T> child = node.getChildAt(i);
      
      if (i == 0) {
        getUserObjectc_ChildIndex = child.getTreeIndex();
      }
      if (i != node.getChildCount() - 1) {
        getUserObjectc_NextIndex = node.getChildAt(i + 1).getTreeIndex();
      }
      constructArray(hitObjects, node.getChildAt(i));
    }
    
    if (!node.isRoot()) {
      hitObjects.add(node.getUserObject());
    }
  }
  
  private <T extends HitObject> void finalizeBoundingBoxes(MapObjectNode<T> node) {
    HitObject obj = (HitObject)node.getUserObject();
    AABB = new BoundingBox();
    
    for (int i = 0; i < node.getChildCount(); i++)
    {
      MapObjectNode<T> childNode = node.getChildAt(i);
      HitObject childObj = (HitObject)childNode.getUserObject();
      
      switch (1.$SwitchMap$editor$map$MapObject$HitType[childObj.getType().ordinal()])
      {
      case 1: 
        AABB.encompass(AABB);
        break;
      case 2: 
      case 3: 
        finalizeBoundingBoxes(childNode);
        AABB.encompass(AABB);
      }
    }
  }
}
