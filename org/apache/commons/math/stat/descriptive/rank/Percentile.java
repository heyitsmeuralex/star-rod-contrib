package org.apache.commons.math.stat.descriptive.rank;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;
import org.apache.commons.math.stat.descriptive.AbstractUnivariateStatistic;
import org.apache.commons.math.util.FastMath;















































































public class Percentile
  extends AbstractUnivariateStatistic
  implements Serializable
{
  private static final long serialVersionUID = -8091216485095130416L;
  private static final int MIN_SELECT_SIZE = 15;
  private static final int MAX_CACHED_LEVELS = 10;
  private double quantile = 0.0D;
  


  private int[] cachedPivots;
  


  public Percentile()
  {
    this(50.0D);
  }
  





  public Percentile(double p)
  {
    setQuantile(p);
    cachedPivots = null;
  }
  





  public Percentile(Percentile original)
  {
    copy(original, this);
  }
  

  public void setData(double[] values)
  {
    if (values == null) {
      cachedPivots = null;
    } else {
      cachedPivots = new int['Ͽ'];
      Arrays.fill(cachedPivots, -1);
    }
    super.setData(values);
  }
  

  public void setData(double[] values, int begin, int length)
  {
    if (values == null) {
      cachedPivots = null;
    } else {
      cachedPivots = new int['Ͽ'];
      Arrays.fill(cachedPivots, -1);
    }
    super.setData(values, begin, length);
  }
  







  public double evaluate(double p)
  {
    return evaluate(getDataRef(), p);
  }
  

























  public double evaluate(double[] values, double p)
  {
    test(values, 0, 0);
    return evaluate(values, 0, values.length, p);
  }
  
























  public double evaluate(double[] values, int start, int length)
  {
    return evaluate(values, start, length, quantile);
  }
  































  public double evaluate(double[] values, int begin, int length, double p)
  {
    test(values, begin, length);
    
    if ((p > 100.0D) || (p <= 0.0D)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE, new Object[] { Double.valueOf(p) });
    }
    
    if (length == 0) {
      return NaN.0D;
    }
    if (length == 1) {
      return values[begin];
    }
    double n = length;
    double pos = p * (n + 1.0D) / 100.0D;
    double fpos = FastMath.floor(pos);
    int intPos = (int)fpos;
    double dif = pos - fpos;
    int[] pivotsHeap;
    double[] work;
    int[] pivotsHeap; if (values == getDataRef()) {
      double[] work = getDataRef();
      pivotsHeap = cachedPivots;
    } else {
      work = new double[length];
      System.arraycopy(values, begin, work, 0, length);
      pivotsHeap = new int['Ͽ'];
      Arrays.fill(pivotsHeap, -1);
    }
    
    if (pos < 1.0D) {
      return select(work, pivotsHeap, 0);
    }
    if (pos >= n) {
      return select(work, pivotsHeap, length - 1);
    }
    double lower = select(work, pivotsHeap, intPos - 1);
    double upper = select(work, pivotsHeap, intPos);
    return lower + dif * (upper - lower);
  }
  











  private double select(double[] work, int[] pivotsHeap, int k)
  {
    int begin = 0;
    int end = work.length;
    int node = 0;
    
    while (end - begin > 15) {
      int pivot;
      int pivot;
      if ((node < pivotsHeap.length) && (pivotsHeap[node] >= 0))
      {

        pivot = pivotsHeap[node];
      }
      else {
        pivot = partition(work, begin, end, medianOf3(work, begin, end));
        if (node < pivotsHeap.length) {
          pivotsHeap[node] = pivot;
        }
      }
      
      if (k == pivot)
      {
        return work[k]; }
      if (k < pivot)
      {
        end = pivot;
        node = Math.min(2 * node + 1, pivotsHeap.length);
      }
      else {
        begin = pivot + 1;
        node = Math.min(2 * node + 2, pivotsHeap.length);
      }
    }
    



    insertionSort(work, begin, end);
    return work[k];
  }
  








  int medianOf3(double[] work, int begin, int end)
  {
    int inclusiveEnd = end - 1;
    int middle = begin + (inclusiveEnd - begin) / 2;
    double wBegin = work[begin];
    double wMiddle = work[middle];
    double wEnd = work[inclusiveEnd];
    
    if (wBegin < wMiddle) {
      if (wMiddle < wEnd) {
        return middle;
      }
      return wBegin < wEnd ? inclusiveEnd : begin;
    }
    
    if (wBegin < wEnd) {
      return begin;
    }
    return wMiddle < wEnd ? inclusiveEnd : middle;
  }
  
















  private int partition(double[] work, int begin, int end, int pivot)
  {
    double value = work[pivot];
    work[pivot] = work[begin];
    
    int i = begin + 1;
    int j = end - 1;
    while (i < j) {
      while ((i < j) && (work[j] >= value)) {
        j--;
      }
      while ((i < j) && (work[i] <= value)) {
        i++;
      }
      
      if (i < j) {
        double tmp = work[i];
        work[(i++)] = work[j];
        work[(j--)] = tmp;
      }
    }
    
    if ((i >= end) || (work[i] > value)) {
      i--;
    }
    work[begin] = work[i];
    work[i] = value;
    return i;
  }
  






  private void insertionSort(double[] work, int begin, int end)
  {
    for (int j = begin + 1; j < end; j++) {
      double saved = work[j];
      int i = j - 1;
      while ((i >= begin) && (saved < work[i])) {
        work[(i + 1)] = work[i];
        i--;
      }
      work[(i + 1)] = saved;
    }
  }
  





  public double getQuantile()
  {
    return quantile;
  }
  







  public void setQuantile(double p)
  {
    if ((p <= 0.0D) || (p > 100.0D)) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.OUT_OF_BOUNDS_QUANTILE_VALUE, new Object[] { Double.valueOf(p) });
    }
    
    quantile = p;
  }
  



  public Percentile copy()
  {
    Percentile result = new Percentile();
    copy(this, result);
    return result;
  }
  







  public static void copy(Percentile source, Percentile dest)
  {
    dest.setData(source.getDataRef());
    if (cachedPivots != null) {
      System.arraycopy(cachedPivots, 0, cachedPivots, 0, cachedPivots.length);
    }
    quantile = quantile;
  }
}
