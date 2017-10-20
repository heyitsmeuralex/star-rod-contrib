package editor.map.marker;

import editor.commands.AbstractCommand;
import editor.map.MutablePoint;
import editor.ui.info.MarkerInfoPanel;
import java.io.ObjectOutput;

public class NpcMovementSettings implements java.io.Externalizable
{
  private static final long serialVersionUID = 1L;
  public MoveType type;
  public boolean flying;
  public MutablePoint wanderPoint;
  public boolean useWanderCircle;
  public int wanderX;
  public int wanderZ;
  public int wanderR;
  public boolean overrideMovementSpeed;
  
  public static enum MoveType
  {
    Stationary, 
    Wander, 
    Patrol;
    

    private MoveType() {}
  }
  

  public float movementSpeedOverride;
  
  public MutablePoint detectPoint;
  
  public boolean useDetectCircle;
  
  public int detectX;
  
  public int detectZ;
  
  public int detectR;
  
  public transient editor.selection.SelectablePoint detectCenter;
  
  public transient editor.selection.SelectablePoint wanderCenter;
  
  public java.util.List<Waypoint> patrolPath;
  
  public NpcMovementSettings() {}
  

  public void readExternal(java.io.ObjectInput in)
    throws java.io.IOException, ClassNotFoundException
  {
    in.readInt();
    
    type = ((MoveType)in.readObject());
    flying = in.readBoolean();
    
    wanderPoint = ((MutablePoint)in.readObject());
    wanderCenter = new editor.selection.SelectablePoint(wanderPoint, 2.0F);
    useWanderCircle = in.readBoolean();
    wanderX = in.readInt();
    wanderZ = in.readInt();
    wanderR = in.readInt();
    
    overrideMovementSpeed = in.readBoolean();
    movementSpeedOverride = in.readFloat();
    
    detectPoint = ((MutablePoint)in.readObject());
    detectCenter = new editor.selection.SelectablePoint(detectPoint, 2.0F);
    useDetectCircle = in.readBoolean();
    detectX = in.readInt();
    detectZ = in.readInt();
    detectR = in.readInt();
    
    int numPatrolPoints = in.readInt();
    patrolPath = new java.util.ArrayList(10);
    for (int i = 0; i < numPatrolPoints; i++)
    {
      MutablePoint point = (MutablePoint)in.readObject();
      Waypoint.addToList(patrolPath, point.getX(), point.getY(), point.getZ());
    }
  }
  
  public void writeExternal(ObjectOutput out)
    throws java.io.IOException
  {
    out.writeInt(0);
    
    out.writeObject(type);
    out.writeBoolean(flying);
    
    out.writeObject(wanderPoint);
    out.writeBoolean(useWanderCircle);
    out.writeInt(wanderX);
    out.writeInt(wanderZ);
    out.writeInt(wanderR);
    
    out.writeBoolean(overrideMovementSpeed);
    out.writeFloat(movementSpeedOverride);
    
    out.writeObject(detectPoint);
    out.writeBoolean(useDetectCircle);
    out.writeInt(detectX);
    out.writeInt(detectZ);
    out.writeInt(detectR);
    
    out.writeInt(patrolPath.size());
    for (Waypoint wp : patrolPath) {
      out.writeObject(point);
    }
  }
  
  public NpcMovementSettings(Marker npcMarker) {
    type = MoveType.Stationary;
    



    wanderPoint = new MutablePoint(position.getX(), position.getY(), position.getZ());
    



    detectPoint = new MutablePoint(position.getX(), position.getY(), position.getZ());
    
    wanderCenter = new editor.selection.SelectablePoint(wanderPoint, 2.0F);
    detectCenter = new editor.selection.SelectablePoint(detectPoint, 2.0F);
    
    patrolPath = new java.util.ArrayList(10);
  }
  
  public NpcMovementSettings(Marker npcMarker, int[] moveData, MoveType type)
  {
    this.type = type;
    
    patrolPath = new java.util.ArrayList(10);
    
    if (type == MoveType.Wander)
    {
      wanderPoint = new MutablePoint(moveData[0], moveData[1], moveData[2]);
      useWanderCircle = (moveData[6] == 0);
      if (useWanderCircle) {
        wanderR = moveData[3];
      } else {
        wanderX = moveData[3];
        wanderZ = moveData[4];
      }
      overrideMovementSpeed = (moveData[5] != 32769);
      if (overrideMovementSpeed) {
        movementSpeedOverride = (moveData[5] / 32767.0F);
      }
      detectPoint = new MutablePoint(moveData[7], moveData[8], moveData[9]);
      useDetectCircle = (moveData[12] == 0);
      if (useDetectCircle) {
        detectR = moveData[10];
      } else {
        detectX = moveData[10];
        detectZ = moveData[11];
      }
      
      flying = (moveData[13] != 0);
    }
    else if (type == MoveType.Patrol)
    {
      wanderPoint = new MutablePoint(moveData[32], moveData[33], moveData[34]);
      
      int numPatrolPoints = moveData[0];
      for (int i = 0; i < numPatrolPoints; i++) {
        Waypoint.addToList(patrolPath, moveData[(3 * i + 1)], moveData[(3 * i + 2)], moveData[(3 * i + 3)]);
      }
      overrideMovementSpeed = (moveData[31] != 32769);
      if (overrideMovementSpeed) {
        movementSpeedOverride = (moveData[31] / 32767.0F);
      }
      detectPoint = new MutablePoint(moveData[32], moveData[33], moveData[34]);
      useDetectCircle = (moveData[37] == 0);
      if (useDetectCircle) {
        detectR = moveData[35];
      } else {
        detectX = moveData[35];
        detectZ = moveData[36];
      }
      
      flying = (moveData[38] != 0);


    }
    else
    {

      wanderPoint = new MutablePoint(position.getX(), position.getY(), position.getZ());
      



      detectPoint = new MutablePoint(position.getX(), position.getY(), position.getZ());
    }
    
    wanderCenter = new editor.selection.SelectablePoint(wanderPoint, 2.0F);
    detectCenter = new editor.selection.SelectablePoint(detectPoint, 2.0F);
  }
  
  public int[] getMovementData()
  {
    int[] moveData = new int[48];
    
    if (type == MoveType.Wander)
    {
      moveData[0] = wanderPoint.getX();
      moveData[1] = wanderPoint.getY();
      moveData[2] = wanderPoint.getZ();
      moveData[3] = (useWanderCircle ? wanderR : wanderX);
      moveData[4] = (useWanderCircle ? 0 : wanderZ);
      moveData[5] = (overrideMovementSpeed ? (int)(movementSpeedOverride * 32767.0F) : 32769);
      moveData[6] = (useWanderCircle ? 0 : 1);
      
      moveData[7] = detectPoint.getX();
      moveData[8] = detectPoint.getY();
      moveData[9] = detectPoint.getZ();
      moveData[10] = (useDetectCircle ? detectR : detectX);
      moveData[11] = (useDetectCircle ? 0 : detectZ);
      moveData[12] = (useDetectCircle ? 0 : 1);
      moveData[13] = (flying ? 1 : 0);
    }
    else if (type == MoveType.Patrol)
    {
      moveData[0] = patrolPath.size();
      for (int i = 0; i < patrolPath.size(); i++)
      {
        Waypoint wp = (Waypoint)patrolPath.get(i);
        moveData[(3 * i + 1)] = wp.getX();
        moveData[(3 * i + 2)] = wp.getY();
        moveData[(3 * i + 3)] = wp.getZ();
      }
      moveData[31] = (overrideMovementSpeed ? (int)(movementSpeedOverride * 32767.0F) : 32769);
      
      moveData[32] = detectPoint.getX();
      moveData[33] = detectPoint.getY();
      moveData[34] = detectPoint.getZ();
      moveData[35] = (useDetectCircle ? detectR : detectX);
      moveData[36] = (useDetectCircle ? 0 : detectZ);
      moveData[37] = (useDetectCircle ? 0 : 1);
      moveData[38] = (flying ? 1 : 0);
    }
    
    return moveData;
  }
  
  public void addSelectablePoints(java.util.List<editor.selection.SelectablePoint> points)
  {
    points.add(detectCenter);
    
    if (type == MoveType.Wander) {
      points.add(wanderCenter);
    } else if (type == MoveType.Patrol)
    {
      for (Waypoint wp : patrolPath) {
        points.add(wp);
      }
    }
  }
  
  public static final class SetMovementType extends AbstractCommand
  {
    private final Marker m;
    private final NpcMovementSettings.MoveType oldType;
    private final NpcMovementSettings.MoveType newType;
    
    public SetMovementType(Marker m, NpcMovementSettings.MoveType newType) {
      super();
      this.m = m;
      oldType = moveSettings.type;
      this.newType = newType;
    }
    

    public boolean shouldExec()
    {
      return oldType != newType;
    }
    

    public void exec()
    {
      super.exec();
      m.moveSettings.type = newType;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.moveSettings.type = oldType;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class ToggleFlying extends AbstractCommand
  {
    private final Marker m;
    
    public ToggleFlying(Marker m)
    {
      super();
      this.m = m;
    }
    

    public void exec()
    {
      super.exec();
      m.moveSettings.flying = (!m.moveSettings.flying);
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.moveSettings.flying = (!m.moveSettings.flying);
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class ToggleOverride extends AbstractCommand
  {
    private final Marker m;
    
    public ToggleOverride(Marker m)
    {
      super();
      this.m = m;
    }
    

    public void exec()
    {
      super.exec();
      m.moveSettings.overrideMovementSpeed = (!m.moveSettings.overrideMovementSpeed);
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.moveSettings.overrideMovementSpeed = (!m.moveSettings.overrideMovementSpeed);
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetOverrideSpeed extends AbstractCommand
  {
    private final Marker m;
    private final float oldValue;
    private final float newValue;
    
    public SetOverrideSpeed(Marker m, float speed)
    {
      super();
      this.m = m;
      oldValue = moveSettings.movementSpeedOverride;
      newValue = speed;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.moveSettings.movementSpeedOverride = newValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.moveSettings.movementSpeedOverride = oldValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetDetectPos extends editor.selection.SelectablePoint.SetPointPosition
  {
    private final Marker m;
    
    public SetDetectPos(Marker m, int axis, int val)
    {
      super(moveSettings.detectCenter, axis, val);
      this.m = m;
    }
    

    public void exec()
    {
      super.exec();
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetDetectMode extends AbstractCommand
  {
    private final Marker m;
    private final boolean oldValue;
    private final boolean newValue;
    
    public SetDetectMode(Marker m, boolean useCircle)
    {
      super();
      this.m = m;
      oldValue = moveSettings.useDetectCircle;
      newValue = useCircle;
    }
    

    public void exec()
    {
      super.exec();
      m.moveSettings.useDetectCircle = newValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.moveSettings.useDetectCircle = oldValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetDetectRadius extends AbstractCommand
  {
    private final Marker m;
    private final int oldValue;
    private final int newValue;
    
    public SetDetectRadius(Marker m, int r)
    {
      super();
      this.m = m;
      oldValue = moveSettings.detectR;
      newValue = r;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.moveSettings.detectR = newValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.moveSettings.detectR = oldValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetDetectSizeX extends AbstractCommand
  {
    private final Marker m;
    private final int oldValue;
    private final int newValue;
    
    public SetDetectSizeX(Marker m, int x)
    {
      super();
      this.m = m;
      oldValue = moveSettings.detectX;
      newValue = x;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.moveSettings.detectX = newValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.moveSettings.detectX = oldValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetDetectSizeZ extends AbstractCommand
  {
    private final Marker m;
    private final int oldValue;
    private final int newValue;
    
    public SetDetectSizeZ(Marker m, int z)
    {
      super();
      this.m = m;
      oldValue = moveSettings.detectZ;
      newValue = z;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.moveSettings.detectZ = newValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.moveSettings.detectZ = oldValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetWanderPos extends editor.selection.SelectablePoint.SetPointPosition
  {
    private final Marker m;
    
    public SetWanderPos(Marker m, int axis, int val)
    {
      super(moveSettings.wanderCenter, axis, val);
      this.m = m;
    }
    

    public void exec()
    {
      super.exec();
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetWanderMode extends AbstractCommand
  {
    private final Marker m;
    private final boolean oldValue;
    private final boolean newValue;
    
    public SetWanderMode(Marker m, boolean useCircle)
    {
      super();
      this.m = m;
      oldValue = moveSettings.useWanderCircle;
      newValue = useCircle;
    }
    

    public void exec()
    {
      super.exec();
      m.moveSettings.useWanderCircle = newValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.moveSettings.useWanderCircle = oldValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetWanderRadius extends AbstractCommand
  {
    private final Marker m;
    private final int oldValue;
    private final int newValue;
    
    public SetWanderRadius(Marker m, int r)
    {
      super();
      this.m = m;
      oldValue = moveSettings.wanderR;
      newValue = r;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.moveSettings.wanderR = newValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.moveSettings.wanderR = oldValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetWanderSizeX extends AbstractCommand
  {
    private final Marker m;
    private final int oldValue;
    private final int newValue;
    
    public SetWanderSizeX(Marker m, int x)
    {
      super();
      this.m = m;
      oldValue = moveSettings.wanderX;
      newValue = x;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.moveSettings.wanderX = newValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.moveSettings.wanderX = oldValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
  
  public static final class SetWanderSizeZ extends AbstractCommand
  {
    private final Marker m;
    private final int oldValue;
    private final int newValue;
    
    public SetWanderSizeZ(Marker m, int z)
    {
      super();
      this.m = m;
      oldValue = moveSettings.wanderZ;
      newValue = z;
    }
    

    public boolean shouldExec()
    {
      return newValue != oldValue;
    }
    

    public void exec()
    {
      super.exec();
      m.moveSettings.wanderZ = newValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
    

    public void undo()
    {
      super.undo();
      m.moveSettings.wanderZ = oldValue;
      MarkerInfoPanel.instance().updateFields(m);
    }
  }
}
