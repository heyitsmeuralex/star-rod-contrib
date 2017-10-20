package org.apache.commons.math.transform;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;















































public class FastSineTransformer
  implements RealTransformer
{
  public FastSineTransformer() {}
  
  public double[] transform(double[] f)
    throws IllegalArgumentException
  {
    return fst(f);
  }
  
















  public double[] transform(UnivariateRealFunction f, double min, double max, int n)
    throws FunctionEvaluationException, IllegalArgumentException
  {
    double[] data = FastFourierTransformer.sample(f, min, max, n);
    data[0] = 0.0D;
    return fst(data);
  }
  









  public double[] transform2(double[] f)
    throws IllegalArgumentException
  {
    double scaling_coefficient = FastMath.sqrt(2.0D / f.length);
    return FastFourierTransformer.scaleArray(fst(f), scaling_coefficient);
  }
  
















  public double[] transform2(UnivariateRealFunction f, double min, double max, int n)
    throws FunctionEvaluationException, IllegalArgumentException
  {
    double[] data = FastFourierTransformer.sample(f, min, max, n);
    data[0] = 0.0D;
    double scaling_coefficient = FastMath.sqrt(2.0D / n);
    return FastFourierTransformer.scaleArray(fst(data), scaling_coefficient);
  }
  









  public double[] inversetransform(double[] f)
    throws IllegalArgumentException
  {
    double scaling_coefficient = 2.0D / f.length;
    return FastFourierTransformer.scaleArray(fst(f), scaling_coefficient);
  }
  














  public double[] inversetransform(UnivariateRealFunction f, double min, double max, int n)
    throws FunctionEvaluationException, IllegalArgumentException
  {
    double[] data = FastFourierTransformer.sample(f, min, max, n);
    data[0] = 0.0D;
    double scaling_coefficient = 2.0D / n;
    return FastFourierTransformer.scaleArray(fst(data), scaling_coefficient);
  }
  









  public double[] inversetransform2(double[] f)
    throws IllegalArgumentException
  {
    return transform2(f);
  }
  














  public double[] inversetransform2(UnivariateRealFunction f, double min, double max, int n)
    throws FunctionEvaluationException, IllegalArgumentException
  {
    return transform2(f, min, max, n);
  }
  






  protected double[] fst(double[] f)
    throws IllegalArgumentException
  {
    double[] transformed = new double[f.length];
    
    FastFourierTransformer.verifyDataSet(f);
    if (f[0] != 0.0D) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.FIRST_ELEMENT_NOT_ZERO, new Object[] { Double.valueOf(f[0]) });
    }
    

    int n = f.length;
    if (n == 1) {
      transformed[0] = 0.0D;
      return transformed;
    }
    

    double[] x = new double[n];
    x[0] = 0.0D;
    x[(n >> 1)] = (2.0D * f[(n >> 1)]);
    for (int i = 1; i < n >> 1; i++) {
      double a = FastMath.sin(i * 3.141592653589793D / n) * (f[i] + f[(n - i)]);
      double b = 0.5D * (f[i] - f[(n - i)]);
      x[i] = (a + b);
      x[(n - i)] = (a - b);
    }
    FastFourierTransformer transformer = new FastFourierTransformer();
    Complex[] y = transformer.transform(x);
    

    transformed[0] = 0.0D;
    transformed[1] = (0.5D * y[0].getReal());
    for (int i = 1; i < n >> 1; i++) {
      transformed[(2 * i)] = (-y[i].getImaginary());
      transformed[(2 * i + 1)] = (y[i].getReal() + transformed[(2 * i - 1)]);
    }
    
    return transformed;
  }
}
