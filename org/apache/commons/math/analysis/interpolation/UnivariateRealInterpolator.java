package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

public abstract interface UnivariateRealInterpolator
{
  public abstract UnivariateRealFunction interpolate(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
    throws MathException;
}
