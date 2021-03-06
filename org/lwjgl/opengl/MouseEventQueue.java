package org.lwjgl.opengl;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;














































class MouseEventQueue
  extends EventQueue
  implements MouseListener, MouseMotionListener, MouseWheelListener
{
  private static final int WHEEL_SCALE = 120;
  public static final int NUM_BUTTONS = 3;
  private final Component component;
  private boolean grabbed;
  private int accum_dx;
  private int accum_dy;
  private int accum_dz;
  private int last_x;
  private int last_y;
  private boolean saved_control_state;
  private final ByteBuffer event = ByteBuffer.allocate(22);
  

  private final byte[] buttons = new byte[3];
  
  MouseEventQueue(Component component) {
    super(22);
    this.component = component;
  }
  
  public synchronized void register() {
    resetCursorToCenter();
    if (component != null) {
      component.addMouseListener(this);
      component.addMouseMotionListener(this);
      component.addMouseWheelListener(this);
    }
  }
  
  public synchronized void unregister() {
    if (component != null) {
      component.removeMouseListener(this);
      component.removeMouseMotionListener(this);
      component.removeMouseWheelListener(this);
    }
  }
  
  protected Component getComponent() {
    return component;
  }
  
  public synchronized void setGrabbed(boolean grabbed) {
    this.grabbed = grabbed;
    resetCursorToCenter();
  }
  
  public synchronized boolean isGrabbed() {
    return grabbed;
  }
  
  protected int transformY(int y) {
    if (component != null) {
      return component.getHeight() - 1 - y;
    }
    return y;
  }
  
  protected void resetCursorToCenter() {
    clearEvents();
    accum_dx = (this.accum_dy = 0);
    if (component != null) {
      Point cursor_location = AWTUtil.getCursorPosition(component);
      if (cursor_location != null) {
        last_x = x;
        last_y = y;
      }
    }
  }
  
  private void putMouseEvent(byte button, byte state, int dz, long nanos) {
    if (grabbed) {
      putMouseEventWithCoords(button, state, 0, 0, dz, nanos);
    } else
      putMouseEventWithCoords(button, state, last_x, last_y, dz, nanos);
  }
  
  protected void putMouseEventWithCoords(byte button, byte state, int coord1, int coord2, int dz, long nanos) {
    event.clear();
    event.put(button).put(state).putInt(coord1).putInt(coord2).putInt(dz).putLong(nanos);
    event.flip();
    putEvent(event);
  }
  
  public synchronized void poll(IntBuffer coord_buffer, ByteBuffer buttons_buffer) {
    if (grabbed) {
      coord_buffer.put(0, accum_dx);
      coord_buffer.put(1, accum_dy);
    } else {
      coord_buffer.put(0, last_x);
      coord_buffer.put(1, last_y);
    }
    coord_buffer.put(2, accum_dz);
    accum_dx = (this.accum_dy = this.accum_dz = 0);
    int old_position = buttons_buffer.position();
    buttons_buffer.put(buttons, 0, buttons.length);
    buttons_buffer.position(old_position);
  }
  
  private void setCursorPos(int x, int y, long nanos) {
    y = transformY(y);
    if (grabbed)
      return;
    int dx = x - last_x;
    int dy = y - last_y;
    addDelta(dx, dy);
    last_x = x;
    last_y = y;
    putMouseEventWithCoords((byte)-1, (byte)0, x, y, 0, nanos);
  }
  
  protected void addDelta(int dx, int dy) {
    accum_dx += dx;
    accum_dy += dy;
  }
  

  public void mouseClicked(MouseEvent e) {}
  

  public void mouseEntered(MouseEvent e) {}
  
  public void mouseExited(MouseEvent e) {}
  
  private void handleButton(MouseEvent e)
  {
    byte state;
    switch (e.getID()) {
    case 501: 
      state = 1;
      break;
    case 502: 
      state = 0;
      break;
    default: 
      throw new IllegalArgumentException("Not a valid event ID: " + e.getID());
    }
    byte button;
    switch (e.getButton())
    {
    case 0: 
      return;
    
    case 1: 
      if (state == 1)
        saved_control_state = e.isControlDown();
      byte button; if (saved_control_state) {
        if (buttons[1] == state)
          return;
        button = 1;
      } else {
        button = 0;
      }
      break;
    case 2: 
      button = 2;
      break;
    case 3: 
      if (buttons[1] == state)
        return;
      button = 1;
      break;
    default: 
      throw new IllegalArgumentException("Not a valid button: " + e.getButton());
    }
    setButton(button, state, e.getWhen() * 1000000L);
  }
  
  public synchronized void mousePressed(MouseEvent e) {
    handleButton(e);
  }
  
  private void setButton(byte button, byte state, long nanos) {
    buttons[button] = state;
    putMouseEvent(button, state, 0, nanos);
  }
  
  public synchronized void mouseReleased(MouseEvent e) {
    handleButton(e);
  }
  
  private void handleMotion(MouseEvent e) {
    if (grabbed) {
      updateDeltas(e.getWhen() * 1000000L);
    } else {
      setCursorPos(e.getX(), e.getY(), e.getWhen() * 1000000L);
    }
  }
  
  public synchronized void mouseDragged(MouseEvent e) {
    handleMotion(e);
  }
  
  public synchronized void mouseMoved(MouseEvent e) {
    handleMotion(e);
  }
  
  private void handleWheel(int amount, long nanos) {
    accum_dz += amount;
    putMouseEvent((byte)-1, (byte)0, amount, nanos);
  }
  
  protected void updateDeltas(long nanos) {}
  
  public synchronized void mouseWheelMoved(MouseWheelEvent e)
  {
    int wheel_amount = -e.getWheelRotation() * 120;
    handleWheel(wheel_amount, e.getWhen() * 1000000L);
  }
}
