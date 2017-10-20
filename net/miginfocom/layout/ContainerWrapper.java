package net.miginfocom.layout;

public abstract interface ContainerWrapper
  extends ComponentWrapper
{
  public abstract ComponentWrapper[] getComponents();
  
  public abstract int getComponentCount();
  
  public abstract Object getLayout();
  
  public abstract boolean isLeftToRight();
  
  public abstract void paintDebugCell(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
}
