package org.apache.commons.math.linear;

import org.apache.commons.math.FieldElement;

public abstract interface FieldDecompositionSolver<T extends FieldElement<T>>
{
  public abstract T[] solve(T[] paramArrayOfT)
    throws IllegalArgumentException, InvalidMatrixException;
  
  public abstract FieldVector<T> solve(FieldVector<T> paramFieldVector)
    throws IllegalArgumentException, InvalidMatrixException;
  
  public abstract FieldMatrix<T> solve(FieldMatrix<T> paramFieldMatrix)
    throws IllegalArgumentException, InvalidMatrixException;
  
  public abstract boolean isNonSingular();
  
  public abstract FieldMatrix<T> getInverse()
    throws InvalidMatrixException;
}
