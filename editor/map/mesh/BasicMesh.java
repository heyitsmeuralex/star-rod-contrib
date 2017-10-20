package editor.map.mesh;

import editor.map.shape.TriangleBatch;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;
import java.util.List;

public class BasicMesh extends AbstractMesh implements Iterable<Triangle>
{
  private static final long serialVersionUID = 1L;
  private static final int latestVersion = 0;
  private int instanceVersion = 0;
  
  public TriangleBatch batch;
  

  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    super.readExternal(in);
    instanceVersion = in.readInt();
    
    batch = ((TriangleBatch)in.readObject());
    updateHierarchy();
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    super.writeExternal(out);
    out.writeInt(0);
    
    out.writeObject(batch);
  }
  

  public BasicMesh()
  {
    batch = new TriangleBatch();
    batch.parentMesh = this;
  }
  

  public void updateHierarchy()
  {
    batch.parentMesh = this;
    for (Triangle t : batch.triangles)
    {
      parentBatch = batch;
      vert[0].parentMesh = this;
      vert[1].parentMesh = this;
      vert[2].parentMesh = this;
    }
  }
  

  public BasicMesh deepCopy()
  {
    BasicMesh m = new BasicMesh();
    batch = batch.deepCopy();
    m.updateHierarchy();
    return m;
  }
  

  public Iterator<Triangle> iterator()
  {
    return batch.triangles.iterator();
  }
}
