package editor.map.mesh;

import editor.map.MapObject;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class AbstractMesh
  implements Iterable<Triangle>, Externalizable
{
  private static final long serialVersionUID = 1L;
  private static final int latestVersion = 0;
  private int instanceVersion = 0;
  
  public transient int selectedTriangleCount = 0;
  
  public MapObject parentObject;
  
  public AbstractMesh() {}
  
  public abstract AbstractMesh deepCopy();
  
  public abstract void updateHierarchy();
  
  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    instanceVersion = in.readInt();
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeInt(0);
  }
}
