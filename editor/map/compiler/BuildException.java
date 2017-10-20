package editor.map.compiler;

public class BuildException extends RuntimeException
{
  private static final long serialVersionUID = 1L;
  
  public BuildException(String msg)
  {
    super(msg);
  }
  
  public BuildException(String format, Object... args)
  {
    super(String.format(format, args));
  }
}
