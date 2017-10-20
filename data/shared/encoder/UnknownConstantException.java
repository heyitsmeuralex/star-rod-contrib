package data.shared.encoder;

public class UnknownConstantException extends RuntimeException
{
  private static final long serialVersionUID = 1L;
  
  public UnknownConstantException(String msg)
  {
    super(msg);
  }
}
