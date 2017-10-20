package org.apache.commons.math.util;

import org.apache.commons.math.MathException;

public abstract interface NumberTransformer
{
  public abstract double transform(Object paramObject)
    throws MathException;
}
