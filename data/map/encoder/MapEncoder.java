package data.map.encoder;

import data.map.MapStructTypes;
import data.shared.DataConstants;
import data.shared.encoder.DataEncoder;
import data.shared.encoder.InvalidExpressionException;
import data.shared.struct.Struct;
import editor.map.Map;
import editor.map.marker.Marker;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import main.Directories;
import patcher.SymbolDatabase;
import util.IOUtils;










public class MapEncoder
  extends DataEncoder
{
  private Map map;
  
  public MapEncoder(SymbolDatabase db)
    throws IOException
  {
    super(MapStructTypes.mapTypes, db);
    setFunctionNameMap(DataConstants.mapFunctionLib, DataConstants.mapScriptLib);
    setAddressLimit(-2144960512);
    setImportDirectory(new Directories[] { Directories.MOD_MAP_IMPORT });
  }
  
  public void encode(File patchFile, String mapName) throws IOException
  {
    File savedMapFile = new File(Directories.MOD_MAP_SAVE + mapName + ".map");
    File sourceMapFile = new File(Directories.MOD_MAP_SRC + mapName + ".map");
    File mapFile = savedMapFile.exists() ? savedMapFile : sourceMapFile;
    
    File indexFile = new File(Directories.DUMP_MAP_SRC + mapName + ".midx");
    File rawFile = new File(Directories.DUMP_MAP_RAW + mapName + ".bin");
    
    File outFile = new File(Directories.MOD_MAP_TEMP + mapName + ".bin");
    File outIndexFile = new File(Directories.MOD_MAP_TEMP + mapName + ".midx");
    
    boolean newMap = !rawFile.exists();
    
    if (!mapFile.exists()) {
      throw new IOException("Cannot find .map file for patch:\r\n" + patchFile.getAbsolutePath());
    }
    map = Map.loadMap(mapFile);
    
    if (newMap)
    {
      fileBuffer = ByteBuffer.allocateDirect(0);
      createSection(-2145124352, 163840);
    } else {
      fileBuffer = IOUtils.getDirectBuffer(rawFile);
      readIndexFile(indexFile);
    }
    
    readPatchFile(patchFile);
    encode(patchFile, outFile, outIndexFile);
  }
  



  protected void removeJapaneseStrings(Collection<Struct> structs) {}
  


  protected void replaceExpression(String[] args, List<String> newTokenList)
    throws InvalidExpressionException
  {
    switch (args[0])
    {
    case "Vec3d": 
      Marker m = map.getMarker(args[1]);
      if (m == null)
        throw new InvalidExpressionException("No such marker: " + args[1]);
      m.putVector(newTokenList, false);
      break;
    
    case "Path3d": 
      Marker m = map.getMarker(args[1]);
      if (m == null)
        throw new InvalidExpressionException("No such marker: " + args[1]);
      m.putPath(newTokenList, false);
      break;
    
    case "PushGrid": 
      Marker m = map.getMarker(args[1]);
      if (m == null)
        throw new InvalidExpressionException("No such marker: " + args[1]);
      m.putGrid(newTokenList);
      break;
    
    case "Vec4d": 
      Marker m = map.getMarker(args[1]);
      if (m == null)
        throw new InvalidExpressionException("No such marker: " + args[1]);
      m.putVector(newTokenList, false);
      m.putAngle(newTokenList, false);
      break;
    
    case "Vec3f": 
      Marker m = map.getMarker(args[1]);
      if (m == null)
        throw new InvalidExpressionException("No such marker: " + args[1]);
      m.putVector(newTokenList, true);
      break;
    
    case "Path3f": 
      Marker m = map.getMarker(args[1]);
      if (m == null)
        throw new InvalidExpressionException("No such marker: " + args[1]);
      m.putPath(newTokenList, true);
      break;
    
    case "Vec4f": 
      Marker m = map.getMarker(args[1]);
      if (m == null)
        throw new InvalidExpressionException("No such marker: " + args[1]);
      m.putVector(newTokenList, true);
      m.putAngle(newTokenList, true);
      break;
    
    case "Angle": 
      Marker m = map.getMarker(args[1]);
      if (m == null)
        throw new InvalidExpressionException("No such marker: " + args[1]);
      m.putAngle(newTokenList, false);
      break;
    
    case "Anglef": 
      Marker m = map.getMarker(args[1]);
      if (m == null)
        throw new InvalidExpressionException("No such marker: " + args[1]);
      m.putAngle(newTokenList, true);
      break;
    
    case "Model": 
    case "ModelShort": 
      int id = getModelID(args[1]);
      if (id < 0)
        throw new InvalidExpressionException("No such model: " + args[1]);
      if (args[0].equals("ModelShort")) {
        newTokenList.add(String.format("%04Xs", new Object[] { Integer.valueOf(id) }));
      } else
        newTokenList.add(String.format("%08X", new Object[] { Integer.valueOf(id) }));
      break;
    
    case "Collider": 
    case "ColliderShort": 
      int id = getColliderID(args[1]);
      if (id < 0)
        throw new InvalidExpressionException("No such collider: " + args[1]);
      if (args[0].equals("ColliderShort")) {
        newTokenList.add(String.format("%04Xs", new Object[] { Integer.valueOf(id) }));
      } else
        newTokenList.add(String.format("%08X", new Object[] { Integer.valueOf(id) }));
      break;
    
    case "Zone": 
    case "ZoneShort": 
      int id = getZoneID(args[1]);
      if (id < 0)
        throw new InvalidExpressionException("No such zone: " + args[1]);
      if (args[0].equals("ZoneShort")) {
        newTokenList.add(String.format("%04Xs", new Object[] { Integer.valueOf(id) }));
      } else {
        newTokenList.add(String.format("%08X", new Object[] { Integer.valueOf(id) }));
      }
      break;
    }
  }
  
  public int getModelID(String name) {
    return map.getModelID(name);
  }
  
  public int getColliderID(String name)
  {
    return map.getColliderID(name);
  }
  
  public int getZoneID(String name)
  {
    return map.getZoneID(name);
  }
  
  public Marker getMarker(String name)
  {
    return map.getMarker(name);
  }
}
