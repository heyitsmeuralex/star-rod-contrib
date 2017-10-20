package editor.map;

import editor.map.shape.Model;
import editor.map.tree.MapObjectTreeModel;
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import main.DevContext;
import main.Directories;

public class Updater
{
  public Updater() {}
  
  public static void main(String[] args) throws java.io.IOException
  {
    DevContext.initialize();
    
    Collection<File> dumpFiles = util.IOUtils.getFilesWithExtension(Directories.DUMP_MAP, "map", true);
    Collection<File> saveFiles = util.IOUtils.getFilesWithExtension(Directories.MOD_MAP_SAVE, "map", true);
    
    HashMap<String, File> dumpFilesMap = new HashMap();
    
    for (File dumpFile : dumpFiles) {
      dumpFilesMap.put(dumpFile.getName(), dumpFile);
    }
    for (File saveFile : saveFiles)
    {
      File dumpFile = (File)dumpFilesMap.get(saveFile.getName());
      
      if (dumpFile != null)
      {

        Map dumpMap = Map.loadMap(dumpFile);
        Map saveMap = Map.loadMap(saveFile);
        
        lightSets = lightSets;
        
        List<Model> dumpModels = modelTree.getList();
        List<Model> saveModels = modelTree.getList();
        
        HashMap<String, Model> dumpModelsMap = new HashMap();
        
        for (Model mdl : dumpModels) {
          dumpModelsMap.put(name, mdl);
        }
        for (Model saveModel : saveModels)
        {
          Model dumpModel = (Model)dumpModelsMap.get(name);
          if (dumpModel != null)
          {

            lightSet = lightSet;
          }
        }
        saveMap.saveMap();
      }
    }
    DevContext.exit();
  }
}
