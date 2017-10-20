package org.apache.commons.math.linear;

public abstract interface RealMatrixPreservingVisitor
{
  public abstract void start(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void visit(int paramInt1, int paramInt2, double paramDouble)
    throws MatrixVisitorException;
  
  public abstract double end();
}
