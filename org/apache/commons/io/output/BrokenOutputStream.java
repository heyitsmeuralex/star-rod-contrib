package org.apache.commons.io.output;

import java.io.IOException;
import java.io.OutputStream;
































public class BrokenOutputStream
  extends OutputStream
{
  private final IOException exception;
  
  public BrokenOutputStream(IOException exception)
  {
    this.exception = exception;
  }
  


  public BrokenOutputStream()
  {
    this(new IOException("Broken output stream"));
  }
  





  public void write(int b)
    throws IOException
  {
    throw exception;
  }
  




  public void flush()
    throws IOException
  {
    throw exception;
  }
  




  public void close()
    throws IOException
  {
    throw exception;
  }
}
