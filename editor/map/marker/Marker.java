package editor.map.marker;

import editor.Editor;
import editor.Editor.Mode;
import editor.Grid;
import editor.commands.AbstractCommand;
import editor.map.Axis;
import editor.map.BoundingBox;
import editor.map.MapObject;
import editor.map.MapObject.MapObjectType;
import editor.map.MutableAngle;
import editor.map.MutableAngle.AngleBackup;
import editor.map.MutablePoint;
import editor.map.MutablePoint.PointBackup;
import editor.map.ReversibleTransform;
import editor.map.mesh.AbstractMesh;
import editor.map.shape.TransformMatrix;
import editor.map.tree.MapObjectNode;
import editor.render.Renderer;
import editor.render.TextureManager;
import editor.selection.PickRay;
import editor.selection.PickRay.PickHit;
import editor.selection.SelectablePoint;
import editor.selection.Selection;
import editor.selection.SelectionManager;
import editor.ui.info.MarkerInfoPanel;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.vector.Vector3f;
import util.IdentityHashSet;



































public class Marker
  extends MapObject
  implements Externalizable
{
  private static final long serialVersionUID = 1L;
  private static final int latestVersion = 1;
  private int instanceVersion = 1;
  private MapObjectNode<Marker> node;
  private MarkerType type;
  
  protected static enum MarkerColor { TEAL(0.0F, 1.0F, 1.0F), 
    GREEN(0.0F, 1.0F, 0.0F), 
    YELLOW(1.0F, 1.0F, 0.0F), 
    RED(1.0F, 0.0F, 0.0F), 
    PINK(1.0F, 0.4F, 0.8F);
    
    private final float r;
    private final float g;
    private final float b;
    
    private MarkerColor(float r, float g, float b) { this.r = r;
      this.g = g;
      this.b = b;
    }
    
    private void setGLColor(float a)
    {
      GL11.glColor4f(r, g, b, a);
    }
  }
  
  public static enum MarkerType
  {
    Root("Root", null, null), 
    Entry("Entry", Marker.MarkerColor.TEAL, Marker.MarkerColor.PINK), 
    NPC("NPC", Marker.MarkerColor.GREEN, Marker.MarkerColor.PINK), 
    Position("Position", Marker.MarkerColor.YELLOW, Marker.MarkerColor.PINK), 
    Trigger("Bomb Position", Marker.MarkerColor.RED, Marker.MarkerColor.PINK), 
    Grid("Push Block Grid", Marker.MarkerColor.RED, Marker.MarkerColor.PINK), 
    Path("Path", Marker.MarkerColor.YELLOW, Marker.MarkerColor.PINK);
    
    public final String name;
    private final Marker.MarkerColor c1;
    private final Marker.MarkerColor c2;
    
    private MarkerType(String name, Marker.MarkerColor c1, Marker.MarkerColor c2)
    {
      this.name = name;
      this.c1 = c1;
      this.c2 = c2;
    }
    
    public Marker.MarkerColor getColor(boolean selected)
    {
      return selected ? c2 : c1;
    }
    
    public String toString()
    {
      return name;
    }
  }
  



  private String description = "";
  
  public MutablePoint position;
  
  public MutableAngle yaw;
  public NpcMovementSettings moveSettings = null;
  

  public float bombRadius;
  

  public int gridX = 1;
  public int gridZ = 1;
  public int gridSize = 25;
  
  public List<Waypoint> path = new ArrayList();
  



  public Marker() {}
  


  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    super.readExternal(in);
    try
    {
      instanceVersion = in.readInt();
    }
    catch (IOException e) {
      System.out.println("Could not read Marker version number.");
      instanceVersion = 0;
    }
    
    type = ((MarkerType)in.readObject());
    node = ((MapObjectNode)in.readObject());
    
    description = in.readUTF();
    
    position = ((MutablePoint)in.readObject());
    yaw = new MutableAngle(in.readDouble(), Axis.Y, true);
    
    moveSettings = ((NpcMovementSettings)in.readObject());
    
    if ((type != MarkerType.Root) && (moveSettings == null)) {
      moveSettings = new NpcMovementSettings(this);
    }
    bombRadius = in.readFloat();
    
    if (instanceVersion >= 1)
    {
      gridX = in.readInt();
      gridZ = in.readInt();
      gridSize = in.readInt();
    }
    
    int pathLength = in.readInt();
    for (int i = 0; i < pathLength; i++)
    {
      int x = in.readInt();
      int y = in.readInt();
      int z = in.readInt();
      Waypoint.addToList(path, x, y, z);
    }
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    super.writeExternal(out);
    out.writeInt(1);
    
    out.writeObject(type);
    out.writeObject(node);
    
    out.writeUTF(description);
    
    out.writeObject(position);
    out.writeDouble(yaw.getAngle());
    
    out.writeObject(moveSettings);
    
    out.writeFloat(bombRadius);
    
    out.writeInt(gridX);
    out.writeInt(gridZ);
    out.writeInt(gridSize);
    
    out.writeInt(path.size());
    for (SelectablePoint p : path)
    {
      out.writeInt(p.getX());
      out.writeInt(p.getY());
      out.writeInt(p.getZ());
    }
  }
  
  public void addPath(List<Vector3f> vectors)
  {
    path = new LinkedList();
    for (Vector3f vec : vectors) {
      Waypoint.addToList(path, vec);
    }
  }
  
  public Marker(String name, MarkerType type, float x, float y, float z, double angle) {
    instanceVersion = 1;
    this.type = type;
    this.name = name;
    
    node = new MapObjectNode(this);
    
    AABB = new BoundingBox();
    
    position = new MutablePoint(x, y, z);
    yaw = new MutableAngle(angle, Axis.Y, true);
    
    if ((type != MarkerType.Root) && (moveSettings == null)) {
      moveSettings = new NpcMovementSettings(this);
    }
    recalculateAABB();
  }
  
  public static Marker makeDummyRoot()
  {
    Marker m = new Marker();
    type = MarkerType.Root;
    name = "Root";
    node = new MapObjectNode(m);
    yaw = new MutableAngle(0, Axis.Y, true);
    return m;
  }
  

  public Marker deepCopy()
  {
    Marker m = new Marker(name, type, position.getX(), position.getY(), position.getZ(), yaw.getAngle());
    AABB = AABB.deepCopy();
    dirtyAABB = dirtyAABB;
    
    moveSettings = new NpcMovementSettings(m, moveSettings.getMovementData(), moveSettings.type);
    bombRadius = bombRadius;
    
    return m;
  }
  
  public MarkerType getType()
  {
    return type;
  }
  
  public void setDescription(String desc)
  {
    if (desc == null) {
      throw new IllegalArgumentException("Invalid description given for Marker.");
    }
    description = desc;
  }
  
  public String getDescription()
  {
    return description;
  }
  
  public void putVector(List<String> line, boolean useFloat)
  {
    if (useFloat)
    {
      line.add(String.format("%08X", new Object[] { Integer.valueOf(Float.floatToIntBits(position.getX())) }));
      line.add(String.format("%08X", new Object[] { Integer.valueOf(Float.floatToIntBits(position.getY())) }));
      line.add(String.format("%08X", new Object[] { Integer.valueOf(Float.floatToIntBits(position.getZ())) }));
    }
    else
    {
      line.add(String.format("%08X", new Object[] { Integer.valueOf(position.getX()) }));
      line.add(String.format("%08X", new Object[] { Integer.valueOf(position.getY()) }));
      line.add(String.format("%08X", new Object[] { Integer.valueOf(position.getZ()) }));
    }
  }
  
  public void putPath(List<String> line, boolean useFloat)
  {
    for (SelectablePoint p : path)
    {
      if (useFloat)
      {
        line.add(String.format("%08X", new Object[] { Integer.valueOf(Float.floatToIntBits(p.getX())) }));
        line.add(String.format("%08X", new Object[] { Integer.valueOf(Float.floatToIntBits(p.getY())) }));
        line.add(String.format("%08X", new Object[] { Integer.valueOf(Float.floatToIntBits(p.getZ())) }));
      }
      else
      {
        line.add(String.format("%08X", new Object[] { Integer.valueOf(p.getX()) }));
        line.add(String.format("%08X", new Object[] { Integer.valueOf(p.getY()) }));
        line.add(String.format("%08X", new Object[] { Integer.valueOf(p.getZ()) }));
      }
    }
  }
  
  public void putGrid(List<String> line)
  {
    line.add(String.format("%08X", new Object[] { Integer.valueOf(gridX) }));
    line.add(String.format("%08X", new Object[] { Integer.valueOf(gridZ) }));
    line.add(String.format("%08X", new Object[] { Integer.valueOf(position.getX()) }));
    line.add(String.format("%08X", new Object[] { Integer.valueOf(position.getY()) }));
    line.add(String.format("%08X", new Object[] { Integer.valueOf(position.getZ()) }));
  }
  
  public void putAngle(List<String> line, boolean useFloat)
  {
    if (useFloat) {
      line.add(String.format("%08X", new Object[] { Integer.valueOf(Float.floatToIntBits((float)yaw.getAngle())) }));
    } else {
      line.add(String.format("%08X", new Object[] { Integer.valueOf((int)yaw.getAngle()) }));
    }
  }
  
  public List<SelectablePoint> getSelectablePoints() {
    List<SelectablePoint> points = new LinkedList();
    if (type == MarkerType.NPC) {
      moveSettings.addSelectablePoints(points);
    }
    else {
      for (Waypoint wp : path)
        points.add(wp);
    }
    return points;
  }
  

  public MapObject.MapObjectType getObjectType()
  {
    return MapObject.MapObjectType.MARKER;
  }
  

  public void addTo(BoundingBox aabb)
  {
    aabb.encompass(position.getX(), position.getY(), position.getZ());
  }
  

  public void startTransformation()
  {
    position.startTransform();
    
    AABB.min.startTransform();
    AABB.max.startTransform();
    
    yaw.startTransform();
  }
  










  public void endTransformation()
  {
    position.endTransform();
    
    AABB.min.endTransform();
    AABB.max.endTransform();
    
    yaw.endTransform();
  }
  









  private int getSize()
  {
    return objectGridbinary ? 8 : 10;
  }
  

  public void recalculateAABB()
  {
    int size = getSize();
    
    AABB.clear();
    if (type != MarkerType.Root)
    {
      AABB.encompass(position.getX() - size, position.getY() - size, position.getZ() - size);
      AABB.encompass(position.getX() + size, position.getY() + size, position.getZ() + size);
    }
  }
  

  public boolean allowRotation(Axis axis)
  {
    return axis == Axis.Y;
  }
  

  public ReversibleTransform createTransformer(TransformMatrix m)
  {
    final IdentityHashSet<MutablePoint.PointBackup> backupList = new IdentityHashSet();
    backupList.add(position.getBackup());
    backupList.add(AABB.min.getBackup());
    backupList.add(AABB.max.getBackup());
    











    final MutableAngle.AngleBackup backupYaw = yaw.getBackup();
    
    new ReversibleTransform()
    {

      public void transform()
      {
        for (MutablePoint.PointBackup b : backupList)
          pos.setPosition(newx, newy, newz);
        yaw.setAngle(backupYawnewAngle);
      }
      

      public void revert()
      {
        for (MutablePoint.PointBackup b : backupList)
          pos.setPosition(oldx, oldy, oldz);
        yaw.setAngle(backupYawoldAngle);
      }
    };
  }
  

  public void addPoints(IdentityHashSet<MutablePoint> positions)
  {
    positions.add(position);
    positions.add(AABB.min);
    positions.add(AABB.max);
  }
  












  public void addAngles(IdentityHashSet<MutableAngle> angles)
  {
    angles.add(yaw);
  }
  

  public boolean shouldTryPick(PickRay ray)
  {
    return true;
  }
  

  public PickRay.PickHit tryPick(PickRay ray)
  {
    PickRay.PickHit hit = PickRay.getIntersection(ray, AABB);
    obj = this;
    return hit;
  }
  

  public boolean shouldTryTrianglePick(PickRay ray)
  {
    return false;
  }
  
  public String toString()
  {
    return name;
  }
  

  public boolean hasMesh()
  {
    return false;
  }
  

  public boolean shouldIterate()
  {
    return type != MarkerType.Root;
  }
  

  public boolean shouldDraw()
  {
    return (!hidden) && (type != MarkerType.Root);
  }
  

  public AbstractMesh getMesh()
  {
    return null;
  }
  

  public void updateMeshHierarchy() {}
  

  public void render(float alpha, boolean drawHiddenPaths)
  {
    boolean editPointsMode = Editor.getEditorMode() == Editor.Mode.EditPoints;
    
    GL11.glPushAttrib(8462);
    
    type.getColor(selected).setGLColor(alpha);
    
    GL11.glPolygonMode(1032, 6914);
    
    GL11.glEnable(3553);
    GL11.glBindTexture(3553, TextureManager.glMarkerTexID);
    
    GL11.glBegin(4);
    drawTexturedCube(position.getX(), position.getY(), position.getZ(), getSize());
    GL11.glEnd();
    


    GL11.glDisable(3553);
    
    GL11.glColor3f(1.0F, 1.0F, 1.0F);
    GL11.glPolygonMode(1032, 6913);
    
    double dx = 32.0D * Math.sin(Math.toRadians(yaw.getAngle()));
    double dz = 32.0D * -Math.cos(Math.toRadians(yaw.getAngle()));
    
    float forwardX = (float)(position.getX() + dx);
    float forwardZ = (float)(position.getZ() + dz);
    
    GL11.glLineWidth(5.0F);
    GL11.glBegin(1);
    GL11.glVertex3f(position.getX(), position.getY(), position.getZ());
    GL11.glVertex3f(forwardX, position.getY(), forwardZ);
    GL11.glEnd();
    


    if (selected)
    {
      switch (2.$SwitchMap$editor$map$marker$Marker$MarkerType[type.ordinal()]) {
      case 1: 
        drawNpcHelpers(editPointsMode, drawHiddenPaths); break;
      case 2:  drawPathHelpers(editPointsMode, drawHiddenPaths); break;
      case 3:  drawBombRadius(); break;
      case 4:  drawGrid(); break;
      }
      
    }
    
    GL11.glPopAttrib();
  }
  
  private void drawBombRadius()
  {
    GL11.glLineWidth(2.0F);
    GL11.glPushMatrix();
    GL11.glTranslatef(position.getX(), position.getY(), position.getZ());
    Sphere sphere = new Sphere();
    sphere.draw(50.0F + bombRadius, 12, 12);
    GL11.glPopMatrix();
  }
  
  private void drawGrid()
  {
    GL11.glLineWidth(2.0F);
    GL11.glDepthMask(false);
    GL11.glDepthFunc(519);
    GL11.glBegin(1);
    for (int i = 0; i <= gridX; i++)
    {
      GL11.glVertex3f(position.getX() + gridSize * i, position.getY() + 1, position.getZ());
      GL11.glVertex3f(position.getX() + gridSize * i, position.getY() + 1, position.getZ() + gridSize * gridZ);
    }
    for (int i = 0; i <= gridZ; i++)
    {
      GL11.glVertex3f(position.getX(), position.getY() + 1, position.getZ() + gridSize * i);
      GL11.glVertex3f(position.getX() + gridSize * gridX, position.getY() + 1, position.getZ() + gridSize * i);
    }
    GL11.glEnd();
    GL11.glDepthMask(true);
  }
  
  private void drawNpcHelpers(boolean editPointsMode, boolean drawHiddenPaths)
  {
    GL11.glLineWidth(2.0F);
    if (moveSettings.useDetectCircle) {
      drawCircularVolume(moveSettings.detectPoint
        .getX(), moveSettings.detectPoint.getY(), moveSettings.detectPoint
        .getZ(), moveSettings.detectR, 50);
    } else {
      drawRectangularVolume(moveSettings.detectPoint
        .getX(), moveSettings.detectPoint.getY(), moveSettings.detectPoint
        .getZ(), moveSettings.detectX, moveSettings.detectZ, 50);
    }
    if (moveSettings.type == NpcMovementSettings.MoveType.Wander)
    {
      if (moveSettings.useWanderCircle) {
        drawCircularVolume(moveSettings.wanderPoint
          .getX(), moveSettings.wanderPoint.getY(), moveSettings.wanderPoint
          .getZ(), moveSettings.wanderR, 50);
      } else {
        drawRectangularVolume(moveSettings.wanderPoint
          .getX(), moveSettings.wanderPoint.getY(), moveSettings.wanderPoint
          .getZ(), moveSettings.wanderX, moveSettings.wanderZ, 50);
      }
    } else if (moveSettings.type == NpcMovementSettings.MoveType.Patrol)
    {
      drawPatrolPath(editPointsMode, drawHiddenPaths);
    }
    
    if (editPointsMode)
    {
      GL11.glColor3f(1.0F, 1.0F, 0.0F);
      GL11.glPointSize(editPointsMode ? 12.0F : 8.0F);
      GL11.glLineWidth(3.0F);
      
      GL11.glDepthFunc(519);
      
      GL11.glBegin(0);
      SelectablePoint point = moveSettings.detectCenter;
      GL11.glVertex3f(point.getX(), point.getY(), point.getZ());
      if (moveSettings.type == NpcMovementSettings.MoveType.Wander)
      {
        point = moveSettings.wanderCenter;
        GL11.glVertex3f(point.getX(), point.getY(), point.getZ());
      }
      GL11.glEnd();
    }
  }
  
  private void drawPathHelpers(boolean editPointsMode, boolean drawHiddenPaths)
  {
    GL11.glColor3f(1.0F, 1.0F, 0.0F);
    GL11.glPointSize(editPointsMode ? 12.0F : 8.0F);
    GL11.glLineWidth(3.0F);
    
    GL11.glDepthFunc(519);
    
    GL11.glBegin(0);
    for (Iterator localIterator = path.iterator(); localIterator.hasNext();) { point = (SelectablePoint)localIterator.next();
      GL11.glVertex3f(point.getX(), point.getY(), point.getZ()); }
    SelectablePoint point; GL11.glEnd();
    
    if (drawHiddenPaths) {
      GL11.glDepthFunc(515);
    }
    GL11.glBegin(1);
    SelectablePoint last = null;
    for (SelectablePoint point : path)
    {
      if (last != null)
      {
        GL11.glVertex3f(last.getX(), last.getY(), last.getZ());
        GL11.glVertex3f(point.getX(), point.getY(), point.getZ());
      }
      last = point;
    }
    GL11.glEnd();
    
    if (drawHiddenPaths)
    {
      GL11.glDepthMask(false);
      GL11.glDepthFunc(516);
      last = null;
      for (SelectablePoint point : path)
      {
        if (last != null)
        {
          Renderer.drawStipple(
            last.getX(), last.getY(), last.getZ(), point
            .getX(), point.getY(), point.getZ(), 10.0F);
        }
        
        last = point;
      }
      GL11.glDepthMask(true);
    }
  }
  
  private void drawPatrolPath(boolean editPointsMode, boolean drawHiddenPaths)
  {
    GL11.glColor3f(1.0F, 1.0F, 0.0F);
    GL11.glPointSize(editPointsMode ? 12.0F : 8.0F);
    GL11.glLineWidth(3.0F);
    
    GL11.glDepthFunc(519);
    
    GL11.glBegin(0);
    for (int i = 0; i < moveSettings.patrolPath.size(); i++)
    {
      Waypoint wp = (Waypoint)moveSettings.patrolPath.get(i);
      GL11.glVertex3f(wp.getX(), wp.getY(), wp.getZ());
    }
    GL11.glEnd();
    
    if (drawHiddenPaths) {
      GL11.glDepthFunc(515);
    }
    GL11.glBegin(1);
    for (int i = 0; i < moveSettings.patrolPath.size() - 1; i++)
    {
      Waypoint wp1 = (Waypoint)moveSettings.patrolPath.get(i);
      Waypoint wp2 = (Waypoint)moveSettings.patrolPath.get(i + 1);
      GL11.glVertex3f(wp1.getX(), wp1.getY(), wp1.getZ());
      GL11.glVertex3f(wp2.getX(), wp2.getY(), wp2.getZ());
    }
    GL11.glEnd();
    
    if (drawHiddenPaths)
    {
      GL11.glDepthMask(false);
      GL11.glDepthFunc(516);
      for (int i = 0; i < moveSettings.patrolPath.size() - 1; i++)
      {
        Waypoint wp1 = (Waypoint)moveSettings.patrolPath.get(i);
        Waypoint wp2 = (Waypoint)moveSettings.patrolPath.get(i + 1);
        Renderer.drawStipple(wp1
          .getX(), wp1.getY(), wp1.getZ(), wp2
          .getX(), wp2.getY(), wp2.getZ(), 10.0F);
      }
      
      GL11.glDepthMask(true);
    }
  }
  


  private void drawTexturedCube(int cx, int cy, int cz, int r)
  {
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(cx - r, cy - r, cz - r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(cx - r, cy + r, cz - r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(cx - r, cy + r, cz + r);
    
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(cx - r, cy - r, cz - r);
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(cx - r, cy - r, cz + r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(cx - r, cy + r, cz + r);
    


    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(cx + r, cy - r, cz - r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(cx + r, cy + r, cz - r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(cx + r, cy + r, cz + r);
    
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(cx + r, cy - r, cz - r);
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(cx + r, cy - r, cz + r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(cx + r, cy + r, cz + r);
    


    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(cx - r, cy - r, cz + r);
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(cx + r, cy - r, cz + r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(cx + r, cy + r, cz + r);
    
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(cx - r, cy - r, cz + r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(cx - r, cy + r, cz + r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(cx + r, cy + r, cz + r);
    


    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(cx - r, cy - r, cz - r);
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(cx - r, cy - r, cz + r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(cx + r, cy - r, cz + r);
    
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(cx - r, cy - r, cz - r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(cx + r, cy - r, cz - r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(cx + r, cy - r, cz + r);
    


    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(cx - r, cy + r, cz - r);
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(cx - r, cy + r, cz + r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(cx + r, cy + r, cz + r);
    
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(cx - r, cy + r, cz - r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(cx + r, cy + r, cz - r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(cx + r, cy + r, cz + r);
    






    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(cx - r, cy - r, cz - r);
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(cx + r, cy - r, cz - r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(cx + r, cy + r, cz - r);
    
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(cx - r, cy - r, cz - r);
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(cx - r, cy + r, cz - r);
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(cx + r, cy + r, cz - r);
  }
  
  private void drawCircularVolume(int cx, int cy, int cz, int radius, int h)
  {
    GL11.glBegin(8);
    GL11.glVertex3f(cx + radius, cy + 0, cz);
    GL11.glVertex3f(cx + radius, cy + h, cz);
    int N = 2 * Math.round(1.0F + (float)(radius / Math.sqrt(radius)));
    for (int i = 1; i <= N; i++)
    {
      float x = radius * (float)Math.cos(2 * i * 3.141592653589793D / N);
      float z = radius * (float)Math.sin(2 * i * 3.141592653589793D / N);
      GL11.glVertex3f(cx + x, cy + 0, cz + z);
      GL11.glVertex3f(cx + x, cy + h, cz + z);
    }
    GL11.glEnd();
  }
  
  private void drawRectangularVolume(int cx, int cy, int cz, int sizeX, int sizeZ, int h)
  {
    GL11.glBegin(8);
    GL11.glVertex3f(cx - sizeX, cy + 0, cz - sizeZ);
    GL11.glVertex3f(cx - sizeX, cy + h, cz - sizeZ);
    GL11.glVertex3f(cx - sizeX, cy + 0, cz + sizeZ);
    GL11.glVertex3f(cx - sizeX, cy + h, cz + sizeZ);
    GL11.glVertex3f(cx + sizeX, cy + 0, cz + sizeZ);
    GL11.glVertex3f(cx + sizeX, cy + h, cz + sizeZ);
    GL11.glVertex3f(cx + sizeX, cy + 0, cz - sizeZ);
    GL11.glVertex3f(cx + sizeX, cy + h, cz - sizeZ);
    GL11.glVertex3f(cx - sizeX, cy + 0, cz - sizeZ);
    GL11.glVertex3f(cx - sizeX, cy + h, cz - sizeZ);
    GL11.glEnd();
  }
  

  public boolean allowsPopup()
  {
    return false;
  }
  

  public boolean allowsChildren()
  {
    return type == MarkerType.Root;
  }
  

  public MapObjectNode<Marker> getNode()
  {
    return node;
  }
  

  public static final class SetMarkerType
    extends AbstractCommand
  {
    private Marker m;
    
    private final Marker.MarkerType oldType;
    
    private final Marker.MarkerType newType;
    
    public SetMarkerType(Marker m, Marker.MarkerType type)
    {
      super();
      this.m = m;
      oldType = type;
      newType = type;
    }
    

    public boolean shouldExec()
    {
      return oldType != newType;
    }
    

    public void exec()
    {
      super.exec();
      m.type = newType;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.type = oldType;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetMarkerName extends AbstractCommand
  {
    private Marker m;
    private final String oldName;
    private final String newName;
    
    public SetMarkerName(Marker m, String s)
    {
      super();
      this.m = m;
      oldName = name;
      newName = s;
    }
    

    public boolean shouldExec()
    {
      return (!newName.isEmpty()) && (!newName.equals(oldName));
    }
    

    public void exec()
    {
      super.exec();
      m.name = newName;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.name = oldName;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetX extends AbstractCommand
  {
    private Marker m;
    private final int oldValue;
    private final int newValue;
    
    public SetX(Marker m, int x)
    {
      super();
      this.m = m;
      oldValue = position.getX();
      newValue = x;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.position.setX(newValue);
      m.recalculateAABB();
      selectionManagercurrentSelection.updateAABB();
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.position.setX(oldValue);
      m.recalculateAABB();
      selectionManagercurrentSelection.updateAABB();
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetY extends AbstractCommand
  {
    private Marker m;
    private final int oldValue;
    private final int newValue;
    
    public SetY(Marker m, int y)
    {
      super();
      this.m = m;
      oldValue = position.getY();
      newValue = y;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.position.setY(newValue);
      m.recalculateAABB();
      selectionManagercurrentSelection.updateAABB();
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.position.setY(oldValue);
      m.recalculateAABB();
      selectionManagercurrentSelection.updateAABB();
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetZ extends AbstractCommand
  {
    private Marker m;
    private final int oldValue;
    private final int newValue;
    
    public SetZ(Marker m, int z)
    {
      super();
      this.m = m;
      oldValue = position.getZ();
      newValue = z;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.position.setZ(newValue);
      m.recalculateAABB();
      selectionManagercurrentSelection.updateAABB();
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.position.setZ(oldValue);
      m.recalculateAABB();
      selectionManagercurrentSelection.updateAABB();
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetAngle extends AbstractCommand
  {
    private Marker m;
    private final double oldValue;
    private final double newValue;
    
    public SetAngle(Marker m, float angle)
    {
      super();
      this.m = m;
      oldValue = yaw.getAngle();
      newValue = angle;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.yaw.setAngle(newValue);
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.yaw.setAngle(oldValue);
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetBombRadius extends AbstractCommand
  {
    private Marker m;
    private final float oldValue;
    private final float newValue;
    
    public SetBombRadius(Marker m, float r)
    {
      super();
      this.m = m;
      oldValue = bombRadius;
      newValue = r;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.bombRadius = newValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.bombRadius = oldValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetGridX extends AbstractCommand
  {
    private Marker m;
    private final int oldValue;
    private final int newValue;
    
    public SetGridX(Marker m, int gridX)
    {
      super();
      this.m = m;
      oldValue = gridX;
      newValue = gridX;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.gridX = newValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.gridX = oldValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetGridZ extends AbstractCommand
  {
    private Marker m;
    private final int oldValue;
    private final int newValue;
    
    public SetGridZ(Marker m, int gridZ)
    {
      super();
      this.m = m;
      oldValue = gridZ;
      newValue = gridZ;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.gridZ = newValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.gridZ = oldValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetGridSize extends AbstractCommand
  {
    private Marker m;
    private final int oldValue;
    private final int newValue;
    
    public SetGridSize(Marker m, int gridSize)
    {
      super();
      this.m = m;
      oldValue = gridSize;
      newValue = gridSize;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.gridSize = newValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.gridSize = oldValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetPathCoordinate extends AbstractCommand
  {
    private final Marker m;
    private final int index;
    private final int axis;
    private final int oldValue;
    private final int newValue;
    
    public SetPathCoordinate(Marker m, int index, int axis, int value)
    {
      super();
      this.m = m;
      this.index = index;
      this.axis = axis;
      oldValue = ((Waypoint)path.get(index)).get(axis);
      newValue = value;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      ((Waypoint)m.path.get(index)).set(axis, newValue);
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      ((Waypoint)m.path.get(index)).set(axis, oldValue);
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class AddPathWaypoint extends AbstractCommand {
    private final List<Waypoint> path;
    private final int x;
    private final int y;
    private final int z;
    private final int maxLength;
    
    public AddPathWaypoint(Marker m, List<Waypoint> path) {
      this(m, path, Integer.MAX_VALUE);
    }
    
    public AddPathWaypoint(Marker m, List<Waypoint> path, int maxLength)
    {
      super();
      this.path = path;
      this.maxLength = maxLength;
      
      if (path.isEmpty())
      {
        x = position.getX();
        y = position.getY();
        z = position.getZ();
      }
      else if (path.size() == 1)
      {
        Waypoint last = (Waypoint)path.get(path.size() - 1);
        x = (last.getX() + 100);
        y = last.getY();
        z = last.getZ();
      }
      else
      {
        Waypoint wpA = (Waypoint)path.get(path.size() - 2);
        Waypoint wpB = (Waypoint)path.get(path.size() - 1);
        Vector3f diff = new Vector3f();
        Vector3f.sub(wpB.getPosition(), wpA.getPosition(), diff);
        
        if (diff.length() < 1.0F)
        {
          x = (wpB.getX() + 100);
          y = wpB.getY();
          z = wpB.getZ();
        }
        else
        {
          diff.normalise();
          x = (wpB.getX() + (int)(100.0F * x));
          y = (wpB.getY() + (int)(100.0F * y));
          z = (wpB.getZ() + (int)(100.0F * z));
        }
      }
    }
    

    public boolean shouldExec()
    {
      return path.size() < maxLength;
    }
    

    public void exec()
    {
      super.exec();
      Waypoint.addToList(path, x, y, z);
      MarkerInfoPanel.instance().refresh();
      MarkerInfoPanel.instance().repaint();
    }
    

    public void undo()
    {
      super.undo();
      path.remove(path.size() - 1);
      MarkerInfoPanel.instance().refresh();
      MarkerInfoPanel.instance().repaint();
    }
  }
  
  public static final class DeletePathWaypoint extends AbstractCommand
  {
    private final Waypoint wp;
    private final int initialPos;
    
    public DeletePathWaypoint(Waypoint wp)
    {
      super();
      this.wp = wp;
      initialPos = wp.getListIndex();
    }
    

    public void exec()
    {
      super.exec();
      wp.parent.remove(initialPos);
      MarkerInfoPanel.instance().refresh();
      MarkerInfoPanel.instance().repaint();
    }
    

    public void undo()
    {
      super.undo();
      wp.parent.add(initialPos, wp);
      MarkerInfoPanel.instance().refresh();
      MarkerInfoPanel.instance().repaint();
    }
  }
  
  public static final class MoveWaypointUp extends AbstractCommand
  {
    private final Waypoint wp;
    private final int initialPos;
    
    public MoveWaypointUp(Waypoint wp)
    {
      super();
      this.wp = wp;
      initialPos = wp.getListIndex();
    }
    

    public boolean shouldExec()
    {
      return initialPos > 0;
    }
    

    public void exec()
    {
      super.exec();
      wp.parent.remove(initialPos);
      wp.parent.add(initialPos - 1, wp);
      MarkerInfoPanel.instance().refresh();
      MarkerInfoPanel.instance().repaint();
    }
    

    public void undo()
    {
      super.undo();
      wp.parent.remove(initialPos - 1);
      wp.parent.add(initialPos, wp);
      MarkerInfoPanel.instance().refresh();
      MarkerInfoPanel.instance().repaint();
    }
  }
  
  public static final class MoveWaypointDown extends AbstractCommand
  {
    private final Waypoint wp;
    private final int initialPos;
    
    public MoveWaypointDown(Waypoint wp)
    {
      super();
      this.wp = wp;
      initialPos = wp.getListIndex();
    }
    

    public boolean shouldExec()
    {
      return initialPos < wp.parent.size() - 1;
    }
    

    public void exec()
    {
      super.exec();
      wp.parent.remove(initialPos);
      wp.parent.add(initialPos + 1, wp);
      MarkerInfoPanel.instance().refresh();
      MarkerInfoPanel.instance().repaint();
    }
    

    public void undo()
    {
      super.undo();
      wp.parent.remove(initialPos + 1);
      wp.parent.add(initialPos, wp);
      MarkerInfoPanel.instance().refresh();
      MarkerInfoPanel.instance().repaint();
    }
  }
}
