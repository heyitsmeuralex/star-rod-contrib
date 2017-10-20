package editor.map.tree;

import editor.map.hit.Collider;

public class ColliderTreeModel extends MapObjectTreeModel<Collider>
{
  private static final long serialVersionUID = 1L;
  
  public ColliderTreeModel(MapObjectNode<Collider> root)
  {
    super(root);
  }
  
  public void recalculateIndicies()
  {
    getRoot().reassignIndexDepthFirstPost(0);
  }
}
