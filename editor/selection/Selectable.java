package editor.selection;

import editor.map.Axis;
import editor.map.BoundingBox;
import editor.map.MutableAngle;
import editor.map.MutablePoint;
import editor.map.ReversibleTransform;
import editor.map.shape.TransformMatrix;
import util.IdentityHashSet;

public abstract interface Selectable
{
  public abstract void addTo(BoundingBox paramBoundingBox);
  
  public abstract boolean transforms();
  
  public abstract void startTransformation();
  
  public abstract void endTransformation();
  
  public abstract void recalculateAABB();
  
  public abstract boolean allowRotation(Axis paramAxis);
  
  public abstract void addPoints(IdentityHashSet<MutablePoint> paramIdentityHashSet);
  
  public void addAngles(IdentityHashSet<MutableAngle> angles) {}
  
  public abstract ReversibleTransform createTransformer(TransformMatrix paramTransformMatrix);
  
  public abstract void setSelected(boolean paramBoolean);
  
  public abstract boolean isSelected();
}
