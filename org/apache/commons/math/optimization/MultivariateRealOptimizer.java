package org.apache.commons.math.optimization;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.MultivariateRealFunction;

public abstract interface MultivariateRealOptimizer
{
  public abstract void setMaxIterations(int paramInt);
  
  public abstract int getMaxIterations();
  
  public abstract void setMaxEvaluations(int paramInt);
  
  public abstract int getMaxEvaluations();
  
  public abstract int getIterations();
  
  public abstract int getEvaluations();
  
  public abstract void setConvergenceChecker(RealConvergenceChecker paramRealConvergenceChecker);
  
  public abstract RealConvergenceChecker getConvergenceChecker();
  
  public abstract RealPointValuePair optimize(MultivariateRealFunction paramMultivariateRealFunction, GoalType paramGoalType, double[] paramArrayOfDouble)
    throws FunctionEvaluationException, OptimizationException, IllegalArgumentException;
}
