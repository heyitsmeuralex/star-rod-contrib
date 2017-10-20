package org.apache.commons.math.analysis.integration;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.ConvergingAlgorithm;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

public abstract interface UnivariateRealIntegrator
  extends ConvergingAlgorithm
{
  public abstract void setMinimalIterationCount(int paramInt);
  
  public abstract int getMinimalIterationCount();
  
  public abstract void resetMinimalIterationCount();
  
  @Deprecated
  public abstract double integrate(double paramDouble1, double paramDouble2)
    throws ConvergenceException, FunctionEvaluationException, IllegalArgumentException;
  
  public abstract double integrate(UnivariateRealFunction paramUnivariateRealFunction, double paramDouble1, double paramDouble2)
    throws ConvergenceException, FunctionEvaluationException, IllegalArgumentException;
  
  public abstract double getResult()
    throws IllegalStateException;
}
