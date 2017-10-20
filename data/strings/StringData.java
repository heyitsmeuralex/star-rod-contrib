package data.strings;

import java.io.Serializable;
import java.nio.ByteBuffer;


public class StringData
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  public final byte[] bytes;
  private final String ascii;
  public final int section;
  public int index;
  public final String name;
  
  public StringData(ByteBuffer bb, int section, int index)
  {
    this(bb, section, index, "");
  }
  
  public StringData(byte[] bytes, int section, int index)
  {
    this(bytes, section, index, "");
  }
  
  public StringData(ByteBuffer bb, int section, int index, String name)
  {
    bytes = new byte[bb.remaining()];
    bb.get(bytes);
    this.section = section;
    this.index = index;
    this.name = name;
    
    ascii = StringDecoder.toASCII(bytes);
  }
  
  public StringData(byte[] bytes, int section, int index, String name)
  {
    this.bytes = bytes;
    this.section = section;
    this.index = index;
    this.name = name;
    
    ascii = StringDecoder.toASCII(bytes);
  }
  
  public int getID()
  {
    return section << 16 | index & 0xFFFF;
  }
  
  public String toString()
  {
    return ascii;
  }
  
  public boolean hasName()
  {
    return !name.isEmpty();
  }
}
