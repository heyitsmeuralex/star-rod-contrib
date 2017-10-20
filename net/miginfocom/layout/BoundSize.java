package net.miginfocom.layout;

import java.beans.Encoder;
import java.beans.Expression;
import java.beans.PersistenceDelegate;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

public class BoundSize
  implements Serializable
{
  public static final BoundSize NULL_SIZE = new BoundSize(null, null);
  public static final BoundSize ZERO_PIXEL = new BoundSize(UnitValue.ZERO, "0px");
  private final transient UnitValue min;
  private final transient UnitValue pref;
  private final transient UnitValue max;
  private final transient boolean gapPush;
  private static final long serialVersionUID = 1L;
  
  public BoundSize(UnitValue paramUnitValue, String paramString)
  {
    this(paramUnitValue, paramUnitValue, paramUnitValue, paramString);
  }
  
  public BoundSize(UnitValue paramUnitValue1, UnitValue paramUnitValue2, UnitValue paramUnitValue3, String paramString)
  {
    this(paramUnitValue1, paramUnitValue2, paramUnitValue3, false, paramString);
  }
  
  public BoundSize(UnitValue paramUnitValue1, UnitValue paramUnitValue2, UnitValue paramUnitValue3, boolean paramBoolean, String paramString)
  {
    min = paramUnitValue1;
    pref = paramUnitValue2;
    max = paramUnitValue3;
    gapPush = paramBoolean;
    LayoutUtil.putCCString(this, paramString);
  }
  
  public final UnitValue getMin()
  {
    return min;
  }
  
  public final UnitValue getPreferred()
  {
    return pref;
  }
  
  public final UnitValue getMax()
  {
    return max;
  }
  
  public boolean getGapPush()
  {
    return gapPush;
  }
  
  public boolean isUnset()
  {
    return (this == ZERO_PIXEL) || ((pref == null) && (min == null) && (max == null) && (!gapPush));
  }
  
  public int constrain(int paramInt, float paramFloat, ContainerWrapper paramContainerWrapper)
  {
    if (max != null) {
      paramInt = Math.min(paramInt, max.getPixels(paramFloat, paramContainerWrapper, paramContainerWrapper));
    }
    if (min != null) {
      paramInt = Math.max(paramInt, min.getPixels(paramFloat, paramContainerWrapper, paramContainerWrapper));
    }
    return paramInt;
  }
  
  final UnitValue getSize(int paramInt)
  {
    switch (paramInt)
    {
    case 0: 
      return min;
    case 1: 
      return pref;
    case 2: 
      return max;
    }
    throw new IllegalArgumentException("Unknown size: " + paramInt);
  }
  
  final int[] getPixelSizes(float paramFloat, ContainerWrapper paramContainerWrapper, ComponentWrapper paramComponentWrapper)
  {
    return new int[] { min != null ? min.getPixels(paramFloat, paramContainerWrapper, paramComponentWrapper) : 0, pref != null ? pref.getPixels(paramFloat, paramContainerWrapper, paramComponentWrapper) : 0, max != null ? max.getPixels(paramFloat, paramContainerWrapper, paramComponentWrapper) : 2097051 };
  }
  
  String getConstraintString()
  {
    String str = LayoutUtil.getCCString(this);
    if (str != null) {
      return str;
    }
    if ((min == pref) && (pref == max)) {
      return min != null ? min.getConstraintString() + "!" : "null";
    }
    StringBuilder localStringBuilder = new StringBuilder(16);
    if (min != null) {
      localStringBuilder.append(min.getConstraintString()).append(':');
    }
    if (pref != null)
    {
      if ((min == null) && (max != null)) {
        localStringBuilder.append(":");
      }
      localStringBuilder.append(pref.getConstraintString());
    }
    else if (min != null)
    {
      localStringBuilder.append('n');
    }
    if (max != null) {
      localStringBuilder.append(localStringBuilder.length() == 0 ? "::" : ":").append(max.getConstraintString());
    }
    if (gapPush)
    {
      if (localStringBuilder.length() > 0) {
        localStringBuilder.append(':');
      }
      localStringBuilder.append("push");
    }
    return localStringBuilder.toString();
  }
  
  void checkNotLinked()
  {
    if (((min != null) && (min.isLinkedDeep())) || ((pref != null) && (pref.isLinkedDeep())) || ((max != null) && (max.isLinkedDeep()))) {
      throw new IllegalArgumentException("Size may not contain links");
    }
  }
  
  protected Object readResolve()
    throws ObjectStreamException
  {
    return LayoutUtil.getSerializedObject(this);
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream)
    throws IOException
  {
    if (getClass() == BoundSize.class) {
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
    if (LayoutUtil.HAS_BEANS) {
      LayoutUtil.setDelegate(BoundSize.class, new PersistenceDelegate()
      {
        protected Expression instantiate(Object paramAnonymousObject, Encoder paramAnonymousEncoder)
        {
          BoundSize localBoundSize = (BoundSize)paramAnonymousObject;
          return new Expression(paramAnonymousObject, BoundSize.class, "new", new Object[] { localBoundSize.getMin(), localBoundSize.getPreferred(), localBoundSize.getMax(), Boolean.valueOf(localBoundSize.getGapPush()), localBoundSize.getConstraintString() });
        }
      });
    }
  }
}
