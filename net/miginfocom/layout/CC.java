package net.miginfocom.layout;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.ArrayList;

public final class CC
  implements Externalizable
{
  private static final BoundSize DEF_GAP = BoundSize.NULL_SIZE;
  static final String[] DOCK_SIDES = { "north", "west", "south", "east" };
  private int dock = -1;
  private UnitValue[] pos = null;
  private UnitValue[] padding = null;
  private Boolean flowX = null;
  private int skip = 0;
  private int split = 1;
  private int spanX = 1;
  private int spanY = 1;
  private int cellX = -1;
  private int cellY = 0;
  private String tag = null;
  private String id = null;
  private int hideMode = -1;
  private DimConstraint hor = new DimConstraint();
  private DimConstraint ver = new DimConstraint();
  private BoundSize newline = null;
  private BoundSize wrap = null;
  private boolean boundsInGrid = true;
  private boolean external = false;
  private Float pushX = null;
  private Float pushY = null;
  private static final String[] EMPTY_ARR = new String[0];
  private transient String[] linkTargets = null;
  
  public CC() {}
  
  String[] getLinkTargets()
  {
    if (linkTargets == null)
    {
      ArrayList localArrayList = new ArrayList(2);
      if (pos != null) {
        for (int i = 0; i < pos.length; i++) {
          addLinkTargetIDs(localArrayList, pos[i]);
        }
      }
      linkTargets = (localArrayList.size() == 0 ? EMPTY_ARR : (String[])localArrayList.toArray(new String[localArrayList.size()]));
    }
    return linkTargets;
  }
  
  private void addLinkTargetIDs(ArrayList<String> paramArrayList, UnitValue paramUnitValue)
  {
    if (paramUnitValue != null)
    {
      String str = paramUnitValue.getLinkTargetId();
      if (str != null) {
        paramArrayList.add(str);
      } else {
        for (int i = paramUnitValue.getSubUnitCount() - 1; i >= 0; i--)
        {
          UnitValue localUnitValue = paramUnitValue.getSubUnitValue(i);
          if (localUnitValue.isLinkedDeep()) {
            addLinkTargetIDs(paramArrayList, localUnitValue);
          }
        }
      }
    }
  }
  
  public final CC endGroupX(String paramString)
  {
    hor.setEndGroup(paramString);
    return this;
  }
  
  public final CC sizeGroupX(String paramString)
  {
    hor.setSizeGroup(paramString);
    return this;
  }
  
  public final CC minWidth(String paramString)
  {
    hor.setSize(LayoutUtil.derive(hor.getSize(), ConstraintParser.parseUnitValue(paramString, true), null, null));
    return this;
  }
  
  public final CC width(String paramString)
  {
    hor.setSize(ConstraintParser.parseBoundSize(paramString, false, true));
    return this;
  }
  
  public final CC maxWidth(String paramString)
  {
    hor.setSize(LayoutUtil.derive(hor.getSize(), null, null, ConstraintParser.parseUnitValue(paramString, true)));
    return this;
  }
  
  public final CC gapX(String paramString1, String paramString2)
  {
    if (paramString1 != null) {
      hor.setGapBefore(ConstraintParser.parseBoundSize(paramString1, true, true));
    }
    if (paramString2 != null) {
      hor.setGapAfter(ConstraintParser.parseBoundSize(paramString2, true, true));
    }
    return this;
  }
  
  public final CC alignX(String paramString)
  {
    hor.setAlign(ConstraintParser.parseUnitValueOrAlign(paramString, true, null));
    return this;
  }
  
  public final CC growPrioX(int paramInt)
  {
    hor.setGrowPriority(paramInt);
    return this;
  }
  
  public final CC growPrio(int... paramVarArgs)
  {
    switch (paramVarArgs.length)
    {
    default: 
      throw new IllegalArgumentException("Illegal argument count: " + paramVarArgs.length);
    case 2: 
      growPrioY(paramVarArgs[1]);
    }
    growPrioX(paramVarArgs[0]);
    return this;
  }
  
  public final CC growX()
  {
    hor.setGrow(ResizeConstraint.WEIGHT_100);
    return this;
  }
  
  public final CC growX(float paramFloat)
  {
    hor.setGrow(new Float(paramFloat));
    return this;
  }
  
  public final CC grow(float... paramVarArgs)
  {
    switch (paramVarArgs.length)
    {
    default: 
      throw new IllegalArgumentException("Illegal argument count: " + paramVarArgs.length);
    case 2: 
      growY(Float.valueOf(paramVarArgs[1]));
    }
    growX(paramVarArgs[0]);
    return this;
  }
  
  public final CC shrinkPrioX(int paramInt)
  {
    hor.setShrinkPriority(paramInt);
    return this;
  }
  
  public final CC shrinkPrio(int... paramVarArgs)
  {
    switch (paramVarArgs.length)
    {
    default: 
      throw new IllegalArgumentException("Illegal argument count: " + paramVarArgs.length);
    case 2: 
      shrinkPrioY(paramVarArgs[1]);
    }
    shrinkPrioX(paramVarArgs[0]);
    return this;
  }
  
  public final CC shrinkX(float paramFloat)
  {
    hor.setShrink(new Float(paramFloat));
    return this;
  }
  
  public final CC shrink(float... paramVarArgs)
  {
    switch (paramVarArgs.length)
    {
    default: 
      throw new IllegalArgumentException("Illegal argument count: " + paramVarArgs.length);
    case 2: 
      shrinkY(paramVarArgs[1]);
    }
    shrinkX(paramVarArgs[0]);
    return this;
  }
  
  public final CC endGroupY(String paramString)
  {
    ver.setEndGroup(paramString);
    return this;
  }
  
  public final CC endGroup(String... paramVarArgs)
  {
    switch (paramVarArgs.length)
    {
    default: 
      throw new IllegalArgumentException("Illegal argument count: " + paramVarArgs.length);
    case 2: 
      endGroupY(paramVarArgs[1]);
    }
    endGroupX(paramVarArgs[0]);
    return this;
  }
  
  public final CC sizeGroupY(String paramString)
  {
    ver.setSizeGroup(paramString);
    return this;
  }
  
  public final CC sizeGroup(String... paramVarArgs)
  {
    switch (paramVarArgs.length)
    {
    default: 
      throw new IllegalArgumentException("Illegal argument count: " + paramVarArgs.length);
    case 2: 
      sizeGroupY(paramVarArgs[1]);
    }
    sizeGroupX(paramVarArgs[0]);
    return this;
  }
  
  public final CC minHeight(String paramString)
  {
    ver.setSize(LayoutUtil.derive(ver.getSize(), ConstraintParser.parseUnitValue(paramString, false), null, null));
    return this;
  }
  
  public final CC height(String paramString)
  {
    ver.setSize(ConstraintParser.parseBoundSize(paramString, false, false));
    return this;
  }
  
  public final CC maxHeight(String paramString)
  {
    ver.setSize(LayoutUtil.derive(ver.getSize(), null, null, ConstraintParser.parseUnitValue(paramString, false)));
    return this;
  }
  
  public final CC gapY(String paramString1, String paramString2)
  {
    if (paramString1 != null) {
      ver.setGapBefore(ConstraintParser.parseBoundSize(paramString1, true, false));
    }
    if (paramString2 != null) {
      ver.setGapAfter(ConstraintParser.parseBoundSize(paramString2, true, false));
    }
    return this;
  }
  
  public final CC alignY(String paramString)
  {
    ver.setAlign(ConstraintParser.parseUnitValueOrAlign(paramString, false, null));
    return this;
  }
  
  public final CC growPrioY(int paramInt)
  {
    ver.setGrowPriority(paramInt);
    return this;
  }
  
  public final CC growY()
  {
    ver.setGrow(ResizeConstraint.WEIGHT_100);
    return this;
  }
  
  public final CC growY(Float paramFloat)
  {
    ver.setGrow(paramFloat);
    return this;
  }
  
  public final CC shrinkPrioY(int paramInt)
  {
    ver.setShrinkPriority(paramInt);
    return this;
  }
  
  public final CC shrinkY(float paramFloat)
  {
    ver.setShrink(new Float(paramFloat));
    return this;
  }
  
  public final CC hideMode(int paramInt)
  {
    setHideMode(paramInt);
    return this;
  }
  
  public final CC id(String paramString)
  {
    setId(paramString);
    return this;
  }
  
  public final CC tag(String paramString)
  {
    setTag(paramString);
    return this;
  }
  
  public final CC cell(int... paramVarArgs)
  {
    switch (paramVarArgs.length)
    {
    default: 
      throw new IllegalArgumentException("Illegal argument count: " + paramVarArgs.length);
    case 4: 
      setSpanY(paramVarArgs[3]);
    case 3: 
      setSpanX(paramVarArgs[2]);
    case 2: 
      setCellY(paramVarArgs[1]);
    }
    setCellX(paramVarArgs[0]);
    return this;
  }
  
  public final CC span(int... paramVarArgs)
  {
    if ((paramVarArgs == null) || (paramVarArgs.length == 0))
    {
      setSpanX(2097051);
      setSpanY(1);
    }
    else if (paramVarArgs.length == 1)
    {
      setSpanX(paramVarArgs[0]);
      setSpanY(1);
    }
    else
    {
      setSpanX(paramVarArgs[0]);
      setSpanY(paramVarArgs[1]);
    }
    return this;
  }
  
  public final CC gap(String... paramVarArgs)
  {
    switch (paramVarArgs.length)
    {
    default: 
      throw new IllegalArgumentException("Illegal argument count: " + paramVarArgs.length);
    case 4: 
      gapBottom(paramVarArgs[3]);
    case 3: 
      gapTop(paramVarArgs[2]);
    case 2: 
      gapRight(paramVarArgs[1]);
    }
    gapLeft(paramVarArgs[0]);
    return this;
  }
  
  public final CC gapBefore(String paramString)
  {
    hor.setGapBefore(ConstraintParser.parseBoundSize(paramString, true, true));
    return this;
  }
  
  public final CC gapAfter(String paramString)
  {
    hor.setGapAfter(ConstraintParser.parseBoundSize(paramString, true, true));
    return this;
  }
  
  public final CC gapTop(String paramString)
  {
    ver.setGapBefore(ConstraintParser.parseBoundSize(paramString, true, false));
    return this;
  }
  
  public final CC gapLeft(String paramString)
  {
    hor.setGapBefore(ConstraintParser.parseBoundSize(paramString, true, true));
    return this;
  }
  
  public final CC gapBottom(String paramString)
  {
    ver.setGapAfter(ConstraintParser.parseBoundSize(paramString, true, false));
    return this;
  }
  
  public final CC gapRight(String paramString)
  {
    hor.setGapAfter(ConstraintParser.parseBoundSize(paramString, true, true));
    return this;
  }
  
  public final CC spanY()
  {
    return spanY(2097051);
  }
  
  public final CC spanY(int paramInt)
  {
    setSpanY(paramInt);
    return this;
  }
  
  public final CC spanX()
  {
    return spanX(2097051);
  }
  
  public final CC spanX(int paramInt)
  {
    setSpanX(paramInt);
    return this;
  }
  
  public final CC push()
  {
    return pushX().pushY();
  }
  
  public final CC push(Float paramFloat1, Float paramFloat2)
  {
    return pushX(paramFloat1).pushY(paramFloat2);
  }
  
  public final CC pushY()
  {
    return pushY(ResizeConstraint.WEIGHT_100);
  }
  
  public final CC pushY(Float paramFloat)
  {
    setPushY(paramFloat);
    return this;
  }
  
  public final CC pushX()
  {
    return pushX(ResizeConstraint.WEIGHT_100);
  }
  
  public final CC pushX(Float paramFloat)
  {
    setPushX(paramFloat);
    return this;
  }
  
  public final CC split(int paramInt)
  {
    setSplit(paramInt);
    return this;
  }
  
  public final CC split()
  {
    setSplit(2097051);
    return this;
  }
  
  public final CC skip(int paramInt)
  {
    setSkip(paramInt);
    return this;
  }
  
  public final CC skip()
  {
    setSkip(1);
    return this;
  }
  
  public final CC external()
  {
    setExternal(true);
    return this;
  }
  
  public final CC flowX()
  {
    setFlowX(Boolean.TRUE);
    return this;
  }
  
  public final CC flowY()
  {
    setFlowX(Boolean.FALSE);
    return this;
  }
  
  public final CC grow()
  {
    growX();
    growY();
    return this;
  }
  
  public final CC newline()
  {
    setNewline(true);
    return this;
  }
  
  public final CC newline(String paramString)
  {
    BoundSize localBoundSize = ConstraintParser.parseBoundSize(paramString, true, (flowX != null) && (!flowX.booleanValue()));
    if (localBoundSize != null) {
      setNewlineGapSize(localBoundSize);
    } else {
      setNewline(true);
    }
    return this;
  }
  
  public final CC wrap()
  {
    setWrap(true);
    return this;
  }
  
  public final CC wrap(String paramString)
  {
    BoundSize localBoundSize = ConstraintParser.parseBoundSize(paramString, true, (flowX != null) && (!flowX.booleanValue()));
    if (localBoundSize != null) {
      setWrapGapSize(localBoundSize);
    } else {
      setWrap(true);
    }
    return this;
  }
  
  public final CC dockNorth()
  {
    setDockSide(0);
    return this;
  }
  
  public final CC dockWest()
  {
    setDockSide(1);
    return this;
  }
  
  public final CC dockSouth()
  {
    setDockSide(2);
    return this;
  }
  
  public final CC dockEast()
  {
    setDockSide(3);
    return this;
  }
  
  public final CC x(String paramString)
  {
    return corrPos(paramString, 0);
  }
  
  public final CC y(String paramString)
  {
    return corrPos(paramString, 1);
  }
  
  public final CC x2(String paramString)
  {
    return corrPos(paramString, 2);
  }
  
  public final CC y2(String paramString)
  {
    return corrPos(paramString, 3);
  }
  
  private final CC corrPos(String paramString, int paramInt)
  {
    UnitValue[] arrayOfUnitValue = getPos();
    if (arrayOfUnitValue == null) {
      arrayOfUnitValue = new UnitValue[4];
    }
    arrayOfUnitValue[paramInt] = ConstraintParser.parseUnitValue(paramString, paramInt % 2 == 0 ? 1 : false);
    setPos(arrayOfUnitValue);
    setBoundsInGrid(true);
    return this;
  }
  
  public final CC pos(String paramString1, String paramString2)
  {
    UnitValue[] arrayOfUnitValue = getPos();
    if (arrayOfUnitValue == null) {
      arrayOfUnitValue = new UnitValue[4];
    }
    arrayOfUnitValue[0] = ConstraintParser.parseUnitValue(paramString1, true);
    arrayOfUnitValue[1] = ConstraintParser.parseUnitValue(paramString2, false);
    setPos(arrayOfUnitValue);
    setBoundsInGrid(false);
    return this;
  }
  
  public final CC pos(String paramString1, String paramString2, String paramString3, String paramString4)
  {
    setPos(new UnitValue[] { ConstraintParser.parseUnitValue(paramString1, true), ConstraintParser.parseUnitValue(paramString2, false), ConstraintParser.parseUnitValue(paramString3, true), ConstraintParser.parseUnitValue(paramString4, false) });
    setBoundsInGrid(false);
    return this;
  }
  
  public final CC pad(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
  {
    setPadding(new UnitValue[] { new UnitValue(paramInt1), new UnitValue(paramInt2), new UnitValue(paramInt3), new UnitValue(paramInt4) });
    return this;
  }
  
  public final CC pad(String paramString)
  {
    setPadding(paramString != null ? ConstraintParser.parseInsets(paramString, false) : null);
    return this;
  }
  
  public DimConstraint getHorizontal()
  {
    return hor;
  }
  
  public void setHorizontal(DimConstraint paramDimConstraint)
  {
    hor = (paramDimConstraint != null ? paramDimConstraint : new DimConstraint());
  }
  
  public DimConstraint getVertical()
  {
    return ver;
  }
  
  public void setVertical(DimConstraint paramDimConstraint)
  {
    ver = (paramDimConstraint != null ? paramDimConstraint : new DimConstraint());
  }
  
  public DimConstraint getDimConstraint(boolean paramBoolean)
  {
    return paramBoolean ? hor : ver;
  }
  
  public UnitValue[] getPos()
  {
    return pos != null ? new UnitValue[] { pos[0], pos[1], pos[2], pos[3] } : null;
  }
  
  public void setPos(UnitValue[] paramArrayOfUnitValue)
  {
    pos = (paramArrayOfUnitValue != null ? new UnitValue[] { paramArrayOfUnitValue[0], paramArrayOfUnitValue[1], paramArrayOfUnitValue[2], paramArrayOfUnitValue[3] } : null);
    linkTargets = null;
  }
  
  public boolean isBoundsInGrid()
  {
    return boundsInGrid;
  }
  
  void setBoundsInGrid(boolean paramBoolean)
  {
    boundsInGrid = paramBoolean;
  }
  
  public int getCellX()
  {
    return cellX;
  }
  
  public void setCellX(int paramInt)
  {
    cellX = paramInt;
  }
  
  public int getCellY()
  {
    return cellX < 0 ? -1 : cellY;
  }
  
  public void setCellY(int paramInt)
  {
    if (paramInt < 0) {
      cellX = -1;
    }
    cellY = (paramInt < 0 ? 0 : paramInt);
  }
  
  public int getDockSide()
  {
    return dock;
  }
  
  public void setDockSide(int paramInt)
  {
    if ((paramInt < -1) || (paramInt > 3)) {
      throw new IllegalArgumentException("Illegal dock side: " + paramInt);
    }
    dock = paramInt;
  }
  
  public boolean isExternal()
  {
    return external;
  }
  
  public void setExternal(boolean paramBoolean)
  {
    external = paramBoolean;
  }
  
  public Boolean getFlowX()
  {
    return flowX;
  }
  
  public void setFlowX(Boolean paramBoolean)
  {
    flowX = paramBoolean;
  }
  
  public int getHideMode()
  {
    return hideMode;
  }
  
  public void setHideMode(int paramInt)
  {
    if ((paramInt < -1) || (paramInt > 3)) {
      throw new IllegalArgumentException("Wrong hideMode: " + paramInt);
    }
    hideMode = paramInt;
  }
  
  public String getId()
  {
    return id;
  }
  
  public void setId(String paramString)
  {
    id = paramString;
  }
  
  public UnitValue[] getPadding()
  {
    return padding != null ? new UnitValue[] { padding[0], padding[1], padding[2], padding[3] } : null;
  }
  
  public void setPadding(UnitValue[] paramArrayOfUnitValue)
  {
    padding = (paramArrayOfUnitValue != null ? new UnitValue[] { paramArrayOfUnitValue[0], paramArrayOfUnitValue[1], paramArrayOfUnitValue[2], paramArrayOfUnitValue[3] } : null);
  }
  
  public int getSkip()
  {
    return skip;
  }
  
  public void setSkip(int paramInt)
  {
    skip = paramInt;
  }
  
  public int getSpanX()
  {
    return spanX;
  }
  
  public void setSpanX(int paramInt)
  {
    spanX = paramInt;
  }
  
  public int getSpanY()
  {
    return spanY;
  }
  
  public void setSpanY(int paramInt)
  {
    spanY = paramInt;
  }
  
  public Float getPushX()
  {
    return pushX;
  }
  
  public void setPushX(Float paramFloat)
  {
    pushX = paramFloat;
  }
  
  public Float getPushY()
  {
    return pushY;
  }
  
  public void setPushY(Float paramFloat)
  {
    pushY = paramFloat;
  }
  
  public int getSplit()
  {
    return split;
  }
  
  public void setSplit(int paramInt)
  {
    split = paramInt;
  }
  
  public String getTag()
  {
    return tag;
  }
  
  public void setTag(String paramString)
  {
    tag = paramString;
  }
  
  public boolean isWrap()
  {
    return wrap != null;
  }
  
  public void setWrap(boolean paramBoolean)
  {
    wrap = (paramBoolean ? wrap : wrap == null ? DEF_GAP : null);
  }
  
  public BoundSize getWrapGapSize()
  {
    return wrap == DEF_GAP ? null : wrap;
  }
  
  public void setWrapGapSize(BoundSize paramBoundSize)
  {
    wrap = (paramBoundSize == null ? null : wrap != null ? DEF_GAP : paramBoundSize);
  }
  
  public boolean isNewline()
  {
    return newline != null;
  }
  
  public void setNewline(boolean paramBoolean)
  {
    newline = (paramBoolean ? newline : newline == null ? DEF_GAP : null);
  }
  
  public BoundSize getNewlineGapSize()
  {
    return newline == DEF_GAP ? null : newline;
  }
  
  public void setNewlineGapSize(BoundSize paramBoundSize)
  {
    newline = (paramBoundSize == null ? null : newline != null ? DEF_GAP : paramBoundSize);
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
    if (getClass() == CC.class) {
      LayoutUtil.writeAsXML(paramObjectOutput, this);
    }
  }
}
