package org.apache.commons.math.optimization;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.DifferentiableMultivariateRealFunction;

public abstract interface DifferentiableMultivariateRealOptimizer
{
  public abstract void setMaxIterations(int paramInt);
  
  public abstract int getMaxIterations();
  
  public abstract int getIterations();
  
  public abstract void setMaxEvaluations(int paramInt);
  
  public abstract int getMaxEvaluations();
  
  public abstract int getEvaluations();
  
  public abstract int getGradientEvaluations();
  
  public abstract void setConvergenceChecker(RealConvergenceChecker paramRealConvergenceChecker);
  
  public abstract RealConvergenceChecker getConvergenceChecker();
  
  public abstract RealPointValuePair optimize(DifferentiableMultivariateRealFunction paramDifferentiableMultivariateRealFunction, GoalType paramGoalType, double[] paramArrayOfDouble)
    throws FunctionEvaluationException, OptimizationException, IllegalArgumentException;
}
