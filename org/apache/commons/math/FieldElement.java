package org.apache.commons.math;

public abstract interface FieldElement<T>
{
  public abstract T add(T paramT);
  
  public abstract T subtract(T paramT);
  
  public abstract T multiply(T paramT);
  
  public abstract T divide(T paramT)
    throws ArithmeticException;
  
  public abstract Field<T> getField();
}
