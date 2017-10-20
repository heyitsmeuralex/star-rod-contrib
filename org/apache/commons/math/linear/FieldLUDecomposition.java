package org.apache.commons.math.linear;

import org.apache.commons.math.FieldElement;

public abstract interface FieldLUDecomposition<T extends FieldElement<T>>
{
  public abstract FieldMatrix<T> getL();
  
  public abstract FieldMatrix<T> getU();
  
  public abstract FieldMatrix<T> getP();
  
  public abstract int[] getPivot();
  
  public abstract T getDeterminant();
  
  public abstract FieldDecompositionSolver<T> getSolver();
}
