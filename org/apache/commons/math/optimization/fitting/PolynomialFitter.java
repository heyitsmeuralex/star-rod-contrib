package org.apache.commons.math.optimization.fitting;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.OptimizationException;


































public class PolynomialFitter
{
  private final CurveFitter fitter;
  private final int degree;
  
  public PolynomialFitter(int degree, DifferentiableMultivariateVectorialOptimizer optimizer)
  {
    fitter = new CurveFitter(optimizer);
    this.degree = degree;
  }
  





  public void addObservedPoint(double weight, double x, double y)
  {
    fitter.addObservedPoint(weight, x, y);
  }
  



  public void clearObservations()
  {
    fitter.clearObservations();
  }
  

  public PolynomialFunction fit()
    throws OptimizationException
  {
    try
    {
      return new PolynomialFunction(fitter.fit(new ParametricPolynomial(null), new double[degree + 1]));
    }
    catch (FunctionEvaluationException fee) {
      throw new RuntimeException(fee);
    }
  }
  
  private static class ParametricPolynomial implements ParametricRealFunction
  {
    private ParametricPolynomial() {}
    
    public double[] gradient(double x, double[] parameters) {
      double[] gradient = new double[parameters.length];
      double xn = 1.0D;
      for (int i = 0; i < parameters.length; i++) {
        gradient[i] = xn;
        xn *= x;
      }
      return gradient;
    }
    
    public double value(double x, double[] parameters)
    {
      double y = 0.0D;
      for (int i = parameters.length - 1; i >= 0; i--) {
        y = y * x + parameters[i];
      }
      return y;
    }
  }
}
