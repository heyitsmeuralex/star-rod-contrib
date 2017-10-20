package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;



































public class BoundedInputStream
  extends InputStream
{
  private final InputStream in;
  private final long max;
  private long pos = 0L;
  

  private long mark = -1L;
  

  private boolean propagateClose = true;
  








  public BoundedInputStream(InputStream in, long size)
  {
    max = size;
    this.in = in;
  }
  





  public BoundedInputStream(InputStream in)
  {
    this(in, -1L);
  }
  






  public int read()
    throws IOException
  {
    if ((max >= 0L) && (pos >= max)) {
      return -1;
    }
    int result = in.read();
    pos += 1L;
    return result;
  }
  






  public int read(byte[] b)
    throws IOException
  {
    return read(b, 0, b.length);
  }
  








  public int read(byte[] b, int off, int len)
    throws IOException
  {
    if ((max >= 0L) && (pos >= max)) {
      return -1;
    }
    long maxRead = max >= 0L ? Math.min(len, max - pos) : len;
    int bytesRead = in.read(b, off, (int)maxRead);
    
    if (bytesRead == -1) {
      return -1;
    }
    
    pos += bytesRead;
    return bytesRead;
  }
  





  public long skip(long n)
    throws IOException
  {
    long toSkip = max >= 0L ? Math.min(n, max - pos) : n;
    long skippedBytes = in.skip(toSkip);
    pos += skippedBytes;
    return skippedBytes;
  }
  


  public int available()
    throws IOException
  {
    if ((max >= 0L) && (pos >= max)) {
      return 0;
    }
    return in.available();
  }
  




  public String toString()
  {
    return in.toString();
  }
  




  public void close()
    throws IOException
  {
    if (propagateClose) {
      in.close();
    }
  }
  



  public synchronized void reset()
    throws IOException
  {
    in.reset();
    pos = mark;
  }
  




  public synchronized void mark(int readlimit)
  {
    in.mark(readlimit);
    mark = pos;
  }
  




  public boolean markSupported()
  {
    return in.markSupported();
  }
  







  public boolean isPropagateClose()
  {
    return propagateClose;
  }
  








  public void setPropagateClose(boolean propagateClose)
  {
    this.propagateClose = propagateClose;
  }
}
