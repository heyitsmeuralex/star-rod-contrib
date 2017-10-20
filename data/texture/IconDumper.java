package data.texture;

import java.io.IOException;
import java.io.RandomAccessFile;
import main.DevContext;
import main.Directories;
import util.Logger;
import util.Priority;





public class IconDumper
{
  public IconDumper() {}
  
  public static void main(String[] args)
    throws IOException
  {
    DevContext.initialize();
    dumpIcons();
    DevContext.exit();
  }
  
  public static void dumpIcons() throws IOException
  {
    dumpMenuIcons();
    dumpWorldIcons();
    dumpPartnerIcons();
    dumpUnknownLogo();
  }
  
  private static void dumpMenuIcons() throws IOException
  {
    Logger.log("Dumping menu icons.", Priority.MILESTONE);
    RandomAccessFile raf = DevContext.getPristineRomReader();
    



    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    


    for (int i = 1; i < 337; i++)
    {
      raf.seek(416384 + 8 * i);
      int paletteDisabled;
      int imgEnabled;
      int paletteEnabled; int imgDisabled; int paletteDisabled; if (i == 329)
      {
        int iconEnabled = raf.readInt() - -2147062960;
        int iconDisabled = raf.readInt() - -2147062960;
        
        raf.seek(iconEnabled + 24);
        int imgEnabled = raf.readInt() - -2147062960;
        int paletteEnabled = raf.readInt() - -2147062960;
        
        raf.seek(iconDisabled + 24);
        int imgDisabled = raf.readInt() - -2147062960;
        paletteDisabled = raf.readInt() - -2147062960;
      }
      else
      {
        int iconEnabled = raf.readInt() - -2147333120;
        int iconDisabled = raf.readInt() - -2147333120;
        
        raf.seek(iconEnabled + 24);
        imgEnabled = raf.readInt() + 1884944;
        paletteEnabled = raf.readInt() + 1884944;
        
        raf.seek(iconDisabled + 24);
        imgDisabled = raf.readInt() + 1884944;
        paletteDisabled = raf.readInt() + 1884944;
        
        if (imgEnabled < min) min = imgEnabled;
        if (imgEnabled > max) max = imgEnabled;
        if (paletteEnabled < min) min = paletteEnabled;
        if (paletteEnabled > max) { max = paletteEnabled;
        }
        if (imgDisabled < min) min = imgDisabled;
        if (imgDisabled > max) max = imgDisabled;
        if (paletteDisabled < min) min = paletteDisabled;
        if (paletteDisabled > max) { max = paletteDisabled;
        }
      }
      dumpIcon(raf, imgEnabled, paletteEnabled, Directories.DUMP_ICON_MENU + 
        String.format("%03X.png", new Object[] {Integer.valueOf(i) }));
      
      dumpIcon(raf, imgDisabled, paletteDisabled, Directories.DUMP_ICON_MENU + 
        String.format("%03X_disabled.png", new Object[] {Integer.valueOf(i) }));
      
      assert (imgDisabled == imgEnabled);
    }
    
    raf.close();
  }
  
  private static void dumpWorldIcons() throws IOException
  {
    Logger.log("Dumping world icons.", Priority.MILESTONE);
    RandomAccessFile raf = DevContext.getPristineRomReader();
    


    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    
    for (int i = 1; i < 193; i++)
    {
      raf.seek(430544 + 4 * i);
      int icon = raf.readInt() - -2147333120;
      
      raf.seek(icon + 8);
      int imgOffset = raf.readInt() + 1884944;
      int palOffset = raf.readInt() + 1884944;
      
      if (icon < min) min = icon;
      if (icon > max) { max = icon;
      }
      dumpIcon(raf, imgOffset, palOffset, Directories.DUMP_ICON_WORLD + 
        String.format("%02X.png", new Object[] {Integer.valueOf(i) }));
    }
    
    raf.close();
  }
  
  private static void dumpPartnerIcons() throws IOException
  {
    Logger.log("Dumping partner icons.", Priority.MILESTONE);
    RandomAccessFile raf = DevContext.getPristineRomReader();
    


    int min = Integer.MAX_VALUE;
    int max = Integer.MIN_VALUE;
    
    for (int i = 0; i < 26; i++)
    {
      raf.seek(435192 + 4 * i);
      
      int icon = raf.readInt() - -2147062960;
      
      if (icon < min) min = icon;
      if (icon > max) { max = icon;
      }
      raf.seek(icon + 24);
      
      int imgOffset = raf.readInt() - -2147062960;
      int palOffset = raf.readInt() - -2147062960;
      
      dumpIcon(raf, imgOffset, palOffset, Directories.DUMP_ICON_PARTNER + 
        String.format("%03X.png", new Object[] {Integer.valueOf(i) }));
    }
    
    raf.close();
  }
  
  private static void dumpIcons4() throws IOException
  {
    RandomAccessFile raf = DevContext.getPristineRomReader();
    


    for (int i = 0; i < 4; i++)
    {
      raf.seek(439008 + 4 * i);
      int palOffset = raf.readInt() - -2145523776;
      int imgOffset = raf.readInt() - -2145523776;
      dumpIcon(raf, imgOffset, palOffset, Directories.DUMP_ICON + 
        String.format("%02X.png", new Object[] {Integer.valueOf(i) }));
    }
    
    raf.close();
  }
  
  private static void dumpUnknownLogo() throws IOException
  {
    RandomAccessFile raf = DevContext.getPristineRomReader();
    
    Image img = new Image(ImageFormat.CI_8, 32, 30);
    img.readImage(raf, 2087728, false);
    img.readPalette(raf, 2088880);
    
    img.savePNG(Directories.DUMP_ICON + "UnknownIcon.png");
  }
  
  private static void dumpIcon(RandomAccessFile raf, int imgOffset, int palOffset, String filename) throws IOException
  {
    Image img = new Image(ImageFormat.CI_4, 32, 32);
    img.readImage(raf, imgOffset, false);
    img.readPalette(raf, palOffset);
    
    img.savePNG(filename);
  }
}
