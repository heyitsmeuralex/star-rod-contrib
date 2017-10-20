package org.apache.commons.math.analysis;

public abstract interface DifferentiableMultivariateVectorialFunction
  extends MultivariateVectorialFunction
{
  public abstract MultivariateMatrixFunction jacobian();
}
