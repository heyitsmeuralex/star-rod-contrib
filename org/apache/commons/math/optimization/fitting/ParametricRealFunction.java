package org.apache.commons.math.optimization.fitting;

import org.apache.commons.math.FunctionEvaluationException;

public abstract interface ParametricRealFunction
{
  public abstract double value(double paramDouble, double[] paramArrayOfDouble)
    throws FunctionEvaluationException;
  
  public abstract double[] gradient(double paramDouble, double[] paramArrayOfDouble)
    throws FunctionEvaluationException;
}
