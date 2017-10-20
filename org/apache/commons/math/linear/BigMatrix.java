package org.apache.commons.math.linear;

import java.math.BigDecimal;

@Deprecated
public abstract interface BigMatrix
  extends AnyMatrix
{
  public abstract BigMatrix copy();
  
  public abstract BigMatrix add(BigMatrix paramBigMatrix)
    throws IllegalArgumentException;
  
  public abstract BigMatrix subtract(BigMatrix paramBigMatrix)
    throws IllegalArgumentException;
  
  public abstract BigMatrix scalarAdd(BigDecimal paramBigDecimal);
  
  public abstract BigMatrix scalarMultiply(BigDecimal paramBigDecimal);
  
  public abstract BigMatrix multiply(BigMatrix paramBigMatrix)
    throws IllegalArgumentException;
  
  public abstract BigMatrix preMultiply(BigMatrix paramBigMatrix)
    throws IllegalArgumentException;
  
  public abstract BigDecimal[][] getData();
  
  public abstract double[][] getDataAsDoubleArray();
  
  public abstract int getRoundingMode();
  
  public abstract BigDecimal getNorm();
  
  public abstract BigMatrix getSubMatrix(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException;
  
  public abstract BigMatrix getSubMatrix(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    throws MatrixIndexException;
  
  public abstract BigMatrix getRowMatrix(int paramInt)
    throws MatrixIndexException;
  
  public abstract BigMatrix getColumnMatrix(int paramInt)
    throws MatrixIndexException;
  
  public abstract BigDecimal[] getRow(int paramInt)
    throws MatrixIndexException;
  
  public abstract double[] getRowAsDoubleArray(int paramInt)
    throws MatrixIndexException;
  
  public abstract BigDecimal[] getColumn(int paramInt)
    throws MatrixIndexException;
  
  public abstract double[] getColumnAsDoubleArray(int paramInt)
    throws MatrixIndexException;
  
  public abstract BigDecimal getEntry(int paramInt1, int paramInt2)
    throws MatrixIndexException;
  
  public abstract double getEntryAsDouble(int paramInt1, int paramInt2)
    throws MatrixIndexException;
  
  public abstract BigMatrix transpose();
  
  public abstract BigMatrix inverse()
    throws InvalidMatrixException;
  
  public abstract BigDecimal getDeterminant()
    throws InvalidMatrixException;
  
  public abstract BigDecimal getTrace();
  
  public abstract BigDecimal[] operate(BigDecimal[] paramArrayOfBigDecimal)
    throws IllegalArgumentException;
  
  public abstract BigDecimal[] preMultiply(BigDecimal[] paramArrayOfBigDecimal)
    throws IllegalArgumentException;
  
  public abstract BigDecimal[] solve(BigDecimal[] paramArrayOfBigDecimal)
    throws IllegalArgumentException, InvalidMatrixException;
  
  public abstract BigMatrix solve(BigMatrix paramBigMatrix)
    throws IllegalArgumentException, InvalidMatrixException;
}
