package org.apache.commons.math.stat.regression;

import org.apache.commons.math.linear.Array2DRowRealMatrix;
import org.apache.commons.math.linear.DecompositionSolver;
import org.apache.commons.math.linear.LUDecompositionImpl;
import org.apache.commons.math.linear.QRDecomposition;
import org.apache.commons.math.linear.QRDecompositionImpl;
import org.apache.commons.math.linear.RealMatrix;
import org.apache.commons.math.linear.RealVector;
import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.moment.SecondMoment;











































public class OLSMultipleLinearRegression
  extends AbstractMultipleLinearRegression
{
  private QRDecomposition qr = null;
  



  public OLSMultipleLinearRegression() {}
  



  public void newSampleData(double[] y, double[][] x)
  {
    validateSampleData(x, y);
    newYSampleData(y);
    newXSampleData(x);
  }
  




  public void newSampleData(double[] data, int nobs, int nvars)
  {
    super.newSampleData(data, nobs, nvars);
    qr = new QRDecompositionImpl(X);
  }
  















  public RealMatrix calculateHat()
  {
    RealMatrix Q = qr.getQ();
    int p = qr.getR().getColumnDimension();
    int n = Q.getColumnDimension();
    Array2DRowRealMatrix augI = new Array2DRowRealMatrix(n, n);
    double[][] augIData = augI.getDataRef();
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        if ((i == j) && (i < p)) {
          augIData[i][j] = 1.0D;
        } else {
          augIData[i][j] = 0.0D;
        }
      }
    }
    

    return Q.multiply(augI).multiply(Q.transpose());
  }
  












  public double calculateTotalSumOfSquares()
  {
    if (isNoIntercept()) {
      return StatUtils.sumSq(Y.getData());
    }
    return new SecondMoment().evaluate(Y.getData());
  }
  






  public double calculateResidualSumOfSquares()
  {
    RealVector residuals = calculateResiduals();
    return residuals.dotProduct(residuals);
  }
  









  public double calculateRSquared()
  {
    return 1.0D - calculateResidualSumOfSquares() / calculateTotalSumOfSquares();
  }
  















  public double calculateAdjustedRSquared()
  {
    double n = X.getRowDimension();
    if (isNoIntercept()) {
      return 1.0D - (1.0D - calculateRSquared()) * (n / (n - X.getColumnDimension()));
    }
    return 1.0D - calculateResidualSumOfSquares() * (n - 1.0D) / (calculateTotalSumOfSquares() * (n - X.getColumnDimension()));
  }
  







  protected void newXSampleData(double[][] x)
  {
    super.newXSampleData(x);
    qr = new QRDecompositionImpl(X);
  }
  





  protected RealVector calculateBeta()
  {
    return qr.getSolver().solve(Y);
  }
  











  protected RealMatrix calculateBetaVariance()
  {
    int p = X.getColumnDimension();
    RealMatrix Raug = qr.getR().getSubMatrix(0, p - 1, 0, p - 1);
    RealMatrix Rinv = new LUDecompositionImpl(Raug).getSolver().getInverse();
    return Rinv.multiply(Rinv.transpose());
  }
}
