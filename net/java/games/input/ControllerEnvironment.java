package net.java.games.input;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;































































public abstract class ControllerEnvironment
{
  static void logln(String msg)
  {
    log(msg + "\n");
  }
  
  static void log(String msg) {
    System.out.print(msg);
  }
  



  private static ControllerEnvironment defaultEnvironment = new DefaultControllerEnvironment();
  




  protected final ArrayList controllerListeners = new ArrayList();
  




  protected ControllerEnvironment() {}
  



  public abstract Controller[] getControllers();
  



  public void addControllerListener(ControllerListener l)
  {
    assert (l != null);
    controllerListeners.add(l);
  }
  




  public abstract boolean isSupported();
  



  public void removeControllerListener(ControllerListener l)
  {
    assert (l != null);
    controllerListeners.remove(l);
  }
  



  protected void fireControllerAdded(Controller c)
  {
    ControllerEvent ev = new ControllerEvent(c);
    Iterator it = controllerListeners.iterator();
    while (it.hasNext()) {
      ((ControllerListener)it.next()).controllerAdded(ev);
    }
  }
  



  protected void fireControllerRemoved(Controller c)
  {
    ControllerEvent ev = new ControllerEvent(c);
    Iterator it = controllerListeners.iterator();
    while (it.hasNext()) {
      ((ControllerListener)it.next()).controllerRemoved(ev);
    }
  }
  



  public static ControllerEnvironment getDefaultEnvironment()
  {
    return defaultEnvironment;
  }
}
