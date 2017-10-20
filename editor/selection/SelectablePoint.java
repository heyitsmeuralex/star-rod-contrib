package editor.selection;

import editor.commands.AbstractCommand;
import editor.map.Axis;
import editor.map.BoundingBox;
import editor.map.MutablePoint;
import editor.map.PointListBackup;
import editor.map.ReversibleTransform;
import editor.map.shape.TransformMatrix;
import org.lwjgl.util.vector.Vector3f;
import util.IdentityHashSet;





public class SelectablePoint
  implements Selectable
{
  public final MutablePoint point;
  private boolean selected;
  public final float sizeScale;
  
  public SelectablePoint(float sizeScale)
  {
    this(new MutablePoint(0, 0, 0), sizeScale);
  }
  
  public SelectablePoint(MutablePoint point, float sizeScale)
  {
    this.sizeScale = sizeScale;
    this.point = point;
    selected = false;
  }
  
  public Vector3f getPosition()
  {
    return new Vector3f(point.getX(), point.getY(), point.getZ());
  }
  
  public void set(int axis, int val)
  {
    point.set(axis, val);
  }
  
  public void setX(int val)
  {
    point.setX(val);
  }
  
  public void setY(int val)
  {
    point.setY(val);
  }
  
  public void setZ(int val)
  {
    point.setZ(val);
  }
  
  public int get(int axis)
  {
    return point.get(axis);
  }
  
  public int getX()
  {
    return point.getX();
  }
  
  public int getY()
  {
    return point.getY();
  }
  
  public int getZ()
  {
    return point.getZ();
  }
  

  public void addTo(BoundingBox selectionAABB)
  {
    selectionAABB.encompass(point.getX(), point.getY(), point.getZ());
  }
  

  public boolean transforms()
  {
    return true;
  }
  

  public void startTransformation()
  {
    point.startTransform();
  }
  

  public void endTransformation()
  {
    point.endTransform();
  }
  



  public void recalculateAABB() {}
  


  public boolean allowRotation(Axis axis)
  {
    return true;
  }
  

  public void addPoints(IdentityHashSet<MutablePoint> positions)
  {
    positions.add(point);
  }
  

  public ReversibleTransform createTransformer(TransformMatrix m)
  {
    return new PointListBackup(point.getBackup());
  }
  

  public void setSelected(boolean val)
  {
    selected = val;
  }
  

  public boolean isSelected()
  {
    return selected;
  }
  

  public int hashCode()
  {
    return point.hashCode();
  }
  

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SelectablePoint other = (SelectablePoint)obj;
    return point.equals(point);
  }
  
  public static class SetPointPosition
    extends AbstractCommand
  {
    private final int oldValue;
    private final int newValue;
    private final SelectablePoint point;
    private final int axis;
    
    public SetPointPosition(String name, SelectablePoint point, int axis, int val)
    {
      super();
      
      this.point = point;
      this.axis = axis;
      
      switch (axis) {
      case 0: 
        oldValue = point.getX(); break;
      case 1:  oldValue = point.getY(); break;
      case 2:  oldValue = point.getZ(); break;
      default: 
        throw new IllegalStateException("Invalid axis value " + axis);
      }
      
      newValue = val;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      
      switch (axis) {
      case 0: 
        point.setX(newValue); break;
      case 1:  point.setY(newValue); break;
      case 2:  point.setZ(newValue);
      }
      
    }
    
    public void undo()
    {
      super.undo();
      
      switch (axis) {
      case 0: 
        point.setX(oldValue); break;
      case 1:  point.setY(oldValue); break;
      case 2:  point.setZ(oldValue);
      }
    }
  }
}
