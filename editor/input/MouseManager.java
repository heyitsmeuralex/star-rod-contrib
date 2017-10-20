package editor.input;

import editor.Editor;
import org.lwjgl.input.Mouse;



public abstract class MouseManager
{
  private static final double HOLD_INTERVAL = 100.0D;
  
  public MouseManager() {}
  
  private static double lastLeftClick = Double.NEGATIVE_INFINITY;
  public static boolean holdingLMB = false;
  
  private static double lastRightClick = Double.NEGATIVE_INFINITY;
  public static boolean holdingRMB = false;
  
  private static int frameDX = 0;
  private static int frameDY = 0;
  private static int frameDW = 0;
  
  public static int getFrameDX() { return frameDX; }
  public static int getFrameDY() { return frameDY; }
  public static int getFrameDW() { return frameDW; }
  
  public static void update()
  {
    double currentTime = System.nanoTime() / 1000000.0D;
    
    frameDW = Mouse.getDWheel();
    

    frameDX = Mouse.getDX();
    frameDY = Mouse.getDY();
    
    if ((frameDX != 0) || (frameDY != 0)) {
      Editor.moveMouse(frameDX, frameDY);
    }
    
    while (Mouse.next())
    {
      if (Mouse.getEventButtonState())
      {
        if (Mouse.getEventButton() == 0)
        {
          Editor.clickLMB();
          lastLeftClick = currentTime;
        }
        
        if (Mouse.getEventButton() == 1)
        {
          Editor.clickRMB();
          lastRightClick = currentTime;
        }
      }
      else {
        if (Mouse.getEventButton() == 0)
        {
          if (holdingLMB)
          {
            Editor.stopHoldingLMB();
            holdingLMB = false;
          }
          else {
            Editor.releaseLMB();
          }
        }
        if (Mouse.getEventButton() == 1)
        {
          if (holdingRMB)
          {
            Editor.stopHoldingRMB();
            holdingRMB = false;
          }
          else {
            Editor.releaseRMB();
          }
        }
      }
    }
    
    if ((!holdingLMB) && (Mouse.isButtonDown(0)) && (currentTime - lastLeftClick > 100.0D))
    {
      Editor.startHoldingLMB();
      holdingLMB = true;
    }
    
    if ((!holdingRMB) && (Mouse.isButtonDown(1)) && (currentTime - lastRightClick > 100.0D))
    {
      Editor.startHoldingRMB();
      holdingRMB = true;
    }
  }
}
