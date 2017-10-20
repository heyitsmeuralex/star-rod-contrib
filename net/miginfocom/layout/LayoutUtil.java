package net.miginfocom.layout;

import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.Beans;
import java.beans.ExceptionListener;
import java.beans.Introspector;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.OutputStream;
import java.util.IdentityHashMap;
import java.util.TreeSet;
import java.util.WeakHashMap;

public final class LayoutUtil
{
  public static final int INF = 2097051;
  static final int NOT_SET = -2147471302;
  public static final int MIN = 0;
  public static final int PREF = 1;
  public static final int MAX = 2;
  private static volatile WeakHashMap<Object, String> CR_MAP = null;
  private static volatile WeakHashMap<Object, Boolean> DT_MAP = null;
  private static int eSz = 0;
  private static int globalDebugMillis = 0;
  public static final boolean HAS_BEANS = hasBeans();
  private static ByteArrayOutputStream writeOutputStream = null;
  private static byte[] readBuf = null;
  private static final IdentityHashMap<Object, Object> SER_MAP = new IdentityHashMap(2);
  
  private static boolean hasBeans()
  {
    try
    {
      LayoutUtil.class.getClassLoader().loadClass("java.beans.Beans");
      return true;
    }
    catch (ClassNotFoundException localClassNotFoundException) {}
    return false;
  }
  
  private LayoutUtil() {}
  
  public static String getVersion()
  {
    return "4.0";
  }
  
  public static int getGlobalDebugMillis()
  {
    return globalDebugMillis;
  }
  
  public static void setGlobalDebugMillis(int paramInt)
  {
    globalDebugMillis = paramInt;
  }
  
  public static void setDesignTime(ContainerWrapper paramContainerWrapper, boolean paramBoolean)
  {
    if (DT_MAP == null) {
      DT_MAP = new WeakHashMap();
    }
    DT_MAP.put(paramContainerWrapper != null ? paramContainerWrapper.getComponent() : null, Boolean.valueOf(paramBoolean));
  }
  
  public static boolean isDesignTime(ContainerWrapper paramContainerWrapper)
  {
    if (DT_MAP == null) {
      return (HAS_BEANS) && (Beans.isDesignTime());
    }
    if ((paramContainerWrapper != null) && (!DT_MAP.containsKey(paramContainerWrapper.getComponent()))) {
      paramContainerWrapper = null;
    }
    Boolean localBoolean = (Boolean)DT_MAP.get(paramContainerWrapper != null ? paramContainerWrapper.getComponent() : null);
    return (localBoolean != null) && (localBoolean.booleanValue());
  }
  
  public static int getDesignTimeEmptySize()
  {
    return eSz;
  }
  
  public static void setDesignTimeEmptySize(int paramInt)
  {
    eSz = paramInt;
  }
  
  static void putCCString(Object paramObject, String paramString)
  {
    if ((paramString != null) && (paramObject != null) && (isDesignTime(null)))
    {
      if (CR_MAP == null) {
        CR_MAP = new WeakHashMap(64);
      }
      CR_MAP.put(paramObject, paramString);
    }
  }
  
  static synchronized void setDelegate(Class paramClass, PersistenceDelegate paramPersistenceDelegate)
  {
    try
    {
      Introspector.getBeanInfo(paramClass, 3).getBeanDescriptor().setValue("persistenceDelegate", paramPersistenceDelegate);
    }
    catch (Exception localException) {}
  }
  
  static String getCCString(Object paramObject)
  {
    return CR_MAP != null ? (String)CR_MAP.get(paramObject) : null;
  }
  
  static void throwCC()
  {
    throw new IllegalStateException("setStoreConstraintData(true) must be set for strings to be saved.");
  }
  
  static int[] calculateSerial(int[][] paramArrayOfInt, ResizeConstraint[] paramArrayOfResizeConstraint, Float[] paramArrayOfFloat, int paramInt1, int paramInt2)
  {
    float[] arrayOfFloat = new float[paramArrayOfInt.length];
    float f1 = 0.0F;
    for (int i = 0; i < paramArrayOfInt.length; i++) {
      if (paramArrayOfInt[i] != null)
      {
        float f2 = paramArrayOfInt[i][paramInt1] != -2147471302 ? paramArrayOfInt[i][paramInt1] : 0.0F;
        int k = getBrokenBoundary(f2, paramArrayOfInt[i][0], paramArrayOfInt[i][2]);
        if (k != -2147471302) {
          f2 = k;
        }
        f1 += f2;
        arrayOfFloat[i] = f2;
      }
    }
    i = Math.round(f1);
    if ((i != paramInt2) && (paramArrayOfResizeConstraint != null))
    {
      int j = i < paramInt2 ? 1 : 0;
      TreeSet localTreeSet = new TreeSet();
      for (int m = 0; m < paramArrayOfInt.length; m++)
      {
        ResizeConstraint localResizeConstraint1 = (ResizeConstraint)getIndexSafe(paramArrayOfResizeConstraint, m);
        if (localResizeConstraint1 != null) {
          localTreeSet.add(Integer.valueOf(j != 0 ? growPrio : shrinkPrio));
        }
      }
      Integer[] arrayOfInteger = (Integer[])localTreeSet.toArray(new Integer[localTreeSet.size()]);
      for (int n = 0; n <= ((j != 0) && (paramArrayOfFloat != null) ? 1 : 0); n++) {
        for (int i1 = arrayOfInteger.length - 1; i1 >= 0; i1--)
        {
          int i2 = arrayOfInteger[i1].intValue();
          float f3 = 0.0F;
          Float[] arrayOfFloat1 = new Float[paramArrayOfInt.length];
          for (int i3 = 0; i3 < paramArrayOfInt.length; i3++) {
            if (paramArrayOfInt[i3] != null)
            {
              ResizeConstraint localResizeConstraint2 = (ResizeConstraint)getIndexSafe(paramArrayOfResizeConstraint, i3);
              if (localResizeConstraint2 != null)
              {
                int i4 = j != 0 ? growPrio : shrinkPrio;
                if (i2 == i4)
                {
                  if (j != 0) {
                    arrayOfFloat1[i3] = ((n == 0) || (grow != null) ? grow : paramArrayOfFloat[(paramArrayOfFloat.length - 1)]);
                  } else {
                    arrayOfFloat1[i3] = shrink;
                  }
                  if (arrayOfFloat1[i3] != null) {
                    f3 += arrayOfFloat1[i3].floatValue();
                  }
                }
              }
            }
          }
          if (f3 > 0.0F) {
            do
            {
              float f4 = paramInt2 - f1;
              i3 = 0;
              float f5 = 0.0F;
              for (int i5 = 0; (i5 < paramArrayOfInt.length) && (f3 > 1.0E-4F); i5++)
              {
                Float localFloat = arrayOfFloat1[i5];
                if (localFloat != null)
                {
                  float f6 = f4 * localFloat.floatValue() / f3;
                  float f7 = arrayOfFloat[i5] + f6;
                  if (paramArrayOfInt[i5] != null)
                  {
                    int i6 = getBrokenBoundary(f7, paramArrayOfInt[i5][0], paramArrayOfInt[i5][2]);
                    if (i6 != -2147471302)
                    {
                      arrayOfFloat1[i5] = null;
                      i3 = 1;
                      f5 += localFloat.floatValue();
                      f7 = i6;
                      f6 = f7 - arrayOfFloat[i5];
                    }
                  }
                  arrayOfFloat[i5] = f7;
                  f1 += f6;
                }
              }
              f3 -= f5;
            } while (i3 != 0);
          }
        }
      }
    }
    return roundSizes(arrayOfFloat);
  }
  
  static Object getIndexSafe(Object[] paramArrayOfObject, int paramInt)
  {
    return paramArrayOfObject != null ? paramArrayOfObject[(paramArrayOfObject.length - 1)] : null;
  }
  
  private static int getBrokenBoundary(float paramFloat, int paramInt1, int paramInt2)
  {
    if (paramInt1 != -2147471302)
    {
      if (paramFloat < paramInt1) {
        return paramInt1;
      }
    }
    else if (paramFloat < 0.0F) {
      return 0;
    }
    if ((paramInt2 != -2147471302) && (paramFloat > paramInt2)) {
      return paramInt2;
    }
    return -2147471302;
  }
  
  static int sum(int[] paramArrayOfInt, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = paramInt1;
    int k = paramInt1 + paramInt2;
    while (j < k)
    {
      i += paramArrayOfInt[j];
      j++;
    }
    return i;
  }
  
  static int sum(int[] paramArrayOfInt)
  {
    return sum(paramArrayOfInt, 0, paramArrayOfInt.length);
  }
  
  public static int getSizeSafe(int[] paramArrayOfInt, int paramInt)
  {
    if ((paramArrayOfInt == null) || (paramArrayOfInt[paramInt] == -2147471302)) {
      return paramInt == 2 ? 2097051 : 0;
    }
    return paramArrayOfInt[paramInt];
  }
  
  static BoundSize derive(BoundSize paramBoundSize, UnitValue paramUnitValue1, UnitValue paramUnitValue2, UnitValue paramUnitValue3)
  {
    if ((paramBoundSize == null) || (paramBoundSize.isUnset())) {
      return new BoundSize(paramUnitValue1, paramUnitValue2, paramUnitValue3, null);
    }
    return new BoundSize(paramUnitValue1 != null ? paramUnitValue1 : paramBoundSize.getMin(), paramUnitValue2 != null ? paramUnitValue2 : paramBoundSize.getPreferred(), paramUnitValue3 != null ? paramUnitValue3 : paramBoundSize.getMax(), paramBoundSize.getGapPush(), null);
  }
  
  public static boolean isLeftToRight(LC paramLC, ContainerWrapper paramContainerWrapper)
  {
    if ((paramLC != null) && (paramLC.getLeftToRight() != null)) {
      return paramLC.getLeftToRight().booleanValue();
    }
    return (paramContainerWrapper == null) || (paramContainerWrapper.isLeftToRight());
  }
  
  static int[] roundSizes(float[] paramArrayOfFloat)
  {
    int[] arrayOfInt = new int[paramArrayOfFloat.length];
    float f = 0.0F;
    for (int i = 0; i < arrayOfInt.length; i++)
    {
      int j = (int)(f + 0.5F);
      f += paramArrayOfFloat[i];
      arrayOfInt[i] = ((int)(f + 0.5F) - j);
    }
    return arrayOfInt;
  }
  
  static boolean equals(Object paramObject1, Object paramObject2)
  {
    return (paramObject1 == paramObject2) || ((paramObject1 != null) && (paramObject2 != null) && (paramObject1.equals(paramObject2)));
  }
  
  static UnitValue getInsets(LC paramLC, int paramInt, boolean paramBoolean)
  {
    UnitValue[] arrayOfUnitValue = paramLC.getInsets();
    return paramBoolean ? PlatformDefaults.getPanelInsets(paramInt) : (arrayOfUnitValue != null) && (arrayOfUnitValue[paramInt] != null) ? arrayOfUnitValue[paramInt] : UnitValue.ZERO;
  }
  
  static void writeXMLObject(OutputStream paramOutputStream, Object paramObject, ExceptionListener paramExceptionListener)
  {
    ClassLoader localClassLoader = Thread.currentThread().getContextClassLoader();
    Thread.currentThread().setContextClassLoader(LayoutUtil.class.getClassLoader());
    XMLEncoder localXMLEncoder = new XMLEncoder(paramOutputStream);
    if (paramExceptionListener != null) {
      localXMLEncoder.setExceptionListener(paramExceptionListener);
    }
    localXMLEncoder.writeObject(paramObject);
    localXMLEncoder.close();
    Thread.currentThread().setContextClassLoader(localClassLoader);
  }
  
  public static synchronized void writeAsXML(ObjectOutput paramObjectOutput, Object paramObject)
    throws IOException
  {
    if (writeOutputStream == null) {
      writeOutputStream = new ByteArrayOutputStream(16384);
    }
    writeOutputStream.reset();
    writeXMLObject(writeOutputStream, paramObject, new ExceptionListener()
    {
      public void exceptionThrown(Exception paramAnonymousException)
      {
        paramAnonymousException.printStackTrace();
      }
    });
    byte[] arrayOfByte = writeOutputStream.toByteArray();
    paramObjectOutput.writeInt(arrayOfByte.length);
    paramObjectOutput.write(arrayOfByte);
  }
  
  public static synchronized Object readAsXML(ObjectInput paramObjectInput)
    throws IOException
  {
    if (readBuf == null) {
      readBuf = new byte['䀀'];
    }
    Thread localThread = Thread.currentThread();
    ClassLoader localClassLoader = null;
    try
    {
      localClassLoader = localThread.getContextClassLoader();
      localThread.setContextClassLoader(LayoutUtil.class.getClassLoader());
    }
    catch (SecurityException localSecurityException) {}
    Object localObject = null;
    try
    {
      int i = paramObjectInput.readInt();
      if (i > readBuf.length) {
        readBuf = new byte[i];
      }
      paramObjectInput.readFully(readBuf, 0, i);
      localObject = new XMLDecoder(new ByteArrayInputStream(readBuf, 0, i)).readObject();
    }
    catch (EOFException localEOFException) {}
    if (localClassLoader != null) {
      localThread.setContextClassLoader(localClassLoader);
    }
    return localObject;
  }
  
  public static void setSerializedObject(Object paramObject1, Object paramObject2)
  {
    synchronized (SER_MAP)
    {
      SER_MAP.put(paramObject1, paramObject2);
    }
  }
  
  public static Object getSerializedObject(Object paramObject)
  {
    synchronized (SER_MAP)
    {
      return SER_MAP.remove(paramObject);
    }
  }
}
