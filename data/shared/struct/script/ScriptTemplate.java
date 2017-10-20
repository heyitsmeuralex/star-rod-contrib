package data.shared.struct.script;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import org.apache.commons.io.IOUtils;
import util.Logger;
import util.Priority;



public enum ScriptTemplate
{
  SEARCH_BUSH("SearchBush"), 
  SHAKE_TREE("ShakeTree");
  
  private final String name;
  private final ByteBuffer buffer;
  
  private ScriptTemplate(String resourceName)
  {
    name = resourceName;
    
    InputStream is = ScriptTemplate.class.getResourceAsStream(resourceName + ".bin");
    if (is == null)
    {
      Logger.log("Unable to find resource " + resourceName, Priority.ERROR);
      buffer = null;
      return;
    }
    
    ByteBuffer bb = null;
    try {
      bb = ByteBuffer.wrap(IOUtils.toByteArray(is));
    } catch (IOException e) {
      Logger.log("Unable to load resource " + resourceName, Priority.ERROR);
      buffer = null;
      return;
    }
    buffer = bb;
  }
  
  public boolean matches(ByteBuffer fileBuffer, int scriptOffset)
  {
    int initialBufferPosition = fileBuffer.position();
    fileBuffer.position(scriptOffset);
    buffer.rewind();
    
    boolean matches = true;
    while (buffer.hasRemaining())
    {
      int v = fileBuffer.getInt();
      int u = buffer.getInt();
      
      if (u != -2145058817)
      {

        if (v != u)
        {
          matches = false;
          break;
        }
      }
    }
    fileBuffer.position(initialBufferPosition);
    return matches;
  }
  
  public String getName()
  {
    return name;
  }
}
