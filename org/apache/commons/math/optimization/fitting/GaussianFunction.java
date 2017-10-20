package org.apache.commons.math.optimization.fitting;

import java.io.Serializable;
import org.apache.commons.math.analysis.DifferentiableUnivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.ZeroException;
import org.apache.commons.math.exception.util.LocalizedFormats;

























































public class GaussianFunction
  implements DifferentiableUnivariateRealFunction, Serializable
{
  private static final long serialVersionUID = -3195385616125629512L;
  private final double a;
  private final double b;
  private final double c;
  private final double d;
  
  public GaussianFunction(double a, double b, double c, double d)
  {
    if (d == 0.0D) {
      throw new ZeroException();
    }
    this.a = a;
    this.b = b;
    this.c = c;
    this.d = d;
  }
  









  public GaussianFunction(double[] parameters)
  {
    if (parameters == null) {
      throw new NullArgumentException(LocalizedFormats.INPUT_ARRAY);
    }
    if (parameters.length != 4) {
      throw new DimensionMismatchException(4, parameters.length);
    }
    if (parameters[3] == 0.0D) {
      throw new ZeroException();
    }
    a = parameters[0];
    b = parameters[1];
    c = parameters[2];
    d = parameters[3];
  }
  
  public UnivariateRealFunction derivative()
  {
    return new GaussianDerivativeFunction(b, c, d);
  }
  
  public double value(double x)
  {
    double xMc = x - c;
    return a + b * Math.exp(-xMc * xMc / (2.0D * (d * d)));
  }
  




  public double getA()
  {
    return a;
  }
  




  public double getB()
  {
    return b;
  }
  




  public double getC()
  {
    return c;
  }
  




  public double getD()
  {
    return d;
  }
}
