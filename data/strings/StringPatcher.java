package data.strings;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import main.Directories;
import patcher.Patcher;
import util.IOUtils;
import util.Logger;







public class StringPatcher
{
  private final Patcher patcher;
  private final RandomAccessFile raf;
  private static final int NUM_SECTIONS = 48;
  private final StringSection[] sections;
  private final int[] sectionOffsets;
  private final HashMap<String, StringData> namedStringMap;
  
  public StringPatcher(Patcher patcher)
  {
    this.patcher = patcher;
    raf = patcher.getRandomAccessFile();
    
    sections = new StringSection[48];
    sectionOffsets = new int[48];
    
    for (int i = 0; i < 48; i++) {
      sections[i] = new StringSection(i);
    }
    namedStringMap = new HashMap();
  }
  
  public void readAllStrings() throws IOException
  {
    for (int i = 0; i < 47; i++)
    {
      readStringFile(new File(Directories.DUMP_STRINGS + StringConstants.STRING_SECTION_NAMES[i] + ".txt"));
    }
    
    Collection<File> customStringFiles = IOUtils.getFilesWithExtension(Directories.MOD_STRINGS, "txt", true);
    for (File f : customStringFiles)
    {
      Logger.log("Reading strings from: " + f.getName());
      readStringFile(f);
    }
    
    for (int i = 0; i < 48; i++)
    {
      sections[i].prepareForWriting();
    }
  }
  
  private void readStringFile(File f) throws IOException
  {
    List<StringData> stringList = StringEncoder.parseStringFile(f);
    
    for (StringData string : stringList)
    {
      if ((section > 47) || (section < 0)) {
        throw new IOException("Invalid string ID in file " + f.getName());
      }
      if (index > 65535) {
        throw new IOException("Invalid string ID in file " + f.getName());
      }
      if (string.hasName()) {
        namedStringMap.put(name, string);
      }
      sections[section].addString(string);
    }
  }
  
  public void writeStrings() throws IOException
  {
    int sectionTableSize = 192;
    int allTableSize = sectionTableSize;
    

    for (int i = 0; i < 48; i++)
    {
      allTableSize += sections[i].getOffsetTableSize();
    }
    
    int limit = 29904176;
    
    raf.seek(28848128 + allTableSize);
    for (int i = 0; i < 48; i++)
    {
      if (raf.getFilePointer() + sections[i].getStringSize() > limit)
      {

        patcher.clearRegion((int)raf.getFilePointer(), limit);
        
        raf.seek(Patcher.nextAlignedOffset(raf));
        limit = Integer.MAX_VALUE;
      }
      
      sections[i].writeStrings(raf);
    }
    
    raf.seek(28848128 + sectionTableSize);
    for (int i = 0; i < 48; i++)
    {
      Logger.logf("Writing string section %02X to %X (%d strings)", new Object[] {
        Integer.valueOf(i), Integer.valueOf((int)raf.getFilePointer()), Integer.valueOf(sections[i].getNumStrings()) });
      sectionOffsets[i] = ((int)raf.getFilePointer() - 28848128);
      sections[i].writeStringOffsetTable(raf);
    }
    
    raf.seek(28848128L);
    for (int i = 0; i < 48; i++) {
      raf.writeInt(sectionOffsets[i]);
    }
  }
  
  public boolean namedStringExists(String name) {
    return namedStringMap.containsKey(name);
  }
  
  public StringData getNamedString(String name)
  {
    return (StringData)namedStringMap.get(name);
  }
  
  public HashMap<String, Integer> getStringIDMap()
  {
    HashMap<String, Integer> stringIDMap = new HashMap();
    
    for (Map.Entry<String, StringData> e : namedStringMap.entrySet())
    {
      stringIDMap.put(e.getKey(), Integer.valueOf(((StringData)e.getValue()).getID()));
    }
    
    return stringIDMap;
  }
}
