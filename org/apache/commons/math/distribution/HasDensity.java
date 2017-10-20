package org.apache.commons.math.distribution;

import org.apache.commons.math.MathException;

@Deprecated
public abstract interface HasDensity<P>
{
  public abstract double density(P paramP)
    throws MathException;
}
