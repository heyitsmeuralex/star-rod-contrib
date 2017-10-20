package data.texture;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import main.DevContext;
import main.Directories;
import main.InputFileException;
import org.apache.commons.io.FileUtils;
import util.IOUtils;

public class TextureArchive
{
  public static final String EXT = "txa";
  public final String name;
  public final List<Texture> textureList;
  
  public static void main(String[] args) throws IOException
  {
    DevContext.initialize();
    
    testTxa();
    DevContext.exit();
  }
  
  private static void testBinary() throws IOException
  {
    File[] assets = Directories.DUMP_MAP_RAW.toFile().listFiles();
    for (File f : assets)
      if (f.getName().endsWith("_tex"))
      {
        TextureArchive ta = new TextureArchive(f);
        String temp = Directories.DUMP_MAP_RAW + "/test/";
        ta.writeBinary(temp);
        
        ByteBuffer buf1 = IOUtils.getDirectBuffer(f);
        ByteBuffer buf2 = IOUtils.getDirectBuffer(new File(temp + name));
        
        System.out.print("Testing " + f.getName() + "...");
        buf1.rewind();
        buf2.rewind();
        assert (buf1.equals(buf2));
        System.out.println(" passed.");
      }
    System.out.println("Done.");
  }
  
  private static void testTxa() throws IOException
  {
    java.util.Collection<File> archives = IOUtils.getFilesWithExtension(Directories.MOD_IMG_TEX, "txa", true);
    for (File f : archives)
    {
      TextureArchive ta = parseText(f.getParentFile().getAbsolutePath(), f.getName().substring(0, f.getName().length() - 4));
      String temp = Directories.DUMP_MAP_RAW + "/test/";
      ta.writeBinary(temp);
      
      ByteBuffer buf1 = IOUtils.getDirectBuffer(new File(Directories.DUMP_MAP_RAW + name));
      ByteBuffer buf2 = IOUtils.getDirectBuffer(new File(temp + name));
      
      System.out.print("Testing " + f.getName() + "...");
      buf1.rewind();
      buf2.rewind();
      assert (buf1.equals(buf2));
      System.out.println(" passed.");
    }
    System.out.println("Done.");
  }
  





  private TextureArchive(String name)
  {
    this.name = name;
    textureList = new LinkedList();
  }
  


  public TextureArchive(File f)
    throws IOException
  {
    name = f.getName();
    textureList = new LinkedList();
    
    ByteBuffer fileBuffer = IOUtils.getDirectBuffer(f);
    while (fileBuffer.hasRemaining())
    {
      Texture tx = new Texture(fileBuffer);
      textureList.add(tx);
    }
  }
  
  public void dumpToDirectory(File dir) throws IOException
  {
    String subdir = dir.getPath() + "/" + name + "/";
    FileUtils.forceMkdir(new File(subdir));
    
    for (Texture tx : textureList)
    {
      img.savePNG(subdir + name);
      
      if (hasAux) {
        aux.savePNG(subdir + name + "_AUX");
      }
      if (hasMipmaps)
      {
        for (int i = 0; i < mipmapList.size(); i++)
        {
          Image mipmap = (Image)mipmapList.get(i);
          mipmap.savePNG(subdir + name + "_MIPMAP_" + (i + 1));
        }
      }
    }
    
    writeText(dir + "/" + name + "." + "txa");
    writeMaterials(subdir + name + ".mtl");
  }
  

  public void writeBinary(String dir)
    throws IOException
  {
    File f = new File(dir + name);
    
    int fileSize = 0;
    for (Iterator localIterator = textureList.iterator(); localIterator.hasNext();) { tx = (Texture)localIterator.next();
      fileSize += tx.getFileSize(); }
    Texture tx; ByteBuffer bb = ByteBuffer.allocateDirect(fileSize);
    
    for (Texture tx : textureList) {
      tx.write(bb);
    }
    byte[] bytes = new byte[bb.limit()];
    bb.rewind();
    bb.get(bytes);
    FileUtils.writeByteArrayToFile(f, bytes);
  }
  
  private void writeText(String fileName) throws IOException
  {
    writePlaintext(new File(fileName));
  }
  
  private void writePlaintext(File f) throws IOException
  {
    PrintWriter pw = IOUtils.getBufferedPrintWriter(f);
    
    for (Texture tx : textureList) {
      tx.print(pw);
    }
    pw.close();
  }
  
  public static TextureArchive parseText(String dir, String name) throws IOException
  {
    return parseText(new File(dir), name);
  }
  
  public static TextureArchive parseText(File dir, String texName) throws IOException
  {
    TextureArchive ta = new TextureArchive(texName);
    File texFile = Paths.get(dir.getAbsolutePath(), new String[] { texName + "." + "txa" }).toFile();
    String subdir = dir.getAbsolutePath() + "/" + texName + "/";
    List<String> lines = IOUtils.readTextFile(texFile, false);
    
    Iterator<String> iter = lines.iterator();
    while (iter.hasNext())
    {
      String line = (String)iter.next();
      if (!line.startsWith("tex:")) {
        throw new InputFileException(texFile, "Invalid texture declaration: " + line);
      }
      String[] tokens = line.split(":\\s*");
      if (tokens.length != 2) {
        throw new InputFileException(texFile, "Invalid texture name: " + line);
      }
      String name = tokens[1];
      
      line = (String)iter.next();
      if (!line.equals("{")) {
        throw new InputFileException(texFile, "Texture " + name + " is missing open curly bracket.");
      }
      int curlyBalance = 1;
      List<String> textureLines = new LinkedList();
      
      for (;;)
      {
        if (!iter.hasNext()) {
          throw new InputFileException(texFile, "Texture " + name + " is missing closed curly bracket.");
        }
        line = (String)iter.next();
        if (line.equals("{")) {
          curlyBalance++;
        } else if (line.equals("}"))
        {
          curlyBalance--;
          if (curlyBalance == 0)
            break;
        }
        if (line.startsWith("tex:")) {
          throw new InputFileException(texFile, "Texture " + name + " is missing closed curly bracket.");
        }
        textureLines.add(line);
      }
      
      Texture tx = Texture.parseTexture(texFile, subdir, name, textureLines);
      textureList.add(tx);
    }
    
    return ta;
  }
  
  public void writeMaterials(String fileName) throws IOException
  {
    writeMaterials(new File(fileName));
  }
  
  public void writeMaterials(File f) throws IOException
  {
    PrintWriter pw = IOUtils.getBufferedPrintWriter(f);
    
    for (Texture tx : textureList)
    {
      pw.println("newmtl m_" + name);
      pw.println("Ka 1 1 1");
      pw.println("Kd 1 1 1");
      pw.println("Ks 1 1 1");
      pw.println("Ns 0");
      pw.println("map_Ka " + name + ".png");
      pw.println("map_Kd " + name + ".png");
      pw.println();
    }
    pw.close();
  }
}
