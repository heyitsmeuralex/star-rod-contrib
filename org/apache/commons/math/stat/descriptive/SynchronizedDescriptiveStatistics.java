package org.apache.commons.math.stat.descriptive;

















public class SynchronizedDescriptiveStatistics
  extends DescriptiveStatistics
{
  private static final long serialVersionUID = 1L;
  















  public SynchronizedDescriptiveStatistics()
  {
    this(-1);
  }
  



  public SynchronizedDescriptiveStatistics(int window)
  {
    super(window);
  }
  




  public SynchronizedDescriptiveStatistics(SynchronizedDescriptiveStatistics original)
  {
    copy(original, this);
  }
  



  public synchronized void addValue(double v)
  {
    super.addValue(v);
  }
  



  public synchronized double apply(UnivariateStatistic stat)
  {
    return super.apply(stat);
  }
  



  public synchronized void clear()
  {
    super.clear();
  }
  



  public synchronized double getElement(int index)
  {
    return super.getElement(index);
  }
  



  public synchronized long getN()
  {
    return super.getN();
  }
  



  public synchronized double getStandardDeviation()
  {
    return super.getStandardDeviation();
  }
  



  public synchronized double[] getValues()
  {
    return super.getValues();
  }
  



  public synchronized int getWindowSize()
  {
    return super.getWindowSize();
  }
  



  public synchronized void setWindowSize(int windowSize)
  {
    super.setWindowSize(windowSize);
  }
  



  public synchronized String toString()
  {
    return super.toString();
  }
  






  public synchronized SynchronizedDescriptiveStatistics copy()
  {
    SynchronizedDescriptiveStatistics result = new SynchronizedDescriptiveStatistics();
    
    copy(this, result);
    return result;
  }
  









  public static void copy(SynchronizedDescriptiveStatistics source, SynchronizedDescriptiveStatistics dest)
  {
    synchronized (source) {
      synchronized (dest) {
        DescriptiveStatistics.copy(source, dest);
      }
    }
  }
}
