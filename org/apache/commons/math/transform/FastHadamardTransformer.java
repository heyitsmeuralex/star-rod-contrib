package org.apache.commons.math.transform;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;

























public class FastHadamardTransformer
  implements RealTransformer
{
  public FastHadamardTransformer() {}
  
  public double[] transform(double[] f)
    throws IllegalArgumentException
  {
    return fht(f);
  }
  

  public double[] transform(UnivariateRealFunction f, double min, double max, int n)
    throws FunctionEvaluationException, IllegalArgumentException
  {
    return fht(FastFourierTransformer.sample(f, min, max, n));
  }
  
  public double[] inversetransform(double[] f)
    throws IllegalArgumentException
  {
    return FastFourierTransformer.scaleArray(fht(f), 1.0D / f.length);
  }
  

  public double[] inversetransform(UnivariateRealFunction f, double min, double max, int n)
    throws FunctionEvaluationException, IllegalArgumentException
  {
    double[] unscaled = fht(FastFourierTransformer.sample(f, min, max, n));
    
    return FastFourierTransformer.scaleArray(unscaled, 1.0D / n);
  }
  







  public int[] transform(int[] f)
    throws IllegalArgumentException
  {
    return fht(f);
  }
  













































































  protected double[] fht(double[] x)
    throws IllegalArgumentException
  {
    int n = x.length;
    int halfN = n / 2;
    

    if (!FastFourierTransformer.isPowerOf2(n)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO, new Object[] { Integer.valueOf(n) });
    }
    




    double[] yPrevious = new double[n];
    double[] yCurrent = (double[])x.clone();
    

    for (int j = 1; j < n; j <<= 1)
    {

      double[] yTmp = yCurrent;
      yCurrent = yPrevious;
      yPrevious = yTmp;
      

      for (int i = 0; i < halfN; i++)
      {

        int twoI = 2 * i;
        yPrevious[twoI] += yPrevious[(twoI + 1)];
      }
      for (int i = halfN; i < n; i++)
      {

        int twoI = 2 * i;
        yCurrent[i] = (yPrevious[(twoI - n)] - yPrevious[(twoI - n + 1)]);
      }
    }
    

    return yCurrent;
  }
  






  protected int[] fht(int[] x)
    throws IllegalArgumentException
  {
    int n = x.length;
    int halfN = n / 2;
    

    if (!FastFourierTransformer.isPowerOf2(n)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO, new Object[] { Integer.valueOf(n) });
    }
    




    int[] yPrevious = new int[n];
    int[] yCurrent = (int[])x.clone();
    

    for (int j = 1; j < n; j <<= 1)
    {

      int[] yTmp = yCurrent;
      yCurrent = yPrevious;
      yPrevious = yTmp;
      

      for (int i = 0; i < halfN; i++)
      {

        int twoI = 2 * i;
        yPrevious[twoI] += yPrevious[(twoI + 1)];
      }
      for (int i = halfN; i < n; i++)
      {

        int twoI = 2 * i;
        yCurrent[i] = (yPrevious[(twoI - n)] - yPrevious[(twoI - n + 1)]);
      }
    }
    

    return yCurrent;
  }
}
