package editor.map.tree;

import editor.map.marker.Marker;

public class MarkerTreeModel extends MapObjectTreeModel<Marker>
{
  private static final long serialVersionUID = 1L;
  
  public MarkerTreeModel(MapObjectNode<Marker> root)
  {
    super(root);
  }
  
  public void recalculateIndicies()
  {
    getRoot().reassignIndexDepthFirstPost(-1);
  }
}
