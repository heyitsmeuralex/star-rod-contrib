package editor.commands;

import editor.Editor;
import editor.render.TextureManager;
import editor.ui.SwingGUI;

public class ChangeTextureArchive extends AbstractCommand
{
  private final String oldName;
  private final String newName;
  
  public ChangeTextureArchive(String name)
  {
    super("Use Texture Archive " + name);
    oldName = maptexName;
    newName = name;
  }
  

  public boolean shouldExec()
  {
    return (!newName.isEmpty()) && (!newName.equals(oldName));
  }
  

  public void exec()
  {
    super.exec();
    
    for (editor.map.shape.Model mdl : mapmodelTree)
      getMeshtexture = null;
    TextureManager.clear();
    
    TextureManager.load(newName);
    TextureManager.assignModelTextures(Editor.map);
    Editor.gui.setSelectedTexture(null);
    Editor.gui.loadTexturePreviews();
    
    maptexName = newName;
  }
  

  public void undo()
  {
    super.undo();
    
    for (editor.map.shape.Model mdl : mapmodelTree)
      getMeshtexture = null;
    TextureManager.clear();
    
    TextureManager.load(oldName);
    TextureManager.assignModelTextures(Editor.map);
    Editor.gui.setSelectedTexture(null);
    Editor.gui.loadTexturePreviews();
    
    maptexName = oldName;
  }
}
