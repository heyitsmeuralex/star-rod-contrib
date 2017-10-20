package editor.map;

import editor.map.shape.LightSet;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.io.PrintStream;
import main.DevContext;
import main.Directories;
import util.IOUtils;




public class VersionUpdater
{
  public static void main(String[] args)
    throws IOException
  {
    DevContext.initialize();
    new VersionUpdater();
    DevContext.exit();
  }
  
  public VersionUpdater() throws IOException
  {
    for (File f : IOUtils.getFilesWithExtension(Directories.MOD_MAP_SRC, "map", true))
    {
      Map.loadMap(f).saveMap();
    }
    
    for (File f : IOUtils.getFilesWithExtension(Directories.MOD_MAP_SAVE, "map", true))
    {
      Map.loadMap(f).saveMap();
    }
    
    System.exit(0);
  }
  

















































  public static class CustomInputStream
    extends ObjectInputStream
  {
    public CustomInputStream(InputStream in)
      throws IOException
    {
      super();
    }
    
    protected ObjectStreamClass readClassDescriptor() throws IOException, ClassNotFoundException
    {
      ObjectStreamClass resultClassDescriptor = super.readClassDescriptor();
      
      if (resultClassDescriptor.getName().equals("editor.map.shape.LightSet")) {
        resultClassDescriptor = ObjectStreamClass.lookup(LightSet.class);
      }
      try
      {
        localClass = Class.forName(resultClassDescriptor.getName());
      } catch (ClassNotFoundException e) { Class<?> localClass;
        System.out.println("Could not find local class for " + resultClassDescriptor.getName());
        return resultClassDescriptor; }
      Class<?> localClass;
      ObjectStreamClass localClassDescriptor = ObjectStreamClass.lookup(localClass);
      

      if (localClassDescriptor != null)
      {
        long localSUID = localClassDescriptor.getSerialVersionUID();
        long streamSUID = resultClassDescriptor.getSerialVersionUID();
        if (streamSUID != localSUID)
        {
          System.out.println(localClass.getName() + " serialVersionUID conflict: " + streamSUID + ", expected " + localSUID);
          resultClassDescriptor = localClassDescriptor;
        }
      }
      return resultClassDescriptor;
    }
  }
}
