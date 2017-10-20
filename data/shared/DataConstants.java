package data.shared;

import data.battle.BattleStructTypes;
import data.map.MapStructTypes;
import data.shared.encoder.UnknownConstantException;
import data.shared.lib.FunctionLibrary;
import data.shared.lib.ScriptLibrary;
import data.shared.struct.script.ScriptVariable;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import main.Directories;
import main.InputFileException;
import main.StarRodDev;
import util.DualHashMap;
import util.IOUtils;


public class DataConstants
{
  public static final char EXPRESSION_PREFIX = '{';
  public static final char EXPRESSION_SUFFIX = '}';
  public static final char SCRIPT_VAR_PREFIX = '*';
  public static final char CONSTANT_PREFIX = '.';
  public static final char CONSTANT_SEPARATOR = ':';
  public static final char POINTER_PREFIX = '$';
  public static final char STRING_DELIMITER = '"';
  public static final Pattern CommentPattern = Pattern.compile("/%([\\S\\s]*?)%/|(?<!/)%.*");
  
  public static final String StartBlockComment = "/%";
  
  public static final String EndBlockComment = "%/";
  
  public static FunctionLibrary mapFunctionLib;
  
  public static FunctionLibrary battleFunctionLib;
  
  public static FunctionLibrary sharedFunctionLib;
  
  public static ScriptLibrary mapScriptLib;
  
  public static ScriptLibrary battleScriptLib;
  
  public static ScriptLibrary sharedScriptLib;
  
  public static DualHashMap<Integer, String> EffectType;
  
  private static HashMap<Integer, String> actorNameMap;
  
  private static HashMap<Integer, String> moveNameMap;
  private static HashMap<String, String> miscConstantsMap;
  private static HashMap<String, ConstEnum> constNameMap;
  private static HashMap<String, ConstEnum> constLibMap;
  public static ConstEnum ItemType;
  public static ConstEnum StatusType;
  public static ConstEnum ElementType;
  public static ConstEnum EntityType;
  public static ConstEnum TriggerType;
  public static ConstEnum EventType;
  public static ConstEnum PhaseType;
  private static DualHashMap<Integer, String> defaultByteNames;
  private static DualHashMap<Integer, String> defaultFlagNames;
  private static DualHashMap<Integer, String> modByteNames;
  private static DualHashMap<Integer, String> modFlagNames;
  private static HashMap<String, String> modItemMap;
  private static boolean initialized = false;
  
  public DataConstants() {}
  
  public static void initialize() throws IOException { if (initialized) {
      return;
    }
    constNameMap = new HashMap();
    constLibMap = new HashMap();
    
    for (File f : IOUtils.getFilesWithExtension(Directories.DATABASE_TYPES, "enum", false))
    {
      ConstEnum ce = new ConstEnum(f, null);
      constNameMap.put(namespace, ce);
      constLibMap.put(libName, ce);
    }
    
    actorNameMap = readDecode(Directories.DATABASE_TYPES + "actors.txt");
    moveNameMap = readDecode(Directories.DATABASE_TYPES + "moves.txt");
    miscConstantsMap = readEncode(Directories.DATABASE_TYPES + "misc.txt");
    
    ItemType = (ConstEnum)constNameMap.get("Item");
    StatusType = (ConstEnum)constNameMap.get("Status");
    ElementType = (ConstEnum)constNameMap.get("Element");
    EntityType = (ConstEnum)constNameMap.get("Entity");
    TriggerType = (ConstEnum)constNameMap.get("Trigger");
    EventType = (ConstEnum)constNameMap.get("Event");
    PhaseType = (ConstEnum)constNameMap.get("Phase");
    
    EffectType = readEffectTypes(new File(Directories.DATABASE_TYPES + "effects.txt"));
    
    mapFunctionLib = new FunctionLibrary(MapStructTypes.mapTypes);
    mapFunctionLib.load(new File(Directories.DATABASE + "shared_func_library.txt"));
    mapFunctionLib.load(new File(Directories.DATABASE + "map_func_library.txt"));
    
    battleFunctionLib = new FunctionLibrary(BattleStructTypes.battleTypes);
    battleFunctionLib.load(new File(Directories.DATABASE + "shared_func_library.txt"));
    battleFunctionLib.load(new File(Directories.DATABASE + "battle_func_library.txt"));
    
    sharedFunctionLib = new FunctionLibrary(SharedStructTypes.sharedTypes);
    sharedFunctionLib.load(new File(Directories.DATABASE + "shared_func_library.txt"));
    
    mapScriptLib = new ScriptLibrary();
    mapScriptLib.load(new File(Directories.DATABASE + "map_script_library.txt"));
    
    battleScriptLib = new ScriptLibrary();
    battleScriptLib.load(new File(Directories.DATABASE + "battle_script_library.txt"));
    
    sharedScriptLib = new ScriptLibrary();
    
    defaultByteNames = new DualHashMap();
    defaultFlagNames = new DualHashMap();
    
    readGameBytes(defaultByteNames, new File(Directories.DATABASE + "/SavedBytes.txt"));
    readGameFlags(defaultFlagNames, new File(Directories.DATABASE + "/SavedFlags.txt"));
    
    modByteNames = new DualHashMap();
    modFlagNames = new DualHashMap();
    modItemMap = new HashMap();
    
    initialized = true;
  }
  
  public static String getActorName(int index)
  {
    return (String)actorNameMap.get(Integer.valueOf(index));
  }
  
  public static boolean hasMoveName(int index)
  {
    return moveNameMap.containsKey(Integer.valueOf(index));
  }
  
  public static String getMoveName(int index)
  {
    return (String)moveNameMap.get(Integer.valueOf(index));
  }
  
  private static HashMap<String, String> readEncode(String path)
    throws IOException
  {
    File f = new File(path);
    HashMap<String, String> map = new HashMap();
    try {
      List<String> lines = IOUtils.readTextFile(f);
      for (int i = 0; i < lines.size(); i++)
      {
        String line = (String)lines.get(i);
        
        if (!line.isEmpty())
        {

          String[] tokens = IOUtils.getKeyValuePair(f, line, i);
          
          map.put(tokens[0], tokens[1]);
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
      StarRodDev.displayStackTrace(e);
      System.exit(-1);
    }
    return map;
  }
  
  private static HashMap<Integer, String> readDecode(String path)
    throws IOException
  {
    File f = new File(path);
    HashMap<Integer, String> map = new HashMap();
    try {
      List<String> lines = IOUtils.readTextFile(f);
      for (int i = 0; i < lines.size(); i++)
      {
        String line = (String)lines.get(i);
        
        if (!line.isEmpty())
        {

          String[] tokens = IOUtils.getKeyValuePair(f, line, i);
          int index = (int)Long.parseLong(tokens[0], 16);
          
          map.put(Integer.valueOf(index), tokens[1]);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      StarRodDev.displayStackTrace(e);
      System.exit(-1);
    }
    return map;
  }
  
  public static boolean has(String s)
  {
    if (miscConstantsMap.containsKey(s)) {
      return true;
    }
    int radix = s.lastIndexOf(':');
    
    if (radix < 0) {
      return false;
    }
    String nameSpace = s.substring(0, radix);
    String name = s.substring(radix);
    
    if ((nameSpace.isEmpty()) || (name.isEmpty())) {
      return false;
    }
    ConstEnum constType = (ConstEnum)constNameMap.get(nameSpace);
    
    if (constType == null) {
      return false;
    }
    return constType.hasID(name);
  }
  
  public static String resolve(String s, boolean checkValidity)
  {
    if (miscConstantsMap.containsKey(s)) {
      return (String)miscConstantsMap.get(s);
    }
    String nameSpace = null;
    String name = null;
    
    int radix = s.lastIndexOf(':');
    
    if (radix > 0)
    {
      nameSpace = s.substring(0, radix);
      name = s.substring(radix + 1);
    }
    
    if ((radix < 0) || (nameSpace.isEmpty()) || (name.isEmpty()))
    {
      if (checkValidity) {
        throw new UnknownConstantException("Could not resolve constant: ." + s);
      }
      return null;
    }
    
    if (nameSpace.equals("ModItem")) {
      return (String)modItemMap.get(name);
    }
    ConstEnum constType = (ConstEnum)constNameMap.get(nameSpace);
    
    if (constType == null) {
      return null;
    }
    return constType.getID(name);
  }
  
  public static class ConstEnum
  {
    private final String namespace;
    private final String libName;
    private HashMap<Integer, String> decodeMap;
    private HashMap<String, String> encodeMap;
    
    private ConstEnum(File f)
      throws IOException
    {
      List<String> lines = IOUtils.readTextFile(f);
      
      if (lines.size() < 3) {
        throw new IOException(f.getName() + " is missing header lines.");
      }
      namespace = ((String)lines.get(0));
      libName = ((String)lines.get(1));
      boolean reversed = Boolean.parseBoolean((String)lines.get(2));
      
      decodeMap = new HashMap();
      encodeMap = new HashMap();
      
      for (int i = 3; i < lines.size(); i++)
      {
        String line = (String)lines.get(i);
        
        if (!line.isEmpty())
        {

          if (reversed)
          {
            String[] tokens = IOUtils.getKeyValuePair(f, line, i);
            encodeMap.put(tokens[0], tokens[1]);
          }
          else
          {
            String[] tokens = IOUtils.getKeyValuePair(f, line, i);
            int index = (int)Long.parseLong(tokens[0], 16);
            decodeMap.put(Integer.valueOf(index), tokens[1]);
          }
        }
      }
      if (reversed) {
        decodeMap = DataConstants.getDecodingMap(encodeMap);
      } else {
        encodeMap = DataConstants.getEncodingMap(decodeMap);
      }
    }
    
    public boolean has(int id) {
      return decodeMap.containsKey(Integer.valueOf(id));
    }
    
    public String getName(int id)
    {
      return (String)decodeMap.get(Integer.valueOf(id));
    }
    
    public void printSet()
    {
      System.out.println(namespace);
      for (Map.Entry<Integer, String> e : decodeMap.entrySet())
        System.out.printf("%08X %s\r\n", new Object[] { e.getKey(), e.getValue() });
      System.out.println();
    }
    
    public String getConstantString(int id)
    {
      if (decodeMap.containsKey(Integer.valueOf(id))) {
        return '.' + namespace + ':' + (String)decodeMap.get(Integer.valueOf(id));
      }
      return String.format("%08X", new Object[] { Integer.valueOf(id) });
    }
    
    public boolean hasID(String name)
    {
      return encodeMap.containsKey(name);
    }
    
    public String getID(String name)
    {
      return (String)encodeMap.get(name);
    }
  }
  
  private static HashMap<Integer, String> getDecodingMap(HashMap<String, String> encodingMap)
  {
    HashMap<Integer, String> decodingMap = new HashMap();
    
    for (Map.Entry<String, String> e : encodingMap.entrySet()) {
      decodingMap.put(Integer.valueOf((int)Long.parseLong((String)e.getValue(), 16)), e.getKey());
    }
    return decodingMap;
  }
  
  private static HashMap<String, String> getEncodingMap(HashMap<Integer, String> decodingMap)
  {
    HashMap<String, String> encodingMap = new HashMap();
    
    for (Map.Entry<Integer, String> e : decodingMap.entrySet()) {
      encodingMap.put(e.getValue(), String.format("%08X", new Object[] { e.getKey() }));
    }
    return encodingMap;
  }
  
  private static DualHashMap<Integer, String> readEffectTypes(File byteFile) throws IOException
  {
    DualHashMap<Integer, String> effectMap = new DualHashMap();
    List<String> lines = IOUtils.readTextFile(byteFile);
    
    for (int i = 0; i < lines.size(); i++)
    {
      String line = (String)lines.get(i);
      
      if ((!line.isEmpty()) && (line.contains("=")))
      {

        String[] tokens = IOUtils.getKeyValuePair(byteFile, line, i);
        
        int effectKey = Integer.parseInt(tokens[0], 16);
        effectMap.add(Integer.valueOf(effectKey), tokens[1]);
      }
    }
    return effectMap;
  }
  
  public static boolean hasLibraryName(String libName)
  {
    return constLibMap.containsKey(libName);
  }
  
  public static ConstEnum getFromLibraryName(String libName)
  {
    return (ConstEnum)constLibMap.get(libName);
  }
  
  public static boolean hasNamespace(String namespace)
  {
    return constLibMap.containsKey(namespace);
  }
  
  public static ConstEnum getFromNamespace(String namespace)
  {
    return (ConstEnum)constLibMap.get(namespace);
  }
  
  public static void loadModGlobals() throws IOException
  {
    readGameBytes(modByteNames, new File(Directories.MOD_GLOBALS + "ModBytes.txt"));
    readGameFlags(modFlagNames, new File(Directories.MOD_GLOBALS + "ModFlags.txt"));
    readModItems(new File(Directories.MOD_GLOBALS + "ModItems.txt"));
  }
  
  public static void clearModGlobals() throws IOException
  {
    modByteNames.clear();
    modFlagNames.clear();
    modItemMap.clear();
  }
  
  private static void readGameBytes(DualHashMap<Integer, String> nameMap, File byteFile) throws IOException
  {
    List<String> lines = IOUtils.readTextFile(byteFile);
    HashSet<Integer> indexSet = new HashSet();
    nameMap.clear();
    
    for (int i = 0; i < lines.size(); i++)
    {
      String line = (String)lines.get(i);
      
      if ((!line.isEmpty()) && (line.contains("=")))
      {

        String[] tokens = IOUtils.getKeyValuePair(byteFile, line, i);
        String newName = '*' + tokens[1];
        int offset = Integer.parseInt(tokens[0], 16);
        
        int max = ScriptVariable.GameByte.getMaxValue();
        if ((offset < 0) || (offset >= max)) {
          throw new InputFileException(byteFile, i, "Saved byte index is out of range (0 to %X): %s", new Object[] { Integer.valueOf(max), line });
        }
        if (indexSet.contains(Integer.valueOf(offset)))
          throw new InputFileException(byteFile, i, "Duplicate saved byte index: ", new Object[] { line });
        indexSet.add(Integer.valueOf(offset));
        
        if (nameMap.containsInverse(newName)) {
          throw new InputFileException(byteFile, i, "Duplicate saved byte name: " + line);
        }
        nameMap.add(Integer.valueOf(offset), newName);
      }
    }
  }
  
  private static void readGameFlags(DualHashMap<Integer, String> nameMap, File flagFile) throws IOException {
    List<String> lines = IOUtils.readTextFile(flagFile);
    HashSet<Integer> indexSet = new HashSet();
    nameMap.clear();
    
    for (int i = 0; i < lines.size(); i++)
    {
      String line = (String)lines.get(i);
      
      if ((!line.isEmpty()) && (line.contains("=")))
      {

        String[] tokens = IOUtils.getKeyValuePair(flagFile, line, i);
        String newName = '*' + tokens[1];
        int offset = Integer.parseInt(tokens[0], 16);
        
        int max = ScriptVariable.GameFlag.getMaxValue();
        if ((offset < 0) || (offset >= max)) {
          throw new InputFileException(flagFile, i, "Saved flag index is out of range (0 to %X): %s", new Object[] { Integer.valueOf(max), line });
        }
        if (indexSet.contains(Integer.valueOf(offset)))
          throw new InputFileException(flagFile, i, "Duplicate saved flag index: ", new Object[] { line });
        indexSet.add(Integer.valueOf(offset));
        
        if (nameMap.containsInverse(newName)) {
          throw new InputFileException(flagFile, i, "Saved flag may not share name with byte: " + line);
        }
        if (nameMap.containsInverse(newName)) {
          throw new InputFileException(flagFile, i, "Duplicate saved flag name: " + line);
        }
        nameMap.add(Integer.valueOf(offset), newName);
      }
    }
  }
  
  private static void readModItems(File file) throws IOException {
    List<String> lines = IOUtils.readTextFile(file);
    modItemMap.clear();
    
    for (int i = 0; i < lines.size(); i++)
    {
      String line = (String)lines.get(i);
      
      if (!line.isEmpty())
      {

        String[] tokens = IOUtils.getKeyValuePair(file, line, i);
        
        int index = (int)Long.parseLong(tokens[0], 16);
        modItemMap.put(tokens[1], String.format("%08X", new Object[] { Integer.valueOf(index) }));
      }
    }
  }
  
  public static String getGameByte(int index) {
    return (String)defaultByteNames.get(Integer.valueOf(index));
  }
  
  public static Integer getGameByte(String name)
  {
    if (modByteNames.isEmpty()) {
      return (Integer)defaultByteNames.getInverse(name);
    }
    return (Integer)modByteNames.getInverse(name);
  }
  
  public static String getGameFlag(int index)
  {
    return (String)defaultFlagNames.get(Integer.valueOf(index));
  }
  
  public static Integer getGameFlag(String name)
  {
    if (modFlagNames.isEmpty()) {
      return (Integer)defaultFlagNames.getInverse(name);
    }
    return (Integer)modFlagNames.getInverse(name);
  }
}
