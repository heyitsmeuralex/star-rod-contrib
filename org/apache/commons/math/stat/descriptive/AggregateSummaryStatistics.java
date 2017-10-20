package org.apache.commons.math.stat.descriptive;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;





























































public class AggregateSummaryStatistics
  implements StatisticalSummary, Serializable
{
  private static final long serialVersionUID = -8207112444016386906L;
  private final SummaryStatistics statisticsPrototype;
  private final SummaryStatistics statistics;
  
  public AggregateSummaryStatistics()
  {
    this(new SummaryStatistics());
  }
  















  public AggregateSummaryStatistics(SummaryStatistics prototypeStatistics)
  {
    this(prototypeStatistics, prototypeStatistics == null ? null : new SummaryStatistics(prototypeStatistics));
  }
  





















  public AggregateSummaryStatistics(SummaryStatistics prototypeStatistics, SummaryStatistics initialStatistics)
  {
    statisticsPrototype = (prototypeStatistics == null ? new SummaryStatistics() : prototypeStatistics);
    
    statistics = (initialStatistics == null ? new SummaryStatistics() : initialStatistics);
  }
  






  public double getMax()
  {
    synchronized (statistics) {
      return statistics.getMax();
    }
  }
  




  public double getMean()
  {
    synchronized (statistics) {
      return statistics.getMean();
    }
  }
  





  public double getMin()
  {
    synchronized (statistics) {
      return statistics.getMin();
    }
  }
  




  public long getN()
  {
    synchronized (statistics) {
      return statistics.getN();
    }
  }
  





  public double getStandardDeviation()
  {
    synchronized (statistics) {
      return statistics.getStandardDeviation();
    }
  }
  




  public double getSum()
  {
    synchronized (statistics) {
      return statistics.getSum();
    }
  }
  





  public double getVariance()
  {
    synchronized (statistics) {
      return statistics.getVariance();
    }
  }
  





  public double getSumOfLogs()
  {
    synchronized (statistics) {
      return statistics.getSumOfLogs();
    }
  }
  





  public double getGeometricMean()
  {
    synchronized (statistics) {
      return statistics.getGeometricMean();
    }
  }
  





  public double getSumsq()
  {
    synchronized (statistics) {
      return statistics.getSumsq();
    }
  }
  







  public double getSecondMoment()
  {
    synchronized (statistics) {
      return statistics.getSecondMoment();
    }
  }
  





  public StatisticalSummary getSummary()
  {
    synchronized (statistics) {
      return new StatisticalSummaryValues(getMean(), getVariance(), getN(), getMax(), getMin(), getSum());
    }
  }
  








  public SummaryStatistics createContributingStatistics()
  {
    SummaryStatistics contributingStatistics = new AggregatingSummaryStatistics(statistics);
    

    SummaryStatistics.copy(statisticsPrototype, contributingStatistics);
    
    return contributingStatistics;
  }
  











  public static StatisticalSummaryValues aggregate(Collection<SummaryStatistics> statistics)
  {
    if (statistics == null) {
      return null;
    }
    Iterator<SummaryStatistics> iterator = statistics.iterator();
    if (!iterator.hasNext()) {
      return null;
    }
    SummaryStatistics current = (SummaryStatistics)iterator.next();
    long n = current.getN();
    double min = current.getMin();
    double sum = current.getSum();
    double max = current.getMax();
    double m2 = current.getSecondMoment();
    double mean = current.getMean();
    while (iterator.hasNext()) {
      current = (SummaryStatistics)iterator.next();
      if ((current.getMin() < min) || (Double.isNaN(min))) {
        min = current.getMin();
      }
      if ((current.getMax() > max) || (Double.isNaN(max))) {
        max = current.getMax();
      }
      sum += current.getSum();
      double oldN = n;
      double curN = current.getN();
      n = (n + curN);
      double meanDiff = current.getMean() - mean;
      mean = sum / n;
      m2 = m2 + current.getSecondMoment() + meanDiff * meanDiff * oldN * curN / n; }
    double variance;
    double variance;
    if (n == 0L) {
      variance = NaN.0D; } else { double variance;
      if (n == 1L) {
        variance = 0.0D;
      } else
        variance = m2 / (n - 1L);
    }
    return new StatisticalSummaryValues(mean, variance, n, max, min, sum);
  }
  







  private static class AggregatingSummaryStatistics
    extends SummaryStatistics
  {
    private static final long serialVersionUID = 1L;
    





    private final SummaryStatistics aggregateStatistics;
    






    public AggregatingSummaryStatistics(SummaryStatistics aggregateStatistics)
    {
      this.aggregateStatistics = aggregateStatistics;
    }
    






    public void addValue(double value)
    {
      super.addValue(value);
      synchronized (aggregateStatistics) {
        aggregateStatistics.addValue(value);
      }
    }
    







    public boolean equals(Object object)
    {
      if (object == this) {
        return true;
      }
      if (!(object instanceof AggregatingSummaryStatistics)) {
        return false;
      }
      AggregatingSummaryStatistics stat = (AggregatingSummaryStatistics)object;
      return (super.equals(stat)) && (aggregateStatistics.equals(aggregateStatistics));
    }
    





    public int hashCode()
    {
      return 123 + super.hashCode() + aggregateStatistics.hashCode();
    }
  }
}
