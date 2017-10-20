package org.apache.commons.math.linear;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;
















































public class LUDecompositionImpl
  implements LUDecomposition
{
  private static final double DEFAULT_TOO_SMALL = 1.0E-11D;
  private double[][] lu;
  private int[] pivot;
  private boolean even;
  private boolean singular;
  private RealMatrix cachedL;
  private RealMatrix cachedU;
  private RealMatrix cachedP;
  
  public LUDecompositionImpl(RealMatrix matrix)
    throws InvalidMatrixException
  {
    this(matrix, 1.0E-11D);
  }
  







  public LUDecompositionImpl(RealMatrix matrix, double singularityThreshold)
    throws NonSquareMatrixException
  {
    if (!matrix.isSquare()) {
      throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
    }
    
    int m = matrix.getColumnDimension();
    lu = matrix.getData();
    pivot = new int[m];
    cachedL = null;
    cachedU = null;
    cachedP = null;
    

    for (int row = 0; row < m; row++) {
      pivot[row] = row;
    }
    even = true;
    singular = false;
    

    for (int col = 0; col < m; col++)
    {
      double sum = 0.0D;
      

      for (int row = 0; row < col; row++) {
        double[] luRow = lu[row];
        sum = luRow[col];
        for (int i = 0; i < row; i++) {
          sum -= luRow[i] * lu[i][col];
        }
        luRow[col] = sum;
      }
      

      int max = col;
      double largest = Double.NEGATIVE_INFINITY;
      for (int row = col; row < m; row++) {
        double[] luRow = lu[row];
        sum = luRow[col];
        for (int i = 0; i < col; i++) {
          sum -= luRow[i] * lu[i][col];
        }
        luRow[col] = sum;
        

        if (FastMath.abs(sum) > largest) {
          largest = FastMath.abs(sum);
          max = row;
        }
      }
      

      if (FastMath.abs(lu[max][col]) < singularityThreshold) {
        singular = true;
        return;
      }
      

      if (max != col) {
        double tmp = 0.0D;
        double[] luMax = lu[max];
        double[] luCol = lu[col];
        for (int i = 0; i < m; i++) {
          tmp = luMax[i];
          luMax[i] = luCol[i];
          luCol[i] = tmp;
        }
        int temp = pivot[max];
        pivot[max] = pivot[col];
        pivot[col] = temp;
        even = (!even);
      }
      

      double luDiag = lu[col][col];
      for (int row = col + 1; row < m; row++) {
        lu[row][col] /= luDiag;
      }
    }
  }
  

  public RealMatrix getL()
  {
    if ((cachedL == null) && (!singular)) {
      int m = pivot.length;
      cachedL = MatrixUtils.createRealMatrix(m, m);
      for (int i = 0; i < m; i++) {
        double[] luI = lu[i];
        for (int j = 0; j < i; j++) {
          cachedL.setEntry(i, j, luI[j]);
        }
        cachedL.setEntry(i, i, 1.0D);
      }
    }
    return cachedL;
  }
  
  public RealMatrix getU()
  {
    if ((cachedU == null) && (!singular)) {
      int m = pivot.length;
      cachedU = MatrixUtils.createRealMatrix(m, m);
      for (int i = 0; i < m; i++) {
        double[] luI = lu[i];
        for (int j = i; j < m; j++) {
          cachedU.setEntry(i, j, luI[j]);
        }
      }
    }
    return cachedU;
  }
  
  public RealMatrix getP()
  {
    if ((cachedP == null) && (!singular)) {
      int m = pivot.length;
      cachedP = MatrixUtils.createRealMatrix(m, m);
      for (int i = 0; i < m; i++) {
        cachedP.setEntry(i, pivot[i], 1.0D);
      }
    }
    return cachedP;
  }
  
  public int[] getPivot()
  {
    return (int[])pivot.clone();
  }
  
  public double getDeterminant()
  {
    if (singular) {
      return 0.0D;
    }
    int m = pivot.length;
    double determinant = even ? 1.0D : -1.0D;
    for (int i = 0; i < m; i++) {
      determinant *= lu[i][i];
    }
    return determinant;
  }
  

  public DecompositionSolver getSolver()
  {
    return new Solver(lu, pivot, singular, null);
  }
  



  private static class Solver
    implements DecompositionSolver
  {
    private final double[][] lu;
    


    private final int[] pivot;
    

    private final boolean singular;
    


    private Solver(double[][] lu, int[] pivot, boolean singular)
    {
      this.lu = lu;
      this.pivot = pivot;
      this.singular = singular;
    }
    
    public boolean isNonSingular()
    {
      return !singular;
    }
    

    public double[] solve(double[] b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      int m = pivot.length;
      if (b.length != m) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(b.length), Integer.valueOf(m) });
      }
      
      if (singular) {
        throw new SingularMatrixException();
      }
      
      double[] bp = new double[m];
      

      for (int row = 0; row < m; row++) {
        bp[row] = b[pivot[row]];
      }
      

      for (int col = 0; col < m; col++) {
        double bpCol = bp[col];
        for (int i = col + 1; i < m; i++) {
          bp[i] -= bpCol * lu[i][col];
        }
      }
      

      for (int col = m - 1; col >= 0; col--) {
        bp[col] /= lu[col][col];
        double bpCol = bp[col];
        for (int i = 0; i < col; i++) {
          bp[i] -= bpCol * lu[i][col];
        }
      }
      
      return bp;
    }
    
    public RealVector solve(RealVector b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      try
      {
        return solve((ArrayRealVector)b);
      }
      catch (ClassCastException cce) {
        int m = pivot.length;
        if (b.getDimension() != m) {
          throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(b.getDimension()), Integer.valueOf(m) });
        }
        
        if (singular) {
          throw new SingularMatrixException();
        }
        
        double[] bp = new double[m];
        

        for (int row = 0; row < m; row++) {
          bp[row] = b.getEntry(pivot[row]);
        }
        

        for (int col = 0; col < m; col++) {
          double bpCol = bp[col];
          for (int i = col + 1; i < m; i++) {
            bp[i] -= bpCol * lu[i][col];
          }
        }
        

        for (int col = m - 1; col >= 0; col--) {
          bp[col] /= lu[col][col];
          double bpCol = bp[col];
          for (int i = 0; i < col; i++) {
            bp[i] -= bpCol * lu[i][col];
          }
        }
        
        return new ArrayRealVector(bp, false);
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
      int m = pivot.length;
      if (b.getRowDimension() != m) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(b.getRowDimension()), Integer.valueOf(b.getColumnDimension()), Integer.valueOf(m), "n" });
      }
      

      if (singular) {
        throw new SingularMatrixException();
      }
      
      int nColB = b.getColumnDimension();
      

      double[][] bp = new double[m][nColB];
      for (int row = 0; row < m; row++) {
        double[] bpRow = bp[row];
        int pRow = pivot[row];
        for (int col = 0; col < nColB; col++) {
          bpRow[col] = b.getEntry(pRow, col);
        }
      }
      

      for (int col = 0; col < m; col++) {
        double[] bpCol = bp[col];
        for (int i = col + 1; i < m; i++) {
          double[] bpI = bp[i];
          double luICol = lu[i][col];
          for (int j = 0; j < nColB; j++) {
            bpI[j] -= bpCol[j] * luICol;
          }
        }
      }
      

      for (int col = m - 1; col >= 0; col--) {
        double[] bpCol = bp[col];
        double luDiag = lu[col][col];
        for (int j = 0; j < nColB; j++) {
          bpCol[j] /= luDiag;
        }
        for (int i = 0; i < col; i++) {
          double[] bpI = bp[i];
          double luICol = lu[i][col];
          for (int j = 0; j < nColB; j++) {
            bpI[j] -= bpCol[j] * luICol;
          }
        }
      }
      
      return new Array2DRowRealMatrix(bp, false);
    }
    
    public RealMatrix getInverse()
      throws InvalidMatrixException
    {
      return solve(MatrixUtils.createRealIdentityMatrix(pivot.length));
    }
  }
}
