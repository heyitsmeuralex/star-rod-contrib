package org.apache.commons.math;

import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ConcurrentModificationException;
import java.util.Locale;
import java.util.NoSuchElementException;
import org.apache.commons.math.exception.MathThrowable;
import org.apache.commons.math.exception.util.DummyLocalizable;
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;






































public class MathRuntimeException
  extends RuntimeException
  implements MathThrowable
{
  private static final long serialVersionUID = 9058794795027570002L;
  private final Localizable pattern;
  private final Object[] arguments;
  
  @Deprecated
  public MathRuntimeException(String pattern, Object... arguments)
  {
    this(new DummyLocalizable(pattern), arguments);
  }
  







  public MathRuntimeException(Localizable pattern, Object... arguments)
  {
    this.pattern = pattern;
    this.arguments = (arguments == null ? new Object[0] : (Object[])arguments.clone());
  }
  






  public MathRuntimeException(Throwable rootCause)
  {
    super(rootCause);
    pattern = LocalizedFormats.SIMPLE_MESSAGE;
    arguments = new Object[] { rootCause == null ? "" : rootCause.getMessage() };
  }
  










  @Deprecated
  public MathRuntimeException(Throwable rootCause, String pattern, Object... arguments)
  {
    this(rootCause, new DummyLocalizable(pattern), arguments);
  }
  










  public MathRuntimeException(Throwable rootCause, Localizable pattern, Object... arguments)
  {
    super(rootCause);
    this.pattern = pattern;
    this.arguments = (arguments == null ? new Object[0] : (Object[])arguments.clone());
  }
  








  private static String buildMessage(Locale locale, Localizable pattern, Object... arguments)
  {
    return new MessageFormat(pattern.getLocalizedString(locale), locale).format(arguments);
  }
  




  @Deprecated
  public String getPattern()
  {
    return pattern.getSourceString();
  }
  




  public Localizable getSpecificPattern()
  {
    return null;
  }
  




  public Localizable getGeneralPattern()
  {
    return pattern;
  }
  
  public Object[] getArguments()
  {
    return (Object[])arguments.clone();
  }
  





  public String getMessage(Locale locale)
  {
    if (pattern != null) {
      return buildMessage(locale, pattern, arguments);
    }
    return "";
  }
  

  public String getMessage()
  {
    return getMessage(Locale.US);
  }
  

  public String getLocalizedMessage()
  {
    return getMessage(Locale.getDefault());
  }
  



  public void printStackTrace()
  {
    printStackTrace(System.err);
  }
  





  public void printStackTrace(PrintStream out)
  {
    synchronized (out) {
      PrintWriter pw = new PrintWriter(out, false);
      printStackTrace(pw);
      
      pw.flush();
    }
  }
  








  @Deprecated
  public static ArithmeticException createArithmeticException(String pattern, Object... arguments)
  {
    return createArithmeticException(new DummyLocalizable(pattern), arguments);
  }
  








  public static ArithmeticException createArithmeticException(Localizable pattern, final Object... arguments)
  {
    new ArithmeticException()
    {
      private static final long serialVersionUID = 5305498554076846637L;
      


      public String getMessage()
      {
        return MathRuntimeException.buildMessage(Locale.US, val$pattern, arguments);
      }
      

      public String getLocalizedMessage()
      {
        return MathRuntimeException.buildMessage(Locale.getDefault(), val$pattern, arguments);
      }
    };
  }
  









  @Deprecated
  public static ArrayIndexOutOfBoundsException createArrayIndexOutOfBoundsException(String pattern, Object... arguments)
  {
    return createArrayIndexOutOfBoundsException(new DummyLocalizable(pattern), arguments);
  }
  








  public static ArrayIndexOutOfBoundsException createArrayIndexOutOfBoundsException(Localizable pattern, final Object... arguments)
  {
    new ArrayIndexOutOfBoundsException()
    {
      private static final long serialVersionUID = 6718518191249632175L;
      


      public String getMessage()
      {
        return MathRuntimeException.buildMessage(Locale.US, val$pattern, arguments);
      }
      

      public String getLocalizedMessage()
      {
        return MathRuntimeException.buildMessage(Locale.getDefault(), val$pattern, arguments);
      }
    };
  }
  









  @Deprecated
  public static EOFException createEOFException(String pattern, Object... arguments)
  {
    return createEOFException(new DummyLocalizable(pattern), arguments);
  }
  








  public static EOFException createEOFException(Localizable pattern, final Object... arguments)
  {
    new EOFException()
    {
      private static final long serialVersionUID = 6067985859347601503L;
      


      public String getMessage()
      {
        return MathRuntimeException.buildMessage(Locale.US, val$pattern, arguments);
      }
      

      public String getLocalizedMessage()
      {
        return MathRuntimeException.buildMessage(Locale.getDefault(), val$pattern, arguments);
      }
    };
  }
  











  public static IOException createIOException(Throwable rootCause)
  {
    IOException ioe = new IOException(rootCause.getLocalizedMessage());
    ioe.initCause(rootCause);
    return ioe;
  }
  








  @Deprecated
  public static IllegalArgumentException createIllegalArgumentException(String pattern, Object... arguments)
  {
    return createIllegalArgumentException(new DummyLocalizable(pattern), arguments);
  }
  








  public static IllegalArgumentException createIllegalArgumentException(Localizable pattern, final Object... arguments)
  {
    new IllegalArgumentException()
    {
      private static final long serialVersionUID = -4284649691002411505L;
      


      public String getMessage()
      {
        return MathRuntimeException.buildMessage(Locale.US, val$pattern, arguments);
      }
      

      public String getLocalizedMessage()
      {
        return MathRuntimeException.buildMessage(Locale.getDefault(), val$pattern, arguments);
      }
    };
  }
  







  public static IllegalArgumentException createIllegalArgumentException(Throwable rootCause)
  {
    IllegalArgumentException iae = new IllegalArgumentException(rootCause.getLocalizedMessage());
    iae.initCause(rootCause);
    return iae;
  }
  








  @Deprecated
  public static IllegalStateException createIllegalStateException(String pattern, Object... arguments)
  {
    return createIllegalStateException(new DummyLocalizable(pattern), arguments);
  }
  








  public static IllegalStateException createIllegalStateException(Localizable pattern, final Object... arguments)
  {
    new IllegalStateException()
    {
      private static final long serialVersionUID = 6880901520234515725L;
      


      public String getMessage()
      {
        return MathRuntimeException.buildMessage(Locale.US, val$pattern, arguments);
      }
      

      public String getLocalizedMessage()
      {
        return MathRuntimeException.buildMessage(Locale.getDefault(), val$pattern, arguments);
      }
    };
  }
  









  @Deprecated
  public static ConcurrentModificationException createConcurrentModificationException(String pattern, Object... arguments)
  {
    return createConcurrentModificationException(new DummyLocalizable(pattern), arguments);
  }
  








  public static ConcurrentModificationException createConcurrentModificationException(Localizable pattern, final Object... arguments)
  {
    new ConcurrentModificationException()
    {
      private static final long serialVersionUID = -1878427236170442052L;
      


      public String getMessage()
      {
        return MathRuntimeException.buildMessage(Locale.US, val$pattern, arguments);
      }
      

      public String getLocalizedMessage()
      {
        return MathRuntimeException.buildMessage(Locale.getDefault(), val$pattern, arguments);
      }
    };
  }
  









  @Deprecated
  public static NoSuchElementException createNoSuchElementException(String pattern, Object... arguments)
  {
    return createNoSuchElementException(new DummyLocalizable(pattern), arguments);
  }
  








  public static NoSuchElementException createNoSuchElementException(Localizable pattern, final Object... arguments)
  {
    new NoSuchElementException()
    {
      private static final long serialVersionUID = 1632410088350355086L;
      


      public String getMessage()
      {
        return MathRuntimeException.buildMessage(Locale.US, val$pattern, arguments);
      }
      

      public String getLocalizedMessage()
      {
        return MathRuntimeException.buildMessage(Locale.getDefault(), val$pattern, arguments);
      }
    };
  }
  











  @Deprecated
  public static UnsupportedOperationException createUnsupportedOperationException(Localizable pattern, final Object... arguments)
  {
    new UnsupportedOperationException()
    {
      private static final long serialVersionUID = -4284649691002411505L;
      


      public String getMessage()
      {
        return MathRuntimeException.buildMessage(Locale.US, val$pattern, arguments);
      }
      

      public String getLocalizedMessage()
      {
        return MathRuntimeException.buildMessage(Locale.getDefault(), val$pattern, arguments);
      }
    };
  }
  









  @Deprecated
  public static NullPointerException createNullPointerException(String pattern, Object... arguments)
  {
    return createNullPointerException(new DummyLocalizable(pattern), arguments);
  }
  









  @Deprecated
  public static NullPointerException createNullPointerException(Localizable pattern, final Object... arguments)
  {
    new NullPointerException()
    {
      private static final long serialVersionUID = 451965530686593945L;
      


      public String getMessage()
      {
        return MathRuntimeException.buildMessage(Locale.US, val$pattern, arguments);
      }
      

      public String getLocalizedMessage()
      {
        return MathRuntimeException.buildMessage(Locale.getDefault(), val$pattern, arguments);
      }
    };
  }
  












  @Deprecated
  public static ParseException createParseException(int offset, String pattern, Object... arguments)
  {
    return createParseException(offset, new DummyLocalizable(pattern), arguments);
  }
  











  public static ParseException createParseException(int offset, final Localizable pattern, final Object... arguments)
  {
    new ParseException(null, offset)
    {
      private static final long serialVersionUID = 8153587599409010120L;
      


      public String getMessage()
      {
        return MathRuntimeException.buildMessage(Locale.US, pattern, arguments);
      }
      

      public String getLocalizedMessage()
      {
        return MathRuntimeException.buildMessage(Locale.getDefault(), pattern, arguments);
      }
    };
  }
  





  public static RuntimeException createInternalError(Throwable cause)
  {
    String argument = "https://issues.apache.org/jira/browse/MATH";
    
    new RuntimeException(cause)
    {
      private static final long serialVersionUID = -201865440834027016L;
      


      public String getMessage()
      {
        return MathRuntimeException.buildMessage(Locale.US, LocalizedFormats.INTERNAL_ERROR, new Object[] { "https://issues.apache.org/jira/browse/MATH" });
      }
      

      public String getLocalizedMessage()
      {
        return MathRuntimeException.buildMessage(Locale.getDefault(), LocalizedFormats.INTERNAL_ERROR, new Object[] { "https://issues.apache.org/jira/browse/MATH" });
      }
    };
  }
}
