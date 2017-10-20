package editor.map.mesh;

import data.texture.EditorTexture;
import editor.map.shape.TriangleBatch;
import editor.map.shape.commands.ChangeGeometryFlags;
import editor.map.shape.commands.DisplayCommand;
import editor.map.shape.commands.FlushPipeline;
import editor.render.TextureManager;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import javax.swing.DefaultListModel;


public class TexturedMesh
  extends AbstractMesh
{
  private static final long serialVersionUID = 1L;
  private static final int latestVersion = 3;
  public int instanceVersion = 3;
  
  public transient DisplayListModel displayListModel;
  
  private transient List<Triangle> triangleListCache;
  
  private transient boolean displayListDirty;
  
  public String textureName = "";
  

  public transient boolean textured = false;
  public transient EditorTexture texture = null;
  
  public void readExternal(ObjectInput in)
    throws IOException, ClassNotFoundException
  {
    super.readExternal(in);
    instanceVersion = in.readInt();
    
    textureName = in.readUTF();
    textured = (!textureName.isEmpty());
    
    if (instanceVersion < 3) {
      throw new UnsupportedOperationException();
    }
    int numCommands = in.readInt();
    for (int i = 0; i < numCommands; i++)
    {
      boolean isTriangleBatch = in.readBoolean();
      
      if (isTriangleBatch)
      {
        TriangleBatch batch = (TriangleBatch)in.readObject();
        displayListModel.addElement(batch);
      }
      else
      {
        int r = in.readInt();
        int s = in.readInt();
        displayListModel.addElement(DisplayCommand.resolveCommand(r, s));
      }
    }
    
    updateHierarchy();
  }
  
  public void writeExternal(ObjectOutput out)
    throws IOException
  {
    super.writeExternal(out);
    out.writeInt(3);
    
    out.writeUTF(textureName);
    
    if (instanceVersion < 3) {
      throw new UnsupportedOperationException();
    }
    int numCommands = displayListModel.size();
    out.writeInt(numCommands);
    for (int i = 0; i < numCommands; i++)
    {
      DisplayCommand cmd = (DisplayCommand)displayListModel.get(i);
      if ((cmd instanceof TriangleBatch))
      {
        out.writeBoolean(true);
        out.writeObject((TriangleBatch)cmd);
      }
      else
      {
        out.writeBoolean(false);
        int[] binaryCmd = cmd.getF3DEX2Command();
        out.writeInt(binaryCmd[0]);
        out.writeInt(binaryCmd[1]);
      }
    }
  }
  
  public static TexturedMesh createDefaultMesh()
  {
    TexturedMesh mesh = new TexturedMesh();
    addDefaultCommands(displayListModel);
    return mesh;
  }
  
  public static void addDefaultCommands(DisplayListModel listModel)
  {
    listModel.addElement(new FlushPipeline());
    listModel.addElement(ChangeGeometryFlags.getCommand(-637665281, 0));
    listModel.addElement(ChangeGeometryFlags.getCommand(-637534209, 2098176));
  }
  

  public TexturedMesh()
  {
    displayListModel = new DisplayListModel(this, null);
  }
  
  public void setTexture(String texName)
  {
    if (texName.isEmpty()) {
      setTexture((EditorTexture)null);
    } else {
      setTexture(TextureManager.get(texName));
    }
  }
  
  public void setTexture(EditorTexture newTexture) {
    texture = newTexture;
    textured = (newTexture != null);
    textureName = (textured ? newTexture.getName() : "");
  }
  
  public void changeTexture(EditorTexture newTexture)
  {
    TextureManager.decrement(texture);
    setTexture(newTexture);
    TextureManager.increment(texture);
  }
  

  public void updateHierarchy()
  {
    for (int i = 0; i < displayListModel.size(); i++)
    {
      DisplayCommand cmd = (DisplayCommand)displayListModel.getElementAt(i);
      if ((cmd instanceof TriangleBatch)) {
        ((TriangleBatch)cmd).setParent(this);
      }
    }
  }
  
  public TexturedMesh deepCopy()
  {
    TexturedMesh m = new TexturedMesh();
    
    for (int i = 0; i < displayListModel.size(); i++)
    {
      DisplayCommand cmd = (DisplayCommand)displayListModel.getElementAt(i);
      displayListModel.addElement(cmd.deepCopy());
    }
    m.updateHierarchy();
    m.changeTexture(texture);
    
    return m;
  }
  

  public Iterator<Triangle> iterator()
  {
    return new TexturedMeshIterator(displayListModel, null);
  }
  
  private static class TexturedMeshIterator implements Iterator<Triangle>
  {
    private ArrayList<Iterator<Triangle>> iterators = new ArrayList(3);
    private int i = 0;
    
    private TexturedMeshIterator(TexturedMesh.DisplayListModel displayList)
    {
      for (int i = 0; i < displayList.size(); i++)
      {
        DisplayCommand cmd = (DisplayCommand)displayList.getElementAt(i);
        if ((cmd instanceof TriangleBatch))
        {
          List<Triangle> tris = triangles;
          iterators.add(tris.iterator());
        }
      }
    }
    

    public boolean hasNext()
    {
      if (iterators.isEmpty()) {
        return false;
      }
      return batchHasNext(i);
    }
    
    private boolean batchHasNext(int i)
    {
      if (((Iterator)iterators.get(i)).hasNext()) {
        return true;
      }
      if (i + 1 < iterators.size()) {
        return batchHasNext(i + 1);
      }
      return false;
    }
    

    public Triangle next()
    {
      if (!hasNext()) {
        throw new NoSuchElementException();
      }
      Iterator<Triangle> iter = (Iterator)iterators.get(i);
      while (!iter.hasNext())
      {
        i += 1;
        iter = (Iterator)iterators.get(i);
      }
      
      return (Triangle)iter.next();
    }
    

    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  

  public static class DisplayListModel
    extends DefaultListModel<DisplayCommand>
    implements Iterable<DisplayCommand>
  {
    private static final long serialVersionUID = 1L;
    
    private final TexturedMesh mesh;
    
    private DisplayListModel(TexturedMesh mesh)
    {
      this.mesh = mesh;
    }
    
    public void setDirty()
    {
      mesh.displayListDirty = true;
    }
    

    public void add(int index, DisplayCommand cmd)
    {
      if ((cmd instanceof TriangleBatch))
        mesh.displayListDirty = true;
      super.add(index, cmd);
    }
    

    public void addElement(DisplayCommand cmd)
    {
      if ((cmd instanceof TriangleBatch))
        mesh.displayListDirty = true;
      super.addElement(cmd);
    }
    

    public void insertElementAt(DisplayCommand cmd, int index)
    {
      if ((cmd instanceof TriangleBatch))
        mesh.displayListDirty = true;
      super.insertElementAt(cmd, index);
    }
    

    public DisplayCommand remove(int index)
    {
      DisplayCommand cmd = (DisplayCommand)super.remove(index);
      if ((cmd instanceof TriangleBatch))
        mesh.displayListDirty = true;
      return cmd;
    }
    

    public boolean removeElement(Object obj)
    {
      boolean removed = super.removeElement(obj);
      if ((removed) && ((obj instanceof TriangleBatch)))
        mesh.displayListDirty = true;
      return removed;
    }
    

    public void removeElementAt(int index)
    {
      if ((get(index) instanceof TriangleBatch))
        mesh.displayListDirty = true;
      super.removeElementAt(index);
    }
    

    public void removeAllElements()
    {
      mesh.displayListDirty = true;
      super.removeAllElements();
    }
    

    public void clear()
    {
      mesh.displayListDirty = true;
      super.clear();
    }
    

    public DisplayCommand set(int index, DisplayCommand cmd)
    {
      DisplayCommand old = (DisplayCommand)super.set(index, cmd);
      if (((old instanceof TriangleBatch)) || ((cmd instanceof TriangleBatch)))
        mesh.displayListDirty = true;
      return old;
    }
    

    public void setElementAt(DisplayCommand cmd, int index)
    {
      if (((get(index) instanceof TriangleBatch)) || ((cmd instanceof TriangleBatch)))
        mesh.displayListDirty = true;
      super.setElementAt(cmd, index);
    }
    

    public Iterator<DisplayCommand> iterator()
    {
      return new DisplayListIterator(this, null);
    }
    
    private static class DisplayListIterator implements Iterator<DisplayCommand>
    {
      private final TexturedMesh.DisplayListModel listModel;
      private int pos = 0;
      
      private DisplayListIterator(TexturedMesh.DisplayListModel listModel)
      {
        this.listModel = listModel;
      }
      

      public boolean hasNext()
      {
        return pos < listModel.getSize();
      }
      

      public DisplayCommand next()
      {
        return (DisplayCommand)listModel.get(pos++);
      }
    }
  }
}
