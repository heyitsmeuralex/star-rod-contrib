package org.apache.commons.math.stat.regression;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.ArrayRealVector;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.stat.descriptive.moment.Variance;
import org.apache.commons.math.util.FastMath;



























public abstract class AbstractMultipleLinearRegression
  implements MultipleLinearRegression
{
  protected RealMatrix X;
  protected RealVector Y;
  private boolean noIntercept = false;
  

  public AbstractMultipleLinearRegression() {}
  
  public boolean isNoIntercept()
  {
    return noIntercept;
  }
  



  public void setNoIntercept(boolean noIntercept)
  {
    this.noIntercept = noIntercept;
  }
  






























  public void newSampleData(double[] data, int nobs, int nvars)
  {
    if (data == null) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NULL_NOT_ALLOWED, new Object[0]);
    }
    
    if (data.length != nobs * (nvars + 1)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INVALID_REGRESSION_ARRAY, new Object[] { Integer.valueOf(data.length), Integer.valueOf(nobs), Integer.valueOf(nvars) });
    }
    
    if (nobs <= nvars) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, new Object[0]);
    }
    
    double[] y = new double[nobs];
    int cols = noIntercept ? nvars : nvars + 1;
    double[][] x = new double[nobs][cols];
    int pointer = 0;
    for (int i = 0; i < nobs; i++) {
      y[i] = data[(pointer++)];
      if (!noIntercept) {
        x[i][0] = 1.0D;
      }
      for (int j = noIntercept ? 0 : 1; j < cols; j++) {
        x[i][j] = data[(pointer++)];
      }
    }
    X = new Array2DRowRealMatrix(x);
    Y = new ArrayRealVector(y);
  }
  





  protected void newYSampleData(double[] y)
  {
    if (y == null) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NULL_NOT_ALLOWED, new Object[0]);
    }
    
    if (y.length == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NO_DATA, new Object[0]);
    }
    
    Y = new ArrayRealVector(y);
  }
  






















  protected void newXSampleData(double[][] x)
  {
    if (x == null) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NULL_NOT_ALLOWED, new Object[0]);
    }
    
    if (x.length == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NO_DATA, new Object[0]);
    }
    
    if (noIntercept) {
      X = new Array2DRowRealMatrix(x, true);
    } else {
      int nVars = x[0].length;
      double[][] xAug = new double[x.length][nVars + 1];
      for (int i = 0; i < x.length; i++) {
        if (x[i].length != nVars) {
          throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIFFERENT_ROWS_LENGTHS, new Object[] { Integer.valueOf(x[i].length), Integer.valueOf(nVars) });
        }
        

        xAug[i][0] = 1.0D;
        System.arraycopy(x[i], 0, xAug[i], 1, nVars);
      }
      X = new Array2DRowRealMatrix(xAug, false);
    }
  }
  













  protected void validateSampleData(double[][] x, double[] y)
  {
    if ((x == null) || (y == null) || (x.length != y.length)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(x == null ? 0 : x.length), Integer.valueOf(y == null ? 0 : y.length) });
    }
    


    if (x.length == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NO_DATA, new Object[0]);
    }
    
    if (x[0].length + 1 > x.length) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_ENOUGH_DATA_FOR_NUMBER_OF_PREDICTORS, new Object[] { Integer.valueOf(x.length), Integer.valueOf(x[0].length) });
    }
  }
  










  protected void validateCovarianceData(double[][] x, double[][] covariance)
  {
    if (x.length != covariance.length) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_SIMPLE, new Object[] { Integer.valueOf(x.length), Integer.valueOf(covariance.length) });
    }
    
    if ((covariance.length > 0) && (covariance.length != covariance[0].length)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NON_SQUARE_MATRIX, new Object[] { Integer.valueOf(covariance.length), Integer.valueOf(covariance[0].length) });
    }
  }
  




  public double[] estimateRegressionParameters()
  {
    RealVector b = calculateBeta();
    return b.getData();
  }
  


  public double[] estimateResiduals()
  {
    RealVector b = calculateBeta();
    RealVector e = Y.subtract(X.operate(b));
    return e.getData();
  }
  


  public double[][] estimateRegressionParametersVariance()
  {
    return calculateBetaVariance().getData();
  }
  


  public double[] estimateRegressionParametersStandardErrors()
  {
    double[][] betaVariance = estimateRegressionParametersVariance();
    double sigma = calculateErrorVariance();
    int length = betaVariance[0].length;
    double[] result = new double[length];
    for (int i = 0; i < length; i++) {
      result[i] = FastMath.sqrt(sigma * betaVariance[i][i]);
    }
    return result;
  }
  


  public double estimateRegressandVariance()
  {
    return calculateYVariance();
  }
  





  public double estimateErrorVariance()
  {
    return calculateErrorVariance();
  }
  






  public double estimateRegressionStandardError()
  {
    return Math.sqrt(estimateErrorVariance());
  }
  






  protected abstract RealVector calculateBeta();
  





  protected abstract RealMatrix calculateBetaVariance();
  





  protected double calculateYVariance()
  {
    return new Variance().evaluate(Y.getData());
  }
  










  protected double calculateErrorVariance()
  {
    RealVector residuals = calculateResiduals();
    return residuals.dotProduct(residuals) / (X.getRowDimension() - X.getColumnDimension());
  }
  










  protected RealVector calculateResiduals()
  {
    RealVector b = calculateBeta();
    return Y.subtract(X.operate(b));
  }
}
