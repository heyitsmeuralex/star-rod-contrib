package util;

import java.util.Stack;

public class EvictingStack<T>
  extends Stack<T>
{
  private static final long serialVersionUID = 1L;
  private final int capacity;
  
  public EvictingStack(int capacity)
  {
    this.capacity = capacity;
  }
  

  public T push(T item)
  {
    if (size() + 1 > capacity)
    {
      remove(0);
    }
    return super.push(item);
  }
  

  public boolean add(T item)
  {
    return push(item) != null;
  }
}
