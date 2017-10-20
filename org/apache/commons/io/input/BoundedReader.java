package org.apache.commons.io.input;

import java.io.IOException;
import java.io.Reader;



































public class BoundedReader
  extends Reader
{
  private static final int INVALID = -1;
  private final Reader target;
  private int charsRead = 0;
  
  private int markedAt = -1;
  


  private int readAheadLimit;
  

  private final int maxCharsFromTargetReader;
  


  public BoundedReader(Reader target, int maxCharsFromTargetReader)
    throws IOException
  {
    this.target = target;
    this.maxCharsFromTargetReader = maxCharsFromTargetReader;
  }
  




  public void close()
    throws IOException
  {
    target.close();
  }
  




  public void reset()
    throws IOException
  {
    charsRead = markedAt;
    target.reset();
  }
  











  public void mark(int readAheadLimit)
    throws IOException
  {
    this.readAheadLimit = (readAheadLimit - charsRead);
    
    markedAt = charsRead;
    
    target.mark(readAheadLimit);
  }
  






  public int read()
    throws IOException
  {
    if (charsRead >= maxCharsFromTargetReader) {
      return -1;
    }
    
    if ((markedAt >= 0) && (charsRead - markedAt >= readAheadLimit)) {
      return -1;
    }
    charsRead += 1;
    return target.read();
  }
  









  public int read(char[] cbuf, int off, int len)
    throws IOException
  {
    for (int i = 0; i < len; i++) {
      int c = read();
      if (c == -1) {
        return i;
      }
      cbuf[(off + i)] = ((char)c);
    }
    return len;
  }
}
