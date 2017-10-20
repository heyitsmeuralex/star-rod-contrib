package editor.map.tree;

import editor.map.shape.Model;

public class ModelTreeModel extends MapObjectTreeModel<Model>
{
  private static final long serialVersionUID = 1L;
  
  public ModelTreeModel(MapObjectNode<Model> root)
  {
    super(root);
  }
  
  public void recalculateIndicies()
  {
    getRoot().reassignIndexDepthFirstPost(0);
  }
}
