package org.apache.commons.io.monitor;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;
























public final class FileAlterationMonitor
  implements Runnable
{
  private final long interval;
  private final List<FileAlterationObserver> observers = new CopyOnWriteArrayList();
  private Thread thread = null;
  private ThreadFactory threadFactory;
  private volatile boolean running = false;
  


  public FileAlterationMonitor()
  {
    this(10000L);
  }
  





  public FileAlterationMonitor(long interval)
  {
    this.interval = interval;
  }
  






  public FileAlterationMonitor(long interval, FileAlterationObserver... observers)
  {
    this(interval);
    if (observers != null) {
      for (FileAlterationObserver observer : observers) {
        addObserver(observer);
      }
    }
  }
  




  public long getInterval()
  {
    return interval;
  }
  




  public synchronized void setThreadFactory(ThreadFactory threadFactory)
  {
    this.threadFactory = threadFactory;
  }
  




  public void addObserver(FileAlterationObserver observer)
  {
    if (observer != null) {
      observers.add(observer);
    }
  }
  




  public void removeObserver(FileAlterationObserver observer)
  {
    while ((observer != null) && 
      (observers.remove(observer))) {}
  }
  







  public Iterable<FileAlterationObserver> getObservers()
  {
    return observers;
  }
  



  public synchronized void start()
    throws Exception
  {
    if (running) {
      throw new IllegalStateException("Monitor is already running");
    }
    for (FileAlterationObserver observer : observers) {
      observer.initialize();
    }
    running = true;
    if (threadFactory != null) {
      thread = threadFactory.newThread(this);
    } else {
      thread = new Thread(this);
    }
    thread.start();
  }
  



  public synchronized void stop()
    throws Exception
  {
    stop(interval);
  }
  






  public synchronized void stop(long stopInterval)
    throws Exception
  {
    if (!running) {
      throw new IllegalStateException("Monitor is not running");
    }
    running = false;
    try {
      thread.join(stopInterval);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    for (FileAlterationObserver observer : observers) {
      observer.destroy();
    }
  }
  


  public void run()
  {
    while (running) {
      for (FileAlterationObserver observer : observers) {
        observer.checkAndNotify();
      }
      if (!running) {
        break;
      }
      try {
        Thread.sleep(interval);
      }
      catch (InterruptedException ignored) {}
    }
  }
}
