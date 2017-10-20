package util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

public class LogFile
  implements Logger.Listener
{
  private final PrintWriter pw;
  
  public LogFile(File logFile, boolean append)
    throws IOException
  {
    OutputStream buffer = new BufferedOutputStream(new FileOutputStream(logFile, append));
    pw = new PrintWriter(buffer);
    Logger.addListener(this);
  }
  

  public void post(Logger.Message msg)
  {
    pw.println(text);
  }
  
  public void flush()
  {
    pw.flush();
  }
  
  public void close()
  {
    Logger.removeListener(this);
    pw.close();
  }
}
