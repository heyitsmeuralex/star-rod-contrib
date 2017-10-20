package net.miginfocom.layout;

public abstract class LayoutCallback
{
  public LayoutCallback() {}
  
  public UnitValue[] getPosition(ComponentWrapper paramComponentWrapper)
  {
    return null;
  }
  
  public BoundSize[] getSize(ComponentWrapper paramComponentWrapper)
  {
    return null;
  }
  
  public void correctBounds(ComponentWrapper paramComponentWrapper) {}
}
