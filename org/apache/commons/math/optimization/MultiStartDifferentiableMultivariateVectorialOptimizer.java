package org.apache.commons.math.optimization;

import java.util.Arrays;
import java.util.Comparator;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.DifferentiableMultivariateVectorialFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.random.RandomVectorGenerator;



























































public class MultiStartDifferentiableMultivariateVectorialOptimizer
  implements DifferentiableMultivariateVectorialOptimizer
{
  private static final long serialVersionUID = 9206382258980561530L;
  private final DifferentiableMultivariateVectorialOptimizer optimizer;
  private int maxIterations;
  private int totalIterations;
  private int maxEvaluations;
  private int totalEvaluations;
  private int totalJacobianEvaluations;
  private int starts;
  private RandomVectorGenerator generator;
  private VectorialPointValuePair[] optima;
  
  public MultiStartDifferentiableMultivariateVectorialOptimizer(DifferentiableMultivariateVectorialOptimizer optimizer, int starts, RandomVectorGenerator generator)
  {
    this.optimizer = optimizer;
    totalIterations = 0;
    totalEvaluations = 0;
    totalJacobianEvaluations = 0;
    this.starts = starts;
    this.generator = generator;
    optima = null;
    setMaxIterations(Integer.MAX_VALUE);
    setMaxEvaluations(Integer.MAX_VALUE);
  }
  

























  public VectorialPointValuePair[] getOptima()
    throws IllegalStateException
  {
    if (optima == null) {
      throw MathRuntimeException.createIllegalStateException(LocalizedFormats.NO_OPTIMUM_COMPUTED_YET, new Object[0]);
    }
    return (VectorialPointValuePair[])optima.clone();
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
  
  public int getJacobianEvaluations()
  {
    return totalJacobianEvaluations;
  }
  
  public void setConvergenceChecker(VectorialConvergenceChecker checker)
  {
    optimizer.setConvergenceChecker(checker);
  }
  
  public VectorialConvergenceChecker getConvergenceChecker()
  {
    return optimizer.getConvergenceChecker();
  }
  



  public VectorialPointValuePair optimize(DifferentiableMultivariateVectorialFunction f, final double[] target, final double[] weights, double[] startPoint)
    throws FunctionEvaluationException, OptimizationException, IllegalArgumentException
  {
    optima = new VectorialPointValuePair[starts];
    totalIterations = 0;
    totalEvaluations = 0;
    totalJacobianEvaluations = 0;
    

    for (int i = 0; i < starts; i++)
    {
      try {
        optimizer.setMaxIterations(maxIterations - totalIterations);
        optimizer.setMaxEvaluations(maxEvaluations - totalEvaluations);
        optima[i] = optimizer.optimize(f, target, weights, i == 0 ? startPoint : generator.nextVector());
      }
      catch (FunctionEvaluationException fee) {
        optima[i] = null;
      } catch (OptimizationException oe) {
        optima[i] = null;
      }
      
      totalIterations += optimizer.getIterations();
      totalEvaluations += optimizer.getEvaluations();
      totalJacobianEvaluations += optimizer.getJacobianEvaluations();
    }
    


    Arrays.sort(optima, new Comparator() {
      public int compare(VectorialPointValuePair o1, VectorialPointValuePair o2) {
        if (o1 == null)
          return o2 == null ? 0 : 1;
        if (o2 == null) {
          return -1;
        }
        return Double.compare(weightedResidual(o1), weightedResidual(o2));
      }
      
      private double weightedResidual(VectorialPointValuePair pv) { double[] value = pv.getValueRef();
        double sum = 0.0D;
        for (int i = 0; i < value.length; i++) {
          double ri = value[i] - target[i];
          sum += weights[i] * ri * ri;
        }
        return sum;
      }
    });
    
    if (optima[0] == null) {
      throw new OptimizationException(LocalizedFormats.NO_CONVERGENCE_WITH_ANY_START_POINT, new Object[] { Integer.valueOf(starts) });
    }
    



    return optima[0];
  }
}
