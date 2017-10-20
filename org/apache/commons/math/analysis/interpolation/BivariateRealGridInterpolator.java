package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.BivariateRealFunction;

public abstract interface BivariateRealGridInterpolator
{
  public abstract BivariateRealFunction interpolate(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[][] paramArrayOfDouble)
    throws MathException;
}
