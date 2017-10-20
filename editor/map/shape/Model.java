package editor.map.shape;

import data.texture.EditorTexture;
import editor.commands.AbstractCommand;
import editor.map.BoundingBox;
import editor.map.MapObject;
import editor.map.MapObject.MapObjectType;
import editor.map.MapObject.ShapeType;
import editor.map.MutablePoint;
import editor.map.ReversibleTransform;
import editor.map.hit.CameraController;
import editor.map.mesh.TexturedMesh;
import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.tree.MapObjectNode;
import editor.render.ShaderManager;
import editor.selection.PickRay;
import editor.selection.PickRay.PickHit;
import editor.ui.info.LightingPanel;
import editor.ui.info.ModelInfoPanel;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import org.lwjgl.opengl.GL20;
import util.Logger;
import util.Priority;


public class Model
  extends MapObject
  implements Externalizable
{
  private static final long serialVersionUID = 1L;
  private static final int latestVersion = 2;
  private int instanceVersion = 2;
  
  private MapObjectNode<Model> node;
  
  private MapObject.ShapeType type;
  
  public boolean hasMesh;
  private TexturedMesh mesh = null;
  
  public transient boolean recievesTransform = false;
  






  public transient TransformMatrix cumulativeTransformMatrix;
  






  public boolean hasTransformMatrix = false;
  
  public TransformMatrix localTransformMatrix;
  
  public transient boolean decomposeMatrix = false;
  
  public transient int decomposeScale;
  
  public transient int decRx;
  
  public transient int Ry;
  
  public transient int Rz;
  public LightSet lightSet;
  public transient int numLights;
  public transient int lightsIndex;
  public boolean has60;
  public int prop60a;
  public int prop60b;
  public boolean hasRenderMode;
  public int renderMode = 0;
  
  public boolean hasAuxProperties;
  
  public int auxOffsetT;
  
  public int auxOffsetS;
  public int auxShiftT;
  public int auxShiftS;
  public int scrollUnit;
  public ReplaceType replaceWith;
  public boolean has62;
  public boolean hasCameraData;
  
  public static enum ReplaceType
  {
    None("None"), 
    RedFire("Red Flame"), 
    BlueFire1("Blue Flame"), 
    BlueFire2("Small Blue Flame"), 
    PinkFire("Pink Flame");
    
    public final String name;
    
    private ReplaceType(String name)
    {
      this.name = name;
    }
    

    public String toString()
    {
      return name;
    }
    
    public static ReplaceType getType(int id)
    {
      switch (id) {
      case 2: 
        return RedFire;
      case 1:  return BlueFire1;
      case 3:  return BlueFire2;
      case 4:  return PinkFire; }
      return None;
    }
    

    public static int getID(ReplaceType type)
    {
      switch (Model.1.$SwitchMap$editor$map$shape$Model$ReplaceType[type.ordinal()]) {
      case 1: 
        return 2;
      case 2:  return 1;
      case 3:  return 3;
      case 4:  return 4; }
      return 0;
    }
  }
  




  public transient CameraController cam = null;
  
  public transient int c_DisplayListOffset;
  
  public transient BoundingBox localAABB;
  public static final int[][] basicModelProperties = { { 92, 0, 1 }, { 95, 0, 0 } };
  


  public static final int[][] basicGroupProperties = { { 96, 0, 0 }, { 97, 0, 1 } };
  





  public Model()
  {
    setDefaultMesh();
    updateMeshHierarchy();
    
    localTransformMatrix = new TransformMatrix();
    localTransformMatrix.setIdentity();
  }
  

  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    super.readExternal(in);
    instanceVersion = in.readInt();
    
    type = ((MapObject.ShapeType)in.readObject());
    node = ((MapObjectNode)in.readObject());
    
    hasMesh = in.readBoolean();
    if (hasMesh)
    {
      mesh = ((TexturedMesh)in.readObject());
      mesh.parentObject = this;
      AABB = new BoundingBox();
    } else {
      AABB = ((BoundingBox)in.readObject());
    }
    
    hasTransformMatrix = in.readBoolean();
    
    if (hasTransformMatrix)
    {
      for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++)
          localTransformMatrix.set(in.readDouble(), i, j);
      }
    }
    if (instanceVersion < 2)
    {
      boolean hasSpecialMatrix = in.readBoolean();
      
      if (hasSpecialMatrix)
      {
        for (int i = 0; i < 4; i++) {
          for (int j = 0; j < 4; j++)
            in.readShort();
        }
      }
      in.readInt();
    }
    else {
      lightsIndex = in.readInt();
    }
    int numProperties = in.readInt();
    int[][] properties = new int[numProperties][3];
    for (int i = 0; i < properties.length; i++)
    {
      properties[i][0] = in.readInt();
      properties[i][1] = in.readInt();
      properties[i][2] = in.readInt();
    }
    
    setProperties(properties);
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    super.writeExternal(out);
    out.writeInt(2);
    
    out.writeObject(type);
    out.writeObject(node);
    
    out.writeBoolean(hasMesh);
    if (hasMesh) {
      out.writeObject(mesh);
    } else {
      out.writeObject(AABB);
    }
    out.writeBoolean(hasTransformMatrix);
    if (hasTransformMatrix)
    {
      for (int i = 0; i < 4; i++) {
        for (int j = 0; j < 4; j++)
          out.writeDouble(localTransformMatrix.get(i, j));
      }
    }
    out.writeInt(lightSet == null ? 0 : lightSet.listIndex);
    
    int[][] properties = getProperties();
    out.writeInt(properties.length);
    for (int i = 0; i < properties.length; i++)
    {
      out.writeInt(properties[i][0]);
      out.writeInt(properties[i][1]);
      out.writeInt(properties[i][2]);
    }
  }
  
  public void setProperties(int[][] properties)
  {
    if (type == MapObject.ShapeType.MODEL) {
      setModelProperties(properties);
    } else {
      setGroupProperties(properties);
    }
  }
  
  private void setModelProperties(int[][] properties) {
    if (type != MapObject.ShapeType.MODEL) {
      throw new IllegalStateException("Tried to invoke setModelProperties on " + type);
    }
    int lastPropertyType = 0;
    
    int camDataPos = 0;
    int[] camData = new int[11];
    
    for (int i = 0; i < properties.length; i++)
    {
      int[] property = properties[i];
      assert (property[0] >= lastPropertyType);
      
      switch (property[0])
      {
      case 92: 
        hasRenderMode = true;
        assert (property[1] == 0);
        renderMode = property[2];
        break;
      

      case 93: 
        if ((camDataPos == 0) || (camDataPos == 10)) {
          if ((!$assertionsDisabled) && (property[1] != 0)) throw new AssertionError();
        } else {
          assert (property[1] == 1);
        }
        hasCameraData = true;
        camData[(camDataPos++)] = property[2];
        break;
      
      case 95: 
        hasAuxProperties = true;
        int a = property[1];
        int b = property[2];
        assert ((a & 0xFF000000) == 0);
        assert ((b & 0xFFF00F00) == 0);
        
        auxOffsetT = (a >> 12 & 0xFFF);
        auxOffsetS = (a & 0xFFF);
        
        auxShiftT = (b >> 16 & 0xF);
        auxShiftS = (b >> 12 & 0xF);
        
        replaceWith = ReplaceType.getType(b >> 4 & 0xF);
        scrollUnit = (b & 0xF);
        break;
      
      case 98: 
        has62 = true;
      }
      
      
      lastPropertyType = property[0];
    }
    
    if (camDataPos > 0) {
      cam = new CameraController(this, camData);
    } else {
      cam = new CameraController(this);
    }
  }
  
  private void setGroupProperties(int[][] properties)
  {
    if (type == MapObject.ShapeType.MODEL) {
      throw new IllegalStateException("Tried to invoke setGroupProperties on " + type);
    }
    if (properties.length != 0)
    {
      if (properties.length != 2) {
        throw new IllegalStateException("Too many properties for group: " + properties.length);
      }
      has60 = true;
      prop60a = properties[0][2];
      prop60b = properties[1][2];
    }
  }
  

  public int[][] getProperties()
  {
    int[][] properties;
    if (type == MapObject.ShapeType.MODEL)
    {
      ArrayList<int[]> propertyList = new ArrayList();
      
      if (hasRenderMode) {
        propertyList.add(new int[] { 92, 0, renderMode });
      }
      if (hasCameraData)
      {
        int[] camData = cam.getData();
        propertyList.add(new int[] { 93, 0, camData[0] });
        for (int i = 1; i < 10; i++)
          propertyList.add(new int[] { 93, 1, camData[i] });
        propertyList.add(new int[] { 93, 0, camData[10] });
      }
      
      if (hasAuxProperties)
      {
        int a = 0;
        a |= (auxOffsetT & 0xFFF) << 12;
        a |= auxOffsetS & 0xFFF;
        
        int b = 0;
        b |= (auxShiftT & 0xF) << 16;
        b |= (auxShiftS & 0xF) << 12;
        b |= (ReplaceType.getID(replaceWith) & 0xF) << 4;
        b |= scrollUnit & 0xF;
        
        propertyList.add(new int[] { 95, a, b });
      }
      
      if (has62) {
        propertyList.add(new int[] { 98, 0, 32768 });
      }
      int[][] properties = new int[propertyList.size()][3];
      propertyList.toArray(properties);


    }
    else if (has60)
    {
      int[][] properties = new int[2][];
      properties[0] = { 96, 0, prop60a };
      properties[1] = { 96, 0, prop60b };
    }
    else
    {
      properties = new int[0][3];
    }
    
    return properties;
  }
  
  public Model(MapObject.ShapeType type)
  {
    instanceVersion = 2;
    this.type = type;
    
    node = new MapObjectNode(this);
    AABB = new BoundingBox();
    
    localTransformMatrix = new TransformMatrix();
    localTransformMatrix.setIdentity();
  }
  
  public static Model createDefaultRoot()
  {
    Model rootModel = new Model(MapObject.ShapeType.ROOT);
    name = "Root";
    cumulativeTransformMatrix = new TransformMatrix();
    cumulativeTransformMatrix.setIdentity();
    rootModel.updateTransformHierarchy();
    return rootModel;
  }
  
  public static Model createBasicModel()
  {
    Model mdl = new Model(MapObject.ShapeType.MODEL);
    mdl.setDefaultMesh();
    mesh.setTexture("");
    
    mdl.setProperties(basicModelProperties);
    return mdl;
  }
  

  public Model deepCopy()
  {
    Model m = new Model(type);
    AABB = AABB.deepCopy();
    dirtyAABB = dirtyAABB;
    
    if (hasMesh) {
      m.setMesh(mesh.deepCopy());
    } else {
      m.setMesh(null);
    }
    name = name;
    
    localTransformMatrix = localTransformMatrix.deepCopy();
    hasTransformMatrix = hasTransformMatrix;
    
    node.parentNode = node.parentNode;
    m.updateTransformHierarchy();
    
    lightSet = lightSet;
    
    int[][] properties = getProperties();
    m.setModelProperties(properties);
    
    return m;
  }
  
  public MapObject.ShapeType getType()
  {
    return type;
  }
  
  public static final MapObject.ShapeType getTypeFromID(int id)
  {
    switch (id) {
    case 2: 
      return MapObject.ShapeType.MODEL;
    case 5:  return MapObject.ShapeType.GROUP;
    case 7:  return MapObject.ShapeType.ROOT;
    case 10:  return MapObject.ShapeType.SPECIAL;
    }
    throw new RuntimeException("Invalid model type ID =" + id);
  }
  

  public static final int getIDFromType(MapObject.ShapeType type)
  {
    switch (1.$SwitchMap$editor$map$MapObject$ShapeType[type.ordinal()]) {
    case 1: 
      return 2;
    case 2:  return 5;
    case 3:  return 7;
    case 4:  return 10;
    }
    throw new RuntimeException("Invalid model type =" + type);
  }
  

  public void setShaderTextureParameters()
  {
    EditorTexture tex = mesh.texture;
    if (tex != null)
    {
      GL20.glUniform1i(ShaderManager.model_textured, 1);
      tex.setShaderParameters();
      
      if ((tex.hasAux()) && (hasAuxProperties))
      {
        GL20.glUniform2f(ShaderManager.model_auxScale, auxShiftS, auxShiftT);
        GL20.glUniform2f(ShaderManager.model_auxOffset, auxOffsetS, auxOffsetT);
      }
    }
    else {
      GL20.glUniform1i(ShaderManager.model_textured, 0);
    }
  }
  
  public MapObject.MapObjectType getObjectType()
  {
    return MapObject.MapObjectType.MODEL;
  }
  

  public boolean hasMesh()
  {
    return hasMesh;
  }
  

  public TexturedMesh getMesh()
  {
    return mesh;
  }
  
  public void setMesh(TexturedMesh newMesh)
  {
    mesh = newMesh;
    hasMesh = (newMesh != null);
    
    dirtyAABB = true;
    if (hasMesh) {
      updateMeshHierarchy();
    }
  }
  
  public void setDefaultMesh() {
    setMesh(TexturedMesh.createDefaultMesh());
  }
  

  public boolean hasCamera()
  {
    return hasCameraData;
  }
  

  public void updateMeshHierarchy()
  {
    mesh.parentObject = this;
    mesh.updateHierarchy();
  }
  

  public boolean transforms()
  {
    return hasMesh();
  }
  
  public boolean shouldTryPick(PickRay ray)
  {
    return (hasMesh()) && (PickRay.intersects(ray, AABB));
  }
  
  public PickRay.PickHit tryPick(PickRay ray)
  {
    PickRay.PickHit nearestHit = new PickRay.PickHit(ray, Float.MAX_VALUE);
    for (Triangle t : mesh)
    {
      PickRay.PickHit hit = PickRay.getIntersection(ray, t);
      if (dist < dist)
        nearestHit = hit;
    }
    obj = this;
    return nearestHit;
  }
  
  public boolean shouldTryTrianglePick(PickRay ray)
  {
    return (hasMesh()) && (PickRay.intersects(ray, AABB));
  }
  
  public String toString()
  {
    if (hasTransformMatrix) {
      return name + " *";
    }
    return name;
  }
  

  public void calculateLocalAABB()
  {
    if (!hasMesh()) {
      return;
    }
    for (Triangle t : mesh) {
      for (Vertex v : vert)
      {
        MutablePoint point = v.getLocalPosition();
        localAABB.encompass(point.getX(), point.getY(), point.getZ());
      }
    }
  }
  
  public void updateTransformHierarchy() {
    if (node.parentNode != null)
    {
      Model parentModel = (Model)node.parentNode.getUserObject();
      recievesTransform = ((recievesTransform) || (hasTransformMatrix));
      
      if (hasTransformMatrix) {
        cumulativeTransformMatrix = TransformMatrix.multiply(localTransformMatrix, cumulativeTransformMatrix);
      } else
        cumulativeTransformMatrix = cumulativeTransformMatrix.deepCopy();
    } else {
      cumulativeTransformMatrix = localTransformMatrix;
    }
    
    if (hasMesh())
    {
      boolean hasTransformation = (recievesTransform) || (hasTransformMatrix);
      
      if (hasTransformation) {
        for (Triangle t : mesh)
          for (Vertex v : vert)
            v.forceTransform(cumulativeTransformMatrix);
      }
      for (Triangle t : mesh) {
        for (Vertex v : vert)
          useLocal = (!hasTransformation);
      }
      dirtyAABB = true;
    }
    
    for (int i = 0; i < node.getChildCount(); i++)
    {
      Object childNode = node.getChildAt(i);
      ((Model)((MapObjectNode)childNode).getUserObject()).updateTransformHierarchy();
    }
  }
  

  public ReversibleTransform createTransformer(TransformMatrix m)
  {
    if (hasMesh())
    {
      if (!recievesTransform)
      {
        return super.createTransformer(m);
      }
      
      Model parentModel = (Model)node.parentNode.getUserObject();
      if (!hasTransformMatrix)
      {
        Logger.log("Could not apply matrix transformation to group!", Priority.WARNING);
        return null;
      }
      
      MatrixTransformer backup = new MatrixTransformer();
      mdl = this;
      parent = parentModel;
      oldMatrix = localTransformMatrix;
      newMatrix = TransformMatrix.multiply(m, localTransformMatrix);
      
      return backup;
    }
    

    return null;
  }
  
  public class MatrixTransformer extends ReversibleTransform {
    private Model mdl;
    private Model parent;
    private TransformMatrix oldMatrix;
    private TransformMatrix newMatrix;
    
    public MatrixTransformer() {}
    
    public void transform() {
      if (mdl.recievesTransform)
      {
        parent.localTransformMatrix = newMatrix;
        parent.updateTransformHierarchy();
      }
    }
    

    public void revert()
    {
      if (mdl.recievesTransform)
      {
        parent.localTransformMatrix = oldMatrix;
        parent.updateTransformHierarchy();
      }
    }
  }
  

  public boolean allowsPopup()
  {
    return type != MapObject.ShapeType.MODEL;
  }
  

  public boolean allowsChildren()
  {
    return type != MapObject.ShapeType.MODEL;
  }
  

  public MapObjectNode<Model> getNode()
  {
    return node;
  }
  

  public static final class SetModelName
    extends AbstractCommand
  {
    private final Model mdl;
    
    private final String oldName;
    
    private final String newName;
    
    public SetModelName(Model mdl, String s)
    {
      super();
      this.mdl = mdl;
      oldName = name;
      newName = s;
    }
    

    public boolean shouldExec()
    {
      return (!newName.isEmpty()) && (!oldName.equals(newName));
    }
    

    public void exec()
    {
      super.exec();
      mdl.name = newName;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.name = oldName;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
  }
  
  public static final class SetModelType extends AbstractCommand
  {
    private final Model mdl;
    private final MapObject.ShapeType oldValue;
    private final MapObject.ShapeType newValue;
    
    public SetModelType(Model mdl, MapObject.ShapeType type)
    {
      super();
      this.mdl = mdl;
      oldValue = type;
      newValue = type;
    }
    

    public boolean shouldExec()
    {
      return oldValue != newValue;
    }
    

    public void exec()
    {
      super.exec();
      mdl.type = newValue;
      ModelInfoPanel.instance().setModel(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.type = oldValue;
      ModelInfoPanel.instance().setModel(mdl);
    }
  }
  
  public static final class ToggleProperty60 extends AbstractCommand
  {
    private final Model mdl;
    
    public ToggleProperty60(Model mdl)
    {
      super();
      this.mdl = mdl;
    }
    

    public void exec()
    {
      super.exec();
      mdl.has60 = (!mdl.has60);
      ModelInfoPanel.instance().setModel(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.has60 = (!mdl.has60);
      ModelInfoPanel.instance().setModel(mdl);
    }
  }
  
  public static final class SetProperty60a extends AbstractCommand
  {
    private final Model mdl;
    private final int oldValue;
    private final int newValue;
    
    public SetProperty60a(Model mdl, int val)
    {
      super();
      this.mdl = mdl;
      oldValue = prop60a;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return oldValue != newValue;
    }
    

    public void exec()
    {
      super.exec();
      mdl.prop60a = newValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.prop60a = oldValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
  }
  
  public static final class SetProperty60b extends AbstractCommand
  {
    private final Model mdl;
    private final int oldValue;
    private final int newValue;
    
    public SetProperty60b(Model mdl, int val)
    {
      super();
      this.mdl = mdl;
      oldValue = prop60b;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return oldValue != newValue;
    }
    

    public void exec()
    {
      super.exec();
      mdl.prop60b = newValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.prop60b = oldValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
  }
  
  public static final class ToggleHasMesh extends AbstractCommand
  {
    private final Model mdl;
    
    public ToggleHasMesh(Model mdl)
    {
      super();
      this.mdl = mdl;
    }
    

    public void exec()
    {
      super.exec();
      mdl.hasMesh = (!mdl.hasMesh);
      mdl.dirtyAABB = true;
      ModelInfoPanel.instance().setModel(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.hasMesh = (!mdl.hasMesh);
      mdl.dirtyAABB = true;
      ModelInfoPanel.instance().setModel(mdl);
    }
  }
  
  public static final class ToggleRenderMode extends AbstractCommand
  {
    private final Model mdl;
    
    public ToggleRenderMode(Model mdl)
    {
      super();
      this.mdl = mdl;
    }
    

    public void exec()
    {
      super.exec();
      mdl.hasRenderMode = (!mdl.hasRenderMode);
      ModelInfoPanel.instance().setModel(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.hasRenderMode = (!mdl.hasRenderMode);
      ModelInfoPanel.instance().setModel(mdl);
    }
  }
  
  public static final class SetRenderMode extends AbstractCommand
  {
    private final Model mdl;
    private final int oldValue;
    private final int newValue;
    
    public SetRenderMode(Model mdl, int val)
    {
      super();
      this.mdl = mdl;
      oldValue = renderMode;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return oldValue != newValue;
    }
    

    public void exec()
    {
      super.exec();
      mdl.renderMode = newValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.renderMode = oldValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
  }
  
  public static final class ToggleAuxProperties extends AbstractCommand
  {
    private final Model mdl;
    
    public ToggleAuxProperties(Model mdl)
    {
      super();
      this.mdl = mdl;
    }
    

    public void exec()
    {
      super.exec();
      mdl.hasAuxProperties = (!mdl.hasAuxProperties);
      ModelInfoPanel.instance().setModel(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.hasAuxProperties = (!mdl.hasAuxProperties);
      ModelInfoPanel.instance().setModel(mdl);
    }
  }
  
  public static final class SetAuxOffsetT extends AbstractCommand
  {
    private final Model mdl;
    private final int oldValue;
    private final int newValue;
    
    public SetAuxOffsetT(Model mdl, int val)
    {
      super();
      this.mdl = mdl;
      oldValue = auxOffsetT;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return (oldValue != newValue) && (newValue < 4096) && (newValue > 61440);
    }
    

    public void exec()
    {
      super.exec();
      mdl.auxOffsetT = newValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.auxOffsetT = oldValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
  }
  
  public static final class SetAuxOffsetS extends AbstractCommand
  {
    private final Model mdl;
    private final int oldValue;
    private final int newValue;
    
    public SetAuxOffsetS(Model mdl, int val)
    {
      super();
      this.mdl = mdl;
      oldValue = auxOffsetS;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return (oldValue != newValue) && (newValue < 4096) && (newValue > 61440);
    }
    

    public void exec()
    {
      super.exec();
      mdl.auxOffsetS = newValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.auxOffsetS = oldValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
  }
  
  public static final class SetAuxShiftT extends AbstractCommand
  {
    private final Model mdl;
    private final int oldValue;
    private final int newValue;
    
    public SetAuxShiftT(Model mdl, int val)
    {
      super();
      this.mdl = mdl;
      oldValue = auxShiftT;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return (oldValue != newValue) && (newValue < 16) && (newValue > -16);
    }
    

    public void exec()
    {
      super.exec();
      mdl.auxShiftT = newValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.auxShiftT = oldValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
  }
  
  public static final class SetAuxShiftS extends AbstractCommand
  {
    private final Model mdl;
    private final int oldValue;
    private final int newValue;
    
    public SetAuxShiftS(Model mdl, int val)
    {
      super();
      this.mdl = mdl;
      oldValue = auxShiftS;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return (oldValue != newValue) && (newValue < 16) && (newValue > -16);
    }
    

    public void exec()
    {
      super.exec();
      mdl.auxShiftS = newValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.auxShiftS = oldValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
  }
  
  public static final class SetScrollUnit extends AbstractCommand
  {
    private final Model mdl;
    private final int oldValue;
    private final int newValue;
    
    public SetScrollUnit(Model mdl, int val)
    {
      super();
      this.mdl = mdl;
      oldValue = scrollUnit;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return (oldValue != newValue) && (newValue < 16) && (newValue >= 0);
    }
    

    public void exec()
    {
      super.exec();
      mdl.scrollUnit = newValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.scrollUnit = oldValue;
      ModelInfoPanel.instance().updateGeneralFields(mdl);
    }
  }
  
  public static final class SetReplaceWith extends AbstractCommand
  {
    private final Model mdl;
    private final Model.ReplaceType oldValue;
    private final Model.ReplaceType newValue;
    
    public SetReplaceWith(Model mdl, Model.ReplaceType type)
    {
      super();
      this.mdl = mdl;
      oldValue = replaceWith;
      newValue = type;
    }
    

    public boolean shouldExec()
    {
      return oldValue != newValue;
    }
    

    public void exec()
    {
      super.exec();
      mdl.replaceWith = newValue;
      ModelInfoPanel.instance().setModel(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.replaceWith = oldValue;
      ModelInfoPanel.instance().setModel(mdl);
    }
  }
  
  public static final class ToggleProperty62 extends AbstractCommand
  {
    private final Model mdl;
    
    public ToggleProperty62(Model mdl)
    {
      super();
      this.mdl = mdl;
    }
    

    public void exec()
    {
      super.exec();
      mdl.has62 = (!mdl.has62);
      ModelInfoPanel.instance().setModel(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.has62 = (!mdl.has62);
      ModelInfoPanel.instance().setModel(mdl);
    }
  }
  
  public static final class ToggleCamera extends AbstractCommand
  {
    private final Model mdl;
    
    public ToggleCamera(Model mdl)
    {
      super();
      this.mdl = mdl;
    }
    

    public void exec()
    {
      super.exec();
      mdl.hasCameraData = (!mdl.hasCameraData);
      ModelInfoPanel.instance().setModel(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.hasCameraData = (!mdl.hasCameraData);
      ModelInfoPanel.instance().setModel(mdl);
    }
  }
  
  public static final class ToggleMatrix extends AbstractCommand
  {
    private final Model mdl;
    
    public ToggleMatrix(Model mdl)
    {
      super();
      this.mdl = mdl;
    }
    

    public void exec()
    {
      super.exec();
      mdl.hasTransformMatrix = (!mdl.hasTransformMatrix);
      mdl.updateTransformHierarchy();
      ModelInfoPanel.instance().setModel(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.hasTransformMatrix = (!mdl.hasTransformMatrix);
      mdl.updateTransformHierarchy();
      ModelInfoPanel.instance().setModel(mdl);
    }
  }
  
  public static final class SetMatrixElement extends AbstractCommand
  {
    private final Model mdl;
    private final double oldValue;
    private final double newValue;
    private final int row;
    private final int col;
    
    public SetMatrixElement(Model mdl, int row, int col, double val) {
      super();
      this.mdl = mdl;
      this.row = row;
      this.col = col;
      oldValue = localTransformMatrix.get(row, col);
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return oldValue != newValue;
    }
    

    public void exec()
    {
      super.exec();
      mdl.localTransformMatrix.set(newValue, row, col);
      mdl.updateTransformHierarchy();
      ModelInfoPanel.instance().setModel(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.localTransformMatrix.set(oldValue, row, col);
      mdl.updateTransformHierarchy();
      ModelInfoPanel.instance().setModel(mdl);
    }
  }
  
  public static final class SetLightSet extends AbstractCommand
  {
    private final Model mdl;
    private final LightSet oldValue;
    private final LightSet newValue;
    
    public SetLightSet(Model mdl, LightSet val)
    {
      super();
      this.mdl = mdl;
      oldValue = lightSet;
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return oldValue != newValue;
    }
    

    public void exec()
    {
      super.exec();
      mdl.lightSet = newValue;
      LightingPanel.setModel(mdl);
    }
    

    public void undo()
    {
      super.undo();
      mdl.lightSet = oldValue;
      LightingPanel.setModel(mdl);
    }
  }
}
