package org.apache.commons.math.analysis;

import org.apache.commons.math.FunctionEvaluationException;

public abstract interface UnivariateRealFunction
{
  public abstract double value(double paramDouble)
    throws FunctionEvaluationException;
}
