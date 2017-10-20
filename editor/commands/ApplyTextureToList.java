package editor.commands;

import data.texture.EditorTexture;
import editor.map.mesh.TexturedMesh;
import editor.map.shape.Model;
import java.util.HashMap;

public class ApplyTextureToList extends AbstractCommand
{
  private Iterable<Model> modelList;
  private HashMap<Model, EditorTexture> textureMap;
  private EditorTexture newTexture;
  
  public ApplyTextureToList(Iterable<Model> modelList, EditorTexture newTexture)
  {
    super("Apply Texture to List");
    this.modelList = modelList;
    this.newTexture = newTexture;
    textureMap = new HashMap();
    
    for (Model mdl : this.modelList)
    {
      textureMap.put(mdl, getMeshtexture);
    }
  }
  

  public void exec()
  {
    super.exec();
    
    for (Model mdl : modelList) {
      mdl.getMesh().changeTexture(newTexture);
    }
  }
  
  public void undo()
  {
    super.undo();
    
    for (Model mdl : modelList) {
      mdl.getMesh().changeTexture((EditorTexture)textureMap.get(mdl));
    }
  }
}
