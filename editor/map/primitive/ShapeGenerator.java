package editor.map.primitive;

import editor.map.hit.Collider;
import editor.map.hit.Zone;
import editor.map.shape.Model;
import editor.map.shape.TriangleBatch;
import org.lwjgl.util.vector.Vector3f;

public abstract class ShapeGenerator
{
  private final Primitive type;
  protected static final int UV_SCALE = 1024;
  
  public static enum Primitive
  {
    CUBE("Cube"), 
    CYLINDER("Cylinder"), 
    CONE("Cone"), 
    RING("Ring"), 
    SPHERE("Sphere"), 
    HEMISPHERE("Hemisphere"), 
    TORUS("Torus"), 
    PLANAR_GRID("Planar Grid"), 
    RADIAL_GRID("Radial Grid"), 
    STAIR("Staircase"), 
    SPIRAL_STAIR("Spiral Staircase"), 
    SPIRAL_RAMP("Spiral Ramp");
    
    public final String name;
    
    private Primitive(String name)
    {
      this.name = name;
    }
    
    public String toString()
    {
      return name;
    }
  }
  


  protected ShapeGenerator(Primitive type)
  {
    this.type = type;
  }
  


  public Model generateModel(Vector3f pos)
  {
    if (pos == null) {
      return generateModel(0, 0, 0);
    }
    return generateModel((int)x, (int)y, (int)z);
  }
  
  public Model generateModel(int centerX, int centerY, int centerZ)
  {
    Model mdl = Model.createBasicModel();
    
    TriangleBatch batch = generate(centerX, centerY, centerZ);
    name = type.name;
    getMeshdisplayListModel.addElement(batch);
    
    mdl.updateMeshHierarchy();
    dirtyAABB = true;
    mdl.updateTransformHierarchy();
    
    return mdl;
  }
  
  public Collider generateCollider(Vector3f pos)
  {
    if (pos == null) {
      return generateCollider(0, 0, 0);
    }
    return generateCollider((int)x, (int)y, (int)z);
  }
  
  public Collider generateCollider(int centerX, int centerY, int centerZ)
  {
    Collider c = new Collider(editor.map.MapObject.HitType.HIT);
    
    TriangleBatch batch = generate(centerX, centerY, centerZ);
    name = type.name;
    
    mesh.batch = batch;
    
    c.updateMeshHierarchy();
    dirtyAABB = true;
    
    return c;
  }
  
  public Zone generateZone(Vector3f pos)
  {
    if (pos == null) {
      return generateZone(0, 0, 0);
    }
    return generateZone((int)x, (int)y, (int)z);
  }
  
  public Zone generateZone(int centerX, int centerY, int centerZ)
  {
    Zone z = new Zone(editor.map.MapObject.HitType.HIT);
    
    TriangleBatch batch = generate(centerX, centerY, centerZ);
    name = type.name;
    
    mesh.batch = batch;
    z.updateMeshHierarchy();
    dirtyAABB = true;
    
    return z;
  }
  
  public TriangleBatch generateTriangles(Vector3f pos)
  {
    if (pos == null) {
      return generateTriangles(0, 0, 0);
    }
    return generateTriangles((int)x, (int)y, (int)z);
  }
  
  public TriangleBatch generateTriangles(int centerX, int centerY, int centerZ)
  {
    return generate(centerX, centerY, centerZ);
  }
  
  public abstract void addFields(editor.ui.dialogs.PrimitiveOptionsPanel paramPrimitiveOptionsPanel);
  
  public abstract void setVisible(boolean paramBoolean);
  
  public abstract TriangleBatch generate(int paramInt1, int paramInt2, int paramInt3);
}
