package org.apache.commons.math.linear;

public abstract interface AnyMatrix
{
  public abstract boolean isSquare();
  
  public abstract int getRowDimension();
  
  public abstract int getColumnDimension();
}
