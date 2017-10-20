package org.apache.commons.math.linear;

import java.io.Serializable;
import org.apache.commons.math.util.OpenIntToDoubleHashMap;
import org.apache.commons.math.util.OpenIntToDoubleHashMap.Iterator;

































public class OpenMapRealMatrix
  extends AbstractRealMatrix
  implements SparseRealMatrix, Serializable
{
  private static final long serialVersionUID = -5962461716457143437L;
  private final int rows;
  private final int columns;
  private final OpenIntToDoubleHashMap entries;
  
  public OpenMapRealMatrix(int rowDimension, int columnDimension)
  {
    super(rowDimension, columnDimension);
    rows = rowDimension;
    columns = columnDimension;
    entries = new OpenIntToDoubleHashMap(0.0D);
  }
  



  public OpenMapRealMatrix(OpenMapRealMatrix matrix)
  {
    rows = rows;
    columns = columns;
    entries = new OpenIntToDoubleHashMap(entries);
  }
  

  public OpenMapRealMatrix copy()
  {
    return new OpenMapRealMatrix(this);
  }
  

  public OpenMapRealMatrix createMatrix(int rowDimension, int columnDimension)
    throws IllegalArgumentException
  {
    return new OpenMapRealMatrix(rowDimension, columnDimension);
  }
  

  public int getColumnDimension()
  {
    return columns;
  }
  
  public OpenMapRealMatrix add(RealMatrix m)
    throws IllegalArgumentException
  {
    try
    {
      return add((OpenMapRealMatrix)m);
    } catch (ClassCastException cce) {}
    return (OpenMapRealMatrix)super.add(m);
  }
  








  public OpenMapRealMatrix add(OpenMapRealMatrix m)
    throws IllegalArgumentException
  {
    MatrixUtils.checkAdditionCompatible(this, m);
    
    OpenMapRealMatrix out = new OpenMapRealMatrix(this);
    for (OpenIntToDoubleHashMap.Iterator iterator = entries.iterator(); iterator.hasNext();) {
      iterator.advance();
      int row = iterator.key() / columns;
      int col = iterator.key() - row * columns;
      out.setEntry(row, col, getEntry(row, col) + iterator.value());
    }
    
    return out;
  }
  

  public OpenMapRealMatrix subtract(RealMatrix m)
    throws IllegalArgumentException
  {
    try
    {
      return subtract((OpenMapRealMatrix)m);
    } catch (ClassCastException cce) {}
    return (OpenMapRealMatrix)super.subtract(m);
  }
  








  public OpenMapRealMatrix subtract(OpenMapRealMatrix m)
    throws IllegalArgumentException
  {
    MatrixUtils.checkAdditionCompatible(this, m);
    
    OpenMapRealMatrix out = new OpenMapRealMatrix(this);
    for (OpenIntToDoubleHashMap.Iterator iterator = entries.iterator(); iterator.hasNext();) {
      iterator.advance();
      int row = iterator.key() / columns;
      int col = iterator.key() - row * columns;
      out.setEntry(row, col, getEntry(row, col) - iterator.value());
    }
    
    return out;
  }
  

  public RealMatrix multiply(RealMatrix m)
    throws IllegalArgumentException
  {
    try
    {
      return multiply((OpenMapRealMatrix)m);
    }
    catch (ClassCastException cce)
    {
      MatrixUtils.checkMultiplicationCompatible(this, m);
      
      int outCols = m.getColumnDimension();
      BlockRealMatrix out = new BlockRealMatrix(rows, outCols);
      for (OpenIntToDoubleHashMap.Iterator iterator = entries.iterator(); iterator.hasNext();) {
        iterator.advance();
        double value = iterator.value();
        int key = iterator.key();
        int i = key / columns;
        int k = key % columns;
        for (int j = 0; j < outCols; j++) {
          out.addToEntry(i, j, value * m.getEntry(k, j));
        }
      }
      
      return out;
    }
  }
  









  public OpenMapRealMatrix multiply(OpenMapRealMatrix m)
    throws IllegalArgumentException
  {
    MatrixUtils.checkMultiplicationCompatible(this, m);
    
    int outCols = m.getColumnDimension();
    OpenMapRealMatrix out = new OpenMapRealMatrix(rows, outCols);
    for (OpenIntToDoubleHashMap.Iterator iterator = entries.iterator(); iterator.hasNext();) {
      iterator.advance();
      double value = iterator.value();
      int key = iterator.key();
      int i = key / columns;
      int k = key % columns;
      for (int j = 0; j < outCols; j++) {
        int rightKey = m.computeKey(k, j);
        if (entries.containsKey(rightKey)) {
          int outKey = out.computeKey(i, j);
          double outValue = entries.get(outKey) + value * entries.get(rightKey);
          
          if (outValue == 0.0D) {
            entries.remove(outKey);
          } else {
            entries.put(outKey, outValue);
          }
        }
      }
    }
    
    return out;
  }
  

  public double getEntry(int row, int column)
    throws MatrixIndexException
  {
    MatrixUtils.checkRowIndex(this, row);
    MatrixUtils.checkColumnIndex(this, column);
    return entries.get(computeKey(row, column));
  }
  

  public int getRowDimension()
  {
    return rows;
  }
  

  public void setEntry(int row, int column, double value)
    throws MatrixIndexException
  {
    MatrixUtils.checkRowIndex(this, row);
    MatrixUtils.checkColumnIndex(this, column);
    if (value == 0.0D) {
      entries.remove(computeKey(row, column));
    } else {
      entries.put(computeKey(row, column), value);
    }
  }
  

  public void addToEntry(int row, int column, double increment)
    throws MatrixIndexException
  {
    MatrixUtils.checkRowIndex(this, row);
    MatrixUtils.checkColumnIndex(this, column);
    int key = computeKey(row, column);
    double value = entries.get(key) + increment;
    if (value == 0.0D) {
      entries.remove(key);
    } else {
      entries.put(key, value);
    }
  }
  

  public void multiplyEntry(int row, int column, double factor)
    throws MatrixIndexException
  {
    MatrixUtils.checkRowIndex(this, row);
    MatrixUtils.checkColumnIndex(this, column);
    int key = computeKey(row, column);
    double value = entries.get(key) * factor;
    if (value == 0.0D) {
      entries.remove(key);
    } else {
      entries.put(key, value);
    }
  }
  





  private int computeKey(int row, int column)
  {
    return row * columns + column;
  }
}
