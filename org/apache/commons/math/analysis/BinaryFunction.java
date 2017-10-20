package org.apache.commons.math.analysis;

import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.util.FastMath;



























@Deprecated
public abstract class BinaryFunction
  implements BivariateRealFunction
{
  public static final BinaryFunction ADD = new BinaryFunction()
  {
    public double value(double x, double y)
    {
      return x + y;
    }
  };
  

  public static final BinaryFunction SUBTRACT = new BinaryFunction()
  {
    public double value(double x, double y)
    {
      return x - y;
    }
  };
  

  public static final BinaryFunction MULTIPLY = new BinaryFunction()
  {
    public double value(double x, double y)
    {
      return x * y;
    }
  };
  

  public static final BinaryFunction DIVIDE = new BinaryFunction()
  {
    public double value(double x, double y)
    {
      return x / y;
    }
  };
  

  public static final BinaryFunction POW = new BinaryFunction()
  {
    public double value(double x, double y)
    {
      return FastMath.pow(x, y);
    }
  };
  

  public static final BinaryFunction ATAN2 = new BinaryFunction()
  {
    public double value(double x, double y)
    {
      return FastMath.atan2(x, y);
    }
  };
  

  public BinaryFunction() {}
  
  public abstract double value(double paramDouble1, double paramDouble2)
    throws FunctionEvaluationException;
  
  public ComposableFunction fix1stArgument(final double fixedX)
  {
    new ComposableFunction()
    {
      public double value(double x) throws FunctionEvaluationException
      {
        return value(fixedX, x);
      }
    };
  }
  



  public ComposableFunction fix2ndArgument(final double fixedY)
  {
    new ComposableFunction()
    {
      public double value(double x) throws FunctionEvaluationException
      {
        return value(x, fixedY);
      }
    };
  }
}
