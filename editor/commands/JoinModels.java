package editor.commands;

import editor.Editor;
import editor.map.Map;
import editor.map.mesh.TexturedMesh;
import editor.map.mesh.TexturedMesh.DisplayListModel;
import editor.map.shape.Model;
import editor.map.shape.TriangleBatch;
import editor.map.shape.commands.DisplayCommand;
import java.util.ArrayList;
import java.util.List;

public class JoinModels extends AbstractCommand
{
  private List<Model> models;
  private ArrayList<List<DisplayCommand>> oldDisplayLists;
  
  public JoinModels(List<Model> models)
  {
    super("Join Models");
    
    this.models = models;
    oldDisplayLists = new ArrayList(models.size());
    
    for (Model mdl : models)
    {
      List<DisplayCommand> displayList = new ArrayList(getMeshdisplayListModel.size());
      for (DisplayCommand cmd : getMeshdisplayListModel)
        displayList.add(cmd);
      oldDisplayLists.add(displayList);
    }
  }
  

  public void exec()
  {
    super.exec();
    
    Model first = (Model)models.get(0);
    
    for (int i = 1; i < models.size(); i++)
    {
      Model mdl = (Model)models.get(i);
      TexturedMesh mesh = mdl.getMesh();
      
      for (DisplayCommand cmd : displayListModel)
      {
        if (cmd.getType() == editor.map.shape.commands.DisplayCommand.CmdType.DrawTriangleBatch)
        {
          TexturedMesh newMesh = first.getMesh();
          displayListModel.addElement(cmd);
          ((TriangleBatch)cmd).setParent(newMesh);
        }
      }
      
      Editor.map.remove(mdl);
      Editor.selectionManager.deleteObject(mdl);
    }
    
    dirtyAABB = true;
  }
  

  public void undo()
  {
    super.undo();
    
    for (int i = models.size() - 1; i >= 0; i--)
    {
      Model mdl = (Model)models.get(i);
      
      if (i != 0)
      {
        Editor.map.add(mdl);
        Editor.selectionManager.createObject(mdl);
      }
      
      TexturedMesh mesh = mdl.getMesh();
      displayListModel.clear();
      
      for (DisplayCommand cmd : (List)oldDisplayLists.get(i))
      {
        displayListModel.addElement(cmd);
        if (cmd.getType() == editor.map.shape.commands.DisplayCommand.CmdType.DrawTriangleBatch) {
          ((TriangleBatch)cmd).setParent(mesh);
        }
      }
      dirtyAABB = true;
    }
  }
}
