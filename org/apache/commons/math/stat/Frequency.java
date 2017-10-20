package org.apache.commons.math.stat;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.math.MathRuntimeException;
import org.apache.commons.math.exception.util.LocalizedFormats;









































public class Frequency
  implements Serializable
{
  private static final long serialVersionUID = -3845586908418844111L;
  private final TreeMap<Comparable<?>, Long> freqTable;
  
  public Frequency()
  {
    freqTable = new TreeMap();
  }
  





  public Frequency(Comparator<?> comparator)
  {
    freqTable = new TreeMap(comparator);
  }
  






  public String toString()
  {
    NumberFormat nf = NumberFormat.getPercentInstance();
    StringBuilder outBuffer = new StringBuilder();
    outBuffer.append("Value \t Freq. \t Pct. \t Cum Pct. \n");
    Iterator<Comparable<?>> iter = freqTable.keySet().iterator();
    while (iter.hasNext()) {
      Comparable<?> value = (Comparable)iter.next();
      outBuffer.append(value);
      outBuffer.append('\t');
      outBuffer.append(getCount(value));
      outBuffer.append('\t');
      outBuffer.append(nf.format(getPct(value)));
      outBuffer.append('\t');
      outBuffer.append(nf.format(getCumPct(value)));
      outBuffer.append('\n');
    }
    return outBuffer.toString();
  }
  











  @Deprecated
  public void addValue(Object v)
  {
    if ((v instanceof Comparable)) {
      addValue((Comparable)v);
    } else {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.CLASS_DOESNT_IMPLEMENT_COMPARABLE, new Object[] { v.getClass().getName() });
    }
  }
  











  public void addValue(Comparable<?> v)
  {
    Comparable<?> obj = v;
    if ((v instanceof Integer)) {
      obj = Long.valueOf(((Integer)v).longValue());
    }
    try {
      Long count = (Long)freqTable.get(obj);
      if (count == null) {
        freqTable.put(obj, Long.valueOf(1L));
      } else {
        freqTable.put(obj, Long.valueOf(count.longValue() + 1L));
      }
    }
    catch (ClassCastException ex) {
      throw MathRuntimeException.createIllegalArgumentException(LocalizedFormats.INSTANCES_NOT_COMPARABLE_TO_EXISTING_VALUES, new Object[] { v.getClass().getName() });
    }
  }
  






  public void addValue(int v)
  {
    addValue(Long.valueOf(v));
  }
  





  @Deprecated
  public void addValue(Integer v)
  {
    addValue(Long.valueOf(v.longValue()));
  }
  




  public void addValue(long v)
  {
    addValue(Long.valueOf(v));
  }
  




  public void addValue(char v)
  {
    addValue(Character.valueOf(v));
  }
  
  public void clear()
  {
    freqTable.clear();
  }
  








  public Iterator<Comparable<?>> valuesIterator()
  {
    return freqTable.keySet().iterator();
  }
  






  public long getSumFreq()
  {
    long result = 0L;
    Iterator<Long> iterator = freqTable.values().iterator();
    while (iterator.hasNext()) {
      result += ((Long)iterator.next()).longValue();
    }
    return result;
  }
  







  @Deprecated
  public long getCount(Object v)
  {
    return getCount((Comparable)v);
  }
  






  public long getCount(Comparable<?> v)
  {
    if ((v instanceof Integer)) {
      return getCount(((Integer)v).longValue());
    }
    long result = 0L;
    try {
      Long count = (Long)freqTable.get(v);
      if (count != null) {
        result = count.longValue();
      }
    }
    catch (ClassCastException ex) {}
    
    return result;
  }
  





  public long getCount(int v)
  {
    return getCount(Long.valueOf(v));
  }
  





  public long getCount(long v)
  {
    return getCount(Long.valueOf(v));
  }
  





  public long getCount(char v)
  {
    return getCount(Character.valueOf(v));
  }
  





  public int getUniqueCount()
  {
    return freqTable.keySet().size();
  }
  











  @Deprecated
  public double getPct(Object v)
  {
    return getPct((Comparable)v);
  }
  








  public double getPct(Comparable<?> v)
  {
    long sumFreq = getSumFreq();
    if (sumFreq == 0L) {
      return NaN.0D;
    }
    return getCount(v) / sumFreq;
  }
  






  public double getPct(int v)
  {
    return getPct(Long.valueOf(v));
  }
  






  public double getPct(long v)
  {
    return getPct(Long.valueOf(v));
  }
  






  public double getPct(char v)
  {
    return getPct(Character.valueOf(v));
  }
  










  @Deprecated
  public long getCumFreq(Object v)
  {
    return getCumFreq((Comparable)v);
  }
  







  public long getCumFreq(Comparable<?> v)
  {
    if (getSumFreq() == 0L) {
      return 0L;
    }
    if ((v instanceof Integer)) {
      return getCumFreq(((Integer)v).longValue());
    }
    
    Comparator<Comparable<?>> c = freqTable.comparator();
    if (c == null) {
      c = new NaturalComparator(null);
    }
    long result = 0L;
    try
    {
      Long value = (Long)freqTable.get(v);
      if (value != null) {
        result = value.longValue();
      }
    } catch (ClassCastException ex) {
      return result;
    }
    
    if (c.compare(v, freqTable.firstKey()) < 0) {
      return 0L;
    }
    
    if (c.compare(v, freqTable.lastKey()) >= 0) {
      return getSumFreq();
    }
    
    Iterator<Comparable<?>> values = valuesIterator();
    while (values.hasNext()) {
      Comparable<?> nextValue = (Comparable)values.next();
      if (c.compare(v, nextValue) > 0) {
        result += getCount(nextValue);
      } else {
        return result;
      }
    }
    return result;
  }
  







  public long getCumFreq(int v)
  {
    return getCumFreq(Long.valueOf(v));
  }
  







  public long getCumFreq(long v)
  {
    return getCumFreq(Long.valueOf(v));
  }
  







  public long getCumFreq(char v)
  {
    return getCumFreq(Character.valueOf(v));
  }
  













  @Deprecated
  public double getCumPct(Object v)
  {
    return getCumPct((Comparable)v);
  }
  











  public double getCumPct(Comparable<?> v)
  {
    long sumFreq = getSumFreq();
    if (sumFreq == 0L) {
      return NaN.0D;
    }
    return getCumFreq(v) / sumFreq;
  }
  








  public double getCumPct(int v)
  {
    return getCumPct(Long.valueOf(v));
  }
  








  public double getCumPct(long v)
  {
    return getCumPct(Long.valueOf(v));
  }
  








  public double getCumPct(char v)
  {
    return getCumPct(Character.valueOf(v));
  }
  





  private static class NaturalComparator<T extends Comparable<T>>
    implements Comparator<Comparable<T>>, Serializable
  {
    private static final long serialVersionUID = -3852193713161395148L;
    





    private NaturalComparator() {}
    





    public int compare(Comparable<T> o1, Comparable<T> o2)
    {
      return o1.compareTo(o2);
    }
  }
  

  public int hashCode()
  {
    int prime = 31;
    int result = 1;
    result = 31 * result + (freqTable == null ? 0 : freqTable.hashCode());
    
    return result;
  }
  

  public boolean equals(Object obj)
  {
    if (this == obj)
      return true;
    if (!(obj instanceof Frequency))
      return false;
    Frequency other = (Frequency)obj;
    if (freqTable == null) {
      if (freqTable != null)
        return false;
    } else if (!freqTable.equals(freqTable))
      return false;
    return true;
  }
}
