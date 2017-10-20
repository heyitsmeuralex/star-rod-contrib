package net.miginfocom.layout;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.ObjectStreamException;

final class ResizeConstraint
  implements Externalizable
{
  static final Float WEIGHT_100 = new Float(100.0F);
  Float grow = null;
  int growPrio = 100;
  Float shrink = WEIGHT_100;
  int shrinkPrio = 100;
  
  public ResizeConstraint() {}
  
  ResizeConstraint(int paramInt1, Float paramFloat1, int paramInt2, Float paramFloat2)
  {
    shrinkPrio = paramInt1;
    shrink = paramFloat1;
    growPrio = paramInt2;
    grow = paramFloat2;
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
    if (getClass() == ResizeConstraint.class) {
      LayoutUtil.writeAsXML(paramObjectOutput, this);
    }
  }
}
