package editor.commands;

import editor.map.MapObject;
import editor.map.tree.MapObjectNode;
import editor.map.tree.MapObjectTreeModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;









public class MoveNodes<T extends MapObject>
  extends AbstractCommand
{
  private List<MapObjectNode<T>> nodeList;
  private MapObjectTreeModel<T> treeModel;
  private MapObjectNode<T> newParentNode;
  private ArrayList<MoveNodes<T>.TreePosition> oldParents;
  
  public MoveNodes(MapObjectTreeModel<T> treeModel, List<MapObjectNode<T>> nodeList, MapObjectNode<T> newParent)
  {
    super("Move Selected Models");
    this.treeModel = treeModel;
    this.nodeList = nodeList;
    newParentNode = newParent;
    

    Collections.sort(nodeList);
    
    LinkedList<MapObjectNode<T>> groups = new LinkedList();
    

    Iterator<MapObjectNode<T>> iter = nodeList.iterator();
    while (iter.hasNext())
    {
      MapObjectNode<T> node = (MapObjectNode)iter.next();
      

      if (newParentNode.isNodeAncestor(node))
      {
        iter.remove();



      }
      else if (newParentNode == node.getParent())
      {
        iter.remove();


      }
      else if (node.getAllowsChildren())
      {
        groups.add(node);
      }
    }
    
    iter = nodeList.iterator();
    while (iter.hasNext())
    {
      MapObjectNode<T> node = (MapObjectNode)iter.next();
      
      if (!node.getAllowsChildren())
      {
        if (groups.contains(node)) {
          iter.remove();
        }
      }
    }
    oldParents = new ArrayList();
    
    for (int i = 0; i < nodeList.size(); i++)
    {
      MapObjectNode<T> node = (MapObjectNode)nodeList.get(i);
      MapObjectNode<T> parent = node.getParent();
      
      MoveNodes<T>.TreePosition pos = new TreePosition(null);
      oldParentNode = parent;
      position = treeModel.getIndexOfChild(parent, node);
      oldParents.add(pos);
    }
  }
  
  public void exec()
  {
    final int index = newParentNode.getChildCount();
    

    SwingUtilities.invokeLater(new Runnable()
    {
      public void run() {
        int position = index;
        
        for (int i = 0; i < nodeList.size(); i++)
        {
          MapObjectNode<T> node = (MapObjectNode)nodeList.get(i);
          treeModel.removeNodeFromParent(node);
          treeModel.insertNodeInto(node, newParentNode, position);
          
          parentNode = newParentNode;
          position++;
        }
        
        for (int i = 0; i < newParentNode.getChildCount(); i++) {
          newParentNode.getChildAt(i).childIndex = i;
        }
        treeModel.recalculateIndicies();
      }
    });
  }
  


  public void undo()
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run() {
        for (int i = 0; i < nodeList.size(); i++)
        {
          MapObjectNode<T> node = (MapObjectNode)nodeList.get(i);
          MapObjectNode<T> oldParentNode = oldParents.get(i)).oldParentNode;
          int oldPosition = oldParents.get(i)).position;
          treeModel.removeNodeFromParent(node);
          treeModel.insertNodeInto(node, oldParentNode, oldPosition);
          
          parentNode = oldParentNode;
        }
        
        for (MoveNodes<T>.TreePosition pos : oldParents)
        {
          for (int i = 0; i < oldParentNode.getChildCount(); i++) {
            oldParentNode.getChildAt(i).childIndex = i;
          }
        }
        treeModel.recalculateIndicies();
      }
    });
  }
  
  private class TreePosition
  {
    MapObjectNode<T> oldParentNode;
    int position;
    
    private TreePosition() {}
  }
}
