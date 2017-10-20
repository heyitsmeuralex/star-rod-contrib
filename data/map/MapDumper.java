package data.map;

import data.map.decoder.MapDecoder;
import data.shared.DataConstants;
import data.yay0.Yay0Helper;
import editor.map.Map;
import editor.map.compiler.CollisionDecompiler;
import editor.map.compiler.GeometryDecompiler;
import editor.map.marker.Marker;
import editor.map.marker.Marker.MarkerType;
import editor.map.tree.MapObjectNode;
import editor.map.tree.MapObjectTreeModel;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import main.DevContext;
import main.Directories;
import main.config.Config;
import main.config.Options;
import org.apache.commons.io.FileUtils;
import reports.FunctionCallTracker;
import util.IOUtils;
import util.Logger;
import util.Priority;




public class MapDumper
{
  public MapDumper() {}
  
  public static void dumpMaps(boolean fullDump)
    throws IOException
  {
    if (fullDump)
    {
      Logger.log("Dumping map assets.", Priority.MILESTONE);
      dumpAssetTable();
      
      Logger.log("Gathering map configs.", Priority.MILESTONE);
      dumpConfigTable();
      
      Logger.log("Stripping and recompressing assets.", Priority.MILESTONE);
      stripAssets();
    }
    else
    {
      Logger.log("Gathering map configs.", Priority.MILESTONE);
      dumpConfigTable();
    }
    
    generateSources();
  }
  



  protected static Iterable<MapBlueprint> getBluePrints()
    throws IOException
  {
    Logger.log("Gathering map information...", Priority.MILESTONE);
    
    HashMap<String, MapBlueprint> blueprints = new HashMap(1200);
    RandomAccessFile raf = DevContext.getPristineRomReader();
    

    raf.seek(31719456L);
    for (;;)
    {
      String name = IOUtils.readString(raf, 16);
      
      int offset = raf.readInt() + 31719456;
      int compressedLength = raf.readInt();
      raf.skipBytes(4);
      
      if (name.equals("end_data")) {
        break;
      }
      boolean shape = name.endsWith("_shape");
      boolean hit = name.endsWith("_hit");
      
      if ((hit) || (shape))
      {

        name = name.substring(0, name.lastIndexOf("_"));
        
        MapBlueprint bp = (MapBlueprint)blueprints.get(name);
        if (bp == null)
        {
          bp = new MapBlueprint();
          name = name;
          blueprints.put(name, bp);
        }
        
        if (shape)
        {
          hasShape = true;
          shapeOffset = offset;
          shapeLength = compressedLength;
        }
        
        if (hit)
        {
          hasHit = true;
          hitOffset = offset;
          hitLength = compressedLength;
        }
      }
    }
    
    for (int i = 0; i < 28; i++)
    {
      raf.seek(452848 + i * 16);
      
      int mapsPerArea = raf.readInt();
      int ptrMaps = raf.readInt() - -2147333120;
      
      for (int j = 0; j < mapsPerArea; j++)
      {
        raf.seek(ptrMaps + j * 32);
        
        int mapNameAddr = raf.readInt();
        int headerAddr = raf.readInt();
        int dataStartOffset = raf.readInt();
        int dataEndOffset = raf.readInt();
        raf.skipBytes(4);
        int bgNameAddr = raf.readInt();
        int initAddr = raf.readInt();
        int soundSettings = raf.readInt();
        
        raf.seek(mapNameAddr - -2147333120);
        String name = IOUtils.readString(raf, 8);
        
        MapBlueprint bp = (MapBlueprint)blueprints.get(name);
        if (bp == null)
        {
          bp = new MapBlueprint();
          name = name;
          blueprints.put(name, bp);
        }
        
        hasData = true;
        dataOffset = dataStartOffset;
        dataLength = (dataEndOffset - dataStartOffset);
        
        headerAddr = headerAddr;
        initAddr = initAddr;
        soundSetting = soundSettings;
        
        if (bgNameAddr != 0)
        {
          raf.seek(bgNameAddr - -2147333120);
          bgName = IOUtils.readString(raf, 8);
          hasBackground = true;
        }
      }
    }
    
    raf.close();
    
    List<MapBlueprint> bpList = new ArrayList(blueprints.values());
    Collections.sort(bpList);
    
    return bpList;
  }
  




  protected static void dumpAssetTable()
    throws IOException
  {
    RandomAccessFile raf = DevContext.getPristineRomReader();
    PrintWriter pw = IOUtils.getBufferedPrintWriter(Directories.DUMP_MAP + "AssetTable.txt");
    
    raf.seek(31719456L);
    for (int i = 0; i < 1033; i++)
    {
      String progress = String.format("(%.1f%%)", new Object[] { Double.valueOf(100.0D * i / 1032.0D) });
      Logger.log("Dumping map assets... " + progress, Priority.UPDATE);
      
      raf.seek(31719456 + i * 28);
      String name = IOUtils.readString(raf, 16);
      int offset = raf.readInt() + 31719456;
      int compressedLength = raf.readInt();
      int decompressedLength = raf.readInt();
      
      if (i != 1032) {
        pw.println(name);
      } else {
        pw.print(name);
      }
      Logger.log("Dumping asset: " + name, Priority.IMPORTANT);
      



      raf.seek(offset);
      byte[] writeBytes; byte[] writeBytes; if (raf.readInt() == 1499560240)
      {
        int yay0length = raf.readInt();
        assert (yay0length == decompressedLength);
        
        byte[] dumpedBytes = new byte[compressedLength];
        raf.seek(offset);
        raf.read(dumpedBytes);
        writeBytes = Yay0Helper.decode(dumpedBytes);
      }
      else
      {
        byte[] dumpedBytes = new byte[decompressedLength];
        
        raf.seek(offset);
        raf.read(dumpedBytes);
        writeBytes = dumpedBytes;
      }
      
      File out = new File(Directories.DUMP_MAP_RAW + name);
      FileUtils.writeByteArrayToFile(out, writeBytes);
    }
    
    pw.close();
    raf.close();
  }
  
  protected static void dumpConfigTable()
    throws IOException
  {
    RandomAccessFile raf = DevContext.getPristineRomReader();
    PrintWriter xmlWriter = IOUtils.getBufferedPrintWriter(Directories.DUMP_MAP + "MapTable.xml");
    xmlWriter.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?>");
    xmlWriter.println("<MapTable>");
    
    for (int i = 0; i < 28; i++)
    {
      raf.seek(452848 + i * 16);
      
      int numMaps = raf.readInt();
      int mapTableOffset = raf.readInt() - -2147333120;
      int areaNameOffset = raf.readInt() - -2147333120;
      
      raf.seek(areaNameOffset + 5);
      String areaName = IOUtils.readString(raf, 16);
      xmlWriter.printf("\t<Area name=\"%s\">\r\n", new Object[] { areaName });
      
      for (int j = 0; j < numMaps; j++)
      {
        raf.seek(mapTableOffset + j * 32);
        
        int mapNameAddr = raf.readInt();
        raf.readInt();
        int dataStartOffset = raf.readInt();
        int dataEndOffset = raf.readInt();
        raf.skipBytes(4);
        int bgNameAddr = raf.readInt();
        raf.readInt();
        int mapFlags = raf.readInt();
        
        raf.seek(mapNameAddr - -2147333120);
        String mapName = IOUtils.readString(raf, 8);
        String bgName = "none";
        
        if (bgNameAddr != 0)
        {
          raf.seek(bgNameAddr - -2147333120);
          bgName = IOUtils.readString(raf, 8);
        }
        
        xmlWriter.printf("\t\t<Map  name=%-9s background=%-9s flags=\"%04X\"/>\r\n", new Object[] { "\"" + mapName + "\"", "\"" + bgName + "\"", 
        

          Integer.valueOf(mapFlags) });
        
        byte[] mapData = new byte[dataEndOffset - dataStartOffset];
        raf.seek(dataStartOffset);
        raf.read(mapData);
        FileUtils.writeByteArrayToFile(new File(Directories.DUMP_MAP_RAW + mapName + ".bin"), mapData);
      }
      
      xmlWriter.println("\t</Area>");
    }
    
    xmlWriter.println("</MapTable>");
    xmlWriter.close();
    raf.close();
  }
  




  protected static void stripAssets()
    throws IOException
  {
    RandomAccessFile raf = DevContext.getPristineRomReader();
    raf.seek(31719456L);
    
    int totalSize = 0;
    int totalSavings = 0;
    
    for (int i = 0; i < 1033; i++)
    {
      String progress = String.format("(%.1f%%)", new Object[] { Double.valueOf(100.0D * i / 1032.0D) });
      Logger.log("Stripping and recompressing assets... " + progress, Priority.UPDATE);
      
      raf.seek(31719456 + i * 28);
      String name = IOUtils.readString(raf, 16);
      int offset = raf.readInt() + 31719456;
      int compressedLength = raf.readInt();
      
      byte[] dumped = new byte[compressedLength];
      raf.seek(offset);
      raf.read(dumped);
      
      File out = new File(Directories.DUMP_MAP_YAY0 + name);
      
      raf.seek(offset);
      if (raf.readInt() != 1499560240)
      {
        FileUtils.writeByteArrayToFile(out, dumped);
      }
      else
      {
        boolean shape = name.endsWith("_shape");
        boolean hit = name.endsWith("_hit");
        
        byte[] decoded = Yay0Helper.decode(dumped);
        byte[] recoded = null;
        
        if (shape)
        {
          byte[] stripped = stripShape(decoded);
          recoded = Yay0Helper.encode(stripped);
        }
        
        if (hit)
        {


          recoded = Yay0Helper.encode(decoded);
        }
        
        if ((!shape) && (!hit))
        {
          recoded = Yay0Helper.encode(decoded);
        }
        
        if (recoded.length < dumped.length)
        {
          Logger.logf("Stripped %05X bytes from %s", new Object[] {
            Integer.valueOf(dumped.length - recoded.length), name });
          
          totalSavings += dumped.length - recoded.length;
          totalSize += dumped.length;
          FileUtils.writeByteArrayToFile(out, recoded);
        }
        else
        {
          Logger.logf("Could not reduce size of %s", new Object[] { name });
          totalSize += dumped.length;
          FileUtils.writeByteArrayToFile(out, dumped);
        }
      }
    }
    Logger.logf("Saved %08X / %08X (%2.3f%%)\n", new Object[] { Integer.valueOf(totalSavings), Integer.valueOf(totalSize), Float.valueOf(100.0F * totalSavings / totalSize) });
  }
  

  protected static byte[] stripShape(byte[] decoded)
    throws IOException
  {
    HashMap<Integer, String> textureNameMap = new HashMap();
    HashMap<String, List<Integer>> texturePointerMap = new HashMap();
    
    ByteBuffer decodedBuffer = IOUtils.getDirectBuffer(decoded);
    

    decodedBuffer.position(32);
    while (decodedBuffer.hasRemaining())
    {
      if ((decodedBuffer.getInt() == 94) && (decodedBuffer.getInt() == 2))
      {
        int textureAddr = decodedBuffer.getInt();
        if ((textureAddr & 0xFF000000) == Integer.MIN_VALUE)
        {
          if (textureNameMap.containsKey(Integer.valueOf(textureAddr)))
          {
            String textureName = (String)textureNameMap.get(Integer.valueOf(textureAddr));
            List<Integer> pointerList = (List)texturePointerMap.get(textureName);
            pointerList.add(Integer.valueOf(decodedBuffer.position() - 4));
          }
          else
          {
            LinkedList<Integer> pointerList = new LinkedList();
            pointerList.add(Integer.valueOf(decodedBuffer.position() - 4));
            pos = decodedBuffer.position();
            
            decodedBuffer.position(textureAddr - -2145320960);
            String textureName = IOUtils.readString(decodedBuffer);
            
            textureNameMap.put(Integer.valueOf(textureAddr), textureName);
            texturePointerMap.put(textureName, pointerList);
            decodedBuffer.position(pos);
          }
        }
      }
    }
    
    decodedBuffer.position(8);
    int objectNamesOffset = decodedBuffer.getInt() - -2145320960;
    

    decodedBuffer.position(8);
    decodedBuffer.putInt(0);
    decodedBuffer.putInt(0);
    decodedBuffer.putInt(0);
    
    HashMap<String, Integer> revisedTextureNameMap = new HashMap();
    

    decodedBuffer.position(objectNamesOffset);
    for (int pos = textureNameMap.values().iterator(); pos.hasNext();) { s = (String)pos.next();
      
      revisedTextureNameMap.put(s, Integer.valueOf(decodedBuffer.position() + -2145320960));
      
      decodedBuffer.put(s.getBytes());
      int paddedSize = s.length() + 4 & 0xFFFFFFFC;
      for (int j = s.length(); j < paddedSize; j++)
        decodedBuffer.put((byte)0); }
    String s;
    int end = decodedBuffer.position();
    

    for (String s : texturePointerMap.keySet())
    {
      newPointer = ((Integer)revisedTextureNameMap.get(s)).intValue();
      List<Integer> pointers = (List)texturePointerMap.get(s);
      
      for (localIterator = pointers.iterator(); localIterator.hasNext();) { int p = ((Integer)localIterator.next()).intValue();
        
        decodedBuffer.position(p);
        decodedBuffer.putInt(newPointer);
      } }
    int newPointer;
    Iterator localIterator;
    decodedBuffer.rewind();
    
    byte[] trimmed = new byte[end];
    decodedBuffer.get(trimmed);
    
    return trimmed;
  }
  
  protected static byte[] stripHit(byte[] decoded) throws IOException
  {
    ByteBuffer decodedBuffer = IOUtils.getDirectBuffer(decoded);
    

    decodedBuffer.position(0);
    decodedBuffer.position(decodedBuffer.getInt());
    
    short count = decodedBuffer.getShort();
    decodedBuffer.getShort();
    decodedBuffer.position(decodedBuffer.getInt());
    
    for (short s = 0; s < count; s = (short)(s + 1))
    {
      decodedBuffer.getShort();
      decodedBuffer.putInt(-1);
      decodedBuffer.getShort();
      decodedBuffer.getInt();
    }
    

    decodedBuffer.position(4);
    decodedBuffer.position(decodedBuffer.getInt());
    
    count = decodedBuffer.getShort();
    decodedBuffer.getShort();
    decodedBuffer.position(decodedBuffer.getInt());
    
    for (short s = 0; s < count; s = (short)(s + 1))
    {
      decodedBuffer.getShort();
      decodedBuffer.putInt(-1);
      decodedBuffer.getShort();
      decodedBuffer.getInt();
    }
    
    decodedBuffer.rewind();
    
    return decodedBuffer.array();
  }
  
  private static final int[][] BATTLE_ENEMY_POSITIONS = { { 5, 0, -20 }, { 45, 0, -5 }, { 85, 0, 10 }, { 125, 0, 25 }, { 10, 50, -20 }, { 50, 45, -5 }, { 90, 50, 10 }, { 130, 55, 25 }, { 15, 85, -20 }, { 55, 80, -5 }, { 95, 85, 10 }, { 135, 90, 25 }, { 15, 125, -20 }, { 55, 120, -5 }, { 95, 125, 10 }, { 135, 130, 25 }, { 105, 0, 0 } };
  




  protected static void generateSources()
    throws IOException
  {
    long ti = System.nanoTime();
    
    int totalUnknownPointers = 0;
    int totalMissingSections = 0;
    
    FunctionCallTracker.clear();
    boolean dumpReports = DevContext.mainConfig.getBoolean(Options.DumpReports);
    boolean enableProfiling = DevContext.mainConfig.getBoolean(Options.DumpProfiling);
    
    Iterable<MapBlueprint> blueprints = getBluePrints();
    for (MapBlueprint bp : blueprints)
    {
      Logger.log("Generating source files for map: " + name, Priority.MILESTONE);
      Map map = new Map(name);
      hasBackground = hasBackground;
      bgName = bgName;
      texName = map.getExpectedTexFilename();
      
      File shapeFile = new File(Directories.DUMP_MAP_RAW + name + "_shape");
      File hitFile = new File(Directories.DUMP_MAP_RAW + name + "_hit");
      File dataFile = new File(Directories.DUMP_MAP_RAW + name + ".bin");
      
      long t0 = System.nanoTime();
      
      if (hasShape)
      {
        Logger.log("Analyzing geometry...");
        new GeometryDecompiler(map, shapeFile);
      }
      
      long t1 = System.nanoTime();
      
      if (hasHit)
      {
        Logger.log("Analyzing collision...");
        new CollisionDecompiler(map, hitFile);
      }
      
      long t2 = System.nanoTime();
      
      if (hasData)
      {
        Logger.log("Analyzing scripts...");
        new MapDecoder(map, bp, dataFile);
      }
      
      if (name.contains("_bt"))
      {
        int i = 0;
        MapObjectNode<Marker> rootNode = markerTree.getRoot();
        for (int[] vec : BATTLE_ENEMY_POSITIONS)
        {
          String name = String.format("Home Position %X", new Object[] { Integer.valueOf(i++) });
          Marker m = new Marker(name, Marker.MarkerType.Position, vec[0], vec[1], vec[2], 0.0D);
          getNodeparentNode = rootNode;
          getNodechildIndex = rootNode.getChildCount();
          rootNode.add(m.getNode());
        }
      }
      
      long t3 = System.nanoTime();
      
      String filename = name + ".map";
      map.saveMapAs(Directories.DUMP_MAP_SRC + filename);
      
      long t4 = System.nanoTime();
      
      if (enableProfiling)
      {
        Logger.logf("%8.2f ms : Geometry", new Object[] { Double.valueOf((t1 - t0) / 1000000.0D) });
        Logger.logf("%8.2f ms : Collision", new Object[] { Double.valueOf((t2 - t1) / 1000000.0D) });
        Logger.logf("%8.2f ms : Data", new Object[] { Double.valueOf((t3 - t2) / 1000000.0D) });
        Logger.logf("%8.2f ms : Serialize", new Object[] { Double.valueOf((t4 - t3) / 1000000.0D) });
        Logger.logf("%8.2f ms : Total", new Object[] { Double.valueOf((t4 - t0) / 1000000.0D) });
      }
      
      totalUnknownPointers += unknownPointers;
      totalMissingSections += missingSections;
      Logger.log("", Priority.IMPORTANT);
    }
    
    Logger.log(totalUnknownPointers + " total unknown pointers.", Priority.IMPORTANT);
    Logger.log(totalMissingSections + " total missing sections.", Priority.IMPORTANT);
    
    if (dumpReports) {
      FunctionCallTracker.printCalls(DataConstants.mapFunctionLib, new PrintWriter(Directories.DUMP_REPORTS + "map_func_list.txt"));
    }
    

    long tf = System.nanoTime();
    Logger.logf("TOTAL TIME: %8.2f ms\n", new Object[] { Double.valueOf((tf - ti) / 1000000.0D) });
  }
}
