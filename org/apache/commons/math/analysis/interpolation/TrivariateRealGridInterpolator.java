package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.TrivariateRealFunction;

public abstract interface TrivariateRealGridInterpolator
{
  public abstract TrivariateRealFunction interpolate(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2, double[] paramArrayOfDouble3, double[][][] paramArrayOfDouble)
    throws MathException;
}
