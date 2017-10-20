package org.apache.commons.math.analysis.interpolation;

import org.apache.commons.math.DimensionMismatchException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.analysis.BivariateRealFunction;
import org.apache.commons.math.analysis.UnivariateRealFunction;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.MathUtils;
import org.apache.commons.math.util.MathUtils.OrderDirection;

































@Deprecated
public class SmoothingBicubicSplineInterpolator
  implements BivariateRealGridInterpolator
{
  public SmoothingBicubicSplineInterpolator() {}
  
  public BivariateRealFunction interpolate(double[] xval, double[] yval, double[][] zval)
    throws MathException, IllegalArgumentException
  {
    if ((xval.length == 0) || (yval.length == 0) || (zval.length == 0)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NO_DATA, new Object[0]);
    }
    if (xval.length != zval.length) {
      throw new DimensionMismatchException(xval.length, zval.length);
    }
    
    MathUtils.checkOrder(xval, MathUtils.OrderDirection.INCREASING, true);
    MathUtils.checkOrder(yval, MathUtils.OrderDirection.INCREASING, true);
    
    int xLen = xval.length;
    int yLen = yval.length;
    




    double[][] zX = new double[yLen][xLen];
    for (int i = 0; i < xLen; i++) {
      if (zval[i].length != yLen) {
        throw new DimensionMismatchException(zval[i].length, yLen);
      }
      
      for (int j = 0; j < yLen; j++) {
        zX[j][i] = zval[i][j];
      }
    }
    
    SplineInterpolator spInterpolator = new SplineInterpolator();
    


    PolynomialSplineFunction[] ySplineX = new PolynomialSplineFunction[yLen];
    for (int j = 0; j < yLen; j++) {
      ySplineX[j] = spInterpolator.interpolate(xval, zX[j]);
    }
    


    double[][] zY_1 = new double[xLen][yLen];
    for (int j = 0; j < yLen; j++) {
      PolynomialSplineFunction f = ySplineX[j];
      for (int i = 0; i < xLen; i++) {
        zY_1[i][j] = f.value(xval[i]);
      }
    }
    


    PolynomialSplineFunction[] xSplineY = new PolynomialSplineFunction[xLen];
    for (int i = 0; i < xLen; i++) {
      xSplineY[i] = spInterpolator.interpolate(yval, zY_1[i]);
    }
    


    double[][] zY_2 = new double[xLen][yLen];
    for (int i = 0; i < xLen; i++) {
      PolynomialSplineFunction f = xSplineY[i];
      for (int j = 0; j < yLen; j++) {
        zY_2[i][j] = f.value(yval[j]);
      }
    }
    

    double[][] dZdX = new double[xLen][yLen];
    for (int j = 0; j < yLen; j++) {
      UnivariateRealFunction f = ySplineX[j].derivative();
      for (int i = 0; i < xLen; i++) {
        dZdX[i][j] = f.value(xval[i]);
      }
    }
    

    double[][] dZdY = new double[xLen][yLen];
    for (int i = 0; i < xLen; i++) {
      UnivariateRealFunction f = xSplineY[i].derivative();
      for (int j = 0; j < yLen; j++) {
        dZdY[i][j] = f.value(yval[j]);
      }
    }
    

    double[][] dZdXdY = new double[xLen][yLen];
    for (int i = 0; i < xLen; i++) {
      int nI = nextIndex(i, xLen);
      int pI = previousIndex(i);
      for (int j = 0; j < yLen; j++) {
        int nJ = nextIndex(j, yLen);
        int pJ = previousIndex(j);
        dZdXdY[i][j] = ((zY_2[nI][nJ] - zY_2[nI][pJ] - zY_2[pI][nJ] + zY_2[pI][pJ]) / ((xval[nI] - xval[pI]) * (yval[nJ] - yval[pJ])));
      }
    }
    



    return new BicubicSplineInterpolatingFunction(xval, yval, zY_2, dZdX, dZdY, dZdXdY);
  }
  








  private int nextIndex(int i, int max)
  {
    int index = i + 1;
    return index < max ? index : index - 1;
  }
  





  private int previousIndex(int i)
  {
    int index = i - 1;
    return index >= 0 ? index : 0;
  }
}
