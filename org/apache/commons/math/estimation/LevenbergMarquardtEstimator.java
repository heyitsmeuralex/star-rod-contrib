package org.apache.commons.math.estimation;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;








































































































































@Deprecated
public class LevenbergMarquardtEstimator
  extends AbstractEstimator
  implements Serializable
{
  private static final long serialVersionUID = -5705952631533171019L;
  private int solvedCols;
  private double[] diagR;
  private double[] jacNorm;
  private double[] beta;
  private int[] permutation;
  private int rank;
  private double lmPar;
  private double[] lmDir;
  private double initialStepBoundFactor;
  private double costRelativeTolerance;
  private double parRelativeTolerance;
  private double orthoTolerance;
  
  public LevenbergMarquardtEstimator()
  {
    setMaxCostEval(1000);
    

    setInitialStepBoundFactor(100.0D);
    setCostRelativeTolerance(1.0E-10D);
    setParRelativeTolerance(1.0E-10D);
    setOrthoTolerance(1.0E-10D);
  }
  









  public void setInitialStepBoundFactor(double initialStepBoundFactor)
  {
    this.initialStepBoundFactor = initialStepBoundFactor;
  }
  





  public void setCostRelativeTolerance(double costRelativeTolerance)
  {
    this.costRelativeTolerance = costRelativeTolerance;
  }
  






  public void setParRelativeTolerance(double parRelativeTolerance)
  {
    this.parRelativeTolerance = parRelativeTolerance;
  }
  






  public void setOrthoTolerance(double orthoTolerance)
  {
    this.orthoTolerance = orthoTolerance;
  }
  





























  public void estimate(EstimationProblem problem)
    throws EstimationException
  {
    initializeEstimate(problem);
    

    solvedCols = FastMath.min(rows, cols);
    diagR = new double[cols];
    jacNorm = new double[cols];
    beta = new double[cols];
    permutation = new int[cols];
    lmDir = new double[cols];
    

    double delta = 0.0D;
    double xNorm = 0.0D;
    double[] diag = new double[cols];
    double[] oldX = new double[cols];
    double[] oldRes = new double[rows];
    double[] work1 = new double[cols];
    double[] work2 = new double[cols];
    double[] work3 = new double[cols];
    

    updateResidualsAndCost();
    

    lmPar = 0.0D;
    boolean firstIteration = true;
    double maxCosine;
    double ratio;
    for (;;) {
      updateJacobian();
      qrDecomposition();
      

      qTy(residuals);
      


      for (int k = 0; k < solvedCols; k++) {
        int pk = permutation[k];
        jacobian[(k * cols + pk)] = diagR[pk];
      }
      
      if (firstIteration)
      {


        xNorm = 0.0D;
        for (int k = 0; k < cols; k++) {
          double dk = jacNorm[k];
          if (dk == 0.0D) {
            dk = 1.0D;
          }
          double xk = dk * parameters[k].getEstimate();
          xNorm += xk * xk;
          diag[k] = dk;
        }
        xNorm = FastMath.sqrt(xNorm);
        

        delta = xNorm == 0.0D ? initialStepBoundFactor : initialStepBoundFactor * xNorm;
      }
      


      maxCosine = 0.0D;
      if (cost != 0.0D) {
        for (int j = 0; j < solvedCols; j++) {
          int pj = permutation[j];
          double s = jacNorm[pj];
          if (s != 0.0D) {
            double sum = 0.0D;
            int index = pj;
            for (int i = 0; i <= j; i++) {
              sum += jacobian[index] * residuals[i];
              index += cols;
            }
            maxCosine = FastMath.max(maxCosine, FastMath.abs(sum) / (s * cost));
          }
        }
      }
      if (maxCosine <= orthoTolerance) {
        return;
      }
      

      for (int j = 0; j < cols; j++) {
        diag[j] = FastMath.max(diag[j], jacNorm[j]);
      }
      

      for (ratio = 0.0D; ratio < 1.0E-4D;)
      {

        for (int j = 0; j < solvedCols; j++) {
          int pj = permutation[j];
          oldX[pj] = parameters[pj].getEstimate();
        }
        double previousCost = cost;
        double[] tmpVec = residuals;
        residuals = oldRes;
        oldRes = tmpVec;
        

        determineLMParameter(oldRes, delta, diag, work1, work2, work3);
        

        double lmNorm = 0.0D;
        for (int j = 0; j < solvedCols; j++) {
          int pj = permutation[j];
          lmDir[pj] = (-lmDir[pj]);
          parameters[pj].setEstimate(oldX[pj] + lmDir[pj]);
          double s = diag[pj] * lmDir[pj];
          lmNorm += s * s;
        }
        lmNorm = FastMath.sqrt(lmNorm);
        

        if (firstIteration) {
          delta = FastMath.min(delta, lmNorm);
        }
        

        updateResidualsAndCost();
        

        double actRed = -1.0D;
        if (0.1D * cost < previousCost) {
          double r = cost / previousCost;
          actRed = 1.0D - r * r;
        }
        


        for (int j = 0; j < solvedCols; j++) {
          int pj = permutation[j];
          double dirJ = lmDir[pj];
          work1[j] = 0.0D;
          int index = pj;
          for (int i = 0; i <= j; i++) {
            work1[i] += jacobian[index] * dirJ;
            index += cols;
          }
        }
        double coeff1 = 0.0D;
        for (int j = 0; j < solvedCols; j++) {
          coeff1 += work1[j] * work1[j];
        }
        double pc2 = previousCost * previousCost;
        coeff1 /= pc2;
        double coeff2 = lmPar * lmNorm * lmNorm / pc2;
        double preRed = coeff1 + 2.0D * coeff2;
        double dirDer = -(coeff1 + coeff2);
        

        ratio = preRed == 0.0D ? 0.0D : actRed / preRed;
        

        if (ratio <= 0.25D) {
          double tmp = actRed < 0.0D ? 0.5D * dirDer / (dirDer + 0.5D * actRed) : 0.5D;
          
          if ((0.1D * cost >= previousCost) || (tmp < 0.1D)) {
            tmp = 0.1D;
          }
          delta = tmp * FastMath.min(delta, 10.0D * lmNorm);
          lmPar /= tmp;
        } else if ((lmPar == 0.0D) || (ratio >= 0.75D)) {
          delta = 2.0D * lmNorm;
          lmPar *= 0.5D;
        }
        

        if (ratio >= 1.0E-4D)
        {
          firstIteration = false;
          xNorm = 0.0D;
          for (int k = 0; k < cols; k++) {
            double xK = diag[k] * parameters[k].getEstimate();
            xNorm += xK * xK;
          }
          xNorm = FastMath.sqrt(xNorm);
        }
        else {
          cost = previousCost;
          for (int j = 0; j < solvedCols; j++) {
            int pj = permutation[j];
            parameters[pj].setEstimate(oldX[pj]);
          }
          tmpVec = residuals;
          residuals = oldRes;
          oldRes = tmpVec;
        }
        

        if (((FastMath.abs(actRed) <= costRelativeTolerance) && (preRed <= costRelativeTolerance) && (ratio <= 2.0D)) || (delta <= parRelativeTolerance * xNorm))
        {


          return;
        }
        


        if ((FastMath.abs(actRed) <= 2.2204E-16D) && (preRed <= 2.2204E-16D) && (ratio <= 2.0D)) {
          throw new EstimationException("cost relative tolerance is too small ({0}), no further reduction in the sum of squares is possible", new Object[] { Double.valueOf(costRelativeTolerance) });
        }
        

        if (delta <= 2.2204E-16D * xNorm) {
          throw new EstimationException("parameters relative tolerance is too small ({0}), no further improvement in the approximate solution is possible", new Object[] { Double.valueOf(parRelativeTolerance) });
        }
        

        if (maxCosine <= 2.2204E-16D) {
          throw new EstimationException("orthogonality tolerance is too small ({0}), solution is orthogonal to the jacobian", new Object[] { Double.valueOf(orthoTolerance) });
        }
      }
    }
  }
  






























  private void determineLMParameter(double[] qy, double delta, double[] diag, double[] work1, double[] work2, double[] work3)
  {
    for (int j = 0; j < rank; j++) {
      lmDir[permutation[j]] = qy[j];
    }
    for (int j = rank; j < cols; j++) {
      lmDir[permutation[j]] = 0.0D;
    }
    for (int k = rank - 1; k >= 0; k--) {
      int pk = permutation[k];
      double ypk = lmDir[pk] / diagR[pk];
      int index = pk;
      for (int i = 0; i < k; i++) {
        lmDir[permutation[i]] -= ypk * jacobian[index];
        index += cols;
      }
      lmDir[pk] = ypk;
    }
    


    double dxNorm = 0.0D;
    for (int j = 0; j < solvedCols; j++) {
      int pj = permutation[j];
      double s = diag[pj] * lmDir[pj];
      work1[pj] = s;
      dxNorm += s * s;
    }
    dxNorm = FastMath.sqrt(dxNorm);
    double fp = dxNorm - delta;
    if (fp <= 0.1D * delta) {
      lmPar = 0.0D;
      return;
    }
    




    double parl = 0.0D;
    if (rank == solvedCols) {
      for (int j = 0; j < solvedCols; j++) {
        int pj = permutation[j];
        work1[pj] *= diag[pj] / dxNorm;
      }
      double sum2 = 0.0D;
      for (int j = 0; j < solvedCols; j++) {
        int pj = permutation[j];
        double sum = 0.0D;
        int index = pj;
        for (int i = 0; i < j; i++) {
          sum += jacobian[index] * work1[permutation[i]];
          index += cols;
        }
        double s = (work1[pj] - sum) / diagR[pj];
        work1[pj] = s;
        sum2 += s * s;
      }
      parl = fp / (delta * sum2);
    }
    

    double sum2 = 0.0D;
    for (int j = 0; j < solvedCols; j++) {
      int pj = permutation[j];
      double sum = 0.0D;
      int index = pj;
      for (int i = 0; i <= j; i++) {
        sum += jacobian[index] * qy[i];
        index += cols;
      }
      sum /= diag[pj];
      sum2 += sum * sum;
    }
    double gNorm = FastMath.sqrt(sum2);
    double paru = gNorm / delta;
    if (paru == 0.0D)
    {
      paru = 2.2251E-308D / FastMath.min(delta, 0.1D);
    }
    


    lmPar = FastMath.min(paru, FastMath.max(lmPar, parl));
    if (lmPar == 0.0D) {
      lmPar = (gNorm / dxNorm);
    }
    
    for (int countdown = 10; countdown >= 0; countdown--)
    {

      if (lmPar == 0.0D) {
        lmPar = FastMath.max(2.2251E-308D, 0.001D * paru);
      }
      double sPar = FastMath.sqrt(lmPar);
      for (int j = 0; j < solvedCols; j++) {
        int pj = permutation[j];
        work1[pj] = (sPar * diag[pj]);
      }
      determineLMDirection(qy, work1, work2, work3);
      
      dxNorm = 0.0D;
      for (int j = 0; j < solvedCols; j++) {
        int pj = permutation[j];
        double s = diag[pj] * lmDir[pj];
        work3[pj] = s;
        dxNorm += s * s;
      }
      dxNorm = FastMath.sqrt(dxNorm);
      double previousFP = fp;
      fp = dxNorm - delta;
      


      if ((FastMath.abs(fp) <= 0.1D * delta) || ((parl == 0.0D) && (fp <= previousFP) && (previousFP < 0.0D)))
      {
        return;
      }
      

      for (int j = 0; j < solvedCols; j++) {
        int pj = permutation[j];
        work1[pj] = (work3[pj] * diag[pj] / dxNorm);
      }
      for (int j = 0; j < solvedCols; j++) {
        int pj = permutation[j];
        work1[pj] /= work2[j];
        double tmp = work1[pj];
        for (int i = j + 1; i < solvedCols; i++) {
          work1[permutation[i]] -= jacobian[(i * cols + pj)] * tmp;
        }
      }
      sum2 = 0.0D;
      for (int j = 0; j < solvedCols; j++) {
        double s = work1[permutation[j]];
        sum2 += s * s;
      }
      double correction = fp / (delta * sum2);
      

      if (fp > 0.0D) {
        parl = FastMath.max(parl, lmPar);
      } else if (fp < 0.0D) {
        paru = FastMath.min(paru, lmPar);
      }
      

      lmPar = FastMath.max(parl, lmPar + correction);
    }
  }
  
























  private void determineLMDirection(double[] qy, double[] diag, double[] lmDiag, double[] work)
  {
    for (int j = 0; j < solvedCols; j++) {
      int pj = permutation[j];
      for (int i = j + 1; i < solvedCols; i++) {
        jacobian[(i * cols + pj)] = jacobian[(j * cols + permutation[i])];
      }
      lmDir[j] = diagR[pj];
      work[j] = qy[j];
    }
    

    for (int j = 0; j < solvedCols; j++)
    {


      int pj = permutation[j];
      double dpj = diag[pj];
      if (dpj != 0.0D) {
        Arrays.fill(lmDiag, j + 1, lmDiag.length, 0.0D);
      }
      lmDiag[j] = dpj;
      



      double qtbpj = 0.0D;
      for (int k = j; k < solvedCols; k++) {
        int pk = permutation[k];
        


        if (lmDiag[k] != 0.0D)
        {


          double rkk = jacobian[(k * cols + pk)];
          double cos; double cos; double sin; if (FastMath.abs(rkk) < FastMath.abs(lmDiag[k])) {
            double cotan = rkk / lmDiag[k];
            double sin = 1.0D / FastMath.sqrt(1.0D + cotan * cotan);
            cos = sin * cotan;
          } else {
            double tan = lmDiag[k] / rkk;
            cos = 1.0D / FastMath.sqrt(1.0D + tan * tan);
            sin = cos * tan;
          }
          


          jacobian[(k * cols + pk)] = (cos * rkk + sin * lmDiag[k]);
          double temp = cos * work[k] + sin * qtbpj;
          qtbpj = -sin * work[k] + cos * qtbpj;
          work[k] = temp;
          

          for (int i = k + 1; i < solvedCols; i++) {
            double rik = jacobian[(i * cols + pk)];
            double temp2 = cos * rik + sin * lmDiag[i];
            lmDiag[i] = (-sin * rik + cos * lmDiag[i]);
            jacobian[(i * cols + pk)] = temp2;
          }
        }
      }
      



      int index = j * cols + permutation[j];
      lmDiag[j] = jacobian[index];
      jacobian[index] = lmDir[j];
    }
    



    int nSing = solvedCols;
    for (int j = 0; j < solvedCols; j++) {
      if ((lmDiag[j] == 0.0D) && (nSing == solvedCols)) {
        nSing = j;
      }
      if (nSing < solvedCols) {
        work[j] = 0.0D;
      }
    }
    if (nSing > 0) {
      for (int j = nSing - 1; j >= 0; j--) {
        int pj = permutation[j];
        double sum = 0.0D;
        for (int i = j + 1; i < nSing; i++) {
          sum += jacobian[(i * cols + pj)] * work[i];
        }
        work[j] = ((work[j] - sum) / lmDiag[j]);
      }
    }
    

    for (int j = 0; j < lmDir.length; j++) {
      lmDir[permutation[j]] = work[j];
    }
  }
  























  private void qrDecomposition()
    throws EstimationException
  {
    for (int k = 0; k < cols; k++) {
      permutation[k] = k;
      double norm2 = 0.0D;
      for (int index = k; index < jacobian.length; index += cols) {
        double akk = jacobian[index];
        norm2 += akk * akk;
      }
      jacNorm[k] = FastMath.sqrt(norm2);
    }
    

    for (int k = 0; k < cols; k++)
    {

      int nextColumn = -1;
      double ak2 = Double.NEGATIVE_INFINITY;
      for (int i = k; i < cols; i++) {
        double norm2 = 0.0D;
        int iDiag = k * cols + permutation[i];
        for (int index = iDiag; index < jacobian.length; index += cols) {
          double aki = jacobian[index];
          norm2 += aki * aki;
        }
        if ((Double.isInfinite(norm2)) || (Double.isNaN(norm2))) {
          throw new EstimationException(LocalizedFormats.UNABLE_TO_PERFORM_QR_DECOMPOSITION_ON_JACOBIAN, new Object[] { Integer.valueOf(rows), Integer.valueOf(cols) });
        }
        

        if (norm2 > ak2) {
          nextColumn = i;
          ak2 = norm2;
        }
      }
      if (ak2 == 0.0D) {
        rank = k;
        return;
      }
      int pk = permutation[nextColumn];
      permutation[nextColumn] = permutation[k];
      permutation[k] = pk;
      

      int kDiag = k * cols + pk;
      double akk = jacobian[kDiag];
      double alpha = akk > 0.0D ? -FastMath.sqrt(ak2) : FastMath.sqrt(ak2);
      double betak = 1.0D / (ak2 - akk * alpha);
      beta[pk] = betak;
      

      diagR[pk] = alpha;
      jacobian[kDiag] -= alpha;
      

      for (int dk = cols - 1 - k; dk > 0; dk--) {
        int dkp = permutation[(k + dk)] - pk;
        double gamma = 0.0D;
        for (int index = kDiag; index < jacobian.length; index += cols) {
          gamma += jacobian[index] * jacobian[(index + dkp)];
        }
        gamma *= betak;
        for (int index = kDiag; index < jacobian.length; index += cols) {
          jacobian[(index + dkp)] -= gamma * jacobian[index];
        }
      }
    }
    

    rank = solvedCols;
  }
  





  private void qTy(double[] y)
  {
    for (int k = 0; k < cols; k++) {
      int pk = permutation[k];
      int kDiag = k * cols + pk;
      double gamma = 0.0D;
      int index = kDiag;
      for (int i = k; i < rows; i++) {
        gamma += jacobian[index] * y[i];
        index += cols;
      }
      gamma *= beta[pk];
      index = kDiag;
      for (int i = k; i < rows; i++) {
        y[i] -= gamma * jacobian[index];
        index += cols;
      }
    }
  }
}
