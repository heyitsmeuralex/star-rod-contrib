package data.sprites;

import data.texture.Image;
import data.texture.ImageConverter;
import data.texture.ImageFormat;
import data.texture.Palette;
import editor.render.TextureManager;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javax.swing.ImageIcon;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import main.DevContext;
import main.Directories;
import main.StarRodDev;
import org.apache.commons.io.FileUtils;
import org.lwjgl.opengl.GL11;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import util.IOUtils;
import util.Logger;
import util.Priority;


























public class Sprite
{
  public String name;
  private int a;
  private int b;
  public List<SpriteAnimation> animations;
  private Image[] rasters;
  private String[] rasterFilenames;
  private int[] defaultPalettes;
  private Palette[] palettes;
  private String[] paletteFilenames;
  public transient ArrayList<ImagePreview> imagePreviews;
  private transient int[][] glTexIDs;
  
  public static void main(String[] args)
    throws IOException
  {
    DevContext.initialize();
    
    convertAll();
    testBinary();
    testXml();
    
    validateAll();
    
    DevContext.exit();
  }
  
  private static void validateAll() throws IOException
  {
    for (int i = 1; i <= 233; i++)
    {
      Sprite spr = readBinary(new File(String.format("%s%02X", new Object[] { Directories.DUMP_SPRITE_RAW, Integer.valueOf(i) })));
      
      System.out.printf("Sprite %02X\r\n", new Object[] { Integer.valueOf(i) });
      validate(spr);
    }
  }
  
  private static void testBinary() throws IOException
  {
    for (int i = 1; i <= 233; i++)
    {
      String name = String.format("%02X", new Object[] { Integer.valueOf(i) });
      File f1 = new File(Directories.DUMP_SPRITE_RAW + name);
      File f2 = new File(Directories.DUMP_SPRITE_RAW + "rec" + name);
      
      Sprite spr = readBinary(f1);
      spr.writeBinary(f2);
      
      assert (f1.length() == f2.length());
      
      ByteBuffer bb1 = IOUtils.getDirectBuffer(f1);
      ByteBuffer bb2 = IOUtils.getDirectBuffer(f2);
      f2.delete();
      for (; 
          bb1.hasRemaining(); 
          
          throw new AssertionError()) label142: if (($assertionsDisabled) || (bb1.get() == bb2.get())) {
          break label142;
        }
    }
  }
  
  private static void testXml() throws IOException {
    for (int i = 1; i <= 233; i++)
    {
      String name = String.format("%02X", new Object[] { Integer.valueOf(i) });
      File raw = new File(Directories.DUMP_SPRITE_RAW + name);
      File xml = new File(Directories.DUMP_SPRITE_SRC + name + "/SpriteSheet.xml");
      File out = new File(Directories.DUMP_SPRITE_RAW + "rec" + name);
      
      System.out.print("Testing sprite " + name + "...");
      
      Sprite spr = readXML(xml);
      spr.writeBinary(out);
      
      assert (raw.length() == out.length());
      
      ByteBuffer bb1 = IOUtils.getDirectBuffer(raw);
      ByteBuffer bb2 = IOUtils.getDirectBuffer(out);
      out.delete();
      
      assert (bb1.equals(bb2));
      System.out.println(" passed.");
    }
  }
  
  public static void convertAll() throws IOException
  {
    for (int i = 1; i <= 233; i++)
    {
      String spriteName = String.format("%02X", new Object[] { Integer.valueOf(i) });
      File source = new File(Directories.DUMP_SPRITE_RAW + spriteName);
      File spriteDir = new File(Directories.DUMP_SPRITE_SRC + String.format("%02X/", new Object[] { Integer.valueOf(i) }));
      File xmlFile = new File(spriteDir.getPath() + "/SpriteSheet.xml");
      
      Logger.log("Converting sprite " + spriteName + ".", Priority.MILESTONE);
      Sprite spr = readBinary(source);
      FileUtils.forceMkdir(spriteDir);
      spr.dumpImages(spriteDir);
      spr.writeXML(xmlFile);
    }
  }
  

  private Sprite() {}
  

  public static final class ImagePreview
  {
    final int index;
    
    String filename;
    
    ImageIcon icon;
    

    public ImagePreview(int index)
    {
      this.index = index;
    }
  }
  









  public static Sprite readBinary(File binFile)
    throws IOException
  {
    ByteBuffer bb = IOUtils.getDirectBuffer(binFile);
    Sprite sprite = new Sprite();
    

    int imageListOffset = bb.getInt();
    int paletteListOffset = bb.getInt();
    a = bb.getInt();
    b = bb.getInt();
    List<Integer> animationOffsets = readOffsetList(bb);
    animations = new ArrayList(animationOffsets.size());
    
    bb.position(imageListOffset);
    List<Integer> imageOffsetList = readOffsetList(bb);
    rasters = new Image[imageOffsetList.size()];
    rasterFilenames = new String[imageOffsetList.size()];
    defaultPalettes = new int[imageOffsetList.size()];
    
    bb.position(paletteListOffset);
    List<Integer> paletteOffsetList = readOffsetList(bb);
    palettes = new Palette[paletteOffsetList.size()];
    paletteFilenames = new String[paletteOffsetList.size()];
    

    for (int i = 0; i < paletteOffsetList.size(); i++)
    {
      bb.position(((Integer)paletteOffsetList.get(i)).intValue());
      
      short[] pal = new short[16];
      for (int j = 0; j < 16; j++) {
        pal[j] = bb.getShort();
      }
      palettes[i] = new Palette(pal);
    }
    
    int paletteIndex;
    for (int i = 0; i < imageOffsetList.size(); i++)
    {
      bb.position(((Integer)imageOffsetList.get(i)).intValue());
      int rasterOffset = bb.getInt();
      int width = bb.get() & 0xFF;
      int height = bb.get() & 0xFF;
      paletteIndex = bb.get();
      int unknown = bb.get();
      assert (unknown == -1);
      
      bb.position(rasterOffset);
      rasters[i] = new Image(ImageFormat.CI_4, height, width);
      rasters[i].readImage(bb, false);
      defaultPalettes[i] = paletteIndex;
    }
    

    for (i = animationOffsets.iterator(); i.hasNext();) { int animOffset = ((Integer)i.next()).intValue();
      
      SpriteAnimation anim = new SpriteAnimation(sprite);
      
      bb.position(animOffset);
      List<Integer> componentOffsets = readOffsetList(bb);
      
      for (paletteIndex = componentOffsets.iterator(); paletteIndex.hasNext();) { int compOffset = ((Integer)paletteIndex.next()).intValue();
        
        SpriteComponent comp = new SpriteComponent(anim);
        Queue<Short> cmdList = new LinkedList();
        
        bb.position(compOffset);
        int sequenceOffset = bb.getInt();
        short sequenceLength = bb.getShort();
        posx = bb.getShort();
        posy = bb.getShort();
        posz = bb.getShort();
        
        bb.position(sequenceOffset);
        for (int i = 0; i < sequenceLength / 2; i++)
        {
          short cmd = bb.getShort();
          cmdList.add(Short.valueOf(cmd));
        }
        
        comp.generateCommands(cmdList);
        componentList.add(comp);
      }
      
      animations.add(anim);
    }
    
    return sprite;
  }
  
  public void writeBinary(File binFile) throws IOException
  {
    if (binFile.exists()) {
      binFile.delete();
    }
    RandomAccessFile raf = new RandomAccessFile(binFile, "rw");
    raf.setLength(262144L);
    raf.seek(16L);
    

    raf.skipBytes(4 * (animations.size() + 1));
    int[] animationOffsets = new int[animations.size()];
    SpriteComponent comp;
    int commandListOffset;
    for (int i = 0; i < animations.size(); i++)
    {
      animationOffsets[i] = ((int)raf.getFilePointer());
      SpriteAnimation anim = (SpriteAnimation)animations.get(i);
      

      raf.skipBytes(4 * (componentList.size() + 1));
      
      int[] componentOffsets = new int[componentList.size()];
      
      for (int j = 0; j < componentList.size(); j++)
      {
        comp = (SpriteComponent)componentList.get(j);
        
        commandListOffset = (int)raf.getFilePointer();
        cmdList = comp.getCommandSequence();
        for (Iterator localIterator = cmdList.iterator(); localIterator.hasNext();) { short s = ((Short)localIterator.next()).shortValue();
          raf.writeShort(s); }
        if (cmdList.size() % 2 == 1) {
          raf.writeShort(0);
        }
        componentOffsets[j] = ((int)raf.getFilePointer());
        raf.writeInt(commandListOffset);
        raf.writeShort(2 * cmdList.size());
        raf.writeShort(posx);
        raf.writeShort(posy);
        raf.writeShort(posz);
      }
      
      int nextAnimation = (int)raf.getFilePointer();
      raf.seek(animationOffsets[i]);
      comp = componentOffsets;commandListOffset = comp.length; for (List<Short> cmdList = 0; cmdList < commandListOffset; cmdList++) { int v = comp[cmdList];
        raf.writeInt(v); }
      raf.writeInt(-1);
      raf.seek(nextAnimation);
    }
    

    if ((raf.getFilePointer() & 0x7) == 4L) {
      raf.writeInt(0);
    }
    
    int[] paletteOffsets = new int[palettes.length];
    for (int i = 0; i < palettes.length; i++)
    {
      paletteOffsets[i] = ((int)raf.getFilePointer());
      palettes[i].write(raf);
    }
    

    int[] imageOffsets = new int[rasters.length];
    int rasterOffset; for (int i = 0; i < rasters.length; i++)
    {
      rasterOffset = (int)raf.getFilePointer();
      rasters[i].writeRaster(raf, false);
      
      imageOffsets[i] = ((int)raf.getFilePointer());
      raf.writeInt(rasterOffset);
      raf.write(rasters[i].width);
      raf.write(rasters[i].height);
      raf.write(defaultPalettes[i]);
      raf.write(-1);
    }
    

    int rasterOffsetListOffset = (int)raf.getFilePointer();
    int i; for (i : imageOffsets)
      raf.writeInt(i);
    raf.writeInt(-1);
    

    int paletteOffsetListOffset = (int)raf.getFilePointer();
    for (int i : paletteOffsets)
      raf.writeInt(i);
    raf.writeInt(-1);
    

    raf.setLength(raf.getFilePointer());
    
    raf.seek(0L);
    raf.writeInt(rasterOffsetListOffset);
    raf.writeInt(paletteOffsetListOffset);
    raf.writeInt(a);
    raf.writeInt(b);
    
    for (int i : animationOffsets)
      raf.writeInt(i);
    raf.writeInt(-1);
    
    raf.close();
  }
  



































































































  public static Sprite readXML(File xmlFile)
    throws IOException
  {
    Sprite sprite = new Sprite();
    
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(xmlFile);
      
      document.getDocumentElement().normalize();
      Element rootElement = document.getDocumentElement();
      if (rootElement.hasAttribute("a"))
        a = ((int)Long.parseLong(rootElement.getAttribute("a"), 16));
      if (rootElement.hasAttribute("b")) {
        b = ((int)Long.parseLong(rootElement.getAttribute("b"), 16));
      }
      NodeList nodes = document.getElementsByTagName("PaletteList");
      if (nodes.getLength() > 1) throw new RuntimeException(xmlFile.toString() + " contains multiple PaletteLists.");
      if (nodes.getLength() < 1) throw new RuntimeException(xmlFile.toString() + " is missing PaletteList.");
      Element paletteListElement = (Element)nodes.item(0);
      
      nodes = paletteListElement.getElementsByTagName("Palette");
      if (nodes.getLength() < 1) throw new RuntimeException(xmlFile.toString() + " has an empty PaletteList.");
      palettes = new Palette[nodes.getLength()];
      paletteFilenames = new String[nodes.getLength()];
      for (int i = 0; i < nodes.getLength(); i++)
      {
        Element paletteElement = (Element)nodes.item(i);
        if (!paletteElement.hasAttribute("src")) throw new RuntimeException(xmlFile.toString() + " Palette is missing src attribute.");
        paletteFilenames[i] = paletteElement.getAttribute("src");
        if (!paletteElement.hasAttribute("id")) throw new RuntimeException(xmlFile.toString() + " Palette is missing id attribute.");
        int id = Integer.parseInt(paletteElement.getAttribute("id"), 16);
        if (id != i) { throw new RuntimeException(xmlFile.toString() + " Palette id is out of order.");
        }
      }
      nodes = document.getElementsByTagName("RasterList");
      if (nodes.getLength() > 1) throw new RuntimeException(xmlFile.toString() + " contains multiple RasterLists.");
      if (nodes.getLength() < 1) throw new RuntimeException(xmlFile.toString() + " is missing RasterList.");
      Element rasterListElement = (Element)nodes.item(0);
      
      nodes = rasterListElement.getElementsByTagName("Raster");
      if (nodes.getLength() < 1) throw new RuntimeException(xmlFile.toString() + " has an empty RasterList.");
      rasters = new Image[nodes.getLength()];
      rasterFilenames = new String[nodes.getLength()];
      defaultPalettes = new int[nodes.getLength()];
      for (int i = 0; i < nodes.getLength(); i++)
      {
        Element rasterElement = (Element)nodes.item(i);
        if (!rasterElement.hasAttribute("src")) throw new RuntimeException(xmlFile.toString() + " Raster is missing src attribute.");
        rasterFilenames[i] = rasterElement.getAttribute("src");
        if (!rasterElement.hasAttribute("palette")) throw new RuntimeException(xmlFile.toString() + " Raster is missing palette attribute.");
        defaultPalettes[i] = Integer.parseInt(rasterElement.getAttribute("palette"), 16);
        if (!rasterElement.hasAttribute("id")) throw new RuntimeException(xmlFile.toString() + " Raster is missing id attribute.");
        int id = Integer.parseInt(rasterElement.getAttribute("id"), 16);
        if (id != i) { throw new RuntimeException(xmlFile.toString() + " Raster id is out of order.");
        }
      }
      nodes = document.getElementsByTagName("AnimationList");
      if (nodes.getLength() > 1) throw new RuntimeException(xmlFile.toString() + " contains multiple AnimationLists.");
      if (nodes.getLength() < 1) throw new RuntimeException(xmlFile.toString() + " is missing AnimationList.");
      Element animationListElement = (Element)nodes.item(0);
      
      nodes = animationListElement.getElementsByTagName("Animation");
      if (nodes.getLength() < 1) throw new RuntimeException(xmlFile.toString() + " has an empty AnimationList.");
      animations = new ArrayList(nodes.getLength());
      for (int i = 0; i < nodes.getLength(); i++)
      {
        SpriteAnimation anim = new SpriteAnimation(sprite);
        animations.add(anim);
        
        NodeList components = ((Element)nodes.item(i)).getElementsByTagName("Component");
        if (components.getLength() < 1) throw new RuntimeException(xmlFile.toString() + " Animation has no Components.");
        for (int j = 0; j < components.getLength(); j++)
        {
          Element compElement = (Element)components.item(j);
          SpriteComponent comp = new SpriteComponent(anim);
          componentList.add(comp);
          comp.readXML(compElement, xmlFile.toString());
        }
      }
    } catch (Throwable t) {
      StarRodDev.displayStackTrace(t);
    }
    
    String xmlDir = xmlFile.getParent() + "/";
    
    imagePreviews = new ArrayList(rasterFilenames.length);
    for (int i = 0; i < rasterFilenames.length; i++) {
      imagePreviews.add(new ImagePreview(i));
    }
    for (int i = 0; i < rasterFilenames.length; i++)
    {
      String filename = rasterFilenames[i];
      rasters[i] = Image.load(xmlDir + filename, ImageFormat.CI_4);
      imagePreviews.get(i)).filename = filename;
    }
    
    for (int i = 0; i < paletteFilenames.length; i++)
    {
      String filename = paletteFilenames[i];
      palettes[i] = loadFileCI_4palette;
    }
    
    return sprite;
  }
  
  public boolean writeXML(File xmlFile)
  {
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.newDocument();
      
      Element rootElement = doc.createElement("SpriteSheet");
      rootElement.setAttribute("a", String.format("%X", new Object[] { Integer.valueOf(a) }));
      rootElement.setAttribute("b", String.format("%X", new Object[] { Integer.valueOf(b) }));
      doc.appendChild(rootElement);
      
      Element paletteListElement = doc.createElement("PaletteList");
      rootElement.appendChild(paletteListElement);
      for (int i = 0; i < palettes.length; i++)
      {
        Element paletteElement = doc.createElement("Palette");
        paletteElement.setAttribute("id", String.format("%02X", new Object[] { Integer.valueOf(i) }));
        paletteElement.setAttribute("src", paletteFilenames[i]);
        paletteListElement.appendChild(paletteElement);
      }
      
      Element rasterListElement = doc.createElement("RasterList");
      rootElement.appendChild(rasterListElement);
      Element rasterElement; for (int i = 0; i < rasters.length; i++)
      {
        rasterElement = doc.createElement("Raster");
        rasterElement.setAttribute("id", String.format("%02X", new Object[] { Integer.valueOf(i) }));
        rasterElement.setAttribute("palette", String.format("%02X", new Object[] { Integer.valueOf(defaultPalettes[i]) }));
        rasterElement.setAttribute("src", rasterFilenames[i]);
        rasterListElement.appendChild(rasterElement);
      }
      
      Element animationListElement = doc.createElement("AnimationList");
      rootElement.appendChild(animationListElement);
      for (SpriteAnimation animation : animations)
      {
        animationElement = doc.createElement("Animation");
        animationListElement.appendChild(animationElement);
        
        for (SpriteComponent component : componentList)
        {
          Element componentElement = doc.createElement("Component");
          animationElement.appendChild(componentElement);
          component.writeXML(doc, componentElement);
        }
      }
      Element animationElement;
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty("indent", "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      
      DOMSource source = new DOMSource(doc);
      StreamResult dest = new StreamResult(xmlFile);
      

      transformer.transform(source, dest);
    }
    catch (Throwable t) {
      StarRodDev.displayStackTrace(t);
      return false;
    }
    return true;
  }
  

  private static List<Integer> readOffsetList(ByteBuffer bb)
  {
    List<Integer> offsetList = new LinkedList();
    int v; while ((v = bb.getInt()) != -1)
      offsetList.add(Integer.valueOf(v));
    return offsetList;
  }
  
  public void dumpImages(File dir) throws IOException
  {
    for (int i = 0; i < rasters.length; i++)
    {
      Palette palette = palettes[defaultPalettes[i]];
      rasterFilenames[i] = String.format("Raster_%02X.png", new Object[] { Integer.valueOf(i) });
      
      rasters[i].palette = palette;
      rasters[i].savePNG(dir.toString() + "/" + rasterFilenames[i]);
    }
    
    for (int i = 0; i < palettes.length; i++)
    {

      int rasterIndex = 0;
      for (int j = 0; j < rasters.length; j++)
      {
        if (defaultPalettes[j] == i)
        {
          rasterIndex = j;
          break;
        }
      }
      
      Palette palette = palettes[i];
      paletteFilenames[i] = String.format("Palette_%02X.png", new Object[] { Integer.valueOf(i) });
      rasters[rasterIndex].palette = palette;
      rasters[rasterIndex].savePNG(dir.toString() + "/" + paletteFilenames[i]);
    }
  }
  
  public void loadTextures()
  {
    glTexIDs = new int[rasters.length][palettes.length];
    for (int i = 0; i < rasters.length; i++)
    {
      for (int j = 0; j < palettes.length; j++)
      {
        rasters[i].palette = palettes[j];
        BufferedImage bimg = ImageConverter.convert(rasters[i]);
        glTexIDs[i][j] = glLoad(bimg);
        if (j == defaultPalettes[i]) {
          imagePreviews.get(i)).icon = new ImageIcon(bimg);
        }
      }
    }
  }
  
  private static int glLoad(BufferedImage bimg) {
    ByteBuffer buffer = TextureManager.createByteBuffer(bimg);
    
    int glID = GL11.glGenTextures();
    GL11.glBindTexture(3553, glID);
    
    GL11.glTexParameteri(3553, 10242, 10497);
    GL11.glTexParameteri(3553, 10243, 10497);
    
    GL11.glTexParameteri(3553, 10241, 9728);
    GL11.glTexParameteri(3553, 10240, 9728);
    
    GL11.glTexImage2D(3553, 0, 32856, bimg.getWidth(), bimg
      .getHeight(), 0, 6408, 5121, buffer);
    
    return glID;
  }
  
  public int getAnimationCount()
  {
    return animations.size();
  }
  
  public int getImageCount()
  {
    return rasters.length;
  }
  
  public int getPaletteCount()
  {
    return palettes.length;
  }
  
  public void updateAnimation(int animationID)
  {
    SpriteAnimation anim = (SpriteAnimation)animations.get(animationID);
    for (SpriteComponent comp : componentList)
    {
      comp.update();
    }
  }
  
  public void resetAnimation(int animationID)
  {
    SpriteAnimation anim = (SpriteAnimation)animations.get(animationID);
    for (SpriteComponent comp : componentList)
    {
      comp.reset();
    }
  }
  
  public void render(int animationID, int paletteOverride)
  {
    SpriteAnimation anim = (SpriteAnimation)animations.get(animationID);
    for (int i = 0; i < componentList.size(); i++)
    {
      SpriteComponent comp = (SpriteComponent)componentList.get(i);
      renderComponent(anim, comp, paletteOverride);
    }
  }
  
  public void render(int animationID, int componentID, int paletteOverride)
  {
    SpriteAnimation anim = (SpriteAnimation)animations.get(animationID);
    SpriteComponent comp = (SpriteComponent)componentList.get(componentID);
    renderComponent(anim, comp, paletteOverride);
  }
  









  private void renderComponent(SpriteAnimation anim, SpriteComponent comp, int paletteOverride)
  {
    if (imageIndex == -1) {
      return;
    }
    int paletteIndex = paletteIndex;
    if (paletteIndex == -1) {
      if (paletteOverride >= 0) {
        paletteIndex = paletteOverride;
      } else
        paletteIndex = defaultPalettes[imageIndex];
    }
    SpriteComponent parent = null;
    if (comp.hasValidParent()) {
      parent = (SpriteComponent)componentList.get(parentIndex);
    }
    int x = comp.hasValidParent() ? parent.getX() + comp.getX() : comp.getX();
    int y = comp.hasValidParent() ? parent.getY() + comp.getY() : comp.getY();
    int z = comp.hasValidParent() ? parent.getZ() + comp.getZ() : comp.getZ();
    
    GL11.glPushMatrix();
    
    GL11.glTranslatef(x, y, z);
    

    if ((rx != 0) || (ry != 0) || (rz != 0))
    {
      GL11.glRotatef(ry, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(rz, 0.0F, 0.0F, 1.0F);
      GL11.glRotatef(rx, 1.0F, 0.0F, 0.0F);
    }
    

    if ((scaleX != 100) || (scaleY != 100))
    {
      float scaleX = scaleX / 100.0F;
      float scaleY = scaleY / 100.0F;
      GL11.glScalef(scaleX, scaleY, 1.0F);
    }
    
    drawQuad(glTexIDs[imageIndex][paletteIndex], rasters[imageIndex].width, rasters[imageIndex].height, z);
    




    GL11.glPopMatrix();
  }
  
  public void drawQuad(int glTexID, int W, int H, int z)
  {
    GL11.glPolygonMode(1032, 6914);
    
    GL11.glEnable(3553);
    GL11.glBindTexture(3553, glTexID);
    
    GL11.glBegin(7);
    
    float x1 = -W / 2;
    float y1 = H;
    float x2 = W / 2;
    float y2 = 0.0F;
    
    GL11.glTexCoord2f(0.0F, 1.0F);
    GL11.glVertex3f(x1, y1, z);
    
    GL11.glTexCoord2f(0.0F, 0.0F);
    GL11.glVertex3f(x1, y2, z);
    
    GL11.glTexCoord2f(1.0F, 0.0F);
    GL11.glVertex3f(x2, y2, z);
    
    GL11.glTexCoord2f(1.0F, 1.0F);
    GL11.glVertex3f(x2, y1, z);
    
    GL11.glEnd();
  }
  
  public static void validate(Sprite spr)
  {
    for (int i = 0; i < animations.size(); i++)
    {
      SpriteAnimation anim = (SpriteAnimation)animations.get(i);
      for (int j = 0; j < componentList.size(); j++)
      {
        SpriteComponent comp = (SpriteComponent)componentList.get(j);
        
        System.out.printf("%02X.%X : ", new Object[] { Integer.valueOf(i), Integer.valueOf(j) });
        
        List<Short> sequence = comp.getCommandSequence();
        Queue<Short> cmdQueue = new LinkedList(sequence);
        
        while (!cmdQueue.isEmpty())
        {
          int pos = sequence.size() - cmdQueue.size();
          short s = ((Short)cmdQueue.poll()).shortValue();
          
          int type = s >> 12 & 0xF;
          int extra = s << 20 >> 20;
          
          System.out.printf("%04X ", new Object[] { Short.valueOf(s) });
          
          switch (type)
          {
          case 0: 
            if (!cmdQueue.isEmpty())
              System.out.print("| ");
            assert (extra > 0);
            if ((!$assertionsDisabled) && (extra > 260)) throw new AssertionError();
            break;
          case 1: 
            if ((!$assertionsDisabled) && (extra != -1) && ((extra < 0) || (extra >= spr.getImageCount()))) throw new AssertionError();
            break;
          case 2: 
            if (!cmdQueue.isEmpty())
              System.out.print("| ");
            assert (extra < sequence.size());
            if ((!$assertionsDisabled) && (extra >= pos)) throw new AssertionError();
            break;
          case 3: 
            assert ((extra == 0) || (extra == 1));
            

            short dx = ((Short)cmdQueue.poll()).shortValue();
            System.out.printf("%04X ", new Object[] { Short.valueOf(dx) });
            short dy = ((Short)cmdQueue.poll()).shortValue();
            System.out.printf("%04X ", new Object[] { Short.valueOf(dy) });
            short dz = ((Short)cmdQueue.poll()).shortValue();
            System.out.printf("%04X ", new Object[] { Short.valueOf(dz) });
            break;
          case 4: 
            assert ((extra >= 65356) && (extra <= 180));
            s = ((Short)cmdQueue.poll()).shortValue();
            System.out.printf("%04X ", new Object[] { Short.valueOf(s) });
            assert ((s >= 65356) && (s <= 180));
            s = ((Short)cmdQueue.poll()).shortValue();
            System.out.printf("%04X ", new Object[] { Short.valueOf(s) });
            if ((!$assertionsDisabled) && ((s < 65356) || (s > 180))) throw new AssertionError();
            break;
          case 5: 
            s = ((Short)cmdQueue.poll()).shortValue();
            System.out.printf("%04X ", new Object[] { Short.valueOf(s) });
            if ((!$assertionsDisabled) && (extra != 0) && (extra != 1) && (extra != 2)) throw new AssertionError();
            break;
          case 6: 
            if ((!$assertionsDisabled) && ((extra < 0) || (extra >= spr.getPaletteCount()))) throw new AssertionError();
            break;
          case 7: 
            s = ((Short)cmdQueue.poll()).shortValue();
            System.out.printf("%04X ", new Object[] { Short.valueOf(s) });
            if (!cmdQueue.isEmpty())
              System.out.print("| ");
            assert (extra < sequence.size());
            assert (extra < pos);
            if ((!$assertionsDisabled) && ((s < 0) || (s >= 25))) throw new AssertionError();
            break;
          case 8: 
            int parentType = extra >> 8 & 0xF;
            
            switch (parentType)
            {

            case 0: 
              if ((!$assertionsDisabled) && (s != Short.MIN_VALUE)) throw new AssertionError();
              break;
            case 1: 
              if ((!$assertionsDisabled) && (pos != 0)) { throw new AssertionError();
              }
              break;
            case 2: 
              break;
            default: 
              if (!$assertionsDisabled) throw new AssertionError();
              break;
            }
            
            break; default:  throw new RuntimeException(String.format("Unknown animation command: %04X", new Object[] { Short.valueOf(s) }));
          }
        }
        System.out.println();
      }
    }
    System.out.println();
  }
}
