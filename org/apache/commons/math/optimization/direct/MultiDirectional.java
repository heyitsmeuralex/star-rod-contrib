package org.apache.commons.math.optimization.direct;

import java.util.Comparator;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealConvergenceChecker;
import org.apache.commons.math.optimization.RealPointValuePair;






























public class MultiDirectional
  extends DirectSearchOptimizer
{
  private final double khi;
  private final double gamma;
  
  public MultiDirectional()
  {
    khi = 2.0D;
    gamma = 0.5D;
  }
  



  public MultiDirectional(double khi, double gamma)
  {
    this.khi = khi;
    this.gamma = gamma;
  }
  


  protected void iterateSimplex(Comparator<RealPointValuePair> comparator)
    throws FunctionEvaluationException, OptimizationException, IllegalArgumentException
  {
    RealConvergenceChecker checker = getConvergenceChecker();
    for (;;)
    {
      incrementIterationsCounter();
      

      RealPointValuePair[] original = simplex;
      RealPointValuePair best = original[0];
      

      RealPointValuePair reflected = evaluateNewSimplex(original, 1.0D, comparator);
      if (comparator.compare(reflected, best) < 0)
      {

        RealPointValuePair[] reflectedSimplex = simplex;
        RealPointValuePair expanded = evaluateNewSimplex(original, khi, comparator);
        if (comparator.compare(reflected, expanded) <= 0)
        {
          simplex = reflectedSimplex;
        }
        
        return;
      }
      


      RealPointValuePair contracted = evaluateNewSimplex(original, gamma, comparator);
      if (comparator.compare(contracted, best) < 0)
      {
        return;
      }
      

      int iter = getIterations();
      boolean converged = true;
      for (int i = 0; i < simplex.length; i++) {
        converged &= checker.converged(iter, original[i], simplex[i]);
      }
      if (converged) {
        return;
      }
    }
  }
  












  private RealPointValuePair evaluateNewSimplex(RealPointValuePair[] original, double coeff, Comparator<RealPointValuePair> comparator)
    throws FunctionEvaluationException, OptimizationException
  {
    double[] xSmallest = original[0].getPointRef();
    int n = xSmallest.length;
    

    simplex = new RealPointValuePair[n + 1];
    simplex[0] = original[0];
    for (int i = 1; i <= n; i++) {
      double[] xOriginal = original[i].getPointRef();
      double[] xTransformed = new double[n];
      for (int j = 0; j < n; j++) {
        xSmallest[j] += coeff * (xSmallest[j] - xOriginal[j]);
      }
      simplex[i] = new RealPointValuePair(xTransformed, NaN.0D, false);
    }
    

    evaluateSimplex(comparator);
    return simplex[0];
  }
}
