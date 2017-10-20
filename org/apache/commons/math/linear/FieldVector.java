package org.apache.commons.math.linear;

import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;

public abstract interface FieldVector<T extends FieldElement<T>>
{
  public abstract Field<T> getField();
  
  public abstract FieldVector<T> copy();
  
  public abstract FieldVector<T> add(FieldVector<T> paramFieldVector)
    throws IllegalArgumentException;
  
  public abstract FieldVector<T> add(T[] paramArrayOfT)
    throws IllegalArgumentException;
  
  public abstract FieldVector<T> subtract(FieldVector<T> paramFieldVector)
    throws IllegalArgumentException;
  
  public abstract FieldVector<T> subtract(T[] paramArrayOfT)
    throws IllegalArgumentException;
  
  public abstract FieldVector<T> mapAdd(T paramT);
  
  public abstract FieldVector<T> mapAddToSelf(T paramT);
  
  public abstract FieldVector<T> mapSubtract(T paramT);
  
  public abstract FieldVector<T> mapSubtractToSelf(T paramT);
  
  public abstract FieldVector<T> mapMultiply(T paramT);
  
  public abstract FieldVector<T> mapMultiplyToSelf(T paramT);
  
  public abstract FieldVector<T> mapDivide(T paramT);
  
  public abstract FieldVector<T> mapDivideToSelf(T paramT);
  
  public abstract FieldVector<T> mapInv();
  
  public abstract FieldVector<T> mapInvToSelf();
  
  public abstract FieldVector<T> ebeMultiply(FieldVector<T> paramFieldVector)
    throws IllegalArgumentException;
  
  public abstract FieldVector<T> ebeMultiply(T[] paramArrayOfT)
    throws IllegalArgumentException;
  
  public abstract FieldVector<T> ebeDivide(FieldVector<T> paramFieldVector)
    throws IllegalArgumentException;
  
  public abstract FieldVector<T> ebeDivide(T[] paramArrayOfT)
    throws IllegalArgumentException;
  
  public abstract T[] getData();
  
  public abstract T dotProduct(FieldVector<T> paramFieldVector)
    throws IllegalArgumentException;
  
  public abstract T dotProduct(T[] paramArrayOfT)
    throws IllegalArgumentException;
  
  public abstract FieldVector<T> projection(FieldVector<T> paramFieldVector)
    throws IllegalArgumentException;
  
  public abstract FieldVector<T> projection(T[] paramArrayOfT)
    throws IllegalArgumentException;
  
  public abstract FieldMatrix<T> outerProduct(FieldVector<T> paramFieldVector)
    throws IllegalArgumentException;
  
  public abstract FieldMatrix<T> outerProduct(T[] paramArrayOfT)
    throws IllegalArgumentException;
  
  public abstract T getEntry(int paramInt)
    throws MatrixIndexException;
  
  public abstract void setEntry(int paramInt, T paramT)
    throws MatrixIndexException;
  
  public abstract int getDimension();
  
  public abstract FieldVector<T> append(FieldVector<T> paramFieldVector);
  
  public abstract FieldVector<T> append(T paramT);
  
  public abstract FieldVector<T> append(T[] paramArrayOfT);
  
  public abstract FieldVector<T> getSubVector(int paramInt1, int paramInt2)
    throws MatrixIndexException;
  
  public abstract void setSubVector(int paramInt, FieldVector<T> paramFieldVector)
    throws MatrixIndexException;
  
  public abstract void setSubVector(int paramInt, T[] paramArrayOfT)
    throws MatrixIndexException;
  
  public abstract void set(T paramT);
  
  public abstract T[] toArray();
}
