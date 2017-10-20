package org.apache.commons.math.optimization.univariate;

import org.apache.commons.math.ConvergingAlgorithmImpl;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MaxEvaluationsExceededException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.MathUnsupportedOperationException;
import org.apache.commons.math.exception.NoDataException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.optimization.GoalType;
import org.apache.commons.math.optimization.UnivariateRealOptimizer;











































public abstract class AbstractUnivariateRealOptimizer
  extends ConvergingAlgorithmImpl
  implements UnivariateRealOptimizer
{
  protected boolean resultComputed;
  protected double result;
  protected double functionValue;
  private int maxEvaluations;
  private int evaluations;
  private GoalType optimizationGoal;
  private double searchMin;
  private double searchMax;
  private double searchStart;
  private UnivariateRealFunction function;
  
  @Deprecated
  protected AbstractUnivariateRealOptimizer(int defaultMaximalIterationCount, double defaultAbsoluteAccuracy)
  {
    super(defaultMaximalIterationCount, defaultAbsoluteAccuracy);
    resultComputed = false;
    setMaxEvaluations(Integer.MAX_VALUE);
  }
  




  protected AbstractUnivariateRealOptimizer() {}
  




  @Deprecated
  protected void checkResultComputed()
  {
    if (!resultComputed) {
      throw new NoDataException();
    }
  }
  
  public double getResult()
  {
    if (!resultComputed) {
      throw new NoDataException();
    }
    return result;
  }
  
  public double getFunctionValue() throws FunctionEvaluationException
  {
    if (Double.isNaN(functionValue)) {
      double opt = getResult();
      functionValue = function.value(opt);
    }
    return functionValue;
  }
  








  @Deprecated
  protected final void setResult(double x, double fx, int iterationCount)
  {
    result = x;
    functionValue = fx;
    this.iterationCount = iterationCount;
    resultComputed = true;
  }
  



  @Deprecated
  protected final void clearResult()
  {
    resultComputed = false;
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
    return evaluations;
  }
  


  public GoalType getGoalType()
  {
    return optimizationGoal;
  }
  

  public double getMin()
  {
    return searchMin;
  }
  

  public double getMax()
  {
    return searchMax;
  }
  

  public double getStartValue()
  {
    return searchStart;
  }
  










  @Deprecated
  protected double computeObjectiveValue(UnivariateRealFunction f, double point)
    throws FunctionEvaluationException
  {
    if (++evaluations > maxEvaluations) {
      throw new FunctionEvaluationException(new MaxEvaluationsExceededException(maxEvaluations), point);
    }
    return f.value(point);
  }
  







  protected double computeObjectiveValue(double point)
    throws FunctionEvaluationException
  {
    if (++evaluations > maxEvaluations) {
      resultComputed = false;
      throw new FunctionEvaluationException(new MaxEvaluationsExceededException(maxEvaluations), point);
    }
    return function.value(point);
  }
  


  public double optimize(UnivariateRealFunction f, GoalType goal, double min, double max, double startValue)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    searchMin = min;
    searchMax = max;
    searchStart = startValue;
    optimizationGoal = goal;
    function = f;
    

    functionValue = NaN.0D;
    evaluations = 0;
    resetIterationsCounter();
    

    result = doOptimize();
    resultComputed = true;
    
    return result;
  }
  




  protected void setFunctionValue(double functionValue)
  {
    this.functionValue = functionValue;
  }
  

  public double optimize(UnivariateRealFunction f, GoalType goal, double min, double max)
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    return optimize(f, goal, min, max, min + 0.5D * (max - min));
  }
  














  protected double doOptimize()
    throws MaxIterationsExceededException, FunctionEvaluationException
  {
    throw new MathUnsupportedOperationException(LocalizedFormats.NOT_OVERRIDEN, new Object[0]);
  }
}
