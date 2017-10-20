package data.shared;

import java.nio.ByteBuffer;

public class DataUtils
{
  public DataUtils() {}
  
  public static boolean isInteger(String s)
  {
    try {
      if ((s.endsWith("`")) || (s.endsWith("'"))) {
        Integer.parseInt(s);
      } else
        Long.parseLong(s, 16);
    } catch (NumberFormatException e) {
      return false;
    }
    return true;
  }
  
  public static int parseIntString(String s)
  {
    if ((s.endsWith("`")) || (s.endsWith("'"))) {
      return (int)Long.parseLong(s.substring(0, s.length() - 1));
    }
    return (int)Long.parseLong(s, 16);
  }
  
  public static int getSize(String s)
  {
    char suffix = s.charAt(s.length() - 1);
    
    switch (suffix)
    {
    case 'b': 
      return 1;
    case 's': 
      return 2;
    case 'd': 
      return 8;
    }
    return 4;
  }
  

  public static String combineTokens(String[] tokens)
  {
    return combineTokens(tokens, " ");
  }
  
  public static String combineTokens(String[] tokens, String delimiter)
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < tokens.length - 1; i++)
      sb.append(tokens[i]).append(delimiter);
    sb.append(tokens[(tokens.length - 1)]);
    return sb.toString();
  }
  
  public static void addToBuffer(ByteBuffer buffer, String s)
  {
    char suffix = s.charAt(s.length() - 1);
    String trimmed = s.substring(0, s.length() - 1);
    
    if (s.contains("."))
    {
      switch (suffix) {
      case 's': 
        buffer.putShort((short)(int)(Float.parseFloat(trimmed) * 32767.0F)); break;
      case 'd':  buffer.putDouble(Double.parseDouble(trimmed)); break;
      default:  buffer.putFloat(Float.parseFloat(s));break;
      
      }
      
    } else {
      switch (suffix) {
      case 'b': 
        buffer.put((byte)parseIntString(trimmed)); break;
      case 's':  buffer.putShort((short)parseIntString(trimmed)); break;
      default:  buffer.putInt(parseIntString(s));
      }
    }
  }
  
  public static void writeWord(java.io.RandomAccessFile raf, String s) throws java.io.IOException
  {
    char suffix = s.charAt(s.length() - 1);
    String trimmed = s.substring(0, s.length() - 1);
    
    if (s.contains("."))
    {
      switch (suffix) {
      case 's': 
        raf.writeShort((short)(int)(Float.parseFloat(trimmed) * 32767.0F)); break;
      case 'd':  raf.writeDouble(Double.parseDouble(trimmed)); break;
      default:  raf.writeFloat(Float.parseFloat(s));break;
      
      }
      
    } else {
      switch (suffix) {
      case 'b': 
        raf.writeByte(parseIntString(trimmed)); break;
      case 's':  raf.writeShort(parseIntString(trimmed)); break;
      default:  raf.writeInt(parseIntString(s));
      }
    }
  }
}
