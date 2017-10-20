package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.ConvergingAlgorithm;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.UnivariateRealFunction;

public abstract interface UnivariateRealSolver
  extends ConvergingAlgorithm
{
  public abstract void setFunctionValueAccuracy(double paramDouble);
  
  public abstract double getFunctionValueAccuracy();
  
  public abstract void resetFunctionValueAccuracy();
  
  @Deprecated
  public abstract double solve(double paramDouble1, double paramDouble2)
    throws ConvergenceException, FunctionEvaluationException;
  
  @Deprecated
  public abstract double solve(UnivariateRealFunction paramUnivariateRealFunction, double paramDouble1, double paramDouble2)
    throws ConvergenceException, FunctionEvaluationException;
  
  @Deprecated
  public abstract double solve(double paramDouble1, double paramDouble2, double paramDouble3)
    throws ConvergenceException, FunctionEvaluationException, IllegalArgumentException;
  
  @Deprecated
  public abstract double solve(UnivariateRealFunction paramUnivariateRealFunction, double paramDouble1, double paramDouble2, double paramDouble3)
    throws ConvergenceException, FunctionEvaluationException, IllegalArgumentException;
  
  public abstract double getResult();
  
  public abstract double getFunctionValue();
}
