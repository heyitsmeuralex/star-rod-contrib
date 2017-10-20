package org.apache.commons.math.linear;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Arrays;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.fraction.BigFraction;
import org.apache.commons.math.fraction.Fraction;










































public class MatrixUtils
{
  private MatrixUtils() {}
  
  public static RealMatrix createRealMatrix(int rows, int columns)
  {
    return rows * columns <= 4096 ? new Array2DRowRealMatrix(rows, columns) : new BlockRealMatrix(rows, columns);
  }
  

















  public static <T extends FieldElement<T>> FieldMatrix<T> createFieldMatrix(org.apache.commons.math.Field<T> field, int rows, int columns)
  {
    return rows * columns <= 4096 ? new Array2DRowFieldMatrix(field, rows, columns) : new BlockFieldMatrix(field, rows, columns);
  }
  


















  public static RealMatrix createRealMatrix(double[][] data)
  {
    return data.length * data[0].length <= 4096 ? new Array2DRowRealMatrix(data) : new BlockRealMatrix(data);
  }
  


















  public static <T extends FieldElement<T>> FieldMatrix<T> createFieldMatrix(T[][] data)
  {
    return data.length * data[0].length <= 4096 ? new Array2DRowFieldMatrix(data) : new BlockFieldMatrix(data);
  }
  








  public static RealMatrix createRealIdentityMatrix(int dimension)
  {
    RealMatrix m = createRealMatrix(dimension, dimension);
    for (int i = 0; i < dimension; i++) {
      m.setEntry(i, i, 1.0D);
    }
    return m;
  }
  










  public static <T extends FieldElement<T>> FieldMatrix<T> createFieldIdentityMatrix(org.apache.commons.math.Field<T> field, int dimension)
  {
    T zero = (FieldElement)field.getZero();
    T one = (FieldElement)field.getOne();
    
    T[][] d = (FieldElement[][])Array.newInstance(zero.getClass(), new int[] { dimension, dimension });
    for (int row = 0; row < dimension; row++) {
      T[] dRow = d[row];
      Arrays.fill(dRow, zero);
      dRow[row] = one;
    }
    return new Array2DRowFieldMatrix(d, false);
  }
  








  @Deprecated
  public static BigMatrix createBigIdentityMatrix(int dimension)
  {
    BigDecimal[][] d = new BigDecimal[dimension][dimension];
    for (int row = 0; row < dimension; row++) {
      BigDecimal[] dRow = d[row];
      Arrays.fill(dRow, BigMatrixImpl.ZERO);
      dRow[row] = BigMatrixImpl.ONE;
    }
    return new BigMatrixImpl(d, false);
  }
  







  public static RealMatrix createRealDiagonalMatrix(double[] diagonal)
  {
    RealMatrix m = createRealMatrix(diagonal.length, diagonal.length);
    for (int i = 0; i < diagonal.length; i++) {
      m.setEntry(i, i, diagonal[i]);
    }
    return m;
  }
  









  public static <T extends FieldElement<T>> FieldMatrix<T> createFieldDiagonalMatrix(T[] diagonal)
  {
    FieldMatrix<T> m = createFieldMatrix(diagonal[0].getField(), diagonal.length, diagonal.length);
    
    for (int i = 0; i < diagonal.length; i++) {
      m.setEntry(i, i, diagonal[i]);
    }
    return m;
  }
  










  @Deprecated
  public static BigMatrix createBigMatrix(double[][] data)
  {
    return new BigMatrixImpl(data);
  }
  










  @Deprecated
  public static BigMatrix createBigMatrix(BigDecimal[][] data)
  {
    return new BigMatrixImpl(data);
  }
  
















  @Deprecated
  public static BigMatrix createBigMatrix(BigDecimal[][] data, boolean copyArray)
  {
    return new BigMatrixImpl(data, copyArray);
  }
  










  @Deprecated
  public static BigMatrix createBigMatrix(String[][] data)
  {
    return new BigMatrixImpl(data);
  }
  







  public static RealVector createRealVector(double[] data)
  {
    return new ArrayRealVector(data, true);
  }
  








  public static <T extends FieldElement<T>> FieldVector<T> createFieldVector(T[] data)
  {
    return new ArrayFieldVector(data, true);
  }
  








  public static RealMatrix createRowRealMatrix(double[] rowData)
  {
    int nCols = rowData.length;
    RealMatrix m = createRealMatrix(1, nCols);
    for (int i = 0; i < nCols; i++) {
      m.setEntry(0, i, rowData[i]);
    }
    return m;
  }
  










  public static <T extends FieldElement<T>> FieldMatrix<T> createRowFieldMatrix(T[] rowData)
  {
    int nCols = rowData.length;
    if (nCols == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_COLUMN, new Object[0]);
    }
    FieldMatrix<T> m = createFieldMatrix(rowData[0].getField(), 1, nCols);
    for (int i = 0; i < nCols; i++) {
      m.setEntry(0, i, rowData[i]);
    }
    return m;
  }
  









  @Deprecated
  public static BigMatrix createRowBigMatrix(double[] rowData)
  {
    int nCols = rowData.length;
    BigDecimal[][] data = new BigDecimal[1][nCols];
    for (int i = 0; i < nCols; i++) {
      data[0][i] = new BigDecimal(rowData[i]);
    }
    return new BigMatrixImpl(data, false);
  }
  









  @Deprecated
  public static BigMatrix createRowBigMatrix(BigDecimal[] rowData)
  {
    int nCols = rowData.length;
    BigDecimal[][] data = new BigDecimal[1][nCols];
    System.arraycopy(rowData, 0, data[0], 0, nCols);
    return new BigMatrixImpl(data, false);
  }
  









  @Deprecated
  public static BigMatrix createRowBigMatrix(String[] rowData)
  {
    int nCols = rowData.length;
    BigDecimal[][] data = new BigDecimal[1][nCols];
    for (int i = 0; i < nCols; i++) {
      data[0][i] = new BigDecimal(rowData[i]);
    }
    return new BigMatrixImpl(data, false);
  }
  








  public static RealMatrix createColumnRealMatrix(double[] columnData)
  {
    int nRows = columnData.length;
    RealMatrix m = createRealMatrix(nRows, 1);
    for (int i = 0; i < nRows; i++) {
      m.setEntry(i, 0, columnData[i]);
    }
    return m;
  }
  










  public static <T extends FieldElement<T>> FieldMatrix<T> createColumnFieldMatrix(T[] columnData)
  {
    int nRows = columnData.length;
    if (nRows == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.AT_LEAST_ONE_ROW, new Object[0]);
    }
    FieldMatrix<T> m = createFieldMatrix(columnData[0].getField(), nRows, 1);
    for (int i = 0; i < nRows; i++) {
      m.setEntry(i, 0, columnData[i]);
    }
    return m;
  }
  









  @Deprecated
  public static BigMatrix createColumnBigMatrix(double[] columnData)
  {
    int nRows = columnData.length;
    BigDecimal[][] data = new BigDecimal[nRows][1];
    for (int row = 0; row < nRows; row++) {
      data[row][0] = new BigDecimal(columnData[row]);
    }
    return new BigMatrixImpl(data, false);
  }
  









  @Deprecated
  public static BigMatrix createColumnBigMatrix(BigDecimal[] columnData)
  {
    int nRows = columnData.length;
    BigDecimal[][] data = new BigDecimal[nRows][1];
    for (int row = 0; row < nRows; row++) {
      data[row][0] = columnData[row];
    }
    return new BigMatrixImpl(data, false);
  }
  









  @Deprecated
  public static BigMatrix createColumnBigMatrix(String[] columnData)
  {
    int nRows = columnData.length;
    BigDecimal[][] data = new BigDecimal[nRows][1];
    for (int row = 0; row < nRows; row++) {
      data[row][0] = new BigDecimal(columnData[row]);
    }
    return new BigMatrixImpl(data, false);
  }
  





  public static void checkRowIndex(AnyMatrix m, int row)
  {
    if ((row < 0) || (row >= m.getRowDimension())) {
      throw new MatrixIndexException(LocalizedFormats.ROW_INDEX_OUT_OF_RANGE, new Object[] { Integer.valueOf(row), Integer.valueOf(0), Integer.valueOf(m.getRowDimension() - 1) });
    }
  }
  






  public static void checkColumnIndex(AnyMatrix m, int column)
    throws MatrixIndexException
  {
    if ((column < 0) || (column >= m.getColumnDimension())) {
      throw new MatrixIndexException(LocalizedFormats.COLUMN_INDEX_OUT_OF_RANGE, new Object[] { Integer.valueOf(column), Integer.valueOf(0), Integer.valueOf(m.getColumnDimension() - 1) });
    }
  }
  













  public static void checkSubMatrixIndex(AnyMatrix m, int startRow, int endRow, int startColumn, int endColumn)
  {
    checkRowIndex(m, startRow);
    checkRowIndex(m, endRow);
    if (startRow > endRow) {
      throw new MatrixIndexException(LocalizedFormats.INITIAL_ROW_AFTER_FINAL_ROW, new Object[] { Integer.valueOf(startRow), Integer.valueOf(endRow) });
    }
    

    checkColumnIndex(m, startColumn);
    checkColumnIndex(m, endColumn);
    if (startColumn > endColumn) {
      throw new MatrixIndexException(LocalizedFormats.INITIAL_COLUMN_AFTER_FINAL_COLUMN, new Object[] { Integer.valueOf(startColumn), Integer.valueOf(endColumn) });
    }
  }
  












  public static void checkSubMatrixIndex(AnyMatrix m, int[] selectedRows, int[] selectedColumns)
    throws MatrixIndexException
  {
    if (selectedRows.length * selectedColumns.length == 0) {
      if (selectedRows.length == 0) {
        throw new MatrixIndexException(LocalizedFormats.EMPTY_SELECTED_ROW_INDEX_ARRAY, new Object[0]);
      }
      throw new MatrixIndexException(LocalizedFormats.EMPTY_SELECTED_COLUMN_INDEX_ARRAY, new Object[0]);
    }
    
    for (int row : selectedRows) {
      checkRowIndex(m, row);
    }
    for (int column : selectedColumns) {
      checkColumnIndex(m, column);
    }
  }
  





  public static void checkAdditionCompatible(AnyMatrix left, AnyMatrix right)
    throws IllegalArgumentException
  {
    if ((left.getRowDimension() != right.getRowDimension()) || (left.getColumnDimension() != right.getColumnDimension()))
    {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_ADDITION_COMPATIBLE_MATRICES, new Object[] { Integer.valueOf(left.getRowDimension()), Integer.valueOf(left.getColumnDimension()), Integer.valueOf(right.getRowDimension()), Integer.valueOf(right.getColumnDimension()) });
    }
  }
  








  public static void checkSubtractionCompatible(AnyMatrix left, AnyMatrix right)
    throws IllegalArgumentException
  {
    if ((left.getRowDimension() != right.getRowDimension()) || (left.getColumnDimension() != right.getColumnDimension()))
    {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_SUBTRACTION_COMPATIBLE_MATRICES, new Object[] { Integer.valueOf(left.getRowDimension()), Integer.valueOf(left.getColumnDimension()), Integer.valueOf(right.getRowDimension()), Integer.valueOf(right.getColumnDimension()) });
    }
  }
  








  public static void checkMultiplicationCompatible(AnyMatrix left, AnyMatrix right)
    throws IllegalArgumentException
  {
    if (left.getColumnDimension() != right.getRowDimension()) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.NOT_MULTIPLICATION_COMPATIBLE_MATRICES, new Object[] { Integer.valueOf(left.getRowDimension()), Integer.valueOf(left.getColumnDimension()), Integer.valueOf(right.getRowDimension()), Integer.valueOf(right.getColumnDimension()) });
    }
  }
  







  public static Array2DRowRealMatrix fractionMatrixToRealMatrix(FieldMatrix<Fraction> m)
  {
    FractionMatrixConverter converter = new FractionMatrixConverter();
    m.walkInOptimizedOrder(converter);
    return converter.getConvertedMatrix();
  }
  

  private static class FractionMatrixConverter
    extends DefaultFieldMatrixPreservingVisitor<Fraction>
  {
    private double[][] data;
    
    public FractionMatrixConverter()
    {
      super();
    }
    


    public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn)
    {
      data = new double[rows][columns];
    }
    

    public void visit(int row, int column, Fraction value)
    {
      data[row][column] = value.doubleValue();
    }
    


    Array2DRowRealMatrix getConvertedMatrix()
    {
      return new Array2DRowRealMatrix(data, false);
    }
  }
  





  public static Array2DRowRealMatrix bigFractionMatrixToRealMatrix(FieldMatrix<BigFraction> m)
  {
    BigFractionMatrixConverter converter = new BigFractionMatrixConverter();
    m.walkInOptimizedOrder(converter);
    return converter.getConvertedMatrix();
  }
  

  private static class BigFractionMatrixConverter
    extends DefaultFieldMatrixPreservingVisitor<BigFraction>
  {
    private double[][] data;
    
    public BigFractionMatrixConverter()
    {
      super();
    }
    


    public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn)
    {
      data = new double[rows][columns];
    }
    

    public void visit(int row, int column, BigFraction value)
    {
      data[row][column] = value.doubleValue();
    }
    


    Array2DRowRealMatrix getConvertedMatrix()
    {
      return new Array2DRowRealMatrix(data, false);
    }
  }
  









































  public static void serializeRealVector(RealVector vector, ObjectOutputStream oos)
    throws IOException
  {
    int n = vector.getDimension();
    oos.writeInt(n);
    for (int i = 0; i < n; i++) {
      oos.writeDouble(vector.getEntry(i));
    }
  }
  



















  public static void deserializeRealVector(Object instance, String fieldName, ObjectInputStream ois)
    throws ClassNotFoundException, IOException
  {
    try
    {
      int n = ois.readInt();
      double[] data = new double[n];
      for (int i = 0; i < n; i++) {
        data[i] = ois.readDouble();
      }
      

      RealVector vector = new ArrayRealVector(data, false);
      

      java.lang.reflect.Field f = instance.getClass().getDeclaredField(fieldName);
      
      f.setAccessible(true);
      f.set(instance, vector);
    }
    catch (NoSuchFieldException nsfe) {
      IOException ioe = new IOException();
      ioe.initCause(nsfe);
      throw ioe;
    } catch (IllegalAccessException iae) {
      IOException ioe = new IOException();
      ioe.initCause(iae);
      throw ioe;
    }
  }
  









































  public static void serializeRealMatrix(RealMatrix matrix, ObjectOutputStream oos)
    throws IOException
  {
    int n = matrix.getRowDimension();
    int m = matrix.getColumnDimension();
    oos.writeInt(n);
    oos.writeInt(m);
    for (int i = 0; i < n; i++) {
      for (int j = 0; j < m; j++) {
        oos.writeDouble(matrix.getEntry(i, j));
      }
    }
  }
  



















  public static void deserializeRealMatrix(Object instance, String fieldName, ObjectInputStream ois)
    throws ClassNotFoundException, IOException
  {
    try
    {
      int n = ois.readInt();
      int m = ois.readInt();
      double[][] data = new double[n][m];
      for (int i = 0; i < n; i++) {
        double[] dataI = data[i];
        for (int j = 0; j < m; j++) {
          dataI[j] = ois.readDouble();
        }
      }
      

      RealMatrix matrix = new Array2DRowRealMatrix(data, false);
      

      java.lang.reflect.Field f = instance.getClass().getDeclaredField(fieldName);
      
      f.setAccessible(true);
      f.set(instance, matrix);
    }
    catch (NoSuchFieldException nsfe) {
      IOException ioe = new IOException();
      ioe.initCause(nsfe);
      throw ioe;
    } catch (IllegalAccessException iae) {
      IOException ioe = new IOException();
      ioe.initCause(iae);
      throw ioe;
    }
  }
}
