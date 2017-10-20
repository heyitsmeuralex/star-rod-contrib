package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NumberIsTooSmallException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.MathUtils;





























public class LinearInterpolator
  implements UnivariateRealInterpolator
{
  public LinearInterpolator() {}
  
  public PolynomialSplineFunction interpolate(double[] x, double[] y)
  {
    if (x.length != y.length) {
      throw new DimensionMismatchException(x.length, y.length);
    }
    
    if (x.length < 2) {
      throw new NumberIsTooSmallException(LocalizedFormats.NUMBER_OF_POINTS, Integer.valueOf(x.length), Integer.valueOf(2), true);
    }
    


    int n = x.length - 1;
    
    MathUtils.checkOrder(x);
    

    double[] m = new double[n];
    for (int i = 0; i < n; i++) {
      m[i] = ((y[(i + 1)] - y[i]) / (x[(i + 1)] - x[i]));
    }
    
    PolynomialFunction[] polynomials = new PolynomialFunction[n];
    double[] coefficients = new double[2];
    for (int i = 0; i < n; i++) {
      coefficients[0] = y[i];
      coefficients[1] = m[i];
      polynomials[i] = new PolynomialFunction(coefficients);
    }
    
    return new PolynomialSplineFunction(x, polynomials);
  }
}
