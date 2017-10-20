package util;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;


public abstract class Logger
{
  private static List<ListenerReference> listeners = new LinkedList();
  private static Priority minListenerPriority = Priority.STANDARD;
  
  private static Listener progressListener;
  
  private static Priority defaultPriority = Priority.STANDARD;
  private static boolean enabled = true;
  public Logger() {}
  
  public static final class Message
  {
    public final String text;
    public final Priority priority;
    
    private Message(String text, Priority importance) {
      this.text = text;
      priority = importance;
    }
  }
  
  public static abstract interface Listener
  {
    public abstract void post(Logger.Message paramMessage);
  }
  
  private static final class ListenerReference
  {
    private final Logger.Listener listener;
    private final Priority priority;
    
    private ListenerReference(Logger.Listener listener, Priority p)
    {
      this.listener = listener;
      priority = p;
    }
  }
  
  public static void addListener(Listener listener)
  {
    listeners.add(new ListenerReference(listener, defaultPriority, null));
    
    if (minListenerPriority.greaterThan(defaultPriority)) {
      minListenerPriority = defaultPriority;
    }
  }
  
  public static void addListener(Listener listener, Priority p) {
    listeners.add(new ListenerReference(listener, p, null));
    
    if (minListenerPriority.greaterThan(p)) {
      minListenerPriority = p;
    }
  }
  
  public static void removeListener(Listener listener) {
    listeners.remove(listener);
    
    minListenerPriority = Priority.MILESTONE;
    for (ListenerReference r : listeners)
    {
      if (minListenerPriority.greaterThan(priority)) {
        minListenerPriority = priority;
      }
    }
  }
  
  public static void setProgressListener(Listener listener) {
    progressListener = listener;
  }
  
  public static void removeProgressListener()
  {
    progressListener = null;
  }
  
  private static void broadcast(String text, Priority p)
  {
    if (!enabled) {
      return;
    }
    Message msg = new Message(text, p, null);
    
    switch (1.$SwitchMap$util$Priority[p.ordinal()])
    {

    case 1: 
      if (progressListener != null)
        progressListener.post(msg);
      return;
    
    case 2: 
      if (progressListener != null)
        progressListener.post(msg);
      break;
    case 3:  text = "WARNING: " + text; break;
    case 4:  text = "ERROR: " + text; break;
    }
    
    
    if (!p.lessThan(defaultPriority))
    {
      if (text.isEmpty()) {
        System.out.println(">");
      } else {
        System.out.println("> " + text);
      }
    }
    if (p.lessThan(minListenerPriority)) {
      return;
    }
    for (ListenerReference ref : listeners)
    {
      if (!p.lessThan(priority)) {
        listener.post(msg);
      }
    }
  }
  
  public static void logWarning(String message) {
    broadcast(message, Priority.WARNING);
  }
  
  public static void logfWarning(String format, Object... args)
  {
    broadcast(String.format(format, args), Priority.WARNING);
  }
  
  public static void log(String message)
  {
    broadcast(message, Priority.STANDARD);
  }
  
  public static void logf(String format, Object... args)
  {
    broadcast(String.format(format, args), Priority.STANDARD);
  }
  
  public static void logDetail(String message)
  {
    broadcast(message, Priority.DETAIL);
  }
  
  public static void logfDetail(String format, Object... args)
  {
    broadcast(String.format(format, args), Priority.DETAIL);
  }
  
  public static void log(String message, Priority p)
  {
    broadcast(message, p);
  }
  
  public static void setDefaultOuputPriority(Priority p)
  {
    defaultPriority = p;
  }
  
  public static void disable()
  {
    enabled = false;
  }
  
  public static void enable()
  {
    enabled = true;
  }
}
