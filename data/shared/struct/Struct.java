package data.shared.struct;

import data.shared.Location;
import data.shared.Location.Type;
import data.shared.encoder.DataEncoder;
import data.shared.encoder.InvalidExpressionException;
import data.shared.encoder.Patch;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;












public class Struct
{
  public final String name;
  private final StructType type;
  public final boolean dumped;
  public final Location originalLocation;
  public final int originalSize;
  public boolean deleted = false;
  public boolean forceDelete = false;
  

  public boolean extended = false;
  public boolean shrunken = false;
  
  public Location finalLocation;
  
  public int finalSize;
  public boolean patched = false;
  
  public final List<Patch> patchList;
  public ByteBuffer patchedBuffer;
  public boolean replaceOriginal = true;
  
  public Struct(StructType type, String name)
  {
    this.type = type;
    this.name = name;
    patchList = new LinkedList();
    
    originalLocation = null;
    originalSize = -1;
    dumped = false;
  }
  
  public Struct(StructType type, String name, int offset, int size)
  {
    this.type = type;
    this.name = name;
    patchList = new LinkedList();
    
    originalLocation = new Location(offset, Location.Type.Offset);
    originalSize = size;
    dumped = true;
    
    finalLocation = new Location(originalLocation);
    finalSize = originalSize;
  }
  





  public Patch parseStructOffset(DataEncoder encoder, String offsetName)
  {
    return type.parseStructOffset(encoder, offsetName);
  }
  
  public void replaceSpecial(DataEncoder encoder, Patch patch)
  {
    type.replaceSpecial(encoder, patch);
  }
  
  public void replaceExpression(DataEncoder encoder, String[] args, List<String> newTokenList) throws InvalidExpressionException
  {
    type.replaceExpression(encoder, args, newTokenList);
  }
  
  public int getPatchLength(DataEncoder encoder, Patch patch)
  {
    return type.getPatchLength(encoder, patch);
  }
  
  public boolean isTypeOf(StructType t)
  {
    return type.isTypeOf(t);
  }
  
  public String getTypeName()
  {
    return type.toString();
  }
}
