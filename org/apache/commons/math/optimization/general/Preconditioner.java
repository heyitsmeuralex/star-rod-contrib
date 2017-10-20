package org.apache.commons.math.optimization.general;

import org.apache.commons.math.FunctionEvaluationException;

public abstract interface Preconditioner
{
  public abstract double[] precondition(double[] paramArrayOfDouble1, double[] paramArrayOfDouble2)
    throws FunctionEvaluationException, IllegalArgumentException;
}
