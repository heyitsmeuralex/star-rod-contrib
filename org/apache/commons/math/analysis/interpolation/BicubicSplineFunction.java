package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.analysis.BivariateRealFunction;
import org.apache.commons.math.exception.OutOfRangeException;









































































































































































































































































































































































class BicubicSplineFunction
  implements BivariateRealFunction
{
  private static final short N = 4;
  private final double[][] a;
  private BivariateRealFunction partialDerivativeX;
  private BivariateRealFunction partialDerivativeY;
  private BivariateRealFunction partialDerivativeXX;
  private BivariateRealFunction partialDerivativeYY;
  private BivariateRealFunction partialDerivativeXY;
  
  public BicubicSplineFunction(double[] a)
  {
    this.a = new double[4][4];
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        this.a[i][j] = a[(i + 4 * j)];
      }
    }
  }
  


  public double value(double x, double y)
  {
    if ((x < 0.0D) || (x > 1.0D)) {
      throw new OutOfRangeException(Double.valueOf(x), Integer.valueOf(0), Integer.valueOf(1));
    }
    if ((y < 0.0D) || (y > 1.0D)) {
      throw new OutOfRangeException(Double.valueOf(y), Integer.valueOf(0), Integer.valueOf(1));
    }
    
    double x2 = x * x;
    double x3 = x2 * x;
    double[] pX = { 1.0D, x, x2, x3 };
    
    double y2 = y * y;
    double y3 = y2 * y;
    double[] pY = { 1.0D, y, y2, y3 };
    
    return apply(pX, pY, a);
  }
  







  private double apply(double[] pX, double[] pY, double[][] coeff)
  {
    double result = 0.0D;
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        result += coeff[i][j] * pX[i] * pY[j];
      }
    }
    
    return result;
  }
  


  public BivariateRealFunction partialDerivativeX()
  {
    if (partialDerivativeX == null) {
      computePartialDerivatives();
    }
    
    return partialDerivativeX;
  }
  

  public BivariateRealFunction partialDerivativeY()
  {
    if (partialDerivativeY == null) {
      computePartialDerivatives();
    }
    
    return partialDerivativeY;
  }
  

  public BivariateRealFunction partialDerivativeXX()
  {
    if (partialDerivativeXX == null) {
      computePartialDerivatives();
    }
    
    return partialDerivativeXX;
  }
  

  public BivariateRealFunction partialDerivativeYY()
  {
    if (partialDerivativeYY == null) {
      computePartialDerivatives();
    }
    
    return partialDerivativeYY;
  }
  

  public BivariateRealFunction partialDerivativeXY()
  {
    if (partialDerivativeXY == null) {
      computePartialDerivatives();
    }
    
    return partialDerivativeXY;
  }
  


  private void computePartialDerivatives()
  {
    final double[][] aX = new double[4][4];
    final double[][] aY = new double[4][4];
    final double[][] aXX = new double[4][4];
    final double[][] aYY = new double[4][4];
    final double[][] aXY = new double[4][4];
    
    for (int i = 0; i < 4; i++) {
      for (int j = 0; j < 4; j++) {
        double c = a[i][j];
        aX[i][j] = (i * c);
        aY[i][j] = (j * c);
        aXX[i][j] = ((i - 1) * aX[i][j]);
        aYY[i][j] = ((j - 1) * aY[i][j]);
        aXY[i][j] = (j * aX[i][j]);
      }
    }
    
    partialDerivativeX = new BivariateRealFunction() {
      public double value(double x, double y) {
        double x2 = x * x;
        double[] pX = { 0.0D, 1.0D, x, x2 };
        
        double y2 = y * y;
        double y3 = y2 * y;
        double[] pY = { 1.0D, y, y2, y3 };
        
        return BicubicSplineFunction.this.apply(pX, pY, aX);
      }
    };
    partialDerivativeY = new BivariateRealFunction() {
      public double value(double x, double y) {
        double x2 = x * x;
        double x3 = x2 * x;
        double[] pX = { 1.0D, x, x2, x3 };
        
        double y2 = y * y;
        double[] pY = { 0.0D, 1.0D, y, y2 };
        
        return BicubicSplineFunction.this.apply(pX, pY, aY);
      }
    };
    partialDerivativeXX = new BivariateRealFunction() {
      public double value(double x, double y) {
        double[] pX = { 0.0D, 0.0D, 1.0D, x };
        
        double y2 = y * y;
        double y3 = y2 * y;
        double[] pY = { 1.0D, y, y2, y3 };
        
        return BicubicSplineFunction.this.apply(pX, pY, aXX);
      }
    };
    partialDerivativeYY = new BivariateRealFunction() {
      public double value(double x, double y) {
        double x2 = x * x;
        double x3 = x2 * x;
        double[] pX = { 1.0D, x, x2, x3 };
        
        double[] pY = { 0.0D, 0.0D, 1.0D, y };
        
        return BicubicSplineFunction.this.apply(pX, pY, aYY);
      }
    };
    partialDerivativeXY = new BivariateRealFunction() {
      public double value(double x, double y) {
        double x2 = x * x;
        double[] pX = { 0.0D, 1.0D, x, x2 };
        
        double y2 = y * y;
        double[] pY = { 0.0D, 1.0D, y, y2 };
        
        return BicubicSplineFunction.this.apply(pX, pY, aXY);
      }
    };
  }
}
