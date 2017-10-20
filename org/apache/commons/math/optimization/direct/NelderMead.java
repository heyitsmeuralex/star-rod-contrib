package org.apache.commons.math.optimization.direct;

import java.util.Comparator;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;



































public class NelderMead
  extends DirectSearchOptimizer
{
  private final double rho;
  private final double khi;
  private final double gamma;
  private final double sigma;
  
  public NelderMead()
  {
    rho = 1.0D;
    khi = 2.0D;
    gamma = 0.5D;
    sigma = 0.5D;
  }
  






  public NelderMead(double rho, double khi, double gamma, double sigma)
  {
    this.rho = rho;
    this.khi = khi;
    this.gamma = gamma;
    this.sigma = sigma;
  }
  


  protected void iterateSimplex(Comparator<RealPointValuePair> comparator)
    throws FunctionEvaluationException, OptimizationException
  {
    incrementIterationsCounter();
    

    int n = simplex.length - 1;
    

    RealPointValuePair best = simplex[0];
    RealPointValuePair secondBest = simplex[(n - 1)];
    RealPointValuePair worst = simplex[n];
    double[] xWorst = worst.getPointRef();
    


    double[] centroid = new double[n];
    for (int i = 0; i < n; i++) {
      double[] x = simplex[i].getPointRef();
      for (int j = 0; j < n; j++) {
        centroid[j] += x[j];
      }
    }
    double scaling = 1.0D / n;
    for (int j = 0; j < n; j++) {
      centroid[j] *= scaling;
    }
    

    double[] xR = new double[n];
    for (int j = 0; j < n; j++) {
      centroid[j] += rho * (centroid[j] - xWorst[j]);
    }
    RealPointValuePair reflected = new RealPointValuePair(xR, evaluate(xR), false);
    
    if ((comparator.compare(best, reflected) <= 0) && (comparator.compare(reflected, secondBest) < 0))
    {


      replaceWorstPoint(reflected, comparator);
    }
    else if (comparator.compare(reflected, best) < 0)
    {

      double[] xE = new double[n];
      for (int j = 0; j < n; j++) {
        centroid[j] += khi * (xR[j] - centroid[j]);
      }
      RealPointValuePair expanded = new RealPointValuePair(xE, evaluate(xE), false);
      
      if (comparator.compare(expanded, reflected) < 0)
      {
        replaceWorstPoint(expanded, comparator);
      }
      else {
        replaceWorstPoint(reflected, comparator);
      }
    }
    else
    {
      if (comparator.compare(reflected, worst) < 0)
      {

        double[] xC = new double[n];
        for (int j = 0; j < n; j++) {
          centroid[j] += gamma * (xR[j] - centroid[j]);
        }
        RealPointValuePair outContracted = new RealPointValuePair(xC, evaluate(xC), false);
        
        if (comparator.compare(outContracted, reflected) <= 0)
        {
          replaceWorstPoint(outContracted, comparator);
          return;
        }
        
      }
      else
      {
        double[] xC = new double[n];
        for (int j = 0; j < n; j++) {
          centroid[j] -= gamma * (centroid[j] - xWorst[j]);
        }
        RealPointValuePair inContracted = new RealPointValuePair(xC, evaluate(xC), false);
        
        if (comparator.compare(inContracted, worst) < 0)
        {
          replaceWorstPoint(inContracted, comparator);
          return;
        }
      }
      


      double[] xSmallest = simplex[0].getPointRef();
      for (int i = 1; i < simplex.length; i++) {
        double[] x = simplex[i].getPoint();
        for (int j = 0; j < n; j++) {
          xSmallest[j] += sigma * (x[j] - xSmallest[j]);
        }
        simplex[i] = new RealPointValuePair(x, NaN.0D, false);
      }
      evaluateSimplex(comparator);
    }
  }
}
