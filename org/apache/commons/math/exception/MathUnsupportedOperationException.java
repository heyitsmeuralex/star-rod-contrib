package org.apache.commons.math.exception;

import java.util.Locale;
import org.apache.commons.math.exception.util.ArgUtils;
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.exception.util.MessageFactory;



































public class MathUnsupportedOperationException
  extends UnsupportedOperationException
  implements MathThrowable
{
  private static final long serialVersionUID = -6024911025449780478L;
  private final Localizable specific;
  private final Object[] arguments;
  
  public MathUnsupportedOperationException(Object... args)
  {
    this(null, args);
  }
  




  public MathUnsupportedOperationException(Localizable specific, Object... args)
  {
    this.specific = specific;
    arguments = ArgUtils.flatten(args);
  }
  
  public Localizable getSpecificPattern()
  {
    return specific;
  }
  
  public Localizable getGeneralPattern()
  {
    return LocalizedFormats.UNSUPPORTED_OPERATION;
  }
  
  public Object[] getArguments()
  {
    return (Object[])arguments.clone();
  }
  






  public String getMessage(Locale locale)
  {
    return MessageFactory.buildMessage(locale, specific, LocalizedFormats.UNSUPPORTED_OPERATION, arguments);
  }
  




  public String getMessage()
  {
    return getMessage(Locale.US);
  }
  

  public String getLocalizedMessage()
  {
    return getMessage(Locale.getDefault());
  }
}
