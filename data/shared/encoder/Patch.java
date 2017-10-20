package data.shared.encoder;

import data.shared.DataUtils;
import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;


public class Patch
{
  public final String identifier;
  public final int startingPos;
  public final List<String[]> lines;
  public final File source;
  public int length = -1;
  public final int maxLength;
  
  public Patch(File source)
  {
    this(source, "", 0, -1);
  }
  
  public Patch(File source, String identifier, int startingPos)
  {
    this(source, identifier, startingPos, -1);
  }
  
  public Patch(File source, String identifier, int startingPos, int maxLength)
  {
    this.source = source;
    this.identifier = identifier;
    this.startingPos = startingPos;
    lines = new ArrayList();
    this.maxLength = maxLength;
  }
  
  public static Patch createWithOffset(File source, String offsetString)
  {
    if ((offsetString.startsWith("[")) && (offsetString.endsWith("]")))
    {
      String trimmed = offsetString.substring(1, offsetString.length() - 1);
      return new Patch(source, offsetString, DataUtils.parseIntString(trimmed));
    }
    
    throw new IllegalArgumentException("Could not parse offset: " + offsetString + ", missing [].");
  }
  
  public void print()
  {
    System.out.println(identifier);
    for (String[] line : lines)
    {
      for (String s : line)
        System.out.print(s + " ");
      System.out.println();
    }
    System.out.println();
  }
}
