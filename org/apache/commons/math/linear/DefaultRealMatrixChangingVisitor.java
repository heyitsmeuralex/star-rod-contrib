package org.apache.commons.math.linear;











public class DefaultRealMatrixChangingVisitor
  implements RealMatrixChangingVisitor
{
  public DefaultRealMatrixChangingVisitor() {}
  









  public void start(int rows, int columns, int startRow, int endRow, int startColumn, int endColumn) {}
  









  public double visit(int row, int column, double value)
    throws MatrixVisitorException
  {
    return value;
  }
  
  public double end()
  {
    return 0.0D;
  }
}
