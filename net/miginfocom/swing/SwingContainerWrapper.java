package net.miginfocom.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Graphics2D;
import net.miginfocom.layout.ComponentWrapper;
import net.miginfocom.layout.ContainerWrapper;

public final class SwingContainerWrapper
  extends SwingComponentWrapper
  implements ContainerWrapper
{
  private static final Color DB_CELL_OUTLINE = new Color(255, 0, 0);
  
  public SwingContainerWrapper(Container paramContainer)
  {
    super(paramContainer);
  }
  
  public ComponentWrapper[] getComponents()
  {
    Container localContainer = (Container)getComponent();
    ComponentWrapper[] arrayOfComponentWrapper = new ComponentWrapper[localContainer.getComponentCount()];
    for (int i = 0; i < arrayOfComponentWrapper.length; i++) {
      arrayOfComponentWrapper[i] = new SwingComponentWrapper(localContainer.getComponent(i));
    }
    return arrayOfComponentWrapper;
  }
  
  public int getComponentCount()
  {
    return ((Container)getComponent()).getComponentCount();
  }
  
  public Object getLayout()
  {
    return ((Container)getComponent()).getLayout();
  }
  
  public final boolean isLeftToRight()
  {
    return ((Container)getComponent()).getComponentOrientation().isLeftToRight();
  }
  
  public final void paintDebugCell(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    Component localComponent = (Component)getComponent();
    if (!localComponent.isShowing()) {
      return;
    }
    Graphics2D localGraphics2D = (Graphics2D)localComponent.getGraphics();
    if (localGraphics2D == null) {
      return;
    }
    localGraphics2D.setStroke(new BasicStroke(1.0F, 2, 0, 10.0F, new float[] { 2.0F, 3.0F }, 0.0F));
    localGraphics2D.setPaint(DB_CELL_OUTLINE);
    localGraphics2D.drawRect(paramInt1, paramInt2, paramInt3 - 1, paramInt4 - 1);
  }
  
  public int getComponetType(boolean paramBoolean)
  {
    return 1;
  }
  
  public int getLayoutHashCode()
  {
    long l = System.nanoTime();
    int i = super.getLayoutHashCode();
    if (isLeftToRight()) {
      i += 416343;
    }
    return 0;
  }
}
