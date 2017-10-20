package data.shared;

import data.shared.struct.StructType;
import data.shared.struct.TypeMap;
import data.shared.struct.other.DisplayList;
import data.shared.struct.other.StringASCII;
import data.shared.struct.other.VectorList;





















public class SharedStructTypes
{
  public SharedStructTypes() {}
  
  public static final TypeMap sharedTypes = new TypeMap();
  
  public static final StructType UnknownT = new StructType(sharedTypes, "Unknown", 1);
  public static final StructType FunctionT = new StructType(sharedTypes, "Function");
  public static final StructType JumpTableT = new StructType(sharedTypes, "JumpTable");
  public static final StructType ScriptT = new StructType(sharedTypes, "Script");
  public static final StructType StringT = new StructType(sharedTypes, "ASCII");
  
  public static final StructType ByteTableT = new StructType(sharedTypes, "ByteTable", 1);
  public static final StructType ShortTableT = new StructType(sharedTypes, "ShortTable", 1);
  public static final StructType IntTableT = new StructType(sharedTypes, "IntTable", 1);
  public static final StructType DataTableT = new StructType(sharedTypes, "DataTable", 1);
  public static final StructType FloatTableT = new StructType(sharedTypes, "FloatTable", 1);
  public static final StructType ConstDoubleT = new StructType(sharedTypes, "ConstDouble");
  
  public static final StructType VectorListT = new StructType(sharedTypes, "VectorList");
  
  public static final StructType DisplayListT = new StructType(sharedTypes, "DisplayList");
  public static final StructType VertexListT = new StructType(sharedTypes, "VertexList", 1);
  
  static { StringT.bind(StringASCII.instance);
    VectorListT.bind(VectorList.instance);
    DisplayListT.bind(DisplayList.instance);
  }
}
