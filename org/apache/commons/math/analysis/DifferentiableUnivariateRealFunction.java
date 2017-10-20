package org.apache.commons.math.analysis;

public abstract interface DifferentiableUnivariateRealFunction
  extends UnivariateRealFunction
{
  public abstract UnivariateRealFunction derivative();
}
