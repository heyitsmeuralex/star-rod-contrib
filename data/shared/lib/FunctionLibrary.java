package data.shared.lib;

import data.shared.struct.StructType;
import data.shared.struct.TypeMap;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import util.IOUtils;

public class FunctionLibrary implements Iterable<NamedFunction>
{
  private TreeMap<Integer, NamedFunction> addrMap;
  private TreeMap<String, NamedFunction> nameMap;
  private TypeMap typeMap;
  
  public NamedFunction get(int addr)
  {
    return (NamedFunction)addrMap.get(Integer.valueOf(addr));
  }
  
  public NamedFunction get(String funcName)
  {
    return (NamedFunction)nameMap.get(funcName);
  }
  
  public boolean conflicts(int addr, int nargs)
  {
    NamedFunction func = (NamedFunction)addrMap.get(Integer.valueOf(addr));
    return func == null ? false : func.conflicts(nargs);
  }
  
  public FunctionLibrary(TypeMap typeMap) throws IOException
  {
    addrMap = new TreeMap();
    nameMap = new TreeMap();
    this.typeMap = typeMap;
  }
  
  public void load(File f) throws IOException
  {
    List<String> lines = IOUtils.readTextFile(f, false);
    
    for (String line : lines)
    {
      String[] tokens = line.split("\\s*:\\s*");
      if (tokens.length != 3) {
        throw new RuntimeException(String.format("Invalid line in %s: %s", new Object[] { f.getName(), line }));
      }
      int addr = (int)Long.parseLong(tokens[0], 16);
      NamedFunction func = (NamedFunction)addrMap.get(Integer.valueOf(addr));
      
      if (func == null)
      {
        func = new NamedFunction(addr, tokens[1]);
        addrMap.put(Integer.valueOf(addr), func);
        nameMap.put(tokens[1], func);
      }
      
      if (tokens[2].equals("*"))
      {
        hasWildcard = true;
      }
      else
      {
        String[] args = tokens[2].split("\\s*,\\s+");
        
        if (func.getArguments(args.length) != null) {
          throw new RuntimeException("Function signature conflicts with existing entry: " + line);
        }
        if ((args.length == 1) && (args[0].equals("none"))) {
          func.add(new ArgumentList(this));
        } else {
          func.add(new ArgumentList(this, args));
        }
      }
    }
  }
  
  protected StructType getType(String name) {
    StructType type = typeMap.get(name);
    
    if (type == null) {
      throw new RuntimeException("Function library references unknown type: " + name);
    }
    return typeMap.get(name);
  }
  

  public Iterator<NamedFunction> iterator()
  {
    return addrMap.values().iterator();
  }
}
