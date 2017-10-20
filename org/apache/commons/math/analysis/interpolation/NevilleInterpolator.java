package org.apache.commons.math.analysis.interpolation;

import java.io.Serializable;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm;




































public class NevilleInterpolator
  implements UnivariateRealInterpolator, Serializable
{
  static final long serialVersionUID = 3003707660147873733L;
  
  public NevilleInterpolator() {}
  
  public PolynomialFunctionLagrangeForm interpolate(double[] x, double[] y)
    throws MathException
  {
    return new PolynomialFunctionLagrangeForm(x, y);
  }
}
