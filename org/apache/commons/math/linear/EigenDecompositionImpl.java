package org.apache.commons.math.linear;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.MaxIterationsExceededException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;















































public class EigenDecompositionImpl
  implements EigenDecomposition
{
  private byte maxIter = 30;
  


  private double[] main;
  


  private double[] secondary;
  


  private TriDiagonalTransformer transformer;
  


  private double[] realEigenvalues;
  


  private double[] imagEigenvalues;
  


  private ArrayRealVector[] eigenvectors;
  


  private RealMatrix cachedV;
  


  private RealMatrix cachedD;
  

  private RealMatrix cachedVt;
  


  public EigenDecompositionImpl(RealMatrix matrix, double splitTolerance)
    throws InvalidMatrixException
  {
    if (isSymmetric(matrix)) {
      transformToTridiagonal(matrix);
      findEigenVectors(transformer.getQ().getData());

    }
    else
    {
      throw new InvalidMatrixException(LocalizedFormats.ASSYMETRIC_EIGEN_NOT_SUPPORTED, new Object[0]);
    }
  }
  











  public EigenDecompositionImpl(double[] main, double[] secondary, double splitTolerance)
    throws InvalidMatrixException
  {
    this.main = ((double[])main.clone());
    this.secondary = ((double[])secondary.clone());
    transformer = null;
    int size = main.length;
    double[][] z = new double[size][size];
    for (int i = 0; i < size; i++) {
      z[i][i] = 1.0D;
    }
    findEigenVectors(z);
  }
  





  private boolean isSymmetric(RealMatrix matrix)
  {
    int rows = matrix.getRowDimension();
    int columns = matrix.getColumnDimension();
    double eps = 10 * rows * columns * 1.1102230246251565E-16D;
    for (int i = 0; i < rows; i++) {
      for (int j = i + 1; j < columns; j++) {
        double mij = matrix.getEntry(i, j);
        double mji = matrix.getEntry(j, i);
        if (FastMath.abs(mij - mji) > FastMath.max(FastMath.abs(mij), FastMath.abs(mji)) * eps)
        {
          return false;
        }
      }
    }
    return true;
  }
  
  public RealMatrix getV()
    throws InvalidMatrixException
  {
    if (cachedV == null) {
      int m = eigenvectors.length;
      cachedV = MatrixUtils.createRealMatrix(m, m);
      for (int k = 0; k < m; k++) {
        cachedV.setColumnVector(k, eigenvectors[k]);
      }
    }
    
    return cachedV;
  }
  
  public RealMatrix getD()
    throws InvalidMatrixException
  {
    if (cachedD == null)
    {
      cachedD = MatrixUtils.createRealDiagonalMatrix(realEigenvalues);
    }
    return cachedD;
  }
  
  public RealMatrix getVT()
    throws InvalidMatrixException
  {
    if (cachedVt == null) {
      int m = eigenvectors.length;
      cachedVt = MatrixUtils.createRealMatrix(m, m);
      for (int k = 0; k < m; k++) {
        cachedVt.setRowVector(k, eigenvectors[k]);
      }
    }
    


    return cachedVt;
  }
  
  public double[] getRealEigenvalues() throws InvalidMatrixException
  {
    return (double[])realEigenvalues.clone();
  }
  
  public double getRealEigenvalue(int i)
    throws InvalidMatrixException, ArrayIndexOutOfBoundsException
  {
    return realEigenvalues[i];
  }
  
  public double[] getImagEigenvalues() throws InvalidMatrixException
  {
    return (double[])imagEigenvalues.clone();
  }
  
  public double getImagEigenvalue(int i)
    throws InvalidMatrixException, ArrayIndexOutOfBoundsException
  {
    return imagEigenvalues[i];
  }
  
  public RealVector getEigenvector(int i)
    throws InvalidMatrixException, ArrayIndexOutOfBoundsException
  {
    return eigenvectors[i].copy();
  }
  



  public double getDeterminant()
  {
    double determinant = 1.0D;
    for (double lambda : realEigenvalues) {
      determinant *= lambda;
    }
    return determinant;
  }
  
  public DecompositionSolver getSolver()
  {
    return new Solver(realEigenvalues, imagEigenvalues, eigenvectors, null);
  }
  




  private static class Solver
    implements DecompositionSolver
  {
    private double[] realEigenvalues;
    



    private double[] imagEigenvalues;
    



    private final ArrayRealVector[] eigenvectors;
    



    private Solver(double[] realEigenvalues, double[] imagEigenvalues, ArrayRealVector[] eigenvectors)
    {
      this.realEigenvalues = realEigenvalues;
      this.imagEigenvalues = imagEigenvalues;
      this.eigenvectors = eigenvectors;
    }
    














    public double[] solve(double[] b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      if (!isNonSingular()) {
        throw new SingularMatrixException();
      }
      
      int m = realEigenvalues.length;
      if (b.length != m) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(b.length), Integer.valueOf(m) });
      }
      


      double[] bp = new double[m];
      for (int i = 0; i < m; i++) {
        ArrayRealVector v = eigenvectors[i];
        double[] vData = v.getDataRef();
        double s = v.dotProduct(b) / realEigenvalues[i];
        for (int j = 0; j < m; j++) {
          bp[j] += s * vData[j];
        }
      }
      
      return bp;
    }
    















    public RealVector solve(RealVector b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      if (!isNonSingular()) {
        throw new SingularMatrixException();
      }
      
      int m = realEigenvalues.length;
      if (b.getDimension() != m) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(b.getDimension()), Integer.valueOf(m) });
      }
      


      double[] bp = new double[m];
      for (int i = 0; i < m; i++) {
        ArrayRealVector v = eigenvectors[i];
        double[] vData = v.getDataRef();
        double s = v.dotProduct(b) / realEigenvalues[i];
        for (int j = 0; j < m; j++) {
          bp[j] += s * vData[j];
        }
      }
      
      return new ArrayRealVector(bp, false);
    }
    















    public RealMatrix solve(RealMatrix b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      if (!isNonSingular()) {
        throw new SingularMatrixException();
      }
      
      int m = realEigenvalues.length;
      if (b.getRowDimension() != m) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(b.getRowDimension()), Integer.valueOf(b.getColumnDimension()), Integer.valueOf(m), "n" });
      }
      




      int nColB = b.getColumnDimension();
      double[][] bp = new double[m][nColB];
      for (int k = 0; k < nColB; k++) {
        for (int i = 0; i < m; i++) {
          ArrayRealVector v = eigenvectors[i];
          double[] vData = v.getDataRef();
          double s = 0.0D;
          for (int j = 0; j < m; j++) {
            s += v.getEntry(j) * b.getEntry(j, k);
          }
          s /= realEigenvalues[i];
          for (int j = 0; j < m; j++) {
            bp[j][k] += s * vData[j];
          }
        }
      }
      
      return MatrixUtils.createRealMatrix(bp);
    }
    




    public boolean isNonSingular()
    {
      for (int i = 0; i < realEigenvalues.length; i++) {
        if ((realEigenvalues[i] == 0.0D) && (imagEigenvalues[i] == 0.0D)) {
          return false;
        }
      }
      return true;
    }
    





    public RealMatrix getInverse()
      throws InvalidMatrixException
    {
      if (!isNonSingular()) {
        throw new SingularMatrixException();
      }
      
      int m = realEigenvalues.length;
      double[][] invData = new double[m][m];
      
      for (int i = 0; i < m; i++) {
        double[] invI = invData[i];
        for (int j = 0; j < m; j++) {
          double invIJ = 0.0D;
          for (int k = 0; k < m; k++) {
            double[] vK = eigenvectors[k].getDataRef();
            invIJ += vK[i] * vK[j] / realEigenvalues[k];
          }
          invI[j] = invIJ;
        }
      }
      return MatrixUtils.createRealMatrix(invData);
    }
  }
  








  private void transformToTridiagonal(RealMatrix matrix)
  {
    transformer = new TriDiagonalTransformer(matrix);
    main = transformer.getMainDiagonalRef();
    secondary = transformer.getSecondaryDiagonalRef();
  }
  






  private void findEigenVectors(double[][] householderMatrix)
  {
    double[][] z = (double[][])householderMatrix.clone();
    int n = main.length;
    realEigenvalues = new double[n];
    imagEigenvalues = new double[n];
    double[] e = new double[n];
    for (int i = 0; i < n - 1; i++) {
      realEigenvalues[i] = main[i];
      e[i] = secondary[i];
    }
    realEigenvalues[(n - 1)] = main[(n - 1)];
    e[(n - 1)] = 0.0D;
    

    double maxAbsoluteValue = 0.0D;
    for (int i = 0; i < n; i++) {
      if (FastMath.abs(realEigenvalues[i]) > maxAbsoluteValue) {
        maxAbsoluteValue = FastMath.abs(realEigenvalues[i]);
      }
      if (FastMath.abs(e[i]) > maxAbsoluteValue) {
        maxAbsoluteValue = FastMath.abs(e[i]);
      }
    }
    
    if (maxAbsoluteValue != 0.0D) {
      for (int i = 0; i < n; i++) {
        if (FastMath.abs(realEigenvalues[i]) <= 1.1102230246251565E-16D * maxAbsoluteValue) {
          realEigenvalues[i] = 0.0D;
        }
        if (FastMath.abs(e[i]) <= 1.1102230246251565E-16D * maxAbsoluteValue) {
          e[i] = 0.0D;
        }
      }
    }
    
    for (int j = 0; j < n; j++) {
      int its = 0;
      int m;
      do {
        for (m = j; m < n - 1; m++) {
          double delta = FastMath.abs(realEigenvalues[m]) + FastMath.abs(realEigenvalues[(m + 1)]);
          if (FastMath.abs(e[m]) + delta == delta) {
            break;
          }
        }
        if (m != j) {
          if (its == maxIter) {
            throw new InvalidMatrixException(new MaxIterationsExceededException(maxIter));
          }
          its++;
          double q = (realEigenvalues[(j + 1)] - realEigenvalues[j]) / (2.0D * e[j]);
          double t = FastMath.sqrt(1.0D + q * q);
          if (q < 0.0D) {
            q = realEigenvalues[m] - realEigenvalues[j] + e[j] / (q - t);
          } else {
            q = realEigenvalues[m] - realEigenvalues[j] + e[j] / (q + t);
          }
          double u = 0.0D;
          double s = 1.0D;
          double c = 1.0D;
          
          for (int i = m - 1; i >= j; i--) {
            double p = s * e[i];
            double h = c * e[i];
            if (FastMath.abs(p) >= FastMath.abs(q)) {
              c = q / p;
              t = FastMath.sqrt(c * c + 1.0D);
              e[(i + 1)] = (p * t);
              s = 1.0D / t;
              c *= s;
            } else {
              s = p / q;
              t = FastMath.sqrt(s * s + 1.0D);
              e[(i + 1)] = (q * t);
              c = 1.0D / t;
              s *= c;
            }
            if (e[(i + 1)] == 0.0D) {
              realEigenvalues[(i + 1)] -= u;
              e[m] = 0.0D;
              break;
            }
            q = realEigenvalues[(i + 1)] - u;
            t = (realEigenvalues[i] - q) * s + 2.0D * c * h;
            u = s * t;
            realEigenvalues[(i + 1)] = (q + u);
            q = c * t - h;
            for (int ia = 0; ia < n; ia++) {
              p = z[ia][(i + 1)];
              z[ia][(i + 1)] = (s * z[ia][i] + c * p);
              z[ia][i] = (c * z[ia][i] - s * p);
            }
          }
          if ((t != 0.0D) || (i < j))
          {
            realEigenvalues[j] -= u;
            e[j] = q;
            e[m] = 0.0D;
          }
        } } while (m != j);
    }
    

    for (int i = 0; i < n; i++) {
      int k = i;
      double p = realEigenvalues[i];
      for (int j = i + 1; j < n; j++) {
        if (realEigenvalues[j] > p) {
          k = j;
          p = realEigenvalues[j];
        }
      }
      if (k != i) {
        realEigenvalues[k] = realEigenvalues[i];
        realEigenvalues[i] = p;
        for (int j = 0; j < n; j++) {
          p = z[j][i];
          z[j][i] = z[j][k];
          z[j][k] = p;
        }
      }
    }
    

    maxAbsoluteValue = 0.0D;
    for (int i = 0; i < n; i++) {
      if (FastMath.abs(realEigenvalues[i]) > maxAbsoluteValue) {
        maxAbsoluteValue = FastMath.abs(realEigenvalues[i]);
      }
    }
    
    if (maxAbsoluteValue != 0.0D) {
      for (int i = 0; i < n; i++) {
        if (FastMath.abs(realEigenvalues[i]) < 1.1102230246251565E-16D * maxAbsoluteValue) {
          realEigenvalues[i] = 0.0D;
        }
      }
    }
    eigenvectors = new ArrayRealVector[n];
    double[] tmp = new double[n];
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < n; j++) {
        tmp[j] = z[j][i];
      }
      eigenvectors[i] = new ArrayRealVector(tmp);
    }
  }
}
