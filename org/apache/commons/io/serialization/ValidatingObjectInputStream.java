package org.apache.commons.io.serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
































public class ValidatingObjectInputStream
  extends ObjectInputStream
{
  private final List<ClassNameMatcher> acceptMatchers = new ArrayList();
  private final List<ClassNameMatcher> rejectMatchers = new ArrayList();
  







  public ValidatingObjectInputStream(InputStream input)
    throws IOException
  {
    super(input);
  }
  



  private void validateClassName(String name)
    throws InvalidClassException
  {
    for (ClassNameMatcher m : rejectMatchers) {
      if (m.matches(name)) {
        invalidClassNameFound(name);
      }
    }
    
    boolean ok = false;
    for (ClassNameMatcher m : acceptMatchers) {
      if (m.matches(name)) {
        ok = true;
        break;
      }
    }
    if (!ok) {
      invalidClassNameFound(name);
    }
  }
  






  protected void invalidClassNameFound(String className)
    throws InvalidClassException
  {
    throw new InvalidClassException("Class name not accepted: " + className);
  }
  
  protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException
  {
    validateClassName(osc.getName());
    return super.resolveClass(osc);
  }
  






  public ValidatingObjectInputStream accept(Class<?>... classes)
  {
    for (Class<?> c : classes) {
      acceptMatchers.add(new FullClassNameMatcher(new String[] { c.getName() }));
    }
    return this;
  }
  






  public ValidatingObjectInputStream reject(Class<?>... classes)
  {
    for (Class<?> c : classes) {
      rejectMatchers.add(new FullClassNameMatcher(new String[] { c.getName() }));
    }
    return this;
  }
  







  public ValidatingObjectInputStream accept(String... patterns)
  {
    for (String pattern : patterns) {
      acceptMatchers.add(new WildcardClassNameMatcher(pattern));
    }
    return this;
  }
  







  public ValidatingObjectInputStream reject(String... patterns)
  {
    for (String pattern : patterns) {
      rejectMatchers.add(new WildcardClassNameMatcher(pattern));
    }
    return this;
  }
  






  public ValidatingObjectInputStream accept(Pattern pattern)
  {
    acceptMatchers.add(new RegexpClassNameMatcher(pattern));
    return this;
  }
  






  public ValidatingObjectInputStream reject(Pattern pattern)
  {
    rejectMatchers.add(new RegexpClassNameMatcher(pattern));
    return this;
  }
  






  public ValidatingObjectInputStream accept(ClassNameMatcher m)
  {
    acceptMatchers.add(m);
    return this;
  }
  






  public ValidatingObjectInputStream reject(ClassNameMatcher m)
  {
    rejectMatchers.add(m);
    return this;
  }
}
