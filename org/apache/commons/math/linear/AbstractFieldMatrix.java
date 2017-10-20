package org.apache.commons.math.linear;

import java.lang.reflect.Array;
import java.util.Arrays;
import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;































public abstract class AbstractFieldMatrix<T extends FieldElement<T>>
  implements FieldMatrix<T>
{
  private final Field<T> field;
  
  protected AbstractFieldMatrix()
  {
    field = null;
  }
  



  protected AbstractFieldMatrix(Field<T> field)
  {
    this.field = field;
  }
  








  protected AbstractFieldMatrix(Field<T> field, int rowDimension, int columnDimension)
    throws IllegalArgumentException
  {
    if (rowDimension < 1) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { Integer.valueOf(rowDimension), Integer.valueOf(1) });
    }
    
    if (columnDimension < 1) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { Integer.valueOf(columnDimension), Integer.valueOf(1) });
    }
    
    this.field = field;
  }
  






  protected static <T extends FieldElement<T>> Field<T> extractField(T[][] d)
    throws IllegalArgumentException
  {
    if (d.length == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_ROW, new Object[0]);
    }
    if (d[0].length == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_COLUMN, new Object[0]);
    }
    return d[0][0].getField();
  }
  






  protected static <T extends FieldElement<T>> Field<T> extractField(T[] d)
    throws IllegalArgumentException
  {
    if (d.length == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_ROW, new Object[0]);
    }
    return d[0].getField();
  }
  













  protected static <T extends FieldElement<T>> T[][] buildArray(Field<T> field, int rows, int columns)
  {
    if (columns < 0) {
      T[] dummyRow = (FieldElement[])Array.newInstance(((FieldElement)field.getZero()).getClass(), 0);
      return (FieldElement[][])Array.newInstance(dummyRow.getClass(), rows);
    }
    T[][] array = (FieldElement[][])Array.newInstance(((FieldElement)field.getZero()).getClass(), new int[] { rows, columns });
    
    for (int i = 0; i < array.length; i++) {
      Arrays.fill(array[i], field.getZero());
    }
    return array;
  }
  










  protected static <T extends FieldElement<T>> T[] buildArray(Field<T> field, int length)
  {
    T[] array = (FieldElement[])Array.newInstance(((FieldElement)field.getZero()).getClass(), length);
    Arrays.fill(array, field.getZero());
    return array;
  }
  
  public Field<T> getField()
  {
    return field;
  }
  

  public abstract FieldMatrix<T> createMatrix(int paramInt1, int paramInt2)
    throws IllegalArgumentException;
  

  public abstract FieldMatrix<T> copy();
  

  public FieldMatrix<T> add(FieldMatrix<T> m)
    throws IllegalArgumentException
  {
    checkAdditionCompatible(m);
    
    int rowCount = getRowDimension();
    int columnCount = getColumnDimension();
    FieldMatrix<T> out = createMatrix(rowCount, columnCount);
    for (int row = 0; row < rowCount; row++) {
      for (int col = 0; col < columnCount; col++) {
        out.setEntry(row, col, (FieldElement)getEntry(row, col).add(m.getEntry(row, col)));
      }
    }
    
    return out;
  }
  


  public FieldMatrix<T> subtract(FieldMatrix<T> m)
    throws IllegalArgumentException
  {
    checkSubtractionCompatible(m);
    
    int rowCount = getRowDimension();
    int columnCount = getColumnDimension();
    FieldMatrix<T> out = createMatrix(rowCount, columnCount);
    for (int row = 0; row < rowCount; row++) {
      for (int col = 0; col < columnCount; col++) {
        out.setEntry(row, col, (FieldElement)getEntry(row, col).subtract(m.getEntry(row, col)));
      }
    }
    
    return out;
  }
  


  public FieldMatrix<T> scalarAdd(T d)
  {
    int rowCount = getRowDimension();
    int columnCount = getColumnDimension();
    FieldMatrix<T> out = createMatrix(rowCount, columnCount);
    for (int row = 0; row < rowCount; row++) {
      for (int col = 0; col < columnCount; col++) {
        out.setEntry(row, col, (FieldElement)getEntry(row, col).add(d));
      }
    }
    
    return out;
  }
  


  public FieldMatrix<T> scalarMultiply(T d)
  {
    int rowCount = getRowDimension();
    int columnCount = getColumnDimension();
    FieldMatrix<T> out = createMatrix(rowCount, columnCount);
    for (int row = 0; row < rowCount; row++) {
      for (int col = 0; col < columnCount; col++) {
        out.setEntry(row, col, (FieldElement)getEntry(row, col).multiply(d));
      }
    }
    
    return out;
  }
  



  public FieldMatrix<T> multiply(FieldMatrix<T> m)
    throws IllegalArgumentException
  {
    checkMultiplicationCompatible(m);
    
    int nRows = getRowDimension();
    int nCols = m.getColumnDimension();
    int nSum = getColumnDimension();
    FieldMatrix<T> out = createMatrix(nRows, nCols);
    for (int row = 0; row < nRows; row++) {
      for (int col = 0; col < nCols; col++) {
        T sum = (FieldElement)field.getZero();
        for (int i = 0; i < nSum; i++) {
          sum = (FieldElement)sum.add(getEntry(row, i).multiply(m.getEntry(i, col)));
        }
        out.setEntry(row, col, sum);
      }
    }
    
    return out;
  }
  

  public FieldMatrix<T> preMultiply(FieldMatrix<T> m)
    throws IllegalArgumentException
  {
    return m.multiply(this);
  }
  

  public T[][] getData()
  {
    T[][] data = buildArray(field, getRowDimension(), getColumnDimension());
    
    for (int i = 0; i < data.length; i++) {
      T[] dataI = data[i];
      for (int j = 0; j < dataI.length; j++) {
        dataI[j] = getEntry(i, j);
      }
    }
    
    return data;
  }
  



  public FieldMatrix<T> getSubMatrix(int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException
  {
    checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
    
    FieldMatrix<T> subMatrix = createMatrix(endRow - startRow + 1, endColumn - startColumn + 1);
    
    for (int i = startRow; i <= endRow; i++) {
      for (int j = startColumn; j <= endColumn; j++) {
        subMatrix.setEntry(i - startRow, j - startColumn, getEntry(i, j));
      }
    }
    
    return subMatrix;
  }
  



  public FieldMatrix<T> getSubMatrix(final int[] selectedRows, final int[] selectedColumns)
    throws MatrixIndexException
  {
    checkSubMatrixIndex(selectedRows, selectedColumns);
    

    FieldMatrix<T> subMatrix = createMatrix(selectedRows.length, selectedColumns.length);
    
    subMatrix.walkInOptimizedOrder(new DefaultFieldMatrixChangingVisitor((FieldElement)field.getZero())
    {

      public T visit(int row, int column, T value)
      {
        return getEntry(selectedRows[row], selectedColumns[column]);
      }
      

    });
    return subMatrix;
  }
  





  public void copySubMatrix(int startRow, int endRow, int startColumn, int endColumn, final T[][] destination)
    throws MatrixIndexException, IllegalArgumentException
  {
    checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
    int rowsCount = endRow + 1 - startRow;
    int columnsCount = endColumn + 1 - startColumn;
    if ((destination.length < rowsCount) || (destination[0].length < columnsCount)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(destination.length), Integer.valueOf(destination[0].length), Integer.valueOf(rowsCount), Integer.valueOf(columnsCount) });
    }
    




    walkInOptimizedOrder(new DefaultFieldMatrixPreservingVisitor((FieldElement)field.getZero())
    {
      private int startRow;
      


      private int startColumn;
      



      public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn)
      {
        this.startRow = startRow;
        this.startColumn = startColumn;
      }
      



      public void visit(int row, int column, T value) { destination[(row - startRow)][(column - startColumn)] = value; } }, startRow, endRow, startColumn, endColumn);
  }
  






  public void copySubMatrix(int[] selectedRows, int[] selectedColumns, T[][] destination)
    throws MatrixIndexException, IllegalArgumentException
  {
    checkSubMatrixIndex(selectedRows, selectedColumns);
    if ((destination.length < selectedRows.length) || (destination[0].length < selectedColumns.length))
    {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(destination.length), Integer.valueOf(destination[0].length), Integer.valueOf(selectedRows.length), Integer.valueOf(selectedColumns.length) });
    }
    




    for (int i = 0; i < selectedRows.length; i++) {
      T[] destinationI = destination[i];
      for (int j = 0; j < selectedColumns.length; j++) {
        destinationI[j] = getEntry(selectedRows[i], selectedColumns[j]);
      }
    }
  }
  


  public void setSubMatrix(T[][] subMatrix, int row, int column)
    throws MatrixIndexException
  {
    int nRows = subMatrix.length;
    if (nRows == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_ROW, new Object[0]);
    }
    
    int nCols = subMatrix[0].length;
    if (nCols == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_COLUMN, new Object[0]);
    }
    
    for (int r = 1; r < nRows; r++) {
      if (subMatrix[r].length != nCols) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIFFERENT_ROWS_LENGTHS, new Object[] { Integer.valueOf(nCols), Integer.valueOf(subMatrix[r].length) });
      }
    }
    


    checkRowIndex(row);
    checkColumnIndex(column);
    checkRowIndex(nRows + row - 1);
    checkColumnIndex(nCols + column - 1);
    
    for (int i = 0; i < nRows; i++) {
      for (int j = 0; j < nCols; j++) {
        setEntry(row + i, column + j, subMatrix[i][j]);
      }
    }
  }
  


  public FieldMatrix<T> getRowMatrix(int row)
    throws MatrixIndexException
  {
    checkRowIndex(row);
    int nCols = getColumnDimension();
    FieldMatrix<T> out = createMatrix(1, nCols);
    for (int i = 0; i < nCols; i++) {
      out.setEntry(0, i, getEntry(row, i));
    }
    
    return out;
  }
  


  public void setRowMatrix(int row, FieldMatrix<T> matrix)
    throws MatrixIndexException, InvalidMatrixException
  {
    checkRowIndex(row);
    int nCols = getColumnDimension();
    if ((matrix.getRowDimension() != 1) || (matrix.getColumnDimension() != nCols))
    {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(matrix.getRowDimension()), Integer.valueOf(matrix.getColumnDimension()), Integer.valueOf(1), Integer.valueOf(nCols) });
    }
    

    for (int i = 0; i < nCols; i++) {
      setEntry(row, i, matrix.getEntry(0, i));
    }
  }
  


  public FieldMatrix<T> getColumnMatrix(int column)
    throws MatrixIndexException
  {
    checkColumnIndex(column);
    int nRows = getRowDimension();
    FieldMatrix<T> out = createMatrix(nRows, 1);
    for (int i = 0; i < nRows; i++) {
      out.setEntry(i, 0, getEntry(i, column));
    }
    
    return out;
  }
  


  public void setColumnMatrix(int column, FieldMatrix<T> matrix)
    throws MatrixIndexException, InvalidMatrixException
  {
    checkColumnIndex(column);
    int nRows = getRowDimension();
    if ((matrix.getRowDimension() != nRows) || (matrix.getColumnDimension() != 1))
    {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(matrix.getRowDimension()), Integer.valueOf(matrix.getColumnDimension()), Integer.valueOf(nRows), Integer.valueOf(1) });
    }
    

    for (int i = 0; i < nRows; i++) {
      setEntry(i, column, matrix.getEntry(i, 0));
    }
  }
  

  public FieldVector<T> getRowVector(int row)
    throws MatrixIndexException
  {
    return new ArrayFieldVector(getRow(row), false);
  }
  

  public void setRowVector(int row, FieldVector<T> vector)
    throws MatrixIndexException, InvalidMatrixException
  {
    checkRowIndex(row);
    int nCols = getColumnDimension();
    if (vector.getDimension() != nCols) {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(1), Integer.valueOf(vector.getDimension()), Integer.valueOf(1), Integer.valueOf(nCols) });
    }
    

    for (int i = 0; i < nCols; i++) {
      setEntry(row, i, vector.getEntry(i));
    }
  }
  

  public FieldVector<T> getColumnVector(int column)
    throws MatrixIndexException
  {
    return new ArrayFieldVector(getColumn(column), false);
  }
  

  public void setColumnVector(int column, FieldVector<T> vector)
    throws MatrixIndexException, InvalidMatrixException
  {
    checkColumnIndex(column);
    int nRows = getRowDimension();
    if (vector.getDimension() != nRows) {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(vector.getDimension()), Integer.valueOf(1), Integer.valueOf(nRows), Integer.valueOf(1) });
    }
    

    for (int i = 0; i < nRows; i++) {
      setEntry(i, column, vector.getEntry(i));
    }
  }
  


  public T[] getRow(int row)
    throws MatrixIndexException
  {
    checkRowIndex(row);
    int nCols = getColumnDimension();
    T[] out = buildArray(field, nCols);
    for (int i = 0; i < nCols; i++) {
      out[i] = getEntry(row, i);
    }
    
    return out;
  }
  


  public void setRow(int row, T[] array)
    throws MatrixIndexException, InvalidMatrixException
  {
    checkRowIndex(row);
    int nCols = getColumnDimension();
    if (array.length != nCols) {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(1), Integer.valueOf(array.length), Integer.valueOf(1), Integer.valueOf(nCols) });
    }
    

    for (int i = 0; i < nCols; i++) {
      setEntry(row, i, array[i]);
    }
  }
  


  public T[] getColumn(int column)
    throws MatrixIndexException
  {
    checkColumnIndex(column);
    int nRows = getRowDimension();
    T[] out = buildArray(field, nRows);
    for (int i = 0; i < nRows; i++) {
      out[i] = getEntry(i, column);
    }
    
    return out;
  }
  


  public void setColumn(int column, T[] array)
    throws MatrixIndexException, InvalidMatrixException
  {
    checkColumnIndex(column);
    int nRows = getRowDimension();
    if (array.length != nRows) {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(array.length), Integer.valueOf(1), Integer.valueOf(nRows), Integer.valueOf(1) });
    }
    

    for (int i = 0; i < nRows; i++) {
      setEntry(i, column, array[i]);
    }
  }
  


  public abstract T getEntry(int paramInt1, int paramInt2)
    throws MatrixIndexException;
  

  public abstract void setEntry(int paramInt1, int paramInt2, T paramT)
    throws MatrixIndexException;
  

  public abstract void addToEntry(int paramInt1, int paramInt2, T paramT)
    throws MatrixIndexException;
  

  public abstract void multiplyEntry(int paramInt1, int paramInt2, T paramT)
    throws MatrixIndexException;
  

  public FieldMatrix<T> transpose()
  {
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    final FieldMatrix<T> out = createMatrix(nCols, nRows);
    walkInOptimizedOrder(new DefaultFieldMatrixPreservingVisitor((FieldElement)field.getZero())
    {

      public void visit(int row, int column, T value)
      {
        out.setEntry(column, row, value);
      }
      

    });
    return out;
  }
  

  public boolean isSquare()
  {
    return getColumnDimension() == getRowDimension();
  }
  

  public abstract int getRowDimension();
  

  public abstract int getColumnDimension();
  
  public T getTrace()
    throws NonSquareMatrixException
  {
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    if (nRows != nCols) {
      throw new NonSquareMatrixException(nRows, nCols);
    }
    T trace = (FieldElement)field.getZero();
    for (int i = 0; i < nRows; i++) {
      trace = (FieldElement)trace.add(getEntry(i, i));
    }
    return trace;
  }
  

  public T[] operate(T[] v)
    throws IllegalArgumentException
  {
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    if (v.length != nCols) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(v.length), Integer.valueOf(nCols) });
    }
    


    T[] out = buildArray(field, nRows);
    for (int row = 0; row < nRows; row++) {
      T sum = (FieldElement)field.getZero();
      for (int i = 0; i < nCols; i++) {
        sum = (FieldElement)sum.add(getEntry(row, i).multiply(v[i]));
      }
      out[row] = sum;
    }
    
    return out;
  }
  
  public FieldVector<T> operate(FieldVector<T> v)
    throws IllegalArgumentException
  {
    try
    {
      return new ArrayFieldVector(operate(((ArrayFieldVector)v).getDataRef()), false);
    } catch (ClassCastException cce) {
      int nRows = getRowDimension();
      int nCols = getColumnDimension();
      if (v.getDimension() != nCols) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(v.getDimension()), Integer.valueOf(nCols) });
      }
      


      T[] out = buildArray(field, nRows);
      for (int row = 0; row < nRows; row++) {
        T sum = (FieldElement)field.getZero();
        for (int i = 0; i < nCols; i++) {
          sum = (FieldElement)sum.add(getEntry(row, i).multiply(v.getEntry(i)));
        }
        out[row] = sum;
      }
      
      return new ArrayFieldVector(out, false);
    }
  }
  

  public T[] preMultiply(T[] v)
    throws IllegalArgumentException
  {
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    if (v.length != nRows) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(v.length), Integer.valueOf(nRows) });
    }
    


    T[] out = buildArray(field, nCols);
    for (int col = 0; col < nCols; col++) {
      T sum = (FieldElement)field.getZero();
      for (int i = 0; i < nRows; i++) {
        sum = (FieldElement)sum.add(getEntry(i, col).multiply(v[i]));
      }
      out[col] = sum;
    }
    
    return out;
  }
  
  public FieldVector<T> preMultiply(FieldVector<T> v)
    throws IllegalArgumentException
  {
    try
    {
      return new ArrayFieldVector(preMultiply(((ArrayFieldVector)v).getDataRef()), false);
    }
    catch (ClassCastException cce) {
      int nRows = getRowDimension();
      int nCols = getColumnDimension();
      if (v.getDimension() != nRows) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(v.getDimension()), Integer.valueOf(nRows) });
      }
      


      T[] out = buildArray(field, nCols);
      for (int col = 0; col < nCols; col++) {
        T sum = (FieldElement)field.getZero();
        for (int i = 0; i < nRows; i++) {
          sum = (FieldElement)sum.add(getEntry(i, col).multiply(v.getEntry(i)));
        }
        out[col] = sum;
      }
      
      return new ArrayFieldVector(out);
    }
  }
  

  public T walkInRowOrder(FieldMatrixChangingVisitor<T> visitor)
    throws MatrixVisitorException
  {
    int rows = getRowDimension();
    int columns = getColumnDimension();
    visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        T oldValue = getEntry(row, column);
        T newValue = visitor.visit(row, column, oldValue);
        setEntry(row, column, newValue);
      }
    }
    return visitor.end();
  }
  
  public T walkInRowOrder(FieldMatrixPreservingVisitor<T> visitor)
    throws MatrixVisitorException
  {
    int rows = getRowDimension();
    int columns = getColumnDimension();
    visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        visitor.visit(row, column, getEntry(row, column));
      }
    }
    return visitor.end();
  }
  


  public T walkInRowOrder(FieldMatrixChangingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
    visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
    
    for (int row = startRow; row <= endRow; row++) {
      for (int column = startColumn; column <= endColumn; column++) {
        T oldValue = getEntry(row, column);
        T newValue = visitor.visit(row, column, oldValue);
        setEntry(row, column, newValue);
      }
    }
    return visitor.end();
  }
  


  public T walkInRowOrder(FieldMatrixPreservingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
    visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
    
    for (int row = startRow; row <= endRow; row++) {
      for (int column = startColumn; column <= endColumn; column++) {
        visitor.visit(row, column, getEntry(row, column));
      }
    }
    return visitor.end();
  }
  
  public T walkInColumnOrder(FieldMatrixChangingVisitor<T> visitor)
    throws MatrixVisitorException
  {
    int rows = getRowDimension();
    int columns = getColumnDimension();
    visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
    for (int column = 0; column < columns; column++) {
      for (int row = 0; row < rows; row++) {
        T oldValue = getEntry(row, column);
        T newValue = visitor.visit(row, column, oldValue);
        setEntry(row, column, newValue);
      }
    }
    return visitor.end();
  }
  
  public T walkInColumnOrder(FieldMatrixPreservingVisitor<T> visitor)
    throws MatrixVisitorException
  {
    int rows = getRowDimension();
    int columns = getColumnDimension();
    visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
    for (int column = 0; column < columns; column++) {
      for (int row = 0; row < rows; row++) {
        visitor.visit(row, column, getEntry(row, column));
      }
    }
    return visitor.end();
  }
  


  public T walkInColumnOrder(FieldMatrixChangingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
    visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
    
    for (int column = startColumn; column <= endColumn; column++) {
      for (int row = startRow; row <= endRow; row++) {
        T oldValue = getEntry(row, column);
        T newValue = visitor.visit(row, column, oldValue);
        setEntry(row, column, newValue);
      }
    }
    return visitor.end();
  }
  


  public T walkInColumnOrder(FieldMatrixPreservingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    checkSubMatrixIndex(startRow, endRow, startColumn, endColumn);
    visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
    
    for (int column = startColumn; column <= endColumn; column++) {
      for (int row = startRow; row <= endRow; row++) {
        visitor.visit(row, column, getEntry(row, column));
      }
    }
    return visitor.end();
  }
  
  public T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> visitor)
    throws MatrixVisitorException
  {
    return walkInRowOrder(visitor);
  }
  
  public T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> visitor)
    throws MatrixVisitorException
  {
    return walkInRowOrder(visitor);
  }
  


  public T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
  }
  


  public T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
  }
  




  public String toString()
  {
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    StringBuilder res = new StringBuilder();
    String fullClassName = getClass().getName();
    String shortClassName = fullClassName.substring(fullClassName.lastIndexOf('.') + 1);
    res.append(shortClassName).append("{");
    
    for (int i = 0; i < nRows; i++) {
      if (i > 0) {
        res.append(",");
      }
      res.append("{");
      for (int j = 0; j < nCols; j++) {
        if (j > 0) {
          res.append(",");
        }
        res.append(getEntry(i, j));
      }
      res.append("}");
    }
    
    res.append("}");
    return res.toString();
  }
  









  public boolean equals(Object object)
  {
    if (object == this) {
      return true;
    }
    if (!(object instanceof FieldMatrix)) {
      return false;
    }
    FieldMatrix<?> m = (FieldMatrix)object;
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    if ((m.getColumnDimension() != nCols) || (m.getRowDimension() != nRows)) {
      return false;
    }
    for (int row = 0; row < nRows; row++) {
      for (int col = 0; col < nCols; col++) {
        if (!getEntry(row, col).equals(m.getEntry(row, col))) {
          return false;
        }
      }
    }
    return true;
  }
  





  public int hashCode()
  {
    int ret = 322562;
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    ret = ret * 31 + nRows;
    ret = ret * 31 + nCols;
    for (int row = 0; row < nRows; row++) {
      for (int col = 0; col < nCols; col++) {
        ret = ret * 31 + (11 * (row + 1) + 17 * (col + 1)) * getEntry(row, col).hashCode();
      }
    }
    return ret;
  }
  




  protected void checkRowIndex(int row)
  {
    if ((row < 0) || (row >= getRowDimension())) {
      throw new MatrixIndexException(LocalizedFormats.ROW_INDEX_OUT_OF_RANGE, new Object[] { Integer.valueOf(row), Integer.valueOf(0), Integer.valueOf(getRowDimension() - 1) });
    }
  }
  





  protected void checkColumnIndex(int column)
    throws MatrixIndexException
  {
    if ((column < 0) || (column >= getColumnDimension())) {
      throw new MatrixIndexException(LocalizedFormats.COLUMN_INDEX_OUT_OF_RANGE, new Object[] { Integer.valueOf(column), Integer.valueOf(0), Integer.valueOf(getColumnDimension() - 1) });
    }
  }
  











  protected void checkSubMatrixIndex(int startRow, int endRow, int startColumn, int endColumn)
  {
    checkRowIndex(startRow);
    checkRowIndex(endRow);
    if (startRow > endRow) {
      throw new MatrixIndexException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW, new Object[] { Integer.valueOf(startRow), Integer.valueOf(endRow) });
    }
    

    checkColumnIndex(startColumn);
    checkColumnIndex(endColumn);
    if (startColumn > endColumn) {
      throw new MatrixIndexException(LocalizedFormats.INITIAL_COLUMN_AFTER_FINAL_COLUMN, new Object[] { Integer.valueOf(startColumn), Integer.valueOf(endColumn) });
    }
  }
  










  protected void checkSubMatrixIndex(int[] selectedRows, int[] selectedColumns)
  {
    if (selectedRows.length * selectedColumns.length == 0) {
      if (selectedRows.length == 0) {
        throw new MatrixIndexException(LocalizedFormats.EMPTY_SELECTED_ROW_INDEX_ARRAY, new Object[0]);
      }
      throw new MatrixIndexException(LocalizedFormats.EMPTY_SELECTED_COLUMN_INDEX_ARRAY, new Object[0]);
    }
    
    for (int row : selectedRows) {
      checkRowIndex(row);
    }
    for (int column : selectedColumns) {
      checkColumnIndex(column);
    }
  }
  




  protected void checkAdditionCompatible(FieldMatrix<T> m)
  {
    if ((getRowDimension() != m.getRowDimension()) || (getColumnDimension() != m.getColumnDimension()))
    {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_ADDITION_COMPATIBLE_MATRICES, new Object[] { Integer.valueOf(getRowDimension()), Integer.valueOf(getColumnDimension()), Integer.valueOf(m.getRowDimension()), Integer.valueOf(m.getColumnDimension()) });
    }
  }
  







  protected void checkSubtractionCompatible(FieldMatrix<T> m)
  {
    if ((getRowDimension() != m.getRowDimension()) || (getColumnDimension() != m.getColumnDimension()))
    {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_SUBTRACTION_COMPATIBLE_MATRICES, new Object[] { Integer.valueOf(getRowDimension()), Integer.valueOf(getColumnDimension()), Integer.valueOf(m.getRowDimension()), Integer.valueOf(m.getColumnDimension()) });
    }
  }
  







  protected void checkMultiplicationCompatible(FieldMatrix<T> m)
  {
    if (getColumnDimension() != m.getRowDimension()) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_MULTIPLICATION_COMPATIBLE_MATRICES, new Object[] { Integer.valueOf(getRowDimension()), Integer.valueOf(getColumnDimension()), Integer.valueOf(m.getRowDimension()), Integer.valueOf(m.getColumnDimension()) });
    }
  }
}
