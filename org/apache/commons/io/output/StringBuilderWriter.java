package org.apache.commons.io.output;

import java.io.Serializable;
import java.io.Writer;





























public class StringBuilderWriter
  extends Writer
  implements Serializable
{
  private final StringBuilder builder;
  
  public StringBuilderWriter()
  {
    builder = new StringBuilder();
  }
  




  public StringBuilderWriter(int capacity)
  {
    builder = new StringBuilder(capacity);
  }
  




  public StringBuilderWriter(StringBuilder builder)
  {
    this.builder = (builder != null ? builder : new StringBuilder());
  }
  






  public Writer append(char value)
  {
    builder.append(value);
    return this;
  }
  






  public Writer append(CharSequence value)
  {
    builder.append(value);
    return this;
  }
  








  public Writer append(CharSequence value, int start, int end)
  {
    builder.append(value, start, end);
    return this;
  }
  






  public void close() {}
  





  public void flush() {}
  





  public void write(String value)
  {
    if (value != null) {
      builder.append(value);
    }
  }
  







  public void write(char[] value, int offset, int length)
  {
    if (value != null) {
      builder.append(value, offset, length);
    }
  }
  




  public StringBuilder getBuilder()
  {
    return builder;
  }
  





  public String toString()
  {
    return builder.toString();
  }
}
