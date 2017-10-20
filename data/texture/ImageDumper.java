package data.texture;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.List;
import main.DevContext;
import main.Directories;
import util.IOUtils;
import util.Logger;
import util.Priority;






public class ImageDumper
{
  public ImageDumper() {}
  
  public static void main(String[] args)
    throws IOException
  {
    DevContext.initialize();
    
    dumpLetterImage();
    
    DevContext.exit();
  }
  
  public static void dumpTextures() throws IOException
  {
    dumpAllTextures();
    dumpAllBackgrounds();
    Logger.log("Dumping system textures.", Priority.MILESTONE);
    dumpTitleImages();
    dumpPartyImages();
    dumpLetterImage();
    dumpWorldMap();
    dumpSpiritSubscreenBG();
  }
  
  private static void dumpAllTextures() throws IOException
  {
    File[] assets = Directories.DUMP_MAP_RAW.toFile().listFiles();
    int count = 0;
    
    for (File f : assets) {
      if (f.getName().endsWith("_tex"))
      {
        Logger.log("Dumping textures from " + f.getName(), Priority.MILESTONE);
        TextureArchive ta = new TextureArchive(f);
        ta.dumpToDirectory(Directories.DUMP_IMG_TEX.toFile());
        count += textureList.size();
      }
    }
    Logger.logf("Dumped %d textures.\n", new Object[] { Integer.valueOf(count) });
  }
  
  private static void dumpAllBackgrounds() throws IOException
  {
    File[] assets = Directories.DUMP_MAP_RAW.toFile().listFiles();
    
    for (File f : assets) {
      if (f.getName().endsWith("_bg"))
      {
        dumpBackground(f, 0, f.getName());
        

        if (f.getName().equals("sbk_bg"))
          dumpBackground(f, 16, "sbk_bg_alt");
      }
    }
  }
  
  private static void dumpBackground(File f, int headerOffset, String name) throws IOException {
    Logger.log("Dumping background " + f.getName(), Priority.MILESTONE);
    
    ByteBuffer bb = IOUtils.getDirectBuffer(f);
    
    bb.position(headerOffset);
    int imageOffset = bb.getInt() - -2145386496;
    int paletteOffset = bb.getInt() - -2145386496;
    bb.getInt();
    int width = bb.getShort();
    int height = bb.getShort();
    
    Image img = new Image(ImageFormat.CI_8, height, width);
    img.readImage(bb, imageOffset, false);
    img.readPalette(bb, paletteOffset);
    
    img.savePNG(Directories.DUMP_IMG_BG + name);
  }
  
  private static void dumpTitleImages() throws IOException
  {
    RandomAccessFile raf = new RandomAccessFile(Directories.DUMP_MAP_RAW + "title_data", "r");
    

    Image img = new Image(ImageFormat.RGBA_32, 112, 200);
    img.readImage(raf, 8720, false);
    img.savePNG(Directories.DUMP_IMG_MISC + "title_1");
    
    img = new Image(ImageFormat.IA_8, 32, 144);
    img.readImage(raf, 16, false);
    img.savePNG(Directories.DUMP_IMG_MISC + "title_2");
    
    img = new Image(ImageFormat.IA_8, 32, 128);
    img.readImage(raf, 4624, false);
    img.savePNG(Directories.DUMP_IMG_MISC + "title_3");
  }
  
  private static void dumpWorldMap() throws IOException
  {
    RandomAccessFile raf = DevContext.getPristineRomReader();
    
    Image img = new Image(ImageFormat.CI_8, 320, 320);
    img.readPalette(raf, 1430512);
    img.readImage(raf, 1328112, false);
    
    img.savePNG(Directories.DUMP_IMG_MISC + "world_map");
  }
  
  private static void dumpSpiritSubscreenBG() throws IOException
  {
    RandomAccessFile raf = DevContext.getPristineRomReader();
    
    Image img = new Image(ImageFormat.CI_4, 110, 128);
    img.readPalette(raf, 1438064);
    img.readImage(raf, 1431024, false);
    
    img.savePNG(Directories.DUMP_IMG_MISC + "spirits_bg.png");
  }
  
  private static void dumpPartyImages() throws IOException
  {
    dumpPartyImage("party_kurio");
    dumpPartyImage("party_kameki");
    dumpPartyImage("party_pinki");
    dumpPartyImage("party_pareta");
    dumpPartyImage("party_resa");
    dumpPartyImage("party_akari");
    dumpPartyImage("party_opuku");
    dumpPartyImage("party_pokopi");
  }
  
  private static void dumpPartyImage(String name) throws IOException
  {
    RandomAccessFile raf = new RandomAccessFile(Directories.DUMP_MAP_RAW + name, "r");
    
    Image img = new Image(ImageFormat.CI_8, 105, 150);
    img.readImage(raf, 512, false);
    img.readPalette(raf, 0);
    img.savePNG(Directories.DUMP_IMG_MISC + name);
  }
  
  private static void dumpLetterImage() throws IOException
  {
    RandomAccessFile raf = DevContext.getPristineRomReader();
    
    Image img = new Image(ImageFormat.CI_8, 105, 150);
    img.readImage(raf, 1139896, false);
    img.readPalette(raf, 1155648);
    img.savePNG(Directories.DUMP_IMG_MISC + "peach_letter.png");
    
    raf.close();
  }
}
