package org.apache.commons.math.linear;

import java.lang.reflect.Array;
import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;




















































public class FieldLUDecompositionImpl<T extends FieldElement<T>>
  implements FieldLUDecomposition<T>
{
  private final Field<T> field;
  private T[][] lu;
  private int[] pivot;
  private boolean even;
  private boolean singular;
  private FieldMatrix<T> cachedL;
  private FieldMatrix<T> cachedU;
  private FieldMatrix<T> cachedP;
  
  public FieldLUDecompositionImpl(FieldMatrix<T> matrix)
    throws NonSquareMatrixException
  {
    if (!matrix.isSquare()) {
      throw new NonSquareMatrixException(matrix.getRowDimension(), matrix.getColumnDimension());
    }
    
    int m = matrix.getColumnDimension();
    field = matrix.getField();
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
      T sum = (FieldElement)field.getZero();
      

      for (int row = 0; row < col; row++) {
        T[] luRow = lu[row];
        sum = luRow[col];
        for (int i = 0; i < row; i++) {
          sum = (FieldElement)sum.subtract(luRow[i].multiply(lu[i][col]));
        }
        luRow[col] = sum;
      }
      

      int nonZero = col;
      for (int row = col; row < m; row++) {
        T[] luRow = lu[row];
        sum = luRow[col];
        for (int i = 0; i < col; i++) {
          sum = (FieldElement)sum.subtract(luRow[i].multiply(lu[i][col]));
        }
        luRow[col] = sum;
        
        if (lu[nonZero][col].equals(field.getZero()))
        {
          nonZero++;
        }
      }
      

      if (nonZero >= m) {
        singular = true;
        return;
      }
      

      if (nonZero != col) {
        T tmp = (FieldElement)field.getZero();
        for (int i = 0; i < m; i++) {
          tmp = lu[nonZero][i];
          lu[nonZero][i] = lu[col][i];
          lu[col][i] = tmp;
        }
        int temp = pivot[nonZero];
        pivot[nonZero] = pivot[col];
        pivot[col] = temp;
        even = (!even);
      }
      

      T luDiag = lu[col][col];
      for (int row = col + 1; row < m; row++) {
        T[] luRow = lu[row];
        luRow[col] = ((FieldElement)luRow[col].divide(luDiag));
      }
    }
  }
  

  public FieldMatrix<T> getL()
  {
    if ((cachedL == null) && (!singular)) {
      int m = pivot.length;
      cachedL = new Array2DRowFieldMatrix(field, m, m);
      for (int i = 0; i < m; i++) {
        T[] luI = lu[i];
        for (int j = 0; j < i; j++) {
          cachedL.setEntry(i, j, luI[j]);
        }
        cachedL.setEntry(i, i, (FieldElement)field.getOne());
      }
    }
    return cachedL;
  }
  
  public FieldMatrix<T> getU()
  {
    if ((cachedU == null) && (!singular)) {
      int m = pivot.length;
      cachedU = new Array2DRowFieldMatrix(field, m, m);
      for (int i = 0; i < m; i++) {
        T[] luI = lu[i];
        for (int j = i; j < m; j++) {
          cachedU.setEntry(i, j, luI[j]);
        }
      }
    }
    return cachedU;
  }
  
  public FieldMatrix<T> getP()
  {
    if ((cachedP == null) && (!singular)) {
      int m = pivot.length;
      cachedP = new Array2DRowFieldMatrix(field, m, m);
      for (int i = 0; i < m; i++) {
        cachedP.setEntry(i, pivot[i], (FieldElement)field.getOne());
      }
    }
    return cachedP;
  }
  
  public int[] getPivot()
  {
    return (int[])pivot.clone();
  }
  
  public T getDeterminant()
  {
    if (singular) {
      return (FieldElement)field.getZero();
    }
    int m = pivot.length;
    T determinant = even ? (FieldElement)field.getOne() : (FieldElement)((FieldElement)field.getZero()).subtract(field.getOne());
    for (int i = 0; i < m; i++) {
      determinant = (FieldElement)determinant.multiply(lu[i][i]);
    }
    return determinant;
  }
  

  public FieldDecompositionSolver<T> getSolver()
  {
    return new Solver(field, lu, pivot, singular, null);
  }
  



  private static class Solver<T extends FieldElement<T>>
    implements FieldDecompositionSolver<T>
  {
    private static final long serialVersionUID = -6353105415121373022L;
    


    private final Field<T> field;
    


    private final T[][] lu;
    


    private final int[] pivot;
    

    private final boolean singular;
    


    private Solver(Field<T> field, T[][] lu, int[] pivot, boolean singular)
    {
      this.field = field;
      this.lu = lu;
      this.pivot = pivot;
      this.singular = singular;
    }
    
    public boolean isNonSingular()
    {
      return !singular;
    }
    

    public T[] solve(T[] b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      int m = pivot.length;
      if (b.length != m) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(b.length), Integer.valueOf(m) });
      }
      

      if (singular) {
        throw new SingularMatrixException();
      }
      

      T[] bp = (FieldElement[])Array.newInstance(((FieldElement)field.getZero()).getClass(), m);
      

      for (int row = 0; row < m; row++) {
        bp[row] = b[pivot[row]];
      }
      

      for (int col = 0; col < m; col++) {
        T bpCol = bp[col];
        for (int i = col + 1; i < m; i++) {
          bp[i] = ((FieldElement)bp[i].subtract(bpCol.multiply(lu[i][col])));
        }
      }
      

      for (int col = m - 1; col >= 0; col--) {
        bp[col] = ((FieldElement)bp[col].divide(lu[col][col]));
        T bpCol = bp[col];
        for (int i = 0; i < col; i++) {
          bp[i] = ((FieldElement)bp[i].subtract(bpCol.multiply(lu[i][col])));
        }
      }
      
      return bp;
    }
    
    public FieldVector<T> solve(FieldVector<T> b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      try
      {
        return solve((ArrayFieldVector)b);
      }
      catch (ClassCastException cce) {
        int m = pivot.length;
        if (b.getDimension() != m) {
          throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(b.getDimension()), Integer.valueOf(m) });
        }
        

        if (singular) {
          throw new SingularMatrixException();
        }
        

        T[] bp = (FieldElement[])Array.newInstance(((FieldElement)field.getZero()).getClass(), m);
        

        for (int row = 0; row < m; row++) {
          bp[row] = b.getEntry(pivot[row]);
        }
        

        for (int col = 0; col < m; col++) {
          T bpCol = bp[col];
          for (int i = col + 1; i < m; i++) {
            bp[i] = ((FieldElement)bp[i].subtract(bpCol.multiply(lu[i][col])));
          }
        }
        

        for (int col = m - 1; col >= 0; col--) {
          bp[col] = ((FieldElement)bp[col].divide(lu[col][col]));
          T bpCol = bp[col];
          for (int i = 0; i < col; i++) {
            bp[i] = ((FieldElement)bp[i].subtract(bpCol.multiply(lu[i][col])));
          }
        }
        
        return new ArrayFieldVector(bp, false);
      }
    }
    







    public ArrayFieldVector<T> solve(ArrayFieldVector<T> b)
      throws IllegalArgumentException, InvalidMatrixException
    {
      return new ArrayFieldVector(solve(b.getDataRef()), false);
    }
    

    public FieldMatrix<T> solve(FieldMatrix<T> b)
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
      


      T[][] bp = (FieldElement[][])Array.newInstance(((FieldElement)field.getZero()).getClass(), new int[] { m, nColB });
      for (int row = 0; row < m; row++) {
        T[] bpRow = bp[row];
        int pRow = pivot[row];
        for (int col = 0; col < nColB; col++) {
          bpRow[col] = b.getEntry(pRow, col);
        }
      }
      

      for (int col = 0; col < m; col++) {
        T[] bpCol = bp[col];
        for (int i = col + 1; i < m; i++) {
          T[] bpI = bp[i];
          T luICol = lu[i][col];
          for (int j = 0; j < nColB; j++) {
            bpI[j] = ((FieldElement)bpI[j].subtract(bpCol[j].multiply(luICol)));
          }
        }
      }
      

      for (int col = m - 1; col >= 0; col--) {
        T[] bpCol = bp[col];
        T luDiag = lu[col][col];
        for (int j = 0; j < nColB; j++) {
          bpCol[j] = ((FieldElement)bpCol[j].divide(luDiag));
        }
        for (int i = 0; i < col; i++) {
          T[] bpI = bp[i];
          T luICol = lu[i][col];
          for (int j = 0; j < nColB; j++) {
            bpI[j] = ((FieldElement)bpI[j].subtract(bpCol[j].multiply(luICol)));
          }
        }
      }
      
      return new Array2DRowFieldMatrix(bp, false);
    }
    
    public FieldMatrix<T> getInverse()
      throws InvalidMatrixException
    {
      int m = pivot.length;
      T one = (FieldElement)field.getOne();
      FieldMatrix<T> identity = new Array2DRowFieldMatrix(field, m, m);
      for (int i = 0; i < m; i++) {
        identity.setEntry(i, i, one);
      }
      return solve(identity);
    }
  }
}
