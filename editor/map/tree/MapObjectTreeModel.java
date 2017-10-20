package editor.map.tree;

import editor.map.BoundingBox;
import editor.map.MapObject;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import javax.swing.tree.DefaultTreeModel;

public abstract class MapObjectTreeModel<T extends MapObject>
  extends DefaultTreeModel implements Iterable<T>
{
  private static final long serialVersionUID = 1L;
  
  public MapObjectTreeModel(MapObjectNode<T> root)
  {
    super(root);
  }
  

  public MapObjectNode<T> getRoot()
  {
    return (MapObjectNode)super.getRoot();
  }
  
  public List<T> getList()
  {
    List<T> list = new LinkedList();
    Stack<MapObjectNode<T>> nodes = new Stack();
    nodes.push(getRoot());
    while (!nodes.isEmpty())
    {
      MapObjectNode<T> node = (MapObjectNode)nodes.pop();
      for (int i = 0; i < node.getChildCount(); i++)
        nodes.push(node.getChildAt(i));
      list.add(node.getUserObject());
    }
    return list;
  }
  

  public Iterator<T> iterator()
  {
    return new MapObjectTreeModelIterator(getRoot());
  }
  
  private class MapObjectTreeModelIterator implements Iterator<T>
  {
    private final Queue<T> objList;
    
    public MapObjectTreeModelIterator()
    {
      objList = new LinkedList();
      addChildren(root);
    }
    
    private void addChildren(MapObjectNode<T> parent)
    {
      if (parent.getUserObject().shouldIterate()) {
        objList.add(parent.getUserObject());
      }
      for (int i = 0; i < parent.getChildCount(); i++) {
        addChildren(parent.getChildAt(i));
      }
    }
    
    public boolean hasNext()
    {
      return !objList.isEmpty();
    }
    

    public T next()
    {
      return (MapObject)objList.poll();
    }
    

    public void remove()
    {
      throw new UnsupportedOperationException("MapObjectTreeModel traversal does not support remove().");
    }
  }
  

  public T getObject(String name)
  {
    Stack<MapObjectNode<T>> nodes = new Stack();
    nodes.push(getRoot());
    
    while (!nodes.isEmpty())
    {
      MapObjectNode<T> node = (MapObjectNode)nodes.pop();
      T obj = node.getUserObject();
      
      if (name.equals(name)) {
        return obj;
      }
      for (int i = 0; i < node.getChildCount(); i++) {
        nodes.push(node.getChildAt(i));
      }
    }
    return null;
  }
  
  public int getObjectID(String name)
  {
    Stack<MapObjectNode<T>> nodes = new Stack();
    nodes.push(getRoot());
    
    while (!nodes.isEmpty())
    {
      MapObjectNode<T> node = (MapObjectNode)nodes.pop();
      T obj = node.getUserObject();
      
      if (name.equals(name)) {
        return node.getTreeIndex();
      }
      for (int i = 0; i < node.getChildCount(); i++) {
        nodes.push(node.getChildAt(i));
      }
    }
    return -1;
  }
  
  public String getObjectName(int id)
  {
    Stack<MapObjectNode<T>> nodes = new Stack();
    nodes.push(getRoot());
    
    while (!nodes.isEmpty())
    {
      MapObjectNode<T> node = (MapObjectNode)nodes.pop();
      T obj = node.getUserObject();
      
      if (node.getTreeIndex() == id) {
        return name;
      }
      for (int i = 0; i < node.getChildCount(); i++) {
        nodes.push(node.getChildAt(i));
      }
    }
    return null;
  }
  
  public T getObject(int id)
  {
    Stack<MapObjectNode<T>> nodes = new Stack();
    nodes.push(getRoot());
    
    while (!nodes.isEmpty())
    {
      MapObjectNode<T> node = (MapObjectNode)nodes.pop();
      T obj = node.getUserObject();
      
      if (node.getTreeIndex() == id) {
        return obj;
      }
      for (int i = 0; i < node.getChildCount(); i++) {
        nodes.push(node.getChildAt(i));
      }
    }
    return null;
  }
  
  public void add(T obj)
  {
    MapObjectNode<?> node = obj.getNode();
    insertNodeInto(obj.getNode(), parentNode, childIndex);
    recalculateIndicies();
  }
  
  public void remove(T obj)
  {
    MapObjectNode<?> node = obj.getNode();
    removeNodeFromParent(node);
    recalculateIndicies();
  }
  

  public void create(T obj)
  {
    MapObjectNode<T> node = obj.getNode();
    if (parentNode == null)
      parentNode = getRoot();
    childIndex = parentNode.getChildCount();
    insertNodeInto(node, parentNode, childIndex);
    recalculateIndicies();
  }
  
  public abstract void recalculateIndicies();
  
  public void recalculateBoundingBoxes()
  {
    recalculateBoundingBoxes(getRoot());
  }
  
  private boolean recalculateBoundingBoxes(MapObjectNode<T> node)
  {
    boolean dirtyChild = false;
    for (int i = 0; i < node.getChildCount(); i++) {
      dirtyChild = (dirtyChild) || (recalculateBoundingBoxes(node.getChildAt(i)));
    }
    MapObject obj = node.getUserObject();
    
    if ((dirtyAABB) || (dirtyChild))
    {
      AABB.clear();
      obj.recalculateAABB();
      
      for (int i = 0; i < node.getChildCount(); i++) {
        AABB.encompass(getChildAtgetUserObjectAABB);
      }
    }
    return false;
  }
}
