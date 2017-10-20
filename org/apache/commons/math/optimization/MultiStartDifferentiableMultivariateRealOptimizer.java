package org.apache.commons.math.optimization;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.random.RandomVectorGenerator;
























































public class MultiStartDifferentiableMultivariateRealOptimizer
  implements DifferentiableMultivariateRealOptimizer
{
  private final DifferentiableMultivariateRealOptimizer optimizer;
  private int maxIterations;
  private int totalIterations;
  private int maxEvaluations;
  private int totalEvaluations;
  private int totalGradientEvaluations;
  private int starts;
  private RandomVectorGenerator generator;
  private RealPointValuePair[] optima;
  
  public MultiStartDifferentiableMultivariateRealOptimizer(DifferentiableMultivariateRealOptimizer optimizer, int starts, RandomVectorGenerator generator)
  {
    this.optimizer = optimizer;
    totalIterations = 0;
    totalEvaluations = 0;
    totalGradientEvaluations = 0;
    this.starts = starts;
    this.generator = generator;
    optima = null;
    setMaxIterations(Integer.MAX_VALUE);
    setMaxEvaluations(Integer.MAX_VALUE);
  }
  

























  public RealPointValuePair[] getOptima()
    throws IllegalStateException
  {
    if (optima == null) {
      throw MathRuntimeException.createIllegalStateException(LocalizedFormats.NO_OPTIMUM_COMPUTED_YET, new Object[0]);
    }
    return (RealPointValuePair[])optima.clone();
  }
  
  public void setMaxIterations(int maxIterations)
  {
    this.maxIterations = maxIterations;
  }
  
  public int getMaxIterations()
  {
    return maxIterations;
  }
  
  public int getIterations()
  {
    return totalIterations;
  }
  
  public void setMaxEvaluations(int maxEvaluations)
  {
    this.maxEvaluations = maxEvaluations;
  }
  
  public int getMaxEvaluations()
  {
    return maxEvaluations;
  }
  
  public int getEvaluations()
  {
    return totalEvaluations;
  }
  
  public int getGradientEvaluations()
  {
    return totalGradientEvaluations;
  }
  
  public void setConvergenceChecker(RealConvergenceChecker checker)
  {
    optimizer.setConvergenceChecker(checker);
  }
  
  public RealConvergenceChecker getConvergenceChecker()
  {
    return optimizer.getConvergenceChecker();
  }
  



  public RealPointValuePair optimize(DifferentiableMultivariateRealFunction f, final GoalType goalType, double[] startPoint)
    throws FunctionEvaluationException, OptimizationException, FunctionEvaluationException
  {
    optima = new RealPointValuePair[starts];
    totalIterations = 0;
    totalEvaluations = 0;
    totalGradientEvaluations = 0;
    

    for (int i = 0; i < starts; i++)
    {
      try {
        optimizer.setMaxIterations(maxIterations - totalIterations);
        optimizer.setMaxEvaluations(maxEvaluations - totalEvaluations);
        optima[i] = optimizer.optimize(f, goalType, i == 0 ? startPoint : generator.nextVector());
      }
      catch (FunctionEvaluationException fee) {
        optima[i] = null;
      } catch (OptimizationException oe) {
        optima[i] = null;
      }
      
      totalIterations += optimizer.getIterations();
      totalEvaluations += optimizer.getEvaluations();
      totalGradientEvaluations += optimizer.getGradientEvaluations();
    }
    


    Arrays.sort(optima, new Comparator() {
      public int compare(RealPointValuePair o1, RealPointValuePair o2) {
        if (o1 == null)
          return o2 == null ? 0 : 1;
        if (o2 == null) {
          return -1;
        }
        double v1 = o1.getValue();
        double v2 = o2.getValue();
        return goalType == GoalType.MINIMIZE ? Double.compare(v1, v2) : Double.compare(v2, v1);
      }
    });
    

    if (optima[0] == null) {
      throw new OptimizationException(LocalizedFormats.NO_CONVERGENCE_WITH_ANY_START_POINT, new Object[] { Integer.valueOf(starts) });
    }
    



    return optima[0];
  }
}
