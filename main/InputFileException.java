package main;

import java.io.File;

public class InputFileException
  extends RuntimeException
{
  private static final long serialVersionUID = 1L;
  private final File source;
  private final int line;
  
  public InputFileException(File source, String msg)
  {
    this(source, -1, msg);
  }
  
  public InputFileException(File source, int line, String msg)
  {
    super(msg);
    this.source = source;
    this.line = line;
  }
  
  public InputFileException(File source, String format, Object... args)
  {
    this(source, -1, format, args);
  }
  
  public InputFileException(File source, int line, String format, Object... args)
  {
    super(String.format(format, args));
    this.source = source;
    this.line = line;
  }
  
  public boolean hasLineNumber()
  {
    return line >= 0;
  }
  
  public int getLineNumber()
  {
    return line;
  }
  
  public boolean hasFile()
  {
    return source != null;
  }
  
  public File getFile()
  {
    return source;
  }
}
