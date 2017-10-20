package org.apache.commons.math.optimization.linear;

import java.util.Collection;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.OptimizationException;
import org.apache.commons.math.optimization.RealPointValuePair;

public abstract interface LinearOptimizer
{
  public abstract void setMaxIterations(int paramInt);
  
  public abstract int getMaxIterations();
  
  public abstract int getIterations();
  
  public abstract RealPointValuePair optimize(LinearObjectiveFunction paramLinearObjectiveFunction, Collection<LinearConstraint> paramCollection, GoalType paramGoalType, boolean paramBoolean)
    throws OptimizationException;
}
