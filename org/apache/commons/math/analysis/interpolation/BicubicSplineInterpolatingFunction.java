package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.FunctionEvaluationException;
import org.apache.commons.math.analysis.BivariateRealFunction;
import org.apache.commons.math.exception.NoDataException;
import org.apache.commons.math.exception.OutOfRangeException;
import org.apache.commons.math.util.MathUtils;




























public class BicubicSplineInterpolatingFunction
  implements BivariateRealFunction
{
  private static final double[][] AINV = { { 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { -3.0D, 3.0D, 0.0D, 0.0D, -2.0D, -1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { 2.0D, -2.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, -3.0D, 3.0D, 0.0D, 0.0D, -2.0D, -1.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 2.0D, -2.0D, 0.0D, 0.0D, 1.0D, 1.0D, 0.0D, 0.0D }, { -3.0D, 0.0D, 3.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, -2.0D, 0.0D, -1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, -3.0D, 0.0D, 3.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, -2.0D, 0.0D, -1.0D, 0.0D }, { 9.0D, -9.0D, -9.0D, 9.0D, 6.0D, 3.0D, -6.0D, -3.0D, 6.0D, -6.0D, 3.0D, -3.0D, 4.0D, 2.0D, 2.0D, 1.0D }, { -6.0D, 6.0D, 6.0D, -6.0D, -3.0D, -3.0D, 3.0D, 3.0D, -4.0D, 4.0D, -2.0D, 2.0D, -2.0D, -2.0D, -1.0D, -1.0D }, { 2.0D, 0.0D, -2.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 1.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D }, { 0.0D, 0.0D, 0.0D, 0.0D, 2.0D, 0.0D, -2.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 1.0D, 0.0D, 1.0D, 0.0D }, { -6.0D, 6.0D, 6.0D, -6.0D, -4.0D, -2.0D, 4.0D, 2.0D, -3.0D, 3.0D, -3.0D, 3.0D, -2.0D, -1.0D, -2.0D, -1.0D }, { 4.0D, -4.0D, -4.0D, 4.0D, 2.0D, 2.0D, -2.0D, -2.0D, 2.0D, -2.0D, 2.0D, -2.0D, 1.0D, 1.0D, 1.0D, 1.0D } };
  







  private final double[] xval;
  







  private final double[] yval;
  






  private final BicubicSplineFunction[][] splines;
  






  private BivariateRealFunction[][][] partialDerivatives = (BivariateRealFunction[][][])null;
  




















  public BicubicSplineInterpolatingFunction(double[] x, double[] y, double[][] f, double[][] dFdX, double[][] dFdY, double[][] d2FdXdY)
    throws DimensionMismatchException
  {
    int xLen = x.length;
    int yLen = y.length;
    
    if ((xLen == 0) || (yLen == 0) || (f.length == 0) || (f[0].length == 0)) {
      throw new NoDataException();
    }
    if (xLen != f.length) {
      throw new DimensionMismatchException(xLen, f.length);
    }
    if (xLen != dFdX.length) {
      throw new DimensionMismatchException(xLen, dFdX.length);
    }
    if (xLen != dFdY.length) {
      throw new DimensionMismatchException(xLen, dFdY.length);
    }
    if (xLen != d2FdXdY.length) {
      throw new DimensionMismatchException(xLen, d2FdXdY.length);
    }
    
    MathUtils.checkOrder(x);
    MathUtils.checkOrder(y);
    
    xval = ((double[])x.clone());
    yval = ((double[])y.clone());
    
    int lastI = xLen - 1;
    int lastJ = yLen - 1;
    splines = new BicubicSplineFunction[lastI][lastJ];
    
    for (int i = 0; i < lastI; i++) {
      if (f[i].length != yLen) {
        throw new DimensionMismatchException(f[i].length, yLen);
      }
      if (dFdX[i].length != yLen) {
        throw new DimensionMismatchException(dFdX[i].length, yLen);
      }
      if (dFdY[i].length != yLen) {
        throw new DimensionMismatchException(dFdY[i].length, yLen);
      }
      if (d2FdXdY[i].length != yLen) {
        throw new DimensionMismatchException(d2FdXdY[i].length, yLen);
      }
      int ip1 = i + 1;
      for (int j = 0; j < lastJ; j++) {
        int jp1 = j + 1;
        double[] beta = { f[i][j], f[ip1][j], f[i][jp1], f[ip1][jp1], dFdX[i][j], dFdX[ip1][j], dFdX[i][jp1], dFdX[ip1][jp1], dFdY[i][j], dFdY[ip1][j], dFdY[i][jp1], dFdY[ip1][jp1], d2FdXdY[i][j], d2FdXdY[ip1][j], d2FdXdY[i][jp1], d2FdXdY[ip1][jp1] };
        





        splines[i][j] = new BicubicSplineFunction(computeSplineCoefficients(beta));
      }
    }
  }
  


  public double value(double x, double y)
  {
    int i = searchIndex(x, xval);
    if (i == -1) {
      throw new OutOfRangeException(Double.valueOf(x), Double.valueOf(xval[0]), Double.valueOf(xval[(xval.length - 1)]));
    }
    int j = searchIndex(y, yval);
    if (j == -1) {
      throw new OutOfRangeException(Double.valueOf(y), Double.valueOf(yval[0]), Double.valueOf(yval[(yval.length - 1)]));
    }
    
    double xN = (x - xval[i]) / (xval[(i + 1)] - xval[i]);
    double yN = (y - yval[j]) / (yval[(j + 1)] - yval[j]);
    
    return splines[i][j].value(xN, yN);
  }
  






  public double partialDerivativeX(double x, double y)
  {
    return partialDerivative(0, x, y);
  }
  





  public double partialDerivativeY(double x, double y)
  {
    return partialDerivative(1, x, y);
  }
  





  public double partialDerivativeXX(double x, double y)
  {
    return partialDerivative(2, x, y);
  }
  





  public double partialDerivativeYY(double x, double y)
  {
    return partialDerivative(3, x, y);
  }
  




  public double partialDerivativeXY(double x, double y)
  {
    return partialDerivative(4, x, y);
  }
  






  private double partialDerivative(int which, double x, double y)
  {
    if (partialDerivatives == null) {
      computePartialDerivatives();
    }
    
    int i = searchIndex(x, xval);
    if (i == -1) {
      throw new OutOfRangeException(Double.valueOf(x), Double.valueOf(xval[0]), Double.valueOf(xval[(xval.length - 1)]));
    }
    int j = searchIndex(y, yval);
    if (j == -1) {
      throw new OutOfRangeException(Double.valueOf(y), Double.valueOf(yval[0]), Double.valueOf(yval[(yval.length - 1)]));
    }
    
    double xN = (x - xval[i]) / (xval[(i + 1)] - xval[i]);
    double yN = (y - yval[j]) / (yval[(j + 1)] - yval[j]);
    try
    {
      return partialDerivatives[which][i][j].value(xN, yN);
    }
    catch (FunctionEvaluationException fee) {
      throw new RuntimeException(fee);
    }
  }
  



  private void computePartialDerivatives()
  {
    int lastI = xval.length - 1;
    int lastJ = yval.length - 1;
    partialDerivatives = new BivariateRealFunction[5][lastI][lastJ];
    
    for (int i = 0; i < lastI; i++) {
      for (int j = 0; j < lastJ; j++) {
        BicubicSplineFunction f = splines[i][j];
        partialDerivatives[0][i][j] = f.partialDerivativeX();
        partialDerivatives[1][i][j] = f.partialDerivativeY();
        partialDerivatives[2][i][j] = f.partialDerivativeXX();
        partialDerivatives[3][i][j] = f.partialDerivativeYY();
        partialDerivatives[4][i][j] = f.partialDerivativeXY();
      }
    }
  }
  






  private int searchIndex(double c, double[] val)
  {
    if (c < val[0]) {
      return -1;
    }
    
    int max = val.length;
    for (int i = 1; i < max; i++) {
      if (c <= val[i]) {
        return i - 1;
      }
    }
    
    return -1;
  }
  




























  private double[] computeSplineCoefficients(double[] beta)
  {
    double[] a = new double[16];
    
    for (int i = 0; i < 16; i++) {
      double result = 0.0D;
      double[] row = AINV[i];
      for (int j = 0; j < 16; j++) {
        result += row[j] * beta[j];
      }
      a[i] = result;
    }
    
    return a;
  }
}
