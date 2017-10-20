package org.apache.commons.math.optimization.fitting;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.OptimizationException;



















































public class GaussianFitter
{
  private final CurveFitter fitter;
  
  public GaussianFitter(DifferentiableMultivariateVectorialOptimizer optimizer)
  {
    fitter = new CurveFitter(optimizer);
  }
  






  public void addObservedPoint(double x, double y)
  {
    addObservedPoint(1.0D, x, y);
  }
  







  public void addObservedPoint(double weight, double x, double y)
  {
    fitter.addObservedPoint(weight, x, y);
  }
  









  public GaussianFunction fit()
    throws FunctionEvaluationException, OptimizationException
  {
    return new GaussianFunction(fitter.fit(new ParametricGaussianFunction(), createParametersGuesser(fitter.getObservations()).guess()));
  }
  









  protected GaussianParametersGuesser createParametersGuesser(WeightedObservedPoint[] observations)
  {
    return new GaussianParametersGuesser(observations);
  }
}
