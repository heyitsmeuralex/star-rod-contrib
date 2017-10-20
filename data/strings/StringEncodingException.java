package data.strings;

public class StringEncodingException extends RuntimeException
{
  private static final long serialVersionUID = 1L;
  
  public StringEncodingException(String msg)
  {
    super(msg);
  }
}
