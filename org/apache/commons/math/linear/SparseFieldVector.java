package org.apache.commons.math.linear;

import java.io.Serializable;
import java.lang.reflect.Array;
import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.util.OpenIntToFieldHashMap;
import org.apache.commons.math.util.OpenIntToFieldHashMap.Iterator;





































public class SparseFieldVector<T extends FieldElement<T>>
  implements FieldVector<T>, Serializable
{
  private static final long serialVersionUID = 7841233292190413362L;
  private final Field<T> field;
  private final OpenIntToFieldHashMap<T> entries;
  private final int virtualSize;
  
  public SparseFieldVector(Field<T> field)
  {
    this(field, 0);
  }
  





  public SparseFieldVector(Field<T> field, int dimension)
  {
    this.field = field;
    virtualSize = dimension;
    entries = new OpenIntToFieldHashMap(field);
  }
  




  protected SparseFieldVector(SparseFieldVector<T> v, int resize)
  {
    field = field;
    virtualSize = (v.getDimension() + resize);
    entries = new OpenIntToFieldHashMap(entries);
  }
  






  public SparseFieldVector(Field<T> field, int dimension, int expectedSize)
  {
    this.field = field;
    virtualSize = dimension;
    entries = new OpenIntToFieldHashMap(field, expectedSize);
  }
  





  public SparseFieldVector(Field<T> field, T[] values)
  {
    this.field = field;
    virtualSize = values.length;
    entries = new OpenIntToFieldHashMap(field);
    for (int key = 0; key < values.length; key++) {
      T value = values[key];
      entries.put(key, value);
    }
  }
  





  public SparseFieldVector(SparseFieldVector<T> v)
  {
    field = field;
    virtualSize = v.getDimension();
    entries = new OpenIntToFieldHashMap(v.getEntries());
  }
  



  private OpenIntToFieldHashMap<T> getEntries()
  {
    return entries;
  }
  




  public FieldVector<T> add(SparseFieldVector<T> v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    SparseFieldVector<T> res = (SparseFieldVector)copy();
    OpenIntToFieldHashMap<T>.Iterator iter = v.getEntries().iterator();
    while (iter.hasNext()) {
      iter.advance();
      int key = iter.key();
      T value = iter.value();
      if (entries.containsKey(key)) {
        res.setEntry(key, (FieldElement)entries.get(key).add(value));
      } else {
        res.setEntry(key, value);
      }
    }
    return res;
  }
  

  public FieldVector<T> add(T[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    SparseFieldVector<T> res = new SparseFieldVector(field, getDimension());
    for (int i = 0; i < v.length; i++) {
      res.setEntry(i, (FieldElement)v[i].add(getEntry(i)));
    }
    return res;
  }
  




  public FieldVector<T> append(SparseFieldVector<T> v)
  {
    SparseFieldVector<T> res = new SparseFieldVector(this, v.getDimension());
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      res.setEntry(iter.key() + virtualSize, iter.value());
    }
    return res;
  }
  
  public FieldVector<T> append(FieldVector<T> v)
  {
    if ((v instanceof SparseFieldVector)) {
      return append((SparseFieldVector)v);
    }
    return append(v.toArray());
  }
  

  public FieldVector<T> append(T d)
  {
    FieldVector<T> res = new SparseFieldVector(this, 1);
    res.setEntry(virtualSize, d);
    return res;
  }
  
  public FieldVector<T> append(T[] a)
  {
    FieldVector<T> res = new SparseFieldVector(this, a.length);
    for (int i = 0; i < a.length; i++) {
      res.setEntry(i + virtualSize, a[i]);
    }
    return res;
  }
  
  public FieldVector<T> copy()
  {
    return new SparseFieldVector(this);
  }
  
  public T dotProduct(FieldVector<T> v) throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    T res = (FieldElement)field.getZero();
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      res = (FieldElement)res.add(v.getEntry(iter.key()).multiply(iter.value()));
    }
    return res;
  }
  
  public T dotProduct(T[] v) throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    T res = (FieldElement)field.getZero();
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      int idx = iter.key();
      T value = (FieldElement)field.getZero();
      if (idx < v.length) {
        value = v[idx];
      }
      res = (FieldElement)res.add(value.multiply(iter.value()));
    }
    return res;
  }
  
  public FieldVector<T> ebeDivide(FieldVector<T> v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    SparseFieldVector<T> res = new SparseFieldVector(this);
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      res.setEntry(iter.key(), (FieldElement)iter.value().divide(v.getEntry(iter.key())));
    }
    return res;
  }
  
  public FieldVector<T> ebeDivide(T[] v) throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    SparseFieldVector<T> res = new SparseFieldVector(this);
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      res.setEntry(iter.key(), (FieldElement)iter.value().divide(v[iter.key()]));
    }
    return res;
  }
  
  public FieldVector<T> ebeMultiply(FieldVector<T> v) throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    SparseFieldVector<T> res = new SparseFieldVector(this);
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      res.setEntry(iter.key(), (FieldElement)iter.value().multiply(v.getEntry(iter.key())));
    }
    return res;
  }
  
  public FieldVector<T> ebeMultiply(T[] v) throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    SparseFieldVector<T> res = new SparseFieldVector(this);
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      res.setEntry(iter.key(), (FieldElement)iter.value().multiply(v[iter.key()]));
    }
    return res;
  }
  
  public T[] getData()
  {
    T[] res = buildArray(virtualSize);
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      res[iter.key()] = iter.value();
    }
    return res;
  }
  
  public int getDimension()
  {
    return virtualSize;
  }
  
  public T getEntry(int index) throws MatrixIndexException
  {
    checkIndex(index);
    return entries.get(index);
  }
  
  public Field<T> getField()
  {
    return field;
  }
  
  public FieldVector<T> getSubVector(int index, int n)
    throws MatrixIndexException
  {
    checkIndex(index);
    checkIndex(index + n - 1);
    SparseFieldVector<T> res = new SparseFieldVector(field, n);
    int end = index + n;
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      int key = iter.key();
      if ((key >= index) && (key < end)) {
        res.setEntry(key - index, iter.value());
      }
    }
    return res;
  }
  
  public FieldVector<T> mapAdd(T d)
  {
    return copy().mapAddToSelf(d);
  }
  
  public FieldVector<T> mapAddToSelf(T d)
  {
    for (int i = 0; i < virtualSize; i++) {
      setEntry(i, (FieldElement)getEntry(i).add(d));
    }
    return this;
  }
  
  public FieldVector<T> mapDivide(T d)
  {
    return copy().mapDivideToSelf(d);
  }
  
  public FieldVector<T> mapDivideToSelf(T d)
  {
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      entries.put(iter.key(), (FieldElement)iter.value().divide(d));
    }
    return this;
  }
  
  public FieldVector<T> mapInv()
  {
    return copy().mapInvToSelf();
  }
  
  public FieldVector<T> mapInvToSelf()
  {
    for (int i = 0; i < virtualSize; i++) {
      setEntry(i, (FieldElement)((FieldElement)field.getOne()).divide(getEntry(i)));
    }
    return this;
  }
  
  public FieldVector<T> mapMultiply(T d)
  {
    return copy().mapMultiplyToSelf(d);
  }
  
  public FieldVector<T> mapMultiplyToSelf(T d)
  {
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      entries.put(iter.key(), (FieldElement)iter.value().multiply(d));
    }
    return this;
  }
  
  public FieldVector<T> mapSubtract(T d)
  {
    return copy().mapSubtractToSelf(d);
  }
  
  public FieldVector<T> mapSubtractToSelf(T d)
  {
    return mapAddToSelf((FieldElement)((FieldElement)field.getZero()).subtract(d));
  }
  





  public FieldMatrix<T> outerProduct(SparseFieldVector<T> v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    SparseFieldMatrix<T> res = new SparseFieldMatrix(field, virtualSize, virtualSize);
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      OpenIntToFieldHashMap<T>.Iterator iter2 = entries.iterator();
      while (iter2.hasNext()) {
        iter2.advance();
        res.setEntry(iter.key(), iter2.key(), (FieldElement)iter.value().multiply(iter2.value()));
      }
    }
    return res;
  }
  
  public FieldMatrix<T> outerProduct(T[] v) throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    FieldMatrix<T> res = new SparseFieldMatrix(field, virtualSize, virtualSize);
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      int row = iter.key();
      FieldElement<T> value = iter.value();
      for (int col = 0; col < virtualSize; col++) {
        res.setEntry(row, col, (FieldElement)value.multiply(v[col]));
      }
    }
    return res;
  }
  
  public FieldMatrix<T> outerProduct(FieldVector<T> v)
    throws IllegalArgumentException
  {
    if ((v instanceof SparseFieldVector)) {
      return outerProduct((SparseFieldVector)v);
    }
    return outerProduct(v.toArray());
  }
  
  public FieldVector<T> projection(FieldVector<T> v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    return v.mapMultiply((FieldElement)dotProduct(v).divide(v.dotProduct(v)));
  }
  
  public FieldVector<T> projection(T[] v) throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    return projection(new SparseFieldVector(field, v));
  }
  
  public void set(T value)
  {
    for (int i = 0; i < virtualSize; i++) {
      setEntry(i, value);
    }
  }
  
  public void setEntry(int index, T value) throws MatrixIndexException
  {
    checkIndex(index);
    entries.put(index, value);
  }
  
  public void setSubVector(int index, FieldVector<T> v)
    throws MatrixIndexException
  {
    checkIndex(index);
    checkIndex(index + v.getDimension() - 1);
    setSubVector(index, v.getData());
  }
  
  public void setSubVector(int index, T[] v) throws MatrixIndexException
  {
    checkIndex(index);
    checkIndex(index + v.length - 1);
    for (int i = 0; i < v.length; i++) {
      setEntry(i + index, v[i]);
    }
  }
  





  public SparseFieldVector<T> subtract(SparseFieldVector<T> v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
    SparseFieldVector<T> res = (SparseFieldVector)copy();
    OpenIntToFieldHashMap<T>.Iterator iter = v.getEntries().iterator();
    while (iter.hasNext()) {
      iter.advance();
      int key = iter.key();
      if (entries.containsKey(key)) {
        res.setEntry(key, (FieldElement)entries.get(key).subtract(iter.value()));
      } else {
        res.setEntry(key, (FieldElement)((FieldElement)field.getZero()).subtract(iter.value()));
      }
    }
    return res;
  }
  
  public FieldVector<T> subtract(FieldVector<T> v)
    throws IllegalArgumentException
  {
    if ((v instanceof SparseFieldVector)) {
      return subtract((SparseFieldVector)v);
    }
    return subtract(v.toArray());
  }
  
  public FieldVector<T> subtract(T[] v) throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    SparseFieldVector<T> res = new SparseFieldVector(this);
    for (int i = 0; i < v.length; i++) {
      if (entries.containsKey(i)) {
        res.setEntry(i, (FieldElement)entries.get(i).subtract(v[i]));
      } else {
        res.setEntry(i, (FieldElement)((FieldElement)field.getZero()).subtract(v[i]));
      }
    }
    return res;
  }
  
  public T[] toArray()
  {
    return getData();
  }
  






  private void checkIndex(int index)
    throws MatrixIndexException
  {
    if ((index < 0) || (index >= getDimension())) {
      throw new MatrixIndexException(LocalizedFormats.INDEX_OUT_OF_RANGE, new Object[] { Integer.valueOf(index), Integer.valueOf(0), Integer.valueOf(getDimension() - 1) });
    }
  }
  







  protected void checkVectorDimensions(int n)
    throws IllegalArgumentException
  {
    if (getDimension() != n) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(getDimension()), Integer.valueOf(n) });
    }
  }
  


  public FieldVector<T> add(FieldVector<T> v)
    throws IllegalArgumentException
  {
    if ((v instanceof SparseFieldVector)) {
      return add((SparseFieldVector)v);
    }
    return add(v.toArray());
  }
  





  private T[] buildArray(int length)
  {
    return (FieldElement[])Array.newInstance(((FieldElement)field.getZero()).getClass(), length);
  }
  


  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (field == null ? 0 : field.hashCode());
    result = 31 * result + virtualSize;
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      int temp = iter.value().hashCode();
      result = 31 * result + temp;
    }
    return result;
  }
  



  public boolean equals(Object obj)
  {
    if (this == obj) {
      return true;
    }
    
    if (!(obj instanceof SparseFieldVector)) {
      return false;
    }
    


    SparseFieldVector<T> other = (SparseFieldVector)obj;
    if (field == null) {
      if (field != null) {
        return false;
      }
    } else if (!field.equals(field)) {
      return false;
    }
    if (virtualSize != virtualSize) {
      return false;
    }
    
    OpenIntToFieldHashMap<T>.Iterator iter = entries.iterator();
    while (iter.hasNext()) {
      iter.advance();
      T test = other.getEntry(iter.key());
      if (!test.equals(iter.value())) {
        return false;
      }
    }
    iter = other.getEntries().iterator();
    while (iter.hasNext()) {
      iter.advance();
      T test = iter.value();
      if (!test.equals(getEntry(iter.key()))) {
        return false;
      }
    }
    return true;
  }
}
