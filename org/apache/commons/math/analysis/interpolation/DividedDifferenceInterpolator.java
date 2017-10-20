package org.apache.commons.math.analysis.interpolation;

import java.io.Serializable;
import org.apache.commons.math.DuplicateSampleAbscissaException;
import org.apache.commons.math.analysis.polynomials.PolynomialFunctionLagrangeForm;
import org.apache.commons.math.analysis.polynomials.PolynomialFunctionNewtonForm;











































public class DividedDifferenceInterpolator
  implements UnivariateRealInterpolator, Serializable
{
  private static final long serialVersionUID = 107049519551235069L;
  
  public DividedDifferenceInterpolator() {}
  
  public PolynomialFunctionNewtonForm interpolate(double[] x, double[] y)
    throws DuplicateSampleAbscissaException
  {
    PolynomialFunctionLagrangeForm.verifyInterpolationArray(x, y);
    








    double[] c = new double[x.length - 1];
    System.arraycopy(x, 0, c, 0, c.length);
    
    double[] a = computeDividedDifference(x, y);
    return new PolynomialFunctionNewtonForm(a, c);
  }
  
















  protected static double[] computeDividedDifference(double[] x, double[] y)
    throws DuplicateSampleAbscissaException
  {
    PolynomialFunctionLagrangeForm.verifyInterpolationArray(x, y);
    
    double[] divdiff = (double[])y.clone();
    
    int n = x.length;
    double[] a = new double[n];
    a[0] = divdiff[0];
    for (int i = 1; i < n; i++) {
      for (int j = 0; j < n - i; j++) {
        double denominator = x[(j + i)] - x[j];
        if (denominator == 0.0D)
        {
          throw new DuplicateSampleAbscissaException(x[j], j, j + i);
        }
        divdiff[j] = ((divdiff[(j + 1)] - divdiff[j]) / denominator);
      }
      a[i] = divdiff[0];
    }
    
    return a;
  }
}
