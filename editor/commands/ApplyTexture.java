package editor.commands;

import data.texture.EditorTexture;
import editor.map.shape.Model;

public class ApplyTexture extends AbstractCommand
{
  private Model mdl;
  private EditorTexture oldTexture;
  private EditorTexture newTexture;
  
  public ApplyTexture(Model mdl, EditorTexture selectedTexture)
  {
    super("Apply Texture");
    this.mdl = mdl;
    newTexture = selectedTexture;
    oldTexture = getMeshtexture;
  }
  

  public void exec()
  {
    super.exec();
    mdl.getMesh().changeTexture(newTexture);
  }
  

  public void undo()
  {
    super.undo();
    mdl.getMesh().changeTexture(oldTexture);
  }
}
