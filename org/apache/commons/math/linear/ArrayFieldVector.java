package org.apache.commons.math.linear;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;





































public class ArrayFieldVector<T extends FieldElement<T>>
  implements FieldVector<T>, Serializable
{
  private static final long serialVersionUID = 7648186910365927050L;
  protected T[] data;
  private final Field<T> field;
  
  public ArrayFieldVector(Field<T> field)
  {
    this(field, 0);
  }
  




  public ArrayFieldVector(Field<T> field, int size)
  {
    this.field = field;
    data = buildArray(size);
    Arrays.fill(data, field.getZero());
  }
  




  public ArrayFieldVector(int size, T preset)
  {
    this(preset.getField(), size);
    Arrays.fill(data, preset);
  }
  










  public ArrayFieldVector(T[] d)
    throws IllegalArgumentException
  {
    try
    {
      field = d[0].getField();
      data = ((FieldElement[])d.clone());
    } catch (ArrayIndexOutOfBoundsException e) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
    }
  }
  






  public ArrayFieldVector(Field<T> field, T[] d)
  {
    this.field = field;
    data = ((FieldElement[])d.clone());
  }
  




















  public ArrayFieldVector(T[] d, boolean copyArray)
    throws NullPointerException, IllegalArgumentException
  {
    if (d.length == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
    }
    
    field = d[0].getField();
    data = (copyArray ? (FieldElement[])d.clone() : d);
  }
  












  public ArrayFieldVector(Field<T> field, T[] d, boolean copyArray)
  {
    this.field = field;
    data = (copyArray ? (FieldElement[])d.clone() : d);
  }
  





  public ArrayFieldVector(T[] d, int pos, int size)
  {
    if (d.length < pos + size) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.POSITION_SIZE_MISMATCH_INPUT_ARRAY, new Object[] { Integer.valueOf(pos), Integer.valueOf(size), Integer.valueOf(d.length) });
    }
    

    field = d[0].getField();
    data = buildArray(size);
    System.arraycopy(d, pos, data, 0, size);
  }
  



  public ArrayFieldVector(FieldVector<T> v)
  {
    field = v.getField();
    data = buildArray(v.getDimension());
    for (int i = 0; i < data.length; i++) {
      data[i] = v.getEntry(i);
    }
  }
  



  public ArrayFieldVector(ArrayFieldVector<T> v)
  {
    field = v.getField();
    data = ((FieldElement[])data.clone());
  }
  




  public ArrayFieldVector(ArrayFieldVector<T> v, boolean deep)
  {
    field = v.getField();
    data = (deep ? (FieldElement[])data.clone() : data);
  }
  




  public ArrayFieldVector(ArrayFieldVector<T> v1, ArrayFieldVector<T> v2)
  {
    field = v1.getField();
    data = buildArray(data.length + data.length);
    System.arraycopy(data, 0, data, 0, data.length);
    System.arraycopy(data, 0, data, data.length, data.length);
  }
  




  public ArrayFieldVector(ArrayFieldVector<T> v1, T[] v2)
  {
    field = v1.getField();
    data = buildArray(data.length + v2.length);
    System.arraycopy(data, 0, data, 0, data.length);
    System.arraycopy(v2, 0, data, data.length, v2.length);
  }
  




  public ArrayFieldVector(T[] v1, ArrayFieldVector<T> v2)
  {
    field = v2.getField();
    data = buildArray(v1.length + data.length);
    System.arraycopy(v1, 0, data, 0, v1.length);
    System.arraycopy(data, 0, data, v1.length, data.length);
  }
  











  public ArrayFieldVector(T[] v1, T[] v2)
  {
    try
    {
      data = buildArray(v1.length + v2.length);
      System.arraycopy(v1, 0, data, 0, v1.length);
      System.arraycopy(v2, 0, data, v1.length, v2.length);
      field = data[0].getField();
    } catch (ArrayIndexOutOfBoundsException e) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
    }
  }
  







  public ArrayFieldVector(Field<T> field, T[] v1, T[] v2)
  {
    if (v1.length + v2.length == 0) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_MUST_HAVE_AT_LEAST_ONE_ELEMENT, new Object[0]);
    }
    
    data = buildArray(v1.length + v2.length);
    System.arraycopy(v1, 0, data, 0, v1.length);
    System.arraycopy(v2, 0, data, v1.length, v2.length);
    this.field = data[0].getField();
  }
  




  private T[] buildArray(int length)
  {
    return (FieldElement[])Array.newInstance(((FieldElement)field.getZero()).getClass(), length);
  }
  
  public Field<T> getField()
  {
    return field;
  }
  
  public FieldVector<T> copy()
  {
    return new ArrayFieldVector(this, true);
  }
  
  public FieldVector<T> add(FieldVector<T> v) throws IllegalArgumentException
  {
    try {
      return add((ArrayFieldVector)v);
    } catch (ClassCastException cce) {
      checkVectorDimensions(v);
      T[] out = buildArray(data.length);
      for (int i = 0; i < data.length; i++) {
        out[i] = ((FieldElement)data[i].add(v.getEntry(i)));
      }
      return new ArrayFieldVector(out);
    }
  }
  
  public FieldVector<T> add(T[] v) throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    T[] out = buildArray(data.length);
    for (int i = 0; i < data.length; i++) {
      out[i] = ((FieldElement)data[i].add(v[i]));
    }
    return new ArrayFieldVector(out);
  }
  





  public ArrayFieldVector<T> add(ArrayFieldVector<T> v)
    throws IllegalArgumentException
  {
    return (ArrayFieldVector)add(data);
  }
  
  public FieldVector<T> subtract(FieldVector<T> v) throws IllegalArgumentException
  {
    try {
      return subtract((ArrayFieldVector)v);
    } catch (ClassCastException cce) {
      checkVectorDimensions(v);
      T[] out = buildArray(data.length);
      for (int i = 0; i < data.length; i++) {
        out[i] = ((FieldElement)data[i].subtract(v.getEntry(i)));
      }
      return new ArrayFieldVector(out);
    }
  }
  
  public FieldVector<T> subtract(T[] v) throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    T[] out = buildArray(data.length);
    for (int i = 0; i < data.length; i++) {
      out[i] = ((FieldElement)data[i].subtract(v[i]));
    }
    return new ArrayFieldVector(out);
  }
  





  public ArrayFieldVector<T> subtract(ArrayFieldVector<T> v)
    throws IllegalArgumentException
  {
    return (ArrayFieldVector)subtract(data);
  }
  
  public FieldVector<T> mapAdd(T d)
  {
    T[] out = buildArray(data.length);
    for (int i = 0; i < data.length; i++) {
      out[i] = ((FieldElement)data[i].add(d));
    }
    return new ArrayFieldVector(out);
  }
  
  public FieldVector<T> mapAddToSelf(T d)
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = ((FieldElement)data[i].add(d));
    }
    return this;
  }
  
  public FieldVector<T> mapSubtract(T d)
  {
    T[] out = buildArray(data.length);
    for (int i = 0; i < data.length; i++) {
      out[i] = ((FieldElement)data[i].subtract(d));
    }
    return new ArrayFieldVector(out);
  }
  
  public FieldVector<T> mapSubtractToSelf(T d)
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = ((FieldElement)data[i].subtract(d));
    }
    return this;
  }
  
  public FieldVector<T> mapMultiply(T d)
  {
    T[] out = buildArray(data.length);
    for (int i = 0; i < data.length; i++) {
      out[i] = ((FieldElement)data[i].multiply(d));
    }
    return new ArrayFieldVector(out);
  }
  
  public FieldVector<T> mapMultiplyToSelf(T d)
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = ((FieldElement)data[i].multiply(d));
    }
    return this;
  }
  
  public FieldVector<T> mapDivide(T d)
  {
    T[] out = buildArray(data.length);
    for (int i = 0; i < data.length; i++) {
      out[i] = ((FieldElement)data[i].divide(d));
    }
    return new ArrayFieldVector(out);
  }
  
  public FieldVector<T> mapDivideToSelf(T d)
  {
    for (int i = 0; i < data.length; i++) {
      data[i] = ((FieldElement)data[i].divide(d));
    }
    return this;
  }
  
  public FieldVector<T> mapInv()
  {
    T[] out = buildArray(data.length);
    T one = (FieldElement)field.getOne();
    for (int i = 0; i < data.length; i++) {
      out[i] = ((FieldElement)one.divide(data[i]));
    }
    return new ArrayFieldVector(out);
  }
  
  public FieldVector<T> mapInvToSelf()
  {
    T one = (FieldElement)field.getOne();
    for (int i = 0; i < data.length; i++) {
      data[i] = ((FieldElement)one.divide(data[i]));
    }
    return this;
  }
  
  public FieldVector<T> ebeMultiply(FieldVector<T> v) throws IllegalArgumentException
  {
    try
    {
      return ebeMultiply((ArrayFieldVector)v);
    } catch (ClassCastException cce) {
      checkVectorDimensions(v);
      T[] out = buildArray(data.length);
      for (int i = 0; i < data.length; i++) {
        out[i] = ((FieldElement)data[i].multiply(v.getEntry(i)));
      }
      return new ArrayFieldVector(out);
    }
  }
  
  public FieldVector<T> ebeMultiply(T[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    T[] out = buildArray(data.length);
    for (int i = 0; i < data.length; i++) {
      out[i] = ((FieldElement)data[i].multiply(v[i]));
    }
    return new ArrayFieldVector(out);
  }
  





  public ArrayFieldVector<T> ebeMultiply(ArrayFieldVector<T> v)
    throws IllegalArgumentException
  {
    return (ArrayFieldVector)ebeMultiply(data);
  }
  
  public FieldVector<T> ebeDivide(FieldVector<T> v) throws IllegalArgumentException
  {
    try
    {
      return ebeDivide((ArrayFieldVector)v);
    } catch (ClassCastException cce) {
      checkVectorDimensions(v);
      T[] out = buildArray(data.length);
      for (int i = 0; i < data.length; i++) {
        out[i] = ((FieldElement)data[i].divide(v.getEntry(i)));
      }
      return new ArrayFieldVector(out);
    }
  }
  
  public FieldVector<T> ebeDivide(T[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    T[] out = buildArray(data.length);
    for (int i = 0; i < data.length; i++) {
      out[i] = ((FieldElement)data[i].divide(v[i]));
    }
    return new ArrayFieldVector(out);
  }
  





  public ArrayFieldVector<T> ebeDivide(ArrayFieldVector<T> v)
    throws IllegalArgumentException
  {
    return (ArrayFieldVector)ebeDivide(data);
  }
  
  public T[] getData()
  {
    return (FieldElement[])data.clone();
  }
  




  public T[] getDataRef()
  {
    return data;
  }
  
  public T dotProduct(FieldVector<T> v) throws IllegalArgumentException
  {
    try
    {
      return dotProduct((ArrayFieldVector)v);
    } catch (ClassCastException cce) {
      checkVectorDimensions(v);
      T dot = (FieldElement)field.getZero();
      for (int i = 0; i < data.length; i++) {
        dot = (FieldElement)dot.add(data[i].multiply(v.getEntry(i)));
      }
      return dot;
    }
  }
  
  public T dotProduct(T[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    T dot = (FieldElement)field.getZero();
    for (int i = 0; i < data.length; i++) {
      dot = (FieldElement)dot.add(data[i].multiply(v[i]));
    }
    return dot;
  }
  





  public T dotProduct(ArrayFieldVector<T> v)
    throws IllegalArgumentException
  {
    return dotProduct(data);
  }
  
  public FieldVector<T> projection(FieldVector<T> v)
  {
    return v.mapMultiply((FieldElement)dotProduct(v).divide(v.dotProduct(v)));
  }
  
  public FieldVector<T> projection(T[] v)
  {
    return projection(new ArrayFieldVector(v, false));
  }
  




  public ArrayFieldVector<T> projection(ArrayFieldVector<T> v)
  {
    return (ArrayFieldVector)v.mapMultiply((FieldElement)dotProduct(v).divide(v.dotProduct(v)));
  }
  
  public FieldMatrix<T> outerProduct(FieldVector<T> v) throws IllegalArgumentException
  {
    try
    {
      return outerProduct((ArrayFieldVector)v);
    } catch (ClassCastException cce) {
      checkVectorDimensions(v);
      int m = data.length;
      FieldMatrix<T> out = new Array2DRowFieldMatrix(field, m, m);
      for (int i = 0; i < data.length; i++) {
        for (int j = 0; j < data.length; j++) {
          out.setEntry(i, j, (FieldElement)data[i].multiply(v.getEntry(j)));
        }
      }
      return out;
    }
  }
  





  public FieldMatrix<T> outerProduct(ArrayFieldVector<T> v)
    throws IllegalArgumentException
  {
    return outerProduct(data);
  }
  
  public FieldMatrix<T> outerProduct(T[] v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.length);
    int m = data.length;
    FieldMatrix<T> out = new Array2DRowFieldMatrix(field, m, m);
    for (int i = 0; i < data.length; i++) {
      for (int j = 0; j < data.length; j++) {
        out.setEntry(i, j, (FieldElement)data[i].multiply(v[j]));
      }
    }
    return out;
  }
  
  public T getEntry(int index) throws MatrixIndexException
  {
    return data[index];
  }
  
  public int getDimension()
  {
    return data.length;
  }
  
  public FieldVector<T> append(FieldVector<T> v)
  {
    try {
      return append((ArrayFieldVector)v);
    } catch (ClassCastException cce) {}
    return new ArrayFieldVector(this, new ArrayFieldVector(v));
  }
  





  public ArrayFieldVector<T> append(ArrayFieldVector<T> v)
  {
    return new ArrayFieldVector(this, v);
  }
  
  public FieldVector<T> append(T in)
  {
    T[] out = buildArray(data.length + 1);
    System.arraycopy(data, 0, out, 0, data.length);
    out[data.length] = in;
    return new ArrayFieldVector(out);
  }
  
  public FieldVector<T> append(T[] in)
  {
    return new ArrayFieldVector(this, in);
  }
  
  public FieldVector<T> getSubVector(int index, int n)
  {
    ArrayFieldVector<T> out = new ArrayFieldVector(field, n);
    try {
      System.arraycopy(data, index, data, 0, n);
    } catch (IndexOutOfBoundsException e) {
      checkIndex(index);
      checkIndex(index + n - 1);
    }
    return out;
  }
  
  public void setEntry(int index, T value)
  {
    try {
      data[index] = value;
    } catch (IndexOutOfBoundsException e) {
      checkIndex(index);
    }
  }
  
  public void setSubVector(int index, FieldVector<T> v)
  {
    try {
      try {
        set(index, (ArrayFieldVector)v);
      } catch (ClassCastException cce) {
        for (int i = index; i < index + v.getDimension(); i++) {
          data[i] = v.getEntry(i - index);
        }
      }
    } catch (IndexOutOfBoundsException e) {
      checkIndex(index);
      checkIndex(index + v.getDimension() - 1);
    }
  }
  
  public void setSubVector(int index, T[] v)
  {
    try {
      System.arraycopy(v, 0, data, index, v.length);
    } catch (IndexOutOfBoundsException e) {
      checkIndex(index);
      checkIndex(index + v.length - 1);
    }
  }
  







  public void set(int index, ArrayFieldVector<T> v)
    throws MatrixIndexException
  {
    setSubVector(index, data);
  }
  
  public void set(T value)
  {
    Arrays.fill(data, value);
  }
  
  public T[] toArray()
  {
    return (FieldElement[])data.clone();
  }
  





  protected void checkVectorDimensions(FieldVector<T> v)
    throws IllegalArgumentException
  {
    checkVectorDimensions(v.getDimension());
  }
  






  protected void checkVectorDimensions(int n)
    throws IllegalArgumentException
  {
    if (data.length != n) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.VECTOR_LENGTH_MISMATCH, new Object[] { Integer.valueOf(data.length), Integer.valueOf(n) });
    }
  }
  






















  public boolean equals(Object other)
  {
    if (this == other) {
      return true;
    }
    
    if (other == null) {
      return false;
    }
    
    try
    {
      FieldVector<T> rhs = (FieldVector)other;
      if (data.length != rhs.getDimension()) {
        return false;
      }
      
      for (int i = 0; i < data.length; i++) {
        if (!data[i].equals(rhs.getEntry(i))) {
          return false;
        }
      }
      return true;
    }
    catch (ClassCastException ex) {}
    
    return false;
  }
  







  public int hashCode()
  {
    int h = 3542;
    for (T a : data) {
      h ^= a.hashCode();
    }
    return h;
  }
  




  private void checkIndex(int index)
    throws MatrixIndexException
  {
    if ((index < 0) || (index >= getDimension())) {
      throw new MatrixIndexException(LocalizedFormats.INDEX_OUT_OF_RANGE, new Object[] { Integer.valueOf(index), Integer.valueOf(0), Integer.valueOf(getDimension() - 1) });
    }
  }
}
