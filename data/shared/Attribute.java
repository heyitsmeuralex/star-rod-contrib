package data.shared;

import data.shared.decoder.DataDecoder;
import java.io.PrintWriter;
import java.nio.ByteBuffer;


public class Attribute
{
  public final String name;
  public final String comment;
  public final int offset;
  public final int length;
  private final Style style;
  private final boolean decimal;
  
  public static enum Style
  {
    Bytes, 
    Shorts, 
    Ints;
    
    private Style() {}
  }
  
  public Attribute(String name, int offset, int length, Style style, boolean decimal) { this(name, offset, length, style, decimal, ""); }
  

  public Attribute(String name, int offset, int length, Style style, boolean decimal, String comment)
  {
    this.name = name;
    this.comment = comment;
    this.offset = offset;
    this.length = length;
    this.style = style;
    this.decimal = decimal;
  }
  
  public void print(DataDecoder decoder, ByteBuffer bb, PrintWriter pw)
  {
    bb.position(offset);
    String format = decimal ? "d`" : "X";
    pw.printf("%-13s ", new Object[] { "[" + name + "]" });
    
    switch (1.$SwitchMap$data$shared$Attribute$Style[style.ordinal()])
    {
    case 1: 
      for (int i = 0; i < length; i++)
        pw.printf("%3" + format + "b ", new Object[] { Byte.valueOf(bb.get()) });
      break;
    case 2: 
      for (int i = 0; i < length; i += 2)
        pw.printf("%3" + format + "s ", new Object[] { Short.valueOf(bb.getShort()) });
      break;
    case 3: 
      for (int i = 0; i < length; i += 4) {
        pw.printf("%s ", new Object[] { decoder.getVariableName(bb.getInt()) });
      }
    }
    
    if (comment.isEmpty()) {
      pw.println();
    } else {
      pw.println("% " + comment);
    }
  }
}
