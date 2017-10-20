package net.miginfocom.layout;

public abstract interface ComponentWrapper
{
  public static final int TYPE_UNSET = -1;
  public static final int TYPE_UNKNOWN = 0;
  public static final int TYPE_CONTAINER = 1;
  public static final int TYPE_LABEL = 2;
  public static final int TYPE_TEXT_FIELD = 3;
  public static final int TYPE_TEXT_AREA = 4;
  public static final int TYPE_BUTTON = 5;
  public static final int TYPE_LIST = 6;
  public static final int TYPE_TABLE = 7;
  public static final int TYPE_SCROLL_PANE = 8;
  public static final int TYPE_IMAGE = 9;
  public static final int TYPE_PANEL = 10;
  public static final int TYPE_COMBO_BOX = 11;
  public static final int TYPE_SLIDER = 12;
  public static final int TYPE_SPINNER = 13;
  public static final int TYPE_PROGRESS_BAR = 14;
  public static final int TYPE_TREE = 15;
  public static final int TYPE_CHECK_BOX = 16;
  public static final int TYPE_SCROLL_BAR = 17;
  public static final int TYPE_SEPARATOR = 18;
  
  public abstract Object getComponent();
  
  public abstract int getX();
  
  public abstract int getY();
  
  public abstract int getWidth();
  
  public abstract int getHeight();
  
  public abstract int getScreenLocationX();
  
  public abstract int getScreenLocationY();
  
  public abstract int getMinimumWidth(int paramInt);
  
  public abstract int getMinimumHeight(int paramInt);
  
  public abstract int getPreferredWidth(int paramInt);
  
  public abstract int getPreferredHeight(int paramInt);
  
  public abstract int getMaximumWidth(int paramInt);
  
  public abstract int getMaximumHeight(int paramInt);
  
  public abstract void setBounds(int paramInt1, int paramInt2, int paramInt3, int paramInt4);
  
  public abstract boolean isVisible();
  
  public abstract int getBaseline(int paramInt1, int paramInt2);
  
  public abstract boolean hasBaseline();
  
  public abstract ContainerWrapper getParent();
  
  public abstract float getPixelUnitFactor(boolean paramBoolean);
  
  public abstract int getHorizontalScreenDPI();
  
  public abstract int getVerticalScreenDPI();
  
  public abstract int getScreenWidth();
  
  public abstract int getScreenHeight();
  
  public abstract String getLinkId();
  
  public abstract int getLayoutHashCode();
  
  public abstract int[] getVisualPadding();
  
  public abstract void paintDebugOutline();
  
  public abstract int getComponetType(boolean paramBoolean);
}
