package org.apache.commons.math.linear;

import java.io.Serializable;
import java.math.BigDecimal;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
















































@Deprecated
public class BigMatrixImpl
  implements BigMatrix, Serializable
{
  static final BigDecimal ZERO = new BigDecimal(0);
  

  static final BigDecimal ONE = new BigDecimal(1);
  

  private static final BigDecimal TOO_SMALL = new BigDecimal(1.0E-11D);
  

  private static final long serialVersionUID = -1011428905656140431L;
  

  protected BigDecimal[][] data = (BigDecimal[][])null;
  



  protected BigDecimal[][] lu = (BigDecimal[][])null;
  

  protected int[] permutation = null;
  

  protected int parity = 1;
  

  private int roundingMode = 4;
  

  private int scale = 64;
  






  public BigMatrixImpl() {}
  





  public BigMatrixImpl(int rowDimension, int columnDimension)
  {
    if (rowDimension < 1) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { Integer.valueOf(rowDimension), Integer.valueOf(1) });
    }
    
    if (columnDimension < 1) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSUFFICIENT_DIMENSION, new Object[] { Integer.valueOf(columnDimension), Integer.valueOf(1) });
    }
    
    data = new BigDecimal[rowDimension][columnDimension];
    lu = ((BigDecimal[][])null);
  }
  











  public BigMatrixImpl(BigDecimal[][] d)
  {
    copyIn(d);
    lu = ((BigDecimal[][])null);
  }
  














  public BigMatrixImpl(BigDecimal[][] d, boolean copyArray)
  {
    if (copyArray) {
      copyIn(d);
    } else {
      if (d == null) {
        throw new NullPointerException();
      }
      int nRows = d.length;
      if (nRows == 0) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_ROW, new Object[0]);
      }
      
      int nCols = d[0].length;
      if (nCols == 0) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_COLUMN, new Object[0]);
      }
      for (int r = 1; r < nRows; r++) {
        if (d[r].length != nCols) {
          throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIFFERENT_ROWS_LENGTHS, new Object[] { Integer.valueOf(nCols), Integer.valueOf(d[r].length) });
        }
      }
      

      data = d;
    }
    lu = ((BigDecimal[][])null);
  }
  










  public BigMatrixImpl(double[][] d)
  {
    int nRows = d.length;
    if (nRows == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_ROW, new Object[0]);
    }
    
    int nCols = d[0].length;
    if (nCols == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_COLUMN, new Object[0]);
    }
    for (int row = 1; row < nRows; row++) {
      if (d[row].length != nCols) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIFFERENT_ROWS_LENGTHS, new Object[] { Integer.valueOf(nCols), Integer.valueOf(d[row].length) });
      }
    }
    

    copyIn(d);
    lu = ((BigDecimal[][])null);
  }
  








  public BigMatrixImpl(String[][] d)
  {
    int nRows = d.length;
    if (nRows == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_ROW, new Object[0]);
    }
    
    int nCols = d[0].length;
    if (nCols == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_COLUMN, new Object[0]);
    }
    for (int row = 1; row < nRows; row++) {
      if (d[row].length != nCols) {
        throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIFFERENT_ROWS_LENGTHS, new Object[] { Integer.valueOf(nCols), Integer.valueOf(d[row].length) });
      }
    }
    

    copyIn(d);
    lu = ((BigDecimal[][])null);
  }
  








  public BigMatrixImpl(BigDecimal[] v)
  {
    int nRows = v.length;
    data = new BigDecimal[nRows][1];
    for (int row = 0; row < nRows; row++) {
      data[row][0] = v[row];
    }
  }
  




  public BigMatrix copy()
  {
    return new BigMatrixImpl(copyOut(), false);
  }
  




  public BigMatrix add(BigMatrix m)
    throws IllegalArgumentException
  {
    try
    {
      return add((BigMatrixImpl)m);
    }
    catch (ClassCastException cce)
    {
      MatrixUtils.checkAdditionCompatible(this, m);
      
      int rowCount = getRowDimension();
      int columnCount = getColumnDimension();
      BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
      for (int row = 0; row < rowCount; row++) {
        BigDecimal[] dataRow = data[row];
        BigDecimal[] outDataRow = outData[row];
        for (int col = 0; col < columnCount; col++) {
          outDataRow[col] = dataRow[col].add(m.getEntry(row, col));
        }
      }
      return new BigMatrixImpl(outData, false);
    }
  }
  







  public BigMatrixImpl add(BigMatrixImpl m)
    throws IllegalArgumentException
  {
    MatrixUtils.checkAdditionCompatible(this, m);
    
    int rowCount = getRowDimension();
    int columnCount = getColumnDimension();
    BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
    for (int row = 0; row < rowCount; row++) {
      BigDecimal[] dataRow = data[row];
      BigDecimal[] mRow = data[row];
      BigDecimal[] outDataRow = outData[row];
      for (int col = 0; col < columnCount; col++) {
        outDataRow[col] = dataRow[col].add(mRow[col]);
      }
    }
    return new BigMatrixImpl(outData, false);
  }
  




  public BigMatrix subtract(BigMatrix m)
    throws IllegalArgumentException
  {
    try
    {
      return subtract((BigMatrixImpl)m);
    }
    catch (ClassCastException cce)
    {
      MatrixUtils.checkSubtractionCompatible(this, m);
      
      int rowCount = getRowDimension();
      int columnCount = getColumnDimension();
      BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
      for (int row = 0; row < rowCount; row++) {
        BigDecimal[] dataRow = data[row];
        BigDecimal[] outDataRow = outData[row];
        for (int col = 0; col < columnCount; col++) {
          outDataRow[col] = dataRow[col].subtract(getEntry(row, col));
        }
      }
      return new BigMatrixImpl(outData, false);
    }
  }
  







  public BigMatrixImpl subtract(BigMatrixImpl m)
    throws IllegalArgumentException
  {
    MatrixUtils.checkSubtractionCompatible(this, m);
    
    int rowCount = getRowDimension();
    int columnCount = getColumnDimension();
    BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
    for (int row = 0; row < rowCount; row++) {
      BigDecimal[] dataRow = data[row];
      BigDecimal[] mRow = data[row];
      BigDecimal[] outDataRow = outData[row];
      for (int col = 0; col < columnCount; col++) {
        outDataRow[col] = dataRow[col].subtract(mRow[col]);
      }
    }
    return new BigMatrixImpl(outData, false);
  }
  





  public BigMatrix scalarAdd(BigDecimal d)
  {
    int rowCount = getRowDimension();
    int columnCount = getColumnDimension();
    BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
    for (int row = 0; row < rowCount; row++) {
      BigDecimal[] dataRow = data[row];
      BigDecimal[] outDataRow = outData[row];
      for (int col = 0; col < columnCount; col++) {
        outDataRow[col] = dataRow[col].add(d);
      }
    }
    return new BigMatrixImpl(outData, false);
  }
  




  public BigMatrix scalarMultiply(BigDecimal d)
  {
    int rowCount = getRowDimension();
    int columnCount = getColumnDimension();
    BigDecimal[][] outData = new BigDecimal[rowCount][columnCount];
    for (int row = 0; row < rowCount; row++) {
      BigDecimal[] dataRow = data[row];
      BigDecimal[] outDataRow = outData[row];
      for (int col = 0; col < columnCount; col++) {
        outDataRow[col] = dataRow[col].multiply(d);
      }
    }
    return new BigMatrixImpl(outData, false);
  }
  




  public BigMatrix multiply(BigMatrix m)
    throws IllegalArgumentException
  {
    try
    {
      return multiply((BigMatrixImpl)m);
    }
    catch (ClassCastException cce)
    {
      MatrixUtils.checkMultiplicationCompatible(this, m);
      
      int nRows = getRowDimension();
      int nCols = m.getColumnDimension();
      int nSum = getColumnDimension();
      BigDecimal[][] outData = new BigDecimal[nRows][nCols];
      for (int row = 0; row < nRows; row++) {
        BigDecimal[] dataRow = data[row];
        BigDecimal[] outDataRow = outData[row];
        for (int col = 0; col < nCols; col++) {
          BigDecimal sum = ZERO;
          for (int i = 0; i < nSum; i++) {
            sum = sum.add(dataRow[i].multiply(m.getEntry(i, col)));
          }
          outDataRow[col] = sum;
        }
      }
      return new BigMatrixImpl(outData, false);
    }
  }
  







  public BigMatrixImpl multiply(BigMatrixImpl m)
    throws IllegalArgumentException
  {
    MatrixUtils.checkMultiplicationCompatible(this, m);
    
    int nRows = getRowDimension();
    int nCols = m.getColumnDimension();
    int nSum = getColumnDimension();
    BigDecimal[][] outData = new BigDecimal[nRows][nCols];
    for (int row = 0; row < nRows; row++) {
      BigDecimal[] dataRow = data[row];
      BigDecimal[] outDataRow = outData[row];
      for (int col = 0; col < nCols; col++) {
        BigDecimal sum = ZERO;
        for (int i = 0; i < nSum; i++) {
          sum = sum.add(dataRow[i].multiply(data[i][col]));
        }
        outDataRow[col] = sum;
      }
    }
    return new BigMatrixImpl(outData, false);
  }
  





  public BigMatrix preMultiply(BigMatrix m)
    throws IllegalArgumentException
  {
    return m.multiply(this);
  }
  






  public BigDecimal[][] getData()
  {
    return copyOut();
  }
  







  public double[][] getDataAsDoubleArray()
  {
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    double[][] d = new double[nRows][nCols];
    for (int i = 0; i < nRows; i++) {
      for (int j = 0; j < nCols; j++) {
        d[i][j] = data[i][j].doubleValue();
      }
    }
    return d;
  }
  






  public BigDecimal[][] getDataRef()
  {
    return data;
  }
  





  public int getRoundingMode()
  {
    return roundingMode;
  }
  




  public void setRoundingMode(int roundingMode)
  {
    this.roundingMode = roundingMode;
  }
  





  public int getScale()
  {
    return scale;
  }
  




  public void setScale(int scale)
  {
    this.scale = scale;
  }
  





  public BigDecimal getNorm()
  {
    BigDecimal maxColSum = ZERO;
    for (int col = 0; col < getColumnDimension(); col++) {
      BigDecimal sum = ZERO;
      for (int row = 0; row < getRowDimension(); row++) {
        sum = sum.add(data[row][col].abs());
      }
      maxColSum = maxColSum.max(sum);
    }
    return maxColSum;
  }
  













  public BigMatrix getSubMatrix(int startRow, int endRow, int startColumn, int endColumn)
    throws MatrixIndexException
  {
    MatrixUtils.checkRowIndex(this, startRow);
    MatrixUtils.checkRowIndex(this, endRow);
    if (startRow > endRow) {
      throw new MatrixIndexException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW, new Object[] { Integer.valueOf(startRow), Integer.valueOf(endRow) });
    }
    

    MatrixUtils.checkColumnIndex(this, startColumn);
    MatrixUtils.checkColumnIndex(this, endColumn);
    if (startColumn > endColumn) {
      throw new MatrixIndexException(LocalizedFormats.INITIAL_COLUMN_AFTER_FINAL_COLUMN, new Object[] { Integer.valueOf(startColumn), Integer.valueOf(endColumn) });
    }
    

    BigDecimal[][] subMatrixData = new BigDecimal[endRow - startRow + 1][endColumn - startColumn + 1];
    
    for (int i = startRow; i <= endRow; i++) {
      System.arraycopy(data[i], startColumn, subMatrixData[(i - startRow)], 0, endColumn - startColumn + 1);
    }
    


    return new BigMatrixImpl(subMatrixData, false);
  }
  












  public BigMatrix getSubMatrix(int[] selectedRows, int[] selectedColumns)
    throws MatrixIndexException
  {
    if (selectedRows.length * selectedColumns.length == 0) {
      if (selectedRows.length == 0) {
        throw new MatrixIndexException(LocalizedFormats.EMPTY_SELECTED_ROW_INDEX_ARRAY, new Object[0]);
      }
      throw new MatrixIndexException(LocalizedFormats.EMPTY_SELECTED_COLUMN_INDEX_ARRAY, new Object[0]);
    }
    
    BigDecimal[][] subMatrixData = new BigDecimal[selectedRows.length][selectedColumns.length];
    int[] arr$;
    int len$;
    int i$; try { for (int i = 0; i < selectedRows.length; i++) {
        BigDecimal[] subI = subMatrixData[i];
        BigDecimal[] dataSelectedI = data[selectedRows[i]];
        for (int j = 0; j < selectedColumns.length; j++) {
          subI[j] = dataSelectedI[selectedColumns[j]];
        }
      }
    }
    catch (ArrayIndexOutOfBoundsException e)
    {
      for (int row : selectedRows) {
        MatrixUtils.checkRowIndex(this, row);
      }
      arr$ = selectedColumns;len$ = arr$.length;i$ = 0; } for (; i$ < len$; i$++) { int column = arr$[i$];
      MatrixUtils.checkColumnIndex(this, column);
    }
    
    return new BigMatrixImpl(subMatrixData, false);
  }
  



























  public void setSubMatrix(BigDecimal[][] subMatrix, int row, int column)
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
    


    if (data == null) {
      if (row > 0) {
        throw MathRuntimeException.createIllegalStateException(LocalizedFormats.FIRST_ROWS_NOT_INITIALIZED_YET, new Object[] { Integer.valueOf(row) });
      }
      

      if (column > 0) {
        throw MathRuntimeException.createIllegalStateException(LocalizedFormats.FIRST_COLUMNS_NOT_INITIALIZED_YET, new Object[] { Integer.valueOf(column) });
      }
      

      data = new BigDecimal[nRows][nCols];
      System.arraycopy(subMatrix, 0, data, 0, subMatrix.length);
    } else {
      MatrixUtils.checkRowIndex(this, row);
      MatrixUtils.checkColumnIndex(this, column);
      MatrixUtils.checkRowIndex(this, nRows + row - 1);
      MatrixUtils.checkColumnIndex(this, nCols + column - 1);
    }
    for (int i = 0; i < nRows; i++) {
      System.arraycopy(subMatrix[i], 0, data[(row + i)], column, nCols);
    }
    
    lu = ((BigDecimal[][])null);
  }
  







  public BigMatrix getRowMatrix(int row)
    throws MatrixIndexException
  {
    MatrixUtils.checkRowIndex(this, row);
    int ncols = getColumnDimension();
    BigDecimal[][] out = new BigDecimal[1][ncols];
    System.arraycopy(data[row], 0, out[0], 0, ncols);
    return new BigMatrixImpl(out, false);
  }
  






  public BigMatrix getColumnMatrix(int column)
    throws MatrixIndexException
  {
    MatrixUtils.checkColumnIndex(this, column);
    int nRows = getRowDimension();
    BigDecimal[][] out = new BigDecimal[nRows][1];
    for (int row = 0; row < nRows; row++) {
      out[row][0] = data[row][column];
    }
    return new BigMatrixImpl(out, false);
  }
  








  public BigDecimal[] getRow(int row)
    throws MatrixIndexException
  {
    MatrixUtils.checkRowIndex(this, row);
    int ncols = getColumnDimension();
    BigDecimal[] out = new BigDecimal[ncols];
    System.arraycopy(data[row], 0, out, 0, ncols);
    return out;
  }
  









  public double[] getRowAsDoubleArray(int row)
    throws MatrixIndexException
  {
    MatrixUtils.checkRowIndex(this, row);
    int ncols = getColumnDimension();
    double[] out = new double[ncols];
    for (int i = 0; i < ncols; i++) {
      out[i] = data[row][i].doubleValue();
    }
    return out;
  }
  








  public BigDecimal[] getColumn(int col)
    throws MatrixIndexException
  {
    MatrixUtils.checkColumnIndex(this, col);
    int nRows = getRowDimension();
    BigDecimal[] out = new BigDecimal[nRows];
    for (int i = 0; i < nRows; i++) {
      out[i] = data[i][col];
    }
    return out;
  }
  









  public double[] getColumnAsDoubleArray(int col)
    throws MatrixIndexException
  {
    MatrixUtils.checkColumnIndex(this, col);
    int nrows = getRowDimension();
    double[] out = new double[nrows];
    for (int i = 0; i < nrows; i++) {
      out[i] = data[i][col].doubleValue();
    }
    return out;
  }
  













  public BigDecimal getEntry(int row, int column)
    throws MatrixIndexException
  {
    try
    {
      return data[row][column];
    } catch (ArrayIndexOutOfBoundsException e) {
      throw new MatrixIndexException(LocalizedFormats.NO_SUCH_MATRIX_ENTRY, new Object[] { Integer.valueOf(row), Integer.valueOf(column), Integer.valueOf(getRowDimension()), Integer.valueOf(getColumnDimension()) });
    }
  }
  
















  public double getEntryAsDouble(int row, int column)
    throws MatrixIndexException
  {
    return getEntry(row, column).doubleValue();
  }
  




  public BigMatrix transpose()
  {
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    BigDecimal[][] outData = new BigDecimal[nCols][nRows];
    for (int row = 0; row < nRows; row++) {
      BigDecimal[] dataRow = data[row];
      for (int col = 0; col < nCols; col++) {
        outData[col][row] = dataRow[col];
      }
    }
    return new BigMatrixImpl(outData, false);
  }
  




  public BigMatrix inverse()
    throws InvalidMatrixException
  {
    return solve(MatrixUtils.createBigIdentityMatrix(getRowDimension()));
  }
  




  public BigDecimal getDeterminant()
    throws InvalidMatrixException
  {
    if (!isSquare()) {
      throw new NonSquareMatrixException(getRowDimension(), getColumnDimension());
    }
    if (isSingular()) {
      return ZERO;
    }
    BigDecimal det = parity == 1 ? ONE : ONE.negate();
    for (int i = 0; i < getRowDimension(); i++) {
      det = det.multiply(lu[i][i]);
    }
    return det;
  }
  




  public boolean isSquare()
  {
    return getColumnDimension() == getRowDimension();
  }
  



  public boolean isSingular()
  {
    if (lu == null) {
      try {
        luDecompose();
        return false;
      } catch (InvalidMatrixException ex) {
        return true;
      }
    }
    return false;
  }
  





  public int getRowDimension()
  {
    return data.length;
  }
  




  public int getColumnDimension()
  {
    return data[0].length;
  }
  






  public BigDecimal getTrace()
    throws IllegalArgumentException
  {
    if (!isSquare()) {
      throw new NonSquareMatrixException(getRowDimension(), getColumnDimension());
    }
    BigDecimal trace = data[0][0];
    for (int i = 1; i < getRowDimension(); i++) {
      trace = trace.add(data[i][i]);
    }
    return trace;
  }
  





  public BigDecimal[] operate(BigDecimal[] v)
    throws IllegalArgumentException
  {
    if (v.length != getColumnDimension()) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(v.length), Integer.valueOf(getColumnDimension()) });
    }
    

    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    BigDecimal[] out = new BigDecimal[nRows];
    for (int row = 0; row < nRows; row++) {
      BigDecimal sum = ZERO;
      for (int i = 0; i < nCols; i++) {
        sum = sum.add(data[row][i].multiply(v[i]));
      }
      out[row] = sum;
    }
    return out;
  }
  





  public BigDecimal[] operate(double[] v)
    throws IllegalArgumentException
  {
    BigDecimal[] bd = new BigDecimal[v.length];
    for (int i = 0; i < bd.length; i++) {
      bd[i] = new BigDecimal(v[i]);
    }
    return operate(bd);
  }
  





  public BigDecimal[] preMultiply(BigDecimal[] v)
    throws IllegalArgumentException
  {
    int nRows = getRowDimension();
    if (v.length != nRows) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(v.length), Integer.valueOf(nRows) });
    }
    

    int nCols = getColumnDimension();
    BigDecimal[] out = new BigDecimal[nCols];
    for (int col = 0; col < nCols; col++) {
      BigDecimal sum = ZERO;
      for (int i = 0; i < nRows; i++) {
        sum = sum.add(data[i][col].multiply(v[i]));
      }
      out[col] = sum;
    }
    return out;
  }
  









  public BigDecimal[] solve(BigDecimal[] b)
    throws IllegalArgumentException, InvalidMatrixException
  {
    int nRows = getRowDimension();
    if (b.length != nRows) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(b.length), Integer.valueOf(nRows) });
    }
    

    BigMatrix bMatrix = new BigMatrixImpl(b);
    BigDecimal[][] solution = ((BigMatrixImpl)solve(bMatrix)).getDataRef();
    BigDecimal[] out = new BigDecimal[nRows];
    for (int row = 0; row < nRows; row++) {
      out[row] = solution[row][0];
    }
    return out;
  }
  









  public BigDecimal[] solve(double[] b)
    throws IllegalArgumentException, InvalidMatrixException
  {
    BigDecimal[] bd = new BigDecimal[b.length];
    for (int i = 0; i < bd.length; i++) {
      bd[i] = new BigDecimal(b[i]);
    }
    return solve(bd);
  }
  









  public BigMatrix solve(BigMatrix b)
    throws IllegalArgumentException, InvalidMatrixException
  {
    if (b.getRowDimension() != getRowDimension()) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.DIMENSIONS_MISMATCH_2x2, new Object[] { Integer.valueOf(b.getRowDimension()), Integer.valueOf(b.getColumnDimension()), Integer.valueOf(getRowDimension()), "n" });
    }
    

    if (!isSquare()) {
      throw new NonSquareMatrixException(getRowDimension(), getColumnDimension());
    }
    if (isSingular()) {
      throw new SingularMatrixException();
    }
    
    int nCol = getColumnDimension();
    int nColB = b.getColumnDimension();
    int nRowB = b.getRowDimension();
    

    BigDecimal[][] bp = new BigDecimal[nRowB][nColB];
    for (int row = 0; row < nRowB; row++) {
      BigDecimal[] bpRow = bp[row];
      for (int col = 0; col < nColB; col++) {
        bpRow[col] = b.getEntry(permutation[row], col);
      }
    }
    

    for (int col = 0; col < nCol; col++) {
      for (int i = col + 1; i < nCol; i++) {
        BigDecimal[] bpI = bp[i];
        BigDecimal[] luI = lu[i];
        for (int j = 0; j < nColB; j++) {
          bpI[j] = bpI[j].subtract(bp[col][j].multiply(luI[col]));
        }
      }
    }
    

    for (int col = nCol - 1; col >= 0; col--) {
      BigDecimal[] bpCol = bp[col];
      BigDecimal luDiag = lu[col][col];
      for (int j = 0; j < nColB; j++) {
        bpCol[j] = bpCol[j].divide(luDiag, scale, roundingMode);
      }
      for (int i = 0; i < col; i++) {
        BigDecimal[] bpI = bp[i];
        BigDecimal[] luI = lu[i];
        for (int j = 0; j < nColB; j++) {
          bpI[j] = bpI[j].subtract(bp[col][j].multiply(luI[col]));
        }
      }
    }
    
    return new BigMatrixImpl(bp, false);
  }
  


















  public void luDecompose()
    throws InvalidMatrixException
  {
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    if (nRows != nCols) {
      throw new NonSquareMatrixException(getRowDimension(), getColumnDimension());
    }
    lu = getData();
    

    permutation = new int[nRows];
    for (int row = 0; row < nRows; row++) {
      permutation[row] = row;
    }
    parity = 1;
    

    for (int col = 0; col < nCols; col++)
    {
      BigDecimal sum = ZERO;
      

      for (int row = 0; row < col; row++) {
        BigDecimal[] luRow = lu[row];
        sum = luRow[col];
        for (int i = 0; i < row; i++) {
          sum = sum.subtract(luRow[i].multiply(lu[i][col]));
        }
        luRow[col] = sum;
      }
      

      int max = col;
      BigDecimal largest = ZERO;
      for (int row = col; row < nRows; row++) {
        BigDecimal[] luRow = lu[row];
        sum = luRow[col];
        for (int i = 0; i < col; i++) {
          sum = sum.subtract(luRow[i].multiply(lu[i][col]));
        }
        luRow[col] = sum;
        

        if (sum.abs().compareTo(largest) == 1) {
          largest = sum.abs();
          max = row;
        }
      }
      

      if (lu[max][col].abs().compareTo(TOO_SMALL) <= 0) {
        lu = ((BigDecimal[][])null);
        throw new SingularMatrixException();
      }
      

      if (max != col) {
        BigDecimal tmp = ZERO;
        for (int i = 0; i < nCols; i++) {
          tmp = lu[max][i];
          lu[max][i] = lu[col][i];
          lu[col][i] = tmp;
        }
        int temp = permutation[max];
        permutation[max] = permutation[col];
        permutation[col] = temp;
        parity = (-parity);
      }
      

      BigDecimal luDiag = lu[col][col];
      for (int row = col + 1; row < nRows; row++) {
        BigDecimal[] luRow = lu[row];
        luRow[col] = luRow[col].divide(luDiag, scale, roundingMode);
      }
    }
  }
  






  public String toString()
  {
    StringBuilder res = new StringBuilder();
    res.append("BigMatrixImpl{");
    if (data != null) {
      for (int i = 0; i < data.length; i++) {
        if (i > 0) {
          res.append(",");
        }
        res.append("{");
        for (int j = 0; j < data[0].length; j++) {
          if (j > 0) {
            res.append(",");
          }
          res.append(data[i][j]);
        }
        res.append("}");
      }
    }
    res.append("}");
    return res.toString();
  }
  









  public boolean equals(Object object)
  {
    if (object == this) {
      return true;
    }
    if (!(object instanceof BigMatrixImpl)) {
      return false;
    }
    BigMatrix m = (BigMatrix)object;
    int nRows = getRowDimension();
    int nCols = getColumnDimension();
    if ((m.getColumnDimension() != nCols) || (m.getRowDimension() != nRows)) {
      return false;
    }
    for (int row = 0; row < nRows; row++) {
      BigDecimal[] dataRow = data[row];
      for (int col = 0; col < nCols; col++) {
        if (!dataRow[col].equals(m.getEntry(row, col))) {
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
      BigDecimal[] dataRow = data[row];
      for (int col = 0; col < nCols; col++) {
        ret = ret * 31 + (11 * (row + 1) + 17 * (col + 1)) * dataRow[col].hashCode();
      }
    }
    
    return ret;
  }
  



























  protected BigMatrix getLUMatrix()
    throws InvalidMatrixException
  {
    if (lu == null) {
      luDecompose();
    }
    return new BigMatrixImpl(lu);
  }
  











  protected int[] getPermutation()
  {
    int[] out = new int[permutation.length];
    System.arraycopy(permutation, 0, out, 0, permutation.length);
    return out;
  }
  






  private BigDecimal[][] copyOut()
  {
    int nRows = getRowDimension();
    BigDecimal[][] out = new BigDecimal[nRows][getColumnDimension()];
    
    for (int i = 0; i < nRows; i++) {
      System.arraycopy(data[i], 0, out[i], 0, data[i].length);
    }
    return out;
  }
  









  private void copyIn(BigDecimal[][] in)
  {
    setSubMatrix(in, 0, 0);
  }
  




  private void copyIn(double[][] in)
  {
    int nRows = in.length;
    int nCols = in[0].length;
    data = new BigDecimal[nRows][nCols];
    for (int i = 0; i < nRows; i++) {
      BigDecimal[] dataI = data[i];
      double[] inI = in[i];
      for (int j = 0; j < nCols; j++) {
        dataI[j] = new BigDecimal(inI[j]);
      }
    }
    lu = ((BigDecimal[][])null);
  }
  





  private void copyIn(String[][] in)
  {
    int nRows = in.length;
    int nCols = in[0].length;
    data = new BigDecimal[nRows][nCols];
    for (int i = 0; i < nRows; i++) {
      BigDecimal[] dataI = data[i];
      String[] inI = in[i];
      for (int j = 0; j < nCols; j++) {
        dataI[j] = new BigDecimal(inI[j]);
      }
    }
    lu = ((BigDecimal[][])null);
  }
}
