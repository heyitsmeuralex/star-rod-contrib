package org.apache.commons.math.analysis;

public abstract interface DifferentiableUnivariateVectorialFunction
  extends UnivariateVectorialFunction
{
  public abstract UnivariateVectorialFunction derivative();
}
