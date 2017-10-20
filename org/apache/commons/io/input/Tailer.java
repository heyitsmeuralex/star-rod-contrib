package org.apache.commons.io.input;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;




















































































































public class Tailer
  implements Runnable
{
  private final File file;
  private final long delay;
  private final boolean end;
  private final TailerListener listener;
  private volatile boolean run = true;
  




  public Tailer(File file, TailerListener listener)
  {
    this(file, listener, 1000L);
  }
  





  public Tailer(File file, TailerListener listener, long delay)
  {
    this(file, listener, delay, false);
  }
  







  public Tailer(File file, TailerListener listener, long delay, boolean end)
  {
    this.file = file;
    this.delay = delay;
    this.end = end;
    

    this.listener = listener;
    listener.init(this);
  }
  








  public static Tailer create(File file, TailerListener listener, long delay, boolean end)
  {
    Tailer tailer = new Tailer(file, listener, delay, end);
    Thread thread = new Thread(tailer);
    thread.setDaemon(true);
    thread.start();
    return tailer;
  }
  







  public static Tailer create(File file, TailerListener listener, long delay)
  {
    return create(file, listener, delay, false);
  }
  







  public static Tailer create(File file, TailerListener listener)
  {
    return create(file, listener, 1000L, false);
  }
  




  public File getFile()
  {
    return file;
  }
  




  public long getDelay()
  {
    return delay;
  }
  


  public void run()
  {
    RandomAccessFile reader = null;
    try {
      long last = 0L;
      long position = 0L;
      
      while ((run) && (reader == null)) {
        try {
          reader = new RandomAccessFile(file, "r");
        } catch (FileNotFoundException e) {
          listener.fileNotFound();
        }
        
        if (reader == null) {
          try {
            Thread.sleep(delay);
          }
          catch (InterruptedException e) {}
        }
        else {
          position = end ? file.length() : 0L;
          last = System.currentTimeMillis();
          reader.seek(position);
        }
      }
      

      while (run)
      {

        long length = file.length();
        
        if (length < position)
        {

          listener.fileRotated();
          

          try
          {
            save = reader;
            reader = new RandomAccessFile(file, "r");
            position = 0L;
          }
          catch (FileNotFoundException e)
          {
            RandomAccessFile save;
            listener.fileNotFound();
          }
          

        }
        else
        {

          if (length > position)
          {

            last = System.currentTimeMillis();
            position = readLines(reader);
          }
          else if (FileUtils.isFileNewer(file, last))
          {




            position = 0L;
            reader.seek(position);
            

            last = System.currentTimeMillis();
            position = readLines(reader);
          }
          try
          {
            Thread.sleep(delay);
          }
          catch (InterruptedException e) {}
        }
      }
    }
    catch (Exception e) {
      listener.handle(e);
    }
    finally {
      IOUtils.closeQuietly(reader);
    }
  }
  


  public void stop()
  {
    run = false;
  }
  





  private long readLines(RandomAccessFile reader)
    throws IOException
  {
    long pos = reader.getFilePointer();
    String line = readLine(reader);
    while (line != null) {
      pos = reader.getFilePointer();
      listener.handle(line);
      line = readLine(reader);
    }
    reader.seek(pos);
    return pos;
  }
  




  private String readLine(RandomAccessFile reader)
    throws IOException
  {
    StringBuffer sb = new StringBuffer();
    
    boolean seenCR = false;
    int ch; while ((ch = reader.read()) != -1) {
      switch (ch) {
      case 10: 
        return sb.toString();
      case 13: 
        seenCR = true;
        break;
      default: 
        if (seenCR) {
          sb.append('\r');
          seenCR = false;
        }
        sb.append((char)ch);
      }
    }
    return null;
  }
}
