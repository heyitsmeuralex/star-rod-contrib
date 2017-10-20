package org.apache.commons.math.linear;

import org.apache.commons.math.FieldElement;

































public class DefaultFieldMatrixPreservingVisitor<T extends FieldElement<T>>
  implements FieldMatrixPreservingVisitor<T>
{
  private final T zero;
  
  public DefaultFieldMatrixPreservingVisitor(T zero)
  {
    this.zero = zero;
  }
  


  public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {}
  

  public void visit(int row, int column, T value)
    throws MatrixVisitorException
  {}
  

  public T end()
  {
    return zero;
  }
}
