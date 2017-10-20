package data.strings;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import main.Directories;
import shared.SwingUtils;








public final class StringDatabase
{
  private static HashMap<String, StringData> stringsMap;
  
  public StringDatabase() {}
  
  private static void loadStrings()
  {
    try
    {
      FileInputStream fis = new FileInputStream(Directories.DUMP_STRINGS + "stringsMap.ser");
      ObjectInputStream objIn = new ObjectInputStream(fis);
      stringsMap = (HashMap)objIn.readObject();
      objIn.close();
      fis.close();
    } catch (IOException e) {
      SwingUtils.showFramedMessageDialog(null, "Could not load string database.\r\n Have you dumped them yet?", "Missing String Database", 2);
      


      System.exit(-1);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }
  
  public static StringData getString(int section, int index)
  {
    return getString(String.format("%04X%04X", new Object[] { Integer.valueOf(section), Integer.valueOf(index) }));
  }
  
  public static StringData getString(int id)
  {
    return getString(String.format("%08X", new Object[] { Integer.valueOf(id) }));
  }
  
  public static StringData getString(String s)
  {
    return (StringData)stringsMap.get(s);
  }
  
  public static boolean hasString(int section, int index)
  {
    return hasString(String.format("%04X%04X", new Object[] { Integer.valueOf(section), Integer.valueOf(index) }));
  }
  
  public static boolean hasString(int id)
  {
    return hasString(String.format("%08X", new Object[] { Integer.valueOf(id) }));
  }
  
  public static boolean hasString(String s)
  {
    return stringsMap.containsKey(s);
  }
  
  static {}
}
