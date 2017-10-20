package org.apache.commons.math.analysis.polynomials;

import java.util.Arrays;
import org.apache.commons.math.ArgumentOutsideDomainException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;

















































































public class PolynomialSplineFunction
  implements DifferentiableUnivariateRealFunction
{
  private final double[] knots;
  private final PolynomialFunction[] polynomials;
  private final int n;
  
  public PolynomialSplineFunction(double[] knots, PolynomialFunction[] polynomials)
  {
    if (knots.length < 2) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_ENOUGH_POINTS_IN_SPLINE_PARTITION, new Object[] { Integer.valueOf(2), Integer.valueOf(knots.length) });
    }
    

    if (knots.length - 1 != polynomials.length) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.POLYNOMIAL_INTERPOLANTS_MISMATCH_SEGMENTS, new Object[] { Integer.valueOf(polynomials.length), Integer.valueOf(knots.length) });
    }
    

    if (!isStrictlyIncreasing(knots)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_STRICTLY_INCREASING_KNOT_VALUES, new Object[0]);
    }
    

    n = (knots.length - 1);
    this.knots = new double[n + 1];
    System.arraycopy(knots, 0, this.knots, 0, n + 1);
    this.polynomials = new PolynomialFunction[n];
    System.arraycopy(polynomials, 0, this.polynomials, 0, n);
  }
  









  public double value(double v)
    throws ArgumentOutsideDomainException
  {
    if ((v < knots[0]) || (v > knots[n])) {
      throw new ArgumentOutsideDomainException(v, knots[0], knots[n]);
    }
    int i = Arrays.binarySearch(knots, v);
    if (i < 0) {
      i = -i - 2;
    }
    


    if (i >= polynomials.length) {
      i--;
    }
    return polynomials[i].value(v - knots[i]);
  }
  



  public UnivariateRealFunction derivative()
  {
    return polynomialSplineDerivative();
  }
  




  public PolynomialSplineFunction polynomialSplineDerivative()
  {
    PolynomialFunction[] derivativePolynomials = new PolynomialFunction[n];
    for (int i = 0; i < n; i++) {
      derivativePolynomials[i] = polynomials[i].polynomialDerivative();
    }
    return new PolynomialSplineFunction(knots, derivativePolynomials);
  }
  





  public int getN()
  {
    return n;
  }
  







  public PolynomialFunction[] getPolynomials()
  {
    PolynomialFunction[] p = new PolynomialFunction[n];
    System.arraycopy(polynomials, 0, p, 0, n);
    return p;
  }
  







  public double[] getKnots()
  {
    double[] out = new double[n + 1];
    System.arraycopy(knots, 0, out, 0, n + 1);
    return out;
  }
  







  private static boolean isStrictlyIncreasing(double[] x)
  {
    for (int i = 1; i < x.length; i++) {
      if (x[(i - 1)] >= x[i]) {
        return false;
      }
    }
    return true;
  }
}
