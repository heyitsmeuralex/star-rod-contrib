package editor.render;

import data.texture.EditorTexture;
import data.texture.Texture;
import data.texture.TextureArchive;
import editor.map.Map;
import editor.map.mesh.TexturedMesh;
import editor.map.shape.Model;
import editor.ui.TextureInfoPanel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import main.Directories;
import main.StarRodDev;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import util.Logger;
import util.Priority;

































public abstract class TextureManager
{
  public static int untexturedCount = 0;
  
  static
  {
    background = loadImage("tex_background.png");
    miniBackground = background.getScaledInstance(48, 48, 4); }
  
  public static HashMap<String, EditorTexture> textureMap = new HashMap();
  public static List<EditorTexture> textureList = new LinkedList();
  public static Image background;
  public static Image miniBackground;
  public static int glMissingTextureID;
  
  private static BufferedImage loadImage(String resourceName) { InputStream is = TextureManager.class.getResourceAsStream(resourceName);
    if (is == null)
    {
      Logger.log("Unable to find resource " + resourceName, Priority.ERROR);
      return null;
    }
    try
    {
      return ImageIO.read(is);
    } catch (IOException e) {
      Logger.log("Unable to load resource " + resourceName, Priority.ERROR);
      StarRodDev.displayStackTrace(e);
      e.printStackTrace(); }
    return null;
  }
  




  public static void bindEditorTextures()
  {
    BufferedImage image = loadImage("notexture.png");
    glMissingTextureID = bindBufferedImage(image);
    
    image = loadImage("nobg.png");
    glNoBackgoundTexID = bindBufferedImage(image);
    
    image = loadImage("block_mono.png");
    glMarkerTexID = bindBufferedImage(image);
  }
  
  public static EditorTexture get(String name)
  {
    return (EditorTexture)textureMap.get(name);
  }
  
  public static void clear()
  {
    for (EditorTexture t : textureList) {
      t.clean();
    }
    textureMap.clear();
    textureList.clear();
  }
  

  public static int glNoBackgoundTexID;
  
  public static int glMarkerTexID;
  
  public static HashMap<String, Integer> textureCount;
  public static boolean load(String texArchiveName)
  {
    try
    {
      ta = TextureArchive.parseText(Directories.MOD_IMG_TEX.toString(), texArchiveName);
    } catch (IOException e) { TextureArchive ta;
      Logger.log("Could not load texture archive " + texArchiveName);
      return false;
    }
    TextureArchive ta;
    int loaded = 0;
    for (Texture tx : textureList)
    {
      EditorTexture texture = new EditorTexture(tx);
      textureMap.put(name, texture);
      textureList.add(texture);
      loaded++;
    }
    
    Logger.log("Loaded " + loaded + " textures from " + texArchiveName);
    return true;
  }
  




  public static void assignModelTextures(Map map)
  {
    for (EditorTexture tex : textureList)
      modelCount = 0;
    untexturedCount = 0;
    
    for (Model mdl : modelTree)
    {
      EditorTexture tex = null;
      TexturedMesh mesh = mdl.getMesh();
      if (!textureName.isEmpty())
        tex = get(textureName);
      texture = tex;
      textured = (tex != null);
      
      if (textured) {
        modelCount += 1;
      } else {
        untexturedCount += 1;
      }
    }
  }
  
  public static void increment(Model mdl) {
    if (mdl.hasMesh()) {
      increment(getMeshtexture);
    }
  }
  
  public static void increment(EditorTexture texture) {
    if (texture != null) {
      modelCount += 1;
    } else
      untexturedCount += 1;
    TextureInfoPanel.instance().updateCount(texture);
  }
  
  public static void decrement(Model mdl)
  {
    if (mdl.hasMesh()) {
      decrement(getMeshtexture);
    }
  }
  
  public static void decrement(EditorTexture texture) {
    if (texture != null) {
      modelCount -= 1;
    } else
      untexturedCount -= 1;
    TextureInfoPanel.instance().updateCount(texture);
  }
  
  public static void update(double deltaTime)
  {
    for (EditorTexture t : textureList) {
      t.updateScroll(deltaTime);
    }
  }
  
  public static int bindBufferedImage(BufferedImage image) {
    ByteBuffer buffer = createByteBuffer(image);
    
    int textureID = GL11.glGenTextures();
    GL11.glBindTexture(3553, textureID);
    
    GL11.glTexParameteri(3553, 10242, 10497);
    GL11.glTexParameteri(3553, 10243, 10497);
    
    GL11.glTexParameteri(3553, 10241, 9728);
    GL11.glTexParameteri(3553, 10240, 9728);
    
    GL11.glTexImage2D(3553, 0, 32856, image.getWidth(), image
      .getHeight(), 0, 6408, 5121, buffer);
    return textureID;
  }
  
  public static ByteBuffer createByteBuffer(BufferedImage image)
  {
    int[] pixels = new int[image.getWidth() * image.getHeight()];
    image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
    
    ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
    
    for (int y = 0; y < image.getHeight(); y++) {
      for (int x = 0; x < image.getWidth(); x++) {
        int pixel = pixels[((image.getHeight() - 1 - y) * image.getWidth() + x)];
        buffer.put((byte)(pixel >> 16 & 0xFF));
        buffer.put((byte)(pixel >> 8 & 0xFF));
        buffer.put((byte)(pixel & 0xFF));
        buffer.put((byte)(pixel >> 24 & 0xFF));
      }
    }
    
    buffer.flip();
    
    return buffer;
  }
  
  public TextureManager() {}
}
