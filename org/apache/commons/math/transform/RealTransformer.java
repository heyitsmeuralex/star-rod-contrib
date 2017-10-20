package org.apache.commons.math.transform;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

public abstract interface RealTransformer
{
  public abstract double[] transform(double[] paramArrayOfDouble)
    throws IllegalArgumentException;
  
  public abstract double[] transform(UnivariateRealFunction paramUnivariateRealFunction, double paramDouble1, double paramDouble2, int paramInt)
    throws FunctionEvaluationException, IllegalArgumentException;
  
  public abstract double[] inversetransform(double[] paramArrayOfDouble)
    throws IllegalArgumentException;
  
  public abstract double[] inversetransform(UnivariateRealFunction paramUnivariateRealFunction, double paramDouble1, double paramDouble2, int paramInt)
    throws FunctionEvaluationException, IllegalArgumentException;
}
