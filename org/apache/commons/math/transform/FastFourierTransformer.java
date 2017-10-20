package org.apache.commons.math.transform;

import java.io.Serializable;
import java.lang.reflect.Array;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;







































public class FastFourierTransformer
  implements Serializable
{
  static final long serialVersionUID = 5138259215438106000L;
  private RootsOfUnity roots = new RootsOfUnity();
  







  public FastFourierTransformer() {}
  







  public Complex[] transform(double[] f)
    throws IllegalArgumentException
  {
    return fft(f, false);
  }
  















  public Complex[] transform(UnivariateRealFunction f, double min, double max, int n)
    throws FunctionEvaluationException, IllegalArgumentException
  {
    double[] data = sample(f, min, max, n);
    return fft(data, false);
  }
  









  public Complex[] transform(Complex[] f)
    throws IllegalArgumentException
  {
    roots.computeOmega(f.length);
    return fft(f);
  }
  










  public Complex[] transform2(double[] f)
    throws IllegalArgumentException
  {
    double scaling_coefficient = 1.0D / FastMath.sqrt(f.length);
    return scaleArray(fft(f, false), scaling_coefficient);
  }
  
















  public Complex[] transform2(UnivariateRealFunction f, double min, double max, int n)
    throws FunctionEvaluationException, IllegalArgumentException
  {
    double[] data = sample(f, min, max, n);
    double scaling_coefficient = 1.0D / FastMath.sqrt(n);
    return scaleArray(fft(data, false), scaling_coefficient);
  }
  










  public Complex[] transform2(Complex[] f)
    throws IllegalArgumentException
  {
    roots.computeOmega(f.length);
    double scaling_coefficient = 1.0D / FastMath.sqrt(f.length);
    return scaleArray(fft(f), scaling_coefficient);
  }
  










  public Complex[] inversetransform(double[] f)
    throws IllegalArgumentException
  {
    double scaling_coefficient = 1.0D / f.length;
    return scaleArray(fft(f, true), scaling_coefficient);
  }
  
















  public Complex[] inversetransform(UnivariateRealFunction f, double min, double max, int n)
    throws FunctionEvaluationException, IllegalArgumentException
  {
    double[] data = sample(f, min, max, n);
    double scaling_coefficient = 1.0D / n;
    return scaleArray(fft(data, true), scaling_coefficient);
  }
  










  public Complex[] inversetransform(Complex[] f)
    throws IllegalArgumentException
  {
    roots.computeOmega(-f.length);
    double scaling_coefficient = 1.0D / f.length;
    return scaleArray(fft(f), scaling_coefficient);
  }
  










  public Complex[] inversetransform2(double[] f)
    throws IllegalArgumentException
  {
    double scaling_coefficient = 1.0D / FastMath.sqrt(f.length);
    return scaleArray(fft(f, true), scaling_coefficient);
  }
  
















  public Complex[] inversetransform2(UnivariateRealFunction f, double min, double max, int n)
    throws FunctionEvaluationException, IllegalArgumentException
  {
    double[] data = sample(f, min, max, n);
    double scaling_coefficient = 1.0D / FastMath.sqrt(n);
    return scaleArray(fft(data, true), scaling_coefficient);
  }
  










  public Complex[] inversetransform2(Complex[] f)
    throws IllegalArgumentException
  {
    roots.computeOmega(-f.length);
    double scaling_coefficient = 1.0D / FastMath.sqrt(f.length);
    return scaleArray(fft(f), scaling_coefficient);
  }
  








  protected Complex[] fft(double[] f, boolean isInverse)
    throws IllegalArgumentException
  {
    verifyDataSet(f);
    Complex[] F = new Complex[f.length];
    if (f.length == 1) {
      F[0] = new Complex(f[0], 0.0D);
      return F;
    }
    


    int N = f.length >> 1;
    Complex[] c = new Complex[N];
    for (int i = 0; i < N; i++) {
      c[i] = new Complex(f[(2 * i)], f[(2 * i + 1)]);
    }
    roots.computeOmega(isInverse ? -N : N);
    Complex[] z = fft(c);
    

    roots.computeOmega(isInverse ? -2 * N : 2 * N);
    F[0] = new Complex(2.0D * (z[0].getReal() + z[0].getImaginary()), 0.0D);
    F[N] = new Complex(2.0D * (z[0].getReal() - z[0].getImaginary()), 0.0D);
    for (int i = 1; i < N; i++) {
      Complex A = z[(N - i)].conjugate();
      Complex B = z[i].add(A);
      Complex C = z[i].subtract(A);
      
      Complex D = new Complex(-roots.getOmegaImaginary(i), roots.getOmegaReal(i));
      
      F[i] = B.subtract(C.multiply(D));
      F[(2 * N - i)] = F[i].conjugate();
    }
    
    return scaleArray(F, 0.5D);
  }
  







  protected Complex[] fft(Complex[] data)
    throws IllegalArgumentException
  {
    int n = data.length;
    Complex[] f = new Complex[n];
    

    verifyDataSet(data);
    if (n == 1) {
      f[0] = data[0];
      return f;
    }
    if (n == 2) {
      f[0] = data[0].add(data[1]);
      f[1] = data[0].subtract(data[1]);
      return f;
    }
    

    int ii = 0;
    for (int i = 0; i < n; i++) {
      f[i] = data[ii];
      int k = n >> 1;
      for (; (ii >= k) && (k > 0); 
          k >>= 1) { ii -= k;
      }
      ii += k;
    }
    

    for (int i = 0; i < n; i += 4) {
      Complex a = f[i].add(f[(i + 1)]);
      Complex b = f[(i + 2)].add(f[(i + 3)]);
      Complex c = f[i].subtract(f[(i + 1)]);
      Complex d = f[(i + 2)].subtract(f[(i + 3)]);
      Complex e1 = c.add(d.multiply(Complex.I));
      Complex e2 = c.subtract(d.multiply(Complex.I));
      f[i] = a.add(b);
      f[(i + 2)] = a.subtract(b);
      
      f[(i + 1)] = (roots.isForward() ? e2 : e1);
      f[(i + 3)] = (roots.isForward() ? e1 : e2);
    }
    

    for (int i = 4; i < n; i <<= 1) {
      int m = n / (i << 1);
      for (int j = 0; j < n; j += (i << 1)) {
        for (int k = 0; k < i; k++)
        {
          int k_times_m = k * m;
          double omega_k_times_m_real = roots.getOmegaReal(k_times_m);
          double omega_k_times_m_imaginary = roots.getOmegaImaginary(k_times_m);
          
          Complex z = new Complex(f[(i + j + k)].getReal() * omega_k_times_m_real - f[(i + j + k)].getImaginary() * omega_k_times_m_imaginary, f[(i + j + k)].getReal() * omega_k_times_m_imaginary + f[(i + j + k)].getImaginary() * omega_k_times_m_real);
          




          f[(i + j + k)] = f[(j + k)].subtract(z);
          f[(j + k)] = f[(j + k)].add(z);
        }
      }
    }
    return f;
  }
  
















  public static double[] sample(UnivariateRealFunction f, double min, double max, int n)
    throws FunctionEvaluationException, IllegalArgumentException
  {
    if (n <= 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_NUMBER_OF_SAMPLES, new Object[] { Integer.valueOf(n) });
    }
    

    verifyInterval(min, max);
    
    double[] s = new double[n];
    double h = (max - min) / n;
    for (int i = 0; i < n; i++) {
      s[i] = f.value(min + i * h);
    }
    return s;
  }
  







  public static double[] scaleArray(double[] f, double d)
  {
    for (int i = 0; i < f.length; i++) {
      f[i] *= d;
    }
    return f;
  }
  







  public static Complex[] scaleArray(Complex[] f, double d)
  {
    for (int i = 0; i < f.length; i++) {
      f[i] = new Complex(d * f[i].getReal(), d * f[i].getImaginary());
    }
    return f;
  }
  





  public static boolean isPowerOf2(long n)
  {
    return (n > 0L) && ((n & n - 1L) == 0L);
  }
  




  public static void verifyDataSet(double[] d)
    throws IllegalArgumentException
  {
    if (!isPowerOf2(d.length)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO_CONSIDER_PADDING, new Object[] { Integer.valueOf(d.length) });
    }
  }
  





  public static void verifyDataSet(Object[] o)
    throws IllegalArgumentException
  {
    if (!isPowerOf2(o.length)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POWER_OF_TWO_CONSIDER_PADDING, new Object[] { Integer.valueOf(o.length) });
    }
  }
  








  public static void verifyInterval(double lower, double upper)
    throws IllegalArgumentException
  {
    if (lower >= upper) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.ENDPOINTS_NOT_AN_INTERVAL, new Object[] { Double.valueOf(lower), Double.valueOf(upper) });
    }
  }
  















  public Object mdfft(Object mdca, boolean forward)
    throws IllegalArgumentException
  {
    MultiDimensionalComplexMatrix mdcm = (MultiDimensionalComplexMatrix)new MultiDimensionalComplexMatrix(mdca).clone();
    
    int[] dimensionSize = mdcm.getDimensionSizes();
    
    for (int i = 0; i < dimensionSize.length; i++) {
      mdfft(mdcm, forward, i, new int[0]);
    }
    return mdcm.getArray();
  }
  









  private void mdfft(MultiDimensionalComplexMatrix mdcm, boolean forward, int d, int[] subVector)
    throws IllegalArgumentException
  {
    int[] dimensionSize = mdcm.getDimensionSizes();
    
    if (subVector.length == dimensionSize.length) {
      Complex[] temp = new Complex[dimensionSize[d]];
      for (int i = 0; i < dimensionSize[d]; i++)
      {
        subVector[d] = i;
        temp[i] = mdcm.get(subVector);
      }
      
      if (forward) {
        temp = transform2(temp);
      } else {
        temp = inversetransform2(temp);
      }
      for (int i = 0; i < dimensionSize[d]; i++) {
        subVector[d] = i;
        mdcm.set(temp[i], subVector);
      }
    } else {
      int[] vector = new int[subVector.length + 1];
      System.arraycopy(subVector, 0, vector, 0, subVector.length);
      if (subVector.length == d)
      {

        vector[d] = 0;
        mdfft(mdcm, forward, d, vector);
      } else {
        for (int i = 0; i < dimensionSize[subVector.length]; i++) {
          vector[subVector.length] = i;
          
          mdfft(mdcm, forward, d, vector);
        }
      }
    }
  }
  





  private static class MultiDimensionalComplexMatrix
    implements Cloneable
  {
    protected int[] dimensionSize;
    




    protected Object multiDimensionalComplexArray;
    




    public MultiDimensionalComplexMatrix(Object multiDimensionalComplexArray)
    {
      this.multiDimensionalComplexArray = multiDimensionalComplexArray;
      

      int numOfDimensions = 0;
      Object lastDimension = multiDimensionalComplexArray;
      while ((lastDimension instanceof Object[])) {
        Object[] array = (Object[])lastDimension;
        numOfDimensions++;
        lastDimension = array[0];
      }
      

      dimensionSize = new int[numOfDimensions];
      

      numOfDimensions = 0;
      Object lastDimension = multiDimensionalComplexArray;
      while ((lastDimension instanceof Object[])) {
        Object[] array = (Object[])lastDimension;
        dimensionSize[(numOfDimensions++)] = array.length;
        lastDimension = array[0];
      }
    }
    






    public Complex get(int... vector)
      throws IllegalArgumentException
    {
      if (vector == null) {
        if (dimensionSize.length > 0) {
          throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(0), Integer.valueOf(dimensionSize.length) });
        }
        
        return null;
      }
      if (vector.length != dimensionSize.length) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(vector.length), Integer.valueOf(dimensionSize.length) });
      }
      

      Object lastDimension = multiDimensionalComplexArray;
      
      for (int i = 0; i < dimensionSize.length; i++) {
        lastDimension = ((Object[])(Object[])lastDimension)[vector[i]];
      }
      return (Complex)lastDimension;
    }
    






    public Complex set(Complex magnitude, int... vector)
      throws IllegalArgumentException
    {
      if (vector == null) {
        if (dimensionSize.length > 0) {
          throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(0), Integer.valueOf(dimensionSize.length) });
        }
        
        return null;
      }
      if (vector.length != dimensionSize.length) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(vector.length), Integer.valueOf(dimensionSize.length) });
      }
      

      Object[] lastDimension = (Object[])multiDimensionalComplexArray;
      for (int i = 0; i < dimensionSize.length - 1; i++) {
        lastDimension = (Object[])lastDimension[vector[i]];
      }
      
      Complex lastValue = (Complex)lastDimension[vector[(dimensionSize.length - 1)]];
      lastDimension[vector[(dimensionSize.length - 1)]] = magnitude;
      
      return lastValue;
    }
    



    public int[] getDimensionSizes()
    {
      return (int[])dimensionSize.clone();
    }
    



    public Object getArray()
    {
      return multiDimensionalComplexArray;
    }
    

    public Object clone()
    {
      MultiDimensionalComplexMatrix mdcm = new MultiDimensionalComplexMatrix(Array.newInstance(Complex.class, dimensionSize));
      

      clone(mdcm);
      return mdcm;
    }
    



    private void clone(MultiDimensionalComplexMatrix mdcm)
    {
      int[] vector = new int[dimensionSize.length];
      int size = 1;
      for (int i = 0; i < dimensionSize.length; i++) {
        size *= dimensionSize[i];
      }
      int[][] vectorList = new int[size][dimensionSize.length];
      for (int[] nextVector : vectorList) {
        System.arraycopy(vector, 0, nextVector, 0, dimensionSize.length);
        
        for (int i = 0; i < dimensionSize.length; i++) {
          vector[i] += 1;
          if (vector[i] < dimensionSize[i]) {
            break;
          }
          vector[i] = 0;
        }
      }
      

      for (int[] nextVector : vectorList) {
        mdcm.set(get(nextVector), nextVector);
      }
    }
  }
  



  private static class RootsOfUnity
    implements Serializable
  {
    private static final long serialVersionUID = 6404784357747329667L;
    


    private int omegaCount;
    


    private double[] omegaReal;
    

    private double[] omegaImaginaryForward;
    

    private double[] omegaImaginaryInverse;
    

    private boolean isForward;
    


    public RootsOfUnity()
    {
      omegaCount = 0;
      omegaReal = null;
      omegaImaginaryForward = null;
      omegaImaginaryInverse = null;
      isForward = true;
    }
    





    public synchronized boolean isForward()
      throws IllegalStateException
    {
      if (omegaCount == 0) {
        throw MathRuntimeException.createIllegalStateException(LocalizedFormats.ROOTS_OF_UNITY_NOT_COMPUTED_YET, new Object[0]);
      }
      return isForward;
    }
    









    public synchronized void computeOmega(int n)
      throws IllegalArgumentException
    {
      if (n == 0) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.CANNOT_COMPUTE_0TH_ROOT_OF_UNITY, new Object[0]);
      }
      

      isForward = (n > 0);
      

      int absN = FastMath.abs(n);
      
      if (absN == omegaCount) {
        return;
      }
      

      double t = 6.283185307179586D / absN;
      double cosT = FastMath.cos(t);
      double sinT = FastMath.sin(t);
      omegaReal = new double[absN];
      omegaImaginaryForward = new double[absN];
      omegaImaginaryInverse = new double[absN];
      omegaReal[0] = 1.0D;
      omegaImaginaryForward[0] = 0.0D;
      omegaImaginaryInverse[0] = 0.0D;
      for (int i = 1; i < absN; i++) {
        omegaReal[i] = (omegaReal[(i - 1)] * cosT + omegaImaginaryForward[(i - 1)] * sinT);
        
        omegaImaginaryForward[i] = (omegaImaginaryForward[(i - 1)] * cosT - omegaReal[(i - 1)] * sinT);
        
        omegaImaginaryInverse[i] = (-omegaImaginaryForward[i]);
      }
      omegaCount = absN;
    }
    








    public synchronized double getOmegaReal(int k)
      throws IllegalStateException, IllegalArgumentException
    {
      if (omegaCount == 0) {
        throw MathRuntimeException.createIllegalStateException(LocalizedFormats.ROOTS_OF_UNITY_NOT_COMPUTED_YET, new Object[0]);
      }
      if ((k < 0) || (k >= omegaCount)) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OUT_OF_RANGE_ROOT_OF_UNITY_INDEX, new Object[] { Integer.valueOf(k), Integer.valueOf(0), Integer.valueOf(omegaCount - 1) });
      }
      

      return omegaReal[k];
    }
    








    public synchronized double getOmegaImaginary(int k)
      throws IllegalStateException, IllegalArgumentException
    {
      if (omegaCount == 0) {
        throw MathRuntimeException.createIllegalStateException(LocalizedFormats.ROOTS_OF_UNITY_NOT_COMPUTED_YET, new Object[0]);
      }
      if ((k < 0) || (k >= omegaCount)) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OUT_OF_RANGE_ROOT_OF_UNITY_INDEX, new Object[] { Integer.valueOf(k), Integer.valueOf(0), Integer.valueOf(omegaCount - 1) });
      }
      

      return isForward ? omegaImaginaryForward[k] : omegaImaginaryInverse[k];
    }
  }
}
