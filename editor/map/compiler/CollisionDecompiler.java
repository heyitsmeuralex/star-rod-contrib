package editor.map.compiler;

import editor.map.BoundingBox;
import editor.map.Map;
import editor.map.MapObject.HitType;
import editor.map.hit.Collider;
import editor.map.hit.Zone;
import editor.map.mesh.BasicMesh;
import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.TriangleBatch;
import editor.map.tree.ColliderTreeModel;
import editor.map.tree.MapObjectNode;
import editor.map.tree.MapObjectTreeModel;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import util.Logger;

public class CollisionDecompiler
{
  private Map map;
  private ByteBuffer bb;
  private List<Vertex> vertexList;
  
  public CollisionDecompiler(Map map, File source) throws java.io.IOException
  {
    this.map = map;
    bb = util.IOUtils.getDirectBuffer(source);
    
    int[] fileHeader = new int[4];
    for (int i = 0; i < fileHeader.length; i++) {
      fileHeader[i] = bb.getInt();
    }
    if (fileHeader[0] != 0) {
      readColliders(fileHeader[0]);
    }
    if (fileHeader[1] != 0) {
      readZones(fileHeader[1]);
    }
  }
  
  private void readColliders(int offset) {
    bb.position(offset);
    
    int numColliders = bb.getInt() >> 16;
    int colliderTableOffset = bb.getInt();
    
    int numVertices = bb.getInt() >> 16;
    int vertexTableOffset = bb.getInt();
    
    bb.getInt();
    int boxTableOffset = bb.getInt();
    
    bb.position(vertexTableOffset);
    vertexList = new ArrayList();
    for (int i = 0; i < numVertices; i++)
    {
      Vertex v = new Vertex(bb.getShort(), bb.getShort(), bb.getShort());
      vertexList.add(v);
    }
    
    Collider rootCollider = Collider.makeDummyRoot();
    map.colliderTree = new ColliderTreeModel(rootCollider.getNode());
    
    List<MapObjectNode<Collider>> nodes = new ArrayList(numColliders);
    int[] children = new int[numColliders];
    int[] next = new int[numColliders];
    int[] parent = new int[numColliders];
    

    for (int i = 0; i < numColliders; i++)
    {
      bb.position(colliderTableOffset + i * 12);
      int fileOffset = bb.position();
      
      int boxEntryOffset = 4 * bb.getShort();
      next[i] = bb.getShort();
      children[i] = bb.getShort();
      int numTriangles = bb.getShort();
      int triangleOffset = bb.getInt();
      
      Collider c = null;
      

      if (numTriangles == 0)
      {
        c = new Collider(MapObject.HitType.GROUP);
      }
      else
      {
        c = new Collider(MapObject.HitType.HIT);
        
        bb.position(triangleOffset);
        for (int j = 0; j < numTriangles; j++)
        {
          Triangle t = makeTriangle(vertexList, bb.getInt());
          mesh.batch.triangles.add(t);
        }
      }
      
      bb.position(boxTableOffset + boxEntryOffset);
      Vertex v1 = new Vertex((int)bb.getFloat(), (int)bb.getFloat(), (int)bb.getFloat());
      Vertex v2 = new Vertex((int)bb.getFloat(), (int)bb.getFloat(), (int)bb.getFloat());
      
      AABB.encompass(v1);
      AABB.encompass(v2);
      flags = bb.getInt();
      
      dumped = true;
      fileOffset = fileOffset;
      

      c.updateMeshHierarchy();
      
      nodes.add(c.getNode());
      parent[i] = -1;
      name = ((String)map.colliderNames.poll());
      
      switch (flags) {
      case 0: 
      case 32768: 
      case 98304: 
        break;
      default: 
        System.out.printf("Unknown collider flags: %08X %6s %s\n", new Object[] { Integer.valueOf(flags), map.name, name });
      }
      
    }
    MapObjectNode<Collider> parentNode = null;
    MapObjectNode<Collider> childNode = null;
    

    for (int i = 0; i < numColliders; i++)
    {
      int child = children[i];
      
      while (child != -1)
      {
        parentNode = (MapObjectNode)nodes.get(i);
        childNode = (MapObjectNode)nodes.get(child);
        parent[child] = i;
        
        parentNode = parentNode;
        childIndex = parentNode.getChildCount();
        parentNode.add(childNode);
        
        if (next[child] != -1) {
          child = next[child];
        } else {
          child = -1;
        }
      }
    }
    
    for (int i = 0; i < numColliders; i++)
    {
      if (parent[i] == -1)
      {
        childNode = (MapObjectNode)nodes.get(i);
        parentNode = rootCollider.getNode();
        childIndex = rootCollider.getNode().getChildCount();
        rootCollider.getNode().add(childNode);
      }
    }
    
    map.colliderTree.recalculateIndicies();
    Logger.log("Read " + nodes.size() + " colliders.");
  }
  
  private void readZones(int offset)
  {
    bb.position(offset);
    
    int numZones = bb.getInt() >> 16;
    int zoneTableOffset = bb.getInt();
    
    int numVertices = bb.getInt() >> 16;
    int vertexTableOffset = bb.getInt();
    
    bb.getInt();
    int camTableOffset = bb.getInt();
    
    bb.position(vertexTableOffset);
    vertexList = new ArrayList();
    for (int i = 0; i < numVertices; i++)
    {
      Vertex v = new Vertex(bb.getShort(), bb.getShort(), bb.getShort());
      vertexList.add(v);
    }
    
    Zone rootZone = Zone.makeDummyRoot();
    map.zoneTree = new editor.map.tree.ZoneTreeModel(rootZone.getNode());
    
    List<MapObjectNode<Zone>> nodes = new ArrayList(numZones);
    int[] children = new int[numZones];
    int[] next = new int[numZones];
    int[] parent = new int[numZones];
    

    for (int i = 0; i < numZones; i++)
    {
      bb.position(zoneTableOffset + i * 12);
      int fileOffset = bb.position();
      
      int camEntryOffset = bb.getShort();
      next[i] = bb.getShort();
      children[i] = bb.getShort();
      int numTriangles = bb.getShort();
      
      Zone z = null;
      

      if (numTriangles == 0)
      {
        z = new Zone(MapObject.HitType.GROUP);

      }
      else
      {
        z = new Zone(MapObject.HitType.HIT);
        
        bb.position(bb.getInt());
        for (int j = 0; j < numTriangles; j++)
        {
          Triangle t = makeTriangle(vertexList, bb.getInt());
          mesh.batch.triangles.add(t);
        }
      }
      
      dumped = true;
      fileOffset = fileOffset;
      name = String.format("%04X", new Object[] { Integer.valueOf(nodes.size()) });
      
      z.updateMeshHierarchy();
      
      if (camEntryOffset != -1)
      {
        hasCameraData = true;
        bb.position(camTableOffset + 4 * camEntryOffset);
        int[] cameraData = new int[11];
        for (int j = 0; j < cameraData.length; j++)
          cameraData[j] = bb.getInt();
        cam = new editor.map.hit.CameraController(z, cameraData);
      }
      
      nodes.add(z.getNode());
      parent[i] = -1;
      name = ((String)map.zoneNames.poll());
    }
    
    MapObjectNode<Zone> parentNode = null;
    MapObjectNode<Zone> childNode = null;
    

    for (int i = 0; i < numZones; i++)
    {
      int child = children[i];
      
      while (child != -1)
      {
        parentNode = (MapObjectNode)nodes.get(i);
        childNode = (MapObjectNode)nodes.get(child);
        parent[child] = i;
        
        parentNode = parentNode;
        childIndex = parentNode.getChildCount();
        parentNode.add(childNode);
        
        if (next[child] != -1) {
          child = next[child];
        } else {
          child = -1;
        }
      }
    }
    
    for (int i = 0; i < numZones; i++)
    {
      if (parent[i] == -1)
      {
        childNode = (MapObjectNode)nodes.get(i);
        parentNode = rootZone.getNode();
        childIndex = rootZone.getNode().getChildCount();
        rootZone.getNode().add(childNode);
      }
    }
    
    map.zoneTree.recalculateIndicies();
    Logger.log("Read " + nodes.size() + " zones.");
  }
  
  private static Triangle makeTriangle(List<Vertex> vertexList, int word)
  {
    assert ((word & 0x80000000) == 0);
    
    Vertex v1 = (Vertex)vertexList.get(word & 0x3FF);
    Vertex v2 = (Vertex)vertexList.get(word >>> 10 & 0x3FF);
    Vertex v3 = (Vertex)vertexList.get(word >>> 20 & 0x3FF);
    
    Triangle t = new Triangle(v1, v2, v3);
    doubleSided = ((word & 0x40000000) == 0);
    return t;
  }
}
