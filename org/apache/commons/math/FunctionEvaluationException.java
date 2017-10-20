package org.apache.commons.math;

import org.apache.commons.math.exception.util.DummyLocalizable;
import org.apache.commons.math.exception.util.Localizable;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.linear.ArrayRealVector;
































public class FunctionEvaluationException
  extends MathException
{
  private static final long serialVersionUID = 1384427981840836868L;
  private double[] argument;
  
  public FunctionEvaluationException(double argument)
  {
    super(LocalizedFormats.EVALUATION_FAILED, new Object[] { Double.valueOf(argument) });
    this.argument = new double[] { argument };
  }
  






  public FunctionEvaluationException(double[] argument)
  {
    super(LocalizedFormats.EVALUATION_FAILED, new Object[] { new ArrayRealVector(argument) });
    this.argument = ((double[])argument.clone());
  }
  








  public FunctionEvaluationException(double argument, String pattern, Object... arguments)
  {
    this(argument, new DummyLocalizable(pattern), arguments);
  }
  








  public FunctionEvaluationException(double argument, Localizable pattern, Object... arguments)
  {
    super(pattern, arguments);
    this.argument = new double[] { argument };
  }
  








  public FunctionEvaluationException(double[] argument, String pattern, Object... arguments)
  {
    this(argument, new DummyLocalizable(pattern), arguments);
  }
  








  public FunctionEvaluationException(double[] argument, Localizable pattern, Object... arguments)
  {
    super(pattern, arguments);
    this.argument = ((double[])argument.clone());
  }
  






  public FunctionEvaluationException(Throwable cause, double argument)
  {
    super(cause);
    this.argument = new double[] { argument };
  }
  






  public FunctionEvaluationException(Throwable cause, double[] argument)
  {
    super(cause);
    this.argument = ((double[])argument.clone());
  }
  










  public FunctionEvaluationException(Throwable cause, double argument, String pattern, Object... arguments)
  {
    this(cause, argument, new DummyLocalizable(pattern), arguments);
  }
  










  public FunctionEvaluationException(Throwable cause, double argument, Localizable pattern, Object... arguments)
  {
    super(cause, pattern, arguments);
    this.argument = new double[] { argument };
  }
  










  public FunctionEvaluationException(Throwable cause, double[] argument, String pattern, Object... arguments)
  {
    this(cause, argument, new DummyLocalizable(pattern), arguments);
  }
  










  public FunctionEvaluationException(Throwable cause, double[] argument, Localizable pattern, Object... arguments)
  {
    super(cause, pattern, arguments);
    this.argument = ((double[])argument.clone());
  }
  




  public double[] getArgument()
  {
    return (double[])argument.clone();
  }
}
