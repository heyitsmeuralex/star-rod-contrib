package org.apache.commons.math.util;

import java.util.Iterator;
import org.apache.commons.math.exception.DimensionMismatchException;
import org.apache.commons.math.exception.NotStrictlyPositiveException;
import org.apache.commons.math.exception.OutOfRangeException;























































public class MultidimensionalCounter
  implements Iterable<Integer>
{
  private final int dimension;
  private final int[] uniCounterOffset;
  private final int[] size;
  private final int totalSize;
  private final int last;
  
  public class Iterator
    implements Iterator<Integer>
  {
    private final int[] counter = new int[dimension];
    


    private int count = -1;
    



    Iterator()
    {
      counter[last] = -1;
    }
    


    public boolean hasNext()
    {
      for (int i = 0; i < dimension; i++) {
        if (counter[i] != size[i] - 1) {
          return true;
        }
      }
      return false;
    }
    



    public Integer next()
    {
      for (int i = last; i >= 0; i--) {
        if (counter[i] == size[i] - 1) {
          counter[i] = 0;
        } else {
          counter[i] += 1;
          break;
        }
      }
      
      return Integer.valueOf(++count);
    }
    




    public int getCount()
    {
      return count;
    }
    



    public int[] getCounts()
    {
      return MultidimensionalCounter.this.copyOf(counter, dimension);
    }
    










    public int getCount(int dim)
    {
      return counter[dim];
    }
    


    public void remove()
    {
      throw new UnsupportedOperationException();
    }
  }
  






  public MultidimensionalCounter(int... size)
  {
    dimension = size.length;
    this.size = copyOf(size, dimension);
    
    uniCounterOffset = new int[dimension];
    
    last = (dimension - 1);
    int tS = size[last];
    for (int i = 0; i < last; i++) {
      int count = 1;
      for (int j = i + 1; j < dimension; j++) {
        count *= size[j];
      }
      uniCounterOffset[i] = count;
      tS *= size[i];
    }
    uniCounterOffset[last] = 0;
    
    if (tS <= 0) {
      throw new NotStrictlyPositiveException(Integer.valueOf(tS));
    }
    
    totalSize = tS;
  }
  




  public Iterator iterator()
  {
    return new Iterator();
  }
  




  public int getDimension()
  {
    return dimension;
  }
  







  public int[] getCounts(int index)
  {
    if ((index < 0) || (index >= totalSize))
    {
      throw new OutOfRangeException(Integer.valueOf(index), Integer.valueOf(0), Integer.valueOf(totalSize));
    }
    
    int[] indices = new int[dimension];
    
    int count = 0;
    for (int i = 0; i < last; i++) {
      int idx = 0;
      int offset = uniCounterOffset[i];
      while (count <= index) {
        count += offset;
        idx++;
      }
      idx--;
      count -= offset;
      indices[i] = idx;
    }
    
    int idx = 1;
    while (count < index) {
      count += idx;
      idx++;
    }
    idx--;
    indices[last] = idx;
    
    return indices;
  }
  









  public int getCount(int... c)
    throws OutOfRangeException
  {
    if (c.length != dimension) {
      throw new DimensionMismatchException(c.length, dimension);
    }
    int count = 0;
    for (int i = 0; i < dimension; i++) {
      int index = c[i];
      if ((index < 0) || (index >= size[i]))
      {
        throw new OutOfRangeException(Integer.valueOf(index), Integer.valueOf(0), Integer.valueOf(size[i] - 1));
      }
      count += uniCounterOffset[i] * c[i];
    }
    return count + c[last];
  }
  




  public int getSize()
  {
    return totalSize;
  }
  



  public int[] getSizes()
  {
    return copyOf(size, dimension);
  }
  



  public String toString()
  {
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < dimension; i++) {
      sb.append("[").append(getCount(new int[] { i })).append("]");
    }
    return sb.toString();
  }
  






  private int[] copyOf(int[] source, int newLen)
  {
    int[] output = new int[newLen];
    System.arraycopy(source, 0, output, 0, Math.min(source.length, newLen));
    return output;
  }
}
