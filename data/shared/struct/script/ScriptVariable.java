package data.shared.struct.script;

import data.shared.DataConstants;
import data.shared.DataUtils;
import data.shared.encoder.ScriptParsingException;
import java.util.HashMap;
import reports.ScriptVariableTracker;




public enum ScriptVariable
{
  Primitive("Primitive", 0), 
  Unknown("Unknown", 250000000), 
  FixedReal("Fixed", 230000000), 
  FlagArray("FlagArray", 210000000), 
  Array("Array", 190000000), 
  GameByte("GameByte", 170000000, 512), 
  AreaByte("AreaByte", 150000000, 16), 
  GameFlag("GameFlag", 130000000, 2048), 
  AreaFlag("AreaFlag", 110000000, 256), 
  MapFlag("MapFlag", 90000000, 96), 
  Flag("Flag", 70000000, 96), 
  MapVar("MapVar", 50000000, 16), 
  Var("Var", 30000000, 16);
  
  private final String name;
  private final int offset;
  private final int max;
  private final boolean hasMax;
  private static final HashMap<String, ScriptVariable> nameMap;
  
  private ScriptVariable(String name, int offset) {
    this.name = name;
    this.offset = offset;
    hasMax = false;
    max = 0;
  }
  
  private ScriptVariable(String name, int offset, int max)
  {
    this.name = name;
    this.offset = offset;
    hasMax = true;
    this.max = max;
  }
  


  static
  {
    nameMap = new HashMap();
    for (ScriptVariable v : values()) {
      nameMap.put(name, v);
    }
  }
  
  public int getMaxValue() {
    return max;
  }
  
  public int getOffset()
  {
    return offset;
  }
  
  public static String getString(ScriptVariable type, int offset)
  {
    switch (1.$SwitchMap$data$shared$struct$script$ScriptVariable[type.ordinal()])
    {
    case 1: 
    case 2: 
    case 3: 
      return String.format("%c%s[%X]", new Object[] { Character.valueOf('*'), name, Integer.valueOf(offset) });
    case 4: 
    case 5: 
    case 6: 
      return String.format("%c%s[%03X]", new Object[] { Character.valueOf('*'), name, Integer.valueOf(offset) });
    case 7: 
    case 8: 
      return String.format("%c%s[%02X]", new Object[] { Character.valueOf('*'), name, Integer.valueOf(offset) });
    case 9: 
    case 10: 
    case 11: 
      return String.format("%c%s[%1X]", new Object[] { Character.valueOf('*'), name, Integer.valueOf(offset) });
    }
    
    
    throw new UnsupportedOperationException("Cannot use index with ScriptVariable type " + type);
  }
  

  private String toString(int v)
  {
    switch (1.$SwitchMap$data$shared$struct$script$ScriptVariable[ordinal()])
    {
    case 12: 
      return v + "";
    case 13: 
      float f = (v + 230000000) / 1024.0F;
      return String.format("%c%s[%s]", new Object[] { Character.valueOf('*'), name, Float.valueOf(f) });
    case 1: 
    case 2: 
    case 3: 
      return String.format("%c%s[%X]", new Object[] { Character.valueOf('*'), name, Integer.valueOf(v + offset) });
    case 4: 
    case 5: 
    case 6: 
      return String.format("%c%s[%03X]", new Object[] { Character.valueOf('*'), name, Integer.valueOf(v + offset) });
    case 7: 
    case 8: 
      return String.format("%c%s[%02X]", new Object[] { Character.valueOf('*'), name, Integer.valueOf(v + offset) });
    case 9: 
    case 10: 
    case 11: 
      return String.format("%c%s[%1X]", new Object[] { Character.valueOf('*'), name, Integer.valueOf(v + offset) });
    }
    
    throw new IllegalStateException(String.format("Unknown variable type for argument: %08X", new Object[] { Integer.valueOf(v) }));
  }
  
  private static ScriptVariable getType(int v)
  {
    if (v > -270000000)
    {
      if (v <= -250000000) return Unknown;
      if (v <= -220000000) return FixedReal;
      if (v <= -200000000) return FlagArray;
      if (v <= -180000000) return Array;
      if (v <= -160000000) return GameByte;
      if (v <= -140000000) return AreaByte;
      if (v <= -120000000) return GameFlag;
      if (v <= -100000000) return AreaFlag;
      if (v <= -80000000) return MapFlag;
      if (v <= -60000000) return Flag;
      if (v <= -40000000) return MapVar;
      if (v <= -20000000) return Var;
    }
    return Primitive;
  }
  
  public static boolean isScriptVariable(int v)
  {
    if ((v <= -270000000) || (v > -20000000)) {
      return false;
    }
    ScriptVariable type = getType(v);
    if (type == Primitive) {
      return false;
    }
    if (!hasMax) {
      return true;
    }
    int index = v + offset;
    if ((index < 0) || (index > max)) {
      return false;
    }
    
    if (type == GameByte) {
      ScriptVariableTracker.foundByte(index);
    } else if (type == GameFlag) {
      ScriptVariableTracker.foundFlag(index);
    }
    return true;
  }
  


  public static String getScriptVariable(int v)
  {
    ScriptVariable type = getType(v);
    if (type == GameByte)
    {
      String name = DataConstants.getGameByte(v + GameByteoffset);
      if (name != null) {
        return name;
      }
    } else if (type == GameFlag)
    {
      String name = DataConstants.getGameFlag(v + GameFlagoffset);
      if (name != null) {
        return name;
      }
    }
    return getType(v).toString(v);
  }
  
  public static String parseScriptVariable(String s)
  {
    Integer i = DataConstants.getGameByte(s);
    if (i != null) {
      s = getString(GameByte, i.intValue());
    }
    else {
      i = DataConstants.getGameFlag(s);
      if (i != null) {
        s = getString(GameFlag, i.intValue());
      }
    }
    int lbracket = s.indexOf("[");
    int rbracket = s.indexOf("]");
    
    if ((lbracket < 0) || (rbracket < lbracket)) {
      throw new ScriptParsingException("Could not parse script variable: " + s);
    }
    String variableType = s.substring(1, lbracket);
    String index = s.substring(lbracket + 1, rbracket);
    
    ScriptVariable type = (ScriptVariable)nameMap.get(variableType);
    
    if (type == null) {
      throw new ScriptParsingException("Unrecognized script variable: " + s);
    }
    if (type == Primitive) {
      throw new ScriptParsingException("Invalid script variable: " + s);
    }
    switch (1.$SwitchMap$data$shared$struct$script$ScriptVariable[type.ordinal()])
    {
    case 13: 
      float f = Float.parseFloat(index) * 1024.0F;
      return String.format("%08X", new Object[] { Integer.valueOf(Math.round(f) - FixedRealoffset) });
    }
    int u = DataUtils.parseIntString(index);
    if ((hasMax) && (u >= max))
      throw new ScriptParsingException(String.format("Maximum index for %s is 0x%X, you used %s", new Object[] {type
        .toString(), Integer.valueOf(max), s }));
    return String.format("%08X", new Object[] { Integer.valueOf(u - offset) });
  }
  

  public static int getScriptVariableIndex(String s)
  {
    Integer i = DataConstants.getGameByte(s);
    if (i != null) {
      s = getString(GameByte, i.intValue());
    }
    else {
      i = DataConstants.getGameFlag(s);
      if (i != null) {
        s = getString(GameFlag, i.intValue());
      }
    }
    int lbracket = s.indexOf("[");
    int rbracket = s.indexOf("]");
    
    if ((lbracket < 0) || (rbracket < lbracket)) {
      throw new ScriptParsingException("Could not parse script variable: " + s);
    }
    String variableType = s.substring(1, lbracket);
    String index = s.substring(lbracket + 1, rbracket);
    
    ScriptVariable type = (ScriptVariable)nameMap.get(variableType);
    
    if (type == null) {
      throw new ScriptParsingException("Unrecognized script variable: " + s);
    }
    if (type == Primitive) {
      throw new ScriptParsingException("Invalid script variable: " + s);
    }
    switch (1.$SwitchMap$data$shared$struct$script$ScriptVariable[type.ordinal()])
    {
    case 13: 
      throw new ScriptParsingException("Cannot resolve index for float variable " + s);
    }
    int u = DataUtils.parseIntString(index);
    if ((hasMax) && (u >= max))
      throw new ScriptParsingException(String.format("Maximum index for %s is 0x%X, you used %s", new Object[] {type
        .toString(), Integer.valueOf(max), s }));
    return u;
  }
}
