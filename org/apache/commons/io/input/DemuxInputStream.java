package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;























public class DemuxInputStream
  extends InputStream
{
  private final InheritableThreadLocal<InputStream> m_streams = new InheritableThreadLocal();
  


  public DemuxInputStream() {}
  


  public InputStream bindStream(InputStream input)
  {
    InputStream oldValue = (InputStream)m_streams.get();
    m_streams.set(input);
    return oldValue;
  }
  






  public void close()
    throws IOException
  {
    InputStream input = (InputStream)m_streams.get();
    if (null != input)
    {
      input.close();
    }
  }
  







  public int read()
    throws IOException
  {
    InputStream input = (InputStream)m_streams.get();
    if (null != input)
    {
      return input.read();
    }
    

    return -1;
  }
}
