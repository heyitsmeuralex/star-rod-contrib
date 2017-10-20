package data.texture;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import main.InputFileException;
import util.IOUtils;
import util.Logger;







public class Texture
{
  public static final int IMG = 1;
  public static final int AUX = 0;
  public static final int WRAP_REPEAT = 0;
  public static final int WRAP_MIRROR = 1;
  public static final int WRAP_CLAMP = 2;
  public final String name;
  public Image img;
  public Image aux;
  public List<Image> mipmapList;
  public int extra;
  public boolean hasAux = false;
  public boolean hasMipmaps = false;
  
  public int[] hWrap;
  
  public int[] vWrap;
  public boolean filter;
  public int auxCombine;
  private static final String IMG_KEY = "img";
  private static final String AUX_KEY = "aux";
  private static final String MIPMAP_KEY = "mipmaps";
  private static final String FILTER_KEY = "filter";
  private static final String COMBINE_KEY = "combine";
  private static final String YES = "yes";
  private static final String NO = "no";
  
  private Texture(String name)
  {
    this.name = name;
  }
  



  public Texture(ByteBuffer bb)
    throws IOException
  {
    name = IOUtils.readString(bb, 32);
    
    int[] width = new int[2];
    width[0] = bb.getShort();
    width[1] = bb.getShort();
    
    int[] height = new int[2];
    height[0] = bb.getShort();
    height[1] = bb.getShort();
    
    extra = (bb.getShort() & 0xF);
    
    switch (extra) {
    case 0: 
      break;
    case 1:  hasMipmaps = true; break;
    case 2: case 3: 
      hasAux = true;
    }
    
    auxCombine = (bb.get() & 0xFF);
    
    System.out.println(name + " combine = " + auxCombine);
    
    int[] type = splitByte(bb.get());
    int[] depth = splitByte(bb.get());
    hWrap = splitByte(bb.get());
    vWrap = splitByte(bb.get());
    byte filterMode = bb.get();
    filter = (filterMode == 2);
    assert ((filterMode == 0) || (filterMode == 2));
    


    switch (auxCombine)
    {
    }
    
    




    ImageFormat fmt = ImageFormat.get(type[1], depth[1]);
    int mmWidth;
    int pos;
    switch (extra)
    {

    case 0: 
      img = new Image(fmt, height[1], width[1]);
      img.readImage(bb, true);
      
      assert (height[0] == 0);
      assert (width[0] == 0);
      assert (type[0] == 0);
      assert (depth[0] == 0);
      assert (vWrap[0] == 0);
      assert (vWrap[0] == 0);
      
      if (img.format.type == 2) {
        img.readPalette(bb);
      }
      break;
    case 1: 
      img = new Image(fmt, height[1], width[1]);
      img.readImage(bb, true);
      
      assert (height[0] == 0);
      assert (width[0] == 0);
      assert (type[0] == 0);
      assert (depth[0] == 0);
      assert (vWrap[0] == 0);
      assert (vWrap[0] == 0);
      
      mipmapList = new LinkedList();
      
      int divisor = 2;
      if (width[1] >= 32 >> depth[1])
      {


        while (width[1] / divisor > 0)
        {
          int mmHeight = height[1] / divisor;
          mmWidth = width[1] / divisor;
          
          Image mipmap = new Image(fmt, mmHeight, mmWidth);
          mipmap.readImage(bb, true);
          mipmapList.add(mipmap);
          
          divisor <<= 1;
          if (width[1] / divisor < 16 >> depth[1]) {
            break;
          }
        }
      }
      if (img.format.type == 2)
      {
        pos = bb.position();
        img.readPalette(bb);
        
        for (Image mipmap : mipmapList)
        {
          bb.position(pos);
          mipmap.readPalette(bb);
        } }
      break;
    

    case 2: 
      assert (height[0] == 0);
      assert (width[0] == 0);
      
      height[1] /= 2;
      width[0] = width[1];
      height[0] = height[1];
      
      img = new Image(fmt, height[1], width[1]);
      img.readImage(bb, true);
      
      aux = new Image(fmt, height[0], width[0]);
      aux.readImage(bb, true);
      
      if (img.format.type == 2)
      {
        int pos = bb.position();
        img.readPalette(bb);
        bb.position(pos);
        aux.readPalette(bb); }
      break;
    

    case 3: 
      img = new Image(fmt, height[1], width[1]);
      img.readImage(bb, true);
      
      if (img.format.type == 2) {
        img.readPalette(bb);
      }
      aux = new Image(ImageFormat.get(type[0], depth[0]), height[0], width[0]);
      aux.readImage(bb, true);
      
      if (aux.format.type == 2) {
        aux.readPalette(bb);
      }
      break;
    default: 
      throw new RuntimeException("Invalid setting for extra data.");
    }
    
  }
  
  private int[] splitByte(byte b) {
    int[] parts = new int[2];
    parts[0] = (b >> 4);
    parts[1] = (b & 0xF);
    return parts;
  }
  
  public void write(ByteBuffer bb)
  {
    int start = bb.position();
    if (name.length() >= 32)
      throw new RuntimeException("Texture name is too long: " + name);
    bb.put(name.getBytes());
    
    bb.position(start + 32);
    
    bb.putShort((short)(extra == 3 ? aux.width : 0));
    bb.putShort((short)img.width);
    bb.putShort((short)(extra == 3 ? aux.height : 0));
    bb.putShort((short)(extra == 2 ? 2 * img.height : img.height));
    bb.put((byte)0);
    bb.put((byte)extra);
    bb.put((byte)auxCombine);
    
    int fmt = img.format.type;
    if (extra == 3)
      fmt |= aux.format.type << 4;
    bb.put((byte)fmt);
    
    int depth = img.format.depth;
    if (extra == 3)
      depth |= aux.format.depth << 4;
    bb.put((byte)depth);
    
    bb.put((byte)(hWrap[0] << 4 | hWrap[1]));
    bb.put((byte)(vWrap[0] << 4 | vWrap[1]));
    bb.put((byte)(filter ? 2 : 0));
    
    img.putRaster(bb, true);
    
    Logger.log("Writing texture: " + name);
    
    switch (extra)
    {
    case 0: 
      if (img.format.type == 2)
        img.palette.put(bb);
      break;
    case 1: 
      for (Image mm : mipmapList)
        mm.putRaster(bb, true);
      if (img.format.type == 2)
        img.palette.put(bb);
      break;
    case 2: 
      aux.putRaster(bb, true);
      if (img.format.type == 2)
        img.palette.put(bb);
      break;
    case 3: 
      if (img.format.type == 2)
        img.palette.put(bb);
      aux.putRaster(bb, true);
      if (aux.format.type == 2) {
        aux.palette.put(bb);
      }
      break;
    }
  }
  
  public int getFileSize() {
    int fileSize = 48 + img.raster.limit();
    switch (1.$SwitchMap$data$texture$ImageFormat[img.format.ordinal()]) {
    case 1: 
      fileSize += 32; break;
    case 2:  fileSize += 512; break;
    }
    
    
    switch (extra)
    {
    case 1: 
      for (Image mm : mipmapList)
        fileSize += raster.limit();
      break;
    case 2: 
      fileSize += aux.raster.limit();
      break;
    case 3: 
      fileSize += aux.raster.limit();
      switch (1.$SwitchMap$data$texture$ImageFormat[aux.format.ordinal()]) {
      case 1: 
        fileSize += 32; break;
      case 2:  fileSize += 512;
      }
      
      break;
    }
    
    return fileSize;
  }
  
  public void print(PrintWriter pw)
  {
    pw.println("tex: " + name);
    pw.println("{");
    
    pw.println("\timg: " + name + ".png");
    pw.println("\t{");
    pw.println("\t\tformat: " + img.format);
    pw.println("\t\thwrap: " + getWrapName(null, name, hWrap[1]));
    pw.println("\t\tvwrap: " + getWrapName(null, name, vWrap[1]));
    pw.println("\t}");
    
    switch (extra) {
    case 0: 
      break;
    case 1: 
      pw.println("\tmipmaps: yes");
      break;
    case 2: 
      pw.printf("\t%s: %s_AUX.png\r\n", new Object[] { "aux", name });
      pw.println("\t{");
      pw.println("\t\tformat: shared");
      pw.println("\t\thwrap: " + getWrapName(null, name, hWrap[0]));
      pw.println("\t\tvwrap: " + getWrapName(null, name, vWrap[0]));
      pw.println("\t}");
      break;
    case 3: 
      pw.printf("\t%s: %s_AUX.png\r\n", new Object[] { "aux", name });
      pw.println("\t{");
      pw.println("\t\tformat: " + aux.format);
      pw.println("\t\thwrap: " + getWrapName(null, name, hWrap[0]));
      pw.println("\t\tvwrap: " + getWrapName(null, name, vWrap[0]));
      pw.println("\t}");
    }
    
    
    pw.printf("\t%s: %s\r\n", new Object[] { "filter", filter ? "yes" : "no" });
    pw.printf("\t%s: %X\r\n", new Object[] { "combine", Integer.valueOf(auxCombine) });
    
    pw.println("}");
    pw.println();
  }
  
  public static Texture parseTexture(File archiveFile, String dir, String name, List<String> lines) throws IOException
  {
    Texture tx = new Texture(name);
    String imgName = null;
    String auxName = null;
    String imgFormatName = null;
    String auxFormatName = null;
    hWrap = new int[2];
    vWrap = new int[2];
    boolean convertImg = false;
    boolean convertAux = false;
    

    Iterator<String> iter = lines.iterator();
    while (iter.hasNext())
    {
      String line = (String)iter.next();
      String[] tokens = splitLine(archiveFile, name, line);
      
      switch (tokens[0])
      {
      case "img": 
        imgName = tokens[1];
        if (!iter.hasNext())
          throw new InputFileException(archiveFile, "(%s) Incomplete texture description.", new Object[] { name });
        line = (String)iter.next();
        if (!line.equals("{"))
          throw new InputFileException(archiveFile, "(%s) Invalid texture description.", new Object[] { name });
        List<String> imgLines = new LinkedList();
        while (!(line = (String)iter.next()).equals("}"))
        {
          imgLines.add(line);
          if (!iter.hasNext())
            throw new InputFileException(archiveFile, "(%s) Incomplete texture description.", new Object[] { name });
        }
        ImageAttributes attr = parseImage(archiveFile, tx, imgLines);
        imgFormatName = format;
        if (convert != null)
          convertImg = convert.equals("yes");
        if (hWrap != null)
          hWrap[1] = getWrapMode(archiveFile, name, hWrap);
        if (vWrap != null) {
          vWrap[1] = getWrapMode(archiveFile, name, vWrap);
        }
        break;
      case "aux": 
        auxName = tokens[1];
        if (!iter.hasNext())
          throw new InputFileException(archiveFile, "(%s) Incomplete texture description.", new Object[] { name });
        line = (String)iter.next();
        if (!line.equals("{"))
          throw new InputFileException(archiveFile, "(%s) Invalid texture description.", new Object[] { name });
        List<String> auxLines = new LinkedList();
        while (!(line = (String)iter.next()).equals("}"))
        {
          auxLines.add(line);
          if (!iter.hasNext())
            throw new InputFileException(archiveFile, "(%s) Incomplete texture description.", new Object[] { name });
        }
        ImageAttributes attr = parseImage(archiveFile, tx, auxLines);
        auxFormatName = format;
        if (convert != null)
          convertAux = convert.equals("yes");
        if (hWrap != null)
          hWrap[0] = getWrapMode(archiveFile, name, hWrap);
        if (vWrap != null) {
          vWrap[0] = getWrapMode(archiveFile, name, vWrap);
        }
        break;
      case "mipmaps": 
        if (tokens[1].equals("yes"))
        {
          hasMipmaps = true;
          mipmapList = new LinkedList();
        }
        
        break;
      case "filter": 
        if (tokens[1].equals("yes")) {
          filter = true;
        }
        break;
      case "combine": 
        auxCombine = ((byte)Short.parseShort(tokens[1], 16));
      }
      
    }
    
    if (imgFormatName == null) {
      throw new InputFileException(archiveFile, "(%s) Texture does not specify an image.", new Object[] { name });
    }
    ImageFormat imgFormat = ImageFormat.getFormat(imgFormatName);
    if (imgFormat == null) {
      throw new InputFileException(archiveFile, "(%s) Unknown image format: %s", new Object[] { name, imgFormatName });
    }
    ImageFormat auxFormat = null;
    if (auxFormatName != null)
    {
      if (auxFormatName.equals("shared"))
      {
        extra = 2;
        auxFormat = imgFormat;
      }
      else
      {
        extra = 3;
        auxFormat = ImageFormat.getFormat(auxFormatName);
        if (auxFormat == null) {
          throw new InputFileException(archiveFile, "(%s) Unknown aux format: %s", new Object[] { name, auxFormatName });
        }
      }
      hasAux = true;
    }
    
    if (hasMipmaps)
    {
      extra = 1;
      if (hasAux) {
        throw new InputFileException(archiveFile, "(%s) Texture cannot have both mipmaps and aux.", new Object[] { name });
      }
    }
    img = Image.load(dir + imgName, imgFormat, convertImg);
    
    if (hasAux) {
      aux = Image.load(dir + auxName, auxFormat, convertAux);
    }
    if (hasMipmaps)
    {
      int divisor = 2;
      if (img.width >= 32 >> img.format.depth)
      {


        while (img.width / divisor > 0)
        {
          int mmHeight = img.height / divisor;
          int mmWidth = img.width / divisor;
          
          if (imgName.contains(".")) {
            imgName = imgName.substring(0, imgName.indexOf("."));
          }
          String mmName = imgName + "_MIPMAP_" + (mipmapList.size() + 1) + ".png";
          Image mipmap = Image.load(dir + mmName, imgFormat, convertImg);
          
          if (height != mmHeight) {
            throw new InputFileException(archiveFile, "%s has incorrect height: %s instead of %s", new Object[] { mmName, Integer.valueOf(height), Integer.valueOf(mmHeight) });
          }
          if (width != mmWidth) {
            throw new InputFileException(archiveFile, "%s has incorrect width: %s instead of %s", new Object[] { mmName, Integer.valueOf(width), Integer.valueOf(mmWidth) });
          }
          mipmapList.add(mipmap);
          
          divisor <<= 1;
          if (img.width / divisor < 16 >> img.format.depth) {
            break;
          }
        }
      }
    }
    return tx;
  }
  
  private static int getWrapMode(File archiveFile, String texname, String wrap)
  {
    switch (wrap) {
    case "repeat": 
      return 0;
    case "mirror":  return 1;
    case "clamp":  return 2;
    }
    throw new InputFileException(archiveFile, "(%s) has invalid wrap mode: %s", new Object[] { texname, wrap });
  }
  

  private static String getWrapName(File archiveFile, String texname, int id)
  {
    switch (id) {
    case 0: 
      return "repeat";
    case 1:  return "mirror";
    case 2:  return "clamp";
    }
    throw new InputFileException(archiveFile, "(%s) has invalid wrap mode: %s", new Object[] { texname, Integer.valueOf(id) });
  }
  

  private static class ImageAttributes
  {
    public String format = null;
    public String convert = null;
    public String hWrap = null;
    public String vWrap = null;
    
    private ImageAttributes() {}
  }
  
  private static ImageAttributes parseImage(File archiveFile, Texture tx, List<String> lines) { ImageAttributes attr = new ImageAttributes(null);
    
    for (String line : lines)
    {
      String[] tokens = splitLine(archiveFile, name, line);
      switch (tokens[0])
      {
      case "format": 
        if (format != null)
          throw new InputFileException(archiveFile, "Format specified more than once (%s)", new Object[] { name });
        format = tokens[1];
        break;
      case "convert": 
        if (convert != null)
          throw new InputFileException(archiveFile, "Convert specified more than once (%s)", new Object[] { name });
        convert = tokens[1];
        break;
      case "hwrap": 
        if (hWrap != null)
          throw new InputFileException(archiveFile, "hWrap specified more than once (%s)", new Object[] { name });
        hWrap = tokens[1];
        break;
      case "vwrap": 
        if (vWrap != null)
          throw new InputFileException(archiveFile, "vWrap specified more than once (%s)", new Object[] { name });
        vWrap = tokens[1];
      }
      
    }
    
    if (format == null) {
      throw new InputFileException(archiveFile, "Format was not specified (%s)", new Object[] { name });
    }
    return attr;
  }
  
  private static String[] splitLine(File archiveFile, String texName, String line)
  {
    String[] tokens = line.split(":\\s*");
    if (tokens.length != 2)
      throw new InputFileException(archiveFile, "Invalid line in texture file: %s (%s)", new Object[] { line, texName });
    return tokens;
  }
  
  public static float getScale(int shift)
  {
    switch (shift) {
    default: 
      return 1.0F;
    case 1:  return 0.5F;
    case 2:  return 0.25F;
    case 3:  return 0.125F;
    case 4:  return 0.0625F;
    case 5:  return 0.03125F;
    case 6:  return 0.015625F;
    case 7:  return 0.0078125F;
    case 8:  return 0.00390625F;
    case 9:  return 0.001953125F;
    case 10:  return 9.765625E-4F;
    case 11:  return 32.0F;
    case 12:  return 16.0F;
    case 13:  return 8.0F;
    case 14:  return 4.0F; }
    return 2.0F;
  }
}
