package org.apache.commons.math.analysis.polynomials;

import org.apache.commons.math.DuplicateSampleAbscissaException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;























































public class PolynomialFunctionLagrangeForm
  implements UnivariateRealFunction
{
  private double[] coefficients;
  private final double[] x;
  private final double[] y;
  private boolean coefficientsComputed;
  
  public PolynomialFunctionLagrangeForm(double[] x, double[] y)
    throws IllegalArgumentException
  {
    verifyInterpolationArray(x, y);
    this.x = new double[x.length];
    this.y = new double[y.length];
    System.arraycopy(x, 0, this.x, 0, x.length);
    System.arraycopy(y, 0, this.y, 0, y.length);
    coefficientsComputed = false;
  }
  
  public double value(double z) throws FunctionEvaluationException
  {
    try {
      return evaluate(x, y, z);
    } catch (DuplicateSampleAbscissaException e) {
      throw new FunctionEvaluationException(z, e.getSpecificPattern(), new Object[] { e.getGeneralPattern(), e.getArguments() });
    }
  }
  




  public int degree()
  {
    return x.length - 1;
  }
  






  public double[] getInterpolatingPoints()
  {
    double[] out = new double[x.length];
    System.arraycopy(x, 0, out, 0, x.length);
    return out;
  }
  






  public double[] getInterpolatingValues()
  {
    double[] out = new double[y.length];
    System.arraycopy(y, 0, out, 0, y.length);
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
  















  public static double evaluate(double[] x, double[] y, double z)
    throws DuplicateSampleAbscissaException, IllegalArgumentException
  {
    verifyInterpolationArray(x, y);
    
    int nearest = 0;
    int n = x.length;
    double[] c = new double[n];
    double[] d = new double[n];
    double min_dist = Double.POSITIVE_INFINITY;
    for (int i = 0; i < n; i++)
    {
      c[i] = y[i];
      d[i] = y[i];
      
      double dist = FastMath.abs(z - x[i]);
      if (dist < min_dist) {
        nearest = i;
        min_dist = dist;
      }
    }
    

    double value = y[nearest];
    
    for (int i = 1; i < n; i++) {
      for (int j = 0; j < n - i; j++) {
        double tc = x[j] - z;
        double td = x[(i + j)] - z;
        double divider = x[j] - x[(i + j)];
        if (divider == 0.0D)
        {
          throw new DuplicateSampleAbscissaException(x[i], i, i + j);
        }
        
        double w = (c[(j + 1)] - d[j]) / divider;
        c[j] = (tc * w);
        d[j] = (td * w);
      }
      
      if (nearest < 0.5D * (n - i + 1)) {
        value += c[nearest];
      } else {
        nearest--;
        value += d[nearest];
      }
    }
    
    return value;
  }
  








  protected void computeCoefficients()
    throws ArithmeticException
  {
    int n = degree() + 1;
    coefficients = new double[n];
    for (int i = 0; i < n; i++) {
      coefficients[i] = 0.0D;
    }
    

    double[] c = new double[n + 1];
    c[0] = 1.0D;
    for (int i = 0; i < n; i++) {
      for (int j = i; j > 0; j--) {
        c[j] = (c[(j - 1)] - c[j] * x[i]);
      }
      c[0] *= -x[i];
      c[(i + 1)] = 1.0D;
    }
    
    double[] tc = new double[n];
    for (int i = 0; i < n; i++)
    {
      double d = 1.0D;
      for (int j = 0; j < n; j++) {
        if (i != j) {
          d *= (x[i] - x[j]);
        }
      }
      if (d == 0.0D)
      {
        for (int k = 0; k < n; k++) {
          if ((i != k) && (x[i] == x[k])) {
            throw MathRuntimeException.createArithmeticException(LocalizedFormats.IDENTICAL_ABSCISSAS_DIVISION_BY_ZERO, new Object[] { Integer.valueOf(i), Integer.valueOf(k), Double.valueOf(x[i]) });
          }
        }
      }
      

      double t = y[i] / d;
      


      tc[(n - 1)] = c[n];
      coefficients[(n - 1)] += t * tc[(n - 1)];
      for (int j = n - 2; j >= 0; j--) {
        tc[j] = (c[(j + 1)] + tc[(j + 1)] * x[i]);
        coefficients[j] += t * tc[j];
      }
    }
    
    coefficientsComputed = true;
  }
  

















  public static void verifyInterpolationArray(double[] x, double[] y)
    throws IllegalArgumentException
  {
    if (x.length != y.length) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(x.length), Integer.valueOf(y.length) });
    }
    

    if (x.length < 2) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.WRONG_NUMBER_OF_POINTS, new Object[] { Integer.valueOf(2), Integer.valueOf(x.length) });
    }
  }
}
