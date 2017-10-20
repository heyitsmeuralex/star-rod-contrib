package org.apache.commons.math.optimization.fitting;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction;
import org.apache.commons.math.analysis.MultivariateMatrixFunction;
import org.apache.commons.math.optimization.DifferentiableMultivariateVectorialOptimizer;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.VectorialPointValuePair;





































public class CurveFitter
{
  private final DifferentiableMultivariateVectorialOptimizer optimizer;
  private final List<WeightedObservedPoint> observations;
  
  public CurveFitter(DifferentiableMultivariateVectorialOptimizer optimizer)
  {
    this.optimizer = optimizer;
    observations = new ArrayList();
  }
  









  public void addObservedPoint(double x, double y)
  {
    addObservedPoint(1.0D, x, y);
  }
  








  public void addObservedPoint(double weight, double x, double y)
  {
    observations.add(new WeightedObservedPoint(weight, x, y));
  }
  





  public void addObservedPoint(WeightedObservedPoint observed)
  {
    observations.add(observed);
  }
  





  public WeightedObservedPoint[] getObservations()
  {
    return (WeightedObservedPoint[])observations.toArray(new WeightedObservedPoint[observations.size()]);
  }
  


  public void clearObservations()
  {
    observations.clear();
  }
  














  public double[] fit(ParametricRealFunction f, double[] initialGuess)
    throws FunctionEvaluationException, OptimizationException, IllegalArgumentException
  {
    double[] target = new double[observations.size()];
    double[] weights = new double[observations.size()];
    int i = 0;
    for (WeightedObservedPoint point : observations) {
      target[i] = point.getY();
      weights[i] = point.getWeight();
      i++;
    }
    

    VectorialPointValuePair optimum = optimizer.optimize(new TheoreticalValuesFunction(f), target, weights, initialGuess);
    


    return optimum.getPointRef();
  }
  



  private class TheoreticalValuesFunction
    implements DifferentiableMultivariateVectorialFunction
  {
    private final ParametricRealFunction f;
    


    public TheoreticalValuesFunction(ParametricRealFunction f)
    {
      this.f = f;
    }
    
    public MultivariateMatrixFunction jacobian()
    {
      new MultivariateMatrixFunction()
      {
        public double[][] value(double[] point) throws FunctionEvaluationException, IllegalArgumentException
        {
          double[][] jacobian = new double[observations.size()][];
          
          int i = 0;
          for (WeightedObservedPoint observed : observations) {
            jacobian[(i++)] = f.gradient(observed.getX(), point);
          }
          
          return jacobian;
        }
      };
    }
    


    public double[] value(double[] point)
      throws FunctionEvaluationException, IllegalArgumentException
    {
      double[] values = new double[observations.size()];
      int i = 0;
      for (WeightedObservedPoint observed : observations) {
        values[(i++)] = f.value(observed.getX(), point);
      }
      
      return values;
    }
  }
}
