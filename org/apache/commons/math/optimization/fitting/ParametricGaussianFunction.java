package org.apache.commons.math.optimization.fitting;

import java.io.Serializable;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NullArgumentException;
import org.apache.commons.math.exception.ZeroException;
import org.apache.commons.math.exception.util.LocalizedFormats;


































































public class ParametricGaussianFunction
  implements ParametricRealFunction, Serializable
{
  private static final long serialVersionUID = -3875578602503903233L;
  
  public ParametricGaussianFunction() {}
  
  public double value(double x, double[] parameters)
    throws ZeroException
  {
    validateParameters(parameters);
    double a = parameters[0];
    double b = parameters[1];
    double c = parameters[2];
    double d = parameters[3];
    double xMc = x - c;
    return a + b * Math.exp(-xMc * xMc / (2.0D * (d * d)));
  }
  






























  public double[] gradient(double x, double[] parameters)
    throws ZeroException
  {
    validateParameters(parameters);
    double b = parameters[1];
    double c = parameters[2];
    double d = parameters[3];
    
    double xMc = x - c;
    double d2 = d * d;
    double exp = Math.exp(-xMc * xMc / (2.0D * d2));
    double f = b * exp * xMc / d2;
    
    return new double[] { 1.0D, exp, f, f * xMc / d };
  }
  












  private void validateParameters(double[] parameters)
    throws ZeroException
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
  }
}
