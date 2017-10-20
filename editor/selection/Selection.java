package editor.selection;

import editor.CommandManager;
import editor.Editor;
import editor.Grid;
import editor.camera.ViewType;
import editor.camera.Viewport;
import editor.commands.AbstractCommand;
import editor.commands.CommandBatch;
import editor.commands.TransformSelection;
import editor.map.Axis;
import editor.map.BoundingBox;
import editor.map.MutableAngle;
import editor.map.MutablePoint;
import editor.map.MutablePoint.PointBackup;
import editor.map.TransformHandle;
import editor.map.shape.TransformMatrix;
import editor.render.Renderer;
import editor.ui.SwingGUI;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.LinkedList;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import util.IdentityHashSet;
import util.Logger;
import util.Priority;





public final class Selection<T extends Selectable>
{
  public ArrayList<T> list;
  public BoundingBox aabb;
  private IdentityHashSet<MutablePoint> transformPoints;
  private IdentityHashSet<MutableAngle> transformAngles;
  private TransformHandle transformHandle;
  private TransformState transformState;
  public CommandBatch transformCommandBatch;
  private Vector3f transformOrigin;
  private Vector3f rotationStartPoint;
  private Vector3f rotationEndPoint;
  private double startAngle;
  private double endAngle;
  private Axis rotationAxis;
  private double currentAngle;
  private PickRay.PickHit handleHit;
  private AxisConstraint axisConstraint;
  private Vector3f accumulatedTranslation;
  private Vector3f currentTranslation;
  private Vector3f scaleStartPoint;
  private Vector3f originalSize;
  private Vector3f currentScale;
  private boolean uniformScaling;
  
  private static enum TransformState
  {
    IDLE,  TRANSLATE,  ROTATE,  SCALE,  SUBSTITUTE;
    













    private TransformState() {}
  }
  













  public Selection()
  {
    list = new ArrayList();
    aabb = new BoundingBox();
    transformHandle = null;
    transformState = TransformState.IDLE;
    transformCommandBatch = new CommandBatch();
  }
  
  public TransformState getTransformState()
  {
    return transformState;
  }
  
  public boolean transforming()
  {
    return transformState != TransformState.IDLE;
  }
  
  private void startTransformation()
  {
    transformOrigin = getCenter();
    
    aabb.min.startTransform();
    aabb.max.startTransform();
    transformHandle.startTransformation();
    transformPoints = new IdentityHashSet();
    transformAngles = new IdentityHashSet();
    
    for (T item : list) {
      item.startTransformation();
    }
  }
  
  private void endTransformation(TransformMatrix m, boolean changed) {
    if (!list.isEmpty())
    {
      if (changed)
      {
        transformCommandBatch.addCommand(new TransformSelection(this, m));
        Editor.commandManager.executeCommand(transformCommandBatch);
      } else {
        transformCommandBatch.undo();
      }
    }
    transformCommandBatch = new CommandBatch();
    
    aabb.min.endTransform();
    aabb.max.endTransform();
    transformHandle.endTransformation();
    transformPoints.clear();
    transformAngles.clear();
    
    for (T item : list) {
      item.recalculateAABB();
    }
    updateAABB();
    
    transformState = TransformState.IDLE;
  }
  






  public PickRay.PickHit pickTransformHandle(PickRay clickRay, Viewport clickedViewport)
  {
    axisConstraint = null;
    if (!isEmpty())
    {
      float scaleFactor = clickedViewport.getScaleFactor(transformHandle.origin
        .getX(), transformHandle.origin
        .getY(), transformHandle.origin
        .getZ());
      handleHit = transformHandle.pick(clickRay, clickedViewport, scaleFactor);
      axisConstraint = (handleHit.missed() ? null : (AxisConstraint)handleHit.obj);
      return handleHit;
    }
    return new PickRay.PickHit(clickRay);
  }
  




  public void applyMatrixTransformation(TransformMatrix m)
  {
    if (transformState != TransformState.IDLE) { return;
    }
    transformHandle.startTransformation();
    transformPoints = new IdentityHashSet();
    transformAngles = new IdentityHashSet();
    transformPoints.add(aabb.min);
    transformPoints.add(aabb.max);
    
    for (T item : list)
    {
      item.startTransformation();
      item.addPoints(transformPoints);
      item.addAngles(transformAngles);
    }
    
    transformState = TransformState.SUBSTITUTE;
    
    for (MutablePoint p : transformPoints) {
      m.applyTransform(p);
    }
    for (MutableAngle a : transformAngles) {
      m.applyTransform(a);
    }
    endTransformation(m, true);
  }
  





  public void startDirectTransformation()
  {
    if (transformState != TransformState.IDLE)
    {
      Logger.log("Cannot initiate transformation when selection is already being transformed!", Priority.WARNING);
      return;
    }
    
    aabb.min.startTransform();
    aabb.max.startTransform();
    transformHandle.startTransformation();
    transformPoints = new IdentityHashSet();
    transformAngles = new IdentityHashSet();
    
    for (T item : list)
    {
      item.startTransformation();
      item.addPoints(transformPoints);
      item.addAngles(transformAngles);
    }
    
    transformState = TransformState.SUBSTITUTE;
  }
  





  public void endDirectTransformation()
  {
    Editor.commandManager.executeCommand(new DirectTransformSelection(this));
    aabb.min.endTransform();
    aabb.max.endTransform();
    transformHandle.endTransformation();
    transformPoints.clear();
    transformAngles.clear();
    
    transformState = TransformState.IDLE;
  }
  




  public void nudgeAlong(Vector3f direction)
  {
    startTranslation();
    direction.normalise();
    int snapz;
    if ((Editor.gridEnabled) && (gridpower > 0) && (Editor.snapTranslation))
    {
      int snapx = 0;
      int snapy = 0;
      snapz = 0;
      
      int spacing = Editor.grid.getSpacing();
      
      if (x < 0.0F)
      {
        snapx = spacing * ((int)Math.ceil(aabb.min.getBaseX() / spacing) - 1);
        snapx -= aabb.min.getBaseX();
      }
      else if (x > 0.0F)
      {
        snapx = spacing * ((int)Math.floor(aabb.max.getBaseX() / spacing) + 1);
        snapx -= aabb.max.getBaseX();
      }
      
      if (y < 0.0F)
      {
        snapy = spacing * ((int)Math.ceil(aabb.min.getBaseY() / spacing) - 1);
        snapy -= aabb.min.getBaseY();
      }
      else if (y > 0.0F)
      {
        snapy = spacing * ((int)Math.floor(aabb.max.getBaseY() / spacing) + 1);
        snapy -= aabb.max.getBaseY();
      }
      
      if (z < 0.0F)
      {
        snapz = spacing * ((int)Math.ceil(aabb.min.getBaseZ() / spacing) - 1);
        snapz -= aabb.min.getBaseZ();
      }
      else if (z > 0.0F)
      {
        snapz = spacing * ((int)Math.floor(aabb.max.getBaseZ() / spacing) + 1);
        snapz -= aabb.max.getBaseZ();
      }
      
      if (snapx != 0) currentTranslation.x = snapx;
      if (snapy != 0) currentTranslation.y = snapy;
      if (snapz != 0) currentTranslation.z = snapz;
    }
    else {
      currentTranslation.x = x;
      currentTranslation.y = y;
      currentTranslation.z = z;
      System.out.println(currentTranslation);
    }
    

    long currentFrame = Editor.getFrame();
    for (MutablePoint p : transformPoints)
    {
      if (lastModified < currentFrame)
      {
        p.setTempTranslation(currentTranslation);
        lastModified = currentFrame;
      }
    }
    aabb.min.setTempTranslation(currentTranslation);
    aabb.max.setTempTranslation(currentTranslation);
    
    endTransform();
  }
  
  public void startTranslation()
  {
    if (transformState != TransformState.IDLE)
    {
      Logger.log("Cannot initiate transformation when selection is already being transformed!", Priority.WARNING);
      return;
    }
    
    startTransformation();
    
    for (T item : list) {
      item.addPoints(transformPoints);
    }
    if (transformPoints.size() == 0)
    {
      endTransformation(null, false);
      return;
    }
    
    accumulatedTranslation = new Vector3f(0.0F, 0.0F, 0.0F);
    currentTranslation = new Vector3f(0.0F, 0.0F, 0.0F);
    
    transformState = TransformState.TRANSLATE;
  }
  
  public void updateTranslation(Vector3f displacement)
  {
    if (transformState != TransformState.TRANSLATE) {
      return;
    }
    
    if (axisConstraint != null)
    {
      if (axisConstraint.allowX) accumulatedTranslation.x += x;
      if (axisConstraint.allowY) accumulatedTranslation.y += y;
      if (axisConstraint.allowZ) accumulatedTranslation.z += z;
    } else {
      accumulatedTranslation.x += x;
      accumulatedTranslation.y += y;
      accumulatedTranslation.z += z;
    }
    
    int snapz;
    if ((Editor.gridEnabled) && (gridpower > 0) && (Editor.snapTranslation))
    {
      int snapx = 0;
      int snapy = 0;
      snapz = 0;
      
      int spacing = Editor.grid.getSpacing();
      
      if (x < 0.0F)
      {
        snapx += (int)accumulatedTranslation.x + aabb.min.getBaseX();
        snapx = spacing * (int)Math.round(snapx / spacing);
        snapx -= aabb.min.getBaseX();
      }
      else if (x > 0.0F)
      {
        snapx += (int)accumulatedTranslation.x + aabb.max.getBaseX();
        snapx = spacing * (int)Math.round(snapx / spacing);
        snapx -= aabb.max.getBaseX();
      }
      
      if (y < 0.0F)
      {
        snapy += (int)accumulatedTranslation.y + aabb.min.getBaseY();
        snapy = spacing * (int)Math.round(snapy / spacing);
        snapy -= aabb.min.getBaseY();
      }
      else if (y > 0.0F)
      {
        snapy += (int)accumulatedTranslation.y + aabb.max.getBaseY();
        snapy = spacing * (int)Math.round(snapy / spacing);
        snapy -= aabb.max.getBaseY();
      }
      
      if (z < 0.0F)
      {
        snapz += (int)accumulatedTranslation.z + aabb.min.getBaseZ();
        snapz = spacing * (int)Math.round(snapz / spacing);
        snapz -= aabb.min.getBaseZ();
      }
      else if (z > 0.0F)
      {
        snapz += (int)accumulatedTranslation.z + aabb.max.getBaseZ();
        snapz = spacing * (int)Math.round(snapz / spacing);
        snapz -= aabb.max.getBaseZ();
      }
      
      if (snapx != 0) currentTranslation.x = snapx;
      if (snapy != 0) currentTranslation.y = snapy;
      if (snapz != 0) currentTranslation.z = snapz;
    }
    else {
      currentTranslation.x = accumulatedTranslation.x;
      currentTranslation.y = accumulatedTranslation.y;
      currentTranslation.z = accumulatedTranslation.z;
    }
    

    if (axisConstraint != null)
    {
      if (!axisConstraint.allowX) currentTranslation.x = 0.0F;
      if (!axisConstraint.allowY) currentTranslation.y = 0.0F;
      if (!axisConstraint.allowZ) { currentTranslation.z = 0.0F;
      }
    }
    
    long currentFrame = Editor.getFrame();
    for (MutablePoint p : transformPoints)
    {
      if (lastModified < currentFrame)
      {
        p.setTempTranslation(currentTranslation);
        lastModified = currentFrame;
      }
    }
    aabb.min.setTempTranslation(currentTranslation);
    aabb.max.setTempTranslation(currentTranslation);
    transformHandle.setTransformDisplacement(currentTranslation);
    
    Editor.gui.setTransformInfo("Translate: %d, %d, %d", new Object[] {
      Integer.valueOf((int)currentTranslation.x), Integer.valueOf((int)currentTranslation.y), Integer.valueOf((int)currentTranslation.z) });
  }
  
  public void startScale(Vector3f clickPoint, boolean uniform)
  {
    if (transformState != TransformState.IDLE)
    {
      Logger.log("Cannot initiate transformation when selection is already being transformed!", Priority.WARNING);
      return;
    }
    
    uniformScaling = uniform;
    startTransformation();
    
    for (T item : list) {
      item.addPoints(transformPoints);
    }
    if ((transformPoints.size() == 0) || (clickPoint == null))
    {
      endTransformation(null, false);
      return;
    }
    
    scaleStartPoint = new Vector3f(clickPoint);
    if (axisConstraint != null)
    {
      if (!axisConstraint.allowX) scaleStartPoint.x = transformOrigin.x;
      if (!axisConstraint.allowY) scaleStartPoint.y = transformOrigin.y;
      if (!axisConstraint.allowZ) { scaleStartPoint.z = transformOrigin.z;
      }
    }
    int sizeX = aabb.max.getX() - aabb.min.getX();
    int sizeY = aabb.max.getY() - aabb.min.getY();
    int sizeZ = aabb.max.getZ() - aabb.min.getZ();
    originalSize = new Vector3f(sizeX, sizeY, sizeZ);
    currentScale = new Vector3f(1.0F, 1.0F, 1.0F);
    
    accumulatedTranslation = new Vector3f(0.0F, 0.0F, 0.0F);
    currentTranslation = new Vector3f(0.0F, 0.0F, 0.0F);
    
    transformState = TransformState.SCALE;
  }
  
  public void updateScale(Vector3f displacement)
  {
    if (transformState != TransformState.SCALE) {
      return;
    }
    
    if (axisConstraint != null)
    {
      if (axisConstraint.allowX) accumulatedTranslation.x += x;
      if (axisConstraint.allowY) accumulatedTranslation.y += y;
      if (axisConstraint.allowZ) accumulatedTranslation.z += z;
    } else {
      accumulatedTranslation.x += x;
      accumulatedTranslation.y += y;
      accumulatedTranslation.z += z;
    }
    
    currentTranslation.x = accumulatedTranslation.x;
    currentTranslation.y = accumulatedTranslation.y;
    currentTranslation.z = accumulatedTranslation.z;
    
    Vector3f scaleEndPoint = new Vector3f();
    Vector3f.add(scaleStartPoint, currentTranslation, scaleEndPoint);
    
    Vector3f dstart = new Vector3f();
    Vector3f dend = new Vector3f();
    Vector3f.sub(scaleStartPoint, transformOrigin, dstart);
    Vector3f.sub(scaleEndPoint, transformOrigin, dend);
    
    if ((Math.abs(scaleStartPoint.x) > 1.0E-5D) && (Math.abs(originalSize.x) > 1.0E-5D)) {
      currentScale.x = (x / x);
    } else {
      currentScale.x = 1.0F;
    }
    if ((Math.abs(scaleStartPoint.y) > 1.0E-5D) && (Math.abs(originalSize.y) > 1.0E-5D)) {
      currentScale.y = (y / y);
    } else {
      currentScale.y = 1.0F;
    }
    if ((Math.abs(scaleStartPoint.z) > 1.0E-5D) && (Math.abs(originalSize.z) > 1.0E-5D)) {
      currentScale.z = (z / z);
    } else {
      currentScale.z = 1.0F;
    }
    boolean allowX = ((axisConstraint == null) || (axisConstraint.allowX)) && (Math.abs(originalSize.x) > 1.0E-5D);
    boolean allowY = ((axisConstraint == null) || (axisConstraint.allowY)) && (Math.abs(originalSize.y) > 1.0E-5D);
    boolean allowZ = ((axisConstraint == null) || (axisConstraint.allowZ)) && (Math.abs(originalSize.z) > 1.0E-5D);
    float sgn;
    if (uniformScaling)
    {
      float rs = dstart.length();
      float re = dend.length();
      sgn = Math.signum(Vector3f.dot(dstart, dend));
      float uniformScale = sgn * (re / rs);
      
      currentScale.x = uniformScale;
      currentScale.y = uniformScale;
      currentScale.z = uniformScale;

    }
    else if (axisConstraint != null)
    {
      if (!allowX) currentScale.x = 1.0F;
      if (!allowY) currentScale.y = 1.0F;
      if (!allowZ) { currentScale.z = 1.0F;
      }
    }
    if (Editor.snapScale)
    {
      if (Editor.snapScaleToGrid)
      {
        float dg = Editor.grid.getSpacing();
        double gx = dg / originalSize.x;
        double gy = dg / originalSize.y;
        double gz = dg / originalSize.z;
        
        if ((allowX) && (Math.abs(originalSize.x) > 1.0E-5D)) {
          currentScale.x = ((float)(gx * Math.round(currentScale.x / gx)));
        } else {
          currentScale.x = 1.0F;
        }
        if ((allowY) && (Math.abs(originalSize.y) > 1.0E-5D)) {
          currentScale.y = ((float)(gy * Math.round(currentScale.y / gy)));
        } else {
          currentScale.y = 1.0F;
        }
        if ((allowZ) && (Math.abs(originalSize.z) > 1.0E-5D)) {
          currentScale.z = ((float)(gz * Math.round(currentScale.z / gz)));
        } else {
          currentScale.z = 1.0F;
        }
      }
      else {
        if (allowX) currentScale.x = (Math.round(currentScale.x * 10.0F) / 10.0F);
        if (allowY) currentScale.y = (Math.round(currentScale.y * 10.0F) / 10.0F);
        if (allowZ) { currentScale.z = (Math.round(currentScale.z * 10.0F) / 10.0F);
        }
      }
    }
    long currentFrame = Editor.getFrame();
    for (MutablePoint p : transformPoints)
    {
      if (lastModified < currentFrame)
      {
        p.setTempScale(transformOrigin, currentScale);
        lastModified = currentFrame;
      }
    }
    aabb.min.setTempScale(transformOrigin, currentScale);
    aabb.max.setTempScale(transformOrigin, currentScale);
    
    Editor.gui.setTransformInfo("Scale: %s, %s, %s", new Object[] { Float.valueOf(currentScale.x), Float.valueOf(currentScale.y), Float.valueOf(currentScale.z) });
  }
  
  public void startRotation(Axis axis, Vector3f clickPoint)
  {
    if (transformState != TransformState.IDLE)
    {
      Logger.log("Cannot initiate transformation when selection is already being transformed!", Priority.WARNING);
      return;
    }
    
    startTransformation();
    
    for (T item : list)
    {
      if (item.allowRotation(axis)) {
        item.addPoints(transformPoints);
      }
      item.addAngles(transformAngles);
    }
    
    if ((transformPoints.size() == 0) && (transformAngles.size() == 0))
    {
      endTransformation(null, false);
      return;
    }
    
    rotationAxis = axis;
    rotationStartPoint = new Vector3f(clickPoint);
    

    switch (1.$SwitchMap$editor$map$Axis[rotationAxis.ordinal()]) {
    case 1: 
      rotationStartPoint.x = transformOrigin.x; break;
    case 2:  rotationStartPoint.y = transformOrigin.y; break;
    case 3:  rotationStartPoint.z = transformOrigin.z;
    }
    
    rotationEndPoint = new Vector3f(rotationStartPoint);
    startAngle = getAngle(rotationEndPoint);
    currentAngle = 0.0D;
    
    transformState = TransformState.ROTATE;
  }
  
  public void updateRotation(Vector3f vec)
  {
    if (transformState != TransformState.ROTATE) {
      return;
    }
    rotationEndPoint.x = x;
    rotationEndPoint.y = y;
    rotationEndPoint.z = z;
    

    switch (1.$SwitchMap$editor$map$Axis[rotationAxis.ordinal()]) {
    case 1: 
      rotationEndPoint.x = transformOrigin.x; break;
    case 2:  rotationEndPoint.y = transformOrigin.y; break;
    case 3:  rotationEndPoint.z = transformOrigin.z;
    }
    
    
    endAngle = getAngle(rotationEndPoint);
    double angle = endAngle - startAngle;
    if (Editor.snapRotation) {
      currentAngle = ((float)Math.round(angle / Editor.rotationSnap) * Editor.rotationSnap);
    } else {
      currentAngle = angle;
    }
    long currentFrame = Editor.getFrame();
    
    for (MutablePoint p : transformPoints) {
      if (lastModified < currentFrame)
      {
        rotatePosition(rotationAxis, currentAngle, p);
        lastModified = currentFrame;
      }
    }
    for (MutableAngle a : transformAngles) {
      if (lastModified < currentFrame)
      {
        rotateAngle(rotationAxis, currentAngle, a);
        lastModified = currentFrame;
      }
    }
    Editor.gui.setTransformInfo("Rotate %s: %d degrees", new Object[] { rotationAxis, Integer.valueOf((int)currentAngle) });
  }
  
  private void rotateAngle(Axis axis, double angle, MutableAngle a)
  {
    if (axis != axis) {
      return;
    }
    

    if (clockwise) {
      a.setTempAngle(a.getBaseAngle() - angle);
    } else {
      a.setTempAngle(a.getBaseAngle() + angle);
    }
  }
  


  private void rotatePosition(Axis axis, double angle, MutablePoint p)
  {
    Vector3f relative = new Vector3f(p.getBaseX() - transformOrigin.x, p.getBaseY() - transformOrigin.y, p.getBaseZ() - transformOrigin.z);
    
    angle = Math.toRadians(angle);
    double dx = 0.0D;
    double dy = 0.0D;
    double dz = 0.0D;
    
    switch (1.$SwitchMap$editor$map$Axis[axis.ordinal()])
    {
    case 1: 
      dy = y * Math.cos(angle) - z * Math.sin(angle);
      dz = y * Math.sin(angle) + z * Math.cos(angle);
      p.setTempPosition(p
        .getX(), (int)(transformOrigin.y + dy), (int)(transformOrigin.z + dz));
      

      break;
    case 2: 
      angle = -angle;
      dx = x * Math.cos(angle) - z * Math.sin(angle);
      dz = x * Math.sin(angle) + z * Math.cos(angle);
      p.setTempPosition((int)(transformOrigin.x + dx), p
      
        .getY(), (int)(transformOrigin.z + dz));
      
      break;
    case 3: 
      dx = x * Math.cos(angle) - y * Math.sin(angle);
      dy = x * Math.sin(angle) + y * Math.cos(angle);
      p.setTempPosition((int)(transformOrigin.x + dx), (int)(transformOrigin.y + dy), p
      

        .getZ());
    }
    
  }
  
  private float getAngle(Vector3f vec)
  {
    Vector3f relative = new Vector3f(x - transformOrigin.x, y - transformOrigin.y, z - transformOrigin.z);
    




    switch (1.$SwitchMap$editor$map$Axis[rotationAxis.ordinal()]) {
    case 1: 
      x = transformOrigin.x; break;
    case 2:  y = transformOrigin.y; break;
    case 3:  z = transformOrigin.z;
    }
    
    double angle = 0.0D;
    
    switch (1.$SwitchMap$editor$map$Axis[rotationAxis.ordinal()])
    {
    case 1: 
      angle = (float)Math.atan2(y, -z);
      break;
    case 2: 
      angle = (float)Math.atan2(-z, x);
      break;
    case 3: 
      angle = (float)Math.atan2(y, x);
    }
    
    
    return (float)Math.toDegrees(angle);
  }
  
  public void endTransform()
  {
    if (transformState == TransformState.TRANSLATE)
    {
      boolean changed = (currentTranslation.x != 0.0F) || (currentTranslation.y != 0.0F) || (currentTranslation.z != 0.0F);
      
      TransformMatrix tx = new TransformMatrix();
      tx.setTranslation(currentTranslation.x, currentTranslation.y, currentTranslation.z);
      
      endTransformation(tx, changed);
      axisConstraint = null;
    }
    else if (transformState == TransformState.ROTATE)
    {
      boolean changed = Math.abs(currentAngle) > 1.0E-12D;
      
      TransformMatrix r = new TransformMatrix();
      r.setRotation(rotationAxis, currentAngle);
      
      TransformMatrix tx = new TransformMatrix();
      tx.setTranslation(-transformOrigin.x, -transformOrigin.y, -transformOrigin.z);
      tx.concat(r);
      tx.translate(transformOrigin);
      
      endTransformation(tx, changed);
    }
    else if (transformState == TransformState.SCALE)
    {
      boolean changedX = Math.abs(currentScale.x - 1.0F) > 1.0E-5D;
      boolean changedY = Math.abs(currentScale.y - 1.0F) > 1.0E-5D;
      boolean changedZ = Math.abs(currentScale.z - 1.0F) > 1.0E-5D;
      boolean changed = (changedX) || (changedY) || (changedZ);
      
      TransformMatrix tx = new TransformMatrix();
      tx.setScale(currentScale.x, currentScale.y, currentScale.z);
      
      endTransformation(tx, changed);
      axisConstraint = null;
    }
    
    Editor.gui.setTransformInfo("", new Object[0]);
  }
  
  public void addWithoutSelecting(T item)
  {
    list.add(item);
    item.addTo(aabb);
    centerTransformHandle();
  }
  




  public void addAndSelect(T item)
  {
    if (item.isSelected()) {
      return;
    }
    list.add(item);
    item.setSelected(true);
    item.addTo(aabb);
    centerTransformHandle();
  }
  




  public void addAndSelect(Iterable<T> items)
  {
    int count = 0;
    
    for (T item : items)
    {
      if (!item.isSelected())
      {

        list.add(item);
        item.setSelected(true);
        item.addTo(aabb);
        count++;
      }
    }
    if (count > 0) {
      centerTransformHandle();
    }
  }
  



  public void removeAndDeselect(T item)
  {
    if (!item.isSelected()) {
      return;
    }
    list.remove(item);
    item.setSelected(false);
    recalculateAABB();
    
    if (list.isEmpty()) {
      transformHandle = null;
    } else {
      centerTransformHandle();
    }
  }
  



  public void removeAndDeselect(Iterable<T> items)
  {
    for (T item : items)
    {
      if (item.isSelected())
      {

        list.remove(item);
        item.setSelected(false);
      } }
    recalculateAABB();
    
    if (list.isEmpty()) {
      transformHandle = null;
    } else {
      centerTransformHandle();
    }
  }
  


  public void clear()
  {
    for (T item : list)
      item.setSelected(false);
    list.clear();
    recalculateAABB();
    transformHandle = null;
  }
  



  public boolean isEmpty()
  {
    return list.isEmpty();
  }
  



  public T getMostRecent()
  {
    if (list.isEmpty()) {
      return null;
    }
    return (Selectable)list.get(list.size() - 1);
  }
  

  public void updateAABB()
  {
    recalculateAABB();
    
    if (transformHandle != null) {
      centerTransformHandle();
    }
  }
  


  private void recalculateAABB()
  {
    aabb.clear();
    for (T item : list) {
      item.addTo(aabb);
    }
  }
  



  public Vector3f getCenter()
  {
    int centerx = (aabb.max.getX() + aabb.min.getX()) / 2;
    int centery = (aabb.max.getY() + aabb.min.getY()) / 2;
    int centerz = (aabb.max.getZ() + aabb.min.getZ()) / 2;
    
    return new Vector3f(centerx, centery, centerz);
  }
  





  private void centerTransformHandle()
  {
    if (transformHandle == null) {
      transformHandle = new TransformHandle(getCenter());
    } else {
      transformHandle.setOrigin(getCenter());
    }
  }
  
  public boolean render(Viewport view) {
    if (transformHandle != null)
    {
      float scaleFactor = view.getScaleFactor(transformHandle.origin
        .getX(), transformHandle.origin
        .getY(), transformHandle.origin
        .getZ());
      renderAABB(type, scaleFactor);
      renderTransform(view);
      transformHandle.render(scaleFactor);
      return true;
    }
    return false;
  }
  
  public void renderTransform(Viewport view)
  {
    switch (transformState)
    {
    case TRANSLATE: 
      renderTranslation();
      break;
    
    case ROTATE: 
      if (type != ViewType.PERSPECTIVE) {
        renderRotation();
      }
      
      break;
    }
    
  }
  
  private void renderTranslation()
  {
    GL11.glPushAttrib(4081);
    GL11.glDisable(2929);
    GL11.glDisable(3553);
    GL11.glLineWidth(2.0F);
    GL11.glPointSize(8.0F);
    
    GL11.glColor3f(1.0F, 1.0F, 1.0F);
    
    GL11.glBegin(0);
    GL11.glVertex3f(transformOrigin.x, transformOrigin.y, transformOrigin.z);
    GL11.glVertex3f(transformOrigin.x + currentTranslation.x, transformOrigin.y + currentTranslation.y, transformOrigin.z + currentTranslation.z);
    


    GL11.glEnd();
    
    GL11.glBegin(1);
    GL11.glVertex3f(transformOrigin.x, transformOrigin.y, transformOrigin.z);
    GL11.glVertex3f(transformOrigin.x + currentTranslation.x, transformOrigin.y + currentTranslation.y, transformOrigin.z + currentTranslation.z);
    


    GL11.glEnd();
    
    GL11.glPopAttrib();
  }
  
  private void renderRotation()
  {
    GL11.glPushAttrib(4081);
    GL11.glDisable(2929);
    GL11.glDisable(3553);
    GL11.glLineWidth(2.0F);
    GL11.glPointSize(8.0F);
    
    GL11.glColor3f(1.0F, 1.0F, 1.0F);
    
    GL11.glBegin(1);
    GL11.glVertex3f(transformOrigin.x, transformOrigin.y, transformOrigin.z);
    GL11.glVertex3f(rotationStartPoint.x, rotationStartPoint.y, rotationStartPoint.z);
    GL11.glVertex3f(transformOrigin.x, transformOrigin.y, transformOrigin.z);
    GL11.glVertex3f(rotationEndPoint.x, rotationEndPoint.y, rotationEndPoint.z);
    GL11.glEnd();
    
    GL11.glBegin(0);
    GL11.glVertex3f(transformOrigin.x, transformOrigin.y, transformOrigin.z);
    GL11.glVertex3f(rotationStartPoint.x, rotationStartPoint.y, rotationStartPoint.z);
    GL11.glVertex3f(rotationEndPoint.x, rotationEndPoint.y, rotationEndPoint.z);
    GL11.glEnd();
    
    GL11.glPopAttrib();
  }
  
  public void renderAABB(ViewType type, float scaleFactor)
  {
    if ((isEmpty()) || (aabb.isEmpty())) {
      return;
    }
    GL11.glPushAttrib(2933);
    if (type != ViewType.PERSPECTIVE)
      GL11.glDisable(2929);
    GL11.glEnable(2852);
    GL11.glLineStipple(10 / (20 + (int)scaleFactor), Renderer.stipplePattern);
    
    aabb.render(1.0F, Renderer.color, Renderer.color, 3.0F);
    
    GL11.glPointSize(5.0F);
    GL11.glBegin(0);
    GL11.glColor3f(1.0F, Renderer.color, Renderer.color);
    
    if (type != ViewType.PERSPECTIVE)
    {
      GL11.glVertex3f(aabb.max.getX(), aabb.max.getY(), aabb.max.getZ());
      GL11.glVertex3f(aabb.max.getX(), aabb.max.getY(), aabb.min.getZ());
      GL11.glVertex3f(aabb.max.getX(), aabb.min.getY(), aabb.max.getZ());
      GL11.glVertex3f(aabb.max.getX(), aabb.min.getY(), aabb.min.getZ());
      GL11.glVertex3f(aabb.min.getX(), aabb.max.getY(), aabb.max.getZ());
      GL11.glVertex3f(aabb.min.getX(), aabb.max.getY(), aabb.min.getZ());
      GL11.glVertex3f(aabb.min.getX(), aabb.min.getY(), aabb.max.getZ());
      GL11.glVertex3f(aabb.min.getX(), aabb.min.getY(), aabb.min.getZ());
    }
    GL11.glEnd();
    
    GL11.glPopAttrib();
  }
  
  private static final class DirectTransformSelection<T extends Selectable> extends AbstractCommand
  {
    private LinkedList<MutablePoint.PointBackup> backupList;
    private Selection<T> selection;
    
    public DirectTransformSelection(Selection<T> selection)
    {
      super();
      backupList = new LinkedList();
      this.selection = selection;
      
      IdentityHashSet<MutablePoint> pointList = new IdentityHashSet();
      
      for (T item : list)
      {
        item.addPoints(pointList);
        item.endTransformation();
      }
      
      transformHandle.addPoints(pointList);
      
      for (MutablePoint p : pointList)
      {
        backupList.add(p.getBackup());
      }
    }
    

    public void exec()
    {
      super.exec();
      
      for (MutablePoint.PointBackup b : backupList) {
        pos.setPosition(newx, newy, newz);
      }
      selection.updateAABB();
    }
    

    public void undo()
    {
      super.undo();
      
      for (MutablePoint.PointBackup b : backupList) {
        pos.setPosition(oldx, oldy, oldz);
      }
      selection.updateAABB();
    }
  }
}
