package org.apache.commons.math.analysis;

import org.apache.commons.math.FunctionEvaluationException;

public abstract interface TrivariateRealFunction
{
  public abstract double value(double paramDouble1, double paramDouble2, double paramDouble3)
    throws FunctionEvaluationException;
}
