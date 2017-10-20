package editor.map.tree;

import editor.map.MapObject;
import javax.swing.tree.DefaultMutableTreeNode;






public class MapObjectNode<T extends MapObject>
  extends DefaultMutableTreeNode
  implements Comparable<MapObjectNode<T>>
{
  private static final long serialVersionUID = 1L;
  public MapObjectNode<T> parentNode;
  public int childIndex;
  private int treeIndex = -1;
  
  public MapObjectNode(T obj)
  {
    super(obj);
  }
  
  public void setUserObject(T obj)
  {
    super.setUserObject(obj);
  }
  



  public MapObjectNode<T> getParent()
  {
    return (MapObjectNode)super.getParent();
  }
  



  public MapObjectNode<T> getChildAt(int index)
  {
    return (MapObjectNode)super.getChildAt(index);
  }
  



  public T getUserObject()
  {
    return (MapObject)super.getUserObject();
  }
  





  public boolean getAllowsChildren()
  {
    return getUserObject().allowsChildren();
  }
  
  public int reassignIndexDepthFirstPost(int current)
  {
    for (int i = 0; i < getChildCount(); i++)
    {
      current = getChildAt(i).reassignIndexDepthFirstPost(current);
      getChildAtchildIndex = i;
    }
    
    treeIndex = (current++);
    
    return current;
  }
  
  public int countDescendents()
  {
    return countDescendents(-1);
  }
  
  private int countDescendents(int accumulator)
  {
    for (int i = 0; i < getChildCount(); i++)
    {
      accumulator = getChildAt(i).countDescendents(accumulator);
    }
    accumulator++;
    return accumulator;
  }
  
  public int getTreeIndex()
  {
    return treeIndex;
  }
  

  public int compareTo(MapObjectNode<T> other)
  {
    return treeIndex - treeIndex;
  }
}
