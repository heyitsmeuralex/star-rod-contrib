package org.apache.commons.math.distribution;

import java.io.Serializable;
import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.random.RandomDataImpl;
import org.apache.commons.math.util.FastMath;
































public abstract class AbstractContinuousDistribution
  extends AbstractDistribution
  implements ContinuousDistribution, Serializable
{
  private static final long serialVersionUID = -38038050983108802L;
  protected final RandomDataImpl randomData = new RandomDataImpl();
  




  private double solverAbsoluteAccuracy = 1.0E-6D;
  





  protected AbstractContinuousDistribution() {}
  





  public double density(double x)
    throws MathRuntimeException
  {
    throw new MathRuntimeException(new UnsupportedOperationException(), LocalizedFormats.NO_DENSITY_FOR_THIS_DISTRIBUTION, new Object[0]);
  }
  











  public double inverseCumulativeProbability(final double p)
    throws MathException
  {
    if ((p < 0.0D) || (p > 1.0D)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OUT_OF_RANGE_SIMPLE, new Object[] { Double.valueOf(p), Double.valueOf(0.0D), Double.valueOf(1.0D) });
    }
    



    UnivariateRealFunction rootFindingFunction = new UnivariateRealFunction()
    {
      public double value(double x) throws FunctionEvaluationException {
        double ret = NaN.0D;
        try {
          ret = cumulativeProbability(x) - p;
        } catch (MathException ex) {
          throw new FunctionEvaluationException(x, ex.getSpecificPattern(), new Object[] { ex.getGeneralPattern(), ex.getArguments() });
        }
        if (Double.isNaN(ret)) {
          throw new FunctionEvaluationException(x, LocalizedFormats.CUMULATIVE_PROBABILITY_RETURNED_NAN, new Object[] { Double.valueOf(x), Double.valueOf(p) });
        }
        return ret;
      }
      

    };
    double lowerBound = getDomainLowerBound(p);
    double upperBound = getDomainUpperBound(p);
    double[] bracket = null;
    try {
      bracket = UnivariateRealSolverUtils.bracket(rootFindingFunction, getInitialDomain(p), lowerBound, upperBound);



    }
    catch (ConvergenceException ex)
    {


      if (FastMath.abs(rootFindingFunction.value(lowerBound)) < getSolverAbsoluteAccuracy()) {
        return lowerBound;
      }
      if (FastMath.abs(rootFindingFunction.value(upperBound)) < getSolverAbsoluteAccuracy()) {
        return upperBound;
      }
      
      throw new MathException(ex);
    }
    

    double root = UnivariateRealSolverUtils.solve(rootFindingFunction, bracket[0], bracket[1], getSolverAbsoluteAccuracy());
    


    return root;
  }
  





  public void reseedRandomGenerator(long seed)
  {
    randomData.reSeed(seed);
  }
  







  public double sample()
    throws MathException
  {
    return randomData.nextInversionDeviate(this);
  }
  








  public double[] sample(int sampleSize)
    throws MathException
  {
    if (sampleSize <= 0) {
      MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_POSITIVE_SAMPLE_SIZE, new Object[] { Integer.valueOf(sampleSize) });
    }
    double[] out = new double[sampleSize];
    for (int i = 0; i < sampleSize; i++) {
      out[i] = sample();
    }
    return out;
  }
  








  protected abstract double getInitialDomain(double paramDouble);
  








  protected abstract double getDomainLowerBound(double paramDouble);
  








  protected abstract double getDomainUpperBound(double paramDouble);
  







  protected double getSolverAbsoluteAccuracy()
  {
    return solverAbsoluteAccuracy;
  }
}
