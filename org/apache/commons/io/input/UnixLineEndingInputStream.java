package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;






















public class UnixLineEndingInputStream
  extends InputStream
{
  private boolean slashNSeen = false;
  
  private boolean slashRSeen = false;
  
  private boolean eofSeen = false;
  


  private final InputStream target;
  

  private final boolean ensureLineFeedAtEndOfFile;
  


  public UnixLineEndingInputStream(InputStream in, boolean ensureLineFeedAtEndOfFile)
  {
    target = in;
    this.ensureLineFeedAtEndOfFile = ensureLineFeedAtEndOfFile;
  }
  



  private int readWithUpdate()
    throws IOException
  {
    int target = this.target.read();
    eofSeen = (target == -1);
    if (eofSeen) {
      return target;
    }
    slashNSeen = (target == 10);
    slashRSeen = (target == 13);
    return target;
  }
  


  public int read()
    throws IOException
  {
    boolean previousWasSlashR = slashRSeen;
    if (eofSeen) {
      return eofGame(previousWasSlashR);
    }
    
    int target = readWithUpdate();
    if (eofSeen) {
      return eofGame(previousWasSlashR);
    }
    if (slashRSeen)
    {
      return 10;
    }
    
    if ((previousWasSlashR) && (slashNSeen)) {
      return read();
    }
    
    return target;
  }
  





  private int eofGame(boolean previousWasSlashR)
  {
    if ((previousWasSlashR) || (!ensureLineFeedAtEndOfFile)) {
      return -1;
    }
    if (!slashNSeen) {
      slashNSeen = true;
      return 10;
    }
    return -1;
  }
  




  public void close()
    throws IOException
  {
    super.close();
    target.close();
  }
  



  public synchronized void mark(int readlimit)
  {
    throw new UnsupportedOperationException("Mark notsupported");
  }
}
