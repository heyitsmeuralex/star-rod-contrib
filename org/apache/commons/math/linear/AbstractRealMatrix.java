package org.apache.commons.math.linear;

import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.FastMath;
import org.apache.commons.math.util.MathUtils;
































public abstract class AbstractRealMatrix
  implements RealMatrix
{
  @Deprecated
  private DecompositionSolver lu;
  
  protected AbstractRealMatrix()
  {
    lu = null;
  }
  






  protected AbstractRealMatrix(int rowDimension, int columnDimension)
    throws IllegalArgumentException
  {
    if (rowDimension < 1) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { Integer.valueOf(rowDimension), Integer.valueOf(1) });
    }
    
    if (columnDimension <= 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { Integer.valueOf(columnDimension), Integer.valueOf(1) });
    }
    
    lu = null;
  }
  

  public abstract RealMatrix createMatrix(int paramInt1, int paramInt2)
    throws IllegalArgumentException;
  

  public abstract RealMatrix copy();
  

  public RealMatrix add(RealMatrix m)
    throws IllegalArgumentException
  {
    MatrixUtils.checkAdditionCompatible(this, m);
    
    int rowCount = getRowDimension();
    int columnCount = getColumnDimension();
    RealMatrix out = createMatrix(rowCount, columnCount);
    for (int row = 0; row < rowCount; row++) {
      for (int col = 0; col < columnCount; col++) {
        out.setEntry(row, col, getEntry(row, col) + m.getEntry(row, col));
      }
    }
    
    return out;
  }
  


  public RealMatrix subtract(RealMatrix m)
    throws IllegalArgumentException
  {
    MatrixUtils.checkSubtractionCompatible(this, m);
    
    int rowCount = getRowDimension();
    int columnCount = getColumnDimension();
    RealMatrix out = createMatrix(rowCount, columnCount);
    for (int row = 0; row < rowCount; row++) {
      for (int col = 0; col < columnCount; col++) {
        out.setEntry(row, col, getEntry(row, col) - m.getEntry(row, col));
      }
    }
    
    return out;
  }
  


  public RealMatrix scalarAdd(double d)
  {
    int rowCount = getRowDimension();
    int columnCount = getColumnDimension();
    RealMatrix out = createMatrix(rowCount, columnCount);
    for (int row = 0; row < rowCount; row++) {
      for (int col = 0; col < columnCount; col++) {
        out.setEntry(row, col, getEntry(row, col) + d);
      }
    }
    
    return out;
  }
  


  public RealMatrix scalarMultiply(double d)
  {
    int rowCount = getRowDimension();
    int columnCount = getColumnDimension();
    RealMatrix out = createMatrix(rowCount, columnCount);
    for (int row = 0; row < rowCount; row++) {
      for (int col = 0; col < columnCount; col++) {
        out.setEntry(row, col, getEntry(row, col) * d);
      }
    }
    
    return out;
  }
  



  public RealMatrix multiply(RealMatrix m)
    throws IllegalArgumentException
  {
    MatrixUtils.checkMultiplicationCompatible(this, m);
    
    int nRows = getRowDimension();
    int nCols = m.getColumnDimension();
    int nSum = getColumnDimension();
    RealMatrix out = createMatrix(nRows, nCols);
    for (int row = 0; row < nRows; row++) {
      for (int col = 0; col < nCols; col++) {
        double sum = 0.0D;
        for (int i = 0; i < nSum; i++) {
          sum += getEntry(row, i) * m.getEntry(i, col);
        }
        out.setEntry(row, col, sum);
      }
    }
    
    return out;
  }
  

  public RealMatrix preMultiply(RealMatrix m)
    throws IllegalArgumentException
  {
    return m.multiply(this);
  }
  

  public double[][] getData()
  {
    double[][] data = new double[getRowDimension()][getColumnDimension()];
    
    for (int i = 0; i < data.length; i++) {
      double[] dataI = data[i];
      for (int j = 0; j < dataI.length; j++) {
        dataI[j] = getEntry(i, j);
      }
    }
    
    return data;
  }
  

  public double getNorm()
  {
    walkInColumnOrder(new RealMatrixPreservingVisitor()
    {
      private double endRow;
      


      private double columnSum;
      

      private double maxColSum;
      


      public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn)
      {
        this.endRow = endRow;
        columnSum = 0.0D;
        maxColSum = 0.0D;
      }
      
      public void visit(int row, int column, double value)
      {
        columnSum += FastMath.abs(value);
        if (row == endRow) {
          maxColSum = FastMath.max(maxColSum, columnSum);
          columnSum = 0.0D;
        }
      }
      
      public double end()
      {
        return maxColSum;
      }
    });
  }
  

  public double getFrobeniusNorm()
  {
    walkInOptimizedOrder(new RealMatrixPreservingVisitor()
    {
      private double sum;
      



      public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn)
      {
        sum = 0.0D;
      }
      
      public void visit(int row, int column, double value)
      {
        sum += value * value;
      }
      
      public double end()
      {
        return FastMath.sqrt(sum);
      }
    });
  }
  



  public RealMatrix getSubMatrix(int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException
  {
    MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
    
    RealMatrix subMatrix = createMatrix(endRow - startRow + 1, endColumn - startColumn + 1);
    
    for (int i = startRow; i <= endRow; i++) {
      for (int j = startColumn; j <= endColumn; j++) {
        subMatrix.setEntry(i - startRow, j - startColumn, getEntry(i, j));
      }
    }
    
    return subMatrix;
  }
  



  public RealMatrix getSubMatrix(final int[] selectedRows, final int[] selectedColumns)
    throws MatrixIndexException
  {
    MatrixUtils.checkSubMatrixIndex(this, selectedRows, selectedColumns);
    

    RealMatrix subMatrix = createMatrix(selectedRows.length, selectedColumns.length);
    
    subMatrix.walkInOptimizedOrder(new DefaultRealMatrixChangingVisitor()
    {

      public double visit(int row, int column, double value)
      {
        return getEntry(selectedRows[row], selectedColumns[column]);
      }
      

    });
    return subMatrix;
  }
  





  public void copySubMatrix(int startRow, int endRow, int startColumn, int endColumn, final double[][] destination)
    throws MatrixIndexException, IllegalArgumentException
  {
    MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
    int rowsCount = endRow + 1 - startRow;
    int columnsCount = endColumn + 1 - startColumn;
    if ((destination.length < rowsCount) || (destination[0].length < columnsCount)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(destination.length), Integer.valueOf(destination[0].length), Integer.valueOf(rowsCount), Integer.valueOf(columnsCount) });
    }
    




    walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor()
    {
      private int startRow;
      


      private int startColumn;
      



      public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn)
      {
        this.startRow = startRow;
        this.startColumn = startColumn;
      }
      



      public void visit(int row, int column, double value) { destination[(row - startRow)][(column - startColumn)] = value; } }, startRow, endRow, startColumn, endColumn);
  }
  






  public void copySubMatrix(int[] selectedRows, int[] selectedColumns, double[][] destination)
    throws MatrixIndexException, IllegalArgumentException
  {
    MatrixUtils.checkSubMatrixIndex(this, selectedRows, selectedColumns);
    if ((destination.length < selectedRows.length) || (destination[0].length < selectedColumns.length))
    {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(destination.length), Integer.valueOf(destination[0].length), Integer.valueOf(selectedRows.length), Integer.valueOf(selectedColumns.length) });
    }
    




    for (int i = 0; i < selectedRows.length; i++) {
      double[] destinationI = destination[i];
      for (int j = 0; j < selectedColumns.length; j++) {
        destinationI[j] = getEntry(selectedRows[i], selectedColumns[j]);
      }
    }
  }
  


  public void setSubMatrix(double[][] subMatrix, int row, int column)
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
    


    MatrixUtils.checkRowIndex(this, row);
    MatrixUtils.checkColumnIndex(this, column);
    MatrixUtils.checkRowIndex(this, nRows + row - 1);
    MatrixUtils.checkColumnIndex(this, nCols + column - 1);
    
    for (int i = 0; i < nRows; i++) {
      for (int j = 0; j < nCols; j++) {
        setEntry(row + i, column + j, subMatrix[i][j]);
      }
    }
    
    lu = null;
  }
  


  public RealMatrix getRowMatrix(int row)
    throws MatrixIndexException
  {
    MatrixUtils.checkRowIndex(this, row);
    int nCols = getColumnDimension();
    RealMatrix out = createMatrix(1, nCols);
    for (int i = 0; i < nCols; i++) {
      out.setEntry(0, i, getEntry(row, i));
    }
    
    return out;
  }
  


  public void setRowMatrix(int row, RealMatrix matrix)
    throws MatrixIndexException, InvalidMatrixException
  {
    MatrixUtils.checkRowIndex(this, row);
    int nCols = getColumnDimension();
    if ((matrix.getRowDimension() != 1) || (matrix.getColumnDimension() != nCols))
    {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(matrix.getRowDimension()), Integer.valueOf(matrix.getColumnDimension()), Integer.valueOf(1), Integer.valueOf(nCols) });
    }
    

    for (int i = 0; i < nCols; i++) {
      setEntry(row, i, matrix.getEntry(0, i));
    }
  }
  


  public RealMatrix getColumnMatrix(int column)
    throws MatrixIndexException
  {
    MatrixUtils.checkColumnIndex(this, column);
    int nRows = getRowDimension();
    RealMatrix out = createMatrix(nRows, 1);
    for (int i = 0; i < nRows; i++) {
      out.setEntry(i, 0, getEntry(i, column));
    }
    
    return out;
  }
  


  public void setColumnMatrix(int column, RealMatrix matrix)
    throws MatrixIndexException, InvalidMatrixException
  {
    MatrixUtils.checkColumnIndex(this, column);
    int nRows = getRowDimension();
    if ((matrix.getRowDimension() != nRows) || (matrix.getColumnDimension() != 1))
    {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(matrix.getRowDimension()), Integer.valueOf(matrix.getColumnDimension()), Integer.valueOf(nRows), Integer.valueOf(1) });
    }
    

    for (int i = 0; i < nRows; i++) {
      setEntry(i, column, matrix.getEntry(i, 0));
    }
  }
  

  public RealVector getRowVector(int row)
    throws MatrixIndexException
  {
    return new ArrayRealVector(getRow(row), false);
  }
  

  public void setRowVector(int row, RealVector vector)
    throws MatrixIndexException, InvalidMatrixException
  {
    MatrixUtils.checkRowIndex(this, row);
    int nCols = getColumnDimension();
    if (vector.getDimension() != nCols) {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(1), Integer.valueOf(vector.getDimension()), Integer.valueOf(1), Integer.valueOf(nCols) });
    }
    

    for (int i = 0; i < nCols; i++) {
      setEntry(row, i, vector.getEntry(i));
    }
  }
  

  public RealVector getColumnVector(int column)
    throws MatrixIndexException
  {
    return new ArrayRealVector(getColumn(column), false);
  }
  

  public void setColumnVector(int column, RealVector vector)
    throws MatrixIndexException, InvalidMatrixException
  {
    MatrixUtils.checkColumnIndex(this, column);
    int nRows = getRowDimension();
    if (vector.getDimension() != nRows) {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(vector.getDimension()), Integer.valueOf(1), Integer.valueOf(nRows), Integer.valueOf(1) });
    }
    

    for (int i = 0; i < nRows; i++) {
      setEntry(i, column, vector.getEntry(i));
    }
  }
  


  public double[] getRow(int row)
    throws MatrixIndexException
  {
    MatrixUtils.checkRowIndex(this, row);
    int nCols = getColumnDimension();
    double[] out = new double[nCols];
    for (int i = 0; i < nCols; i++) {
      out[i] = getEntry(row, i);
    }
    
    return out;
  }
  


  public void setRow(int row, double[] array)
    throws MatrixIndexException, InvalidMatrixException
  {
    MatrixUtils.checkRowIndex(this, row);
    int nCols = getColumnDimension();
    if (array.length != nCols) {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(1), Integer.valueOf(array.length), Integer.valueOf(1), Integer.valueOf(nCols) });
    }
    

    for (int i = 0; i < nCols; i++) {
      setEntry(row, i, array[i]);
    }
  }
  


  public double[] getColumn(int column)
    throws MatrixIndexException
  {
    MatrixUtils.checkColumnIndex(this, column);
    int nRows = getRowDimension();
    double[] out = new double[nRows];
    for (int i = 0; i < nRows; i++) {
      out[i] = getEntry(i, column);
    }
    
    return out;
  }
  


  public void setColumn(int column, double[] array)
    throws MatrixIndexException, InvalidMatrixException
  {
    MatrixUtils.checkColumnIndex(this, column);
    int nRows = getRowDimension();
    if (array.length != nRows) {
      throw new InvalidMatrixException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(array.length), Integer.valueOf(1), Integer.valueOf(nRows), Integer.valueOf(1) });
    }
    

    for (int i = 0; i < nRows; i++) {
      setEntry(i, column, array[i]);
    }
  }
  


  public abstract double getEntry(int paramInt1, int paramInt2)
    throws MatrixIndexException;
  

  public abstract void setEntry(int paramInt1, int paramInt2, double paramDouble)
    throws MatrixIndexException;
  

  public abstract void addToEntry(int paramInt1, int paramInt2, double paramDouble)
    throws MatrixIndexException;
  

  public abstract void multiplyEntry(int paramInt1, int paramInt2, double paramDouble)
    throws MatrixIndexException;
  

  public RealMatrix transpose()
  {
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    final RealMatrix out = createMatrix(nCols, nRows);
    walkInOptimizedOrder(new DefaultRealMatrixPreservingVisitor()
    {

      public void visit(int row, int column, double value)
      {
        out.setEntry(column, row, value);
      }
      

    });
    return out;
  }
  

  @Deprecated
  public RealMatrix inverse()
    throws InvalidMatrixException
  {
    if (lu == null) {
      lu = new LUDecompositionImpl(this, 2.2250738585072014E-308D).getSolver();
    }
    return lu.getInverse();
  }
  
  @Deprecated
  public double getDeterminant()
    throws InvalidMatrixException
  {
    return new LUDecompositionImpl(this, 2.2250738585072014E-308D).getDeterminant();
  }
  
  public boolean isSquare()
  {
    return getColumnDimension() == getRowDimension();
  }
  
  @Deprecated
  public boolean isSingular()
  {
    if (lu == null) {
      lu = new LUDecompositionImpl(this, 2.2250738585072014E-308D).getSolver();
    }
    return !lu.isNonSingular();
  }
  

  public abstract int getRowDimension();
  

  public abstract int getColumnDimension();
  
  public double getTrace()
    throws NonSquareMatrixException
  {
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    if (nRows != nCols) {
      throw new NonSquareMatrixException(nRows, nCols);
    }
    double trace = 0.0D;
    for (int i = 0; i < nRows; i++) {
      trace += getEntry(i, i);
    }
    return trace;
  }
  

  public double[] operate(double[] v)
    throws IllegalArgumentException
  {
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    if (v.length != nCols) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(v.length), Integer.valueOf(nCols) });
    }
    


    double[] out = new double[nRows];
    for (int row = 0; row < nRows; row++) {
      double sum = 0.0D;
      for (int i = 0; i < nCols; i++) {
        sum += getEntry(row, i) * v[i];
      }
      out[row] = sum;
    }
    
    return out;
  }
  
  public RealVector operate(RealVector v)
    throws IllegalArgumentException
  {
    try
    {
      return new ArrayRealVector(operate(((ArrayRealVector)v).getDataRef()), false);
    } catch (ClassCastException cce) {
      int nRows = getRowDimension();
      int nCols = getColumnDimension();
      if (v.getDimension() != nCols) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(v.getDimension()), Integer.valueOf(nCols) });
      }
      


      double[] out = new double[nRows];
      for (int row = 0; row < nRows; row++) {
        double sum = 0.0D;
        for (int i = 0; i < nCols; i++) {
          sum += getEntry(row, i) * v.getEntry(i);
        }
        out[row] = sum;
      }
      
      return new ArrayRealVector(out, false);
    }
  }
  

  public double[] preMultiply(double[] v)
    throws IllegalArgumentException
  {
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    if (v.length != nRows) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(v.length), Integer.valueOf(nRows) });
    }
    


    double[] out = new double[nCols];
    for (int col = 0; col < nCols; col++) {
      double sum = 0.0D;
      for (int i = 0; i < nRows; i++) {
        sum += getEntry(i, col) * v[i];
      }
      out[col] = sum;
    }
    
    return out;
  }
  
  public RealVector preMultiply(RealVector v)
    throws IllegalArgumentException
  {
    try
    {
      return new ArrayRealVector(preMultiply(((ArrayRealVector)v).getDataRef()), false);
    }
    catch (ClassCastException cce) {
      int nRows = getRowDimension();
      int nCols = getColumnDimension();
      if (v.getDimension() != nRows) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(v.getDimension()), Integer.valueOf(nRows) });
      }
      


      double[] out = new double[nCols];
      for (int col = 0; col < nCols; col++) {
        double sum = 0.0D;
        for (int i = 0; i < nRows; i++) {
          sum += getEntry(i, col) * v.getEntry(i);
        }
        out[col] = sum;
      }
      
      return new ArrayRealVector(out);
    }
  }
  

  public double walkInRowOrder(RealMatrixChangingVisitor visitor)
    throws MatrixVisitorException
  {
    int rows = getRowDimension();
    int columns = getColumnDimension();
    visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
    for (int row = 0; row < rows; row++) {
      for (int column = 0; column < columns; column++) {
        double oldValue = getEntry(row, column);
        double newValue = visitor.visit(row, column, oldValue);
        setEntry(row, column, newValue);
      }
    }
    lu = null;
    return visitor.end();
  }
  
  public double walkInRowOrder(RealMatrixPreservingVisitor visitor)
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
  


  public double walkInRowOrder(RealMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
    visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
    
    for (int row = startRow; row <= endRow; row++) {
      for (int column = startColumn; column <= endColumn; column++) {
        double oldValue = getEntry(row, column);
        double newValue = visitor.visit(row, column, oldValue);
        setEntry(row, column, newValue);
      }
    }
    lu = null;
    return visitor.end();
  }
  


  public double walkInRowOrder(RealMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
    visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
    
    for (int row = startRow; row <= endRow; row++) {
      for (int column = startColumn; column <= endColumn; column++) {
        visitor.visit(row, column, getEntry(row, column));
      }
    }
    return visitor.end();
  }
  
  public double walkInColumnOrder(RealMatrixChangingVisitor visitor)
    throws MatrixVisitorException
  {
    int rows = getRowDimension();
    int columns = getColumnDimension();
    visitor.start(rows, columns, 0, rows - 1, 0, columns - 1);
    for (int column = 0; column < columns; column++) {
      for (int row = 0; row < rows; row++) {
        double oldValue = getEntry(row, column);
        double newValue = visitor.visit(row, column, oldValue);
        setEntry(row, column, newValue);
      }
    }
    lu = null;
    return visitor.end();
  }
  
  public double walkInColumnOrder(RealMatrixPreservingVisitor visitor)
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
  


  public double walkInColumnOrder(RealMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
    visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
    
    for (int column = startColumn; column <= endColumn; column++) {
      for (int row = startRow; row <= endRow; row++) {
        double oldValue = getEntry(row, column);
        double newValue = visitor.visit(row, column, oldValue);
        setEntry(row, column, newValue);
      }
    }
    lu = null;
    return visitor.end();
  }
  


  public double walkInColumnOrder(RealMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    MatrixUtils.checkSubMatrixIndex(this, startRow, endRow, startColumn, endColumn);
    visitor.start(getRowDimension(), getColumnDimension(), startRow, endRow, startColumn, endColumn);
    
    for (int column = startColumn; column <= endColumn; column++) {
      for (int row = startRow; row <= endRow; row++) {
        visitor.visit(row, column, getEntry(row, column));
      }
    }
    return visitor.end();
  }
  
  public double walkInOptimizedOrder(RealMatrixChangingVisitor visitor)
    throws MatrixVisitorException
  {
    return walkInRowOrder(visitor);
  }
  
  public double walkInOptimizedOrder(RealMatrixPreservingVisitor visitor)
    throws MatrixVisitorException
  {
    return walkInRowOrder(visitor);
  }
  


  public double walkInOptimizedOrder(RealMatrixChangingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
  }
  


  public double walkInOptimizedOrder(RealMatrixPreservingVisitor visitor, int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException, MatrixVisitorException
  {
    return walkInRowOrder(visitor, startRow, endRow, startColumn, endColumn);
  }
  
  @Deprecated
  public double[] solve(double[] b)
    throws IllegalArgumentException, InvalidMatrixException
  {
    if (lu == null) {
      lu = new LUDecompositionImpl(this, 2.2250738585072014E-308D).getSolver();
    }
    return lu.solve(b);
  }
  
  @Deprecated
  public RealMatrix solve(RealMatrix b)
    throws IllegalArgumentException, InvalidMatrixException
  {
    if (lu == null) {
      lu = new LUDecompositionImpl(this, 2.2250738585072014E-308D).getSolver();
    }
    return lu.solve(b);
  }
  


















  @Deprecated
  public void luDecompose()
    throws InvalidMatrixException
  {
    if (lu == null) {
      lu = new LUDecompositionImpl(this, 2.2250738585072014E-308D).getSolver();
    }
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
    if (!(object instanceof RealMatrix)) {
      return false;
    }
    RealMatrix m = (RealMatrix)object;
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    if ((m.getColumnDimension() != nCols) || (m.getRowDimension() != nRows)) {
      return false;
    }
    for (int row = 0; row < nRows; row++) {
      for (int col = 0; col < nCols; col++) {
        if (getEntry(row, col) != m.getEntry(row, col)) {
          return false;
        }
      }
    }
    return true;
  }
  





  public int hashCode()
  {
    int ret = 7;
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    ret = ret * 31 + nRows;
    ret = ret * 31 + nCols;
    for (int row = 0; row < nRows; row++) {
      for (int col = 0; col < nCols; col++) {
        ret = ret * 31 + (11 * (row + 1) + 17 * (col + 1)) * MathUtils.hash(getEntry(row, col));
      }
    }
    
    return ret;
  }
}
