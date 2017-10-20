package org.apache.commons.math.analysis;

import org.apache.commons.math.FunctionEvaluationException;

public abstract interface MultivariateMatrixFunction
{
  public abstract double[][] value(double[] paramArrayOfDouble)
    throws FunctionEvaluationException, IllegalArgumentException;
}
