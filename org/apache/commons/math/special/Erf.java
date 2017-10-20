package org.apache.commons.math.special;

import org.apache.commons.math.MathException;
import org.apache.commons.math.util.FastMath;











































public class Erf
{
  private Erf() {}
  
  public static double erf(double x)
    throws MathException
  {
    if (FastMath.abs(x) > 40.0D) {
      return x > 0.0D ? 1.0D : -1.0D;
    }
    double ret = Gamma.regularizedGammaP(0.5D, x * x, 1.0E-15D, 10000);
    if (x < 0.0D) {
      ret = -ret;
    }
    return ret;
  }
  

















  public static double erfc(double x)
    throws MathException
  {
    if (FastMath.abs(x) > 40.0D) {
      return x > 0.0D ? 0.0D : 2.0D;
    }
    double ret = Gamma.regularizedGammaQ(0.5D, x * x, 1.0E-15D, 10000);
    return x < 0.0D ? 2.0D - ret : ret;
  }
}
