package editor.map;

import editor.Editor;
import editor.commands.AbstractCommand;
import editor.map.hit.Collider;
import editor.map.hit.Zone;
import editor.map.marker.Marker;
import editor.map.mesh.AbstractMesh;
import editor.map.mesh.TexturedMesh;
import editor.map.mesh.Triangle;
import editor.map.mesh.Vertex;
import editor.map.shape.LightSet;
import editor.map.shape.Model;
import editor.map.shape.UV;
import editor.map.tree.ColliderTreeModel;
import editor.map.tree.MapObjectNode;
import editor.map.tree.MapObjectTreeModel;
import editor.map.tree.MarkerTreeModel;
import editor.map.tree.ModelTreeModel;
import editor.map.tree.ZoneTreeModel;
import editor.render.TextureManager;
import editor.selection.PickRay;
import editor.selection.PickRay.PickHit;
import editor.ui.SwingGUI;
import editor.ui.info.LightingPanel;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Externalizable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import javax.swing.JCheckBoxMenuItem;
import main.Directories;
import main.StarRodDev;
import org.lwjgl.opengl.GL11;
import util.Logger;
import util.Priority;

public class Map
  implements Externalizable, MapIO.BinarySerializable
{
  private static final long serialVersionUID = 1L;
  private static final int latestVersion = 2;
  private int instanceVersion = 2;
  
  public MapObjectTreeModel<Model> modelTree;
  
  public MapObjectTreeModel<Collider> colliderTree;
  
  public MapObjectTreeModel<Zone> zoneTree;
  public MapObjectTreeModel<Marker> markerTree;
  public ArrayList<LightSet> lightSets;
  public String name;
  public String texName;
  public boolean hasBackground;
  public String bgName = "nok_bg";
  
  public static final String NO_BG = "none";
  
  public static final String DEFAULT_BG = "nok_bg";
  
  public transient Queue<String> modelNames;
  
  public transient Queue<String> colliderNames;
  
  public transient Queue<String> zoneNames;
  public transient File source;
  public transient boolean modified = false;
  public transient BufferedImage bgImage = null;
  public transient int glBackgroundTexID = -1;
  
  public static boolean saveBinary(Map map, File f)
  {
    try {
      MapIO io = new MapIO(f, MapIO.ReadMode.WRITE);
      map.writeToBinary(io);
      io.close();
      return true;
    } catch (IOException e) {
      Logger.log("IOException during map save. " + e.getMessage());
      StarRodDev.displayStackTrace(e); }
    return false;
  }
  
  public static Map loadBinary(File f)
  {
    try
    {
      MapIO io = new MapIO(f, MapIO.ReadMode.READ);
      Map map = new Map();
      map.readFromBinary(io);
      io.close();
      return map;
    } catch (IOException e) {
      Logger.log("IOException during map load. " + e.getMessage());
      StarRodDev.displayStackTrace(e); }
    return null;
  }
  

  public void writeToBinary(MapIO io)
    throws IOException
  {
    io.writeInt(instanceVersion);
    io.writeString(name);
    
    io.writeBoolean(hasBackground);
    io.writeString(bgName);
  }
  
  public void readFromBinary(MapIO io)
    throws IOException
  {
    instanceVersion = io.readInt();
    name = io.readString();
    
    hasBackground = io.readBoolean();
    bgName = io.readString();
  }
  



  public Map() {}
  


  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    instanceVersion = in.readInt();
    
    name = in.readUTF();
    
    hasBackground = in.readBoolean();
    bgName = in.readUTF();
    
    if (instanceVersion > 0) {
      texName = in.readUTF();
    } else
      texName = getExpectedTexFilename();
    int numLights;
    if (instanceVersion > 1)
    {
      numLights = in.readInt();
      lightSets = new ArrayList(numLights);
      
      for (int i = 0; i < numLights; i++) {
        lightSets.add((LightSet)in.readObject());
      }
    }
    else {
      lightSets = new ArrayList();
      lightSets.add(LightSet.createEmptySet());
    }
    
    if (lightSets.isEmpty()) {
      lightSets.add(LightSet.createEmptySet());
    }
    modelTree = ((MapObjectTreeModel)in.readObject());
    colliderTree = ((MapObjectTreeModel)in.readObject());
    zoneTree = ((MapObjectTreeModel)in.readObject());
    markerTree = ((MapObjectTreeModel)in.readObject());
    
    for (Model mdl : modelTree.getList()) {
      lightSet = ((LightSet)lightSets.get(lightsIndex));
    }
  }
  
  public void writeExternal(ObjectOutput out) throws IOException
  {
    out.writeInt(2);
    out.writeUTF(name);
    
    out.writeBoolean(hasBackground);
    out.writeUTF(bgName);
    out.writeUTF(texName);
    
    out.writeInt(lightSets.size());
    for (int i = 0; i < lightSets.size(); i++)
    {
      LightSet lights = (LightSet)lightSets.get(i);
      listIndex = i;
      out.writeObject(lights);
    }
    
    out.writeObject(modelTree);
    out.writeObject(colliderTree);
    out.writeObject(zoneTree);
    out.writeObject(markerTree);
  }
  
  public Map(String name)
  {
    instanceVersion = 2;
    this.name = name;
    
    modelTree = new ModelTreeModel(Model.createDefaultRoot().getNode());
    colliderTree = new ColliderTreeModel(Collider.makeDummyRoot().getNode());
    zoneTree = new ZoneTreeModel(Zone.makeDummyRoot().getNode());
    markerTree = new MarkerTreeModel(Marker.makeDummyRoot().getNode());
    
    lightSets = new ArrayList();
  }
  
  public static Map createNewMap(String name)
  {
    Map m = new Map(name);
    source = new File(Directories.MOD_MAP_SAVE + name + ".map");
    texName = "mac_tex";
    lightSets.add(LightSet.createEmptySet());
    return m;
  }
  
  public void add(MapObject obj)
  {
    switch (1.$SwitchMap$editor$map$MapObject$MapObjectType[obj.getObjectType().ordinal()])
    {
    case 1: 
      modelTree.add((Model)obj);
      TextureManager.increment((Model)obj);
      break;
    case 2:  colliderTree.add((Collider)obj); break;
    case 3:  zoneTree.add((Zone)obj); break;
    case 4:  markerTree.add((Marker)obj);
    }
  }
  
  public void remove(MapObject obj)
  {
    switch (1.$SwitchMap$editor$map$MapObject$MapObjectType[obj.getObjectType().ordinal()])
    {
    case 1: 
      modelTree.remove((Model)obj);
      TextureManager.decrement((Model)obj);
      break;
    case 2:  colliderTree.remove((Collider)obj); break;
    case 3:  zoneTree.remove((Zone)obj); break;
    case 4:  markerTree.remove((Marker)obj);
    }
  }
  
  public void create(MapObject obj)
  {
    switch (1.$SwitchMap$editor$map$MapObject$MapObjectType[obj.getObjectType().ordinal()])
    {
    case 1: 
      modelTree.create((Model)obj);
      TextureManager.increment((Model)obj);
      break;
    case 2:  colliderTree.create((Collider)obj); break;
    case 3:  zoneTree.create((Zone)obj); break;
    case 4:  markerTree.create((Marker)obj);
    }
  }
  
  public String toString()
  {
    return name;
  }
  
  public List<MapObject> getObjectsWithinRegion(BoundingBox selectionBox)
  {
    List<MapObject> objs = new LinkedList();
    MapObject o; for (Iterator localIterator = modelTree.iterator(); localIterator.hasNext(); 
        objs.add(o))
    {
      o = (MapObject)localIterator.next();
      if ((hidden) || (!selectionBox.contains(AABB.getCenter()))) {} }
    MapObject o; for (localIterator = colliderTree.iterator(); localIterator.hasNext(); 
        objs.add(o))
    {
      o = (MapObject)localIterator.next();
      if ((hidden) || (!selectionBox.contains(AABB.getCenter()))) {} }
    MapObject o; for (localIterator = zoneTree.iterator(); localIterator.hasNext(); 
        objs.add(o))
    {
      o = (MapObject)localIterator.next();
      if ((hidden) || (!selectionBox.contains(AABB.getCenter()))) {} }
    MapObject o; for (localIterator = markerTree.iterator(); localIterator.hasNext(); 
        objs.add(o))
    {
      o = (MapObject)localIterator.next();
      if ((hidden) || (!selectionBox.contains(AABB.getCenter()))) {}
    }
    return objs;
  }
  
  public PickRay.PickHit pickNearestObject(PickRay pickRay, MapObject.MapObjectType favoredType)
  {
    LinkedList<MapObject> candidates = new LinkedList();
    Iterator localIterator;
    switch (1.$SwitchMap$editor$map$MapObject$MapObjectType[favoredType.ordinal()]) {
    case 1: 
      MapObject o;
      for (localIterator = modelTree.iterator(); localIterator.hasNext(); 
          candidates.add(o))
      {
        o = (MapObject)localIterator.next();
        if ((hidden) || (!o.shouldTryPick(pickRay))) {} }
      break;
    case 2:  MapObject o;
      for (localIterator = colliderTree.iterator(); localIterator.hasNext(); 
          candidates.add(o))
      {
        o = (MapObject)localIterator.next();
        if ((hidden) || (!o.shouldTryPick(pickRay))) {} }
      break;
    case 3:  MapObject o;
      for (localIterator = zoneTree.iterator(); localIterator.hasNext(); 
          candidates.add(o))
      {
        o = (MapObject)localIterator.next();
        if ((hidden) || (!o.shouldTryPick(pickRay))) {} }
      break;
    case 4:  MapObject o;
      for (localIterator = markerTree.iterator(); localIterator.hasNext(); 
          candidates.add(o))
      {
        o = (MapObject)localIterator.next();
        if ((hidden) || (!o.shouldTryPick(pickRay))) {}
      }
    }
    
    if (favoredType != MapObject.MapObjectType.MODEL) {
      MapObject o;
      for (localIterator = modelTree.iterator(); localIterator.hasNext(); 
          candidates.add(o))
      {
        o = (MapObject)localIterator.next();
        if ((hidden) || (!o.shouldTryPick(pickRay))) {}
      }
    }
    if (favoredType != MapObject.MapObjectType.COLLIDER) {
      MapObject o;
      for (localIterator = colliderTree.iterator(); localIterator.hasNext(); 
          candidates.add(o))
      {
        o = (MapObject)localIterator.next();
        if ((hidden) || (!o.shouldTryPick(pickRay))) {}
      }
    }
    if (favoredType != MapObject.MapObjectType.ZONE) {
      MapObject o;
      for (localIterator = zoneTree.iterator(); localIterator.hasNext(); 
          candidates.add(o))
      {
        o = (MapObject)localIterator.next();
        if ((hidden) || (!o.shouldTryPick(pickRay))) {}
      }
    }
    if (favoredType != MapObject.MapObjectType.MARKER) {
      MapObject o;
      for (localIterator = markerTree.iterator(); localIterator.hasNext(); 
          candidates.add(o))
      {
        o = (MapObject)localIterator.next();
        if ((hidden) || (!o.shouldTryPick(pickRay))) {}
      }
    }
    return pickObjectFromSet(pickRay, candidates);
  }
  
  public <T extends MapObject> PickRay.PickHit pickObjectFromSet(PickRay pickRay, Iterable<T> candidates)
  {
    PickRay.PickHit closestHit = new PickRay.PickHit(pickRay, Float.MAX_VALUE);
    for (T obj : candidates)
    {
      if (!hidden)
      {

        PickRay.PickHit hit = obj.tryPick(pickRay);
        if (dist < dist)
        {
          closestHit = hit;
          obj = obj;
        }
      }
    }
    return closestHit;
  }
  
  public PickRay.PickHit pickNearestTriangle(PickRay pickRay)
  {
    ArrayList<MapObject> candidates = new ArrayList();
    Model mdl; for (Iterator localIterator = modelTree.iterator(); localIterator.hasNext(); 
        candidates.add(mdl))
    {
      mdl = (Model)localIterator.next();
      if ((hidden) || (!mdl.shouldTryPick(pickRay))) {} }
    Collider c; for (localIterator = colliderTree.iterator(); localIterator.hasNext(); 
        candidates.add(c))
    {
      c = (Collider)localIterator.next();
      if ((hidden) || (!c.shouldTryPick(pickRay))) {} }
    Zone z; for (localIterator = zoneTree.iterator(); localIterator.hasNext(); 
        candidates.add(z))
    {
      z = (Zone)localIterator.next();
      if ((hidden) || (!z.shouldTryPick(pickRay))) {}
    }
    return pickTriangleFromObjectList(pickRay, candidates);
  }
  
  public List<Triangle> getTrianglesWithinRegion(BoundingBox box)
  {
    List<Triangle> triangleList = new ArrayList();
    
    ArrayList<AbstractMesh> meshes = new ArrayList();
    Model mdl; for (Iterator localIterator1 = modelTree.iterator(); localIterator1.hasNext(); 
        meshes.add(mdl.getMesh()))
    {
      mdl = (Model)localIterator1.next();
      if ((hidden) || (!mdl.hasMesh())) {} }
    Collider c; for (localIterator1 = colliderTree.iterator(); localIterator1.hasNext(); 
        meshes.add(mesh))
    {
      c = (Collider)localIterator1.next();
      if ((hidden) || (!c.hasMesh())) {} }
    Zone z; for (localIterator1 = zoneTree.iterator(); localIterator1.hasNext(); 
        meshes.add(mesh))
    {
      z = (Zone)localIterator1.next();
      if ((hidden) || (!z.hasMesh())) {}
    }
    for (AbstractMesh m : meshes)
    {
      for (Triangle t : m)
      {
        if ((box.contains(t.getCenter())) && (!triangleList.contains(t))) {
          triangleList.add(t);
        }
      }
    }
    return triangleList;
  }
  
  public PickRay.PickHit pickTriangleFromObjectList(PickRay pickRay, Iterable<? extends MapObject> candidates)
  {
    PickRay.PickHit closestHit = new PickRay.PickHit(pickRay, Float.MAX_VALUE);
    for (MapObject obj : candidates)
    {
      if (!hidden)
      {

        for (Triangle t : obj.getMesh())
        {
          PickRay.PickHit hit = PickRay.getIntersection(pickRay, t);
          if (dist < dist)
          {
            closestHit = hit;
            obj = t;
          }
        } }
    }
    return closestHit;
  }
  
  public PickRay.PickHit pickTriangleFromList(PickRay pickRay, Iterable<Triangle> candidates)
  {
    PickRay.PickHit closestHit = new PickRay.PickHit(pickRay, Float.MAX_VALUE);
    for (Triangle t : candidates)
    {
      PickRay.PickHit hit = PickRay.getIntersection(pickRay, t);
      if (dist < dist)
      {
        closestHit = hit;
        obj = t;
      }
    }
    return closestHit;
  }
  
  public PickRay.PickHit pickVertexFromList(PickRay pickRay, Iterable<Vertex> candidates)
  {
    PickRay.PickHit closestHit = new PickRay.PickHit(pickRay, Float.MAX_VALUE);
    for (Vertex v : candidates)
    {
      PickRay.PickHit hit = PickRay.getPointIntersection(pickRay, v.getCurrentX(), v.getCurrentY(), v.getCurrentZ(), 1.0F);
      if (dist < dist)
      {
        closestHit = hit;
        obj = v;
      }
    }
    return closestHit;
  }
  
  public PickRay.PickHit pickUVFromList(PickRay pickRay, Iterable<UV> candidates)
  {
    PickRay.PickHit closestHit = new PickRay.PickHit(pickRay, Float.MAX_VALUE);
    for (UV uv : candidates)
    {
      PickRay.PickHit hit = PickRay.getIntersection(pickRay, uv);
      if (dist < dist)
      {
        closestHit = hit;
        obj = uv;
      }
    }
    return closestHit;
  }
  
  public static Map loadMap(String filename) throws IOException
  {
    return loadMap(new File(filename));
  }
  
  public static Map loadMap(File f)
  {
    Map map = load(f);
    source = f;
    initializeMapObjectData(map);
    return map;
  }
  
  public static Map loadMap(File backupFile, File f)
  {
    Map map = load(backupFile);
    source = f;
    initializeMapObjectData(map);
    return map;
  }
  
  private static Map load(File f)
  {
    Map map = null;
    try
    {
      InputStream buffer = new BufferedInputStream(new FileInputStream(f));
      ObjectInputStream in = new ObjectInputStream(buffer);
      
      map = (Map)in.readObject();
      in.close();
      Logger.log("Loaded map " + name);
    }
    catch (IOException e) {
      Logger.log(f.getName() + " could not be opened.", Priority.IMPORTANT);
      StarRodDev.displayStackTrace(e);
      map = null;
    } catch (ClassNotFoundException e) {
      Logger.log(f.getName() + " could not be deserialized.", Priority.IMPORTANT);
      StarRodDev.displayStackTrace(e);
      System.exit(0);
    }
    
    return map;
  }
  
  private static void initializeMapObjectData(Map map)
  {
    if (modelTree == null) {
      Logger.logWarning("Map has no geometry data!");
    }
    map.recalculateBoundingBoxes();
    
    MapObjectNode<Model> modelRoot = modelTree.getRoot();
    ((Model)modelRoot.getUserObject()).updateTransformHierarchy();
    modelTree.recalculateIndicies();
    
    if (colliderTree == null) {
      Logger.logWarning("Map has no collider data!");
    }
    colliderTree.recalculateIndicies();
    
    if (zoneTree == null) {
      Logger.logWarning("Map has no zone data!");
    }
    zoneTree.recalculateIndicies();
    
    if (markerTree == null) {
      Logger.logWarning("Map has no marker data!");
    }
    markerTree.recalculateIndicies();
  }
  




  public void recalculateBoundingBoxes()
  {
    modelTree.recalculateBoundingBoxes();
    colliderTree.recalculateBoundingBoxes();
    zoneTree.recalculateBoundingBoxes();
    markerTree.recalculateBoundingBoxes();
  }
  
  public void saveMap()
  {
    saveMapAs(source);
  }
  
  public void saveMapAs(String filename)
  {
    saveMapAs(new File(filename));
    modified = false;
  }
  
  public void saveMapAs(File f)
  {
    source = f;
    
    String filename = f.getName();
    if (filename.endsWith(".map")) {
      name = filename.substring(0, filename.length() - 4);
    } else {
      name = filename;
    }
    
    try
    {
      OutputStream buffer = new BufferedOutputStream(new FileOutputStream(f));
      ObjectOutputStream out = new ObjectOutputStream(buffer);
      out.writeObject(this);
      out.close();
      Logger.log("Saved map as " + f.getName());
    } catch (IOException e) {
      Logger.log("IOException during map save. " + e.getMessage());
      StarRodDev.displayStackTrace(e);
    }
  }
  
  public void saveBackup(File f)
  {
    try {
      OutputStream buffer = new BufferedOutputStream(new FileOutputStream(f));
      ObjectOutputStream out = new ObjectOutputStream(buffer);
      out.writeObject(this);
      out.close();
      Logger.log("Saved backup for " + name);
    } catch (IOException e) {
      Logger.log("IOException during map backup. " + e.getMessage());
      StarRodDev.displayStackTrace(e);
    }
  }
  
  public boolean fileExists()
  {
    return (source != null) && (source.exists());
  }
  
  public String getFilePath()
  {
    return source.getAbsolutePath();
  }
  

  public String getExpectedTexFilename()
  {
    return name.substring(0, 3) + "_tex";
  }
  
  public void exportSelection(File f) throws IOException
  {
    ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(f));
    List<MapObject> objectList = new LinkedList();
    
    addSelectionFromTree(objectList, modelTree);
    addSelectionFromTree(objectList, colliderTree);
    addSelectionFromTree(objectList, zoneTree);
    addSelectionFromTree(objectList, markerTree);
    
    for (MapObject obj : objectList) {
      Logger.log("Exported " + name);
    }
    out.writeObject(objectList);
    
    out.close();
  }
  
  private <T extends MapObject> void addSelectionFromTree(List<MapObject> objectList, MapObjectTreeModel<T> treeModel)
  {
    Stack<MapObjectNode<T>> nodes = new Stack();
    nodes.push(treeModel.getRoot());
    
    while (!nodes.isEmpty())
    {
      MapObjectNode<T> node = (MapObjectNode)nodes.pop();
      T obj = node.getUserObject();
      
      if (selected)
      {
        objectList.add(obj);
      }
      else
      {
        for (int i = 0; i < node.getChildCount(); i++) {
          nodes.push(node.getChildAt(i));
        }
      }
    }
  }
  
  public List<MapObject> importSelection(File f) throws IOException
  {
    List<MapObject> objectList = null;
    try
    {
      ObjectInputStream in = new ObjectInputStream(new FileInputStream(f));
      objectList = (List)in.readObject();
      in.close();
      
      for (MapObject obj : objectList)
      {
        Logger.log("Importing " + name);
        
        getNodeparentNode = null;
        
        if ((obj instanceof Model))
        {
          Model mdl = (Model)obj;
          mdl.updateTransformHierarchy();
          mdl.getMesh().setTexture(getMeshtextureName);
        }
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      Logger.log(f.getName() + " could not be opened.", Priority.IMPORTANT);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
      System.exit(0);
    }
    
    return objectList;
  }
  
  public Model getModel(String name)
  {
    return (Model)modelTree.getObject(name);
  }
  
  public int getModelID(String name)
  {
    return modelTree.getObjectID(name);
  }
  
  public String getModelName(int id)
  {
    return modelTree.getObjectName(id);
  }
  
  public Collider getCollider(String name)
  {
    return (Collider)colliderTree.getObject(name);
  }
  
  public int getColliderID(String name)
  {
    return colliderTree.getObjectID(name);
  }
  
  public String getColliderName(int id)
  {
    return colliderTree.getObjectName(id);
  }
  
  public Zone getZone(String name)
  {
    return (Zone)zoneTree.getObject(name);
  }
  
  public int getZoneID(String name)
  {
    return zoneTree.getObjectID(name);
  }
  
  public String getZoneName(int id)
  {
    return zoneTree.getObjectName(id);
  }
  
  public Marker getMarker(String name)
  {
    for (Marker m : markerTree)
    {
      if (name.equals(name)) {
        return m;
      }
    }
    return null;
  }
  




  public MapObject find(MapObject.MapObjectType objType, String objName)
  {
    MapObjectTreeModel<?> tree = null;
    switch (1.$SwitchMap$editor$map$MapObject$MapObjectType[objType.ordinal()]) {
    case 1: 
      tree = modelTree; break;
    case 2:  tree = colliderTree; break;
    case 3:  tree = zoneTree; break;
    case 4:  tree = markerTree;
    }
    
    Stack<MapObjectNode<?>> stack = new Stack();
    stack.push(tree.getRoot());
    
    while (!stack.isEmpty())
    {
      MapObjectNode<?> node = (MapObjectNode)stack.pop();
      for (int i = 0; i < node.getChildCount(); i++) {
        stack.push(node.getChildAt(i));
      }
      MapObject obj = node.getUserObject();
      if (name.equals(objName)) {
        return obj;
      }
    }
    return null;
  }
  
  public static final class ToggleBackground extends AbstractCommand
  {
    private final Map m;
    
    public ToggleBackground(Map m)
    {
      super();
      this.m = m;
    }
    

    public void exec()
    {
      super.exec();
      m.hasBackground = (!m.hasBackground);
      guihasBackgroundCheckbox.setSelected(m.hasBackground);
    }
    

    public void undo()
    {
      super.undo();
      m.hasBackground = (!m.hasBackground);
      guihasBackgroundCheckbox.setSelected(m.hasBackground);
    }
  }
  
  public static final class SetBackground
    extends AbstractCommand
  {
    private final Map map;
    private final String oldName;
    private final String newName;
    private final BufferedImage oldImage;
    private final BufferedImage newImage;
    
    public SetBackground(Map map, String name, BufferedImage img)
    {
      super();
      this.map = map;
      
      oldName = bgName;
      oldImage = bgImage;
      
      newName = name;
      newImage = img;
    }
    

    public boolean shouldExec()
    {
      return (!newName.isEmpty()) && (!newName.equals(oldName));
    }
    

    public void exec()
    {
      super.exec();
      map.bgName = newName;
      
      GL11.glDeleteTextures(map.glBackgroundTexID);
      map.glBackgroundTexID = TextureManager.bindBufferedImage(newImage);
    }
    

    public void undo()
    {
      super.undo();
      map.bgName = oldName;
      
      GL11.glDeleteTextures(map.glBackgroundTexID);
      map.glBackgroundTexID = TextureManager.bindBufferedImage(oldImage);
    }
  }
  
  public static final class CreateLightset extends AbstractCommand
  {
    private final Map map;
    private final Model mdl;
    private final LightSet newLightSet;
    private final LightSet prevLightSet;
    
    public CreateLightset(Map map, Model mdl)
    {
      super();
      this.map = map;
      this.mdl = mdl;
      newLightSet = LightSet.createEmptySet();
      prevLightSet = lightSet;
    }
    

    public void exec()
    {
      super.exec();
      map.lightSets.add(newLightSet);
      mdl.lightSet = newLightSet;
      LightingPanel.setModel(mdl);
    }
    

    public void undo()
    {
      super.undo();
      map.lightSets.remove(newLightSet);
      mdl.lightSet = prevLightSet;
      LightingPanel.setModel(mdl);
    }
  }
  
  public static final class DeleteLightset extends AbstractCommand
  {
    private final Map map;
    private final List<Model> mdlList;
    private final LightSet lightSet;
    private final int listPosition;
    
    public DeleteLightset(Map map, LightSet lightSet)
    {
      super();
      this.map = map;
      this.lightSet = lightSet;
      listPosition = lightSets.indexOf(lightSet);
      
      mdlList = new LinkedList();
      for (Model mdl : modelTree.getList())
      {
        if (lightSet == lightSet) {
          mdlList.add(mdl);
        }
      }
    }
    
    public boolean shouldExec()
    {
      return map.lightSets.size() > 1;
    }
    

    public void exec()
    {
      super.exec();
      map.lightSets.remove(lightSet);
      for (Model mdl : mdlList)
        lightSet = ((LightSet)map.lightSets.get(0));
      LightingPanel.refresh();
    }
    

    public void undo()
    {
      super.undo();
      map.lightSets.add(listPosition, lightSet);
      for (Model mdl : mdlList)
        lightSet = lightSet;
      LightingPanel.refresh();
    }
  }
}
