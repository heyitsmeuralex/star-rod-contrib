package editor.map.shape;

import editor.map.mesh.AbstractMesh;
import editor.map.mesh.Triangle;
import editor.map.shape.commands.DisplayCommand;
import editor.map.shape.commands.DisplayCommand.CmdType;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.LinkedList;
import java.util.List;

public class TriangleBatch extends DisplayCommand implements Externalizable
{
  private static final long serialVersionUID = 1L;
  private static final int latestVersion = 0;
  private int instanceVersion = 0;
  
  public List<Triangle> triangles;
  public transient AbstractMesh parentMesh;
  
  public void setParent(AbstractMesh parent)
  {
    parentMesh = parent;
    for (Triangle t : triangles)
    {
      parentBatch = this;
      vert[0].parentMesh = parent;
      vert[1].parentMesh = parent;
      vert[2].parentMesh = parent;
    }
  }
  
  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    instanceVersion = in.readInt();
    
    int numTriangles = in.readInt();
    triangles = new LinkedList();
    for (int i = 0; i < numTriangles; i++)
    {
      Triangle t = (Triangle)in.readObject();
      parentBatch = this;
      triangles.add(t);
    }
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    out.writeInt(0);
    
    out.writeInt(triangles.size());
    for (Triangle t : triangles) {
      out.writeObject(t);
    }
  }
  
  public TriangleBatch() {
    triangles = new LinkedList();
  }
  
  public TriangleBatch deepCopy()
  {
    TriangleBatch copyBatch = new TriangleBatch();
    
    for (Triangle t : triangles)
    {
      Triangle copyTriangle = t.deepCopy();
      copyTriangle.setParent(copyBatch);
      triangles.add(copyTriangle);
    }
    
    return copyBatch;
  }
  

  public String toString()
  {
    return "Draw " + triangles.size() + " triangles.";
  }
  

  public DisplayCommand.CmdType getType()
  {
    return DisplayCommand.CmdType.DrawTriangleBatch;
  }
  

  public int[] getF3DEX2Command()
  {
    throw new UnsupportedOperationException("Cannot create F3DEX2 command for TriangleBatch objects!");
  }
}
