package org.apache.commons.math.exception.util;

import java.text.MessageFormat;
import java.util.Locale;


































public class MessageFactory
{
  private MessageFactory() {}
  
  public static String buildMessage(Locale locale, Localizable pattern, Object... arguments)
  {
    return buildMessage(locale, null, pattern, arguments);
  }
  













  public static String buildMessage(Locale locale, Localizable specific, Localizable general, Object... arguments)
  {
    StringBuilder sb = new StringBuilder();
    if (general != null) {
      MessageFormat fmt = new MessageFormat(general.getLocalizedString(locale), locale);
      sb.append(fmt.format(arguments));
    }
    if (specific != null) {
      if (general != null) {
        sb.append(": ");
      }
      MessageFormat fmt = new MessageFormat(specific.getLocalizedString(locale), locale);
      sb.append(fmt.format(arguments));
    }
    
    return sb.toString();
  }
}
