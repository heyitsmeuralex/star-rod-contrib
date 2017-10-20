package data.globals;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import main.DevContext;
import main.Directories;
import main.InputFileException;
import main.StarRodDev;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import util.Logger;






public class WorldMapEditor
{
  public WorldMapEditor() {}
  
  public static void dump()
    throws IOException
  {
    Logger.log("Dumping world map to XML file...");
    List<Location> locations = readROM();
    writeXML(locations, new File(Directories.DUMP_GLOBALS + "WorldMap.xml"));
  }
  
  public static void patch(RandomAccessFile raf) throws IOException
  {
    List<Location> locations = readXML(new File(Directories.MOD_GLOBALS + "WorldMap.xml"));
    writeROM(locations, raf);
  }
  














  private static List<Location> readROM()
    throws IOException
  {
    ByteBuffer bb = DevContext.getPristineRomBuffer();
    bb.position(1324536);
    
    ArrayList<Location> locations = new ArrayList(34);
    
    for (int i = 0; i < 34; i++)
    {
      Location loc = new Location(null);
      locations.add(loc);
      
      x = bb.getShort();
      y = bb.getShort();
      parent = bb.get();
      pathLength = bb.get();
      bb.getShort();
      ptrPath = bb.getInt();
      descUpdate = ((byte)bb.getInt());
      id = bb.getInt();
    }
    

    for (Location loc : locations)
    {
      parent = getaccess$300id;
    }
    

    for (Location loc : locations)
    {
      bb.position(ptrPath - -2146382656);
      path = new byte[pathLength][2];
      for (int j = 0; j < pathLength; j++)
      {
        path[j][0] = bb.get();
        path[j][1] = bb.get();
      }
    }
    
    return locations;
  }
  
  private static void writeROM(List<Location> locations, RandomAccessFile raf) throws IOException
  {
    if (locations.size() > 34) {
      throw new RuntimeException("Error: tried to write more than 34 locations for world map.");
    }
    HashMap<Integer, Integer> indexLookup = new HashMap();
    
    int i = 0;
    for (Location loc : locations) {
      indexLookup.put(Integer.valueOf(id), Integer.valueOf(i++));
    }
    
    for (Location loc : locations)
    {
      if (!indexLookup.containsKey(Integer.valueOf(parent)))
        throw new RuntimeException("Location parent ID could not be found.");
      parent = ((Integer)indexLookup.get(Integer.valueOf(parent))).intValue();
    }
    

    i = 0;
    for (Location loc : locations)
    {
      ptrPath = (-2145060296 + i);
      raf.seek(1322360 + i);
      i += 64;
      
      if (pathLength > 32) {
        throw new RuntimeException("Error: location path length exceeds limit: (" + pathLength + " / 32)");
      }
      for (int j = 0; j < pathLength; j++)
      {
        raf.writeByte(path[j][0]);
        raf.writeByte(path[j][1]);
      }
    }
    

    raf.seek(1324536L);
    for (Location loc : locations)
    {
      raf.writeShort(x);
      raf.writeShort(y);
      raf.writeByte(parent);
      raf.writeByte(pathLength);
      raf.writeShort(0);
      raf.writeInt(ptrPath);
      raf.writeInt(descUpdate);
      raf.writeInt(id);
    }
  }
  
  private static List<Location> readXML(File xmlFile) throws IOException
  {
    ArrayList<Location> locations = new ArrayList(34);
    
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(xmlFile);
      document.getDocumentElement().normalize();
      
      NodeList nodes = document.getElementsByTagName("Location");
      if (nodes.getLength() > 34)
        throw new InputFileException(xmlFile, "Only 34 locations may be defined for the world map.");
      if (nodes.getLength() < 1) {
        throw new InputFileException(xmlFile, "No locations defined for world map.");
      }
      for (int i = 0; i < nodes.getLength(); i++)
      {
        Location loc = new Location(null);
        locations.add(loc);
        
        Element locationElement = (Element)nodes.item(i);
        
        if (!locationElement.hasAttribute("posX"))
          throw new InputFileException(xmlFile, "Location is missing attribute: posX.");
        x = Integer.parseInt(locationElement.getAttribute("posX"));
        
        if (!locationElement.hasAttribute("posY"))
          throw new InputFileException(xmlFile, "Location is missing attribute: posY.");
        y = Integer.parseInt(locationElement.getAttribute("posY"));
        
        if (!locationElement.hasAttribute("id"))
          throw new InputFileException(xmlFile, "Location is missing attribute: id.");
        id = Integer.parseInt(locationElement.getAttribute("id"), 16);
        
        if (!locationElement.hasAttribute("parent"))
          throw new InputFileException(xmlFile, "Location is missing attribute: parent.");
        parent = Integer.parseInt(locationElement.getAttribute("parent"), 16);
        
        if (!locationElement.hasAttribute("update"))
          throw new InputFileException(xmlFile, "Location is missing attribute: update.");
        descUpdate = ((byte)Integer.parseInt(locationElement.getAttribute("update"), 16));
        
        if (!locationElement.hasAttribute("path"))
          throw new InputFileException(xmlFile, "Location is missing attribute: path.");
        String path = locationElement.getAttribute("path").replaceAll("//s+", "");
        if (path.isEmpty())
        {
          pathLength = 0;
          path = ((byte[][])null);
        }
        else
        {
          String[] points = path.split(";");
          pathLength = points.length;
          if (pathLength > 32) {
            throw new InputFileException(xmlFile, "Path length exceeds limit: (" + pathLength + " / 32)");
          }
          path = new byte[points.length][2];
          for (int j = 0; j < points.length; j++)
          {
            String[] coords = points[j].split(",");
            if (coords.length != 2)
              throw new InputFileException(xmlFile, "Path has invalid coordinate: " + points[j]);
            path[j][0] = ((byte)Integer.parseInt(coords[0]));
            path[j][1] = ((byte)Integer.parseInt(coords[1]));
          }
        }
      }
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
      StarRodDev.displayStackTrace(e);
    } catch (SAXException e) {
      e.printStackTrace();
      StarRodDev.displayStackTrace(e);
    }
    
    return locations;
  }
  
  private static void writeXML(List<Location> locations, File xmlFile)
  {
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.newDocument();
      
      Element rootElement = doc.createElement("WorldMap");
      doc.appendChild(rootElement);
      
      for (Location loc : locations)
      {
        Element locationElement = doc.createElement("Location");
        locationElement.setAttribute("id", String.format("%02X", new Object[] { Integer.valueOf(id) }));
        locationElement.setAttribute("parent", String.format("%02X", new Object[] { Integer.valueOf(parent) }));
        locationElement.setAttribute("posX", String.format("%d", new Object[] { Integer.valueOf(x) }));
        locationElement.setAttribute("posY", String.format("%d", new Object[] { Integer.valueOf(y) }));
        locationElement.setAttribute("update", String.format("%02X", new Object[] { Byte.valueOf(descUpdate) }));
        
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < pathLength; j++)
        {
          String s = String.format("%d,%d", new Object[] { Byte.valueOf(path[j][0]), Byte.valueOf(path[j][1]) });
          sb.append(s);
          if (j < pathLength - 1) {
            sb.append(";");
          }
        }
        locationElement.setAttribute("path", sb.toString());
        rootElement.appendChild(locationElement);
      }
      
      TransformerFactory transformerFactory = TransformerFactory.newInstance();
      Transformer transformer = transformerFactory.newTransformer();
      transformer.setOutputProperty("indent", "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      
      DOMSource source = new DOMSource(doc);
      StreamResult dest = new StreamResult(xmlFile);
      
      transformer.transform(source, dest);
    }
    catch (ParserConfigurationException e) {
      e.printStackTrace();
      StarRodDev.displayStackTrace(e);
    } catch (TransformerException e) {
      e.printStackTrace();
      StarRodDev.displayStackTrace(e);
    }
  }
  
  private static final class Location
  {
    private int x;
    private int y;
    private int id;
    private int parent;
    private byte descUpdate;
    private int ptrPath;
    private int pathLength;
    private byte[][] path;
    
    private Location() {}
  }
}
