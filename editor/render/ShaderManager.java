package editor.render;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.lwjgl.opengl.GL20;
import util.Logger;
import util.Priority;





















public abstract class ShaderManager
{
  public static int modelShader = 0;
  public static int model_sampleMain = 0;
  public static int model_sampleAux = 0;
  
  public static int model_selected = 0;
  public static int model_textured = 0;
  
  public static int model_auxCombineMode = 0;
  public static int model_auxScale = 0;
  public static int model_auxOffset = 0;
  
  public static int model_useFiltering = 0;
  
  public static int model_mainScroll = 0;
  public static int model_auxScroll = 0;
  
  public ShaderManager() {}
  
  public static void loadShaders() { modelShader = createModelShader();
    model_sampleMain = GL20.glGetUniformLocation(modelShader, "MainImage");
    model_sampleAux = GL20.glGetUniformLocation(modelShader, "AuxImage");
    
    model_selected = GL20.glGetUniformLocation(modelShader, "selected");
    model_textured = GL20.glGetUniformLocation(modelShader, "textured");
    
    model_auxCombineMode = GL20.glGetUniformLocation(modelShader, "auxCombineMode");
    model_auxScale = GL20.glGetUniformLocation(modelShader, "auxScale");
    model_auxOffset = GL20.glGetUniformLocation(modelShader, "auxOffset");
    
    model_useFiltering = GL20.glGetUniformLocation(modelShader, "useFiltering");
    
    model_mainScroll = GL20.glGetUniformLocation(modelShader, "mainScroll");
    model_auxScroll = GL20.glGetUniformLocation(modelShader, "auxScroll");
  }
  
  private static int createModelShader()
  {
    int vertShader = createShader("vert.glsl", 35633);
    int fragShader = createShader("frag.glsl", 35632);
    int programID = GL20.glCreateProgram();
    
    GL20.glAttachShader(programID, vertShader);
    GL20.glAttachShader(programID, fragShader);
    
    GL20.glLinkProgram(programID);
    
    if (GL20.glGetProgrami(programID, 35714) == 0)
    {
      GL20.glDeleteProgram(programID);
      throw new RuntimeException("Failed to link shaders \n" + getInfoLog(programID));
    }
    
    GL20.glDetachShader(programID, vertShader);
    GL20.glDetachShader(programID, fragShader);
    
    GL20.glDeleteShader(vertShader);
    GL20.glDeleteShader(fragShader);
    
    return programID;
  }
  
  private static int createShader(String resourceName, int type)
  {
    InputStream is = ShaderManager.class.getResourceAsStream(resourceName);
    if (is == null)
    {
      Logger.log("Unable to find resource " + resourceName, Priority.ERROR);
      return 0;
    }
    
    StringBuilder sb = new StringBuilder();
    try { BufferedReader reader = new BufferedReader(new InputStreamReader(is));Throwable localThrowable3 = null;
      try { String line;
        while ((line = reader.readLine()) != null) {
          sb.append(line).append('\n');
        }
      }
      catch (Throwable localThrowable1)
      {
        localThrowable3 = localThrowable1;throw localThrowable1;
      }
      finally
      {
        if (reader != null) if (localThrowable3 != null) try { reader.close(); } catch (Throwable localThrowable2) { localThrowable3.addSuppressed(localThrowable2); } else reader.close();
      } } catch (IOException e) { Logger.log("Exception while reading shader " + resourceName, Priority.ERROR);
    }
    
    int shaderID = GL20.glCreateShader(type);
    GL20.glShaderSource(shaderID, sb.toString());
    GL20.glCompileShader(shaderID);
    
    if (GL20.glGetShaderi(shaderID, 35713) == 0)
    {
      GL20.glDeleteShader(shaderID);
      throw new RuntimeException("Failed to compile shader " + resourceName + "\n" + getInfoLog(shaderID));
    }
    
    return shaderID;
  }
  
  private static String getInfoLog(int shaderID)
  {
    int messageLength = GL20.glGetShaderi(shaderID, 35716);
    return GL20.glGetShaderInfoLog(shaderID, messageLength);
  }
}
