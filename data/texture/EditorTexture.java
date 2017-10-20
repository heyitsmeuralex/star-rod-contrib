package data.texture;

import editor.Editor;
import editor.render.ShaderManager;
import editor.render.TextureManager;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;





























public class EditorTexture
{
  private final Texture tx;
  private int glID_img = -1;
  private int glID_aux = -1;
  
  public BufferedImage imgPreview = null;
  public BufferedImage auxPreview = null;
  
  public int modelCount = 0;
  
  private static boolean ENABLE_FILTERING = false;
  
  public EditorTexture(Texture tx)
  {
    this.tx = tx;
    
    BufferedImage img = ImageConverter.convert(img);
    imgPreview = createPreview(img);
    glID_img = glLoad(img, img, hWrap[1], vWrap[1], filter);
    
    if (hasAux)
    {
      BufferedImage aux = ImageConverter.convert(aux);
      auxPreview = createPreview(aux);
      glID_aux = glLoad(aux, aux, hWrap[0], vWrap[0], filter);
    }
  }
  
  public String getName()
  {
    return tx.name;
  }
  
  public int getHeight()
  {
    return tx.img.height;
  }
  
  public int getWidth()
  {
    return tx.img.width;
  }
  
  public boolean hasAux()
  {
    return tx.hasAux;
  }
  
  public int getAuxHeight()
  {
    return tx.aux.height;
  }
  
  public int getAuxWidth()
  {
    return tx.aux.width;
  }
  
  public int getAuxCombine()
  {
    return tx.auxCombine;
  }
  
  public static float getScaleU(EditorTexture texture)
  {
    return texture != null ? 32.0F * tx.img.width : 1024.0F;
  }
  
  public static float getScaleV(EditorTexture texture)
  {
    return texture != null ? 32.0F * tx.img.height : 1024.0F;
  }
  
  public void clean()
  {
    GL11.glDeleteTextures(glID_img);
    
    if (tx.hasAux) {
      GL11.glDeleteTextures(glID_aux);
    }
  }
  


  private static BufferedImage createPreview(BufferedImage img)
  {
    float scaleFactor;
    

    float scaleFactor;
    
    if (img.getWidth() > img.getHeight()) {
      scaleFactor = 96.0F / img.getWidth();
    } else {
      scaleFactor = 96.0F / img.getHeight();
    }
    int newX = (int)(scaleFactor * img.getWidth());
    int newY = (int)(scaleFactor * img.getHeight());
    
    BufferedImage preview = new BufferedImage(newX, newY, 2);
    Graphics2D g = preview.createGraphics();
    g.drawImage(img, 0, 0, newX, newY, null);
    g.dispose();
    
    return preview;
  }
  
  private static int glLoad(BufferedImage bimg, Image img, int hWrap, int vWrap, boolean filter)
  {
    ByteBuffer buffer = TextureManager.createByteBuffer(bimg);
    
    int glID = GL11.glGenTextures();
    GL11.glBindTexture(3553, glID);
    int hWrapMode;
    int hWrapMode;
    int hWrapMode; switch (hWrap) {
    case 0: 
    default: 
      hWrapMode = 10497; break;
    case 1:  hWrapMode = 33648; break;
    case 2:  hWrapMode = 33071; }
    int vWrapMode;
    int vWrapMode;
    int vWrapMode;
    switch (vWrap) {
    case 0: 
    default: 
      vWrapMode = 10497; break;
    case 1:  vWrapMode = 33648; break;
    case 2:  vWrapMode = 33071;
    }
    
    GL11.glTexParameteri(3553, 10242, hWrapMode);
    GL11.glTexParameteri(3553, 10243, vWrapMode);
    
    if ((ENABLE_FILTERING) && (filter))
    {
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
    }
    else
    {
      GL11.glTexParameteri(3553, 10241, 9728);
      GL11.glTexParameteri(3553, 10240, 9728);
    }
    
    GL11.glTexImage2D(3553, 0, 32856, width, height, 0, 6408, 5121, buffer);
    

    return glID;
  }
  
  public void setShaderParameters()
  {
    GL13.glActiveTexture(33984);
    GL11.glBindTexture(3553, glID_img);
    GL20.glUniform1i(ShaderManager.model_sampleMain, 0);
    
    if ((tx.filter) && (Editor.useFiltering)) {
      GL20.glUniform1i(ShaderManager.model_useFiltering, 1);
    } else {
      GL20.glUniform1i(ShaderManager.model_useFiltering, 0);
    }
    if (tx.hasAux)
    {
      GL13.glActiveTexture(33985);
      GL11.glBindTexture(3553, glID_aux);
      GL20.glUniform1i(ShaderManager.model_sampleAux, 1);
      
      switch (tx.auxCombine)
      {
      case 0: 
      case 8: 
        GL20.glUniform1i(ShaderManager.model_auxCombineMode, 1);
        break;
      case 13: 
        GL20.glUniform1i(ShaderManager.model_auxCombineMode, 2);
        break;
      case 16: 
        GL20.glUniform1i(ShaderManager.model_auxCombineMode, 3);
        break;
      default: 
        GL20.glUniform1i(ShaderManager.model_auxCombineMode, 0);
        break;
      }
    }
    else
    {
      GL20.glUniform1i(ShaderManager.model_auxCombineMode, 0);
    }
    
    GL20.glUniform2f(ShaderManager.model_mainScroll, (float)mainScroll[0], (float)mainScroll[1]);
    GL20.glUniform2f(ShaderManager.model_auxScroll, (float)auxScroll[0], (float)auxScroll[1]);
  }
  
  private double[] mainScroll = new double[2];
  private double[] auxScroll = new double[2];
  
  public int[] mainScrollRate = new int[2];
  public int[] auxScrollRate = new int[2];
  
  public void updateScroll(double deltaTime)
  {
    mainScroll[0] = (mainScrollRate[0] == 0 ? 0.0D : mainScroll[0] + deltaTime * mainScrollRate[0]);
    mainScroll[1] = (mainScrollRate[1] == 0 ? 0.0D : mainScroll[1] + deltaTime * mainScrollRate[1]);
    
    auxScroll[0] = (auxScrollRate[0] == 0 ? 0.0D : auxScroll[0] + deltaTime * auxScrollRate[0]);
    auxScroll[1] = (auxScrollRate[1] == 0 ? 0.0D : auxScroll[1] + deltaTime * auxScrollRate[1]);
  }
}
