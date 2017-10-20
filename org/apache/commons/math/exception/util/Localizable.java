package org.apache.commons.math.exception.util;

import java.io.Serializable;
import java.util.Locale;

public abstract interface Localizable
  extends Serializable
{
  public abstract String getSourceString();
  
  public abstract String getLocalizedString(Locale paramLocale);
}
