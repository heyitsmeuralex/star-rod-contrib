package shared;

import java.awt.Image;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;


public class Globals
{
  public static final int MOD_HEADER_IDENTIFIER = 1347244882;
  public static final int MOD_HEADER_IDENTIFIER_BS = 1297109587;
  public static final ImageIcon ICON_DEFAULT = loadIcon("icon.png");
  public static final ImageIcon ICON_ERROR = loadIcon("error.png");
  
  public Globals() {}
  
  public static final Image getDefaultIconImage() { return ICON_DEFAULT == null ? null : ICON_DEFAULT.getImage(); }
  

  public static final Image getErrorIconImage()
  {
    return ICON_DEFAULT == null ? null : ICON_DEFAULT.getImage();
  }
  
  private static ImageIcon loadIcon(String resourceName)
  {
    InputStream is = Globals.class.getResourceAsStream(resourceName);
    if (is == null)
    {
      System.err.println("Unable to find resource " + resourceName);
      return null;
    }
    try
    {
      return new ImageIcon(ImageIO.read(is));
    } catch (IOException e) {
      System.err.println("Exception while reading shader " + resourceName); }
    return null;
  }
  

  public static final String osName = System.getProperty("os.name");
  public static final String NL = System.lineSeparator();
}
