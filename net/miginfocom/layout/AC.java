package net.miginfocom.layout;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;
import java.util.ArrayList;

public final class AC
  implements Externalizable
{
  private final ArrayList<DimConstraint> cList = new ArrayList(8);
  private transient int curIx = 0;
  
  public AC()
  {
    cList.add(new DimConstraint());
  }
  
  public final DimConstraint[] getConstaints()
  {
    return (DimConstraint[])cList.toArray(new DimConstraint[cList.size()]);
  }
  
  public final void setConstaints(DimConstraint[] paramArrayOfDimConstraint)
  {
    if ((paramArrayOfDimConstraint == null) || (paramArrayOfDimConstraint.length < 1)) {
      paramArrayOfDimConstraint = new DimConstraint[] { new DimConstraint() };
    }
    cList.clear();
    cList.ensureCapacity(paramArrayOfDimConstraint.length);
    for (DimConstraint localDimConstraint : paramArrayOfDimConstraint) {
      cList.add(localDimConstraint);
    }
  }
  
  public int getCount()
  {
    return cList.size();
  }
  
  public final AC count(int paramInt)
  {
    makeSize(paramInt);
    return this;
  }
  
  public final AC noGrid()
  {
    return noGrid(new int[] { curIx });
  }
  
  public final AC noGrid(int... paramVarArgs)
  {
    for (int i = paramVarArgs.length - 1; i >= 0; i--)
    {
      int j = paramVarArgs[i];
      makeSize(j);
      ((DimConstraint)cList.get(j)).setNoGrid(true);
    }
    return this;
  }
  
  public final AC index(int paramInt)
  {
    makeSize(paramInt);
    curIx = paramInt;
    return this;
  }
  
  public final AC fill()
  {
    return fill(new int[] { curIx });
  }
  
  public final AC fill(int... paramVarArgs)
  {
    for (int i = paramVarArgs.length - 1; i >= 0; i--)
    {
      int j = paramVarArgs[i];
      makeSize(j);
      ((DimConstraint)cList.get(j)).setFill(true);
    }
    return this;
  }
  
  public final AC sizeGroup()
  {
    return sizeGroup("", new int[] { curIx });
  }
  
  public final AC sizeGroup(String paramString)
  {
    return sizeGroup(paramString, new int[] { curIx });
  }
  
  public final AC sizeGroup(String paramString, int... paramVarArgs)
  {
    for (int i = paramVarArgs.length - 1; i >= 0; i--)
    {
      int j = paramVarArgs[i];
      makeSize(j);
      ((DimConstraint)cList.get(j)).setSizeGroup(paramString);
    }
    return this;
  }
  
  public final AC size(String paramString)
  {
    return size(paramString, new int[] { curIx });
  }
  
  public final AC size(String paramString, int... paramVarArgs)
  {
    BoundSize localBoundSize = ConstraintParser.parseBoundSize(paramString, false, true);
    for (int i = paramVarArgs.length - 1; i >= 0; i--)
    {
      int j = paramVarArgs[i];
      makeSize(j);
      ((DimConstraint)cList.get(j)).setSize(localBoundSize);
    }
    return this;
  }
  
  public final AC gap()
  {
    curIx += 1;
    return this;
  }
  
  public final AC gap(String paramString)
  {
    return gap(paramString, new int[] { curIx++ });
  }
  
  public final AC gap(String paramString, int... paramVarArgs)
  {
    BoundSize localBoundSize = paramString != null ? ConstraintParser.parseBoundSize(paramString, true, true) : null;
    for (int i = paramVarArgs.length - 1; i >= 0; i--)
    {
      int j = paramVarArgs[i];
      makeSize(j);
      if (localBoundSize != null) {
        ((DimConstraint)cList.get(j)).setGapAfter(localBoundSize);
      }
    }
    return this;
  }
  
  public final AC align(String paramString)
  {
    return align(paramString, new int[] { curIx });
  }
  
  public final AC align(String paramString, int... paramVarArgs)
  {
    UnitValue localUnitValue = ConstraintParser.parseAlignKeywords(paramString, true);
    if (localUnitValue == null) {
      localUnitValue = ConstraintParser.parseAlignKeywords(paramString, false);
    }
    for (int i = paramVarArgs.length - 1; i >= 0; i--)
    {
      int j = paramVarArgs[i];
      makeSize(j);
      ((DimConstraint)cList.get(j)).setAlign(localUnitValue);
    }
    return this;
  }
  
  public final AC growPrio(int paramInt)
  {
    return growPrio(paramInt, new int[] { curIx });
  }
  
  public final AC growPrio(int paramInt, int... paramVarArgs)
  {
    for (int i = paramVarArgs.length - 1; i >= 0; i--)
    {
      int j = paramVarArgs[i];
      makeSize(j);
      ((DimConstraint)cList.get(j)).setGrowPriority(paramInt);
    }
    return this;
  }
  
  public final AC grow()
  {
    return grow(1.0F, new int[] { curIx });
  }
  
  public final AC grow(float paramFloat)
  {
    return grow(paramFloat, new int[] { curIx });
  }
  
  public final AC grow(float paramFloat, int... paramVarArgs)
  {
    Float localFloat = new Float(paramFloat);
    for (int i = paramVarArgs.length - 1; i >= 0; i--)
    {
      int j = paramVarArgs[i];
      makeSize(j);
      ((DimConstraint)cList.get(j)).setGrow(localFloat);
    }
    return this;
  }
  
  public final AC shrinkPrio(int paramInt)
  {
    return shrinkPrio(paramInt, new int[] { curIx });
  }
  
  public final AC shrinkPrio(int paramInt, int... paramVarArgs)
  {
    for (int i = paramVarArgs.length - 1; i >= 0; i--)
    {
      int j = paramVarArgs[i];
      makeSize(j);
      ((DimConstraint)cList.get(j)).setShrinkPriority(paramInt);
    }
    return this;
  }
  
  public final AC shrink()
  {
    return shrink(100.0F, new int[] { curIx });
  }
  
  public final AC shrink(float paramFloat)
  {
    return shrink(paramFloat, new int[] { curIx });
  }
  
  public final AC shrink(float paramFloat, int... paramVarArgs)
  {
    Float localFloat = new Float(paramFloat);
    for (int i = paramVarArgs.length - 1; i >= 0; i--)
    {
      int j = paramVarArgs[i];
      makeSize(j);
      ((DimConstraint)cList.get(j)).setShrink(localFloat);
    }
    return this;
  }
  
  /**
   * @deprecated
   */
  public final AC shrinkWeight(float paramFloat)
  {
    return shrink(paramFloat);
  }
  
  /**
   * @deprecated
   */
  public final AC shrinkWeight(float paramFloat, int... paramVarArgs)
  {
    return shrink(paramFloat, paramVarArgs);
  }
  
  private void makeSize(int paramInt)
  {
    if (cList.size() <= paramInt)
    {
      cList.ensureCapacity(paramInt);
      for (int i = cList.size(); i <= paramInt; i++) {
        cList.add(new DimConstraint());
      }
    }
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
    if (getClass() == AC.class) {
      LayoutUtil.writeAsXML(paramObjectOutput, this);
    }
  }
}
