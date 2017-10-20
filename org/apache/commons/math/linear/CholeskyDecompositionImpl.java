package org.apache.commons.math.linear;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;


























































public class CholeskyDecompositionImpl
  implements CholeskyDecomposition
{
  public static final double DEFAULT_RELATIVE_SYMMETRY_THRESHOLD = 1.0E-15D;
  public static final double DEFAULT_ABSOLUTE_POSITIVITY_THRESHOLD = 1.0E-10D;
  private double[][] lTData;
  private RealMatrix cachedL;
  private RealMatrix cachedLT;
  
  public CholeskyDecompositionImpl(RealMatrix matrix)
    throws NonSquareMatrixException, NotSymmetricMatrixException, NotPositiveDefiniteMatrixException
  {
    this(matrix, 1.0E-15D, 1.0E-10D);
  }
  



















  public CholeskyDecompositionImpl(RealMatrix matrix, double relativeSymmetryThreshold, double absolutePositivityThreshold)
    throws NonSquareMatrixException, NotSymmetricMatrixException, NotPositiveDefiniteMatrixException
  {
    if (!matrix.isSquare()) {
      throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
    }
    

    int order = matrix.getRowDimension();
    lTData = matrix.getData();
    cachedL = null;
    cachedLT = null;
    

    for (int i = 0; i < order; i++)
    {
      double[] lI = lTData[i];
      

      for (int j = i + 1; j < order; j++) {
        double[] lJ = lTData[j];
        double lIJ = lI[j];
        double lJI = lJ[i];
        double maxDelta = relativeSymmetryThreshold * FastMath.max(FastMath.abs(lIJ), FastMath.abs(lJI));
        
        if (FastMath.abs(lIJ - lJI) > maxDelta) {
          throw new NotSymmetricMatrixException();
        }
        lJ[i] = 0.0D;
      }
    }
    

    for (int i = 0; i < order; i++)
    {
      double[] ltI = lTData[i];
      

      if (ltI[i] < absolutePositivityThreshold) {
        throw new NotPositiveDefiniteMatrixException();
      }
      
      ltI[i] = FastMath.sqrt(ltI[i]);
      double inverse = 1.0D / ltI[i];
      
      for (int q = order - 1; q > i; q--) {
        ltI[q] *= inverse;
        double[] ltQ = lTData[q];
        for (int p = q; p < order; p++) {
          ltQ[p] -= ltI[q] * ltI[p];
        }
      }
    }
  }
  


  public RealMatrix getL()
  {
    if (cachedL == null) {
      cachedL = getLT().transpose();
    }
    return cachedL;
  }
  

  public RealMatrix getLT()
  {
    if (cachedLT == null) {
      cachedLT = MatrixUtils.createRealMatrix(lTData);
    }
    

    return cachedLT;
  }
  

  public double getDeterminant()
  {
    double determinant = 1.0D;
    for (int i = 0; i < lTData.length; i++) {
      double lTii = lTData[i][i];
      determinant *= lTii * lTii;
    }
    return determinant;
  }
  
  public DecompositionSolver getSolver()
  {
    return new Solver(lTData, null);
  }
  


  private static class Solver
    implements DecompositionSolver
  {
    private final double[][] lTData;
    


    private Solver(double[][] lTData)
    {
      this.lTData = lTData;
    }
    

    public boolean isNonSingular()
    {
      return true;
    }
    

    public double[] solve(double[] b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      int m = lTData.length;
      if (b.length != m) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(b.length), Integer.valueOf(m) });
      }
      


      double[] x = (double[])b.clone();
      

      for (int j = 0; j < m; j++) {
        double[] lJ = lTData[j];
        x[j] /= lJ[j];
        double xJ = x[j];
        for (int i = j + 1; i < m; i++) {
          x[i] -= xJ * lJ[i];
        }
      }
      

      for (int j = m - 1; j >= 0; j--) {
        x[j] /= lTData[j][j];
        double xJ = x[j];
        for (int i = 0; i < j; i++) {
          x[i] -= xJ * lTData[i][j];
        }
      }
      
      return x;
    }
    
    public RealVector solve(RealVector b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      try
      {
        return solve((ArrayRealVector)b);
      }
      catch (ClassCastException cce) {
        int m = lTData.length;
        if (b.getDimension() != m) {
          throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(b.getDimension()), Integer.valueOf(m) });
        }
        


        double[] x = b.getData();
        

        for (int j = 0; j < m; j++) {
          double[] lJ = lTData[j];
          x[j] /= lJ[j];
          double xJ = x[j];
          for (int i = j + 1; i < m; i++) {
            x[i] -= xJ * lJ[i];
          }
        }
        

        for (int j = m - 1; j >= 0; j--) {
          x[j] /= lTData[j][j];
          double xJ = x[j];
          for (int i = 0; i < j; i++) {
            x[i] -= xJ * lTData[i][j];
          }
        }
        
        return new ArrayRealVector(x, false);
      }
    }
    







    public ArrayRealVector solve(ArrayRealVector b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      return new ArrayRealVector(solve(b.getDataRef()), false);
    }
    

    public RealMatrix solve(RealMatrix b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      int m = lTData.length;
      if (b.getRowDimension() != m) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(b.getRowDimension()), Integer.valueOf(b.getColumnDimension()), Integer.valueOf(m), "n" });
      }
      


      int nColB = b.getColumnDimension();
      double[][] x = b.getData();
      

      for (int j = 0; j < m; j++) {
        double[] lJ = lTData[j];
        double lJJ = lJ[j];
        double[] xJ = x[j];
        for (int k = 0; k < nColB; k++) {
          xJ[k] /= lJJ;
        }
        for (int i = j + 1; i < m; i++) {
          double[] xI = x[i];
          double lJI = lJ[i];
          for (int k = 0; k < nColB; k++) {
            xI[k] -= xJ[k] * lJI;
          }
        }
      }
      

      for (int j = m - 1; j >= 0; j--) {
        double lJJ = lTData[j][j];
        double[] xJ = x[j];
        for (int k = 0; k < nColB; k++) {
          xJ[k] /= lJJ;
        }
        for (int i = 0; i < j; i++) {
          double[] xI = x[i];
          double lIJ = lTData[i][j];
          for (int k = 0; k < nColB; k++) {
            xI[k] -= xJ[k] * lIJ;
          }
        }
      }
      
      return new Array2DRowRealMatrix(x, false);
    }
    
    public RealMatrix getInverse()
      throws InvalidMatrixException
    {
      return solve(MatrixUtils.createRealIdentityMatrix(lTData.length));
    }
  }
}
