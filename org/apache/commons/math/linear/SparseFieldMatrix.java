package org.apache.commons.math.linear;

import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.util.OpenIntToFieldHashMap;




































public class SparseFieldMatrix<T extends FieldElement<T>>
  extends AbstractFieldMatrix<T>
{
  private static final long serialVersionUID = 9078068119297757342L;
  private final OpenIntToFieldHashMap<T> entries;
  private final int rows;
  private final int columns;
  
  public SparseFieldMatrix(Field<T> field)
  {
    super(field);
    rows = 0;
    columns = 0;
    entries = new OpenIntToFieldHashMap(field);
  }
  








  public SparseFieldMatrix(Field<T> field, int rowDimension, int columnDimension)
    throws IllegalArgumentException
  {
    super(field, rowDimension, columnDimension);
    rows = rowDimension;
    columns = columnDimension;
    entries = new OpenIntToFieldHashMap(field);
  }
  



  public SparseFieldMatrix(SparseFieldMatrix<T> other)
  {
    super(other.getField(), other.getRowDimension(), other.getColumnDimension());
    rows = other.getRowDimension();
    columns = other.getColumnDimension();
    entries = new OpenIntToFieldHashMap(entries);
  }
  



  public SparseFieldMatrix(FieldMatrix<T> other)
  {
    super(other.getField(), other.getRowDimension(), other.getColumnDimension());
    rows = other.getRowDimension();
    columns = other.getColumnDimension();
    entries = new OpenIntToFieldHashMap(getField());
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        setEntry(i, j, other.getEntry(i, j));
      }
    }
  }
  

  public void addToEntry(int row, int column, T increment)
    throws MatrixIndexException
  {
    checkRowIndex(row);
    checkColumnIndex(column);
    int key = computeKey(row, column);
    T value = (FieldElement)entries.get(key).add(increment);
    if (((FieldElement)getField().getZero()).equals(value)) {
      entries.remove(key);
    } else {
      entries.put(key, value);
    }
  }
  


  public FieldMatrix<T> copy()
  {
    return new SparseFieldMatrix(this);
  }
  

  public FieldMatrix<T> createMatrix(int rowDimension, int columnDimension)
    throws IllegalArgumentException
  {
    return new SparseFieldMatrix(getField(), rowDimension, columnDimension);
  }
  

  public int getColumnDimension()
  {
    return columns;
  }
  
  public T getEntry(int row, int column)
    throws MatrixIndexException
  {
    checkRowIndex(row);
    checkColumnIndex(column);
    return entries.get(computeKey(row, column));
  }
  

  public int getRowDimension()
  {
    return rows;
  }
  

  public void multiplyEntry(int row, int column, T factor)
    throws MatrixIndexException
  {
    checkRowIndex(row);
    checkColumnIndex(column);
    int key = computeKey(row, column);
    T value = (FieldElement)entries.get(key).multiply(factor);
    if (((FieldElement)getField().getZero()).equals(value)) {
      entries.remove(key);
    } else {
      entries.put(key, value);
    }
  }
  


  public void setEntry(int row, int column, T value)
    throws MatrixIndexException
  {
    checkRowIndex(row);
    checkColumnIndex(column);
    if (((FieldElement)getField().getZero()).equals(value)) {
      entries.remove(computeKey(row, column));
    } else {
      entries.put(computeKey(row, column), value);
    }
  }
  





  private int computeKey(int row, int column)
  {
    return row * columns + column;
  }
}
