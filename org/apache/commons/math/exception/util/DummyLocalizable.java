package org.apache.commons.math.exception.util;

import java.util.Locale;



























public class DummyLocalizable
  implements Localizable
{
  private static final long serialVersionUID = 8843275624471387299L;
  private final String source;
  
  public DummyLocalizable(String source)
  {
    this.source = source;
  }
  
  public String getSourceString()
  {
    return source;
  }
  
  public String getLocalizedString(Locale locale)
  {
    return source;
  }
  

  public String toString()
  {
    return source;
  }
}
