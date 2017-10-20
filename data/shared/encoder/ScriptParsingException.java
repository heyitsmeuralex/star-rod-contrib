package data.shared.encoder;

public class ScriptParsingException extends RuntimeException
{
  private static final long serialVersionUID = 1L;
  
  public ScriptParsingException(String msg)
  {
    super(msg);
  }
  
  public ScriptParsingException(String fmt, Object... args)
  {
    super(String.format(fmt, args));
  }
}
