package data.shared.struct;

import java.util.Collection;
import java.util.HashMap;

public class TypeMap implements Iterable<StructType>
{
  private HashMap<String, StructType> nameMap;
  
  public TypeMap()
  {
    nameMap = new HashMap();
  }
  
  public void add(StructType s)
  {
    nameMap.put(s.toString(), s);
  }
  
  public void add(TypeMap types)
  {
    nameMap.putAll(nameMap);
  }
  
  public StructType get(String name)
  {
    return (StructType)nameMap.get(name);
  }
  

  public java.util.Iterator<StructType> iterator()
  {
    return nameMap.values().iterator();
  }
}
