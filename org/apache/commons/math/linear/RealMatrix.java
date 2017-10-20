package org.apache.commons.math.linear;

public abstract interface RealMatrix
  extends AnyMatrix
{
  public abstract RealMatrix createMatrix(int paramInt1, int paramInt2);
  
  public abstract RealMatrix copy();
  
  public abstract RealMatrix add(RealMatrix paramRealMatrix)
    throws IllegalArgumentException;
  
  public abstract RealMatrix subtract(RealMatrix paramRealMatrix)
    throws IllegalArgumentException;
  
  public abstract RealMatrix scalarAdd(double paramDouble);
  
  public abstract RealMatrix scalarMultiply(double paramDouble);
  
  public abstract RealMatrix multiply(RealMatrix paramRealMatrix)
    throws IllegalArgumentException;
  
  public abstract RealMatrix preMultiply(RealMatrix paramRealMatrix)
    throws IllegalArgumentException;
  
  public abstract double[][] getData();
  
  public abstract double getNorm();
  
  public abstract double getFrobeniusNorm();
  
  public abstract RealMatrix getSubMatrix(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException;
  
  public abstract RealMatrix getSubMatrix(int[] paramArrayOfInt1, int[] paramArrayOfInt2)
    throws MatrixIndexException;
  
  public abstract void copySubMatrix(int paramInt1, int paramInt2, int paramInt3, int paramInt4, double[][] paramArrayOfDouble)
    throws MatrixIndexException, IllegalArgumentException;
  
  public abstract void copySubMatrix(int[] paramArrayOfInt1, int[] paramArrayOfInt2, double[][] paramArrayOfDouble)
    throws MatrixIndexException, IllegalArgumentException;
  
  public abstract void setSubMatrix(double[][] paramArrayOfDouble, int paramInt1, int paramInt2)
    throws MatrixIndexException;
  
  public abstract RealMatrix getRowMatrix(int paramInt)
    throws MatrixIndexException;
  
  public abstract void setRowMatrix(int paramInt, RealMatrix paramRealMatrix)
    throws MatrixIndexException, InvalidMatrixException;
  
  public abstract RealMatrix getColumnMatrix(int paramInt)
    throws MatrixIndexException;
  
  public abstract void setColumnMatrix(int paramInt, RealMatrix paramRealMatrix)
    throws MatrixIndexException, InvalidMatrixException;
  
  public abstract RealVector getRowVector(int paramInt)
    throws MatrixIndexException;
  
  public abstract void setRowVector(int paramInt, RealVector paramRealVector)
    throws MatrixIndexException, InvalidMatrixException;
  
  public abstract RealVector getColumnVector(int paramInt)
    throws MatrixIndexException;
  
  public abstract void setColumnVector(int paramInt, RealVector paramRealVector)
    throws MatrixIndexException, InvalidMatrixException;
  
  public abstract double[] getRow(int paramInt)
    throws MatrixIndexException;
  
  public abstract void setRow(int paramInt, double[] paramArrayOfDouble)
    throws MatrixIndexException, InvalidMatrixException;
  
  public abstract double[] getColumn(int paramInt)
    throws MatrixIndexException;
  
  public abstract void setColumn(int paramInt, double[] paramArrayOfDouble)
    throws MatrixIndexException, InvalidMatrixException;
  
  public abstract double getEntry(int paramInt1, int paramInt2)
    throws MatrixIndexException;
  
  public abstract void setEntry(int paramInt1, int paramInt2, double paramDouble)
    throws MatrixIndexException;
  
  public abstract void addToEntry(int paramInt1, int paramInt2, double paramDouble)
    throws MatrixIndexException;
  
  public abstract void multiplyEntry(int paramInt1, int paramInt2, double paramDouble)
    throws MatrixIndexException;
  
  public abstract RealMatrix transpose();
  
  @Deprecated
  public abstract RealMatrix inverse()
    throws InvalidMatrixException;
  
  @Deprecated
  public abstract double getDeterminant();
  
  @Deprecated
  public abstract boolean isSingular();
  
  public abstract double getTrace()
    throws NonSquareMatrixException;
  
  public abstract double[] operate(double[] paramArrayOfDouble)
    throws IllegalArgumentException;
  
  public abstract RealVector operate(RealVector paramRealVector)
    throws IllegalArgumentException;
  
  public abstract double[] preMultiply(double[] paramArrayOfDouble)
    throws IllegalArgumentException;
  
  public abstract RealVector preMultiply(RealVector paramRealVector)
    throws IllegalArgumentException;
  
  public abstract double walkInRowOrder(RealMatrixChangingVisitor paramRealMatrixChangingVisitor)
    throws MatrixVisitorException;
  
  public abstract double walkInRowOrder(RealMatrixPreservingVisitor paramRealMatrixPreservingVisitor)
    throws MatrixVisitorException;
  
  public abstract double walkInRowOrder(RealMatrixChangingVisitor paramRealMatrixChangingVisitor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException, MatrixVisitorException;
  
  public abstract double walkInRowOrder(RealMatrixPreservingVisitor paramRealMatrixPreservingVisitor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException, MatrixVisitorException;
  
  public abstract double walkInColumnOrder(RealMatrixChangingVisitor paramRealMatrixChangingVisitor)
    throws MatrixVisitorException;
  
  public abstract double walkInColumnOrder(RealMatrixPreservingVisitor paramRealMatrixPreservingVisitor)
    throws MatrixVisitorException;
  
  public abstract double walkInColumnOrder(RealMatrixChangingVisitor paramRealMatrixChangingVisitor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException, MatrixVisitorException;
  
  public abstract double walkInColumnOrder(RealMatrixPreservingVisitor paramRealMatrixPreservingVisitor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException, MatrixVisitorException;
  
  public abstract double walkInOptimizedOrder(RealMatrixChangingVisitor paramRealMatrixChangingVisitor)
    throws MatrixVisitorException;
  
  public abstract double walkInOptimizedOrder(RealMatrixPreservingVisitor paramRealMatrixPreservingVisitor)
    throws MatrixVisitorException;
  
  public abstract double walkInOptimizedOrder(RealMatrixChangingVisitor paramRealMatrixChangingVisitor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException, MatrixVisitorException;
  
  public abstract double walkInOptimizedOrder(RealMatrixPreservingVisitor paramRealMatrixPreservingVisitor, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws MatrixIndexException, MatrixVisitorException;
  
  @Deprecated
  public abstract double[] solve(double[] paramArrayOfDouble)
    throws IllegalArgumentException, InvalidMatrixException;
  
  @Deprecated
  public abstract RealMatrix solve(RealMatrix paramRealMatrix)
    throws IllegalArgumentException, InvalidMatrixException;
}
