package data.shared.decoder;

public class HintException extends RuntimeException
{
  private static final long serialVersionUID = 1L;
  
  public HintException(String msg)
  {
    super(msg);
  }
  
  public HintException(String format, Object... args)
  {
    super(String.format(format, args));
  }
}
