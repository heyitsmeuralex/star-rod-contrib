package org.apache.commons.math.optimization.fitting;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.util.FastMath;
































public class HarmonicFitter
{
  private final CurveFitter fitter;
  private double[] parameters;
  
  public HarmonicFitter(DifferentiableMultivariateVectorialOptimizer optimizer)
  {
    fitter = new CurveFitter(optimizer);
    parameters = null;
  }
  







  public HarmonicFitter(DifferentiableMultivariateVectorialOptimizer optimizer, double[] initialGuess)
  {
    fitter = new CurveFitter(optimizer);
    parameters = ((double[])initialGuess.clone());
  }
  





  public void addObservedPoint(double weight, double x, double y)
  {
    fitter.addObservedPoint(weight, x, y);
  }
  





  public HarmonicFunction fit()
    throws OptimizationException
  {
    if (parameters == null) {
      WeightedObservedPoint[] observations = fitter.getObservations();
      if (observations.length < 4) {
        throw new OptimizationException(LocalizedFormats.INSUFFICIENT_OBSERVED_POINTS_IN_SAMPLE, new Object[] { Integer.valueOf(observations.length), Integer.valueOf(4) });
      }
      

      HarmonicCoefficientsGuesser guesser = new HarmonicCoefficientsGuesser(observations);
      guesser.guess();
      parameters = new double[] { guesser.getGuessedAmplitude(), guesser.getGuessedPulsation(), guesser.getGuessedPhase() };
    }
    




    try
    {
      double[] fitted = fitter.fit(new ParametricHarmonicFunction(null), parameters);
      return new HarmonicFunction(fitted[0], fitted[1], fitted[2]);
    }
    catch (FunctionEvaluationException fee) {
      throw new RuntimeException(fee);
    }
  }
  
  private static class ParametricHarmonicFunction implements ParametricRealFunction
  {
    private ParametricHarmonicFunction() {}
    
    public double value(double x, double[] parameters)
    {
      double a = parameters[0];
      double omega = parameters[1];
      double phi = parameters[2];
      return a * FastMath.cos(omega * x + phi);
    }
    
    public double[] gradient(double x, double[] parameters)
    {
      double a = parameters[0];
      double omega = parameters[1];
      double phi = parameters[2];
      double alpha = omega * x + phi;
      double cosAlpha = FastMath.cos(alpha);
      double sinAlpha = FastMath.sin(alpha);
      return new double[] { cosAlpha, -a * x * sinAlpha, -a * sinAlpha };
    }
  }
}
