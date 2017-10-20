package net.miginfocom.layout;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;

public final class DimConstraint
  implements Externalizable
{
  final ResizeConstraint resize = new ResizeConstraint();
  private String sizeGroup = null;
  private BoundSize size = BoundSize.NULL_SIZE;
  private BoundSize gapBefore = null;
  private BoundSize gapAfter = null;
  private UnitValue align = null;
  private String endGroup = null;
  private boolean fill = false;
  private boolean noGrid = false;
  
  public DimConstraint() {}
  
  public int getGrowPriority()
  {
    return resize.growPrio;
  }
  
  public void setGrowPriority(int paramInt)
  {
    resize.growPrio = paramInt;
  }
  
  public Float getGrow()
  {
    return resize.grow;
  }
  
  public void setGrow(Float paramFloat)
  {
    resize.grow = paramFloat;
  }
  
  public int getShrinkPriority()
  {
    return resize.shrinkPrio;
  }
  
  public void setShrinkPriority(int paramInt)
  {
    resize.shrinkPrio = paramInt;
  }
  
  public Float getShrink()
  {
    return resize.shrink;
  }
  
  public void setShrink(Float paramFloat)
  {
    resize.shrink = paramFloat;
  }
  
  public UnitValue getAlignOrDefault(boolean paramBoolean)
  {
    if (align != null) {
      return align;
    }
    if (paramBoolean) {
      return UnitValue.LEADING;
    }
    return (fill) || (!PlatformDefaults.getDefaultRowAlignmentBaseline()) ? UnitValue.CENTER : UnitValue.BASELINE_IDENTITY;
  }
  
  public UnitValue getAlign()
  {
    return align;
  }
  
  public void setAlign(UnitValue paramUnitValue)
  {
    align = paramUnitValue;
  }
  
  public BoundSize getGapAfter()
  {
    return gapAfter;
  }
  
  public void setGapAfter(BoundSize paramBoundSize)
  {
    gapAfter = paramBoundSize;
  }
  
  boolean hasGapAfter()
  {
    return (gapAfter != null) && (!gapAfter.isUnset());
  }
  
  boolean isGapAfterPush()
  {
    return (gapAfter != null) && (gapAfter.getGapPush());
  }
  
  public BoundSize getGapBefore()
  {
    return gapBefore;
  }
  
  public void setGapBefore(BoundSize paramBoundSize)
  {
    gapBefore = paramBoundSize;
  }
  
  boolean hasGapBefore()
  {
    return (gapBefore != null) && (!gapBefore.isUnset());
  }
  
  boolean isGapBeforePush()
  {
    return (gapBefore != null) && (gapBefore.getGapPush());
  }
  
  public BoundSize getSize()
  {
    return size;
  }
  
  public void setSize(BoundSize paramBoundSize)
  {
    if (paramBoundSize != null) {
      paramBoundSize.checkNotLinked();
    }
    size = paramBoundSize;
  }
  
  public String getSizeGroup()
  {
    return sizeGroup;
  }
  
  public void setSizeGroup(String paramString)
  {
    sizeGroup = paramString;
  }
  
  public String getEndGroup()
  {
    return endGroup;
  }
  
  public void setEndGroup(String paramString)
  {
    endGroup = paramString;
  }
  
  public boolean isFill()
  {
    return fill;
  }
  
  public void setFill(boolean paramBoolean)
  {
    fill = paramBoolean;
  }
  
  public boolean isNoGrid()
  {
    return noGrid;
  }
  
  public void setNoGrid(boolean paramBoolean)
  {
    noGrid = paramBoolean;
  }
  
  int[] getRowGaps(ContainerWrapper paramContainerWrapper, BoundSize paramBoundSize, int paramInt, boolean paramBoolean)
  {
    BoundSize localBoundSize = paramBoolean ? gapBefore : gapAfter;
    if ((localBoundSize == null) || (localBoundSize.isUnset())) {
      localBoundSize = paramBoundSize;
    }
    if ((localBoundSize == null) || (localBoundSize.isUnset())) {
      return null;
    }
    int[] arrayOfInt = new int[3];
    for (int i = 0; i <= 2; i++)
    {
      UnitValue localUnitValue = localBoundSize.getSize(i);
      arrayOfInt[i] = (localUnitValue != null ? localUnitValue.getPixels(paramInt, paramContainerWrapper, null) : -2147471302);
    }
    return arrayOfInt;
  }
  
  int[] getComponentGaps(ContainerWrapper paramContainerWrapper, ComponentWrapper paramComponentWrapper1, BoundSize paramBoundSize, ComponentWrapper paramComponentWrapper2, String paramString, int paramInt1, int paramInt2, boolean paramBoolean)
  {
    BoundSize localBoundSize = paramInt2 < 2 ? gapBefore : gapAfter;
    int i = (localBoundSize != null) && (localBoundSize.getGapPush()) ? 1 : 0;
    if (((localBoundSize == null) || (localBoundSize.isUnset())) && ((paramBoundSize == null) || (paramBoundSize.isUnset())) && (paramComponentWrapper1 != null)) {
      localBoundSize = PlatformDefaults.getDefaultComponentGap(paramComponentWrapper1, paramComponentWrapper2, paramInt2 + 1, paramString, paramBoolean);
    }
    if (localBoundSize == null) {
      return i != 0 ? new int[] { 0, 0, -2147471302 } : null;
    }
    int[] arrayOfInt = new int[3];
    for (int j = 0; j <= 2; j++)
    {
      UnitValue localUnitValue = localBoundSize.getSize(j);
      arrayOfInt[j] = (localUnitValue != null ? localUnitValue.getPixels(paramInt1, paramContainerWrapper, null) : -2147471302);
    }
    return arrayOfInt;
  }
  
  private Object readResolve()
    throws ObjectStreamException
  {
    return LayoutUtil.getSerializedObject(this);
  }
  
  public void readExternal(ObjectInput paramObjectInput)
    throws IOException, ClassNotFoundException
  {
    LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(paramObjectInput));
  }
  
  public void writeExternal(ObjectOutput paramObjectOutput)
    throws IOException
  {
    if (getClass() == DimConstraint.class) {
      LayoutUtil.writeAsXML(paramObjectOutput, this);
    }
  }
}
