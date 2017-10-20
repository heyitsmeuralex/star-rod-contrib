package org.apache.commons.math.analysis;

public abstract interface DifferentiableUnivariateMatrixFunction
  extends UnivariateMatrixFunction
{
  public abstract UnivariateMatrixFunction derivative();
}
