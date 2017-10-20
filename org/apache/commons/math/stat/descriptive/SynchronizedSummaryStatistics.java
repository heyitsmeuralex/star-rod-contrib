package org.apache.commons.math.stat.descriptive;














public class SynchronizedSummaryStatistics
  extends SummaryStatistics
{
  private static final long serialVersionUID = 1909861009042253704L;
  













  public SynchronizedSummaryStatistics() {}
  












  public SynchronizedSummaryStatistics(SynchronizedSummaryStatistics original)
  {
    copy(original, this);
  }
  



  public synchronized StatisticalSummary getSummary()
  {
    return super.getSummary();
  }
  



  public synchronized void addValue(double value)
  {
    super.addValue(value);
  }
  



  public synchronized long getN()
  {
    return super.getN();
  }
  



  public synchronized double getSum()
  {
    return super.getSum();
  }
  



  public synchronized double getSumsq()
  {
    return super.getSumsq();
  }
  



  public synchronized double getMean()
  {
    return super.getMean();
  }
  



  public synchronized double getStandardDeviation()
  {
    return super.getStandardDeviation();
  }
  



  public synchronized double getVariance()
  {
    return super.getVariance();
  }
  



  public synchronized double getMax()
  {
    return super.getMax();
  }
  



  public synchronized double getMin()
  {
    return super.getMin();
  }
  



  public synchronized double getGeometricMean()
  {
    return super.getGeometricMean();
  }
  



  public synchronized String toString()
  {
    return super.toString();
  }
  



  public synchronized void clear()
  {
    super.clear();
  }
  



  public synchronized boolean equals(Object object)
  {
    return super.equals(object);
  }
  



  public synchronized int hashCode()
  {
    return super.hashCode();
  }
  



  public synchronized StorelessUnivariateStatistic getSumImpl()
  {
    return super.getSumImpl();
  }
  



  public synchronized void setSumImpl(StorelessUnivariateStatistic sumImpl)
  {
    super.setSumImpl(sumImpl);
  }
  



  public synchronized StorelessUnivariateStatistic getSumsqImpl()
  {
    return super.getSumsqImpl();
  }
  



  public synchronized void setSumsqImpl(StorelessUnivariateStatistic sumsqImpl)
  {
    super.setSumsqImpl(sumsqImpl);
  }
  



  public synchronized StorelessUnivariateStatistic getMinImpl()
  {
    return super.getMinImpl();
  }
  



  public synchronized void setMinImpl(StorelessUnivariateStatistic minImpl)
  {
    super.setMinImpl(minImpl);
  }
  



  public synchronized StorelessUnivariateStatistic getMaxImpl()
  {
    return super.getMaxImpl();
  }
  



  public synchronized void setMaxImpl(StorelessUnivariateStatistic maxImpl)
  {
    super.setMaxImpl(maxImpl);
  }
  



  public synchronized StorelessUnivariateStatistic getSumLogImpl()
  {
    return super.getSumLogImpl();
  }
  



  public synchronized void setSumLogImpl(StorelessUnivariateStatistic sumLogImpl)
  {
    super.setSumLogImpl(sumLogImpl);
  }
  



  public synchronized StorelessUnivariateStatistic getGeoMeanImpl()
  {
    return super.getGeoMeanImpl();
  }
  



  public synchronized void setGeoMeanImpl(StorelessUnivariateStatistic geoMeanImpl)
  {
    super.setGeoMeanImpl(geoMeanImpl);
  }
  



  public synchronized StorelessUnivariateStatistic getMeanImpl()
  {
    return super.getMeanImpl();
  }
  



  public synchronized void setMeanImpl(StorelessUnivariateStatistic meanImpl)
  {
    super.setMeanImpl(meanImpl);
  }
  



  public synchronized StorelessUnivariateStatistic getVarianceImpl()
  {
    return super.getVarianceImpl();
  }
  



  public synchronized void setVarianceImpl(StorelessUnivariateStatistic varianceImpl)
  {
    super.setVarianceImpl(varianceImpl);
  }
  






  public synchronized SynchronizedSummaryStatistics copy()
  {
    SynchronizedSummaryStatistics result = new SynchronizedSummaryStatistics();
    
    copy(this, result);
    return result;
  }
  









  public static void copy(SynchronizedSummaryStatistics source, SynchronizedSummaryStatistics dest)
  {
    synchronized (source) {
      synchronized (dest) {
        SummaryStatistics.copy(source, dest);
      }
    }
  }
}
