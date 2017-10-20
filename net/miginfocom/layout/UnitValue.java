package net.miginfocom.layout;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public final class UnitValue
  implements Serializable
{
  private static final HashMap<String, Integer> UNIT_MAP = new HashMap(32);
  private static final ArrayList<UnitConverter> CONVERTERS = new ArrayList();
  public static final int STATIC = 100;
  public static final int ADD = 101;
  public static final int SUB = 102;
  public static final int MUL = 103;
  public static final int DIV = 104;
  public static final int MIN = 105;
  public static final int MAX = 106;
  public static final int MID = 107;
  public static final int PIXEL = 0;
  public static final int LPX = 1;
  public static final int LPY = 2;
  public static final int MM = 3;
  public static final int CM = 4;
  public static final int INCH = 5;
  public static final int PERCENT = 6;
  public static final int PT = 7;
  public static final int SPX = 8;
  public static final int SPY = 9;
  public static final int ALIGN = 12;
  public static final int MIN_SIZE = 13;
  public static final int PREF_SIZE = 14;
  public static final int MAX_SIZE = 15;
  public static final int BUTTON = 16;
  public static final int LINK_X = 18;
  public static final int LINK_Y = 19;
  public static final int LINK_W = 20;
  public static final int LINK_H = 21;
  public static final int LINK_X2 = 22;
  public static final int LINK_Y2 = 23;
  public static final int LINK_XPOS = 24;
  public static final int LINK_YPOS = 25;
  public static final int LOOKUP = 26;
  public static final int LABEL_ALIGN = 27;
  private static final int IDENTITY = -1;
  static final UnitValue ZERO;
  static final UnitValue TOP;
  static final UnitValue LEADING;
  static final UnitValue LEFT;
  static final UnitValue CENTER;
  static final UnitValue TRAILING;
  static final UnitValue RIGHT;
  static final UnitValue BOTTOM;
  static final UnitValue LABEL;
  static final UnitValue INF;
  static final UnitValue BASELINE_IDENTITY;
  private final transient float value;
  private final transient int unit;
  private final transient int oper;
  private final transient String unitStr;
  private transient String linkId = null;
  private final transient boolean isHor;
  private final transient UnitValue[] subUnits;
  private static final float[] SCALE;
  private static final long serialVersionUID = 1L;
  
  public UnitValue(float paramFloat)
  {
    this(paramFloat, null, 0, true, 100, null, null, paramFloat + "px");
  }
  
  public UnitValue(float paramFloat, int paramInt, String paramString)
  {
    this(paramFloat, null, paramInt, true, 100, null, null, paramString);
  }
  
  UnitValue(float paramFloat, String paramString1, boolean paramBoolean, int paramInt, String paramString2)
  {
    this(paramFloat, paramString1, -1, paramBoolean, paramInt, null, null, paramString2);
  }
  
  UnitValue(boolean paramBoolean, int paramInt, UnitValue paramUnitValue1, UnitValue paramUnitValue2, String paramString)
  {
    this(0.0F, "", -1, paramBoolean, paramInt, paramUnitValue1, paramUnitValue2, paramString);
    if ((paramUnitValue1 == null) || (paramUnitValue2 == null)) {
      throw new IllegalArgumentException("Sub units is null!");
    }
  }
  
  private UnitValue(float paramFloat, String paramString1, int paramInt1, boolean paramBoolean, int paramInt2, UnitValue paramUnitValue1, UnitValue paramUnitValue2, String paramString2)
  {
    if ((paramInt2 < 100) || (paramInt2 > 107)) {
      throw new IllegalArgumentException("Unknown Operation: " + paramInt2);
    }
    if ((paramInt2 >= 101) && (paramInt2 <= 107) && ((paramUnitValue1 == null) || (paramUnitValue2 == null))) {
      throw new IllegalArgumentException(paramInt2 + " Operation may not have null sub-UnitValues.");
    }
    value = paramFloat;
    oper = paramInt2;
    isHor = paramBoolean;
    unitStr = paramString1;
    unit = (paramString1 != null ? parseUnitString() : paramInt1);
    subUnits = ((paramUnitValue1 != null) && (paramUnitValue2 != null) ? new UnitValue[] { paramUnitValue1, paramUnitValue2 } : null);
    LayoutUtil.putCCString(this, paramString2);
  }
  
  public final int getPixels(float paramFloat, ContainerWrapper paramContainerWrapper, ComponentWrapper paramComponentWrapper)
  {
    return Math.round(getPixelsExact(paramFloat, paramContainerWrapper, paramComponentWrapper));
  }
  
  public final float getPixelsExact(float paramFloat, ContainerWrapper paramContainerWrapper, ComponentWrapper paramComponentWrapper)
  {
    if (paramContainerWrapper == null) {
      return 1.0F;
    }
    float f1;
    if (oper == 100)
    {
      switch (unit)
      {
      case 0: 
        return value;
      case 1: 
      case 2: 
        return paramContainerWrapper.getPixelUnitFactor(unit == 1) * value;
      case 3: 
      case 4: 
      case 5: 
      case 7: 
        f1 = SCALE[(unit - 3)];
        Float localFloat = isHor ? PlatformDefaults.getHorizontalScaleFactor() : PlatformDefaults.getVerticalScaleFactor();
        if (localFloat != null) {
          f1 *= localFloat.floatValue();
        }
        return (isHor ? paramContainerWrapper.getHorizontalScreenDPI() : paramContainerWrapper.getVerticalScreenDPI()) * value / f1;
      case 6: 
        return value * paramFloat * 0.01F;
      case 8: 
      case 9: 
        return (unit == 8 ? paramContainerWrapper.getScreenWidth() : paramContainerWrapper.getScreenHeight()) * value * 0.01F;
      case 12: 
        Integer localInteger1 = LinkHandler.getValue(paramContainerWrapper.getLayout(), "visual", isHor ? 0 : 1);
        Integer localInteger2 = LinkHandler.getValue(paramContainerWrapper.getLayout(), "visual", isHor ? 2 : 3);
        if ((localInteger1 == null) || (localInteger2 == null)) {
          return 0.0F;
        }
        return value * (Math.max(0, localInteger2.intValue()) - paramFloat) + localInteger1.intValue();
      case 13: 
        if (paramComponentWrapper == null) {
          return 0.0F;
        }
        return isHor ? paramComponentWrapper.getMinimumWidth(paramComponentWrapper.getHeight()) : paramComponentWrapper.getMinimumHeight(paramComponentWrapper.getWidth());
      case 14: 
        if (paramComponentWrapper == null) {
          return 0.0F;
        }
        return isHor ? paramComponentWrapper.getPreferredWidth(paramComponentWrapper.getHeight()) : paramComponentWrapper.getPreferredHeight(paramComponentWrapper.getWidth());
      case 15: 
        if (paramComponentWrapper == null) {
          return 0.0F;
        }
        return isHor ? paramComponentWrapper.getMaximumWidth(paramComponentWrapper.getHeight()) : paramComponentWrapper.getMaximumHeight(paramComponentWrapper.getWidth());
      case 16: 
        return PlatformDefaults.getMinimumButtonWidth().getPixels(paramFloat, paramContainerWrapper, paramComponentWrapper);
      case 18: 
      case 19: 
      case 20: 
      case 21: 
      case 22: 
      case 23: 
      case 24: 
      case 25: 
        Integer localInteger3 = LinkHandler.getValue(paramContainerWrapper.getLayout(), getLinkTargetId(), unit - (unit >= 24 ? 24 : 18));
        if (localInteger3 == null) {
          return 0.0F;
        }
        if (unit == 24) {
          return paramContainerWrapper.getScreenLocationX() + localInteger3.intValue();
        }
        if (unit == 25) {
          return paramContainerWrapper.getScreenLocationY() + localInteger3.intValue();
        }
        return localInteger3.intValue();
      case 26: 
        float f3 = lookup(paramFloat, paramContainerWrapper, paramComponentWrapper);
        if (f3 != -8.7654312E7F) {
          return f3;
        }
      case 27: 
        return PlatformDefaults.getLabelAlignPercentage() * paramFloat;
      }
      throw new IllegalArgumentException("Unknown/illegal unit: " + unit + ", unitStr: " + unitStr);
    }
    if ((subUnits != null) && (subUnits.length == 2))
    {
      f1 = subUnits[0].getPixelsExact(paramFloat, paramContainerWrapper, paramComponentWrapper);
      float f2 = subUnits[1].getPixelsExact(paramFloat, paramContainerWrapper, paramComponentWrapper);
      switch (oper)
      {
      case 101: 
        return f1 + f2;
      case 102: 
        return f1 - f2;
      case 103: 
        return f1 * f2;
      case 104: 
        return f1 / f2;
      case 105: 
        return f1 < f2 ? f1 : f2;
      case 106: 
        return f1 > f2 ? f1 : f2;
      case 107: 
        return (f1 + f2) * 0.5F;
      }
    }
    throw new IllegalArgumentException("Internal: Unknown Oper: " + oper);
  }
  
  private float lookup(float paramFloat, ContainerWrapper paramContainerWrapper, ComponentWrapper paramComponentWrapper)
  {
    float f = -8.7654312E7F;
    for (int i = CONVERTERS.size() - 1; i >= 0; i--)
    {
      f = ((UnitConverter)CONVERTERS.get(i)).convertToPixels(value, unitStr, isHor, paramFloat, paramContainerWrapper, paramComponentWrapper);
      if (f != -8.7654312E7F) {
        return f;
      }
    }
    return PlatformDefaults.convertToPixels(value, unitStr, isHor, paramFloat, paramContainerWrapper, paramComponentWrapper);
  }
  
  private int parseUnitString()
  {
    int i = unitStr.length();
    if (i == 0) {
      return isHor ? PlatformDefaults.getDefaultHorizontalUnit() : PlatformDefaults.getDefaultVerticalUnit();
    }
    Integer localInteger = (Integer)UNIT_MAP.get(unitStr);
    if (localInteger != null) {
      return localInteger.intValue();
    }
    if (unitStr.equals("lp")) {
      return isHor ? 1 : 2;
    }
    if (unitStr.equals("sp")) {
      return isHor ? 8 : 9;
    }
    if (lookup(0.0F, null, null) != -8.7654312E7F) {
      return 26;
    }
    int j = unitStr.indexOf('.');
    if (j != -1)
    {
      linkId = unitStr.substring(0, j);
      String str = unitStr.substring(j + 1);
      if (str.equals("x")) {
        return 18;
      }
      if (str.equals("y")) {
        return 19;
      }
      if ((str.equals("w")) || (str.equals("width"))) {
        return 20;
      }
      if ((str.equals("h")) || (str.equals("height"))) {
        return 21;
      }
      if (str.equals("x2")) {
        return 22;
      }
      if (str.equals("y2")) {
        return 23;
      }
      if (str.equals("xpos")) {
        return 24;
      }
      if (str.equals("ypos")) {
        return 25;
      }
    }
    throw new IllegalArgumentException("Unknown keyword: " + unitStr);
  }
  
  final boolean isLinked()
  {
    return linkId != null;
  }
  
  final boolean isLinkedDeep()
  {
    if (subUnits == null) {
      return linkId != null;
    }
    for (UnitValue localUnitValue : subUnits) {
      if (localUnitValue.isLinkedDeep()) {
        return true;
      }
    }
    return false;
  }
  
  final String getLinkTargetId()
  {
    return linkId;
  }
  
  final UnitValue getSubUnitValue(int paramInt)
  {
    return subUnits[paramInt];
  }
  
  final int getSubUnitCount()
  {
    return subUnits != null ? subUnits.length : 0;
  }
  
  public final UnitValue[] getSubUnits()
  {
    return subUnits != null ? (UnitValue[])subUnits.clone() : null;
  }
  
  public final int getUnit()
  {
    return unit;
  }
  
  public final String getUnitString()
  {
    return unitStr;
  }
  
  public final int getOperation()
  {
    return oper;
  }
  
  public final float getValue()
  {
    return value;
  }
  
  public final boolean isHorizontal()
  {
    return isHor;
  }
  
  public final String toString()
  {
    return getClass().getName() + ". Value=" + value + ", unit=" + unit + ", unitString: " + unitStr + ", oper=" + oper + ", isHor: " + isHor;
  }
  
  public final String getConstraintString()
  {
    return LayoutUtil.getCCString(this);
  }
  
  public final int hashCode()
  {
    return (int)(value * 12345.0F) + (oper >>> 5) + unit >>> 17;
  }
  
  public static synchronized void addGlobalUnitConverter(UnitConverter paramUnitConverter)
  {
    if (paramUnitConverter == null) {
      throw new NullPointerException();
    }
    CONVERTERS.add(paramUnitConverter);
  }
  
  public static synchronized boolean removeGlobalUnitConverter(UnitConverter paramUnitConverter)
  {
    return CONVERTERS.remove(paramUnitConverter);
  }
  
  public static synchronized UnitConverter[] getGlobalUnitConverters()
  {
    return (UnitConverter[])CONVERTERS.toArray(new UnitConverter[CONVERTERS.size()]);
  }
  
  /**
   * @deprecated
   */
  public static int getDefaultUnit()
  {
    return PlatformDefaults.getDefaultHorizontalUnit();
  }
  
  /**
   * @deprecated
   */
  public static void setDefaultUnit(int paramInt)
  {
    PlatformDefaults.setDefaultHorizontalUnit(paramInt);
    PlatformDefaults.setDefaultVerticalUnit(paramInt);
  }
  
  private Object readResolve()
    throws ObjectStreamException
  {
    return LayoutUtil.getSerializedObject(this);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (getClass() == UnitValue.class) {
      LayoutUtil.writeAsXML(paramObjectOutputStream, this);
    }
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream)
    throws IOException, ClassNotFoundException
  {
    LayoutUtil.setSerializedObject(this, LayoutUtil.readAsXML(paramObjectInputStream));
  }
  
  static
  {
    UNIT_MAP.put("px", Integer.valueOf(0));
    UNIT_MAP.put("lpx", Integer.valueOf(1));
    UNIT_MAP.put("lpy", Integer.valueOf(2));
    UNIT_MAP.put("%", Integer.valueOf(6));
    UNIT_MAP.put("cm", Integer.valueOf(4));
    UNIT_MAP.put("in", Integer.valueOf(5));
    UNIT_MAP.put("spx", Integer.valueOf(8));
    UNIT_MAP.put("spy", Integer.valueOf(9));
    UNIT_MAP.put("al", Integer.valueOf(12));
    UNIT_MAP.put("mm", Integer.valueOf(3));
    UNIT_MAP.put("pt", Integer.valueOf(7));
    UNIT_MAP.put("min", Integer.valueOf(13));
    UNIT_MAP.put("minimum", Integer.valueOf(13));
    UNIT_MAP.put("p", Integer.valueOf(14));
    UNIT_MAP.put("pref", Integer.valueOf(14));
    UNIT_MAP.put("max", Integer.valueOf(15));
    UNIT_MAP.put("maximum", Integer.valueOf(15));
    UNIT_MAP.put("button", Integer.valueOf(16));
    UNIT_MAP.put("label", Integer.valueOf(27));
    ZERO = new UnitValue(0.0F, null, 0, true, 100, null, null, "0px");
    TOP = new UnitValue(0.0F, null, 6, false, 100, null, null, "top");
    LEADING = new UnitValue(0.0F, null, 6, true, 100, null, null, "leading");
    LEFT = new UnitValue(0.0F, null, 6, true, 100, null, null, "left");
    CENTER = new UnitValue(50.0F, null, 6, true, 100, null, null, "center");
    TRAILING = new UnitValue(100.0F, null, 6, true, 100, null, null, "trailing");
    RIGHT = new UnitValue(100.0F, null, 6, true, 100, null, null, "right");
    BOTTOM = new UnitValue(100.0F, null, 6, false, 100, null, null, "bottom");
    LABEL = new UnitValue(0.0F, null, 27, false, 100, null, null, "label");
    INF = new UnitValue(2097051.0F, null, 0, true, 100, null, null, "inf");
    BASELINE_IDENTITY = new UnitValue(0.0F, null, -1, false, 100, null, null, "baseline");
    SCALE = new float[] { 25.4F, 2.54F, 1.0F, 0.0F, 72.0F };
    if (LayoutUtil.HAS_BEANS) {
      LayoutUtil.setDelegate(UnitValue.class, new PersistenceDelegate()
      {
        protected Expression instantiate(Object paramAnonymousObject, Encoder paramAnonymousEncoder)
        {
          UnitValue localUnitValue = (UnitValue)paramAnonymousObject;
          String str = localUnitValue.getConstraintString();
          if (str == null) {
            throw new IllegalStateException("Design time must be on to use XML persistence. See LayoutUtil.");
          }
          return new Expression(paramAnonymousObject, ConstraintParser.class, "parseUnitValueOrAlign", new Object[] { localUnitValue.getConstraintString(), localUnitValue.isHorizontal() ? Boolean.TRUE : Boolean.FALSE, null });
        }
      });
    }
  }
}
