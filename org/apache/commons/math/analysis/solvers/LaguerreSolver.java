package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.complex.Complex;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;







































public class LaguerreSolver
  extends UnivariateRealSolverImpl
{
  @Deprecated
  private final PolynomialFunction p;
  
  @Deprecated
  public LaguerreSolver(UnivariateRealFunction f)
    throws IllegalArgumentException
  {
    super(f, 100, 1.0E-6D);
    if ((f instanceof PolynomialFunction)) {
      p = ((PolynomialFunction)f);
    } else {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.FUNCTION_NOT_POLYNOMIAL, new Object[0]);
    }
  }
  



  @Deprecated
  public LaguerreSolver()
  {
    super(100, 1.0E-6D);
    p = null;
  }
  





  @Deprecated
  public PolynomialFunction getPolynomialFunction()
  {
    return new PolynomialFunction(p.getCoefficients());
  }
  
  @Deprecated
  public double solve(double min, double max)
    throws ConvergenceException, FunctionEvaluationException
  {
    return solve(p, min, max);
  }
  
  @Deprecated
  public double solve(double min, double max, double initial)
    throws ConvergenceException, FunctionEvaluationException
  {
    return solve(p, min, max, initial);
  }
  

















  public double solve(int maxEval, UnivariateRealFunction f, double min, double max, double initial)
    throws ConvergenceException, FunctionEvaluationException
  {
    setMaximalIterationCount(maxEval);
    return solve(f, min, max, initial);
  }
  


















  @Deprecated
  public double solve(UnivariateRealFunction f, double min, double max, double initial)
    throws ConvergenceException, FunctionEvaluationException
  {
    if (f.value(min) == 0.0D) {
      return min;
    }
    if (f.value(max) == 0.0D) {
      return max;
    }
    if (f.value(initial) == 0.0D) {
      return initial;
    }
    
    verifyBracketing(min, max, f);
    verifySequence(min, initial, max);
    if (isBracketing(min, initial, f)) {
      return solve(f, min, initial);
    }
    return solve(f, initial, max);
  }
  






















  public double solve(int maxEval, UnivariateRealFunction f, double min, double max)
    throws ConvergenceException, FunctionEvaluationException
  {
    setMaximalIterationCount(maxEval);
    return solve(f, min, max);
  }
  





















  @Deprecated
  public double solve(UnivariateRealFunction f, double min, double max)
    throws ConvergenceException, FunctionEvaluationException
  {
    if (!(f instanceof PolynomialFunction)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.FUNCTION_NOT_POLYNOMIAL, new Object[0]);
    }
    

    if (f.value(min) == 0.0D) return min;
    if (f.value(max) == 0.0D) return max;
    verifyBracketing(min, max, f);
    
    double[] coefficients = ((PolynomialFunction)f).getCoefficients();
    Complex[] c = new Complex[coefficients.length];
    for (int i = 0; i < coefficients.length; i++) {
      c[i] = new Complex(coefficients[i], 0.0D);
    }
    Complex initial = new Complex(0.5D * (min + max), 0.0D);
    Complex z = solve(c, initial);
    if (isRootOK(min, max, z)) {
      setResult(z.getReal(), iterationCount);
      return result;
    }
    

    Complex[] root = solveAll(c, initial);
    for (int i = 0; i < root.length; i++) {
      if (isRootOK(min, max, root[i])) {
        setResult(root[i].getReal(), iterationCount);
        return result;
      }
    }
    

    throw new ConvergenceException();
  }
  








  protected boolean isRootOK(double min, double max, Complex z)
  {
    double tolerance = FastMath.max(relativeAccuracy * z.abs(), absoluteAccuracy);
    return (isSequence(min, z.getReal(), max)) && ((FastMath.abs(z.getImaginary()) <= tolerance) || (z.abs() <= functionValueAccuracy));
  }
  















  @Deprecated
  public Complex[] solveAll(double[] coefficients, double initial)
    throws ConvergenceException, FunctionEvaluationException
  {
    Complex[] c = new Complex[coefficients.length];
    Complex z = new Complex(initial, 0.0D);
    for (int i = 0; i < c.length; i++) {
      c[i] = new Complex(coefficients[i], 0.0D);
    }
    return solveAll(c, z);
  }
  













  @Deprecated
  public Complex[] solveAll(Complex[] coefficients, Complex initial)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    int n = coefficients.length - 1;
    int iterationCount = 0;
    if (n < 1) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NON_POSITIVE_POLYNOMIAL_DEGREE, new Object[] { Integer.valueOf(n) });
    }
    
    Complex[] c = new Complex[n + 1];
    for (int i = 0; i <= n; i++) {
      c[i] = coefficients[i];
    }
    

    Complex[] root = new Complex[n];
    for (int i = 0; i < n; i++) {
      Complex[] subarray = new Complex[n - i + 1];
      System.arraycopy(c, 0, subarray, 0, subarray.length);
      root[i] = solve(subarray, initial);
      
      Complex newc = c[(n - i)];
      Complex oldc = null;
      for (int j = n - i - 1; j >= 0; j--) {
        oldc = c[j];
        c[j] = newc;
        newc = oldc.add(newc.multiply(root[i]));
      }
      iterationCount += this.iterationCount;
    }
    
    resultComputed = true;
    this.iterationCount = iterationCount;
    return root;
  }
  













  @Deprecated
  public Complex solve(Complex[] coefficients, Complex initial)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    int n = coefficients.length - 1;
    if (n < 1) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NON_POSITIVE_POLYNOMIAL_DEGREE, new Object[] { Integer.valueOf(n) });
    }
    
    Complex N = new Complex(n, 0.0D);
    Complex N1 = new Complex(n - 1, 0.0D);
    
    int i = 1;
    Complex pv = null;
    Complex dv = null;
    Complex d2v = null;
    Complex G = null;
    Complex G2 = null;
    Complex H = null;
    Complex delta = null;
    Complex denominator = null;
    Complex z = initial;
    Complex oldz = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    while (i <= maximalIterationCount)
    {

      pv = coefficients[n];
      dv = Complex.ZERO;
      d2v = Complex.ZERO;
      for (int j = n - 1; j >= 0; j--) {
        d2v = dv.add(z.multiply(d2v));
        dv = pv.add(z.multiply(dv));
        pv = coefficients[j].add(z.multiply(pv));
      }
      d2v = d2v.multiply(new Complex(2.0D, 0.0D));
      

      double tolerance = FastMath.max(relativeAccuracy * z.abs(), absoluteAccuracy);
      
      if (z.subtract(oldz).abs() <= tolerance) {
        resultComputed = true;
        iterationCount = i;
        return z;
      }
      if (pv.abs() <= functionValueAccuracy) {
        resultComputed = true;
        iterationCount = i;
        return z;
      }
      

      G = dv.divide(pv);
      G2 = G.multiply(G);
      H = G2.subtract(d2v.divide(pv));
      delta = N1.multiply(N.multiply(H).subtract(G2));
      
      Complex deltaSqrt = delta.sqrt();
      Complex dplus = G.add(deltaSqrt);
      Complex dminus = G.subtract(deltaSqrt);
      denominator = dplus.abs() > dminus.abs() ? dplus : dminus;
      

      if (denominator.equals(new Complex(0.0D, 0.0D))) {
        z = z.add(new Complex(absoluteAccuracy, absoluteAccuracy));
        oldz = new Complex(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
      }
      else {
        oldz = z;
        z = z.subtract(N.divide(denominator));
      }
      i++;
    }
    throw new MaxIterationsExceededException(maximalIterationCount);
  }
}
