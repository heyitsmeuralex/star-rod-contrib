package org.apache.commons.math.linear;

import org.apache.commons.math.FieldElement;

public abstract interface FieldMatrixPreservingVisitor<T extends FieldElement<?>>
{
  public abstract void start(int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, int paramInt6);
  
  public abstract void visit(int paramInt1, int paramInt2, T paramT)
    throws MatrixVisitorException;
  
  public abstract T end();
}
