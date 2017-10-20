package data.shared.struct;

import data.shared.BaseStruct;
import data.shared.decoder.DataDecoder;
import data.shared.decoder.IPrint;
import data.shared.decoder.IScan;
import data.shared.decoder.Pointer;
import data.shared.encoder.DataEncoder;
import data.shared.encoder.IParseOffset;
import data.shared.encoder.IPatchLength;
import data.shared.encoder.IReplaceExpression;
import data.shared.encoder.IReplaceSpecial;
import data.shared.encoder.InvalidExpressionException;
import data.shared.encoder.Patch;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.util.List;





















public class StructType
{
  public static final int VARIABLE_SIZE = 1;
  public static final int UNIQUE = 2;
  private final String name;
  private final StructType parent;
  public IScan scanner = BaseStruct.instance;
  public IPrint printer = BaseStruct.instance;
  
  public IParseOffset parseOffset = BaseStruct.instance;
  public IReplaceExpression replaceExpression = BaseStruct.instance;
  public IReplaceSpecial replaceConstants = BaseStruct.instance;
  public IPatchLength patchLength = BaseStruct.instance;
  
  public final boolean hasKnownSize;
  
  public final boolean isUnique;
  public int count = 0;
  
  public StructType(TypeMap types, String name)
  {
    this(types, name, 0);
  }
  
  public StructType(TypeMap types, String name, int flags)
  {
    this.name = name;
    this.name.hashCode();
    
    parent = null;
    
    hasKnownSize = ((flags & 0x1) == 0);
    isUnique = ((flags & 0x2) != 0);
    
    types.add(this);
  }
  
  public StructType(TypeMap types, String name, StructType parent)
  {
    this(types, name, parent, 0);
  }
  
  public StructType(TypeMap types, String name, StructType parent, int flags)
  {
    this.name = name;
    this.name.hashCode();
    
    if (isUnique) {
      throw new IllegalArgumentException("StructType " + name + " cannot extend unique parent " + parent);
    }
    
    this.parent = parent;
    scanner = null;
    printer = null;
    parseOffset = null;
    replaceExpression = null;
    replaceConstants = null;
    patchLength = null;
    
    hasKnownSize = ((flags & 0x1) == 0);
    isUnique = ((flags & 0x2) != 0);
    
    types.add(this);
  }
  
  public void bind(BaseStruct<?, ?> type)
  {
    scanner = type;
    printer = type;
    parseOffset = type;
    replaceExpression = type;
    replaceConstants = type;
    patchLength = type;
  }
  
  public String toString()
  {
    return name;
  }
  





  public void scan(DataDecoder decoder, Pointer ptr, ByteBuffer fileBuffer)
  {
    if ((parent != null) && (scanner == null)) {
      parent.scan(decoder, ptr, fileBuffer);
    } else {
      scanner.scan(decoder, ptr, fileBuffer);
    }
  }
  
  public void print(DataDecoder decoder, Pointer ptr, ByteBuffer fileBuffer, PrintWriter pw)
  {
    if ((parent != null) && (printer == null)) {
      parent.print(decoder, ptr, fileBuffer, pw);
    } else {
      printer.print(decoder, ptr, fileBuffer, pw);
    }
  }
  




  public Patch parseStructOffset(DataEncoder encoder, String offsetName)
  {
    if ((parent != null) && (parseOffset == null)) {
      return parent.parseStructOffset(encoder, offsetName);
    }
    return parseOffset.parseStructOffset(encoder, offsetName);
  }
  



  public void replaceExpression(DataEncoder encoder, String[] args, List<String> newTokenList)
    throws InvalidExpressionException
  {
    if ((parent != null) && (parseOffset == null)) {
      parent.replaceExpression(encoder, args, newTokenList);
    } else {
      replaceExpression.replaceExpression(encoder, args, newTokenList);
    }
  }
  




  public void replaceSpecial(DataEncoder encoder, Patch patch)
  {
    if ((parent != null) && (parseOffset == null)) {
      parent.replaceSpecial(encoder, patch);
    } else {
      replaceConstants.replaceStructConstants(encoder, patch);
    }
  }
  
  public int getPatchLength(DataEncoder encoder, Patch patch)
  {
    if ((parent != null) && (parseOffset == null)) {
      return parent.getPatchLength(encoder, patch);
    }
    return patchLength.getPatchLength(encoder, patch);
  }
  
  public boolean isTypeOf(StructType t)
  {
    if (this == t) {
      return true;
    }
    if (parent == null) {
      return false;
    }
    return parent.isTypeOf(t);
  }
}
