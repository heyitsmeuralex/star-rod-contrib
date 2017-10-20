package data.shared.decoder;

import data.map.MapStructTypes;
import data.shared.SharedStructTypes;
import data.shared.struct.StructType;
import java.util.Iterator;
import util.IdentityHashSet;


public class Pointer
{
  public StructType type;
  public final int address;
  public int length = 0;
  
  public int newlineHint = 0;
  
  private String descriptor = "";
  private String importName = "";
  
  private String overrideName = "";
  
  public String comment = "";
  
  public static enum Origin {
    UNIDENTIFIED,  DECODED,  HINT,  HEURISTIC;
    private Origin() {} } public Origin origin = Origin.UNIDENTIFIED;
  public boolean root = false;
  

  public final IdentityHashSet<Pointer> children;
  
  public final IdentityHashSet<Pointer> parents;
  
  public final IdentityHashSet<Pointer> ancestors;
  
  public int listLength = -1;
  public int listIndex = -1;
  

  public int npcBattleID = -1;
  public String mapName = "";
  
  public boolean hasKnownSize()
  {
    return (type.hasKnownSize) || (length != 0);
  }
  
  public Pointer(int address)
  {
    this.address = address;
    parents = new IdentityHashSet();
    children = new IdentityHashSet();
    
    ancestors = new IdentityHashSet();
    type = SharedStructTypes.UnknownT;
  }
  
  public void addUniqueChild(Pointer child)
  {
    children.add(child);
    parents.add(this);
  }
  
  public void setType(StructType type)
  {
    this.type = type;
  }
  
  public void setImportAffix(String suffix)
  {
    assert (importName.isEmpty());
    importName = suffix;
  }
  
  public String getDescriptor()
  {
    return descriptor;
  }
  
  public void setDescriptor(String descriptor)
  {
    this.descriptor = descriptor;
  }
  
  public void forceName(String name)
  {
    overrideName = name;
  }
  
  private String getNpcSuffix()
  {
    return listIndex == 0 ? "" : String.format("_%03X", new Object[] { Integer.valueOf(listIndex * 496) });
  }
  

  private String getBaseName()
  {
    if (type.isTypeOf(MapStructTypes.NpcT))
    {
      Pointer parent = (Pointer)parents.iterator().next();
      return parent.getBaseName() + getNpcSuffix();
    }
    

    if (listIndex >= 0) {
      return String.format("_%02X", new Object[] { Integer.valueOf(listIndex) });
    }
    
    if (type.isUnique)
    {
      if (descriptor.isEmpty()) {
        return "";
      }
      return "_" + descriptor;
    }
    

    if (descriptor.isEmpty()) {
      return String.format("_%08X", new Object[] { Integer.valueOf(address) });
    }
    return "_" + descriptor + String.format("_%08X", new Object[] { Integer.valueOf(address) });
  }
  



  public String getImportName()
  {
    if (type.isTypeOf(MapStructTypes.NpcT))
    {
      Pointer parent = (Pointer)parents.iterator().next();
      return parent.getImportName() + getNpcSuffix();
    }
    
    if (!overrideName.isEmpty()) {
      return String.format(importName + "_" + overrideName, new Object[0]);
    }
    return String.format(importName + getBaseName(), new Object[0]);
  }
  





  public String getPointerName()
  {
    if (type.isTypeOf(MapStructTypes.NpcT))
    {
      Pointer parent = (Pointer)parents.iterator().next();
      String typePrefix = type.toString();
      String importSuffix = "_" + importName;
      return "$" + typePrefix + parent.getBaseName() + getNpcSuffix() + importSuffix;
    }
    
    String typePrefix = type.isTypeOf(SharedStructTypes.UnknownT) ? "???" : type.toString();
    String importSuffix = "_" + importName;
    
    if (!overrideName.isEmpty()) {
      return "$" + overrideName + importSuffix;
    }
    return "$" + typePrefix + getBaseName() + importSuffix;
  }
}
