package editor.selection;

public class AxisConstraint {
  public final boolean allowX;
  public final boolean allowY;
  public final boolean allowZ;
  
  public AxisConstraint(boolean x, boolean y, boolean z) {
    allowX = x;
    allowY = y;
    allowZ = z;
  }
}
