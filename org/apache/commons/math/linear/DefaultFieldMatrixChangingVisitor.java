package org.apache.commons.math.linear;

import org.apache.commons.math.FieldElement;

































public class DefaultFieldMatrixChangingVisitor<T extends FieldElement<T>>
  implements FieldMatrixChangingVisitor<T>
{
  private final T zero;
  
  public DefaultFieldMatrixChangingVisitor(T zero)
  {
    this.zero = zero;
  }
  

  public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {}
  

  public T visit(int row, int column, T value)
    throws MatrixVisitorException
  {
    return value;
  }
  
  public T end()
  {
    return zero;
  }
}
