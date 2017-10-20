package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.MultivariateRealFunction;

public abstract interface MultivariateRealInterpolator
{
  public abstract MultivariateRealFunction interpolate(double[][] paramArrayOfDouble, double[] paramArrayOfDouble1)
    throws MathException, IllegalArgumentException;
}
