package org.apache.commons.math.exception;

import java.util.Locale;
import org.apache.commons.math.exception.util.Localizable;

public abstract interface MathThrowable
{
  public abstract Localizable getSpecificPattern();
  
  public abstract Localizable getGeneralPattern();
  
  public abstract Object[] getArguments();
  
  public abstract String getMessage(Locale paramLocale);
  
  public abstract String getMessage();
  
  public abstract String getLocalizedMessage();
}
