package org.apache.commons.math.stat.regression;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;







































public class GLSMultipleLinearRegression
  extends AbstractMultipleLinearRegression
{
  private RealMatrix Omega;
  private RealMatrix OmegaInverse;
  
  public GLSMultipleLinearRegression() {}
  
  public void newSampleData(double[] y, double[][] x, double[][] covariance)
  {
    validateSampleData(x, y);
    newYSampleData(y);
    newXSampleData(x);
    validateCovarianceData(x, covariance);
    newCovarianceData(covariance);
  }
  




  protected void newCovarianceData(double[][] omega)
  {
    Omega = new Array2DRowRealMatrix(omega);
    OmegaInverse = null;
  }
  




  protected RealMatrix getOmegaInverse()
  {
    if (OmegaInverse == null) {
      OmegaInverse = new LUDecompositionImpl(Omega).getSolver().getInverse();
    }
    return OmegaInverse;
  }
  







  protected RealVector calculateBeta()
  {
    RealMatrix OI = getOmegaInverse();
    RealMatrix XT = X.transpose();
    RealMatrix XTOIX = XT.multiply(OI).multiply(X);
    RealMatrix inverse = new LUDecompositionImpl(XTOIX).getSolver().getInverse();
    return inverse.multiply(XT).multiply(OI).operate(Y);
  }
  







  protected RealMatrix calculateBetaVariance()
  {
    RealMatrix OI = getOmegaInverse();
    RealMatrix XTOIX = X.transpose().multiply(OI).multiply(X);
    return new LUDecompositionImpl(XTOIX).getSolver().getInverse();
  }
  












  protected double calculateErrorVariance()
  {
    RealVector residuals = calculateResiduals();
    double t = residuals.dotProduct(getOmegaInverse().operate(residuals));
    return t / (X.getRowDimension() - X.getColumnDimension());
  }
}
