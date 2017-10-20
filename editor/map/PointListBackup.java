package editor.map;

import util.IdentityHashSet;

public class PointListBackup
  extends ReversibleTransform
{
  private IdentityHashSet<MutablePoint.PointBackup> backupList;
  
  public PointListBackup()
  {
    backupList = new IdentityHashSet();
  }
  
  public PointListBackup(MutablePoint.PointBackup pb)
  {
    backupList = new IdentityHashSet();
    backupList.add(pb);
  }
  
  public void addPoint(MutablePoint.PointBackup pb)
  {
    backupList.add(pb);
  }
  

  public void transform()
  {
    for (MutablePoint.PointBackup b : backupList) {
      pos.setPosition(newx, newy, newz);
    }
  }
  
  public void revert()
  {
    for (MutablePoint.PointBackup b : backupList) {
      pos.setPosition(oldx, oldy, oldz);
    }
  }
}
