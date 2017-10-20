package org.apache.commons.math.genetics;

public abstract interface StoppingCondition
{
  public abstract boolean isSatisfied(Population paramPopulation);
}
