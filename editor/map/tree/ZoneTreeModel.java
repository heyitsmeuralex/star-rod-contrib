package editor.map.tree;

import editor.map.hit.Zone;

public class ZoneTreeModel extends MapObjectTreeModel<Zone>
{
  private static final long serialVersionUID = 1L;
  
  public ZoneTreeModel(MapObjectNode<Zone> root)
  {
    super(root);
  }
  
  public void recalculateIndicies()
  {
    getRoot().reassignIndexDepthFirstPost(0);
  }
}
