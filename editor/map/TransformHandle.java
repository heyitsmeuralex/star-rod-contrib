package editor.map;

import editor.Editor;
import editor.camera.Viewport;
import editor.map.shape.TransformMatrix;
import editor.render.EditorModel;
import editor.selection.AxisConstraint;
import editor.selection.PickRay;
import editor.selection.PickRay.PickHit;
import editor.selection.Selectable;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import util.IdentityHashSet;





















public class TransformHandle
  implements Selectable
{
  public transient boolean selected = false;
  
  private static final EditorModel TRANSLATE_MODEL = new EditorModel("transform.obj");
  private static final EditorModel RESCALE_MODEL = new EditorModel("transform_scale.obj");
  
  private static final int HALF_WIDTH = 4;
  private static final int LENGTH = 40;
  public transient MutablePoint origin;
  private transient BoundingBox aabb;
  public transient BoundingBox xbox;
  public transient BoundingBox ybox;
  public transient BoundingBox zbox;
  
  public TransformHandle(Vector3f vec)
  {
    origin = new MutablePoint(vec);
    
    aabb = new BoundingBox(new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(0.0F, 0.0F, 0.0F));
    








    xbox = new BoundingBox(new Vector3f(origin.getX() + 4, origin.getY() - 4, origin.getZ() - 4), new Vector3f(origin.getX() + 40, origin.getY() + 4, origin.getZ() + 4));
    








    ybox = new BoundingBox(new Vector3f(origin.getX() - 4, origin.getY() + 4, origin.getZ() - 4), new Vector3f(origin.getX() + 4, origin.getY() + 40, origin.getZ() + 4));
    








    zbox = new BoundingBox(new Vector3f(origin.getX() - 4, origin.getY() - 4, origin.getZ() + 4), new Vector3f(origin.getX() + 4, origin.getY() + 4, origin.getZ() + 40));
  }
  
  public void setOrigin(Vector3f vec)
  {
    origin.setPosition(vec);
    
    xbox.min.setPosition(origin
      .getX() + 4, origin
      .getY() - 4, origin
      .getZ() - 4);
    
    xbox.max.setPosition(origin
      .getX() + 40, origin
      .getY() + 4, origin
      .getZ() + 4);
    
    ybox.min.setPosition(origin
      .getX() - 4, origin
      .getY() + 4, origin
      .getZ() - 4);
    
    ybox.max.setPosition(origin
      .getX() + 4, origin
      .getY() + 40, origin
      .getZ() + 4);
    
    zbox.min.setPosition(origin
      .getX() - 4, origin
      .getY() - 4, origin
      .getZ() + 4);
    
    zbox.max.setPosition(origin
      .getX() + 4, origin
      .getY() + 4, origin
      .getZ() + 40);
  }
  








  public void render(float scaleFactor)
  {
    GL11.glPushMatrix();
    
    GL11.glPushAttrib(40951);
    GL11.glDisable(3553);
    GL11.glEnable(2896);
    GL11.glLineWidth(2.0F);
    GL11.glEnable(32823);
    GL11.glPolygonOffset(-2.5F, -2.5F);
    
    GL11.glTranslatef(origin.getX(), origin.getY(), origin.getZ());
    GL11.glScalef(scaleFactor, scaleFactor, scaleFactor);
    
    boolean useScaleModel = (Keyboard.isKeyDown(57)) || (Editor.rescaling);
    EditorModel currentModel = useScaleModel ? RESCALE_MODEL : TRANSLATE_MODEL;
    

    GL11.glPolygonMode(1032, 6913);
    GL11.glColor3f(0.1F, 0.1F, 0.1F);
    currentModel.render(false);
    

    GL11.glPolygonMode(1032, 6914);
    currentModel.render(true);
    
    GL11.glPopAttrib();
    GL11.glPopMatrix();
  }
  






  public PickRay.PickHit pick(PickRay clickRay, Viewport clickedViewport, float scaleFactor)
  {
    float length = 40.0F * scaleFactor;
    float halfWidth = 4.0F * scaleFactor;
    halfWidth = Math.max(halfWidth, 0.5F);
    

    aabb.min.setPosition(origin
      .getX() - halfWidth, origin
      .getY() - halfWidth, origin
      .getZ() - halfWidth);
    aabb.max.setPosition(origin
      .getX() + length, origin
      .getY() + length, origin
      .getZ() + length);
    if (!PickRay.intersects(clickRay, aabb)) {
      return new PickRay.PickHit(clickRay);
    }
    
    xbox.min.setPosition(
      Math.round(origin.getX() + halfWidth), 
      Math.round(origin.getY() - halfWidth), 
      Math.round(origin.getZ() - halfWidth));
    xbox.max.setPosition(
      Math.round(origin.getX() + length), 
      Math.round(origin.getY() + halfWidth), 
      Math.round(origin.getZ() + halfWidth));
    PickRay.PickHit hitX = PickRay.getIntersection(clickRay, xbox);
    
    ybox.min.setPosition(
      Math.round(origin.getX() - halfWidth), 
      Math.round(origin.getY() + halfWidth), 
      Math.round(origin.getZ() - halfWidth));
    ybox.max.setPosition(
      Math.round(origin.getX() + halfWidth), 
      Math.round(origin.getY() + length), 
      Math.round(origin.getZ() + halfWidth));
    PickRay.PickHit hitY = PickRay.getIntersection(clickRay, ybox);
    
    zbox.min.setPosition(
      Math.round(origin.getX() - halfWidth), 
      Math.round(origin.getY() - halfWidth), 
      Math.round(origin.getZ() + halfWidth));
    zbox.max.setPosition(
      Math.round(origin.getX() + halfWidth), 
      Math.round(origin.getY() + halfWidth), 
      Math.round(origin.getZ() + length));
    PickRay.PickHit hitZ = PickRay.getIntersection(clickRay, zbox);
    
    boolean ignoreX = false;
    boolean ignoreY = false;
    boolean ignoreZ = false;
    
    switch (1.$SwitchMap$editor$camera$ViewType[type.ordinal()]) {
    case 1: 
      ignoreZ = true; break;
    case 2:  ignoreX = true; break;
    case 3:  ignoreY = true; break;
    }
    
    

    boolean allowX = false;
    boolean allowY = false;
    boolean allowZ = false;
    boolean constrain = false;
    PickRay.PickHit closestHit = new PickRay.PickHit(clickRay);
    
    if ((dist < dist) && (dist < dist) && (!ignoreX))
    {
      constrain = true;
      allowX = true;
      closestHit = hitX;
    }
    else if ((dist < dist) && (dist < dist) && (!ignoreZ))
    {
      constrain = true;
      allowZ = true;
      closestHit = hitZ;
    }
    else if ((!hitY.missed()) && (!ignoreY))
    {
      constrain = true;
      allowY = true;
      closestHit = hitY;
    }
    
    if (constrain) {
      obj = new AxisConstraint(allowX, allowY, allowZ);
    }
    return closestHit;
  }
  
  public void setTransformDisplacement(Vector3f translation)
  {
    origin.setTempTranslation(translation);
    xbox.min.setTempTranslation(translation);
    xbox.max.setTempTranslation(translation);
    ybox.min.setTempTranslation(translation);
    ybox.max.setTempTranslation(translation);
    zbox.min.setTempTranslation(translation);
    zbox.max.setTempTranslation(translation);
  }
  


  public void addTo(BoundingBox aabb) {}
  


  public void recalculateAABB() {}
  

  public boolean allowRotation(Axis axis)
  {
    return false;
  }
  

  public void addPoints(IdentityHashSet<MutablePoint> positions)
  {
    positions.add(origin);
  }
  

  public void setSelected(boolean val)
  {
    selected = val;
  }
  

  public boolean isSelected()
  {
    return selected;
  }
  

  public boolean transforms()
  {
    return true;
  }
  

  public void startTransformation()
  {
    origin.startTransform();
    xbox.min.startTransform();
    xbox.max.startTransform();
    ybox.min.startTransform();
    ybox.max.startTransform();
    zbox.min.startTransform();
    zbox.max.startTransform();
  }
  

  public void endTransformation()
  {
    origin.endTransform();
    xbox.min.endTransform();
    xbox.max.endTransform();
    ybox.min.endTransform();
    ybox.max.endTransform();
    zbox.min.endTransform();
    zbox.max.endTransform();
  }
  

  public PointListBackup createTransformer(TransformMatrix m)
  {
    return new PointListBackup(origin.getBackup());
  }
}
