package data.shared.lib;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;
import util.IOUtils;

public class ScriptLibrary implements Iterable<String>
{
  private TreeMap<Integer, String> addrMap;
  private TreeMap<String, Integer> nameMap;
  
  public boolean has(int addr)
  {
    return addrMap.containsKey(Integer.valueOf(addr));
  }
  
  public String get(int addr)
  {
    return (String)addrMap.get(Integer.valueOf(addr));
  }
  
  public boolean has(String funcName)
  {
    return nameMap.containsKey(funcName);
  }
  
  public int get(String funcName)
  {
    return ((Integer)nameMap.get(funcName)).intValue();
  }
  
  public boolean conflicts(int addr)
  {
    return addrMap.containsKey(Integer.valueOf(addr));
  }
  
  public ScriptLibrary() throws IOException
  {
    addrMap = new TreeMap();
    nameMap = new TreeMap();
  }
  
  public void load(File f) throws IOException
  {
    List<String> lines = IOUtils.readTextFile(f, false);
    
    for (String line : lines)
    {
      String[] tokens = line.split("\\s*:\\s*");
      if (tokens.length != 2) {
        throw new RuntimeException(String.format("Invalid line in %s: %s", new Object[] { f.getName(), line }));
      }
      int addr = (int)Long.parseLong(tokens[0], 16);
      String name = tokens[1];
      addrMap.put(Integer.valueOf(addr), name);
      nameMap.put(name, Integer.valueOf(addr));
    }
  }
  

  public Iterator<String> iterator()
  {
    return addrMap.values().iterator();
  }
}
