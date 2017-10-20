package org.apache.commons.math.analysis.polynomials;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;


























































public class PolynomialFunctionNewtonForm
  implements UnivariateRealFunction
{
  private double[] coefficients;
  private final double[] c;
  private final double[] a;
  private boolean coefficientsComputed;
  
  public PolynomialFunctionNewtonForm(double[] a, double[] c)
    throws IllegalArgumentException
  {
    verifyInputArray(a, c);
    this.a = new double[a.length];
    this.c = new double[c.length];
    System.arraycopy(a, 0, this.a, 0, a.length);
    System.arraycopy(c, 0, this.c, 0, c.length);
    coefficientsComputed = false;
  }
  






  public double value(double z)
    throws FunctionEvaluationException
  {
    return evaluate(a, c, z);
  }
  




  public int degree()
  {
    return c.length;
  }
  






  public double[] getNewtonCoefficients()
  {
    double[] out = new double[a.length];
    System.arraycopy(a, 0, out, 0, a.length);
    return out;
  }
  






  public double[] getCenters()
  {
    double[] out = new double[c.length];
    System.arraycopy(c, 0, out, 0, c.length);
    return out;
  }
  






  public double[] getCoefficients()
  {
    if (!coefficientsComputed) {
      computeCoefficients();
    }
    double[] out = new double[coefficients.length];
    System.arraycopy(coefficients, 0, out, 0, coefficients.length);
    return out;
  }
  












  public static double evaluate(double[] a, double[] c, double z)
    throws FunctionEvaluationException, IllegalArgumentException
  {
    verifyInputArray(a, c);
    
    int n = c.length;
    double value = a[n];
    for (int i = n - 1; i >= 0; i--) {
      value = a[i] + (z - c[i]) * value;
    }
    
    return value;
  }
  



  protected void computeCoefficients()
  {
    int n = degree();
    
    coefficients = new double[n + 1];
    for (int i = 0; i <= n; i++) {
      coefficients[i] = 0.0D;
    }
    
    coefficients[0] = a[n];
    for (int i = n - 1; i >= 0; i--) {
      for (int j = n - i; j > 0; j--) {
        coefficients[j] = (coefficients[(j - 1)] - c[i] * coefficients[j]);
      }
      coefficients[0] = (a[i] - c[i] * coefficients[0]);
    }
    
    coefficientsComputed = true;
  }
  












  protected static void verifyInputArray(double[] a, double[] c)
    throws IllegalArgumentException
  {
    if ((a.length < 1) || (c.length < 1)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.EMPTY_POLYNOMIALS_COEFFICIENTS_ARRAY, new Object[0]);
    }
    
    if (a.length != c.length + 1) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.ARRAY_SIZES_SHOULD_HAVE_DIFFERENCE_1, new Object[] { Integer.valueOf(a.length), Integer.valueOf(c.length) });
    }
  }
}
