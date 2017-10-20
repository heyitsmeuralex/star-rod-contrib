package org.apache.commons.math.analysis.solvers;

import org.apache.commons.math.ConvergenceException;
import org.apache.commons.math.ConvergingAlgorithmImpl;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.util.LocalizedFormats;





























@Deprecated
public abstract class UnivariateRealSolverImpl
  extends ConvergingAlgorithmImpl
  implements UnivariateRealSolver
{
  protected double functionValueAccuracy;
  protected double defaultFunctionValueAccuracy;
  protected boolean resultComputed = false;
  





  protected double result;
  





  protected double functionValue;
  





  @Deprecated
  protected UnivariateRealFunction f;
  






  @Deprecated
  protected UnivariateRealSolverImpl(UnivariateRealFunction f, int defaultMaximalIterationCount, double defaultAbsoluteAccuracy)
  {
    super(defaultMaximalIterationCount, defaultAbsoluteAccuracy);
    if (f == null) {
      throw new NullArgumentException(LocalizedFormats.FUNCTION);
    }
    this.f = f;
    defaultFunctionValueAccuracy = 1.0E-15D;
    functionValueAccuracy = defaultFunctionValueAccuracy;
  }
  








  protected UnivariateRealSolverImpl(int defaultMaximalIterationCount, double defaultAbsoluteAccuracy)
  {
    super(defaultMaximalIterationCount, defaultAbsoluteAccuracy);
    defaultFunctionValueAccuracy = 1.0E-15D;
    functionValueAccuracy = defaultFunctionValueAccuracy;
  }
  

  protected void checkResultComputed()
    throws IllegalStateException
  {
    if (!resultComputed) {
      throw MathRuntimeException.createIllegalStateException(LocalizedFormats.NO_RESULT_AVAILABLE, new Object[0]);
    }
  }
  
  public double getResult()
  {
    checkResultComputed();
    return result;
  }
  
  public double getFunctionValue()
  {
    checkResultComputed();
    return functionValue;
  }
  
  public void setFunctionValueAccuracy(double accuracy)
  {
    functionValueAccuracy = accuracy;
  }
  
  public double getFunctionValueAccuracy()
  {
    return functionValueAccuracy;
  }
  
  public void resetFunctionValueAccuracy()
  {
    functionValueAccuracy = defaultFunctionValueAccuracy;
  }
  

















  public double solve(int maxEval, UnivariateRealFunction function, double min, double max)
    throws ConvergenceException, FunctionEvaluationException
  {
    throw MathRuntimeException.createUnsupportedOperationException(LocalizedFormats.NOT_OVERRIDEN, new Object[0]);
  }
  


















  public double solve(int maxEval, UnivariateRealFunction function, double min, double max, double startValue)
    throws ConvergenceException, FunctionEvaluationException, IllegalArgumentException
  {
    throw MathRuntimeException.createUnsupportedOperationException(LocalizedFormats.NOT_OVERRIDEN, new Object[0]);
  }
  





  protected final void setResult(double newResult, int iterationCount)
  {
    result = newResult;
    this.iterationCount = iterationCount;
    resultComputed = true;
  }
  







  protected final void setResult(double x, double fx, int iterationCount)
  {
    result = x;
    functionValue = fx;
    this.iterationCount = iterationCount;
    resultComputed = true;
  }
  


  protected final void clearResult()
  {
    iterationCount = 0;
    resultComputed = false;
  }
  









  protected boolean isBracketing(double lower, double upper, UnivariateRealFunction function)
    throws FunctionEvaluationException
  {
    double f1 = function.value(lower);
    double f2 = function.value(upper);
    return ((f1 > 0.0D) && (f2 < 0.0D)) || ((f1 < 0.0D) && (f2 > 0.0D));
  }
  







  protected boolean isSequence(double start, double mid, double end)
  {
    return (start < mid) && (mid < end);
  }
  







  protected void verifyInterval(double lower, double upper)
  {
    if (lower >= upper) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.ENDPOINTS_NOT_AN_INTERVAL, new Object[] { Double.valueOf(lower), Double.valueOf(upper) });
    }
  }
  










  protected void verifySequence(double lower, double initial, double upper)
  {
    if (!isSequence(lower, initial, upper)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INVALID_INTERVAL_INITIAL_VALUE_PARAMETERS, new Object[] { Double.valueOf(lower), Double.valueOf(initial), Double.valueOf(upper) });
    }
  }
  













  protected void verifyBracketing(double lower, double upper, UnivariateRealFunction function)
    throws FunctionEvaluationException
  {
    verifyInterval(lower, upper);
    if (!isBracketing(lower, upper, function)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.SAME_SIGN_AT_ENDPOINTS, new Object[] { Double.valueOf(lower), Double.valueOf(upper), Double.valueOf(function.value(lower)), Double.valueOf(function.value(upper)) });
    }
  }
}
