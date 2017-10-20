package data.map;

import data.map.encoder.MapEncoder;
import data.shared.Location;
import data.shared.struct.Struct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import main.DevContext;
import main.Directories;
import main.InputFileException;
import main.StarRodDev;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import patcher.Patcher;
import patcher.Region;
import util.IOUtils;
import util.Logger;
import util.Priority;


















public class MapPatcher
{
  private final Patcher patcher;
  private final RandomAccessFile raf;
  private List<AreaConfig> areaConfigs;
  
  private static final class MapConfig
  {
    public String name;
    public String bgName;
    public int ptrHeader = 0;
    public int ptrInitFucntion = 0;
    
    public int flags;
    
    public int tableEntryOffset;
    public boolean oldExists = false;
    public int startOffset = -1414812757;
    public int endOffset = -1414812757;
    

    private MapConfig() {}
  }
  

  public MapPatcher(Patcher patcher)
  {
    this.patcher = patcher;
    raf = patcher.getRandomAccessFile();
  }
  
  public void patchMapData() throws IOException
  {
    Collection<File> patchFiles = IOUtils.getFilesWithExtension(Directories.MOD_MAP_PATCH, "mpat", true);
    for (File f : patchFiles)
    {
      Logger.log("Executing patch: " + f.getName());
      String name = FilenameUtils.removeExtension(f.getName());
      MapEncoder encoder = new MapEncoder(patcher);
      encoder.encode(f, name);
    }
  }
  
  private static ArrayList<AreaConfig> readXML(File xmlFile) throws IOException
  {
    ArrayList<AreaConfig> areas = new ArrayList(32);
    
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(xmlFile);
      document.getDocumentElement().normalize();
      
      NodeList areaElements = document.getElementsByTagName("Area");
      if (areaElements.getLength() < 1) {
        throw new InputFileException(xmlFile, "No areas found.");
      }
      for (int i = 0; i < areaElements.getLength(); i++)
      {
        AreaConfig area = new AreaConfig(null);
        maps = new LinkedList();
        areas.add(area);
        
        Element areaElement = (Element)areaElements.item(i);
        
        if (!areaElement.hasAttribute("name")) {
          throw new InputFileException(xmlFile, "Area is missing attribute: name.");
        }
        name = areaElement.getAttribute("name");
        if (name.length() > 3) {
          throw new InputFileException(xmlFile, "Area names must not exceed three characters! " + name);
        }
        NodeList mapElements = areaElement.getElementsByTagName("Map");
        if (mapElements.getLength() < 1) {
          throw new InputFileException(xmlFile, name + " has no maps.");
        }
        for (int j = 0; j < mapElements.getLength(); j++)
        {
          MapConfig map = new MapConfig(null);
          maps.add(map);
          
          Element mapElement = (Element)mapElements.item(j);
          
          if (!mapElement.hasAttribute("name")) {
            throw new InputFileException(xmlFile, "Map in " + name + " is missing attribute: name.");
          }
          name = mapElement.getAttribute("name");
          if (name.length() > 7) {
            throw new InputFileException(xmlFile, "Map names must not exceed seven characters! " + name);
          }
          if (!mapElement.hasAttribute("background")) {
            throw new InputFileException(xmlFile, name + " is missing attribute: background.");
          }
          bgName = mapElement.getAttribute("background");
          if (bgName.length() > 7) {
            throw new InputFileException(xmlFile, "Map background names must not exceed seven characters! " + name);
          }
          if (!mapElement.hasAttribute("flags")) {
            throw new InputFileException(xmlFile, name + " is missing attribute: flags.");
          }
          flags = ((int)Long.parseLong(mapElement.getAttribute("flags"), 16));
        }
      }
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
      StarRodDev.displayStackTrace(e);
    } catch (SAXException e) {
      e.printStackTrace();
      StarRodDev.displayStackTrace(e);
    }
    
    return areas;
  }
  




  public void readConfigs()
    throws IOException
  {
    areaConfigs = readXML(new File(Directories.MOD_MAP + "MapTable.xml"));
    






    HashMap<String, 1Segment> originalEntries = new HashMap(1200);
    
    RandomAccessFile praf = DevContext.getPristineRomReader();
    int mapTableOffset; for (int i = 0; i < 28; i++)
    {
      praf.seek(452848 + i * 16);
      
      int numMaps = praf.readInt();
      mapTableOffset = praf.readInt() - -2147333120;
      
      for (int j = 0; j < numMaps; j++)
      {
        praf.seek(mapTableOffset + j * 32);
        int mapNameAddr = praf.readInt();
        praf.skipBytes(4);
        Object seg = new Object() { public int start; public int end; };
        start = praf.readInt();
        end = praf.readInt();
        
        praf.seek(mapNameAddr - -2147333120);
        String mapName = IOUtils.readString(praf, 8);
        


        originalEntries.put(mapName, seg);
      }
    }
    praf.close();
    
    for (AreaConfig area : areaConfigs)
    {
      for (MapConfig map : maps)
      {
        Object seg = (1Segment)originalEntries.get(name);
        if (seg != null)
        {
          oldExists = true;
          startOffset = start;
          endOffset = end;
        }
      }
    }
  }
  





  public int writeConfigTable()
    throws IOException
  {
    HashMap<String, Integer> names = new HashMap(1200);
    
    for (AreaConfig area : areaConfigs)
    {



      for (MapConfig map : maps)
      {
        if (!names.containsKey(name)) {
          names.put(name, Integer.valueOf(-1));
        }
        if (!names.containsKey(bgName)) {
          names.put(bgName, Integer.valueOf(-1));
        }
      }
    }
    raf.seek(Patcher.nextAlignedOffset(raf));
    for (String name : names.keySet())
    {
      if (!name.isEmpty())
      {

        names.put(name, Integer.valueOf(Patcher.toAddress((int)raf.getFilePointer())));
        
        raf.write(name.getBytes());
        for (i = name.length(); i < 8; i++)
          raf.write(0);
      } }
    int i;
    names.put("none", Integer.valueOf(0));
    
    raf.seek(Patcher.nextAlignedOffset(raf));
    for (??? = areaConfigs.iterator(); ???.hasNext();) { area = (AreaConfig)???.next();
      
      mapTableAddr = Patcher.toAddress((int)raf.getFilePointer());
      for (MapConfig map : maps)
      {
        tableEntryOffset = ((int)raf.getFilePointer());
        raf.writeInt(((Integer)names.get(name)).intValue());
        raf.writeInt(0);
        raf.writeInt(0);
        raf.writeInt(0);
        raf.writeInt(0);
        raf.writeInt(((Integer)names.get(bgName)).intValue());
        raf.writeInt(0);
        raf.writeInt(flags);
      }
    }
    AreaConfig area;
    int tableBase = (int)raf.getFilePointer();
    for (AreaConfig area : areaConfigs)
    {
      raf.writeInt(maps.size());
      raf.writeInt(mapTableAddr);
      raf.writeInt(0);
      raf.writeInt(0);
    }
    

    raf.writeInt(0);
    raf.writeInt(0);
    raf.writeInt(0);
    raf.writeInt(0);
    
    Logger.log("Wrote map config table to 0x" + String.format("%X", new Object[] { Integer.valueOf(tableBase) }));
    
    fixPointersToConfigTable(tableBase);
    return tableBase;
  }
  







  private void fixPointersToConfigTable(int areaTableStart)
    throws IOException
  {
    int addr = Patcher.toAddress(areaTableStart);
    int upper = addr >>> 16;
    int lower = addr & 0xFFFF;
    
    raf.seek(221204L);
    raf.writeInt(0x3C020000 | upper);
    raf.writeInt(0x24420000 | lower);
    
    raf.seek(221268L);
    raf.writeInt(0x3C020000 | upper);
    raf.writeInt(0x24420000 | lower);
    
    raf.seek(221352L);
    raf.writeInt(0x3C020000 | upper);
    raf.writeInt(0x24420000 | lower);
    
    raf.seek(920372L);
    raf.writeInt(0x3C070000 | upper);
    raf.writeInt(0x24E70000 | lower);
    



















    raf.seek(220128L);
    raf.writeInt(0x3C010000 | upper);
    raf.skipBytes(4);
    raf.writeInt(0x8C220000 | lower + 4);
    


    raf.seek(61540L);
    raf.writeInt(0x3C010000 | upper);
    raf.skipBytes(4);
    raf.writeInt(0x8C230000 | lower + 4);
    
    raf.seek(505540L);
    raf.writeInt(0x3C010000 | upper);
    raf.skipBytes(4);
    raf.writeInt(0x8C230000 | lower + 4);
    

    raf.seek(65840L);
    raf.writeInt(0x3C060000 | upper);
    raf.skipBytes(4);
    raf.writeInt(0x8CC60000 | lower + 4);
    

    raf.seek(68780L);
    raf.writeInt(0x3C050000 | upper);
    raf.skipBytes(4);
    raf.writeInt(0x8CA50000 | lower + 4);
    

    raf.seek(221340L);
    raf.writeInt(0x3C040000 | upper);
    raf.skipBytes(4);
    raf.writeInt(0x8C840000 | lower + 4);
  }
  
  public void writeMapData() throws IOException
  {
    for (AreaConfig area : areaConfigs)
    {
      for (MapConfig map : maps)
      {
        File customData = new File(Directories.MOD_MAP_BUILD + name + ".bin");
        File patchData = new File(Directories.MOD_MAP_TEMP + name + ".bin");
        
        if ((customData.exists()) || (patchData.exists()))
        {

          File newData = customData.exists() ? customData : patchData;
          
          if ((customData.exists()) && (patchData.exists())) {
            Logger.log(String.format("CONFLICT: %s has data in both %s and %s.", new Object[] { name, Directories.MOD_MAP_BUILD, Directories.MOD_MAP_TEMP }), Priority.ERROR);
          }
          


          int writeOffset = startOffset;
          
          if (!oldExists)
          {

            writeOffset = Patcher.nextAlignedOffset(raf);
          }
          else if (newData.length() > endOffset - startOffset)
          {

            writeOffset = Patcher.nextAlignedOffset(raf);
          }
          
          startOffset = writeOffset;
          endOffset = (writeOffset + (int)newData.length());
          
          byte[] data = FileUtils.readFileToByteArray(newData);
          raf.seek(writeOffset);
          raf.write(data);
          
          Logger.log("Wrote " + newData.getName() + " to " + String.format("%X", new Object[] { Integer.valueOf(writeOffset) }));
        }
      }
    }
  }
  


  public void updateConfigTable()
    throws IOException
  {
    for (AreaConfig area : areaConfigs)
    {
      for (MapConfig map : maps)
      {
        File patched = new File(Directories.MOD_MAP_TEMP + name + ".bin");
        File index;
        File index;
        if (patched.exists()) {
          index = new File(Directories.MOD_MAP_TEMP + name + ".midx");
        } else {
          index = new File(Directories.MOD_MAP_SRC + name + ".midx");
        }
        HashMap<String, Struct> structMap = new HashMap();
        new MapEncoder(patcher).loadIndexFile(structMap, index);
        
        boolean foundHeader = false;
        boolean foundInitFunction = false;
        
        for (Struct str : structMap.values())
        {
          if (str.isTypeOf(MapStructTypes.HeaderT))
          {
            ptrHeader = originalLocation.address();
            if (foundHeader)
              throw new RuntimeException("Found more than one " + MapStructTypes.HeaderT + " in " + name);
            foundHeader = true;
          }
          
          if (str.isTypeOf(MapStructTypes.InitFunctionT))
          {
            ptrInitFucntion = originalLocation.address();
            if (foundInitFunction)
              throw new RuntimeException("Found more than one " + MapStructTypes.InitFunctionT + " in " + name);
            foundInitFunction = true;
          }
        }
        
        raf.seek(tableEntryOffset + 4);
        raf.writeInt(ptrHeader);
        raf.writeInt(startOffset);
        raf.writeInt(endOffset);
        raf.writeInt(-2145124352);
        raf.skipBytes(4);
        raf.writeInt(ptrInitFucntion);
      }
    }
  }
  
  public void writeAssetTable()
    throws IOException
  {
    File manifestFile = new File(Directories.MOD_MAP + "AssetTable.txt");
    if (!manifestFile.exists()) {
      throw new RuntimeException("Could not find AssetTable.txt in mod directory!");
    }
    Deque<File> fileList = new LinkedList();
    int tableSize = 28;
    
    BufferedReader in = new BufferedReader(new FileReader(manifestFile));
    
    String line;
    while ((line = in.readLine()) != null)
    {
      if (line.contains("#"))
        line = line.substring(0, line.indexOf("#"));
      line = line.replaceAll("[\t ]+", " ");
      if (!line.equals(""))
      {

        if (line.length() > 15)
        {
          in.close();
          throw new RuntimeException("Map asset names must not exceed 15 characters! " + line);
        }
        
        File f = new File(Directories.MOD_MAP_BUILD + line);
        if (!f.exists())
        {
          f = new File(Directories.DUMP_MAP_YAY0 + line);
          if (!f.exists())
          {
            in.close();
            throw new RuntimeException("Could not find default map asset: " + f);
          }
        }
        else
        {
          Logger.log("Writing asset: " + f.getName());
        }
        tableSize += 28;
        fileList.add(f);
      }
    }
    in.close();
    
    int entryWritePosition = 31719456 + tableSize;
    int spaceLeft = 41938466 - entryWritePosition;
    int numWritten = 0;
    
    while (!fileList.isEmpty())
    {
      File f = (File)fileList.pollFirst();
      int compressedLength = (int)f.length();
      int decompressedLength = (int)f.length();
      
      RandomAccessFile fraf = new RandomAccessFile(f, "r");
      if (fraf.readInt() == 1499560240)
        decompressedLength = fraf.readInt();
      fraf.close();
      
      int writeOffset;
      int writeOffset;
      if (compressedLength > spaceLeft)
      {
        writeOffset = Patcher.nextAlignedOffset(raf);
      } else {
        writeOffset = entryWritePosition;
        entryWritePosition += compressedLength;
        spaceLeft -= compressedLength;
      }
      
      raf.seek(writeOffset);
      raf.write(FileUtils.readFileToByteArray(f));
      
      raf.seek(31719456 + 28 * numWritten);
      raf.write(f.getName().getBytes());
      for (int i = f.getName().length(); i < 16; i++)
        raf.write(0);
      raf.writeInt(writeOffset - 31719456);
      raf.writeInt(compressedLength);
      raf.writeInt(decompressedLength);
      numWritten++;
    }
    
    if (spaceLeft > 0) {
      patcher.addEmptyRegion(new Region(entryWritePosition, 41938466));
    }
    raf.seek(31719456 + 28 * numWritten);
    raf.write("end_data".getBytes());
    for (int i = "end_data".length(); i < 16; i++)
      raf.write(0);
    raf.writeInt(0);
    raf.writeInt(0);
    raf.writeInt(0);
  }
  
  private static final class AreaConfig
  {
    public String name;
    public List<MapPatcher.MapConfig> maps;
    public int mapTableAddr;
    
    private AreaConfig() {}
  }
}
