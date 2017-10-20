package data.shared.lib;

import data.shared.DataConstants;
import data.shared.DataConstants.ConstEnum;
import data.shared.struct.StructType;
import java.util.HashMap;


public class FunctionArgument
{
  public final ArgumentType type;
  public final PointerInfo ptr;
  public final DataConstants.ConstEnum constType;
  
  public FunctionArgument(FunctionLibrary lib, String argSpec)
  {
    if (argSpec.charAt(0) == '$')
    {
      type = ArgumentType.Pointer;
      ptr = new PointerInfo(lib, argSpec, null);
      constType = null;
    }
    else if (DataConstants.hasLibraryName(argSpec))
    {
      type = ArgumentType.Const;
      ptr = null;
      constType = DataConstants.getFromLibraryName(argSpec);
    }
    else
    {
      type = ArgumentType.get(argSpec);
      ptr = null;
      constType = null;
      
      if (type == null) {
        throw new RuntimeException("Unknown argument type in function library: " + argSpec);
      }
    }
  }
  
  public StructType getPointerType() {
    return ptr.ptrType;
  }
  
  public String getPointerDesc()
  {
    return ptr.ptrDesc;
  }
  
  public int getPointerListLength()
  {
    return ptr.ptrLength;
  }
  
  public boolean listLengthAsIndex()
  {
    return ptr.useIndex;
  }
  
  public static enum ArgumentType
  {
    Unknown("???"), 
    Int("int"), 
    PrintString("print"), 
    Bool("bool"), 
    Pointer(null), 
    Model("modelID"), 
    Collider("colliderID"), 
    Zone("zoneID"), 
    Const(null), 
    Camera("camID");
    
    public final String name;
    private static HashMap<String, ArgumentType> typeMap;
    
    private ArgumentType(String name) {
      this.name = name;
    }
    


    static
    {
      typeMap = new HashMap();
      for (ArgumentType t : values())
      {
        if (name != null)
        {
          typeMap.put(name, t);
        }
      }
    }
    
    private static ArgumentType get(String name) {
      return (ArgumentType)typeMap.get(name);
    }
  }
  
  private class PointerInfo
  {
    public final StructType ptrType;
    public String ptrDesc = null;
    public int ptrLength = -1;
    public boolean useIndex = false;
    
    private PointerInfo(FunctionLibrary lib, String spec)
    {
      int paramStart = spec.indexOf('(');
      String ptrTypeName = paramStart > 1 ? spec.substring(1, paramStart) : spec.substring(1);
      ptrType = lib.getType(ptrTypeName);
      
      if (ptrType == null) {
        throw new RuntimeException("Unknown struct type: " + spec);
      }
      if (paramStart > 1)
      {
        if (spec.charAt(spec.length() - 1) != ')') {
          throw new RuntimeException(String.format("Invalid specification for pointer: %s", new Object[] { spec }));
        }
        String ptrArgs = spec.substring(paramStart + 1, spec.length() - 1);
        if (!ptrArgs.isEmpty())
        {
          for (String param : ptrArgs.split(","))
          {
            if (param.charAt(0) == '"')
            {
              if (param.charAt(param.length() - 1) != '"')
                throw new RuntimeException(String.format("Invalid property specification for pointer: %s", new Object[] { spec }));
              if (ptrDesc != null)
                throw new RuntimeException(String.format("Duplicate property specification for pointer: %s", new Object[] { spec }));
              ptrDesc = param.substring(1, param.length() - 1);
            }
            else
            {
              if (ptrLength >= 0) {
                throw new RuntimeException(String.format("Duplicate property specification for pointer: %s", new Object[] { spec }));
              }
              try
              {
                if (param.charAt(0) == '#')
                {
                  ptrLength = Integer.parseInt(param.substring(1));
                  useIndex = true;
                }
                else
                {
                  ptrLength = Integer.parseInt(param);
                }
              }
              catch (NumberFormatException e)
              {
                throw new RuntimeException(String.format("Could not parse list length specification for pointer: %s", new Object[] { spec }));
              }
            }
          }
        }
      }
    }
  }
}
