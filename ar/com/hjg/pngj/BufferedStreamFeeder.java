package ar.com.hjg.pngj;

import java.io.IOException;
import java.io.InputStream;





public class BufferedStreamFeeder
{
  private InputStream stream;
  private byte[] buf;
  private int pendinglen;
  private int offset;
  private boolean eof = false;
  private boolean closeStream = true;
  private boolean failIfNoFeed = false;
  
  private static final int DEFAULTSIZE = 8192;
  
  public BufferedStreamFeeder(InputStream is)
  {
    this(is, 8192);
  }
  
  public BufferedStreamFeeder(InputStream is, int bufsize) {
    stream = is;
    buf = new byte[bufsize < 1 ? 8192 : bufsize];
  }
  




  public InputStream getStream()
  {
    return stream;
  }
  




  public int feed(IBytesConsumer consumer)
  {
    return feed(consumer, Integer.MAX_VALUE);
  }
  




  public int feed(IBytesConsumer consumer, int maxbytes)
  {
    if (pendinglen == 0)
      refillBuffer();
    int tofeed = (maxbytes >= 0) && (maxbytes < pendinglen) ? maxbytes : pendinglen;
    int n = 0;
    if (tofeed > 0) {
      n = consumer.consume(buf, offset, tofeed);
      if (n > 0) {
        offset += n;
        pendinglen -= n;
      }
    }
    if ((n < 1) && (failIfNoFeed))
      throw new PngjInputException("Failed to feed bytes (premature ending?)");
    return n;
  }
  






  public long feedAll(IBytesConsumer consumer)
  {
    long n = 0L;
    while (hasMoreToFeed()) {
      int n1 = feed(consumer);
      if (n1 < 1)
        break;
      n += n1;
    }
    return n;
  }
  







  public boolean feedFixed(IBytesConsumer consumer, int nbytes)
  {
    int remain = nbytes;
    while (remain > 0) {
      int n = feed(consumer, remain);
      if (n < 1)
        return false;
      remain -= n;
    }
    return true;
  }
  


  protected void refillBuffer()
  {
    if ((pendinglen > 0) || (eof)) {
      return;
    }
    try {
      offset = 0;
      pendinglen = stream.read(buf);
      if (pendinglen < 0) {
        close();
        return;
      }
      return;
    } catch (IOException e) {
      throw new PngjInputException(e);
    }
  }
  



  public boolean hasMoreToFeed()
  {
    if (eof) {
      return pendinglen > 0;
    }
    refillBuffer();
    return pendinglen > 0;
  }
  


  public void setCloseStream(boolean closeStream)
  {
    this.closeStream = closeStream;
  }
  








  public void close()
  {
    eof = true;
    buf = null;
    pendinglen = 0;
    offset = 0;
    if ((stream != null) && (closeStream)) {
      try {
        stream.close();
      }
      catch (Exception e) {}
    }
    
    stream = null;
  }
  





  public void setInputStream(InputStream is)
  {
    stream = is;
    eof = false;
  }
  


  public boolean isEof()
  {
    return eof;
  }
  





  public void setFailIfNoFeed(boolean failIfNoFeed)
  {
    this.failIfNoFeed = failIfNoFeed;
  }
}
