package data.battle;

import data.shared.DataConstants;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import main.DevContext;
import main.Directories;
import main.InputFileException;
import main.StarRodDev;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import patcher.Patcher;
import util.IOUtils;
import util.Logger;







public class ActorTypesEditor
{
  public ActorTypesEditor() {}
  
  private static final class ActorType
  {
    int ID;
    int groupID;
    int nameStringID;
    int tattleStringID;
    byte shadowOffset;
    byte[] tattleCamOffset;
    
    private ActorType()
    {
      tattleCamOffset = new byte[3];
      groupID = 255;
    }
    

    public String toString()
    {
      return String.format("%02X %02X %08X %08X (%d %d %d) %d", new Object[] {
        Integer.valueOf(ID), Integer.valueOf(groupID), Integer.valueOf(nameStringID), Integer.valueOf(tattleStringID), 
        Byte.valueOf(tattleCamOffset[0]), Byte.valueOf(tattleCamOffset[1]), Byte.valueOf(tattleCamOffset[2]), 
        Byte.valueOf(shadowOffset) });
    }
    

    public boolean equals(Object obj)
    {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      ActorType other = (ActorType)obj;
      if (ID != ID)
        return false;
      if (groupID != groupID)
        return false;
      if (nameStringID != nameStringID)
        return false;
      if (shadowOffset != shadowOffset)
        return false;
      if (!Arrays.equals(tattleCamOffset, tattleCamOffset))
        return false;
      if (tattleStringID != tattleStringID)
        return false;
      return true;
    }
  }
  

















  public static void dump()
    throws IOException
  {
    Logger.log("Dumping world map to XML file...");
    List<ActorType> types = readROM();
    writeXML(types, new File(Directories.DUMP_BATTLE + "ActorTypes.xml"));
  }
  
  public static void patch(Patcher patcher, RandomAccessFile raf) throws IOException
  {
    List<ActorType> types = readXML(new File(Directories.MOD_BATTLE + "ActorTypes.xml"));
    writeROM(patcher, raf, types);
  }
  
  private static List<ActorType> readROM() throws IOException
  {
    ArrayList<ActorType> types = new ArrayList(212);
    for (int i = 0; i < 212; i++)
    {
      types.add(new ActorType(null));
      getID = i;
    }
    
    ByteBuffer bb = DevContext.getPristineRomBuffer();
    
    for (int j = 0;; j += 4)
    {
      bb.position(1780004 + j);
      int addr = bb.getInt();
      if (addr == 0)
        break;
      bb.position(addr - -2146625760);
      int id = bb.get() & 0xFF;
      int groupID = id;
      do
      {
        getgroupID = groupID;
        id = bb.get() & 0xFF;
      }
      while (id != 255);
    }
    
    bb.position(1767908);
    for (int i = 0; i < types.size(); i++) {
      getnameStringID = bb.getInt();
    }
    bb.position(1774712);
    for (int i = 0; i < types.size(); i++) {
      gettattleStringID = bb.getInt();
    }
    bb.position(1775560);
    for (int i = 0; i < types.size(); i++)
    {
      ActorType type = (ActorType)types.get(i);
      tattleCamOffset[0] = bb.get();
      tattleCamOffset[1] = bb.get();
      tattleCamOffset[2] = bb.get();
      shadowOffset = bb.get();
    }
    
    return types;
  }
  
  private static void writeROM(Patcher patcher, RandomAccessFile raf, List<ActorType> types) throws IOException
  {
    if (types.size() > 255) {
      throw new RuntimeException("Error: tried to write more than 255 actor types.");
    }
    writeActorsGroups(raf, types);
    

    raf.seek(1767908L);
    for (int i = 0; i < 212; i++) {
      raf.writeInt(0);
    }
    
    raf.seek(1774712L);
    for (int i = 0; i < 212; i++) {
      raf.writeInt(0);
    }
    
    raf.seek(1775560L);
    for (int i = 0; i < 212; i++) {
      raf.writeInt(0);
    }
    if (types.size() <= 212) {
      writeModifyActors(patcher, raf, types);
    } else {
      writeRelocateActors(patcher, raf, types);
    }
  }
  
  private static final class Group
  {
    List<Integer> members;
    int pointer;
    
    private Group() {
      members = new LinkedList();
    }
  }
  
  private static void writeActorsGroups(RandomAccessFile raf, List<ActorType> types)
    throws IOException
  {
    TreeMap<Integer, Group> groupMap = new TreeMap();
    int totalGroupBytes = 0;
    ActorType type; for (int i = 0; i < types.size(); i++)
    {
      type = (ActorType)types.get(i);
      if (groupID != 255) {
        Group g;
        Group g;
        if (groupMap.containsKey(Integer.valueOf(groupID)))
        {
          g = (Group)groupMap.get(Integer.valueOf(groupID));
        }
        else
        {
          g = new Group(null);
          groupMap.put(Integer.valueOf(groupID), g);
        }
        members.add(Integer.valueOf(ID));
        totalGroupBytes++;
      }
    }
    totalGroupBytes += groupMap.size();
    

    raf.seek(1779940L);
    for (int i = 0; i < 112; i += 4) {
      raf.writeInt(0);
    }
    
    int sizeLimit = groupMap.size() > 11 ? 112 : 64;
    Iterator localIterator; if (totalGroupBytes > sizeLimit)
    {
      raf.seek(Patcher.nextAlignedOffset(raf));
      for (Group g : groupMap.values())
      {
        pointer = Patcher.toAddress((int)raf.getFilePointer());
        for (localIterator = members.iterator(); localIterator.hasNext();) { int i = ((Integer)localIterator.next()).intValue();
          raf.write(i); }
        raf.write(255);
      }
    }
    else
    {
      raf.seek(1779940L);
      for (Group g : groupMap.values())
      {
        pointer = (-2146625760 + (int)raf.getFilePointer());
        for (localIterator = members.iterator(); localIterator.hasNext();) { int i = ((Integer)localIterator.next()).intValue();
          raf.write(i); }
        raf.write(255);
      }
    }
    
    int groupsLocation;
    if (groupMap.size() > 11)
    {
      groupsLocation = Patcher.nextAlignedOffset(raf);
      
      int addr = Patcher.toAddress(groupsLocation);
      int upper = addr >>> 16;
      int lower = addr & 0xFFFF;
      
      raf.seek(1582612L);
      raf.writeInt(0x3C050000 | upper);
      raf.writeInt(0x24A50000 | lower);
      
      raf.seek(1582972L);
      raf.writeInt(0x3C050000 | upper);
      raf.writeInt(0x24A50000 | lower);
      
      raf.seek(groupsLocation);
    }
    else {
      raf.seek(1780004L);
    }
    
    for (Group g : groupMap.values())
      raf.writeInt(pointer);
    raf.writeInt(0);
  }
  
  private static void writeModifyActors(Patcher patcher, RandomAccessFile raf, List<ActorType> types) throws IOException
  {
    patcher.addGlobalPointer("$Global_ActorNameTable", -2144857852);
    raf.seek(1767908L);
    for (int i = 0; i < types.size(); i++) {
      raf.writeInt(getnameStringID);
    }
    patcher.addGlobalPointer("$Global_ActorTattleTable", -2144851048);
    raf.seek(1774712L);
    for (int i = 0; i < types.size(); i++) {
      raf.writeInt(gettattleStringID);
    }
    patcher.addGlobalPointer("$Global_ActorOffsetsTable", -2144850200);
    raf.seek(1775560L);
    for (int i = 0; i < types.size(); i++)
    {
      ActorType type = (ActorType)types.get(i);
      raf.write(tattleCamOffset[0]);
      raf.write(tattleCamOffset[1]);
      raf.write(tattleCamOffset[2]);
      raf.write(shadowOffset);
    }
  }
  
  private static void writeRelocateActors(Patcher patcher, RandomAccessFile raf, List<ActorType> types) throws IOException
  {
    int actorNameTableBase = Patcher.nextAlignedOffset(raf);
    patcher.addGlobalPointer("$Global_ActorNameTable", Patcher.toAddress(actorNameTableBase));
    raf.seek(actorNameTableBase);
    for (int i = 0; i < types.size(); i++) {
      raf.writeInt(getnameStringID);
    }
    int actorTattleTableBase = Patcher.nextAlignedOffset(raf);
    patcher.addGlobalPointer("$Global_ActorTattleTable", Patcher.toAddress(actorTattleTableBase));
    raf.seek(actorTattleTableBase);
    for (int i = 0; i < types.size(); i++) {
      raf.writeInt(gettattleStringID);
    }
    int actorOffsetsTableBase = Patcher.nextAlignedOffset(raf);
    patcher.addGlobalPointer("$Global_ActorOffsetsTable", Patcher.toAddress(actorOffsetsTableBase));
    raf.seek(actorOffsetsTableBase);
    for (int i = 0; i < types.size(); i++)
    {
      ActorType type = (ActorType)types.get(i);
      raf.write(tattleCamOffset[0]);
      raf.write(tattleCamOffset[1]);
      raf.write(tattleCamOffset[2]);
      raf.write(shadowOffset);
    }
    



    int addr = Patcher.toAddress(actorNameTableBase);
    int upper = addr >>> 16;
    int lower = addr & 0xFFFF;
    
    raf.seek(4324884L);
    raf.writeInt(0x3C040000 | upper);
    raf.skipBytes(4);
    raf.writeInt(0x8C840000 | lower);
    
    raf.seek(4325324L);
    raf.writeInt(0x3C040000 | upper);
    raf.skipBytes(4);
    raf.writeInt(0x8C840000 | lower);
    

    addr = Patcher.toAddress(actorOffsetsTableBase);
    upper = addr >>> 16;
    lower = addr & 0xFFFF;
    

    raf.seek(1591212L);
    raf.writeInt(0x3C010000 | upper);
    raf.skipBytes(4);
    raf.writeInt(0x8C220000 | lower + 3);
  }
  
  private static List<ActorType> readXML(File xmlFile) throws IOException
  {
    ArrayList<ActorType> types = new ArrayList(255);
    
    try
    {
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      DocumentBuilder builder = factory.newDocumentBuilder();
      Document document = builder.parse(xmlFile);
      document.getDocumentElement().normalize();
      
      NodeList nodes = document.getElementsByTagName("ActorType");
      if (nodes.getLength() > 255)
        throw new InputFileException(xmlFile, "You cannot define more than 255 ActorTypes.");
      if (nodes.getLength() < 1) {
        throw new InputFileException(xmlFile, "No ActorTypes are defined.");
      }
      for (int i = 0; i < nodes.getLength(); i++)
      {
        ActorType type = new ActorType(null);
        types.add(type);
        
        Element actorElement = (Element)nodes.item(i);
        
        if (!actorElement.hasAttribute("id"))
          throw new InputFileException(xmlFile, "ActorType is missing required attribute: id");
        ID = Integer.parseInt(actorElement.getAttribute("id"), 16);
        
        if (actorElement.hasAttribute("group")) {
          groupID = Integer.parseInt(actorElement.getAttribute("group"), 16);
        }
        if (!actorElement.hasAttribute("name"))
          throw new InputFileException(xmlFile, "ActorType is missing required attribute: name");
        nameStringID = ((int)Long.parseLong(actorElement.getAttribute("name"), 16));
        
        if (!actorElement.hasAttribute("tattle"))
          throw new InputFileException(xmlFile, "ActorType is missing required attribute: tattle");
        tattleStringID = ((int)Long.parseLong(actorElement.getAttribute("tattle"), 16));
        
        if (actorElement.hasAttribute("shadowOffset")) {
          shadowOffset = ((byte)Integer.parseInt(actorElement.getAttribute("shadowOffset")));
        }
        if (actorElement.hasAttribute("tattleCamOffset"))
        {
          String vec = actorElement.getAttribute("tattleCamOffset").replaceAll("//s+", "");
          String[] coords = vec.split(",");
          if (coords.length != 3) {
            throw new InputFileException(xmlFile, "ActorType has invalid tattleCamOffset: " + coords);
          }
          tattleCamOffset[0] = ((byte)Integer.parseInt(coords[0]));
          tattleCamOffset[1] = ((byte)Integer.parseInt(coords[1]));
          tattleCamOffset[2] = ((byte)Integer.parseInt(coords[2]));
        }
      }
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
      StarRodDev.displayStackTrace(e);
    } catch (SAXException e) {
      e.printStackTrace();
      StarRodDev.displayStackTrace(e);
    }
    
    return types;
  }
  
  private static void writeXML(List<ActorType> types, File xmlFile) throws FileNotFoundException
  {
    PrintWriter pw = IOUtils.getBufferedPrintWriter(xmlFile);
    pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
    pw.println("<!-- You can define up to 255 actor types (0-FE). Read the docs for more information. -->");
    pw.println("<ActorTypes>");
    
    for (ActorType type : types)
    {
      String groupString = groupID == 255 ? "" : String.format(" group=\"%02X\"", new Object[] { Integer.valueOf(groupID) });
      String shadowOffsetString = shadowOffset == 0 ? "" : String.format(" shadowOffset=\"%d\"", new Object[] { Byte.valueOf(shadowOffset) });
      String camOffsetString = "";
      if ((tattleCamOffset[0] != 0) || (tattleCamOffset[1] != 0) || (tattleCamOffset[2] != 0)) {
        camOffsetString = String.format(" tattleCamOffset=\"%d,%d,%d\"", new Object[] {
          Byte.valueOf(tattleCamOffset[0]), Byte.valueOf(tattleCamOffset[1]), Byte.valueOf(tattleCamOffset[2]) });
      }
      
      pw.printf("\t<ActorType id=\"%02X\"%s name=\"%08X\" tattle=\"%08X\"%s%s/> <!-- %s -->\r\n", new Object[] {
        Integer.valueOf(ID), groupString, Integer.valueOf(nameStringID), Integer.valueOf(tattleStringID), camOffsetString, shadowOffsetString, 
        DataConstants.getActorName(ID) });
    }
    pw.println("</ActorTypes>");
    pw.close();
  }
}
