package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.analysis.TrivariateRealFunction;
import org.apache.commons.math.exception.OutOfRangeException;



































































































































































































































































































































































































































class TricubicSplineFunction
  implements TrivariateRealFunction
{
  private static final short N = 4;
  private final double[][][] a = new double[4][4][4];
  


  public TricubicSplineFunction(double[] aV)
  {
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        for (int k = 0; k < 4; k++) {
          a[i][j][k] = aV[(i + 4 * (j + 4 * k))];
        }
      }
    }
  }
  





  public double value(double x, double y, double z)
  {
    if ((x < 0.0D) || (x > 1.0D)) {
      throw new OutOfRangeException(Double.valueOf(x), Integer.valueOf(0), Integer.valueOf(1));
    }
    if ((y < 0.0D) || (y > 1.0D)) {
      throw new OutOfRangeException(Double.valueOf(y), Integer.valueOf(0), Integer.valueOf(1));
    }
    if ((z < 0.0D) || (z > 1.0D)) {
      throw new OutOfRangeException(Double.valueOf(z), Integer.valueOf(0), Integer.valueOf(1));
    }
    
    double x2 = x * x;
    double x3 = x2 * x;
    double[] pX = { 1.0D, x, x2, x3 };
    
    double y2 = y * y;
    double y3 = y2 * y;
    double[] pY = { 1.0D, y, y2, y3 };
    
    double z2 = z * z;
    double z3 = z2 * z;
    double[] pZ = { 1.0D, z, z2, z3 };
    
    double result = 0.0D;
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        for (int k = 0; k < 4; k++) {
          result += a[i][j][k] * pX[i] * pY[j] * pZ[k];
        }
      }
    }
    
    return result;
  }
}
