package org.apache.commons.io.input;

import java.io.IOException;
import java.io.InputStream;





















public class WindowsLineEndingInputStream
  extends InputStream
{
  private boolean slashRSeen = false;
  
  private boolean slashNSeen = false;
  
  private boolean injectSlashN = false;
  
  private boolean eofSeen = false;
  


  private final InputStream target;
  

  private final boolean ensureLineFeedAtEndOfFile;
  


  public WindowsLineEndingInputStream(InputStream in, boolean ensureLineFeedAtEndOfFile)
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
    slashRSeen = (target == 13);
    slashNSeen = (target == 10);
    return target;
  }
  


  public int read()
    throws IOException
  {
    if (eofSeen)
      return eofGame();
    if (injectSlashN) {
      injectSlashN = false;
      return 10;
    }
    boolean prevWasSlashR = slashRSeen;
    int target = readWithUpdate();
    if (eofSeen) {
      return eofGame();
    }
    if ((target == 10) && 
      (!prevWasSlashR))
    {
      injectSlashN = true;
      return 13;
    }
    
    return target;
  }
  





  private int eofGame()
  {
    if (!ensureLineFeedAtEndOfFile) {
      return -1;
    }
    if ((!slashNSeen) && (!slashRSeen)) {
      slashRSeen = true;
      return 13;
    }
    if (!slashNSeen) {
      slashRSeen = false;
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
    throw new UnsupportedOperationException("Mark not supported");
  }
}
