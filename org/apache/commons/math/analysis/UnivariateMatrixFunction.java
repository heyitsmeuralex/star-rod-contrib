package org.apache.commons.math.analysis;

import org.apache.commons.math.FunctionEvaluationException;

public abstract interface UnivariateMatrixFunction
{
  public abstract double[][] value(double paramDouble)
    throws FunctionEvaluationException;
}
