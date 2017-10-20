package org.apache.commons.math.optimization;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.ConvergingAlgorithm;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

public abstract interface UnivariateRealOptimizer
  extends ConvergingAlgorithm
{
  public abstract void setMaxEvaluations(int paramInt);
  
  public abstract int getMaxEvaluations();
  
  public abstract int getEvaluations();
  
  public abstract double optimize(UnivariateRealFunction paramUnivariateRealFunction, GoalType paramGoalType, double paramDouble1, double paramDouble2)
    throws ConvergenceException, FunctionEvaluationException;
  
  public abstract double optimize(UnivariateRealFunction paramUnivariateRealFunction, GoalType paramGoalType, double paramDouble1, double paramDouble2, double paramDouble3)
    throws ConvergenceException, FunctionEvaluationException;
  
  public abstract double getResult();
  
  public abstract double getFunctionValue()
    throws FunctionEvaluationException;
}
