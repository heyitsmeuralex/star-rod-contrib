package org.apache.commons.math.analysis;

import org.apache.commons.math.FunctionEvaluationException;

public abstract interface BivariateRealFunction
{
  public abstract double value(double paramDouble1, double paramDouble2)
    throws FunctionEvaluationException;
}
