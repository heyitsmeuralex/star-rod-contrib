package org.apache.commons.math.linear;

import org.apache.commons.math.Field;
import org.apache.commons.math.FieldElement;

public abstract interface FieldMatrix<T extends FieldElement<T>>
  extends AnyMatrix
{
  public abstract Field<T> getField();
  
  public abstract FieldMatrix<T> createMatrix(int paramInt1, int paramInt2);
  
  public abstract FieldMatrix<T> copy();
  
  public abstract FieldMatrix<T> add(FieldMatrix<T> paramFieldMatrix)
    throws IllegalArgumentException;
  
  public abstract FieldMatrix<T> subtract(FieldMatrix<T> paramFieldMatrix)
    throws IllegalArgumentException;
  
  public abstract FieldMatrix<T> scalarAdd(T paramT);
  
  public abstract FieldMatrix<T> scalarMultiply(T paramT);
  
  public abstract FieldMatrix<T> multiply(FieldMatrix<T> paramFieldMatrix)
    throws IllegalArgumentException;
  
  public abstract FieldMatrix<T> preMultiply(FieldMatrix<T> paramFieldMatrix)
    throws IllegalArgumentException;
  
  public abstract T[][] getData();
  
  public abstract FieldMatrix<T> getSubMatrix(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException;
  
  public abstract FieldMatrix<T> getSubMatrix(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    throws MatrixIndexException;
  
  public abstract void copySubMatrix(int paramInt1, int paramInt2, int paramInt3, int paramInt4, T[][] paramArrayOfT)
    throws MatrixIndexException, IllegalArgumentException;
  
  public abstract void copySubMatrix(int[] paramArrayOfInt1, int[] paramArrayOfInt2, T[][] paramArrayOfT)
    throws MatrixIndexException, IllegalArgumentException;
  
  public abstract void setSubMatrix(T[][] paramArrayOfT, int paramInt1, int paramInt2)
    throws MatrixIndexException;
  
  public abstract FieldMatrix<T> getRowMatrix(int paramInt)
    throws MatrixIndexException;
  
  public abstract void setRowMatrix(int paramInt, FieldMatrix<T> paramFieldMatrix)
    throws MatrixIndexException, InvalidMatrixException;
  
  public abstract FieldMatrix<T> getColumnMatrix(int paramInt)
    throws MatrixIndexException;
  
  public abstract void setColumnMatrix(int paramInt, FieldMatrix<T> paramFieldMatrix)
    throws MatrixIndexException, InvalidMatrixException;
  
  public abstract FieldVector<T> getRowVector(int paramInt)
    throws MatrixIndexException;
  
  public abstract void setRowVector(int paramInt, FieldVector<T> paramFieldVector)
    throws MatrixIndexException, InvalidMatrixException;
  
  public abstract FieldVector<T> getColumnVector(int paramInt)
    throws MatrixIndexException;
  
  public abstract void setColumnVector(int paramInt, FieldVector<T> paramFieldVector)
    throws MatrixIndexException, InvalidMatrixException;
  
  public abstract T[] getRow(int paramInt)
    throws MatrixIndexException;
  
  public abstract void setRow(int paramInt, T[] paramArrayOfT)
    throws MatrixIndexException, InvalidMatrixException;
  
  public abstract T[] getColumn(int paramInt)
    throws MatrixIndexException;
  
  public abstract void setColumn(int paramInt, T[] paramArrayOfT)
    throws MatrixIndexException, InvalidMatrixException;
  
  public abstract T getEntry(int paramInt1, int paramInt2)
    throws MatrixIndexException;
  
  public abstract void setEntry(int paramInt1, int paramInt2, T paramT)
    throws MatrixIndexException;
  
  public abstract void addToEntry(int paramInt1, int paramInt2, T paramT)
    throws MatrixIndexException;
  
  public abstract void multiplyEntry(int paramInt1, int paramInt2, T paramT)
    throws MatrixIndexException;
  
  public abstract FieldMatrix<T> transpose();
  
  public abstract T getTrace()
    throws NonSquareMatrixException;
  
  public abstract T[] operate(T[] paramArrayOfT)
    throws IllegalArgumentException;
  
  public abstract FieldVector<T> operate(FieldVector<T> paramFieldVector)
    throws IllegalArgumentException;
  
  public abstract T[] preMultiply(T[] paramArrayOfT)
    throws IllegalArgumentException;
  
  public abstract FieldVector<T> preMultiply(FieldVector<T> paramFieldVector)
    throws IllegalArgumentException;
  
  public abstract T walkInRowOrder(FieldMatrixChangingVisitor<T> paramFieldMatrixChangingVisitor)
    throws MatrixVisitorException;
  
  public abstract T walkInRowOrder(FieldMatrixPreservingVisitor<T> paramFieldMatrixPreservingVisitor)
    throws MatrixVisitorException;
  
  public abstract T walkInRowOrder(FieldMatrixChangingVisitor<T> paramFieldMatrixChangingVisitor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException, MatrixVisitorException;
  
  public abstract T walkInRowOrder(FieldMatrixPreservingVisitor<T> paramFieldMatrixPreservingVisitor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException, MatrixVisitorException;
  
  public abstract T walkInColumnOrder(FieldMatrixChangingVisitor<T> paramFieldMatrixChangingVisitor)
    throws MatrixVisitorException;
  
  public abstract T walkInColumnOrder(FieldMatrixPreservingVisitor<T> paramFieldMatrixPreservingVisitor)
    throws MatrixVisitorException;
  
  public abstract T walkInColumnOrder(FieldMatrixChangingVisitor<T> paramFieldMatrixChangingVisitor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException, MatrixVisitorException;
  
  public abstract T walkInColumnOrder(FieldMatrixPreservingVisitor<T> paramFieldMatrixPreservingVisitor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException, MatrixVisitorException;
  
  public abstract T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> paramFieldMatrixChangingVisitor)
    throws MatrixVisitorException;
  
  public abstract T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> paramFieldMatrixPreservingVisitor)
    throws MatrixVisitorException;
  
  public abstract T walkInOptimizedOrder(FieldMatrixChangingVisitor<T> paramFieldMatrixChangingVisitor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException, MatrixVisitorException;
  
  public abstract T walkInOptimizedOrder(FieldMatrixPreservingVisitor<T> paramFieldMatrixPreservingVisitor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException, MatrixVisitorException;
}
