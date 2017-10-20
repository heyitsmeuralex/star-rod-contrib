package data.texture;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import main.Directories;
import util.Logger;








public class IconPatcher
{
  public IconPatcher() {}
  
  public static void patchMenuIcons(RandomAccessFile raf)
    throws IOException
  {
    for (int i = 1; i < 337; i++)
    {
      File fileEnabled = new File(Directories.MOD_ICON_MENU + String.format("%03X.png", new Object[] { Integer.valueOf(i) }));
      File fileDisabled = new File(Directories.MOD_ICON_MENU + String.format("%03X_disabled.png", new Object[] { Integer.valueOf(i) }));
      
      if ((fileEnabled.exists()) || (fileDisabled.exists()))
      {

        Image imgEnabled = fileEnabled.exists() ? Image.load(fileEnabled, ImageFormat.CI_4) : null;
        Image imgDisabled = fileDisabled.exists() ? Image.load(fileDisabled, ImageFormat.CI_4) : null;
        
        raf.seek(416384 + 8 * i);
        int palOffsetDisabled;
        int imgOffsetEnabled;
        int palOffsetEnabled; int imgOffsetDisabled; int palOffsetDisabled; if (i == 329)
        {
          int offsetEnabled = raf.readInt() - -2147062960;
          int offsetDisabled = raf.readInt() - -2147062960;
          
          raf.seek(offsetEnabled + 24);
          int imgOffsetEnabled = raf.readInt() - -2147062960;
          int palOffsetEnabled = raf.readInt() - -2147062960;
          
          raf.seek(offsetDisabled + 24);
          int imgOffsetDisabled = raf.readInt() - -2147062960;
          palOffsetDisabled = raf.readInt() - -2147062960;
        }
        else
        {
          int offsetEnabled = raf.readInt() - -2147333120;
          int offsetDisabled = raf.readInt() - -2147333120;
          
          raf.seek(offsetEnabled + 24);
          imgOffsetEnabled = raf.readInt() + 1884944;
          palOffsetEnabled = raf.readInt() + 1884944;
          
          raf.seek(offsetDisabled + 24);
          imgOffsetDisabled = raf.readInt() + 1884944;
          palOffsetDisabled = raf.readInt() + 1884944;
        }
        
        assert (imgOffsetEnabled == imgOffsetDisabled);
        
        if (imgEnabled != null)
        {
          Logger.logf("Writing menu icon %03X.png to %X and palette to %X", new Object[] { Integer.valueOf(i), Integer.valueOf(imgOffsetEnabled), Integer.valueOf(palOffsetEnabled) });
          imgEnabled.writeRaster(raf, imgOffsetEnabled, false);
          palette.write(raf, palOffsetEnabled);
        }
        
        if (imgDisabled != null)
        {
          Logger.logf("Writing menu icon %03X_disabled.png palette to %X", new Object[] { Integer.valueOf(i), Integer.valueOf(palOffsetDisabled) });
          palette.write(raf, palOffsetDisabled);
        }
      }
    }
  }
}
