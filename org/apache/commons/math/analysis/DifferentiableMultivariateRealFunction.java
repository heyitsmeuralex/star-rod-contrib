package org.apache.commons.math.analysis;

public abstract interface DifferentiableMultivariateRealFunction
  extends MultivariateRealFunction
{
  public abstract MultivariateRealFunction partialDerivative(int paramInt);
  
  public abstract MultivariateVectorialFunction gradient();
}
