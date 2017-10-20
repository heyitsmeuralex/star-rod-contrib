package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.polynomials.PolynomialFunction;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NoDataException;
import org.apache.commons.math.optimization.fitting.PolynomialFitter;
import org.apache.commons.math.optimization.general.GaussNewtonOptimizer;
import org.apache.commons.math.util.MathUtils;






























public class SmoothingPolynomialBicubicSplineInterpolator
  extends BicubicSplineInterpolator
{
  private final PolynomialFitter xFitter;
  private final PolynomialFitter yFitter;
  
  public SmoothingPolynomialBicubicSplineInterpolator()
  {
    this(3);
  }
  


  public SmoothingPolynomialBicubicSplineInterpolator(int degree)
  {
    this(degree, degree);
  }
  






  public SmoothingPolynomialBicubicSplineInterpolator(int xDegree, int yDegree)
  {
    xFitter = new PolynomialFitter(xDegree, new GaussNewtonOptimizer(false));
    yFitter = new PolynomialFitter(yDegree, new GaussNewtonOptimizer(false));
  }
  





  public BicubicSplineInterpolatingFunction interpolate(double[] xval, double[] yval, double[][] fval)
    throws MathException
  {
    if ((xval.length == 0) || (yval.length == 0) || (fval.length == 0)) {
      throw new NoDataException();
    }
    if (xval.length != fval.length) {
      throw new DimensionMismatchException(xval.length, fval.length);
    }
    
    int xLen = xval.length;
    int yLen = yval.length;
    
    for (int i = 0; i < xLen; i++) {
      if (fval[i].length != yLen) {
        throw new DimensionMismatchException(fval[i].length, yLen);
      }
    }
    
    MathUtils.checkOrder(xval);
    MathUtils.checkOrder(yval);
    


    PolynomialFunction[] yPolyX = new PolynomialFunction[yLen];
    for (int j = 0; j < yLen; j++) {
      xFitter.clearObservations();
      for (int i = 0; i < xLen; i++) {
        xFitter.addObservedPoint(1.0D, xval[i], fval[i][j]);
      }
      
      yPolyX[j] = xFitter.fit();
    }
    


    double[][] fval_1 = new double[xLen][yLen];
    for (int j = 0; j < yLen; j++) {
      PolynomialFunction f = yPolyX[j];
      for (int i = 0; i < xLen; i++) {
        fval_1[i][j] = f.value(xval[i]);
      }
    }
    


    PolynomialFunction[] xPolyY = new PolynomialFunction[xLen];
    for (int i = 0; i < xLen; i++) {
      yFitter.clearObservations();
      for (int j = 0; j < yLen; j++) {
        yFitter.addObservedPoint(1.0D, yval[j], fval_1[i][j]);
      }
      
      xPolyY[i] = yFitter.fit();
    }
    


    double[][] fval_2 = new double[xLen][yLen];
    for (int i = 0; i < xLen; i++) {
      PolynomialFunction f = xPolyY[i];
      for (int j = 0; j < yLen; j++) {
        fval_2[i][j] = f.value(yval[j]);
      }
    }
    
    return super.interpolate(xval, yval, fval_2);
  }
}
