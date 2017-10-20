package data.texture;

import data.yay0.Yay0Helper;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.Collection;
import main.Directories;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import patcher.Patcher;
import util.IOUtils;
import util.Logger;












public class ImagePatcher
{
  private final Patcher patcher;
  private static final int DRAW_POS = 786452;
  
  public ImagePatcher(Patcher patcher)
  {
    this.patcher = patcher;
  }
  
  public void buildTextureArchives() throws IOException
  {
    Collection<File> archives = IOUtils.getFilesWithExtension(Directories.MOD_IMG_TEX, "txa", true);
    for (File f : archives)
    {
      String name = FilenameUtils.removeExtension(f.getName());
      TextureArchive ta = TextureArchive.parseText(Directories.MOD_IMG_TEX.toFile(), name);
      Logger.log("Building texture archive: " + f.getName());
      
      ta.writeBinary(Directories.MOD_MAP_BUILD.toString());
    }
  }
  
  public void buildBackgrounds() throws IOException
  {
    for (File f : Directories.MOD_IMG_BG.toFile().listFiles())
    {
      String baseName = FilenameUtils.removeExtension(f.getName());
      
      if (baseName.endsWith("_bg"))
      {
        String ext = FilenameUtils.getExtension(f.getName());
        File altFile = new File(Directories.MOD_IMG_BG + baseName + "_alt." + ext);
        
        Image img = Image.load(f, ImageFormat.CI_8);
        Image alt = null;
        
        int fileSize = raster.limit() + 528;
        int imgAddress = -2145385968;
        int palAddress = -2145386480;
        
        if (altFile.exists())
        {
          alt = Image.load(altFile, ImageFormat.CI_8);
          fileSize += 528;
          imgAddress += 528;
          palAddress += 16;
        }
        
        byte[] bytes = new byte[fileSize];
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        
        bb.putInt(imgAddress);
        bb.putInt(palAddress);
        bb.putInt(786452);
        bb.putShort((short)width);
        bb.putShort((short)height);
        
        if (altFile.exists())
        {
          bb.putInt(imgAddress);
          bb.putInt(palAddress + 512);
          bb.putInt(786452);
          bb.putShort((short)width);
          bb.putShort((short)height);
        }
        
        palette.put(bb);
        if (altFile.exists())
          palette.put(bb);
        img.putRaster(bb, false);
        
        File out = new File(Directories.MOD_MAP_BUILD + baseName);
        byte[] compressed = Yay0Helper.encode(bytes);
        FileUtils.writeByteArrayToFile(out, compressed);
      }
    }
  }
  
  public void patchMiscImages() throws IOException
  {
    patchTitleImages();
    patchWorldMap();
    patchSpiritSubscreenBG();
    patchPartyImages();
    patchLetterImage();
  }
  
  private void patchTitleImages() throws IOException
  {
    File f1 = new File(Directories.MOD_IMG_MISC + "title_1.png");
    File f2 = new File(Directories.MOD_IMG_MISC + "title_2.png");
    File f3 = new File(Directories.MOD_IMG_MISC + "title_3.png");
    
    File in = new File(Directories.DUMP_MAP_RAW + "title_data");
    File out = new File(Directories.MOD_MAP_BUILD + "title_data");
    
    byte[] titleData = FileUtils.readFileToByteArray(in);
    ByteBuffer bb = ByteBuffer.wrap(titleData);
    

    if ((!f1.exists()) && (!f2.exists()) && (!f3.exists()))
    {
      FileUtils.forceDelete(out);
      return;
    }
    
    if (f1.exists())
    {
      Image img = Image.load(f1, ImageFormat.RGBA_32);
      if ((height != 112) || (width != 200)) {
        throw new RuntimeException(f1.getName() + " must be 112x200 pixels.");
      }
      bb.position(8720);
      img.putRaster(bb, false);
    }
    
    if (f2.exists())
    {
      Image img = Image.load(f2, ImageFormat.IA_8);
      if ((height != 32) || (width != 144)) {
        throw new RuntimeException(f2.getName() + " must be 32x144 pixels.");
      }
      bb.position(16);
      img.putRaster(bb, false);
    }
    
    if (f3.exists())
    {
      Image img = Image.load(f3, ImageFormat.IA_8);
      if ((height != 32) || (width != 128)) {
        throw new RuntimeException(f3.getName() + " must be 32x128 pixels.");
      }
      bb.position(4624);
      img.putRaster(bb, false);
    }
    
    byte[] compressed = Yay0Helper.encode(titleData);
    FileUtils.writeByteArrayToFile(out, compressed);
  }
  
  private void patchWorldMap() throws IOException
  {
    File f = new File(Directories.MOD_IMG_MISC + "world_map.png");
    
    if (f.exists())
    {
      Image img = Image.load(f, ImageFormat.CI_8);
      
      if ((height != 320) || (width != 320)) {
        throw new RuntimeException(f.getName() + " must be 320x320 pixels.");
      }
      RandomAccessFile raf = patcher.getRandomAccessFile();
      raf.seek(1430512L);
      palette.write(raf);
      raf.seek(1328112L);
      img.writeRaster(raf, false);
    }
  }
  
  private void patchSpiritSubscreenBG() throws IOException
  {
    File f = new File(Directories.MOD_IMG_MISC + "spirits_bg.png");
    
    if (f.exists())
    {
      Image img = Image.load(f, ImageFormat.CI_4);
      
      if ((height != 110) || (width != 128)) {
        throw new RuntimeException(f.getName() + " must be 128x128 pixels.");
      }
      RandomAccessFile raf = patcher.getRandomAccessFile();
      raf.seek(1438064L);
      palette.write(raf);
      raf.seek(1431024L);
      img.writeRaster(raf, false);
    }
  }
  
  private void patchPartyImages() throws IOException
  {
    patchPartyImage("party_kurio");
    patchPartyImage("party_kameki");
    patchPartyImage("party_pinki");
    patchPartyImage("party_pareta");
    patchPartyImage("party_resa");
    patchPartyImage("party_akari");
    patchPartyImage("party_opuku");
    patchPartyImage("party_pokopi");
  }
  
  private void patchPartyImage(String name) throws IOException
  {
    File f = new File(Directories.MOD_IMG_MISC + name + ".png");
    
    if (f.exists())
    {
      File in = new File(Directories.DUMP_MAP_RAW + name);
      File out = new File(Directories.MOD_MAP_BUILD + name);
      
      byte[] bytes = FileUtils.readFileToByteArray(in);
      ByteBuffer bb = ByteBuffer.wrap(bytes);
      
      Image img = Image.load(f, ImageFormat.CI_8);
      if ((height != 105) || (width != 150)) {
        throw new RuntimeException(f.getName() + " must be 105x150 pixels.");
      }
      bb.position(0);
      palette.put(bb);
      img.putRaster(bb, false);
      
      byte[] compressed = Yay0Helper.encode(bytes);
      FileUtils.writeByteArrayToFile(out, compressed);
    }
  }
  
  private void patchLetterImage() throws IOException
  {
    File f = new File(Directories.MOD_IMG_MISC + "peach_letter.png");
    
    if (f.exists())
    {
      Image img = Image.load(f, ImageFormat.CI_8);
      
      if ((height != 105) || (width != 150)) {
        throw new RuntimeException(f.getName() + " must be 105x150 pixels.");
      }
      RandomAccessFile raf = patcher.getRandomAccessFile();
      raf.seek(1155648L);
      palette.write(raf);
      raf.seek(1139896L);
      img.writeRaster(raf, false);
    }
  }
}
