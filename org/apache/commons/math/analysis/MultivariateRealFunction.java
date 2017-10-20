package org.apache.commons.math.analysis;

import org.apache.commons.math.FunctionEvaluationException;

public abstract interface MultivariateRealFunction
{
  public abstract double value(double[] paramArrayOfDouble)
    throws FunctionEvaluationException, IllegalArgumentException;
}
