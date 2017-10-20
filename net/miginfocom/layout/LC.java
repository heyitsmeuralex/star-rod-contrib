package net.miginfocom.layout;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;

public final class LC
  implements Externalizable
{
  private int wrapAfter = 2097051;
  private Boolean leftToRight = null;
  private UnitValue[] insets = null;
  private UnitValue alignX = null;
  private UnitValue alignY = null;
  private BoundSize gridGapX = null;
  private BoundSize gridGapY = null;
  private BoundSize width = BoundSize.NULL_SIZE;
  private BoundSize height = BoundSize.NULL_SIZE;
  private BoundSize packW = BoundSize.NULL_SIZE;
  private BoundSize packH = BoundSize.NULL_SIZE;
  private float pwAlign = 0.5F;
  private float phAlign = 1.0F;
  private int debugMillis = 0;
  private int hideMode = 0;
  private boolean noCache = false;
  private boolean flowX = true;
  private boolean fillX = false;
  private boolean fillY = false;
  private boolean topToBottom = true;
  private boolean noGrid = false;
  private boolean visualPadding = true;
  
  public LC() {}
  
  public boolean isNoCache()
  {
    return noCache;
  }
  
  public void setNoCache(boolean paramBoolean)
  {
    noCache = paramBoolean;
  }
  
  public final UnitValue getAlignX()
  {
    return alignX;
  }
  
  public final void setAlignX(UnitValue paramUnitValue)
  {
    alignX = paramUnitValue;
  }
  
  public final UnitValue getAlignY()
  {
    return alignY;
  }
  
  public final void setAlignY(UnitValue paramUnitValue)
  {
    alignY = paramUnitValue;
  }
  
  public final int getDebugMillis()
  {
    return debugMillis;
  }
  
  public final void setDebugMillis(int paramInt)
  {
    debugMillis = paramInt;
  }
  
  public final boolean isFillX()
  {
    return fillX;
  }
  
  public final void setFillX(boolean paramBoolean)
  {
    fillX = paramBoolean;
  }
  
  public final boolean isFillY()
  {
    return fillY;
  }
  
  public final void setFillY(boolean paramBoolean)
  {
    fillY = paramBoolean;
  }
  
  public final boolean isFlowX()
  {
    return flowX;
  }
  
  public final void setFlowX(boolean paramBoolean)
  {
    flowX = paramBoolean;
  }
  
  public final BoundSize getGridGapX()
  {
    return gridGapX;
  }
  
  public final void setGridGapX(BoundSize paramBoundSize)
  {
    gridGapX = paramBoundSize;
  }
  
  public final BoundSize getGridGapY()
  {
    return gridGapY;
  }
  
  public final void setGridGapY(BoundSize paramBoundSize)
  {
    gridGapY = paramBoundSize;
  }
  
  public final int getHideMode()
  {
    return hideMode;
  }
  
  public final void setHideMode(int paramInt)
  {
    if ((paramInt < 0) || (paramInt > 3)) {
      throw new IllegalArgumentException("Wrong hideMode: " + paramInt);
    }
    hideMode = paramInt;
  }
  
  public final UnitValue[] getInsets()
  {
    return insets != null ? new UnitValue[] { insets[0], insets[1], insets[2], insets[3] } : null;
  }
  
  public final void setInsets(UnitValue[] paramArrayOfUnitValue)
  {
    insets = (paramArrayOfUnitValue != null ? new UnitValue[] { paramArrayOfUnitValue[0], paramArrayOfUnitValue[1], paramArrayOfUnitValue[2], paramArrayOfUnitValue[3] } : null);
  }
  
  public final Boolean getLeftToRight()
  {
    return leftToRight;
  }
  
  public final void setLeftToRight(Boolean paramBoolean)
  {
    leftToRight = paramBoolean;
  }
  
  public final boolean isNoGrid()
  {
    return noGrid;
  }
  
  public final void setNoGrid(boolean paramBoolean)
  {
    noGrid = paramBoolean;
  }
  
  public final boolean isTopToBottom()
  {
    return topToBottom;
  }
  
  public final void setTopToBottom(boolean paramBoolean)
  {
    topToBottom = paramBoolean;
  }
  
  public final boolean isVisualPadding()
  {
    return visualPadding;
  }
  
  public final void setVisualPadding(boolean paramBoolean)
  {
    visualPadding = paramBoolean;
  }
  
  public final int getWrapAfter()
  {
    return wrapAfter;
  }
  
  public final void setWrapAfter(int paramInt)
  {
    wrapAfter = paramInt;
  }
  
  public final BoundSize getPackWidth()
  {
    return packW;
  }
  
  public final void setPackWidth(BoundSize paramBoundSize)
  {
    packW = (paramBoundSize != null ? paramBoundSize : BoundSize.NULL_SIZE);
  }
  
  public final BoundSize getPackHeight()
  {
    return packH;
  }
  
  public final void setPackHeight(BoundSize paramBoundSize)
  {
    packH = (paramBoundSize != null ? paramBoundSize : BoundSize.NULL_SIZE);
  }
  
  public final float getPackHeightAlign()
  {
    return phAlign;
  }
  
  public final void setPackHeightAlign(float paramFloat)
  {
    phAlign = Math.max(0.0F, Math.min(1.0F, paramFloat));
  }
  
  public final float getPackWidthAlign()
  {
    return pwAlign;
  }
  
  public final void setPackWidthAlign(float paramFloat)
  {
    pwAlign = Math.max(0.0F, Math.min(1.0F, paramFloat));
  }
  
  public final BoundSize getWidth()
  {
    return width;
  }
  
  public final void setWidth(BoundSize paramBoundSize)
  {
    width = (paramBoundSize != null ? paramBoundSize : BoundSize.NULL_SIZE);
  }
  
  public final BoundSize getHeight()
  {
    return height;
  }
  
  public final void setHeight(BoundSize paramBoundSize)
  {
    height = (paramBoundSize != null ? paramBoundSize : BoundSize.NULL_SIZE);
  }
  
  public final LC pack()
  {
    return pack("pref", "pref");
  }
  
  public final LC pack(String paramString1, String paramString2)
  {
    setPackWidth(paramString1 != null ? ConstraintParser.parseBoundSize(paramString1, false, false) : BoundSize.NULL_SIZE);
    setPackHeight(paramString2 != null ? ConstraintParser.parseBoundSize(paramString2, false, false) : BoundSize.NULL_SIZE);
    return this;
  }
  
  public final LC packAlign(float paramFloat1, float paramFloat2)
  {
    setPackWidthAlign(paramFloat1);
    setPackHeightAlign(paramFloat2);
    return this;
  }
  
  public final LC wrap()
  {
    setWrapAfter(0);
    return this;
  }
  
  public final LC wrapAfter(int paramInt)
  {
    setWrapAfter(paramInt);
    return this;
  }
  
  public final LC noCache()
  {
    setNoCache(true);
    return this;
  }
  
  public final LC flowY()
  {
    setFlowX(false);
    return this;
  }
  
  public final LC flowX()
  {
    setFlowX(true);
    return this;
  }
  
  public final LC fill()
  {
    setFillX(true);
    setFillY(true);
    return this;
  }
  
  public final LC fillX()
  {
    setFillX(true);
    return this;
  }
  
  public final LC fillY()
  {
    setFillY(true);
    return this;
  }
  
  public final LC leftToRight(boolean paramBoolean)
  {
    setLeftToRight(paramBoolean ? Boolean.TRUE : Boolean.FALSE);
    return this;
  }
  
  public final LC rightToLeft()
  {
    setLeftToRight(Boolean.FALSE);
    return this;
  }
  
  public final LC bottomToTop()
  {
    setTopToBottom(false);
    return this;
  }
  
  public final LC topToBottom()
  {
    setTopToBottom(true);
    return this;
  }
  
  public final LC noGrid()
  {
    setNoGrid(true);
    return this;
  }
  
  public final LC noVisualPadding()
  {
    setVisualPadding(false);
    return this;
  }
  
  public final LC insetsAll(String paramString)
  {
    UnitValue localUnitValue1 = ConstraintParser.parseUnitValue(paramString, true);
    UnitValue localUnitValue2 = ConstraintParser.parseUnitValue(paramString, false);
    insets = new UnitValue[] { localUnitValue2, localUnitValue1, localUnitValue2, localUnitValue1 };
    return this;
  }
  
  public final LC insets(String paramString)
  {
    insets = ConstraintParser.parseInsets(paramString, true);
    return this;
  }
  
  public final LC insets(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    insets = new UnitValue[] { ConstraintParser.parseUnitValue(paramString1, false), ConstraintParser.parseUnitValue(paramString2, true), ConstraintParser.parseUnitValue(paramString3, false), ConstraintParser.parseUnitValue(paramString4, true) };
    return this;
  }
  
  public final LC alignX(String paramString)
  {
    setAlignX(ConstraintParser.parseUnitValueOrAlign(paramString, true, null));
    return this;
  }
  
  public final LC alignY(String paramString)
  {
    setAlignY(ConstraintParser.parseUnitValueOrAlign(paramString, false, null));
    return this;
  }
  
  public final LC align(String paramString1, String paramString2)
  {
    if (paramString1 != null) {
      alignX(paramString1);
    }
    if (paramString2 != null) {
      alignY(paramString2);
    }
    return this;
  }
  
  public final LC gridGapX(String paramString)
  {
    setGridGapX(ConstraintParser.parseBoundSize(paramString, true, true));
    return this;
  }
  
  public final LC gridGapY(String paramString)
  {
    setGridGapY(ConstraintParser.parseBoundSize(paramString, true, false));
    return this;
  }
  
  public final LC gridGap(String paramString1, String paramString2)
  {
    if (paramString1 != null) {
      gridGapX(paramString1);
    }
    if (paramString2 != null) {
      gridGapY(paramString2);
    }
    return this;
  }
  
  public final LC debug(int paramInt)
  {
    setDebugMillis(paramInt);
    return this;
  }
  
  public final LC hideMode(int paramInt)
  {
    setHideMode(paramInt);
    return this;
  }
  
  public final LC minWidth(String paramString)
  {
    setWidth(LayoutUtil.derive(getWidth(), ConstraintParser.parseUnitValue(paramString, true), null, null));
    return this;
  }
  
  public final LC width(String paramString)
  {
    setWidth(ConstraintParser.parseBoundSize(paramString, false, true));
    return this;
  }
  
  public final LC maxWidth(String paramString)
  {
    setWidth(LayoutUtil.derive(getWidth(), null, null, ConstraintParser.parseUnitValue(paramString, true)));
    return this;
  }
  
  public final LC minHeight(String paramString)
  {
    setHeight(LayoutUtil.derive(getHeight(), ConstraintParser.parseUnitValue(paramString, false), null, null));
    return this;
  }
  
  public final LC height(String paramString)
  {
    setHeight(ConstraintParser.parseBoundSize(paramString, false, false));
    return this;
  }
  
  public final LC maxHeight(String paramString)
  {
    setHeight(LayoutUtil.derive(getHeight(), null, null, ConstraintParser.parseUnitValue(paramString, false)));
    return this;
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
    if (getClass() == LC.class) {
      LayoutUtil.writeAsXML(paramObjectOutput, this);
    }
  }
}
